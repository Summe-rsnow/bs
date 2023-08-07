package com.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sms.common.Result;
import com.sms.common.VisualizationData;
import com.sms.dto.CourseSelectDto;
import com.sms.entity.Course;
import com.sms.entity.User;
import com.sms.mapper.CourseMapper;
import com.sms.mapper.UserMapper;
import com.sms.service.CourseService;
import com.sms.vo.CourseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Resource
    CourseMapper courseMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public Page<CourseVo> getVoPage(Integer page, Integer pagesize, Long id, CourseSelectDto courseSelectDto) {

        Page<CourseVo> courseVoPage = new Page<>(page, pagesize);

        return courseMapper.selectCourseVoPage(courseVoPage, id, courseSelectDto);
    }

    @Override
    public Result<String> addCourse(Course course) {
        User user = userMapper.selectById(course.getTeacherId());
        if (user == null || user.getUserGrant() != 1) {
            return Result.error("该id不是教师id!");
        }
        log.info("添加课程:{}", course);
        save(course);
        return Result.success("添加成功");
    }

    @Override
    public Result<String> addCourse(List<Course> courses) {
        Integer flag = 0;
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();

        for (Course course : courses) {
            User user = userMapper.selectById(course.getTeacherId());
            if (user == null || user.getUserGrant() != 1) {
                flag++;
            }
        }
        if (flag > 0){
            return Result.error("请校验信息的合法性");
        }
        saveBatch(courses);
        return Result.success("添加成功");
    }

    @Override
    public List<VisualizationData> courseCountRanking() {
        return courseMapper.courseCountRanking();
    }
}
