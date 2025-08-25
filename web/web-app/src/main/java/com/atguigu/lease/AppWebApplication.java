package com.atguigu.lease;

import com.atguigu.lease.common.minio.MinioConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = MinioConfiguration.class))
@SpringBootApplication
public class AppWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppWebApplication.class, args);
    }
}
