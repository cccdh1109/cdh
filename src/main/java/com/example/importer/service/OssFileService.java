package com.example.importer.service;

import java.io.InputStream;

public interface OssFileService {

    InputStream readFile(String bucket, String objectKey);
}
