package com.sms.dto;

import lombok.Data;

/**
 * @author sssnow
 */
@Data
public class UserSelectDto {
    private String name;
    private String gender;
    private Integer userGrant;//权限等级 0管理员 1教师 2学生
}
