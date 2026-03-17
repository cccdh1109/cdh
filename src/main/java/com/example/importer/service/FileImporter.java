package com.example.importer.service;

import com.example.importer.domain.FileImportConfig;

import java.io.InputStream;

public interface FileImporter {

    String supportedType();

    void importFile(FileImportConfig config, InputStream stream);
}
