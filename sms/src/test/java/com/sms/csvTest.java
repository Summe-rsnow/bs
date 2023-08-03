package com.sms;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.sms.dto.UserAddDto;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.util.List;

@SpringBootTest
public class csvTest {
    @Test
    public void read() {
        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        //转换为Bean
        //以下为从文件读取方法
        //BufferedInputStream in = FileUtil.getInputStream("C:\\Users\\sssnow\\Downloads\\MOCK_DATA.csv");
        //BufferedReader utf8Reader = IoUtil.getUtf8Reader(in);
        BufferedReader utf8Reader = ResourceUtil.getUtf8Reader("MOCK_DATA.csv");
        List<UserAddDto> list = reader.read(utf8Reader, UserAddDto.class);
        list.forEach(i -> {
            System.out.println(i);
        });
    }

    @Data
    private static class TestBean {
        // 如果csv中标题与字段不对应，可以使用alias注解设置别名
        private String name;
        private String gender;
        private String email;
        private Integer age;
        private String phone;
        private String idNumber;
        private Integer userGrant;
        private String username;
    }
}
