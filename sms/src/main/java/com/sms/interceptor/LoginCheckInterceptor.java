package com.sms.interceptor;

import com.sms.common.BaseContext;
import com.sms.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    private String mySecretKey="summersnow";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("拦截到链接:{}", request.getRequestURI());
        //如果已登录，则放行并将id保存在ThreadLocal中
        String token = request.getHeader("Authorization");
        try {
            JwtUtils.validateToken(token, mySecretKey);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            response.setStatus(401);
            return false;
        }
        Long id = JwtUtils.getUserIdFromToken(token, mySecretKey);
        BaseContext.setCurrentId(id);
        return true;
    }
}

