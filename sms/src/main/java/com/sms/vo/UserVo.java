package com.sms.vo;

import lombok.Data;

@Data
public class UserVo {
    private Long id;
    private String username;
    private String name;//真实姓名
    private String email;//邮箱
    private String gender;//性别
    private Integer age;//年龄
    private String phone;//手机号码
    private String idNumber;//身份证号码
    private Integer userGrant;//权限等级 0管理员 1教师 2学生
    private Integer status;//0启用 1禁用 2逻辑删除
    private String avatar;//头像图片的名称
    private String token;//令牌
}
