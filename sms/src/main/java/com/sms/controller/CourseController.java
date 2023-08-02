package com.sms.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.Result;
import com.sms.dto.CourseSelectDto;
import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.service.UserService;
import com.sms.vo.CourseVo;
import com.sms.vo.UseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {
    @Resource
    CourseService courseService;

    @Resource
    UserService userService;

    /**
     * 课程添加接口
     *
     * @param course
     * @return
     */
    @PostMapping("/add")
    @CacheEvict(cacheNames = "CourseVisualization", allEntries = true)
    public Result<String> add(@RequestBody Course course) {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        return courseService.add(course);
    }

    /**
     * 课程信息修改接口
     *
     * @param course
     * @return
     */
    @PostMapping("/edit")
    @CacheEvict(cacheNames = "CourseVisualization", allEntries = true)
    public Result<UseVo> edit(@RequestBody Course course) {
        log.info("修改信息:{}", course);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, course.getId());
        courseService.update(course, wrapper);
        return Result.success("修改成功");
    }

    /**
     * 课程删除接口
     *
     * @param id
     * @return
     */
    @PostMapping("/del/{id}")
    @CacheEvict(cacheNames = "CourseVisualization", allEntries = true)
    public Result<String> editInfo(@PathVariable(value = "id") Long id) {
        log.info("删除课程id:{}", id);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        courseService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{page}/{pagesize}")
    public Result<Page<CourseVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody CourseSelectDto courseSelectDto) {
        if (!userService.isAdmin()) {
            return Result.success("当前用户没有该操作权限");
        }
        return Result.success(courseService.getVoPage(page, pagesize, null, courseSelectDto));
    }
}
