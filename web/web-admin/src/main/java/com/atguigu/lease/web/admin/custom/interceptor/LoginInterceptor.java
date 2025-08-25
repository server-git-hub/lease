package com.atguigu.lease.web.admin.custom.interceptor;

import com.atguigu.lease.common.Exception.notDeleteException;
import com.atguigu.lease.common.login.JwtUtil;
import com.atguigu.lease.common.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //验证是否登录
        String token = request.getHeader("access_token");
        if(ObjectUtils.isEmpty(token)){
            throw new notDeleteException(ResultCodeEnum.APP_LOGIN_AUTH);
        }
        //验证token是否过期
        JwtUtil.parseToken(token);
        return true;
    }
}
