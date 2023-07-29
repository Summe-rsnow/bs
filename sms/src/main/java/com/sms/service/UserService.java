package com.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sms.common.Result;
import com.sms.dto.PwdDto;
import com.sms.dto.UserDto;
import com.sms.dto.UserSelectDto;
import com.sms.entity.User;
import com.sms.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    Result<UserVo> login(HttpServletRequest request, UserDto userDto);

    Result<String> resetPassword(PwdDto pwdDto);

    Result<String> addUser(User user);

    boolean isAdmin();

    boolean isTeacher();

    boolean isStudent();

    Integer grantLevel();

    Page<UserVo> getVoPage(Integer page, Integer pagesize, UserSelectDto userSelectDto);
}
