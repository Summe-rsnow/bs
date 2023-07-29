package com.sms.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.dto.PwdDto;
import com.sms.dto.UserDto;
import com.sms.entity.User;
import com.sms.service.UserService;
import com.sms.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    UserService userService;

    @Value("${sms.verificationCode}")
    boolean enableVerificationCode;

    @PostMapping("/login")
    public Result<UserVo> login(HttpServletRequest request, @RequestBody UserDto userDto) {
        log.info("登录学生信息:{}", userDto);
        String vcode = (String) request.getSession().getAttribute("vcode");
        if (enableVerificationCode) {
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

    @PostMapping("/pwd")
    public Result<String> pwd(@RequestBody PwdDto pwdDto) {
        return userService.resetPassword(pwdDto);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody User user) {
        return userService.addUser(user);
    }

    //管理员修改成员的信息
    @PostMapping("/edit")
    public Result<UserVo> edit(@RequestBody User user) {
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
    public Result<Page<UserVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize) {
        if (!userService.isAdmin()) {
            return Result.success("当前用户没有该操作权限");
        }
        return Result.success(userService.getVoPage(page, pagesize));
    }
}
