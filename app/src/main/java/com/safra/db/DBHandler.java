package com.safra.db;

import static com.safra.utilities.Common.TYPE_FORM;
import static com.safra.utilities.Common.TYPE_GROUP;
import static com.safra.utilities.Common.TYPE_RESPONSE;
import static com.safra.utilities.Common.TYPE_TASK;
import static com.safra.utilities.Common.TYPE_TEMPLATE;
import static com.safra.utilities.Common.TYPE_USER;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.safra.Safra;
import com.safra.extensions.FormExtension;
import com.safra.extensions.GeneralExtension;
import com.safra.models.AccessItem;
import com.safra.models.FormItem;
import com.safra.models.FormTypeItem;
import com.safra.models.LanguageItem;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.ResponseItem;
import com.safra.models.RoleItem;
import com.safra.models.TaskItem;
import com.safra.models.TemplateItem;
import com.safra.models.UserItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    public static final String TAG = "db_handler";

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "safra.db";

    public static DBHandler dbHandler = null;

    public static synchronized DBHandler getInstance() {
        if (dbHandler == null)
            dbHandler = new DBHandler(Safra.getInstance().getApplicationContext());

        return dbHandler;
    }

    private DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBEntry.CREATE_LANGUAGE_TABLE);
        db.execSQL(DBEntry.CREATE_TEMPLATE_TABLE);
        db.execSQL(DBEntry.CREATE_FORM_TABLE);
        db.execSQL(DBEntry.CREATE_MODULE_TABLE);
        db.execSQL(DBEntry.CREATE_PERMISSION_TABLE);
        db.execSQL(DBEntry.CREATE_RESPONSE_TABLE);
        db.execSQL(DBEntry.CREATE_USER_TABLE);
        db.execSQL(DBEntry.CREATE_GROUP_TABLE);
        db.execSQL(DBEntry.CREATE_TASK_TABLE);
        db.execSQL(DBEntry.CREATE_FORM_TYPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationName = String.format(Locale.US, "from_%d_to_%d.sql", i, (i + 1));
            Log.d(TAG, "Looking for migration file: " + migrationName);
            readAndExecuteSQLScript(db, Safra.getInstance().getApplicationContext(), migrationName);
        }

