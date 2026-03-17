package com.example.importer.controller;

import com.example.importer.service.ImportOrchestrator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/import")
public class ImportTaskController {

    private final ImportOrchestrator importOrchestrator;

    public ImportTaskController(ImportOrchestrator importOrchestrator) {
        this.importOrchestrator = importOrchestrator;
    }

    @PostMapping("/{fileConfigId}")
    public String execute(@PathVariable Long fileConfigId) {
        importOrchestrator.executeImport(fileConfigId);
        return "OK";
    }
}
