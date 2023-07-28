package com.sms.vo;

import lombok.Data;

@Data
public class GradeVo {
    private Long id;
    private Long studentId;//学生id
    private Long teacherId;//教师id
    private Integer score;//分数
    private String studentName;//学生姓名
    private String teacherName;//教师姓名
    private String courseName;//课程名
}
