package com.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author sssnow
 */
@Component
@ConfigurationProperties(prefix = "sms.jwt") // yml中的前缀
@Primary //当一个 Bean 被标注为 @Primary 时，Spring 将会优先选择该 Bean 进行依赖注入。
@Data
public class JwtConfig {
    private String mySecretKey;
    private long timeout;
}
