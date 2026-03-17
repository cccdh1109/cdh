package com.example.importer.support;

import com.example.importer.service.FileImporter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FileImporterRegistry {

    private final Map<String, FileImporter> importerMap = new ConcurrentHashMap<>();

    public FileImporterRegistry(List<FileImporter> importers) {
        for (FileImporter importer : importers) {
            importerMap.put(importer.supportedType(), importer);
        }
    }

    public FileImporter getByType(String fileType) {
        return importerMap.get(fileType);
    }
}
