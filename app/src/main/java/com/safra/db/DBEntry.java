package com.safra.db;

public class DBEntry {

//    Tables
    public static final String TABLE_TEMPLATE = "template_table";
    public static final String TABLE_FORM = "form_table";
    public static final String TABLE_RESPONSE = "response_table";
    public static final String TABLE_LANGUAGE = "language_table";
    public static final String TABLE_MODULE = "module_table";
    public static final String TABLE_PERMISSION = "permission_table";
    public static final String TABLE_USER = "user_table";
    public static final String TABLE_USER_GROUPS = "user_groups_table";
    public static final String TABLE_TASK = "task_table";
    public static final String TABLE_FORM_TYPE = "form_type_table";

//    Columns
    public static final String IS_SYNCED = "is_synced";
    public static final String IS_STATUS_SYNCED = "is_status_synced";
    public static final String IS_DELETE = "is_delete";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String MASTER_ID = "master_id";

//    Language Columns
    public static final String LANGUAGE_ID = "language_id";
    public static final String LANGUAGE_TITLE = "language_title";
    public static final String LANGUAGE_FILE = "language_file";
    public static final String LANGUAGE_SLUG = "language_slug";

//    Module Columns
    public static final String MODULE_ID = "module_id";
    public static final String MODULE_NAME = "module_name";
    public static final String PT_MODULE_NAME = "pt_module_name";

//    Permission Columns
    public static final String PERMISSION_ID = "permission_id";
    public static final String PERMISSION_NAME = "permission_name";
    public static final String PT_PERMISSION_NAME = "pt_permission_name";
    public static final String PERMISSION_MODULE = "permission_module";

//    Template Columns
    public static final String TEMPLATE_ID = "template_id";
    public static final String TEMPLATE_UNIQUE_ID = "template_unique_id";
    public static final String TEMPLATE_LANGUAGE_ID = "template_language_id";
    public static final String TEMPLATE_NAME = "template_name";
    public static final String TEMPLATE_JSON = "template_json";
    public static final String TEMPLATE_TYPE = "template_type";  // 1->Free, 2->Premium
    public static final String TEMPLATE_STATUS = "template_status"; // 1->Active, 0->Inactive
    public static final String TEMPLATE_IMAGE = "template_image";

//    Form Columns
    public static final String FORM_ID = "form_id";
    public static final String FORM_UNIQUE_ID = "form_unique_id";
    public static final String FORM_ONLINE_ID = "form_online_id";
    public static final String FORM_USER_ID = "form_user_id";
    public static final String FORM_NAME = "form_name";
    public static final String FORM_LANGUAGE_ID = "form_language_id";
    public static final String FORM_DESCRIPTION = "form_description";
    public static final String FORM_JSON = "form_json";
    public static final String FORM_EXPIRY_DATE = "form_expiry_date";
    public static final String FORM_ACCESS = "form_access"; //1->Private 2->Public
    public static final String FORM_STATUS = "form_status"; //0->Saved 1->Publish
    public static final String FORM_LINK = "form_link";
    public static final String FORM_USER_IDS = "form_user_ids";
    public static final String FORM_GROUP_IDS = "form_group_ids";
    public static final String FORM_GROUP_USER_IDS = "form_group_user_ids";
    public static final String FORM_TYPE = "form_type";
    public static final String TOTAL_MARKS = "total_marks";

//    Response Columns
    public static final String RESPONSE_ID = "response_id";
    public static final String RESPONSE_ONLINE_ID = "response_online_id";
    public static final String RESPONSE_FORM_ID = "response_form_id";
    public static final String RESPONSE_USER_ID = "response_user_id";
    public static final String RESPONSE_DATE = "response_date";
    public static final String RESPONSE_USER_NAME = "response_user_name";
    public static final String RESPONSE_USER_DATA = "response_user_data";
    public static final String RESPONSE_FILE_DATA = "response_file_data";

//    User Columns
    public static final String USER_ID = "user_id";
    public static final String USER_ONLINE_ID = "user_online_id";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_MOBILE_NO = "user_mobile_no";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_PROFILE = "user_profile";
    public static final String USER_MODULE_IDS = "user_module_ids";
    public static final String USER_PERMISSION_IDS = "user_permissions_ids";
    public static final String USER_PERMISSION = "user_permission";
    public static final String USER_ROLE_ID = "user_role_id";
    public static final String USER_STATUS = "user_status";
    public static final String IS_AGENCY = "is_agency";
    public static final String USER_ADDED_BY = "user_added_by";
    public static final String SIGNED_IN_USER = "signed_in_user";

//    Group Columns
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_ONLINE_ID = "group_online_id";
    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_MODULE_LIST = "group_module_list";
    public static final String GROUP_PERMISSION_LIST = "group_permission_list";
    public static final String GROUP_ADDED_BY = "group_added_by";

//    Task Columns
    public static final String TASK_ID = "task_id";
    public static final String TASK_ONLINE_ID = "task_online_id";
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_PRIORITY = "task_priority";
    public static final String TASK_DETAILS = "task_details";
    public static final String TASK_START_DATE = "task_start_date";
    public static final String TASK_END_DATE = "task_end_date";
    public static final String TASK_ADDED_BY = "task_added_by";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK_USER_IDS = "task_user_ids";
    public static final String TASK_GROUP_IDS = "task_group_ids";
    public static final String TASK_USER_STATUS = "task_user_status";
    public static final String TASK_ALL_USER_IDS = "task_all_user_ids";
    public static final String TASK_ADDED_BY_NAME = "task_added_by_name";

//    Form Type Columns
    public static final String FORM_TYPE_ID = "form_type_id";
    public static final String FORM_TYPE_NAME = "form_type_name";
    public static final String PT_FORM_TYPE_NAME = "pt_form_type_name";


