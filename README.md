# OSS 文件导入 Elasticsearch（SOFABoot）

该项目满足以下要求：
- 框架：SOFABoot + Spring Boot 2.3.12
- Elasticsearch：7.10
- 主配置：`application.properties`
- 支持 OSS 上 `.dat` / `.txt` 文件导入
- 文件字段、分隔符、索引均可配置（DB 配置）
- 导入日志清晰（START/BULK/DONE/ERROR）
- 定时任务：动态 Cron + MyBatis + 数据库配置
- 后续新增文件导入：通过新增 DB 配置 + 扩展 `FileImporter` 实现

## 配置与运行
1. 修改 `src/main/resources/application.properties` 中数据库、OSS、ES 配置。
2. 在 MySQL 执行 `src/main/resources/schema.sql`。
3. 启动应用：
   ```bash
   mvn spring-boot:run
   ```

## 数据库配置说明
### 1) file_import_config（文件导入配置）
- `oss_bucket`：OSS bucket
- `oss_object_key`：OSS 对象路径（仅允许 `.dat` 或 `.txt`）
- `file_type`：导入类型，默认 `DELIMITED_TEXT`
- `delimiter`：分隔符，例如 `|`
- `field_names`：字段列表，用英文逗号分隔
- `target_index`：ES 索引名
- `batch_size`：批量写 ES 的条数

### 2) import_schedule_config（动态任务配置）
- `task_code`：任务编码
- `cron_expression`：cron 表达式
- `file_config_id`：关联 `file_import_config.id`
- `enabled`：是否启用

## 导入流程
1. `DynamicScheduleManager` 周期刷新 DB 中任务并动态注册/更新 Cron。
2. 触发任务后调用 `ImportOrchestrator`。
3. 从 OSS 读取文件流，按 `file_type` 找到对应 `FileImporter`。
4. `DelimitedTextFileImporter` 按配置的分隔符解析文本并批量写入 ES。

## 后续扩展（新增文件导入）
### 场景 A：同样是分隔文本（dat/txt）
只需在 `file_import_config` 新增一条记录，配置不同的路径、字段、索引、分隔符。

### 场景 B：新增文件格式（例如 CSV、JSONL）
1. 新增 `FileImporter` 实现并声明 `supportedType()`。
2. 在 `file_import_config.file_type` 使用新类型值。
3. 无需改动调度主流程。

## 手工触发接口
```http
POST /api/import/{fileConfigId}
```
用于临时手工执行指定配置导入。
