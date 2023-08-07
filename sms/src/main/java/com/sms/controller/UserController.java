package com.sms.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.common.Token;
import com.sms.config.CodeConfig;
import com.sms.config.JwtConfig;
import com.sms.dto.*;
import com.sms.entity.User;
import com.sms.service.UserService;
import com.sms.utils.JwtUtils;
import com.sms.utils.SendPhoneCodeUtils;
import com.sms.utils.ValidateCodeUtils;
import com.sms.vo.UseVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    UserService userService;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    JwtConfig jwtConfig;

    @Resource
    CodeConfig codeConfig;

    /**
     * 用户登录接口
     *
     * @param request
     * @param userLoginDto
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UseVo> login(HttpServletRequest request, HttpServletResponse response, @RequestBody UserLoginDto userLoginDto) {
        log.info("登录学生信息:{}", userLoginDto);
        String vcode = (String) request.getSession().getAttribute("vcode");
        if (codeConfig.isVerificationCode()) {
            if (userLoginDto.getVerificationCode() == null || !userLoginDto.getVerificationCode().equals(vcode)) {
                return Result.error("验证码错误，请重试");
            }
        }
        //从数据库查询对象
        return userService.login(response, userLoginDto);
    }

    /**
     * 用户登出接口
     *
     * @return
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        log.info("退出登录");
        return Result.success("退出成功");
    }

    /**
     * remember me功能配套接口 直接通过令牌获取信息
     *
     * @param token
     * @return
     */
    @ApiOperation("通过令牌获取个人信息")
    @PostMapping("/self/info")
    public Result<User> selfInfo(@RequestBody Token token) {
        Claims claims = JwtUtils.getClaims(token.getToken(), jwtConfig.getMySecretKey());
        Long id = (Long) claims.get("id");
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id);
        User user = userService.getOne(wrapper);
        return Result.success(user);
    }

    /**
     * 忘记密码功能手机验证码获取接口
     *
     * @param username
     * @return
     */
    @ApiOperation("手机验证码")
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

    /**
     * 修改密码接口
     *
     * @param pwdDto
     * @return
     */
    @ApiOperation("修改密码")
    @PostMapping("/pwd")
    public Result<String> pwd(@RequestBody PwdDto pwdDto) {
        return userService.resetPassword(pwdDto);
    }

    /**
     * 忘记密码的修改密码接口
     *
     * @param pwdForgetDto
     * @return
     */
    @ApiOperation("忘记密码重设密码")
    @PostMapping("/pwd/forget")
    public Result<String> pwdForget(@RequestBody PwdForgetDto pwdForgetDto) {
        String username = pwdForgetDto.getUsername();
        // 从Redis里获取验证码
        String phoneCode = (String) redisTemplate.boundValueOps("phoneCode:" + username).get();
        if (!pwdForgetDto.getVerificationCode().equals(phoneCode)) {
            return Result.error("验证码错误或已失效，请重试");
        }
        //根据用户名获取用户信息
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userService.getOne(wrapper);

        String md5Password = DigestUtils.md5DigestAsHex(pwdForgetDto.getNewPwd().getBytes());
        user.setPassword(md5Password);

        //因为还没登录 Mybatis自动填充功能需要id 需要手动设置
        Claims claims = new DefaultClaims();
        claims.put("id", user.getId());
        claims.put("userGrant", user.getUserGrant());
        BaseContext.setCurrentClaims(claims);

        userService.updateById(user);
        return Result.success("重置密码成功");
    }

    /**
     * 用户添加接口
     *
     * @param userAddDto
     * @return
     */
    @ApiOperation("用户添加")
    @PostMapping("/add")
    @CacheEvict(cacheNames = "UserVisualization", allEntries = true)
    public Result<String> add(@RequestBody UserAddDto userAddDto) {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddDto, user);
        List<User> users = List.of(user);
        return userService.addUser(users);
    }

    /**
     * 从csv批量导入用户的接口
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("文件批量添加用户")
    @PostMapping("/csv/add")
    @CacheEvict(cacheNames = "UserVisualization", allEntries = true)
    public Result<String> csvUserAdd(MultipartFile file) throws IOException {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        if (file == null) {
            return Result.error("未选择文件");
        }
        // 获取文件后缀名
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        String[] split = filename.split("\\.");
        String fileExtension = split[split.length - 1];
        if (!fileExtension.equals("csv")) {
            return Result.error("请上传格式正确的文件");
        }
        //从流读取csv文件
        InputStream in = file.getInputStream();
        BufferedReader utf8Reader = IoUtil.getUtf8Reader(in);
        CsvReader reader = CsvUtil.getReader();
        List<User> list = reader.read(utf8Reader, User.class);
        return userService.addUser(list);
    }

    /**
     * 用户管理的信息修改接口
     *
     * @param user
     * @return
     */
    @ApiOperation("用户管理信息修改")
    @PostMapping("/edit")
    @CacheEvict(cacheNames = "UserVisualization", allEntries = true)
    public Result<UseVo> edit(@RequestBody User user) {
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

    /**
     * 个人修改自己信息的接口
     *
     * @param user
     * @return
     */
    @ApiOperation("个人修改信息")
    @PostMapping("/self/edit")
    @CacheEvict(cacheNames = "UserVisualization", allEntries = true)
    public Result<UseVo> editInfo(@RequestBody User user) {
        log.info("修改信息:{}", user);
        log.info("登录用户id:{}", BaseContext.getCurrentId());
        if (!Objects.equals(BaseContext.getCurrentId(), user.getId())) {
            return Result.error("只支持修改自己的信息");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        userService.update(user, wrapper);
        UseVo vo = new UseVo();
        BeanUtils.copyProperties(userService.getById(user.getId()), vo);
        return Result.success(vo);
    }

    /**
     * 禁用用户接口 ban=0时为禁用 ban=1时为启用
     *
     * @param id
     * @param ban
     * @return
     */
    @ApiOperation("禁用用户")
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

    /**
     * 删除用户接口
     *
     * @param id
     * @return
     */
    @ApiOperation("删除用户")
    @PostMapping("/del/{id}")
    @CacheEvict(cacheNames = "UserVisualization", allEntries = true)
    public Result<String> delUser(@PathVariable(value = "id") Long id) {
        log.info("删除用户id:{}", id);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        userService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 用户信息分页查询接口
     *
     * @param page
     * @param pagesize
     * @param userSelectDto
     * @return
     */
    @ApiOperation("用户信息分页查询")
    @PostMapping("/{page}/{pagesize}")
    public Result<Page<UseVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody UserSelectDto userSelectDto) {
        if (!userService.isAdmin()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<UseVo> voPage = userService.getVoPage(page, pagesize, userSelectDto);
        return Result.success(voPage);
    }
}
