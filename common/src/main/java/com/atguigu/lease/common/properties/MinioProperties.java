package com.atguigu.lease.common.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="com.minio")
public class MinioProperties {

    private String point;
    private String username;
    private String password;
    private String bucket;
}
