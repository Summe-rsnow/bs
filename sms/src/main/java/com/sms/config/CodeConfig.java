package com.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sms.code") // yml中的前缀
@Primary //当一个 Bean 被标注为 @Primary 时，Spring 将会优先选择该 Bean 进行依赖注入。
@Data
public class CodeConfig {
    private String signName;
    private String templateCode;
    private boolean phoneCode;
    private boolean verificationCode;
}