    static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_ONLINE_ID + " INTEGER DEFAULT 0, "
            + USER_ROLE_ID + " INTEGER DEFAULT 0, " + USER_TOKEN + " TEXT, " + USER_NAME + " TEXT, "
            + USER_EMAIL + " TEXT, " + USER_MOBILE_NO + " TEXT, " + USER_PASSWORD + " TEXT, "
            + USER_PROFILE + " TEXT, " + USER_PERMISSION + " TEXT, " + USER_STATUS + " INTEGER, "
            + IS_SYNCED + " INTEGER DEFAULT 0, " + IS_DELETE + " INTEGER DEFAULT 0, "
            + IS_AGENCY + " INTEGER DEFAULT 0, " + USER_ADDED_BY + " INTEGER DEFAULT 0, "
            + USER_MODULE_IDS + " TEXT, " + USER_PERMISSION_IDS + " TEXT, " + IS_STATUS_SYNCED + " INTEGER DEFAULT 0, "
            + SIGNED_IN_USER + " INTEGER DEFAULT 0)";

    static final String CREATE_GROUP_TABLE = "CREATE TABLE " + TABLE_USER_GROUPS + "("
            + GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GROUP_ONLINE_ID + " INTEGER DEFAULT 0, "
            + GROUP_NAME + " TEXT, " + GROUP_MODULE_LIST + " TEXT, " + GROUP_PERMISSION_LIST + " TEXT, "
            + IS_SYNCED + " INTEGER DEFAULT 0, " + IS_DELETE + " INTEGER DEFAULT 0, "
            + GROUP_ADDED_BY + " INTEGER DEFAULT 0)";

    static final String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASK + "("
            + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_ONLINE_ID + " INTEGER DEFAULT 0, "
            + TASK_TITLE + " TEXT, " + TASK_DETAILS + " TEXT, " + TASK_PRIORITY + " TEXT, "
            + TASK_START_DATE + " INTEGER , " + TASK_END_DATE + " INTEGER, " + TASK_ADDED_BY + " INTEGER, "
            + TASK_STATUS + " TEXT, " + TASK_USER_IDS + " TEXT, " + TASK_GROUP_IDS + " TEXT, "
            + TASK_USER_STATUS + " TEXT, " + IS_SYNCED + " INTEGER DEFAULT 0, " + IS_DELETE + " INTEGER DEFAULT 0, "
            + TASK_ALL_USER_IDS + " TEXT, " + IS_STATUS_SYNCED + " INTEGER DEFAULT 0, " + TASK_ADDED_BY_NAME + " TEXT, "
            + MASTER_ID + " INTEGER DEFAULT 0)";