//        switch (oldVersion) {
//            case 1:
//                db.execSQL(DBEntry.CREATE_TEMPLATE_TABLE);
////                db.execSQL(DBEntry.CREATE_FORM_TABLE);
////                db.execSQL(DBEntry.CREATE_MODULE_TABLE);
////                db.execSQL(DBEntry.CREATE_PERMISSION_TABLE);
////                db.execSQL(DBEntry.CREATE_RESPONSE_TABLE);
////                db.execSQL(DBEntry.CREATE_USER_TABLE);
////                db.execSQL(DBEntry.CREATE_GROUP_TABLE);
////                db.execSQL(DBEntry.CREATE_TASK_TABLE);
//                break;
//            case 2:
//                db.execSQL(DBEntry.CREATE_FORM_TABLE);
////                db.execSQL(DBEntry.CREATE_MODULE_TABLE);
////                db.execSQL(DBEntry.CREATE_PERMISSION_TABLE);
////                db.execSQL(DBEntry.CREATE_RESPONSE_TABLE);
////                db.execSQL(DBEntry.CREATE_USER_TABLE);
////                db.execSQL(DBEntry.CREATE_GROUP_TABLE);
////                db.execSQL(DBEntry.CREATE_TASK_TABLE);
//                break;
//            case 3:
//                db.execSQL(DBEntry.CREATE_MODULE_TABLE);
//                db.execSQL(DBEntry.CREATE_PERMISSION_TABLE);
////                db.execSQL(DBEntry.CREATE_RESPONSE_TABLE);
////                db.execSQL(DBEntry.CREATE_USER_TABLE);
////                db.execSQL(DBEntry.CREATE_GROUP_TABLE);
////                db.execSQL(DBEntry.CREATE_TASK_TABLE);
//                break;
//            case 4:
//                db.execSQL(DBEntry.CREATE_RESPONSE_TABLE);
////                db.execSQL(DBEntry.CREATE_USER_TABLE);
////                db.execSQL(DBEntry.CREATE_GROUP_TABLE);
////                db.execSQL(DBEntry.CREATE_TASK_TABLE);
////                db.execSQL(DBEntry.ALTER_MODULE_TABLE_ADD_PT_MODULE_NAME_COLUMN);
////                db.execSQL(DBEntry.ALTER_PERMISSION_TABLE_ADD_PT_PERMISSION_NAME_COLUMN);
//                break;
//            case 5:
//                db.execSQL(DBEntry.CREATE_USER_TABLE);
//                db.execSQL(DBEntry.CREATE_GROUP_TABLE);
////                db.execSQL(DBEntry.CREATE_TASK_TABLE);
////                db.execSQL(DBEntry.ALTER_MODULE_TABLE_ADD_PT_MODULE_NAME_COLUMN);
////                db.execSQL(DBEntry.ALTER_PERMISSION_TABLE_ADD_PT_PERMISSION_NAME_COLUMN);
//                break;
//            case 6:
//                db.execSQL(DBEntry.CREATE_TASK_TABLE);
//                db.execSQL(DBEntry.ALTER_USER_TABLE_ADD_AGENCY_COLUMN);
//                db.execSQL(DBEntry.ALTER_USER_TABLE_ADD_ADDED_BY_COLUMN);
//                db.execSQL(DBEntry.ALTER_GROUP_TABLE_ADD_ADDED_BY_COLUMN);
//                db.execSQL(DBEntry.ALTER_USER_TABLE_ADD_MODULE_IDS_COLUMN);
//                db.execSQL(DBEntry.ALTER_USER_TABLE_ADD_PERMISSION_IDS_COLUMN);
//                db.execSQL(DBEntry.ALTER_USER_TABLE_ADD_STATUS_SYNCED_COLUMN);
//                db.execSQL(DBEntry.ALTER_USER_TABLE_ADD_SIGNED_IN_COLUMN);
////                db.execSQL(DBEntry.ALTER_MODULE_TABLE_ADD_PT_MODULE_NAME_COLUMN);
////                db.execSQL(DBEntry.ALTER_PERMISSION_TABLE_ADD_PT_PERMISSION_NAME_COLUMN);
//                break;
//            case 7:
//                db.execSQL(DBEntry.ALTER_MODULE_TABLE_ADD_PT_MODULE_NAME_COLUMN);
//                db.execSQL(DBEntry.ALTER_PERMISSION_TABLE_ADD_PT_PERMISSION_NAME_COLUMN);
//                break;
//            case 8:
//                db.execSQL(DBEntry.ALTER_FORM_TABLE_ADD_FORM_TYPE_COLUMN);
//                db.execSQL(DBEntry.ALTER_FORM_TABLE_ADD_TOTAL_MARKS_COLUMN);
//                break;
//        }
    }

    public boolean checkIfAvailable(SQLiteDatabase db, int type, long onlineId) {
        String s;
        switch (type) {
            case TYPE_USER:
                s = DBEntry.CHECK_IF_USER_AVAILABLE;
                break;
            case TYPE_GROUP:
                s = DBEntry.CHECK_IF_GROUP_AVAILABLE;
                break;
            case TYPE_TASK:
                s = DBEntry.CHECK_IF_TASK_AVAILABLE;
                break;
            case TYPE_FORM:
                s = DBEntry.CHECK_IF_FORM_AVAILABLE;
                break;
            case TYPE_RESPONSE:
                s = DBEntry.CHECK_IF_RESPONSE_AVAILABLE;
                break;
            case TYPE_TEMPLATE:
                s = DBEntry.CHECK_IF_TEMPLATE_AVAILABLE;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        Cursor cursor = db.rawQuery(s, new String[]{String.valueOf(onlineId)});
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    public void addLanguages(List<LanguageItem> languageList) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        clearLanguageTable(db);

        for (LanguageItem li : languageList) {
            cv.put(DBEntry.LANGUAGE_ID, li.getLanguageId());
            cv.put(DBEntry.LANGUAGE_TITLE, li.getLanguageName());
            cv.put(DBEntry.LANGUAGE_SLUG, li.getLanguageSlug());
            cv.put(DBEntry.LANGUAGE_FILE, li.getLangFileUrl());

            db.replace(DBEntry.TABLE_LANGUAGE, null, cv);
        }

        db.close();
    }

    public void addFormTypes(List<FormTypeItem> formTypeList){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        clearFormTypeTable(db);
        for(FormTypeItem fi: formTypeList){
            cv.put(DBEntry.FORM_TYPE_ID, fi.getTypeId());
            cv.put(DBEntry.FORM_TYPE_NAME, fi.getTypeName());
            cv.put(DBEntry.PT_FORM_TYPE_NAME, fi.getPtTypeName());

            db.replace(DBEntry.TABLE_FORM_TYPE, null, cv);
        }

        db.close();
    }

    public void addModules(List<ModuleItem> moduleList) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        clearModuleTable(db);
        clearPermissionTable(db);

        for (ModuleItem mi : moduleList) {
            cv.put(DBEntry.MODULE_ID, mi.getModuleId());
            cv.put(DBEntry.MODULE_NAME, mi.getModuleName());
            cv.put(DBEntry.PT_MODULE_NAME, mi.getPtModuleName());
            addPermissions(db, mi.getModuleId(), mi.getPermissionList());

            db.replace(DBEntry.TABLE_MODULE, null, cv);
        }

        db.close();
    }

    public void addPermissions(SQLiteDatabase db, long moduleId, List<PermissionItem> permissionList) {
        ContentValues cv = new ContentValues();

        for (PermissionItem pi : permissionList) {
            cv.put(DBEntry.PERMISSION_ID, pi.getPermissionId());
            cv.put(DBEntry.PERMISSION_NAME, pi.getPermissionName());
            cv.put(DBEntry.PT_PERMISSION_NAME, pi.getPtPermissionName());
            cv.put(DBEntry.PERMISSION_MODULE, moduleId);

            db.replace(DBEntry.TABLE_PERMISSION, null, cv);
        }
    }

    public int addUsers(List<UserItem> userList) {
        SQLiteDatabase db = getWritableDatabase();

        int i = 0;
        for (UserItem ui : userList) {
            ContentValues cv = new ContentValues();
            cv.put(DBEntry.USER_ONLINE_ID, ui.getUserOnlineId());
            cv.put(DBEntry.USER_NAME, ui.getUserName());
            cv.put(DBEntry.USER_EMAIL, ui.getUserEmail());
            cv.put(DBEntry.USER_MOBILE_NO, ui.getUserPhone());
            if (ui.getUserToken() != null)
                cv.put(DBEntry.USER_TOKEN, ui.getUserToken());
            if (ui.getUserPassword() != null)
                cv.put(DBEntry.USER_PASSWORD, ui.getUserPassword());
            else
                cv.putNull(DBEntry.USER_PASSWORD);
            if (ui.getUserProfile() != null)
                cv.put(DBEntry.USER_PROFILE, ui.getUserProfile());
            if (ui.getUserAccessJson() != null)
                cv.put(DBEntry.USER_PERMISSION, ui.getUserAccessJson());
            if (ui.getModuleIds() != null)
                cv.put(DBEntry.USER_MODULE_IDS, GeneralExtension.toString(ui.getModuleIds()));
            if (ui.getPermissionIds() != null)
                cv.put(DBEntry.USER_PERMISSION_IDS, GeneralExtension.toString(ui.getPermissionIds()));
            if (ui.getRoleId() > 0)
                cv.put(DBEntry.USER_ROLE_ID, ui.getRoleId());
            if (ui.getUserStatus() > 0)
                cv.put(DBEntry.USER_STATUS, ui.getUserStatus());
            if (ui.getUserAddedBy() > 0)
                cv.put(DBEntry.USER_ADDED_BY, ui.getUserAddedBy());
            if (ui.isAgency())
                cv.put(DBEntry.IS_AGENCY, ui.isAgency() ? 1 : 0);
            cv.put(DBEntry.IS_SYNCED, 1);
            cv.put(DBEntry.IS_STATUS_SYNCED, 1);

            if (checkIfAvailable(db, TYPE_USER, ui.getUserOnlineId())) {
                db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ONLINE_ID + "=?",
                        new String[]{String.valueOf(ui.getUserOnlineId())});
            } else {
                db.insert(DBEntry.TABLE_USER, null, cv);
            }
            i++;
        }
        db.close();

        return i;
    }

    public long addUser(UserItem userItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.USER_ONLINE_ID, userItem.getUserOnlineId());
        cv.put(DBEntry.USER_NAME, userItem.getUserName());
        cv.put(DBEntry.USER_EMAIL, userItem.getUserEmail());
        cv.put(DBEntry.USER_MOBILE_NO, userItem.getUserPhone());
        if (userItem.getUserToken() != null)
            cv.put(DBEntry.USER_TOKEN, userItem.getUserToken());
        if (userItem.getUserPassword() != null)
            cv.put(DBEntry.USER_PASSWORD, userItem.getUserPassword());
        else
            cv.putNull(DBEntry.USER_PASSWORD);
        if (userItem.getUserProfile() != null)
            cv.put(DBEntry.USER_PROFILE, userItem.getUserProfile());
        if (userItem.getUserAccessJson() != null)
            cv.put(DBEntry.USER_PERMISSION, userItem.getUserAccessJson());
        if (userItem.getModuleIds() != null)
            cv.put(DBEntry.USER_MODULE_IDS, GeneralExtension.toString(userItem.getModuleIds()));
        if (userItem.getPermissionIds() != null)
            cv.put(DBEntry.USER_PERMISSION_IDS, GeneralExtension.toString(userItem.getPermissionIds()));
        if (userItem.getRoleId() > 0)
            cv.put(DBEntry.USER_ROLE_ID, userItem.getRoleId());
        if (userItem.getUserStatus() > 0)
            cv.put(DBEntry.USER_STATUS, userItem.getUserStatus());
        if (userItem.getUserAddedBy() > 0)
            cv.put(DBEntry.USER_ADDED_BY, userItem.getUserAddedBy());
        if (userItem.isAgency())
            cv.put(DBEntry.IS_AGENCY, userItem.isAgency() ? 1 : 0);
        cv.put(DBEntry.IS_SYNCED, 1);
        cv.put(DBEntry.IS_STATUS_SYNCED, 1);

        long i;
        if (checkIfAvailable(db, TYPE_USER, userItem.getUserOnlineId())) {
            i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ONLINE_ID + "=?",
                    new String[]{String.valueOf(userItem.getUserOnlineId())});
        } else {
            i = db.insert(DBEntry.TABLE_USER, null, cv);
        }

        return i;
    }

    public void addUserViaSignIn(UserItem userItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.USER_ONLINE_ID, userItem.getUserOnlineId());
        cv.put(DBEntry.USER_NAME, userItem.getUserName());
        cv.put(DBEntry.USER_EMAIL, userItem.getUserEmail());
        cv.put(DBEntry.USER_MOBILE_NO, userItem.getUserPhone());
        if (userItem.getUserToken() != null)
            cv.put(DBEntry.USER_TOKEN, userItem.getUserToken());
        if (userItem.getUserPassword() != null)
            cv.put(DBEntry.USER_PASSWORD, userItem.getUserPassword());
        else
            cv.putNull(DBEntry.USER_PASSWORD);
        if (userItem.getUserProfile() != null)
            cv.put(DBEntry.USER_PROFILE, userItem.getUserProfile());
        if (userItem.getUserAccessJson() != null)
            cv.put(DBEntry.USER_PERMISSION, userItem.getUserAccessJson());
        if (userItem.getModuleIds() != null)
            cv.put(DBEntry.USER_MODULE_IDS, GeneralExtension.toString(userItem.getModuleIds()));
        if (userItem.getPermissionIds() != null)
            cv.put(DBEntry.USER_PERMISSION_IDS, GeneralExtension.toString(userItem.getPermissionIds()));
        cv.put(DBEntry.IS_AGENCY, userItem.isAgency() ? 1 : 0);
        cv.put(DBEntry.SIGNED_IN_USER, 1);
        cv.put(DBEntry.IS_SYNCED, 1);
        cv.put(DBEntry.IS_STATUS_SYNCED, 1);

        if (checkIfAvailable(db, TYPE_USER, userItem.getUserOnlineId())) {
            db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ONLINE_ID + "=?",
                    new String[]{String.valueOf(userItem.getUserOnlineId())});
        } else {
            db.insert(DBEntry.TABLE_USER, null, cv);
        }

    }

