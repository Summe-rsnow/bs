package com.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sms.common.Result;
import com.sms.dto.UserDto;
import com.sms.entity.User;
import com.sms.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public interface UserService extends IService<User> {
    Result<UserVo> login(HttpServletRequest request, UserDto userDto);

    boolean isAdmin();

    boolean isTeacher();

    boolean isStudent();

    Integer grantLevel();

    Page<UserVo> getVoPage(Integer page, Integer pagesize);
}
