package com.sms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.entity.Grade;
import com.sms.entity.User;
import com.sms.service.CourseService;
import com.sms.service.GradeService;
import com.sms.service.UserService;
import com.sms.vo.CourseVo;
import com.sms.vo.GradeVo;
import com.sms.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public Result<String> insert(@RequestBody Grade grade) {
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        log.info("新增成绩信息:{}", grade);
        gradeService.save(grade);
        return Result.success(null, "新增成功");
    }

    @PostMapping("/edit")
    public Result<UserVo> edit(@RequestBody Grade grade) {
        log.info("修改信息:{}", grade);
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<Grade> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Grade::getId, grade.getId());
        gradeService.update(grade, wrapper);
        return Result.success("修改成功");
    }

    @PostMapping("/del/{id}")
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
    public Result<Page<GradeVo>> getGradePageByStudentId(@PathVariable Integer page, @PathVariable Integer pagesize) {
        if (!userService.isStudent()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<GradeVo> gradePage = gradeService.getVoPageByStudentId(BaseContext.getCurrentId(), page, pagesize);
        return Result.success(gradePage);
    }

    //教师查询自己所教学生的成绩信息
    @PostMapping("/change/{page}/{pagesize}")
    public Result<Page<GradeVo>> changeGradePage(@PathVariable Integer page, @PathVariable Integer pagesize) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<GradeVo> gradePage = gradeService.getVoPageByTeacherId(BaseContext.getCurrentId(), page, pagesize);
        return Result.success(gradePage);
    }

    //教师查询自己教的学生的信息
    @PostMapping("/student/{page}/{pagesize}")
    public Result<Page<User>> studentPage(@PathVariable Integer page, @PathVariable Integer pagesize) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<User> userPage = new Page<>(page,pagesize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserGrant, 2);
        return Result.success(userService.page(userPage, wrapper));
    }

    //教师查询自己的课的信息
    @PostMapping("/course/{page}/{pagesize}")
    public Result<Page<CourseVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        return Result.success(courseService.getVoPage(page, pagesize));
    }
}
