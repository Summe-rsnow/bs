package com.sms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sssnow
 */
@Data
public class Grade {//成绩表
    private Long id;
    private Long courseId;//课程id
    private Long studentId;//学生id
    private Long teacherId;//教师id
    private Integer score;//分数

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
