package com.sms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;//真实姓名
    private String email;//邮箱
    private String gender;//性别
    private Integer age;//年龄
    private String phone;//手机号码
    private String idNumber;//身份证号码
    private Integer userGrant;//权限等级 0管理员 1教师 2学生
    private String avatar;//头像图片的名称
    private Integer status;//0启用 1禁用

    @TableLogic
    private Integer deleted;//逻辑删除标志

    @TableField(fill = FieldFill.INSERT)//插入时填充
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