    static final String CREATE_TEMPLATE_TABLE = "CREATE TABLE " + TABLE_TEMPLATE + "("
            + TEMPLATE_ID + " INTEGER PRIMARY KEY, " + TEMPLATE_UNIQUE_ID + " TEXT, "
            + TEMPLATE_LANGUAGE_ID + " INTEGER, " + TEMPLATE_NAME + " TEXT, " + TEMPLATE_TYPE + " INTEGER, "
            + TEMPLATE_STATUS + " INTEGER, " + TEMPLATE_JSON + " TEXT, " + TEMPLATE_IMAGE + " TEXT, "
            + IS_DELETE + " INTEGER, " + CREATED_AT + " TEXT, " + UPDATED_AT + " TEXT)";

    static final String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_LANGUAGE + "("
            + LANGUAGE_ID + " INTEGER PRIMARY KEY, " + LANGUAGE_TITLE + " TEXT, "
            + LANGUAGE_SLUG + " TEXT, " + LANGUAGE_FILE + " TEXT)";

    static final String CREATE_MODULE_TABLE = "CREATE TABLE " + TABLE_MODULE + "("
            + MODULE_ID + " INTEGER PRIMARY KEY, " + MODULE_NAME + " TEXT, " + PT_MODULE_NAME + " TEXT)";

    static final String CREATE_PERMISSION_TABLE = "CREATE TABLE " + TABLE_PERMISSION + "("
            + PERMISSION_ID + " INTEGER PRIMARY KEY, " + PERMISSION_NAME + " TEXT, "
            + PERMISSION_MODULE + " INTEGER, " + PT_PERMISSION_NAME + " TEXT)";

    static final String CREATE_FORM_TABLE = "CREATE TABLE " + TABLE_FORM + "("
            + FORM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FORM_UNIQUE_ID + " TEXT, "
            + FORM_ONLINE_ID + " INTEGER DEFAULT 0, " + FORM_USER_ID + " INTEGER, " + FORM_LANGUAGE_ID + " INTEGER, "
            + FORM_NAME + " TEXT, " + FORM_DESCRIPTION + " TEXT, " + FORM_EXPIRY_DATE + " TEXT, "
            + FORM_STATUS + " INTEGER, " + FORM_ACCESS + " INTEGER, " + FORM_JSON + " TEXT, " + FORM_LINK + " TEXT, "
            + FORM_USER_IDS + " TEXT, " + FORM_GROUP_IDS + " TEXT, " + FORM_GROUP_USER_IDS + " TEXT, "
            + IS_SYNCED + " INTEGER DEFAULT 0, " + IS_DELETE + " INTEGER DEFAULT 0, " + CREATED_AT + " TEXT, "
            + UPDATED_AT + " TEXT, " + FORM_TYPE + " INTEGER DEFAULT 0, " + TOTAL_MARKS + " INTEGER DEFAULT 0)";

    static final String CREATE_RESPONSE_TABLE = "CREATE TABLE " + TABLE_RESPONSE + "("
            + RESPONSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RESPONSE_ONLINE_ID + " INTEGER DEFAULT 0, "
            + RESPONSE_FORM_ID + " INTEGER, " + RESPONSE_USER_ID + " INTEGER, " + RESPONSE_DATE + " TEXT, "
            + RESPONSE_USER_NAME + " TEXT, " + RESPONSE_USER_DATA + " TEXT, " + RESPONSE_FILE_DATA + " TEXT, "
            + IS_SYNCED + " INTEGER DEFAULT 0, " + IS_DELETE + " INTEGER DEFAULT 0)";

    static final String CREATE_FORM_TYPE_TABLE = "CREATE TABLE " + TABLE_FORM_TYPE + "("
            + FORM_TYPE_ID + " INTEGER PRIMARY KEY, " + FORM_TYPE_NAME + " TEXT, " + PT_FORM_TYPE_NAME + " TEXT)";

    static final String ALTER_USER_TABLE_ADD_AGENCY_COLUMN = "ALTER TABLE " + TABLE_USER + " ADD COLUMN "
            + IS_AGENCY + " INTEGER DEFAULT 0";

    static final String ALTER_USER_TABLE_ADD_ADDED_BY_COLUMN = "ALTER TABLE " + TABLE_USER + " ADD COLUMN "
            + USER_ADDED_BY + " INTEGER DEFAULT 0";

