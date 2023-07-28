package com.sms.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.entity.User;
import com.sms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    UserService userService;

    @Value("${sms.path}")
    private String basePath;

    //头像上传接口
    @PostMapping("/avatar/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        String fileName;
        String path;
        //获取文件原始文件名,并获文件后缀
        User user = userService.getById(BaseContext.getCurrentId());
        String userAvatar = user.getAvatar();
        if (userAvatar == null || userAvatar.isEmpty()) {
            String suffix = getSuffix(file);
            //使用雪花算法生成头像名称 防止头像名称碰撞覆盖
            Snowflake snowflake = IdUtil.getSnowflake(1, 1);
            fileName = snowflake.nextIdStr() + suffix;
            updateAvatar(fileName);
        }else {
            fileName = userAvatar;
        }
        path = basePath + "\\" + userAvatar;
        saveAvatar(file, path, fileName);
        return Result.success(fileName);
    }

    private static String getSuffix(MultipartFile file) {
        String suffix = null;
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return suffix;
    }

    private void saveAvatar(MultipartFile file, String path, String fileName) throws IOException {
        //此方法创建文件时，会自动插件父级文件夹
        File touch = FileUtil.touch(path);
        //将图片写入文件中
        file.transferTo(touch);
        log.info("已存储文件{}至{}", fileName, basePath);
    }

    private void updateAvatar(String fileName) {
        User user = userService.getById(BaseContext.getCurrentId());
        user.setAvatar(fileName);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,user.getId());
        userService.update(user,wrapper);
    }

    //头像下载接口
    @GetMapping("/avatar/download")
    public void avatar(HttpServletResponse response) {
        User user = userService.getById(BaseContext.getCurrentId());
        String userAvatar = user.getAvatar();
        if (userAvatar == null || userAvatar.isEmpty()) {
            userAvatar = "1.png";
        }
        String fielname = basePath + "\\" + userAvatar;
        if (!FileUtil.exist(fielname)) {
            fielname = basePath + "\\1.png";
        }
        log.info("需要下的文件名称为：{}", fielname);
        //设置相应头 让头像不缓存 确保切换用户时能刷新头像
        responseHeader(response);
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(fielname);
            //输出流，将图片写回浏览器
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len;
            byte[] bytes = new byte[2048];
            while ((len = fileInputStream.read(bytes)) != -1) {
                servletOutputStream.write(bytes, 0, len);
                servletOutputStream.flush();
            }
            servletOutputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    private static void responseHeader(HttpServletResponse response) {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    //验证码返回接口
    @GetMapping("/vcode")
    public void vcode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(140, 37);
        lineCaptcha.createCode();
        String vcode = lineCaptcha.getCode();
        log.info("生成的验证码是：{}", vcode);
        //输出流，将验证码写回浏览器
        ServletOutputStream servletOutputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        lineCaptcha.write(servletOutputStream);
    }
}
