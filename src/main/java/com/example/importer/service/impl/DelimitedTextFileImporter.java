package com.example.importer.service.impl;

import com.example.importer.domain.FileImportConfig;
import com.example.importer.service.FileImporter;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class DelimitedTextFileImporter implements FileImporter {

    private static final Logger log = LoggerFactory.getLogger(DelimitedTextFileImporter.class);
    public static final String TYPE = "DELIMITED_TEXT";

    private final RestHighLevelClient client;

    public DelimitedTextFileImporter(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public String supportedType() {
        return TYPE;
    }

    @Override
    public void importFile(FileImportConfig config, InputStream stream) {
        String delimiter = config.getDelimiter();
        List<String> fields = Arrays.asList(config.getFieldNames().split(","));
        int batchSize = config.getBatchSize() == null ? 500 : config.getBatchSize();
        Pattern pattern = Pattern.compile(Pattern.quote(delimiter));

        long lineNo = 0;
        long success = 0;
        BulkRequest bulkRequest = new BulkRequest();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = pattern.split(line, -1);
                if (parts.length != fields.size()) {
                    log.warn("[IMPORT][SKIP] configCode={}, lineNo={}, reason=field-count-mismatch, expected={}, actual={}, line={}",
                            config.getConfigCode(), lineNo, fields.size(), parts.length, line);
                    continue;
                }

                Map<String, Object> doc = new LinkedHashMap<>();
                for (int i = 0; i < fields.size(); i++) {
                    doc.put(fields.get(i).trim(), parts[i]);
                }
                bulkRequest.add(new IndexRequest(config.getTargetIndex())
                        .source(doc, XContentType.JSON));

                if (bulkRequest.numberOfActions() >= batchSize) {
                    success += flush(config, bulkRequest);
                    bulkRequest = new BulkRequest();
                }
            }

            if (bulkRequest.numberOfActions() > 0) {
                success += flush(config, bulkRequest);
            }

            log.info("[IMPORT][DONE] configCode={}, index={}, totalLines={}, successDocs={}",
                    config.getConfigCode(), config.getTargetIndex(), lineNo, success);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to import file for configCode=" + config.getConfigCode(), ex);
        }
    }

    private long flush(FileImportConfig config, BulkRequest request) throws IOException {
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        if (response.hasFailures()) {
            log.error("[IMPORT][BULK] configCode={}, index={}, status=partial-failed, message={}",
                    config.getConfigCode(), config.getTargetIndex(), response.buildFailureMessage());
        } else {
            log.info("[IMPORT][BULK] configCode={}, index={}, status=success, batchCount={}",
                    config.getConfigCode(), config.getTargetIndex(), request.numberOfActions());
        }
        return request.numberOfActions();
    }
}
