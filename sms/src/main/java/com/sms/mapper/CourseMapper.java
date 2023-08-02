package com.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.VisualizationData;
import com.sms.dto.CourseSelectDto;
import com.sms.entity.Course;
import com.sms.vo.CourseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    Page<CourseVo> selectCourseVoPage(@Param("page") Page<CourseVo> page, @Param("id") Long id, @Param("courseSelectDto") CourseSelectDto courseSelectDto);

    List<VisualizationData> courseCountRanking();
}
