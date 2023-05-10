CREATE TABLE task_table(task_id INTEGER PRIMARY KEY AUTOINCREMENT, task_online_id INTEGER DEFAULT 0, task_title TEXT, task_details TEXT, task_priority TEXT, task_start_date INTEGER , task_end_date INTEGER, task_added_by INTEGER, task_status TEXT, task_user_ids TEXT, task_group_ids TEXT, task_user_status TEXT, is_synced INTEGER DEFAULT 0, is_delete INTEGER DEFAULT 0, task_all_user_ids TEXT, is_status_synced INTEGER DEFAULT 0, task_added_by_name TEXT, master_id INTEGER DEFAULT 0);
ALTER TABLE user_table ADD COLUMN is_agency INTEGER DEFAULT 0;
ALTER TABLE user_table ADD COLUMN user_added_by INTEGER DEFAULT 0;
ALTER TABLE user_table ADD COLUMN user_module_ids TEXT;
ALTER TABLE user_table ADD COLUMN user_permissions_ids TEXT;
ALTER TABLE user_table ADD COLUMN is_status_synced INTEGER DEFAULT 0;
ALTER TABLE user_table ADD COLUMN signed_in_user INTEGER DEFAULT 0;
ALTER TABLE user_groups_table ADD COLUMN group_added_by INTEGER DEFAULT 0;
