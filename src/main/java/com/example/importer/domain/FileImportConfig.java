package com.example.importer.domain;

import java.time.LocalDateTime;

public class FileImportConfig {
    private Long id;
    private String configCode;
    private String ossBucket;
    private String ossObjectKey;
    private String fileType;
    private String delimiter;
    private String fieldNames;
    private String targetIndex;
    private Integer batchSize;
    private Integer enabled;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConfigCode() { return configCode; }
    public void setConfigCode(String configCode) { this.configCode = configCode; }
    public String getOssBucket() { return ossBucket; }
    public void setOssBucket(String ossBucket) { this.ossBucket = ossBucket; }
    public String getOssObjectKey() { return ossObjectKey; }
    public void setOssObjectKey(String ossObjectKey) { this.ossObjectKey = ossObjectKey; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getDelimiter() { return delimiter; }
    public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
    public String getFieldNames() { return fieldNames; }
    public void setFieldNames(String fieldNames) { this.fieldNames = fieldNames; }
    public String getTargetIndex() { return targetIndex; }
    public void setTargetIndex(String targetIndex) { this.targetIndex = targetIndex; }
    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public LocalDateTime getGmtCreated() { return gmtCreated; }
    public void setGmtCreated(LocalDateTime gmtCreated) { this.gmtCreated = gmtCreated; }
    public LocalDateTime getGmtModified() { return gmtModified; }
    public void setGmtModified(LocalDateTime gmtModified) { this.gmtModified = gmtModified; }
}
