package com.sms.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.dto.CourseSelectDto;
import com.sms.dto.GradeEditDto;
import com.sms.dto.GradeSelectDto;
import com.sms.dto.UserSelectDto;
import com.sms.entity.Course;
import com.sms.entity.Grade;
import com.sms.service.CourseService;
import com.sms.service.GradeService;
import com.sms.service.UserService;
import com.sms.vo.CourseVo;
import com.sms.vo.GradeVo;
import com.sms.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/grade")
@Slf4j
public class GradeController {

    @Resource
    GradeService gradeService;
    @Resource
    CourseService courseService;
    @Resource
    UserService userService;

    @PostMapping("/add")
    @CacheEvict(cacheNames = "GradeVisualization", allEntries = true)
    public Result<String> insert(@RequestBody Grade grade) {
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, grade.getCourseId());
        Course course = courseService.getOne(wrapper);

        if (!course.getTeacherId().equals(grade.getTeacherId())) {
            return Result.error("创建的成绩应该为教师自己所授课程的成绩");
        }
        log.info("新增成绩信息:{}", grade);
        gradeService.save(grade);
        return Result.success("新增成功");
    }

    /**
     * 成绩修改接口 只能修改成绩的分数
     */
    @PostMapping("/edit")
    @CacheEvict(cacheNames = "GradeVisualization", allEntries = true)
    public Result<String> edit(@RequestBody GradeEditDto gradeEditDto) {
        log.info("修改成绩信息:{}", gradeEditDto);
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        return gradeService.edit(gradeEditDto);
    }

    @PostMapping("/del/{id}")
    @CacheEvict(cacheNames = "GradeVisualization", allEntries = true)
    public Result<String> editInfo(@PathVariable(value = "id") Long id) {
        log.info("删除成绩id:{}", id);
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        gradeService.removeById(id);
        return Result.success("删除成功");
    }

    //学生查询自己的成绩
    @PostMapping("/{page}/{pagesize}")
    public Result<Page<GradeVo>> getGradePageByStudentId(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody GradeSelectDto gradeSelectDto) {
        if (!userService.isStudent()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<GradeVo> gradePage = gradeService.getVoPageByStudentId(BaseContext.getCurrentId(), page, pagesize, gradeSelectDto);
        return Result.success(gradePage);
    }

    //教师查询自己所教学生的成绩信息
    @PostMapping("/change/{page}/{pagesize}")
    public Result<Page<GradeVo>> changeGradePage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody GradeSelectDto gradeSelectDto) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<GradeVo> gradePage = gradeService.getVoPageByTeacherId(BaseContext.getCurrentId(), page, pagesize, gradeSelectDto);
        return Result.success(gradePage);
    }

    //教师查询学生的信息
    @PostMapping("/student/{page}/{pagesize}")
    public Result<Page<UserVo>> studentPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody UserSelectDto userSelectDto) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        userSelectDto.setUserGrant(2);
        Page<UserVo> voPage = userService.getVoPage(page, pagesize, userSelectDto);
        return Result.success(voPage);
    }

    //教师查询自己的课程的信息
    @PostMapping("/course/{page}/{pagesize}")
    public Result<Page<CourseVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody CourseSelectDto courseSelectDto) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        Long id = BaseContext.getCurrentId();
        Page<CourseVo> voPage = courseService.getVoPage(page, pagesize, id, courseSelectDto);
        return Result.success(voPage);
    }
}
