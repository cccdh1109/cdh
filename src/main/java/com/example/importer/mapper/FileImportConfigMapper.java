package com.example.importer.mapper;

import com.example.importer.domain.FileImportConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileImportConfigMapper {

    FileImportConfig selectById(@Param("id") Long id);

    List<FileImportConfig> selectEnabledConfigs();
}
