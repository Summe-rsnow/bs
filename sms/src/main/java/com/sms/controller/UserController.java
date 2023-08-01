package com.sms.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.config.CodeConfig;
import com.sms.dto.PwdDto;
import com.sms.dto.PwdForgetDto;
import com.sms.dto.UserDto;
import com.sms.dto.UserSelectDto;
import com.sms.entity.User;
import com.sms.service.UserService;
import com.sms.utils.SendPhoneCodeUtils;
import com.sms.utils.ValidateCodeUtils;
import com.sms.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    UserService userService;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    CodeConfig codeConfig;

    @PostMapping("/login")
    public Result<UserVo> login(HttpServletRequest request, @RequestBody UserDto userDto) {
        log.info("登录学生信息:{}", userDto);
        String vcode = (String) request.getSession().getAttribute("vcode");
        if (codeConfig.isVerificationCode()) {
            if (userDto.getVerificationCode() == null || !userDto.getVerificationCode().equals(vcode)) {
                return Result.error("验证码错误，请重试");
            }
        }
        //从数据库查询对象
        return userService.login(request, userDto);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        log.info("退出登录");
        return Result.success("退出成功");
    }

    //忘记密码返回手机验证码接口
    @PostMapping("/phone_code/{username}")
    public Result<String> phoneCode(@PathVariable String username) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("用户名不存在");
        }
        String phone = user.getPhone();
        if (phone == null) {
            return Result.error("当前用户名未设置手机号码，请联系管理员解决问题");
        }
        String code = ValidateCodeUtils.generateNumericCode();
        log.info("生成的手机验证码为:{}", code);
        if (codeConfig.isPhoneCode()) {
            SendPhoneCodeUtils.sendMessage(codeConfig.getSignName(), codeConfig.getTemplateCode(), phone, code);
        }
        // 向Redis存储验证码和过期时间
        redisTemplate.boundValueOps("phoneCode:" + username).set(code, 5, TimeUnit.MINUTES);
        return Result.success("获取验证码成功");
    }

    //修改密码接口
    @PostMapping("/pwd")
    public Result<String> pwd(@RequestBody PwdDto pwdDto) {
        return userService.resetPassword(pwdDto);
    }

    //忘记密码修改接口
    @PostMapping("/pwd/forget")
    public Result<String> pwdForget(@RequestBody PwdForgetDto pwdForgetDto) {
        String username = pwdForgetDto.getUsername();
        // 从Redis里获取验证码
        String phoneCode = (String) redisTemplate.boundValueOps("phoneCode:" + username).get();
        if (!phoneCode.equals(pwdForgetDto.getVerificationCode())) {
            return Result.error("验证码错误或已失效，请重试");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userService.getOne(wrapper);
        String md5Password = DigestUtils.md5DigestAsHex(pwdForgetDto.getNewPwd().getBytes());
        user.setPassword(md5Password);
        //因为还没登录 自动填充需要id 需要手动设置
        BaseContext.setCurrentId(user.getId());
        userService.updateById(user);
        return Result.success("重置密码成功");
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody User user) {
        return userService.addUser(user);
    }

    //管理员修改成员的信息
    @PostMapping("/edit")
    public Result<UserVo> edit(@RequestBody User user) {
        //不允许修改权限
        user.setUserGrant(null);
        log.info("修改信息:{}", user);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        userService.update(user, wrapper);
        return Result.success("修改成功");
    }

    //个人修改自己信息
    @PostMapping("/self/edit")
    public Result<UserVo> editInfo(@RequestBody User user) {
        log.info("修改信息:{}", user);
        log.info("登录用户id:{}", BaseContext.getCurrentId());
        if (!Objects.equals(BaseContext.getCurrentId(), user.getId())) {
            return Result.error("只支持修改自己的信息");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        userService.update(user, wrapper);
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(userService.getById(user.getId()), vo);
        return Result.success(vo);
    }

    //禁用用户接口 ban=0时为禁用 ban=1时为启用
    @PostMapping("/ban/{ban}/{id}")
    public Result<String> banUser(@PathVariable Long id, @PathVariable Integer ban) {
        log.info("禁用用户id:{}", id);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        User user = new User();
        user.setStatus(ban);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id);
        userService.update(user, wrapper);
        return Result.success("禁用成功");
    }

    @PostMapping("/del/{id}")
    public Result<String> delUser(@PathVariable(value = "id") Long id) {
        log.info("删除用户id:{}", id);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        userService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{page}/{pagesize}")
    public Result<Page<UserVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody UserSelectDto userSelectDto) {
        if (!userService.isAdmin()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<UserVo> voPage = userService.getVoPage(page, pagesize, userSelectDto);
        return Result.success(voPage);
    }
}
