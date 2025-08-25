package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.BaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private BaseEnumConverterFactory baseEnumConverterFactory;
    @Value("${web.admin.include}")
    private String includes[];
    @Value("${web.admin.exclude}")
    private String excludes[];

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(baseEnumConverterFactory);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(includes).excludePathPatterns(excludes).order(1);
    }
}