//    public int addTemplates(List<TemplateItem> templateList) {
//        SQLiteDatabase db = getWritableDatabase();
//
//        int i = 0;
//        for (TemplateItem templateItem : templateList) {
//            ContentValues cv = new ContentValues();
//            cv.put(DBEntry.TEMPLATE_ID, templateItem.getTemplateId());
//            cv.put(DBEntry.TEMPLATE_UNIQUE_ID, templateItem.getTemplateUniqueId());
//            cv.put(DBEntry.TEMPLATE_LANGUAGE_ID, templateItem.getTemplateLanguageId());
//            cv.put(DBEntry.TEMPLATE_NAME, templateItem.getTemplateName());
//            cv.put(DBEntry.TEMPLATE_TYPE, templateItem.getTemplateType());
////        cv.put(DBEntry.TEMPLATE_STATUS, templateItem.getTemplateStatus());
//            cv.put(DBEntry.TEMPLATE_JSON, templateItem.getTemplateJson());
//            cv.put(DBEntry.TEMPLATE_IMAGE, templateItem.getTemplateImage());
//            cv.put(DBEntry.IS_DELETE, 0);
////        cv.put(DBEntry.CREATED_AT, templateItem.getCreatedAt());
////        cv.put(DBEntry.UPDATED_AT, templateItem.getUpdatedAt());
//            if (checkIfAvailable(db, TYPE_TEMPLATE, templateItem.getTemplateId())) {
//                db.update(DBEntry.TABLE_TEMPLATE, cv, DBEntry.TEMPLATE_ID + "=?",
//                        new String[]{String.valueOf(templateItem.getTemplateId())});
//            } else {
//                db.insert(DBEntry.TABLE_TEMPLATE, null, cv);
//            }
//            i++;
//        }
//        db.close();
//
//        return i;
//    }
//
//    public void addTemplate(TemplateItem templateItem) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//
//        cv.put(DBEntry.TEMPLATE_ID, templateItem.getTemplateId());
//        cv.put(DBEntry.TEMPLATE_UNIQUE_ID, templateItem.getTemplateUniqueId());
//        cv.put(DBEntry.TEMPLATE_LANGUAGE_ID, templateItem.getTemplateLanguageId());
//        cv.put(DBEntry.TEMPLATE_NAME, templateItem.getTemplateName());
//        cv.put(DBEntry.TEMPLATE_TYPE, templateItem.getTemplateType());
////        cv.put(DBEntry.TEMPLATE_STATUS, templateItem.getTemplateStatus());
//        cv.put(DBEntry.TEMPLATE_JSON, templateItem.getTemplateJson());
//        cv.put(DBEntry.TEMPLATE_IMAGE, templateItem.getTemplateImage());
//        cv.put(DBEntry.IS_DELETE, 0);
////        cv.put(DBEntry.CREATED_AT, templateItem.getCreatedAt());
////        cv.put(DBEntry.UPDATED_AT, templateItem.getUpdatedAt());
//        if (checkIfAvailable(db, TYPE_TEMPLATE, templateItem.getTemplateId())) {
//            db.update(DBEntry.TABLE_TEMPLATE, cv, DBEntry.TEMPLATE_ID + "=?",
//                    new String[]{String.valueOf(templateItem.getTemplateId())});
//        } else {
//            db.insert(DBEntry.TABLE_TEMPLATE, null, cv);
//        }
//
//        db.close();
//
//    }

    public int addForms(List<FormItem> formList) {
        SQLiteDatabase db = getWritableDatabase();

        int i = 0;
        for (FormItem formItem : formList) {
            ContentValues cv = new ContentValues();
            cv.put(DBEntry.FORM_ONLINE_ID, formItem.getFormOnlineId());
            cv.put(DBEntry.FORM_UNIQUE_ID, formItem.getFormUniqueId());
            cv.put(DBEntry.FORM_USER_ID, formItem.getFormUserId());
            cv.put(DBEntry.FORM_LANGUAGE_ID, formItem.getFormLanguageId());
            cv.put(DBEntry.FORM_NAME, formItem.getFormName());
            cv.put(DBEntry.FORM_DESCRIPTION, formItem.getFormDescription());
            cv.put(DBEntry.FORM_EXPIRY_DATE, formItem.getFormExpiryDate());
            cv.put(DBEntry.FORM_STATUS, formItem.getFormStatus());
            cv.put(DBEntry.FORM_ACCESS, formItem.getFormAccess());
            cv.put(DBEntry.FORM_TYPE, formItem.getFormType());
            cv.put(DBEntry.TOTAL_MARKS, formItem.getTotalMarks());
            cv.put(DBEntry.FORM_JSON, formItem.getFormJson());
            cv.put(DBEntry.FORM_LINK, formItem.getFormLink());
            cv.put(DBEntry.IS_SYNCED, 1);
            cv.put(DBEntry.IS_DELETE, formItem.isDelete() ? 1 : 0);
            cv.put(DBEntry.FORM_USER_IDS, GeneralExtension.toString(formItem.getUserIds()));
            cv.put(DBEntry.FORM_GROUP_IDS, GeneralExtension.toString(formItem.getGroupIds()));
            cv.put(DBEntry.FORM_GROUP_USER_IDS, GeneralExtension.toString(formItem.getGroupUserIds()));

            if (checkIfAvailable(db, TYPE_FORM, formItem.getFormOnlineId())) {
                db.update(DBEntry.TABLE_FORM, cv, DBEntry.FORM_ONLINE_ID + "=?",
                        new String[]{String.valueOf(formItem.getFormOnlineId())});
            } else {
                db.insert(DBEntry.TABLE_FORM, null, cv);
            }
            i++;
        }
        db.close();

        return i;
    }

    public long addForm(FormItem formItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.FORM_ONLINE_ID, formItem.getFormOnlineId());
        cv.put(DBEntry.FORM_UNIQUE_ID, formItem.getFormUniqueId());
        cv.put(DBEntry.FORM_USER_ID, formItem.getFormUserId());
        cv.put(DBEntry.FORM_LANGUAGE_ID, formItem.getFormLanguageId());
        cv.put(DBEntry.FORM_NAME, formItem.getFormName());
        cv.put(DBEntry.FORM_DESCRIPTION, formItem.getFormDescription());
        cv.put(DBEntry.FORM_EXPIRY_DATE, formItem.getFormExpiryDate());
        cv.put(DBEntry.FORM_STATUS, formItem.getFormStatus());
        cv.put(DBEntry.FORM_ACCESS, formItem.getFormAccess());
        cv.put(DBEntry.FORM_TYPE, formItem.getFormType());
        cv.put(DBEntry.TOTAL_MARKS, formItem.getTotalMarks());
        cv.put(DBEntry.FORM_JSON, formItem.getFormJson());
        cv.put(DBEntry.FORM_LINK, formItem.getFormLink());
        cv.put(DBEntry.IS_SYNCED, 1);
        cv.put(DBEntry.IS_DELETE, formItem.isDelete() ? 1 : 0);
        cv.put(DBEntry.FORM_USER_IDS, GeneralExtension.toString(formItem.getUserIds()));
        cv.put(DBEntry.FORM_GROUP_IDS, GeneralExtension.toString(formItem.getGroupIds()));
        cv.put(DBEntry.FORM_GROUP_USER_IDS, GeneralExtension.toString(formItem.getGroupUserIds()));

        long i;
        if (checkIfAvailable(db, TYPE_FORM, formItem.getFormOnlineId())) {
            i = db.update(DBEntry.TABLE_FORM, cv, DBEntry.FORM_ONLINE_ID + "=?",
                    new String[]{String.valueOf(formItem.getFormOnlineId())});
        } else {
            i = db.insert(DBEntry.TABLE_FORM, null, cv);
        }
        db.close();

        return i;
    }

    public long addResponses(List<ResponseItem> responseList) {
        SQLiteDatabase db = getWritableDatabase();

        int i = 0;
        for (ResponseItem responseItem : responseList) {
            ContentValues cv = new ContentValues();

//        cv.put(DBEntry.FORM_UNIQUE_ID, responseItem.getFormUniqueId());
            cv.put(DBEntry.RESPONSE_ONLINE_ID, responseItem.getOnlineId());
            cv.put(DBEntry.RESPONSE_USER_ID, responseItem.getUserId());
            cv.put(DBEntry.RESPONSE_FORM_ID, responseItem.getFormId());
            cv.put(DBEntry.RESPONSE_DATE, responseItem.getSubmitDate());
            cv.put(DBEntry.RESPONSE_USER_NAME, responseItem.getUserName());
            cv.put(DBEntry.RESPONSE_USER_DATA, responseItem.getResponseData());
            if (responseItem.getResponseFiles() != null) {
                try {
                    cv.put(DBEntry.RESPONSE_FILE_DATA, FormExtension
                            .convertFileListToArray(responseItem.getResponseFiles()).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cv.put(DBEntry.IS_SYNCED, 1);
            cv.put(DBEntry.IS_DELETE, responseItem.isDelete() ? 1 : 0);

            if (checkIfAvailable(db, TYPE_RESPONSE, responseItem.getOnlineId())) {
                db.update(DBEntry.TABLE_RESPONSE, cv, DBEntry.RESPONSE_ONLINE_ID + "=?",
                        new String[]{String.valueOf(responseItem.getOnlineId())});
            } else {
                db.insert(DBEntry.TABLE_RESPONSE, null, cv);
            }
            i++;
        }
        db.close();

        return i;
    }

    public long addResponse(ResponseItem responseItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(DBEntry.FORM_UNIQUE_ID, responseItem.getFormUniqueId());
        cv.put(DBEntry.RESPONSE_ONLINE_ID, responseItem.getOnlineId());
        cv.put(DBEntry.RESPONSE_USER_ID, responseItem.getUserId());
        cv.put(DBEntry.RESPONSE_FORM_ID, responseItem.getFormId());
        cv.put(DBEntry.RESPONSE_DATE, responseItem.getSubmitDate());
        cv.put(DBEntry.RESPONSE_USER_NAME, responseItem.getUserName());
        cv.put(DBEntry.RESPONSE_USER_DATA, responseItem.getResponseData());
        if (responseItem.getResponseFiles() != null) {
            try {
                cv.put(DBEntry.RESPONSE_FILE_DATA, FormExtension
                        .convertFileListToArray(responseItem.getResponseFiles()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cv.put(DBEntry.IS_SYNCED, 1);
        cv.put(DBEntry.IS_DELETE, responseItem.isDelete() ? 1 : 0);

        long i;
        if (checkIfAvailable(db, TYPE_RESPONSE, responseItem.getOnlineId())) {
            i = db.update(DBEntry.TABLE_RESPONSE, cv, DBEntry.RESPONSE_ONLINE_ID + "=?",
                    new String[]{String.valueOf(responseItem.getOnlineId())});
        } else {
            i = db.insert(DBEntry.TABLE_RESPONSE, null, cv);
        }
        db.close();

        return i;
    }

    public long addUserOffline(UserItem userItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.USER_NAME, userItem.getUserName());
        cv.put(DBEntry.USER_EMAIL, userItem.getUserEmail());
        cv.put(DBEntry.USER_MOBILE_NO, userItem.getUserPhone());
        cv.put(DBEntry.USER_ADDED_BY, userItem.getUserAddedBy());
        if (userItem.getUserPassword() != null)
            cv.put(DBEntry.USER_PASSWORD, userItem.getUserPassword());
        else
            cv.putNull(DBEntry.USER_PASSWORD);
        if (userItem.getUserAccessJson() != null)
            cv.put(DBEntry.USER_PERMISSION, userItem.getUserAccessJson());
        if (userItem.getModuleIds() != null)
            cv.put(DBEntry.USER_MODULE_IDS, GeneralExtension.toString(userItem.getModuleIds()));
        if (userItem.getPermissionIds() != null)
            cv.put(DBEntry.USER_PERMISSION_IDS, GeneralExtension.toString(userItem.getPermissionIds()));
        if (userItem.getRoleId() > 0)
            cv.put(DBEntry.USER_ROLE_ID, userItem.getRoleId());
        cv.put(DBEntry.IS_SYNCED, 0);

        long i = db.insert(DBEntry.TABLE_USER, null, cv);
        db.close();

        return i;
    }

    public long addFormOffline(FormItem formItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(DBEntry.FORM_UNIQUE_ID, formItem.getFormUniqueId());
//        cv.put(DBEntry.FORM_ONLINE_ID, formItem.getFormOnlineId());
        cv.put(DBEntry.FORM_USER_ID, formItem.getFormUserId());
        cv.put(DBEntry.FORM_LANGUAGE_ID, formItem.getFormLanguageId());
        cv.put(DBEntry.FORM_NAME, formItem.getFormName());
        cv.put(DBEntry.FORM_DESCRIPTION, formItem.getFormDescription());
        cv.put(DBEntry.FORM_EXPIRY_DATE, formItem.getFormExpiryDate());
        cv.put(DBEntry.FORM_STATUS, formItem.getFormStatus());
        cv.put(DBEntry.FORM_ACCESS, formItem.getFormAccess());
        cv.put(DBEntry.FORM_JSON, formItem.getFormJson());
        cv.put(DBEntry.FORM_TYPE, formItem.getFormType());
        cv.put(DBEntry.TOTAL_MARKS, formItem.getTotalMarks());
//        cv.put(DBEntry.FORM_LINK, formItem.getFormLink());
        cv.put(DBEntry.IS_SYNCED, 0);
        cv.put(DBEntry.IS_DELETE, formItem.isDelete() ? 1 : 0);
        if (formItem.getUserIds() != null)
            cv.put(DBEntry.FORM_USER_IDS, GeneralExtension.toString(formItem.getUserIds()));
        if (formItem.getGroupIds() != null)
            cv.put(DBEntry.FORM_GROUP_IDS, GeneralExtension.toString(formItem.getGroupIds()));
//        cv.put(DBEntry.FORM_GROUP_USER_IDS, Arrays.toString(formItem.getGroupUserIds()));
//        cv.put(DBEntry.CREATED_AT, formItem.getCreatedAt());
//        cv.put(DBEntry.UPDATED_AT, formItem.getUpdatedAt());

        long i = db.insert(DBEntry.TABLE_FORM, null, cv);
        db.close();

        return i;
    }

    public long addResponseOffline(ResponseItem responseItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.RESPONSE_USER_ID, responseItem.getUserId());
        cv.put(DBEntry.RESPONSE_FORM_ID, responseItem.getFormId());
        cv.put(DBEntry.RESPONSE_USER_NAME, responseItem.getUserName());
        cv.put(DBEntry.RESPONSE_DATE, responseItem.getSubmitDate());
        cv.put(DBEntry.RESPONSE_USER_DATA, responseItem.getResponseData());
        if (responseItem.getResponseFiles() != null) {
            try {
                cv.put(DBEntry.RESPONSE_FILE_DATA, FormExtension
                        .convertFileListToArray(responseItem.getResponseFiles()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cv.put(DBEntry.IS_SYNCED, 0);

        long i = db.insert(DBEntry.TABLE_RESPONSE, null, cv);
        db.close();

        return i;
    }

    public int addGroups(List<RoleItem> roleList) {
        SQLiteDatabase db = getWritableDatabase();

        int i = 0;
        for (RoleItem roleItem : roleList) {
            ContentValues cv = new ContentValues();
            cv.put(DBEntry.GROUP_ONLINE_ID, roleItem.getRoleOnlineId());
            cv.put(DBEntry.GROUP_NAME, roleItem.getRoleName());
            cv.put(DBEntry.GROUP_MODULE_LIST, GeneralExtension.toString(roleItem.getModuleIds()));
            cv.put(DBEntry.GROUP_PERMISSION_LIST, GeneralExtension.toString(roleItem.getPermissionIds()));
            cv.put(DBEntry.GROUP_ADDED_BY, roleItem.getAddedBy());
            cv.put(DBEntry.IS_SYNCED, 1);

            if (checkIfAvailable(db, TYPE_GROUP, roleItem.getRoleOnlineId())) {
                db.update(DBEntry.TABLE_USER_GROUPS, cv, DBEntry.GROUP_ONLINE_ID + "=?",
                        new String[]{String.valueOf(roleItem.getRoleOnlineId())});
            } else {
                db.insert(DBEntry.TABLE_USER_GROUPS, null, cv);
            }
            i++;
        }
        db.close();

        return i;
    }

    public long addGroup(RoleItem roleItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBEntry.GROUP_ONLINE_ID, roleItem.getRoleOnlineId());
        cv.put(DBEntry.GROUP_NAME, roleItem.getRoleName());
        cv.put(DBEntry.GROUP_MODULE_LIST, GeneralExtension.toString(roleItem.getModuleIds()));
        cv.put(DBEntry.GROUP_PERMISSION_LIST, GeneralExtension.toString(roleItem.getPermissionIds()));
        cv.put(DBEntry.GROUP_ADDED_BY, roleItem.getAddedBy());
        cv.put(DBEntry.IS_SYNCED, 1);

        long i;
        if (checkIfAvailable(db, TYPE_GROUP, roleItem.getRoleOnlineId())) {
            i = db.update(DBEntry.TABLE_USER_GROUPS, cv, DBEntry.GROUP_ONLINE_ID + "=?",
                    new String[]{String.valueOf(roleItem.getRoleOnlineId())});
        } else {
            i = db.insert(DBEntry.TABLE_USER_GROUPS, null, cv);
        }

        return i;
    }

    public long addGroupOffline(RoleItem roleItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.GROUP_NAME, roleItem.getRoleName());
        cv.put(DBEntry.GROUP_MODULE_LIST, GeneralExtension.toString(roleItem.getModuleIds()));
        cv.put(DBEntry.GROUP_PERMISSION_LIST, GeneralExtension.toString(roleItem.getPermissionIds()));
        cv.put(DBEntry.GROUP_ADDED_BY, roleItem.getAddedBy());
        cv.put(DBEntry.IS_SYNCED, 0);
        cv.put(DBEntry.IS_DELETE, roleItem.isDelete() ? 1 : 0);

        long i = db.insert(DBEntry.TABLE_USER_GROUPS, null, cv);
        db.close();

        return i;
    }

    public long addTasks(List<TaskItem> taskList) {
        SQLiteDatabase db = getWritableDatabase();

        int i = 0;
        for (TaskItem taskItem : taskList) {
            ContentValues cv = new ContentValues();
            cv.put(DBEntry.TASK_ONLINE_ID, taskItem.getTaskOnlineId());
            cv.put(DBEntry.TASK_TITLE, taskItem.getTaskName());
            cv.put(DBEntry.TASK_DETAILS, taskItem.getTaskDetail());
            cv.put(DBEntry.TASK_PRIORITY, taskItem.getPriority());
            cv.put(DBEntry.TASK_START_DATE, taskItem.getStartDate());
            cv.put(DBEntry.TASK_END_DATE, taskItem.getEndDate());
            cv.put(DBEntry.TASK_STATUS, taskItem.getTaskStatus());
            cv.put(DBEntry.MASTER_ID, taskItem.getMasterId());
            cv.put(DBEntry.TASK_ADDED_BY, taskItem.getAddedBy());
            cv.put(DBEntry.TASK_ADDED_BY_NAME, taskItem.getAddedByName());
            if (taskItem.getUserIds() != null)
                cv.put(DBEntry.TASK_USER_IDS, GeneralExtension.toString(taskItem.getUserIds()));
            if (taskItem.getGroupIds() != null)
                cv.put(DBEntry.TASK_GROUP_IDS, GeneralExtension.toString(taskItem.getGroupIds()));
            if (taskItem.getTaskUserStatus() != null)
                cv.put(DBEntry.TASK_USER_STATUS, taskItem.getTaskUserStatus());
            if (taskItem.getAllUserIds() != null)
                cv.put(DBEntry.TASK_ALL_USER_IDS, GeneralExtension.toString(taskItem.getAllUserIds()));
            cv.put(DBEntry.IS_SYNCED, 1);
            cv.put(DBEntry.IS_STATUS_SYNCED, 1);
            cv.put(DBEntry.IS_DELETE, taskItem.isDelete() ? 1 : 0);


            if (checkIfAvailable(db, TYPE_TASK, taskItem.getTaskOnlineId())) {
                db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ONLINE_ID + "=?",
                        new String[]{String.valueOf(taskItem.getTaskOnlineId())});
            } else {
                db.insert(DBEntry.TABLE_TASK, null, cv);
            }
            i++;
        }
        db.close();

        return i;
    }

    public long addTask(TaskItem taskItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.TASK_ONLINE_ID, taskItem.getTaskOnlineId());
        cv.put(DBEntry.TASK_TITLE, taskItem.getTaskName());
        cv.put(DBEntry.TASK_DETAILS, taskItem.getTaskDetail());
        cv.put(DBEntry.TASK_PRIORITY, taskItem.getPriority());
        cv.put(DBEntry.TASK_START_DATE, taskItem.getStartDate());
        cv.put(DBEntry.TASK_END_DATE, taskItem.getEndDate());
        cv.put(DBEntry.TASK_STATUS, taskItem.getTaskStatus());
        cv.put(DBEntry.MASTER_ID, taskItem.getMasterId());
        cv.put(DBEntry.TASK_ADDED_BY, taskItem.getAddedBy());
        cv.put(DBEntry.TASK_ADDED_BY_NAME, taskItem.getAddedByName());
        if (taskItem.getUserIds() != null)
            cv.put(DBEntry.TASK_USER_IDS, GeneralExtension.toString(taskItem.getUserIds()));
        if (taskItem.getGroupIds() != null)
            cv.put(DBEntry.TASK_GROUP_IDS, GeneralExtension.toString(taskItem.getGroupIds()));
        if (taskItem.getTaskUserStatus() != null)
            cv.put(DBEntry.TASK_USER_STATUS, taskItem.getTaskUserStatus());
        if (taskItem.getAllUserIds() != null)
            cv.put(DBEntry.TASK_ALL_USER_IDS, GeneralExtension.toString(taskItem.getAllUserIds()));
        cv.put(DBEntry.IS_SYNCED, 1);
        cv.put(DBEntry.IS_STATUS_SYNCED, 1);
        cv.put(DBEntry.IS_DELETE, taskItem.isDelete() ? 1 : 0);

        long i;
        if (checkIfAvailable(db, TYPE_TASK, taskItem.getTaskOnlineId())) {
            i = db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ONLINE_ID + "=?",
                    new String[]{String.valueOf(taskItem.getTaskOnlineId())});
        } else {
            i = db.insert(DBEntry.TABLE_TASK, null, cv);
        }
        db.close();

        return i;
    }

    public long addTaskOffline(TaskItem taskItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.TASK_TITLE, taskItem.getTaskName());
        cv.put(DBEntry.TASK_DETAILS, taskItem.getTaskDetail());
        cv.put(DBEntry.TASK_PRIORITY, taskItem.getPriority());
        cv.put(DBEntry.TASK_START_DATE, taskItem.getStartDate());
        cv.put(DBEntry.TASK_END_DATE, taskItem.getEndDate());
        cv.put(DBEntry.MASTER_ID, taskItem.getMasterId());
        cv.put(DBEntry.TASK_ADDED_BY, taskItem.getAddedBy());
        cv.put(DBEntry.TASK_ADDED_BY_NAME, taskItem.getAddedByName());
        if (taskItem.getTaskStatus() != null)
            cv.put(DBEntry.TASK_STATUS, taskItem.getTaskStatus());
        if (taskItem.getUserIds() != null)
            cv.put(DBEntry.TASK_USER_IDS, GeneralExtension.toString(taskItem.getUserIds()));
        if (taskItem.getGroupIds() != null)
            cv.put(DBEntry.TASK_GROUP_IDS, GeneralExtension.toString(taskItem.getGroupIds()));
        if (taskItem.getTaskUserStatus() != null)
            cv.put(DBEntry.TASK_USER_STATUS, taskItem.getTaskUserStatus());
        if (taskItem.getAllUserIds() != null)
            cv.put(DBEntry.TASK_ALL_USER_IDS, GeneralExtension.toString(taskItem.getAllUserIds()));
        cv.put(DBEntry.IS_SYNCED, 0);
        cv.put(DBEntry.IS_DELETE, taskItem.isDelete() ? 1 : 0);

        long i = db.insert(DBEntry.TABLE_TASK, null, cv);
        db.close();

        return i;
    }

    public UserItem signInUser(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.SIGN_IN_USER, new String[]{email, password});
        if (cursor != null && cursor.moveToFirst()) {
            UserItem userItem = new UserItem();
            userItem.setUserId(cursor.getLong(0));
            userItem.setUserOnlineId(cursor.getLong(1));
            userItem.setUserToken(cursor.getString(3) != null ? cursor.getString(3) : "");
            userItem.setUserName(cursor.getString(4) != null ? cursor.getString(4) : "");
            userItem.setUserEmail(cursor.getString(5) != null ? cursor.getString(5) : "");
            userItem.setUserPhone(cursor.getString(6) != null ? cursor.getString(6) : "");
            userItem.setUserProfile(cursor.getString(8) != null ? cursor.getString(8) : "");
            userItem.setUserAccessJson(cursor.getString(9) != null ? cursor.getString(9) : "[]");

            cursor.close();

            updateUserSignedIn(db, userItem.getUserOnlineId());

            db.close();
            return userItem;
        }
        db.close();
        return null;
    }

    public List<LanguageItem> getLanguages() {
        List<LanguageItem> languages = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_LANGUAGE_LIST, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                LanguageItem languageItem = new LanguageItem();
                languageItem.setLanguageId(cursor.getLong(0));
                languageItem.setLanguageName(cursor.getString(1));
                languageItem.setLanguageSlug(cursor.getString(2));
                languageItem.setLangFileUrl(cursor.getString(3));

                languages.add(languageItem);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();

        return languages;
    }

    public List<FormTypeItem> getFormTypeList(){
        List<FormTypeItem> formTypes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_FORM_TYPE_LIST, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                FormTypeItem formTypeItem = new FormTypeItem();
                formTypeItem.setTypeId(cursor.getLong(0));
                formTypeItem.setTypeName(cursor.getString(1));
                formTypeItem.setPtTypeName(cursor.getString(2));

                formTypes.add(formTypeItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return formTypes;
    }

    public List<ModuleItem> getModules() {
        List<ModuleItem> modules = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_MODULE_LIST, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ModuleItem moduleItem = new ModuleItem();
                moduleItem.setModuleId(cursor.getLong(0));
                moduleItem.setModuleName(cursor.getString(1));
                moduleItem.setPtModuleName(cursor.getString(2));
                moduleItem.setPermissionList(getPermissions(db, cursor.getLong(0)));

                modules.add(moduleItem);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();

        return modules;
    }

    public List<ModuleItem> getModulesOnly() {
        List<ModuleItem> modules = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_MODULE_LIST, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ModuleItem moduleItem = new ModuleItem();
                moduleItem.setModuleId(cursor.getLong(0));
                moduleItem.setModuleName(cursor.getString(1));
//                moduleItem.setPermissionList(getPermissions(db, cursor.getLong(0)));

                modules.add(moduleItem);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();

        return modules;
    }

    public List<PermissionItem> getPermissions(SQLiteDatabase db, long moduleId) {
        List<PermissionItem> permissions = new ArrayList<>();
        Cursor cursor = db.rawQuery(DBEntry.GET_PERMISSION_LIST, new String[]{String.valueOf(moduleId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PermissionItem permissionItem = new PermissionItem();
                permissionItem.setPermissionId(cursor.getLong(0));
                permissionItem.setPermissionName(cursor.getString(1));
                permissionItem.setPtPermissionName(cursor.getString(3));

                permissions.add(permissionItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return permissions;
    }

//    public List<TemplateItem> getTemplates() {
//        List<TemplateItem> templates = new ArrayList<>();
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(DBEntry.GET_TEMPLATE_LIST, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                TemplateItem templateItem = new TemplateItem();
//                templateItem.setTemplateId(cursor.getLong(0));
//                templateItem.setTemplateUniqueId(cursor.getString(1));
//                templateItem.setTemplateLanguageId(cursor.getLong(2));
//                templateItem.setTemplateName(cursor.getString(3));
//                templateItem.setTemplateType(cursor.getInt(4));
//                templateItem.setTemplateJson(cursor.getString(5));
//                templateItem.setTemplateImage(cursor.getString(6));
//
//                templates.add(templateItem);
//            } while (cursor.moveToNext());
//
//            cursor.close();
//        }
//        db.close();
//
//        return templates;
//    }

    public List<UserItem> getUnsyncedUsers() {
        List<UserItem> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_OFFLINE_EDITED_USERS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                UserItem userItem = new UserItem();
                userItem.setUserId(cursor.getLong(0));
                userItem.setUserOnlineId(cursor.getLong(1));
                userItem.setRoleId(cursor.getLong(2));
                userItem.setUserName(cursor.getString(3));
                userItem.setUserEmail(cursor.getString(4));
                userItem.setUserPhone(cursor.getString(5));
                userItem.setUserPassword(cursor.getString(6));
                userItem.setUserStatus(cursor.getInt(7));
                userItem.setSynced(cursor.getInt(8) == 1);
                userItem.setUserAddedBy(cursor.getLong(9));
                userItem.setModuleIds(GeneralExtension.toLongArray(cursor.getString(10), ","));
                userItem.setPermissionIds(GeneralExtension.toLongArray(cursor.getString(11), ","));
                userItem.setStatusSynced(cursor.getInt(12) == 1);

                users.add(userItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return users;
    }

    public List<RoleItem> getUnsyncedGroups() {
        List<RoleItem> groups = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_OFFLINE_EDITED_GROUPS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                RoleItem roleItem = new RoleItem();
                roleItem.setRoleId(cursor.getLong(0));
                roleItem.setRoleOnlineId(cursor.getLong(1));
                roleItem.setRoleName(cursor.getString(2));
                roleItem.setModuleIds(GeneralExtension
                        .toLongArray(cursor.getString(3), ","));
                roleItem.setPermissionIds(GeneralExtension
                        .toLongArray(cursor.getString(4), ","));
                roleItem.setAddedBy(cursor.getLong(5));

                groups.add(roleItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return groups;
    }

    public List<TaskItem> getUnsyncedTasks() {
        List<TaskItem> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_OFFLINE_EDITED_TASKS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaskItem taskItem = new TaskItem();
                taskItem.setTaskId(cursor.getLong(0));
                taskItem.setTaskOnlineId(cursor.getLong(1));
                taskItem.setTaskName(cursor.getString(2));
                taskItem.setTaskDetail(cursor.getString(3));
                taskItem.setPriority(cursor.getInt(4));
                taskItem.setStartDate(cursor.getLong(5));
                taskItem.setEndDate(cursor.getLong(6));
                taskItem.setAddedBy(cursor.getLong(7));
                taskItem.setTaskStatus(cursor.getString(8));
                taskItem.setUserIds(GeneralExtension
                        .toLongArray(cursor.getString(9), ","));
                taskItem.setGroupIds(GeneralExtension
                        .toLongArray(cursor.getString(10), ","));
                taskItem.setAddedByName(cursor.getString(11));
                taskItem.setSynced(cursor.getInt(12) == 1);
                taskItem.setStatusSynced(cursor.getInt(13) == 1);

                tasks.add(taskItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return tasks;
    }

    public List<FormItem> getUnsyncedForms() {
        List<FormItem> forms = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_OFFLINE_EDITED_FORMS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                FormItem formItem = new FormItem();
                formItem.setFormId(cursor.getLong(0));
                formItem.setFormOnlineId(cursor.getLong(1));
                formItem.setFormUniqueId(cursor.getString(2));
                formItem.setFormLanguageId(cursor.getLong(3));
                formItem.setFormUserId(cursor.getLong(4));
                formItem.setFormName(cursor.getString(5));
                formItem.setFormDescription(cursor.getString(6));
                formItem.setFormExpiryDate(cursor.getString(7));
                formItem.setFormJson(cursor.getString(8));
                formItem.setFormAccess(cursor.getLong(9));
                formItem.setFormStatus(cursor.getInt(10));
                formItem.setFormLink(cursor.getString(11));
                formItem.setUserIds(GeneralExtension.toLongArray(cursor.getString(12), ","));
                formItem.setGroupIds(GeneralExtension.toLongArray(cursor.getString(13), ","));
                formItem.setGroupUserIds(GeneralExtension.toLongArray(cursor.getString(14), ","));
                formItem.setFormType(cursor.getInt(15));
                formItem.setTotalMarks(cursor.getInt(16));
                formItem.setDelete(cursor.getInt(17) == 1);

                forms.add(formItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return forms;
    }

    public List<ResponseItem> getUnsyncedResponses() {
        List<ResponseItem> responses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_OFFLINE_ADDED_RESPONSE, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ResponseItem responseItem = new ResponseItem();
                responseItem.setResponseId(cursor.getLong(0));
                responseItem.setOnlineId(cursor.getLong(1));
                responseItem.setFormId(cursor.getLong(2));
                responseItem.setUserId(cursor.getLong(3));
                responseItem.setSubmitDate(cursor.getString(4));
                responseItem.setUserName(cursor.getString(5));
                responseItem.setResponseData(cursor.getString(6));
                Log.e(TAG, "getResponse: " + cursor.getString(7));
                if (cursor.getString(7) != null) {
                    try {
                        responseItem.setResponseFiles(FormExtension
                                .convertFileDataToList(new JSONArray(cursor.getString(7))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                responses.add(responseItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return responses;
    }

    public List<UserItem> getUsers(long userId) {
        List<UserItem> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_USERS_LIST, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                UserItem userItem = new UserItem();
                userItem.setUserId(cursor.getLong(0));
                userItem.setUserOnlineId(cursor.getLong(1));
                userItem.setRoleId(cursor.getLong(2));
                userItem.setUserName(cursor.getString(3));
                userItem.setUserEmail(cursor.getString(4));
                userItem.setUserPhone(cursor.getString(5));
                userItem.setUserStatus(cursor.getInt(6));
                userItem.setUserAddedBy(cursor.getLong(7));
                userItem.setRoleName(cursor.getString(8));

                users.add(userItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        Log.e(TAG, "getUsers: " + users.size());

        return users;
    }

    public List<RoleItem> getGroups(long userId) {
        List<RoleItem> groups = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_GROUPS_LIST, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                RoleItem roleItem = new RoleItem();
                roleItem.setRoleId(cursor.getLong(0));
                roleItem.setRoleOnlineId(cursor.getLong(1));
                roleItem.setRoleName(cursor.getString(2));
                roleItem.setModuleIds(GeneralExtension
                        .toLongArray(cursor.getString(3), ","));
                roleItem.setPermissionIds(GeneralExtension
                        .toLongArray(cursor.getString(4), ","));
                roleItem.setAddedBy(cursor.getLong(5));

                groups.add(roleItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return groups;
    }

    public List<AccessItem> getAccessList(long userId) {
        List<AccessItem> groups = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_GROUPS_LIST, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                AccessItem accessItem = new AccessItem();
                accessItem.setAccessId(cursor.getLong(1));
                accessItem.setAccessName(cursor.getString(2));

                groups.add(accessItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return groups;
    }

    public List<TaskItem> getTasks(long userId, long roleId) {
        List<TaskItem> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_TASKS, new String[]{String.valueOf(userId),
                String.valueOf(userId), String.valueOf(userId), String.valueOf(roleId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaskItem taskItem = new TaskItem();
                taskItem.setTaskId(cursor.getLong(0));
                taskItem.setTaskOnlineId(cursor.getLong(1));
                taskItem.setTaskName(cursor.getString(2));
                taskItem.setTaskDetail(cursor.getString(3));
                taskItem.setPriority(cursor.getInt(4));
                taskItem.setStartDate(cursor.getLong(5));
                taskItem.setEndDate(cursor.getLong(6));
                taskItem.setAddedBy(cursor.getLong(7));
                taskItem.setTaskStatus(cursor.getString(8));
                taskItem.setUserIds(GeneralExtension
                        .toLongArray(cursor.getString(9), ","));
                taskItem.setGroupIds(GeneralExtension
                        .toLongArray(cursor.getString(10), ","));
                taskItem.setAllUserIds(GeneralExtension
                        .toLongArray(cursor.getString(11), ","));
                taskItem.setAddedByName(cursor.getString(12));

                tasks.add(taskItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return tasks;
    }

    public List<FormItem> getForms(long userId) {
        List<FormItem> forms = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_FORMS, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                FormItem formItem = new FormItem();
                formItem.setFormId(cursor.getLong(0));
                formItem.setFormOnlineId(cursor.getLong(1));
                formItem.setFormUniqueId(cursor.getString(2));
                formItem.setFormLanguageId(cursor.getLong(3));
                formItem.setFormUserId(cursor.getLong(4));
                formItem.setFormName(cursor.getString(5));
                formItem.setFormDescription(cursor.getString(6));
                formItem.setFormExpiryDate(cursor.getString(7));
                formItem.setFormJson(cursor.getString(8));
                formItem.setFormAccess(cursor.getLong(9));
                formItem.setFormStatus(cursor.getInt(10));
                formItem.setFormLink(cursor.getString(11));
                formItem.setUserIds(GeneralExtension.toLongArray(cursor.getString(12), ","));
                formItem.setGroupIds(GeneralExtension.toLongArray(cursor.getString(13), ","));
                formItem.setGroupUserIds(GeneralExtension.toLongArray(cursor.getString(14), ","));
                formItem.setFormLanguageName(cursor.getString(15));
                formItem.setFormType(cursor.getInt(16));
                formItem.setTotalMarks(cursor.getInt(17));

                forms.add(formItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return forms;
    }

    public List<Long> getFormIds() {
        List<Long> formIds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_FORM_IDS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                formIds.add(cursor.getLong(0));
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return formIds;
    }

    public FormItem getFormDetails(long formId) {
        FormItem formItem = new FormItem();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_FORM, new String[]{String.valueOf(formId)});
        if (cursor != null && cursor.moveToFirst()) {
            formItem.setFormId(cursor.getLong(0));
            formItem.setFormOnlineId(cursor.getLong(1));
            formItem.setFormUniqueId(cursor.getString(2));
            formItem.setFormLanguageId(cursor.getLong(3));
            formItem.setFormUserId(cursor.getLong(4));
            formItem.setFormName(cursor.getString(5));
            formItem.setFormDescription(cursor.getString(6));
            Log.e(TAG, "getFormDetails: " + cursor.getString(7));
            formItem.setFormExpiryDate(cursor.getString(7));
            formItem.setFormJson(cursor.getString(8));
            formItem.setFormAccess(cursor.getLong(9));
            formItem.setFormStatus(cursor.getInt(10));
            formItem.setFormLink(cursor.getString(11));
            formItem.setUserIds(GeneralExtension.toLongArray(cursor.getString(12), ","));
            formItem.setGroupIds(GeneralExtension.toLongArray(cursor.getString(13), ","));
            formItem.setGroupUserIds(GeneralExtension.toLongArray(cursor.getString(14), ","));
            formItem.setFormType(cursor.getInt(15));
            formItem.setTotalMarks(cursor.getInt(16));
            formItem.setDelete(cursor.getInt(17) == 1);

            cursor.close();
        }

        db.close();
        return formItem;
    }

    public List<ResponseItem> getResponses(long formId) {
        List<ResponseItem> responses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_RESPONSES, new String[]{String.valueOf(formId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ResponseItem responseItem = new ResponseItem();
                responseItem.setResponseId(cursor.getLong(0));
                responseItem.setOnlineId(cursor.getLong(1));
                responseItem.setFormId(cursor.getLong(2));
                responseItem.setUserId(cursor.getLong(3));
                responseItem.setSubmitDate(cursor.getString(4));
                responseItem.setUserName(cursor.getString(5));
                responseItem.setResponseData(cursor.getString(6));
                Log.e(TAG, "getFormDetails: " + cursor.getString(7));
                if (cursor.getString(7) != null) {
                    try {
                        responseItem.setResponseFiles(FormExtension
                                .convertFileDataToList(new JSONArray(cursor.getString(7))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                responses.add(responseItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return responses;
    }

    public UserItem getUserDetails(long userId) {
        UserItem userItem = new UserItem();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_USER, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            userItem.setUserId(cursor.getLong(0));
            userItem.setUserOnlineId(cursor.getLong(1));
            userItem.setRoleId(cursor.getLong(2));
            userItem.setUserName(cursor.getString(3));
            userItem.setUserEmail(cursor.getString(4));
            userItem.setUserPhone(cursor.getString(5));
            userItem.setUserStatus(cursor.getInt(6));
            userItem.setUserAddedBy(cursor.getLong(7));
            userItem.setRoleName(cursor.getString(8));
            userItem.setModuleIds(GeneralExtension.toLongArray(cursor.getString(9), ","));
            userItem.setPermissionIds(GeneralExtension.toLongArray(cursor.getString(10), ","));
            userItem.setUserProfile(cursor.getString(11));

            cursor.close();
        }
        db.close();
        return userItem;
    }

    public RoleItem getGroupDetails(long roleId) {
        RoleItem roleItem = new RoleItem();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_GROUP, new String[]{String.valueOf(roleId)});
        if (cursor != null && cursor.moveToFirst()) {
            roleItem.setRoleId(cursor.getLong(0));
            roleItem.setRoleOnlineId(cursor.getLong(1));
            roleItem.setRoleName(cursor.getString(2));
            roleItem.setModuleIds(GeneralExtension
                    .toLongArray(cursor.getString(3), ","));
            roleItem.setPermissionIds(GeneralExtension
                    .toLongArray(cursor.getString(4), ","));
            roleItem.setAddedBy(cursor.getLong(5));

            cursor.close();
        }
        db.close();

        return roleItem;
    }

    public TaskItem getTaskDetails(long taskId) {
        TaskItem taskItem = new TaskItem();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_TASK, new String[]{String.valueOf(taskId)});
        if (cursor != null && cursor.moveToFirst()) {
            taskItem.setTaskId(cursor.getLong(0));
            taskItem.setTaskOnlineId(cursor.getLong(1));
            taskItem.setTaskName(cursor.getString(2));
            taskItem.setTaskDetail(cursor.getString(3));
            taskItem.setPriority(cursor.getInt(4));
            taskItem.setStartDate(cursor.getLong(5));
            taskItem.setEndDate(cursor.getLong(6));
            taskItem.setAddedBy(cursor.getLong(7));
            taskItem.setTaskStatus(cursor.getString(8));
            taskItem.setUserIds(GeneralExtension
                    .toLongArray(cursor.getString(9), ","));
            taskItem.setGroupIds(GeneralExtension
                    .toLongArray(cursor.getString(10), ","));
            taskItem.setTaskUserStatus(cursor.getString(11));
            taskItem.setAddedByName(cursor.getString(12));

            cursor.close();
        }
        db.close();
        return taskItem;
    }

    public ResponseItem getResponseDetails(long responseId, long onlineId) {
        ResponseItem responseItem = new ResponseItem();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DBEntry.GET_RESPONSE, new String[]{String.valueOf(responseId), String.valueOf(onlineId)});
        if (cursor != null && cursor.moveToFirst()) {
            responseItem.setResponseId(cursor.getLong(0));
            responseItem.setOnlineId(cursor.getLong(1));
            responseItem.setFormId(cursor.getLong(2));
            responseItem.setUserId(cursor.getLong(3));
            responseItem.setSubmitDate(cursor.getString(4));
            responseItem.setUserName(cursor.getString(5));
            responseItem.setResponseData(cursor.getString(6));
            Log.e(TAG, "getFormDetails: " + cursor.getString(7));
            if (cursor.getString(7) != null) {
                try {
                    responseItem.setResponseFiles(FormExtension
                            .convertFileDataToList(new JSONArray(cursor.getString(7))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            responseItem.setDelete(cursor.getInt(8) == 1);

            cursor.close();
        }

        db.close();
        return responseItem;
    }

    public Bundle getDashboardStatistics(long userId) {
        Bundle bundle = new Bundle();
        SQLiteDatabase db = getReadableDatabase();
        Cursor gCursor = db.rawQuery(DBEntry.GET_GROUPS_COUNT, new String[]{String.valueOf(userId)});
        if (gCursor != null && gCursor.moveToFirst()) {
            bundle.putInt("group_count", gCursor.getInt(0));
            gCursor.close();
        }
        Cursor uCursor = db.rawQuery(DBEntry.GET_USERS_COUNT, new String[]{String.valueOf(userId)});
        if (uCursor != null && uCursor.moveToFirst()) {
            bundle.putInt("user_count", uCursor.getInt(0));
            uCursor.close();
        }
        Cursor tCursor = db.rawQuery(DBEntry.GET_TASKS_COUNT, new String[]{String.valueOf(userId)});
        if (tCursor != null && tCursor.moveToFirst()) {
            bundle.putInt("task_count", tCursor.getInt(0));
            tCursor.close();
        }
        Cursor fCursor = db.rawQuery(DBEntry.GET_FORMS_COUNT, new String[]{String.valueOf(userId)});
        if (fCursor != null && fCursor.moveToFirst()) {
            bundle.putInt("form_count", fCursor.getInt(0));
            fCursor.close();
        }

        return bundle;
    }

    public long updateUserOffline(UserItem userItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.USER_NAME, userItem.getUserName());
        cv.put(DBEntry.USER_EMAIL, userItem.getUserEmail());
        cv.put(DBEntry.USER_MOBILE_NO, userItem.getUserPhone());
        if (userItem.getUserPassword() != null)
            cv.put(DBEntry.USER_PASSWORD, userItem.getUserPassword());
        else
            cv.putNull(DBEntry.USER_PASSWORD);
        if (userItem.getUserAccessJson() != null)
            cv.put(DBEntry.USER_PERMISSION, userItem.getUserAccessJson());
        if (userItem.getModuleIds() != null)
            cv.put(DBEntry.USER_MODULE_IDS, GeneralExtension.toString(userItem.getModuleIds()));
        if (userItem.getPermissionIds() != null)
            cv.put(DBEntry.USER_PERMISSION_IDS, GeneralExtension.toString(userItem.getPermissionIds()));
        if (userItem.getRoleId() > 0)
            cv.put(DBEntry.USER_ROLE_ID, userItem.getRoleId());
        cv.put(DBEntry.IS_SYNCED, 0);

        long i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ID + "=?", new String[]{String.valueOf(userItem.getUserId())});
        db.close();

        return i;
    }

    public long updateGroupOffline(RoleItem roleItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.GROUP_NAME, roleItem.getRoleName());
        cv.put(DBEntry.GROUP_MODULE_LIST, GeneralExtension.toString(roleItem.getModuleIds()));
        cv.put(DBEntry.GROUP_PERMISSION_LIST, GeneralExtension.toString(roleItem.getPermissionIds()));
        cv.put(DBEntry.GROUP_ADDED_BY, roleItem.getAddedBy());
        cv.put(DBEntry.IS_SYNCED, 0);

        long i = db.update(DBEntry.TABLE_USER_GROUPS, cv, DBEntry.GROUP_ID + "=?", new String[]{String.valueOf(roleItem.getRoleId())});
        db.close();

        return i;
    }

    public long updateTaskOffline(TaskItem taskItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.TASK_TITLE, taskItem.getTaskName());
        cv.put(DBEntry.TASK_DETAILS, taskItem.getTaskDetail());
        cv.put(DBEntry.TASK_PRIORITY, taskItem.getPriority());
        cv.put(DBEntry.TASK_START_DATE, taskItem.getStartDate());
        cv.put(DBEntry.TASK_END_DATE, taskItem.getEndDate());
        cv.put(DBEntry.TASK_STATUS, taskItem.getTaskStatus());
        if (taskItem.getUserIds() != null)
            cv.put(DBEntry.TASK_USER_IDS, GeneralExtension.toString(taskItem.getUserIds()));
        if (taskItem.getGroupIds() != null)
            cv.put(DBEntry.TASK_GROUP_IDS, GeneralExtension.toString(taskItem.getGroupIds()));
        cv.put(DBEntry.IS_SYNCED, 0);
        cv.put(DBEntry.IS_DELETE, taskItem.isDelete() ? 1 : 0);

        long i = db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ID + "=?", new String[]{String.valueOf(taskItem.getTaskId())});
        db.close();

        return i;
    }

    public long updateUserStatusOffline(long userId, int userStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.USER_STATUS, userStatus);
        cv.put(DBEntry.IS_STATUS_SYNCED, 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        db.close();
        return i;
    }

    public long updateFormOffline(FormItem formItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(DBEntry.FORM_UNIQUE_ID, formItem.getFormUniqueId());
//        cv.put(DBEntry.FORM_ONLINE_ID, formItem.getFormOnlineId());
//        cv.put(DBEntry.FORM_USER_ID, formItem.getFormUserId());
        cv.put(DBEntry.FORM_LANGUAGE_ID, formItem.getFormLanguageId());
        cv.put(DBEntry.FORM_NAME, formItem.getFormName());
        cv.put(DBEntry.FORM_DESCRIPTION, formItem.getFormDescription());
        cv.put(DBEntry.FORM_EXPIRY_DATE, formItem.getFormExpiryDate());
        cv.put(DBEntry.FORM_STATUS, formItem.getFormStatus());
        cv.put(DBEntry.FORM_ACCESS, formItem.getFormAccess());
        cv.put(DBEntry.FORM_JSON, formItem.getFormJson());
        cv.put(DBEntry.FORM_LINK, formItem.getFormLink());
        cv.put(DBEntry.FORM_TYPE, formItem.getFormType());
        cv.put(DBEntry.TOTAL_MARKS, formItem.getTotalMarks());
        cv.put(DBEntry.IS_SYNCED, 0);
        cv.put(DBEntry.IS_DELETE, formItem.isDelete() ? 1 : 0);
        if (formItem.getUserIds() != null)
            cv.put(DBEntry.FORM_USER_IDS, GeneralExtension.toString(formItem.getUserIds()));
        if (formItem.getGroupIds() != null)
            cv.put(DBEntry.FORM_GROUP_IDS, GeneralExtension.toString(formItem.getGroupIds()));
//        cv.put(DBEntry.FORM_GROUP_USER_IDS, Arrays.toString(formItem.getGroupUserIds()));
//        cv.put(DBEntry.CREATED_AT, formItem.getCreatedAt());
//        cv.put(DBEntry.UPDATED_AT, formItem.getUpdatedAt());

        long i = db.update(DBEntry.TABLE_FORM, cv, DBEntry.FORM_ID + "=?", new String[]{String.valueOf(formItem.getFormId())});
        db.close();

        return i;
    }

    public long updateTaskStatusOffline(long taskId, String taskStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.TASK_STATUS, taskStatus);
        cv.put(DBEntry.IS_STATUS_SYNCED, 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ID + "=?",
                new String[]{String.valueOf(taskId)});

        db.close();
        return i;
    }

    public int updateResponseOffline(ResponseItem responseItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.RESPONSE_USER_ID, responseItem.getUserId());
        cv.put(DBEntry.RESPONSE_FORM_ID, responseItem.getFormId());
        cv.put(DBEntry.RESPONSE_USER_NAME, responseItem.getUserName());
//        cv.put(DBEntry.RESPONSE_DATE, responseItem.getSubmitDate());
        cv.put(DBEntry.RESPONSE_USER_DATA, responseItem.getResponseData());
        if (responseItem.getResponseFiles() != null) {
            try {
                cv.put(DBEntry.RESPONSE_FILE_DATA, FormExtension
                        .convertFileListToArray(responseItem.getResponseFiles()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cv.put(DBEntry.IS_SYNCED, 0);

        int i = db.update(DBEntry.TABLE_RESPONSE, cv, DBEntry.RESPONSE_ID + "=?",
                new String[]{String.valueOf(responseItem.getResponseId())});
        db.close();

        return i;
    }

    public void updateUserSignedIn(SQLiteDatabase db, long onlineId) {
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.SIGNED_IN_USER, 1);

        db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ONLINE_ID + "=?", new String[]{String.valueOf(onlineId)});
    }

    public int updateOnlineIdOfUser(long id, long onlineId, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.USER_ONLINE_ID, onlineId);
        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int updateUserStatusSync(long onlineId, boolean isStatusSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.IS_STATUS_SYNCED, isStatusSynced ? 1 : 0);

        int i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ONLINE_ID + "=?",
                new String[]{String.valueOf(onlineId)});

        db.close();
        return i;
    }

    public int updateTaskStatusSync(long onlineId, boolean isStatusSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.IS_STATUS_SYNCED, isStatusSynced ? 1 : 0);

        int i = db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ONLINE_ID + "=?",
                new String[]{String.valueOf(onlineId)});

        db.close();
        return i;
    }

    public int updateOnlineIdOfGroup(long id, long onlineId, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.GROUP_ONLINE_ID, onlineId);
        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_USER_GROUPS, cv, DBEntry.GROUP_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int updateOnlineIdOfTask(long id, long onlineId, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.TASK_ONLINE_ID, onlineId);
        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int updateOnlineIdAndUniqueIdOfForm(long id, long onlineId/*, String uniqueId*/, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.FORM_ONLINE_ID, onlineId);
        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_FORM, cv, DBEntry.FORM_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int updateFormSyncStatus(long id, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
        int i = db.update(DBEntry.TABLE_FORM, cv, DBEntry.FORM_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int updateFormStatus(long id, String status, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.FORM_STATUS, status.equals("active") ? 1 : 0);
        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
        int i = db.update(DBEntry.TABLE_FORM, cv, DBEntry.FORM_ONLINE_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int updateOnlineIdOfResponse(long id, long onlineId, boolean isSynced) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.RESPONSE_ONLINE_ID, onlineId);
        cv.put(DBEntry.IS_SYNCED, isSynced ? 1 : 0);
//        cv.put(DBEntry.FORM_UNIQUE_ID, uniqueId);

        int i = db.update(DBEntry.TABLE_RESPONSE, cv, DBEntry.RESPONSE_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
        return i;
    }

    public int deleteUserOffline(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.IS_DELETE, 1);
        cv.put(DBEntry.IS_SYNCED, 0);

        int i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();

        return i;
    }

    public int deleteTaskOffline(long taskId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.IS_DELETE, 1);
        cv.put(DBEntry.IS_SYNCED, 0);

        int i = db.update(DBEntry.TABLE_TASK, cv, DBEntry.TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();

        return i;
    }

    private void clearLanguageTable(SQLiteDatabase db) {
        db.delete(DBEntry.TABLE_LANGUAGE, null, null);
    }

    private void clearModuleTable(SQLiteDatabase db) {
        db.delete(DBEntry.TABLE_MODULE, null, null);
    }

    private void clearPermissionTable(SQLiteDatabase db) {
        db.delete(DBEntry.TABLE_PERMISSION, null, null);
    }

    private void clearFormTypeTable(SQLiteDatabase db){
        db.delete(DBEntry.TABLE_FORM_TYPE, null, null);
    }

    public int signOutUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBEntry.SIGNED_IN_USER, 0);

        int i = db.update(DBEntry.TABLE_USER, cv, DBEntry.USER_ONLINE_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return i;
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }

}