    static final String ALTER_GROUP_TABLE_ADD_ADDED_BY_COLUMN = "ALTER TABLE " + TABLE_USER_GROUPS + " ADD COLUMN "
            + GROUP_ADDED_BY + " INTEGER DEFAULT 0";

    static final String ALTER_USER_TABLE_ADD_MODULE_IDS_COLUMN = "ALTER TABLE " + TABLE_USER + " ADD COLUMN "
            + USER_MODULE_IDS + " TEXT";

    static final String ALTER_USER_TABLE_ADD_PERMISSION_IDS_COLUMN = "ALTER TABLE " + TABLE_USER + " ADD COLUMN "
            + USER_PERMISSION_IDS + " TEXT";

    static final String ALTER_USER_TABLE_ADD_STATUS_SYNCED_COLUMN = "ALTER TABLE " + TABLE_USER + " ADD COLUMN "
            + IS_STATUS_SYNCED + " INTEGER DEFAULT 0";

    static final String ALTER_USER_TABLE_ADD_SIGNED_IN_COLUMN = "ALTER TABLE " + TABLE_USER + " ADD COLUMN "
            + SIGNED_IN_USER + " INTEGER DEFAULT 0";

    static final String ALTER_MODULE_TABLE_ADD_PT_MODULE_NAME_COLUMN = "ALTER TABLE " + TABLE_MODULE + " ADD COLUMN "
            + PT_MODULE_NAME + " TEXT";

    static final String ALTER_PERMISSION_TABLE_ADD_PT_PERMISSION_NAME_COLUMN = "ALTER TABLE " + TABLE_PERMISSION
            + " ADD COLUMN " + PT_PERMISSION_NAME + " TEXT";

    static final String ALTER_FORM_TABLE_ADD_FORM_TYPE_COLUMN = "ALTER TABLE " + TABLE_FORM
            + " ADD COLUMN " + FORM_TYPE + " INTEGER DEFAULT 0";

    static final String ALTER_FORM_TABLE_ADD_TOTAL_MARKS_COLUMN = "ALTER TABLE " + TABLE_FORM
            + " ADD COLUMN " + TOTAL_MARKS + " INTEGER DEFAULT 0";

    static final String CHECK_IF_FORM_AVAILABLE = "SELECT " + FORM_ID + " FROM " + TABLE_FORM
            + " WHERE " + FORM_ONLINE_ID + "=?";

    static final String CHECK_IF_TEMPLATE_AVAILABLE = "SELECT " + TEMPLATE_UNIQUE_ID + " FROM " + TABLE_TEMPLATE
            + " WHERE " + TEMPLATE_ID + "=?";

    static final String CHECK_IF_RESPONSE_AVAILABLE = "SELECT " + RESPONSE_ID + " FROM " + TABLE_RESPONSE
            + " WHERE " + RESPONSE_ONLINE_ID + "=?";

    static final String CHECK_IF_USER_AVAILABLE = "SELECT " + USER_ID + " FROM " + TABLE_USER
            + " WHERE " + USER_ONLINE_ID + "=?";

    static final String CHECK_IF_GROUP_AVAILABLE = "SELECT " + GROUP_ID + " FROM " + TABLE_USER_GROUPS
            + " WHERE " + GROUP_ONLINE_ID + "=?";

    static final String CHECK_IF_TASK_AVAILABLE = "SELECT " + TASK_ID + " FROM " + TABLE_TASK
            + " WHERE " + TASK_ONLINE_ID + "=?";

    static final String SELECT_TEMPLATE_LIST = "SELECT * FROM " + TABLE_TEMPLATE + " WHERE " + IS_DELETE + "='0'";

    static final String GET_LANGUAGE_LIST = "SELECT * FROM " + TABLE_LANGUAGE;

    static final String GET_MODULE_LIST = "SELECT * FROM " + TABLE_MODULE;

    static final String GET_PERMISSION_LIST = "SELECT * FROM " + TABLE_PERMISSION + " WHERE " + PERMISSION_MODULE + "=?";

    static final String GET_FORM_TYPE_LIST = "SELECT * FROM " + TABLE_FORM_TYPE;

