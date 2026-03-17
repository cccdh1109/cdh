CREATE TABLE IF NOT EXISTS file_import_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_code VARCHAR(64) NOT NULL,
  oss_bucket VARCHAR(128) NOT NULL,
  oss_object_key VARCHAR(512) NOT NULL,
  file_type VARCHAR(64) NOT NULL DEFAULT 'DELIMITED_TEXT',
  delimiter VARCHAR(8) NOT NULL DEFAULT '|',
  field_names VARCHAR(2000) NOT NULL,
  target_index VARCHAR(128) NOT NULL,
  batch_size INT DEFAULT 500,
  enabled TINYINT NOT NULL DEFAULT 1,
  gmt_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_config_code (config_code)
);

CREATE TABLE IF NOT EXISTS import_schedule_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_code VARCHAR(64) NOT NULL,
  cron_expression VARCHAR(64) NOT NULL,
  file_config_id BIGINT NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  gmt_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_task_code (task_code)
);

INSERT INTO file_import_config(config_code, oss_bucket, oss_object_key, file_type, delimiter, field_names, target_index, batch_size, enabled)
VALUES ('demo_user_import', 'your-bucket', 'path/demo-user.dat', 'DELIMITED_TEXT', '|', 'user_id,user_name,mobile,city', 'user_profile', 500, 1);

INSERT INTO import_schedule_config(task_code, cron_expression, file_config_id, enabled)
VALUES ('demo_user_import_task', '0 0/5 * * * ?', 1, 1);
