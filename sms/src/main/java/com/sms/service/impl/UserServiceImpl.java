package com.sms.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.config.JwtConfig;
import com.sms.dto.PwdDto;
import com.sms.dto.UserLoginDto;
import com.sms.dto.UserSelectDto;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.service.UserService;
import com.sms.utils.JwtUtils;
import com.sms.vo.UseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Resource
    JwtConfig jwtConfig;

    @Override
    public Result<UseVo> login(HttpServletResponse response, UserLoginDto userLoginDto) {

        //从数据库查询对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userLoginDto.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        //将密码进行MD5加密
        String password = DigestUtils.md5DigestAsHex(userLoginDto.getPassword().getBytes());
        if (user == null || !user.getPassword().equals(password)) {//判断密码是否正确
            return Result.error("用户名或密码错误，请重试");
        }
        if (user.getStatus() == 0) {//判断账户是否可用  0为不可用 1为可用 2逻辑删除
            return Result.error("该用户名已禁用，请重试");
        }
        if (user.getStatus() == 2) {//判断账户是否可用  0为不可用 1为可用 2逻辑删除
            return Result.error("该用户名不存在，请重试");
        }
        UseVo vo = new UseVo();
        BeanUtils.copyProperties(user, vo);
        Long id = vo.getId();
        //如果勾选记住我 加入if逻辑
        String token;
        if (userLoginDto.isRememberMe()) {
            token = JwtUtils.generateToken(id, 604800000L, jwtConfig.getMySecretKey());//如果勾选记住我 设置1周时长的token
            Cookie rememberMe = new Cookie("rememberMe", token);
            rememberMe.setMaxAge(604800);
            rememberMe.setPath("/");
            response.addCookie(rememberMe);
        } else {
            token = JwtUtils.generateToken(id, jwtConfig.getTimeout(), jwtConfig.getMySecretKey());
        }
        vo.setToken(token);
        return Result.success(vo);//返回查到对象（以json格式）
    }

    @Override
    public Result<String> resetPassword(PwdDto pwdDto) {
        String oldPwd = pwdDto.getOldPwd();
        String newPwd = pwdDto.getNewPwd();
        oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
        Long currentId = BaseContext.getCurrentId();
        User user = getById(currentId);
        if (!user.getPassword().equals(oldPwd)) {
            return Result.error("原密码错误，请重试");
        }
        newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());
        user.setPassword(newPwd);
        updateById(user);
        return Result.success("修改密码成功！");
    }

    @Override
    public Result<String> addUser(List<User> users) {
        if (!isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        List<User> processedUsers = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        String md5Password = DigestUtils.md5DigestAsHex("123456".getBytes());
        for (User user : users) {
            String validationMsg = validateUser(user);
            if (validationMsg != null) {
                errorMsg.append(validationMsg);
            } else {
                user.setPassword(md5Password);
                user.setStatus(1);
                Snowflake snowflake = IdUtil.getSnowflake(1, 1);
                user.setAvatar(snowflake.nextIdStr() + ".png");
                log.info("新增学生信息: {}", user);
                processedUsers.add(user);
            }
        }

        if (errorMsg.length() > 0) {
            return Result.error(errorMsg.toString());
        }
        saveBatch(processedUsers);
        return Result.success("新增成功");
    }

    /**
     * 用户数据校验方法
     *
     * @param user
     * @return
     */
    private String validateUser(User user) {
        String idNumber = user.getIdNumber();
        String phone = user.getPhone();
        String email = user.getEmail();
        String username = user.getUsername();

        //身份证校验
        //if (idNumber != null && !"".equals(idNumber.trim()) && !IdcardUtil.isValidCard(idNumber))  这个太严格了
        if (idNumber != null && !"".equals(idNumber.trim()) && !ReUtil.isMatch("[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9X]", idNumber)) {
            return "用户：" + username + "身份证无效\n";
        }
        //手机验证
        if (phone != null && !"".equals(phone.trim()) && !ReUtil.isMatch("^\\d{11}$", phone)) {
            return "用户：" + username + "手机号码无效\n";
        }
        //邮箱验证
        if (email != null && !"".equals(email.trim()) && !ReUtil.isMatch("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", email)) {
            return "用户：" + username + "邮箱无效\n";
        }
        //用户名验证
        if (username != null && !"".equals(username.trim()) && !ReUtil.isMatch("^[a-zA-Z][a-zA-Z0-9]{4,15}$", username)) {
            return "用户：" + username + "请输入5-16位账号，且账号开头为字母，只能包含字母和数字\n";
        }
        return null;
    }

    @Override
    public boolean isAdmin() {
        return grantLevel() == 0;
    }

    @Override
    public boolean isTeacher() {
        return grantLevel() == 1;
    }

    @Override
    public boolean isStudent() {
        return grantLevel() == 2;
    }

    @Override
    public Integer grantLevel() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, BaseContext.getCurrentId());
        return userMapper.selectOne(queryWrapper).getUserGrant();
    }

    @Override
    public Page<UseVo> getVoPage(Integer page, Integer pagesize, UserSelectDto userSelectDto) {
        Page<UseVo> userVoPage = new Page<>(page, pagesize);
        return userMapper.selectUserVoPage(userVoPage, userSelectDto);
    }
}
