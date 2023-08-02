package com.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sms.common.Result;
import com.sms.common.VisualizationData;
import com.sms.dto.CourseSelectDto;
import com.sms.entity.Course;
import com.sms.vo.CourseVo;

import java.util.List;

public interface CourseService extends IService<Course> {

    Page<CourseVo> getVoPage(Integer page, Integer pagesize, Long id, CourseSelectDto courseSelectDto);

    Result add(Course course);

    List<VisualizationData> courseCountRanking();
}
