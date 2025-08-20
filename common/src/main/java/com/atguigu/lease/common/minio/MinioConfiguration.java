package com.atguigu.lease.common.minio;

import com.atguigu.lease.common.properties.MinioProperties;
import io.minio.MinioClient;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =MinioClient.builder()
                .endpoint(minioProperties.getPoint())
                .credentials(minioProperties.getUsername(), minioProperties.getPassword())
                .build();
        return minioClient;
    }
}