    static final String GET_TEMPLATE_LIST = "SELECT " + TEMPLATE_ID + ", " + TEMPLATE_UNIQUE_ID + ", "
            + TEMPLATE_LANGUAGE_ID + ", " + TEMPLATE_NAME + ", " + TEMPLATE_TYPE + ", " + TEMPLATE_JSON + ", "
            + TEMPLATE_IMAGE + " FROM " + TABLE_TEMPLATE;

    static final String GET_FORMS = "SELECT " + FORM_ID + ", " + FORM_ONLINE_ID + ", " + FORM_UNIQUE_ID + ", "
            + FORM_LANGUAGE_ID + ", " + FORM_USER_ID + ", " + FORM_NAME + ", " + FORM_DESCRIPTION + ", "
            + FORM_EXPIRY_DATE + ", " + FORM_JSON + ", " + FORM_ACCESS + ", " + FORM_STATUS + ", "
            + FORM_LINK + ", " + FORM_USER_IDS + ", " + FORM_GROUP_IDS + ", " + FORM_GROUP_USER_IDS + ", "
            + LANGUAGE_TITLE + ", " + FORM_TYPE + ", " + TOTAL_MARKS + " FROM " + TABLE_FORM
            + " LEFT JOIN " + TABLE_LANGUAGE + " ON "
            + TABLE_FORM + "." + FORM_LANGUAGE_ID + " = " + TABLE_LANGUAGE + "." + LANGUAGE_ID + " WHERE "
            + FORM_USER_ID + "=? AND " + IS_DELETE + "='0' ORDER BY " + FORM_LANGUAGE_ID + " ASC, "
            + FORM_ONLINE_ID + " ASC";

    static final String GET_FORM_IDS = "SELECT " + FORM_ONLINE_ID + " FROM " + TABLE_FORM
            + " WHERE " + IS_DELETE + "='0'";

    static final String GET_OFFLINE_EDITED_FORMS = "SELECT " + FORM_ID + ", " + FORM_ONLINE_ID + ", "
            + FORM_UNIQUE_ID + ", " + FORM_LANGUAGE_ID + ", " + FORM_USER_ID + ", " + FORM_NAME + ", "
            + FORM_DESCRIPTION + ", " + FORM_EXPIRY_DATE + ", " + FORM_JSON + ", " + FORM_ACCESS + ", "
            + FORM_STATUS + ", " + FORM_LINK + ", " + FORM_USER_IDS + ", " + FORM_GROUP_IDS + ", "
            + FORM_GROUP_USER_IDS + ", " + FORM_TYPE + ", " + TOTAL_MARKS + ", " + IS_DELETE + " FROM " + TABLE_FORM
            + " WHERE " + IS_SYNCED + "='0'";

    static final String GET_FORM = "SELECT " + FORM_ID + ", " + FORM_ONLINE_ID + ", " + FORM_UNIQUE_ID + ", "
            + FORM_LANGUAGE_ID + ", " + FORM_USER_ID + ", " + FORM_NAME + ", " + FORM_DESCRIPTION + ", "
            + FORM_EXPIRY_DATE + ", " + FORM_JSON + ", " + FORM_ACCESS + ", " + FORM_STATUS + ", "
            + FORM_LINK + ", " + FORM_USER_IDS + ", " + FORM_GROUP_IDS + ", " + FORM_GROUP_USER_IDS + ", "
            + FORM_TYPE + ", " + TOTAL_MARKS + ", " + IS_DELETE + " FROM " + TABLE_FORM + " WHERE " + FORM_ID + "=?";

    static final String GET_RESPONSES = "SELECT " + RESPONSE_ID + ", " + RESPONSE_ONLINE_ID + ", "
            + RESPONSE_FORM_ID + ", " + RESPONSE_USER_ID + ", " + RESPONSE_DATE + ", " + RESPONSE_USER_NAME + ", "
            + RESPONSE_USER_DATA + ", " + RESPONSE_FILE_DATA + " FROM " + TABLE_RESPONSE + " WHERE "
            + RESPONSE_FORM_ID + "=? AND " + IS_DELETE + "='0' ORDER BY "
            + RESPONSE_ONLINE_ID + " DESC";

