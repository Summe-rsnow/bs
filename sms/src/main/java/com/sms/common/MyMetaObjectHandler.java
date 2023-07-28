package com.sms.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

//公共字段自动填充配置类
@Configuration
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long id = BaseContext.getCurrentId();
        metaObject.setValue("createUser", id);
        metaObject.setValue("updateUser", id);
        LocalDateTime now = LocalDateTime.now();
        metaObject.setValue("createTime", now);
        metaObject.setValue("updateTime", now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
