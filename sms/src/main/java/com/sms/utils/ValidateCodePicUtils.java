package com.sms.utils;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class ValidateCodePicUtils {
    public static ValidateCodePic create(int width, int height, int length) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        //画笔的基础设置
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        String code = ValidateCodeUtils.generateAlphanumericCode(length);
        Font font = new Font("Arial", Font.BOLD, 24);
        graphics.setFont(font);

        //绘制基础图片
        for (int i = 0; i < code.length(); i++) {
            int x = (width / code.length()) * i + 10;
            int y = height / 2 + 10;
            graphics.setColor(new Color(20 + new Random().nextInt(110),
                    20 + new Random().nextInt(110), 20 + new Random().nextInt(110)));
            graphics.drawString(code.charAt(i) + "", x, y);
        }

        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            int x1 = new Random().nextInt(width);
            int y1 = new Random().nextInt(height);
            int x2 = new Random().nextInt(width);
            int y2 = new Random().nextInt(height);
            graphics.setColor(new Color(20 + new Random().nextInt(110),
                    20 + new Random().nextInt(110), 20 + new Random().nextInt(110)));
            graphics.drawLine(x1, y1, x2, y2);
        }

        // 添加干扰点
        for (int i = 0; i < 30; i++) {
            int x = new Random().nextInt(width);
            int y = new Random().nextInt(height);
            graphics.setColor(new Color(20 + new Random().nextInt(110),
                    20 + new Random().nextInt(110), 20 + new Random().nextInt(110)));
            graphics.drawRect(x, y, 0, 0);
        }

        graphics.dispose();

        return new ValidateCodePic(code, image);
    }

    /**
     * 验证码图片工具类生成的实例化对象
     */
    @Getter
    public static class ValidateCodePic {
        private final String code;
        private final BufferedImage image;

        public ValidateCodePic(String code, BufferedImage image) {
            this.code = code;
            this.image = image;
        }

        public void write(OutputStream out) throws IOException {
            ImageIO.write(this.getImage(), "JPEG", out);
        }
    }
}