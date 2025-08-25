package com.atguigu.lease.web.app.custom.config;


import com.atguigu.lease.web.app.custom.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class SpringMVCConfiguration implements WebMvcConfigurer {

    @Value("${web.app.include}")
    private String include[];
    @Value("${web.app.exclude}")
    private String exclude[];

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(include).excludePathPatterns(exclude).order(1);
    }
}
