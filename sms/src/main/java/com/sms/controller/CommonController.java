package com.sms.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.common.VisualizationData;
import com.sms.entity.Grade;
import com.sms.entity.User;
import com.sms.service.CourseService;
import com.sms.service.GradeService;
import com.sms.service.UserService;
import com.sms.utils.ValidateCodePicUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    UserService userService;

    @Resource
    GradeService gradeService;

    @Resource
    CourseService courseService;

    @Value("${sms.path}")
    private String basePath;

    private static void responseHeader(HttpServletResponse response) {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void saveAvatar(MultipartFile file, String path, String fileName) throws IOException {
        //此方法创建文件时，会自动创建父级文件夹
        File touch = FileUtil.touch(path);
        //将图片写入文件中
        file.transferTo(touch);
        log.info("已存储文件{}至{}", fileName, basePath);
    }

    /**
     * 头像上传接口
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/avatar/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        //获取文件原始文件名,并获文件后缀
        User user = userService.getById(BaseContext.getCurrentId());
        String fileName = user.getAvatar();
        String path = basePath + "\\" + fileName;
        saveAvatar(file, path, fileName);
        return Result.success(fileName);
    }

    /**
     * 头像的下载接口
     *
     * @param response
     * @param id
     * @throws IOException
     */
    @PostMapping("/avatar/download/{id}")
    public void avatar(HttpServletResponse response, @PathVariable long id) throws IOException {
        User user = userService.getById(id);
        String userAvatar = user.getAvatar();
        String fielname = basePath + "\\" + userAvatar;
        if (!FileUtil.exist(fielname)) {
            fielname = basePath + "\\1.png";
        }
        log.info("需要下载的文件名称为：{}", fielname);
        //设置响应头 让头像不缓存 确保切换用户时能刷新头像
        responseHeader(response);
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(fielname);
        //输出流，将图片写回浏览器
        ServletOutputStream servletOutputStream = response.getOutputStream();
        byte[] bytes = IoUtil.readBytes(fileInputStream);
        IoUtil.write(servletOutputStream, true, bytes);
    }

    /**
     * 获取登录验证码的接口
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/vcode")
    public void vcode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //定义图形验证码的长，宽和长度
        ValidateCodePicUtils.ValidateCodePic validateCodePic = ValidateCodePicUtils.create(140, 37, 4);
        String code = validateCodePic.getCode();
        request.getSession().setAttribute("vcode", code);
        log.info("生成的验证码是：{}", code);
        //输出流，将验证码写回浏览器
        ServletOutputStream servletOutputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        validateCodePic.write(servletOutputStream);
    }

    /**
     * 男女比率可视化数据查询接口，使用缓存功能
     *
     * @return
     */
    @PostMapping("/data/user/mf_ratio")
    @Cacheable(cacheNames = "UserVisualization", key = "'mfRatio'")
    public Result<List<VisualizationData>> mfRatio() {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();

        Long total = userService.count(wrapper);

        wrapper.eq(User::getGender, "男");
        Long male = userService.count(wrapper);

        wrapper.clear();
        wrapper.eq(User::getGender, "女");
        Long female = userService.count(wrapper);

        Long undefined = total - male - female;

        List<VisualizationData> list = List.of(
                new VisualizationData(undefined, "未设定"),
                new VisualizationData(male, "男"),
                new VisualizationData(female, "女"));
        return Result.success(list);
    }

    /**
     * 用户权限比率可视化数据查询接口，使用缓存功能
     *
     * @return
     */
    @PostMapping("/data/user/grant_ratio")
    @Cacheable(cacheNames = "UserVisualization", key = "'grantRatio'")
    public Result<List<VisualizationData>> grantRatio() {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(User::getUserGrant, 0);
        Long admin = userService.count(wrapper);

        wrapper.clear();
        wrapper.eq(User::getUserGrant, 1);
        Long teacher = userService.count(wrapper);

        wrapper.clear();
        wrapper.eq(User::getUserGrant, 2);
        Long student = userService.count(wrapper);

        List<VisualizationData> list = List.of(
                new VisualizationData(admin, "管理员"),
                new VisualizationData(teacher, "教师"),
                new VisualizationData(student, "学生"));
        return Result.success(list);
    }

    /**
     * 成绩分布可视化数据查询接口，使用缓存功能
     *
     * @return
     */
    @PostMapping("/data/grade/grade_distribution")
    @Cacheable(cacheNames = "GradeVisualization", key = "'gradeDistribution'")
    public Result<List<VisualizationData>> gradeDistribution() {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        LambdaUpdateWrapper<Grade> wrapper = new LambdaUpdateWrapper<>();
        // 查询成绩在 0-60 范围内的数量，并命名为 gradeA
        wrapper.between(Grade::getScore, 0, 60);
        Long gradeA = gradeService.count(wrapper);
        // 查询成绩在 61-70 范围内的数量，并命名为 gradeB
        wrapper.clear();
        wrapper.between(Grade::getScore, 61, 70);
        Long gradeB = gradeService.count(wrapper);
        // 查询成绩在 71-80 范围内的数量，并命名为 gradeC
        wrapper.clear();
        wrapper.between(Grade::getScore, 71, 80);
        Long gradeC = gradeService.count(wrapper);
        // 查询成绩在 81-90 范围内的数量，并命名为 gradeD
        wrapper.clear();
        wrapper.between(Grade::getScore, 81, 90);
        Long gradeD = gradeService.count(wrapper);
        // 查询成绩在 91-100 范围内的数量，并命名为 gradeE
        wrapper.clear();
        wrapper.between(Grade::getScore, 91, 100);
        Long gradeE = gradeService.count(wrapper);
        List<VisualizationData> list = List.of(
                new VisualizationData(gradeA, "60以下"),
                new VisualizationData(gradeB, "61-70"),
                new VisualizationData(gradeC, "71-80"),
                new VisualizationData(gradeD, "81-90"),
                new VisualizationData(gradeE, "91-100"));
        return Result.success(list);
    }

    /**
     * 教师授课数量排行可视化数据查询接口，使用缓存功能
     *
     * @return
     */
    @PostMapping("/data/course/count_ranking")
    @Cacheable(cacheNames = "CourseVisualization", key = "'countRanking'")
    public Result<List<VisualizationData>> countRanking() {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        List<VisualizationData> list = courseService.courseCountRanking();
        return Result.success(list);
    }
}
