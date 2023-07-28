package com.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.dto.PwdDto;
import com.sms.dto.UserDto;
import com.sms.entity.User;
import com.sms.mapper.UserMapper;
import com.sms.service.UserService;
import com.sms.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;


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
        request.getSession().setAttribute("userId", user.getId());//在request域中存储用户id 表示为登录状态
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(user, vo);
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
    public Page<UserVo> getVoPage(Integer page, Integer pagesize) {
        Page<UserVo> userVoPage = new Page<>(page, pagesize);
        return userMapper.selectUserVoPage(userVoPage);
    }
}
