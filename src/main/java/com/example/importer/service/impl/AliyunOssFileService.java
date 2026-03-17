package com.example.importer.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.example.importer.service.OssFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.InputStream;

@Service
public class AliyunOssFileService implements OssFileService {

    private final OSS ossClient;

    public AliyunOssFileService(@Value("${oss.endpoint}") String endpoint,
                                @Value("${oss.access-key-id}") String accessKeyId,
                                @Value("${oss.access-key-secret}") String accessKeySecret) {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public InputStream readFile(String bucket, String objectKey) {
        OSSObject object = ossClient.getObject(bucket, objectKey);
        return object.getObjectContent();
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