    static final String GET_OFFLINE_ADDED_RESPONSE = "SELECT " + RESPONSE_ID + ", " + RESPONSE_ONLINE_ID + ", "
            + RESPONSE_FORM_ID + ", " + RESPONSE_USER_ID + ", " + RESPONSE_DATE + ", "
            + RESPONSE_USER_NAME + ", " + RESPONSE_USER_DATA + ", " + RESPONSE_FILE_DATA
            + " FROM " + TABLE_RESPONSE + " WHERE " + IS_SYNCED + "='0'";

    static final String GET_RESPONSE = "SELECT " + RESPONSE_ID + ", " + RESPONSE_ONLINE_ID + ", "
            + RESPONSE_FORM_ID + ", " + RESPONSE_USER_ID + ", " + RESPONSE_DATE + ", " + RESPONSE_USER_NAME + ", "
            + RESPONSE_USER_DATA + ", " + RESPONSE_FILE_DATA + ", " + IS_DELETE + " FROM "
            + TABLE_RESPONSE + " WHERE " + RESPONSE_ID + "=? OR " + RESPONSE_ONLINE_ID + "=?";

    static final String SIGN_IN_USER = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_EMAIL + "=? AND "
            + USER_PASSWORD + "=?";

    static final String GET_USERS_LIST = "SELECT u." + USER_ID + ", u." + USER_ONLINE_ID + ", u."
            + USER_ROLE_ID + ", u." + USER_NAME + ", u." + USER_EMAIL + ", u." + USER_MOBILE_NO + ", u."
            + USER_STATUS + ", u." + USER_ADDED_BY + ", g." + GROUP_NAME + " FROM " + TABLE_USER + " AS u "
            + " LEFT JOIN " + TABLE_USER_GROUPS + " AS g" + " ON u." + USER_ROLE_ID + "=g." + GROUP_ONLINE_ID
            + " WHERE u." + USER_ADDED_BY + "=? AND u."+ IS_DELETE + "='0' ORDER BY u." + USER_ONLINE_ID + " ASC";

    static final String GET_OFFLINE_EDITED_USERS = "SELECT " + USER_ID + ", " + USER_ONLINE_ID + ", "
            + USER_ROLE_ID + ", " + USER_NAME + ", " + USER_EMAIL + ", " + USER_MOBILE_NO + ", "
            + USER_PASSWORD + ", " + USER_STATUS + ", " + IS_SYNCED + ", " + USER_ADDED_BY + ", "
            + USER_MODULE_IDS + ", " + USER_PERMISSION_IDS + ", " + IS_STATUS_SYNCED + " FROM " + TABLE_USER
            + " WHERE " + SIGNED_IN_USER + "='0' AND (" + IS_SYNCED + "='0' OR " + IS_STATUS_SYNCED + "='0')";

    static final String GET_OFFLINE_STATUS_CHANGED_USERS = "SELECT " + USER_ID + ", " + USER_ONLINE_ID + ", "
            + USER_STATUS + " FROM " + TABLE_USER + " WHERE " + IS_STATUS_SYNCED + "='0'";

    static final String GET_USER = "SELECT u." + USER_ID + ", u." + USER_ONLINE_ID + ", u." + USER_ROLE_ID + ", u."
            + USER_NAME + ", u." + USER_EMAIL + ", u." + USER_MOBILE_NO + ", u." + USER_STATUS + ", u."
            + USER_ADDED_BY + ", g." + GROUP_NAME + ", u." + USER_MODULE_IDS + ", u." + USER_PERMISSION_IDS + ", u."
            + USER_PROFILE + " FROM " + TABLE_USER + " AS u " + "LEFT JOIN " + TABLE_USER_GROUPS + " AS g"
            + " ON u." + USER_ROLE_ID + "=g." + GROUP_ONLINE_ID + " WHERE u." + USER_ID + "=?";

    static final String GET_GROUPS_LIST = "SELECT " + GROUP_ID + ", " + GROUP_ONLINE_ID + ", " + GROUP_NAME + ", "
            + GROUP_MODULE_LIST + ", " + GROUP_PERMISSION_LIST + ", " + GROUP_ADDED_BY + " FROM " + TABLE_USER_GROUPS
            + " WHERE " + GROUP_ADDED_BY + "=? AND " + IS_DELETE + "='0' ORDER BY " + GROUP_ONLINE_ID + " ASC";

