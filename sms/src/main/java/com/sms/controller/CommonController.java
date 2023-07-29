package com.sms.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import com.sms.entity.User;
import com.sms.service.UserService;
import com.sms.utils.ValidateCodePicUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    UserService userService;

    @Value("${sms.path}")
    private String basePath;

    private static void responseHeader(HttpServletResponse response) {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    //头像上传接口
    @PostMapping("/avatar/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        //获取文件原始文件名,并获文件后缀
        User user = userService.getById(BaseContext.getCurrentId());
        String fileName = user.getAvatar();
        String path = basePath + "\\" + fileName;
        saveAvatar(file, path, fileName);
        return Result.success(fileName);
    }

    private void saveAvatar(MultipartFile file, String path, String fileName) throws IOException {
        //此方法创建文件时，会自动创建父级文件夹
        File touch = FileUtil.touch(path);
        //将图片写入文件中
        file.transferTo(touch);
        log.info("已存储文件{}至{}", fileName, basePath);
    }

    //头像下载接口
    @PostMapping  ("/avatar/download/{id}")
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

    //验证码返回接口
    @GetMapping ("/vcode")
    public void vcode(HttpServletResponse response) throws IOException {
        //定义图形验证码的长，宽和长度
        ValidateCodePicUtils.ValidateCodePic validateCodePic = ValidateCodePicUtils.create(140, 37, 4);
        String code = validateCodePic.getCode();
        log.info("生成的验证码是：{}", code);
        //输出流，将验证码写回浏览器
        ServletOutputStream servletOutputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        validateCodePic.write(servletOutputStream);
    }
}
