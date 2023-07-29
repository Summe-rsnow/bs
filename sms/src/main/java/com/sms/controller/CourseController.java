package com.sms.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.Result;
import com.sms.entity.Course;
import com.sms.entity.User;
import com.sms.service.CourseService;
import com.sms.service.UserService;
import com.sms.vo.CourseVo;
import com.sms.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/add")
    public Result<String> add(@RequestBody Course course) {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        User user = userService.getById(course.getTeacherId());
        if (user == null || user.getUserGrant() != 1) {
            return Result.error("该id不是教师id!");
        }
        log.info("添加课程:{}", course);
        courseService.save(course);
        return Result.success("添加成功");
    }

    @PostMapping("/edit")
    public Result<UserVo> edit(@RequestBody Course course) {
        log.info("修改信息:{}", course);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, course.getId());
        courseService.update(course, wrapper);
        return Result.success("修改成功");
    }

    @PostMapping("/del/{id}")
    public Result<String> editInfo(@PathVariable(value = "id") Long id) {
        log.info("删除课程id:{}", id);
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        courseService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{page}/{pagesize}")
    public Result<Page<CourseVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize) {
        if (!userService.isAdmin()) {
            return Result.success("当前用户没有该操作权限");
        }
        return Result.success(courseService.getVoPage(page, pagesize));
    }
}
