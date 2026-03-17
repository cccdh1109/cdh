package com.example.importer.service.impl;

import com.example.importer.domain.FileImportConfig;
import com.example.importer.mapper.FileImportConfigMapper;
import com.example.importer.service.FileImporter;
import com.example.importer.service.ImportOrchestrator;
import com.example.importer.service.OssFileService;
import com.example.importer.support.FileImporterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ImportOrchestratorImpl implements ImportOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ImportOrchestratorImpl.class);

    private final FileImportConfigMapper fileConfigMapper;
    private final OssFileService ossFileService;
    private final FileImporterRegistry fileImporterRegistry;

    public ImportOrchestratorImpl(FileImportConfigMapper fileConfigMapper,
                                  OssFileService ossFileService,
                                  FileImporterRegistry fileImporterRegistry) {
        this.fileConfigMapper = fileConfigMapper;
        this.ossFileService = ossFileService;
        this.fileImporterRegistry = fileImporterRegistry;
    }

    @Override
    public void executeImport(Long fileConfigId) {
        FileImportConfig config = fileConfigMapper.selectById(fileConfigId);
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1) {
            log.warn("[IMPORT][SKIP] fileConfigId={}, reason=config-disabled-or-not-found", fileConfigId);
            return;
        }
        validateFileType(config.getOssObjectKey());

        FileImporter importer = fileImporterRegistry.getByType(config.getFileType());
        if (importer == null) {
            throw new IllegalStateException("No importer found for fileType=" + config.getFileType());
        }

        log.info("[IMPORT][START] fileConfigId={}, configCode={}, bucket={}, objectKey={}, fileType={}, delimiter={}, targetIndex={}",
                config.getId(), config.getConfigCode(), config.getOssBucket(), config.getOssObjectKey(), config.getFileType(),
                config.getDelimiter(), config.getTargetIndex());

        try (InputStream stream = ossFileService.readFile(config.getOssBucket(), config.getOssObjectKey())) {
            importer.importFile(config, stream);
        } catch (Exception ex) {
            log.error("[IMPORT][ERROR] fileConfigId={}, configCode={}, message={}",
                    fileConfigId, config.getConfigCode(), ex.getMessage(), ex);
            throw new IllegalStateException("Import failed for fileConfigId=" + fileConfigId, ex);
        }
    }

    private void validateFileType(String objectKey) {
        if (!(objectKey.endsWith(".dat") || objectKey.endsWith(".txt"))) {
            throw new IllegalArgumentException("Only .dat or .txt files are supported: " + objectKey);
        }
    }
}
