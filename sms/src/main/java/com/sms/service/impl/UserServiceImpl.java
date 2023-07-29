package com.sms.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.dto.PwdDto;
import com.sms.dto.UserDto;
import com.sms.dto.UserSelectDto;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.service.UserService;
import com.sms.utils.JwtUtils;
import com.sms.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Value("${sms.jwt.timeout}")
    private long timeout;
    @Value("${sms.jwt.my-secret-key}")
    private String mySecretKey;

    @Override
    public Result<UserVo> login(HttpServletRequest request, UserDto userDto) {
        //从数据库查询对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDto.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        //将密码进行MD5加密
        String password = DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes());
        if (user == null || !user.getPassword().equals(password)) {//判断密码是否正确
            return Result.error("用户名或密码错误，请重试");
        }
        if (user.getStatus() == 0) {//判断账户是否可用  0为不可用 1为可用 2逻辑删除
            return Result.error("该用户名已禁用，请重试");
        }
        if (user.getStatus() == 2) {//判断账户是否可用  0为不可用 1为可用 2逻辑删除
            return Result.error("该用户名不存在，请重试");
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(user, vo);
        String token = JwtUtils.generateToken(vo.getId(), timeout, mySecretKey);
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
    public Result<String> addUser(User user) {
        if (!isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        String msg = validateUser(user);
        if (msg != null) {
            return Result.error(msg);
        }
        log.info("新增学生信息:{}", user);
        String md5Password = DigestUtils.md5DigestAsHex("123456".getBytes());
        user.setPassword(md5Password);
        user.setStatus(1);
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        user.setAvatar(snowflake.nextIdStr() + ".png");
        save(user);
        return Result.success("新增成功");
    }

    private String validateUser(User user) {
        String idNumber = user.getIdNumber();
        String phone = user.getPhone();
        String email = user.getEmail();
        String username = user.getUsername();

        //身份证校验
        if (idNumber != null && !"".equals(idNumber.trim()) && !IdcardUtil.isValidCard(idNumber)) {
            return "验证码无效";
        }
        //手机验证
        if (phone != null && !"".equals(phone.trim()) && !ReUtil.isMatch("^\\d{11}$", phone)) {
            return "手机号码无效";
        }
        //邮箱验证
        if (email != null && !"".equals(email.trim()) && !ReUtil.isMatch("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", email)) {
            return "邮箱无效";
        }
        //用户名验证
        if (username != null && !"".equals(username.trim()) && !ReUtil.isMatch("^[a-zA-Z][a-zA-Z0-9]{4,15}$", username)) {
            return "请输入5-16位账号，且账号开头为字母，只能包含字母和数字";
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
    public Page<UserVo> getVoPage(Integer page, Integer pagesize, UserSelectDto userSelectDto) {
        Page<UserVo> userVoPage = new Page<>(page, pagesize);
        return userMapper.selectUserVoPage(userVoPage,userSelectDto);
    }
}
