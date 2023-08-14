package com.sms.dto;

import lombok.Data;

/**
 * @author sssnow
 */
@Data
public class UserAddDto {
    private String username;
    private String name;//真实姓名
    private String email;//邮箱
    private String gender;//性别
    private Integer age;//年龄
    private String phone;//手机号码
    private String idNumber;//身份证号码
    private Integer userGrant;//权限等级 0管理员 1教师 2学生
}
