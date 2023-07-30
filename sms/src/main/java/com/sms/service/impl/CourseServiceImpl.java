package com.sms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.Result;
import com.sms.dto.CourseSelectDto;
import com.sms.entity.Course;
import com.sms.mapper.CourseMapper;
import com.sms.service.CourseService;
import com.sms.vo.CourseVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Resource
    CourseMapper courseMapper;

    @Override
    public Page<CourseVo> getVoPage(Integer page, Integer pagesize, CourseSelectDto courseSelectDto) {

        Page<CourseVo> courseVoPage = new Page<>(page, pagesize);

        return courseMapper.selectCourseVoPage(courseVoPage, courseSelectDto);
    }

    @Override
    public Result add(Course course) {
        return null;
    }
}
