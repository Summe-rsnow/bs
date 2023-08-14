package com.sms.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
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
import com.sms.entity.User;
import com.sms.service.CourseService;
import com.sms.service.GradeService;
import com.sms.service.UserService;
import com.sms.vo.CourseVo;
import com.sms.vo.GradeVo;
import com.sms.vo.UseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(tags = "成绩相关接口")
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

    /**
     * 成绩添加接口 需要判断是否为教师自己所授课程的成绩 并且没被添加过
     *
     * @param grade
     * @return
     */
    @ApiOperation("成绩添加")
    @PostMapping("/add")
    @CacheEvict(cacheNames = "GradeVisualization", allEntries = true)
    public Result<String> insert(@RequestBody Grade grade) {
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<Course> courseWrapper = new LambdaUpdateWrapper<>();
        courseWrapper.eq(Course::getId, grade.getCourseId());
        Course course = courseService.getOne(courseWrapper);
        if (course == null || !course.getTeacherId().equals(grade.getTeacherId())) {
            return Result.error("课程id不存在或创建的成绩不为教师自己所授课程的成绩");
        }
        LambdaUpdateWrapper<Grade> gradeWrapper = new LambdaUpdateWrapper<>();
        gradeWrapper.eq(Grade::getStudentId, grade.getStudentId());
        gradeWrapper.eq(Grade::getCourseId, grade.getCourseId());
        Grade one = gradeService.getOne(gradeWrapper);
        if (one != null) {
            return Result.error("该生的改门课程已经有成绩了，请勿重复添加");
        }
        User user = userService.getById(grade.getStudentId());
        if (user == null) {
            return Result.error("该学生id不存在，请重试");
        }
        log.info("新增成绩信息:{}", grade);
        gradeService.save(grade);
        return Result.success("新增成功");
    }

    /**
     * 从csv批量导入成绩的接口
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("文件批量添加成绩")
    @PostMapping("/csv/add")
    @CacheEvict(cacheNames = "GradeVisualization", allEntries = true)
    public Result<String> csvAdd(MultipartFile file) throws IOException {
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        if (file == null) {
            return Result.error("未选择文件");
        }
        // 获取文件后缀名
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        String[] split = filename.split("\\.");
        String fileExtension = split[split.length - 1];
        if (!"csv".equals(fileExtension)) {
            return Result.error("请上传格式正确的文件");
        }
        //从流读取csv文件
        InputStream in = file.getInputStream();
        BufferedReader utf8Reader = IoUtil.getUtf8Reader(in);
        CsvReader reader = CsvUtil.getReader();
        List<Grade> list = reader.read(utf8Reader, Grade.class);
        return gradeService.addGrade(list);
    }

    /**
     * 成绩修改接口 只能修改成绩的分数
     *
     * @param gradeEditDto
     * @return
     */
    @ApiOperation("成绩修改")
    @PostMapping("/edit")
    @CacheEvict(cacheNames = "GradeVisualization", allEntries = true)
    public Result<String> edit(@RequestBody GradeEditDto gradeEditDto) {
        log.info("修改成绩信息:{}", gradeEditDto);
        if (!userService.isTeacher()) {
            return Result.error("当前用户没有该操作权限");
        }
        return gradeService.edit(gradeEditDto);
    }

    /**
     * 成绩的删除接口
     *
     * @param id
     * @return
     */
    @ApiOperation("成绩删除")
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

    /**
     * 学生分页查询自己成绩的接口
     *
     * @param page
     * @param pagesize
     * @param gradeSelectDto
     * @return
     */
    @ApiOperation("学生个人成绩分页查询")
    @PostMapping("/{page}/{pagesize}")
    public Result<Page<GradeVo>> getGradePageByStudentId(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody GradeSelectDto gradeSelectDto) {
        if (!userService.isStudent()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<GradeVo> gradePage = gradeService.getVoPageByStudentId(BaseContext.getCurrentId(), page, pagesize, gradeSelectDto);
        return Result.success(gradePage);
    }

    /**
     * 教师分页查询自己添加的成绩的信息接口
     *
     * @param page
     * @param pagesize
     * @param gradeSelectDto
     * @return
     */
    @ApiOperation("教师的学生成绩分页查询")
    @PostMapping("/change/{page}/{pagesize}")
    public Result<Page<GradeVo>> changeGradePage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody GradeSelectDto gradeSelectDto) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        Page<GradeVo> gradePage = gradeService.getVoPageByTeacherId(BaseContext.getCurrentId(), page, pagesize, gradeSelectDto);
        return Result.success(gradePage);
    }

    /**
     * 教师分页查询学生信息接口
     *
     * @param page
     * @param pagesize
     * @param userSelectDto
     * @return
     */
    @ApiOperation("教师的学生信息分页查询")
    @PostMapping("/student/{page}/{pagesize}")
    public Result<Page<UseVo>> studentPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody UserSelectDto userSelectDto) {
        if (!userService.isTeacher()) {
            return Result.success("当前用户没有该操作权限");
        }
        userSelectDto.setUserGrant(2);
        Page<UseVo> voPage = userService.getVoPage(page, pagesize, userSelectDto);
        return Result.success(voPage);
    }

    /**
     * 教师分页查询自己的课程的信息接口
     *
     * @param page
     * @param pagesize
     * @param courseSelectDto
     * @return
     */
    @ApiOperation("教师的课程信息分页查询")
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
