package com.sms.config;

import com.sms.common.JacksonObjectMapper;
import com.sms.interceptor.LoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sssnow
 */
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Resource
    LoginCheckInterceptor loginCheckInterceptor;

    /**
     * 接口文档生成配置
     */
    @Bean
    public Docket docket1() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("学生成绩管理系统项目接口文档")
                .version("2.0")
                .description("学生成绩管理系统项目接口文档")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("主分组")
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.sms.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //为接口文档添加静态资源映射
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将java对象转成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的对象转换器放入MVC对象转换器的集合中，设置索引0优先使用
        converters.add(0, messageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                //添加拦截器的拦截路径
                .addPathPatterns(
                        "/user/**",
                        "/grade/**",
                        "/course/**",
                        "/common/**",
                        "/notice/**")
                //不拦截的路径
                .excludePathPatterns(
                        "/user/login",
                        "/user/logout",
                        "/user/self/info",
                        "/user/phone_code/*",
                        "/user/pwd/forget",
                        "/common/vcode");
    }
}
