package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.properties.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;


    @Override
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());
        if(!flag){
            minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(minioProperties.getBucket())
                    .build());
            String policy="""
            {
                "Statement" : [ {
                "Action" : "s3:GetObject",
                        "Effect" : "Allow",
                        "Principal" : "*",
                        "Resource" : "arn:aws:s3:::%s/*"
            } ],
                "Version" : "2012-10-17"
            } """;
            policy=policy.formatted(minioProperties.getBucket());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().config(policy).build());
        }
        String path=new SimpleDateFormat("yyyyMMdd").format(new Date());
        String objectname=path+"/"+UUID.randomUUID().toString().replace("-","")+file.getOriginalFilename();

        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectname)
                        .stream(file.getInputStream(),file.getSize(),-1)
                        .contentType(file.getContentType())
                .build());
        String url=String.join("/",minioProperties.getPoint(),minioProperties.getBucket(),objectname);
        return url;
    }
}