    static final String GET_OFFLINE_EDITED_GROUPS = "SELECT " + GROUP_ID + ", " + GROUP_ONLINE_ID + ", "
            + GROUP_NAME + ", " + GROUP_MODULE_LIST + ", " + GROUP_PERMISSION_LIST + ", " + GROUP_ADDED_BY
            + " FROM " + TABLE_USER_GROUPS + " WHERE " + IS_SYNCED + "='0'";

    static final String GET_GROUP = "SELECT " + GROUP_ID + ", " + GROUP_ONLINE_ID + ", " + GROUP_NAME + ", "
            + GROUP_MODULE_LIST + ", " + GROUP_PERMISSION_LIST + ", " + GROUP_ADDED_BY + " FROM " + TABLE_USER_GROUPS
            + " WHERE " + GROUP_ID + "=? OR " + GROUP_ONLINE_ID + "=?";

    static final String GET_TASKS = "SELECT " + TASK_ID + ", " + TASK_ONLINE_ID + ", " + TASK_TITLE + ", "
            + TASK_DETAILS + ", " + TASK_PRIORITY + ", " + TASK_START_DATE + ", " + TASK_END_DATE + ", "
            + TASK_ADDED_BY + ", " + TASK_STATUS + ", " + TASK_USER_IDS + ", " + TASK_GROUP_IDS + ", "
            + TASK_ALL_USER_IDS + ", " + TASK_ADDED_BY_NAME + " FROM " + TABLE_TASK + " WHERE ("
            + MASTER_ID + "=? OR " + TASK_ADDED_BY + "=? OR ',' || " + TASK_USER_IDS
            + " || ',' LIKE '%,' || ? || ',%' OR ',' || " + TASK_ALL_USER_IDS + " || ',' LIKE '%,' || ? || ',%' OR ',' || " + TASK_GROUP_IDS
            + " || ',' LIKE '%,' || ? || ',%') AND " + IS_DELETE + "='0' ORDER BY " + TASK_ONLINE_ID + " ASC";

    static final String GET_OFFLINE_EDITED_TASKS = "SELECT " + TASK_ID + ", " + TASK_ONLINE_ID + ", " + TASK_TITLE + ", "
            + TASK_DETAILS + ", " + TASK_PRIORITY + ", " + TASK_START_DATE + ", " + TASK_END_DATE + ", "
            + TASK_ADDED_BY + ", " + TASK_STATUS + ", " + TASK_USER_IDS + ", " + TASK_GROUP_IDS + ", "
            + TASK_ADDED_BY_NAME + ", " + IS_SYNCED + ", " + IS_STATUS_SYNCED + " FROM " + TABLE_TASK
            + " WHERE " + IS_SYNCED + "='0' OR " + IS_STATUS_SYNCED + "='0'";

    static final String GET_TASK = "SELECT " + TASK_ID + ", " + TASK_ONLINE_ID + ", " + TASK_TITLE + ", "
            + TASK_DETAILS + ", " + TASK_PRIORITY + ", " + TASK_START_DATE + ", " + TASK_END_DATE + ", "
            + TASK_ADDED_BY + ", " + TASK_STATUS + ", " + TASK_USER_IDS + ", " + TASK_GROUP_IDS + ", "
            + TASK_USER_STATUS + ", " + TASK_ADDED_BY_NAME + " FROM " + TABLE_TASK + " WHERE " + TASK_ID + "=?";

    static final String GET_GROUPS_COUNT = "SELECT COUNT(" + GROUP_ID + ") FROM " + TABLE_USER_GROUPS
            + " WHERE " + GROUP_ADDED_BY + "=?";

    static final String GET_USERS_COUNT = "SELECT COUNT(" + USER_ID + ") FROM " + TABLE_USER
            + " WHERE " + USER_ADDED_BY + "=?";

    static final String GET_FORMS_COUNT = "SELECT COUNT(" + FORM_ID + ") FROM " + TABLE_FORM
            + " WHERE " + FORM_USER_ID + "=?";

    static final String GET_TASKS_COUNT = "SELECT COUNT(" + TASK_ID + ") FROM " + TABLE_TASK
            + " WHERE " + MASTER_ID + "=?";
}
