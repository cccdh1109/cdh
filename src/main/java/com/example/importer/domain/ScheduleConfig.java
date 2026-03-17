package com.example.importer.domain;

import java.time.LocalDateTime;

public class ScheduleConfig {
    private Long id;
    private String taskCode;
    private String cronExpression;
    private Long fileConfigId;
    private Integer enabled;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskCode() { return taskCode; }
    public void setTaskCode(String taskCode) { this.taskCode = taskCode; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public Long getFileConfigId() { return fileConfigId; }
    public void setFileConfigId(Long fileConfigId) { this.fileConfigId = fileConfigId; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public LocalDateTime getGmtCreated() { return gmtCreated; }
    public void setGmtCreated(LocalDateTime gmtCreated) { this.gmtCreated = gmtCreated; }
    public LocalDateTime getGmtModified() { return gmtModified; }
    public void setGmtModified(LocalDateTime gmtModified) { this.gmtModified = gmtModified; }
}
