package com.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sms.common.Result;
import com.sms.dto.PwdDto;
import com.sms.dto.UserLoginDto;
import com.sms.dto.UserSelectDto;
import com.sms.entity.User;
import com.sms.vo.UseVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService extends IService<User> {
    Result<UseVo> login(HttpServletResponse response, UserLoginDto userLoginDto);

    Result<String> resetPassword(PwdDto pwdDto);

    Result<String> addUser(List<User> users);

    boolean isAdmin();

    boolean isTeacher();

    boolean isStudent();

    Integer grantLevel();

    Page<UseVo> getVoPage(Integer page, Integer pagesize, UserSelectDto userSelectDto);
}
