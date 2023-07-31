package com.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sms.entity.TeacherCourseStudent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TCSMapper extends BaseMapper<TeacherCourseStudent> {
}
