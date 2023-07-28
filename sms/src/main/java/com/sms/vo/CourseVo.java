package com.sms.vo;

import lombok.Data;

@Data
public class CourseVo {
    private Long id;
    private String name;//课程名称
    private Long teacherId;//授课老师id
    private String teacherName;//教师名称
}
