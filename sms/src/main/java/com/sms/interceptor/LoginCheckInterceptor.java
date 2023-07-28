package com.sms.interceptor;

import com.alibaba.fastjson.JSON;
import com.sms.common.BaseContext;
import com.sms.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (BaseContext.getCurrentId() != null) {
            log.info("线程池之前的id:{}", BaseContext.getCurrentId());
        }
        //如果已登录，则放行并将id保存在ThreadLocal中
        Long id = (Long) request.getSession().getAttribute("userId");
        log.info("拦截到链接:{},用户id:{}", request.getRequestURI(), id);
        if (id != null) {
            BaseContext.setCurrentId(id);
            return true;
        }

        //未登录，则拦截请求
        response.getWriter().write(JSON.toJSONString(Result.error("用户未登录")));
        return false;
    }
}

