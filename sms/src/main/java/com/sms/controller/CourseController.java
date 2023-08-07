package com.sms.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sms.common.Result;
import com.sms.dto.CourseSelectDto;
import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.service.UserService;
import com.sms.vo.CourseVo;
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

@Api(tags = "课程相关接口")
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
    @ApiOperation("课程添加")
    @PostMapping("/add")
    @CacheEvict(cacheNames = "CourseVisualization", allEntries = true)
    public Result<String> add(@RequestBody Course course) {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        return courseService.addCourse(course);
    }

    /**
     * 从csv批量导入课程的接口
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("文件批量添加课程")
    @PostMapping("/csv/add")
    @CacheEvict(cacheNames = "CourseVisualization", allEntries = true)
    public Result<String> csvAdd(MultipartFile file) throws IOException {
        if (!userService.isAdmin()) {
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
        if (!fileExtension.equals("csv")) {
            return Result.error("请上传格式正确的文件");
        }
        //从流读取csv文件
        InputStream in = file.getInputStream();
        BufferedReader utf8Reader = IoUtil.getUtf8Reader(in);
        CsvReader reader = CsvUtil.getReader();
        List<Course> list = reader.read(utf8Reader, Course.class);
        return courseService.addCourse(list);
    }

    /**
     * 课程信息修改接口
     *
     * @param course
     * @return
     */
    @ApiOperation("课程修改")
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
    @ApiOperation("课程删除")
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

    /**
     * 课程信息分页查询接口
     *
     * @param page
     * @param pagesize
     * @param courseSelectDto
     * @return
     */
    @ApiOperation("课程分页查询")
    @PostMapping("/{page}/{pagesize}")
    public Result<Page<CourseVo>> getUserPage(@PathVariable Integer page, @PathVariable Integer pagesize, @RequestBody CourseSelectDto courseSelectDto) {
        if (!userService.isAdmin()) {
            return Result.success("当前用户没有该操作权限");
        }
        return Result.success(courseService.getVoPage(page, pagesize, null, courseSelectDto));
    }
}
