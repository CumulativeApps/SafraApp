package com.safra.utilities;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.Safra;
import com.safra.events.FormListSyncEvent;
import com.safra.events.GroupListSyncEvent;
import com.safra.events.ResponseListSyncEvent;
import com.safra.events.TaskListSyncEvent;
import com.safra.events.TemplateListSyncEvent;
import com.safra.events.UserListSyncEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.models.FileItem;
import com.safra.models.FormItem;
import com.safra.models.ResponseItem;
import com.safra.models.RoleItem;
import com.safra.models.TaskItem;
import com.safra.models.TemplateItem;
import com.safra.models.UserItem;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_FILL_PRIVATE_API;
import static com.safra.utilities.Common.FORM_LIST_API;
import static com.safra.utilities.Common.FORM_RESPONSE_LIST_API;
import static com.safra.utilities.Common.FORM_SAVE_API;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.GROUP_SAVE_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.Common.TASK_LIST_API;
import static com.safra.utilities.Common.TASK_SAVE_API;
import static com.safra.utilities.Common.TASK_STATUS_API;
import static com.safra.utilities.Common.TEMPLATE_LIST_API;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.Common.USER_SAVE_API;
import static com.safra.utilities.Common.USER_STATUS_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class SyncData {

    public static void uploadUnsyncedForms(String TAG) {
        Safra.isFormSyncing = true;
        Log.e(TAG, "uploadUnsyncedForms: start syncing...");
        List<FormItem> forms = dbHandler.getUnsyncedForms();
        if (forms.size() > 0) {
            final int[] totalForms = {forms.size()};
            for (FormItem fi : forms) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken);
                hashMap.put("form_name", fi.getFormName());
                if (fi.getFormDescription() != null && !fi.getFormDescription().isEmpty())
                    hashMap.put("form_description", fi.getFormDescription());
                hashMap.put("form_json", fi.getFormJson());
                hashMap.put("form_access", String.valueOf(fi.getFormAccess()));
                hashMap.put("form_type", String.valueOf(fi.getFormType()));
                hashMap.put("form_language_id", String.valueOf(fi.getFormLanguageId()));
                hashMap.put("form_status", String.valueOf(fi.getFormStatus()));
                if (fi.getFormExpiryDate() != null && !fi.getFormExpiryDate().isEmpty())
                    hashMap.put("form_expiry_date", fi.getFormExpiryDate());
                if (fi.getUserIds().length > 0) {
                    hashMap.put("form_user_ids", GeneralExtension.toString(fi.getUserIds()));
                }
                if (fi.getGroupIds().length > 0) {
                    hashMap.put("form_group_ids", GeneralExtension.toString(fi.getGroupIds()));
                }
                if (fi.getTotalMarks() > 0)
                    hashMap.put("form_mcq_marks", String.valueOf(fi.getTotalMarks()));
                if (fi.getFormOnlineId() > 0)
                    hashMap.put("form_id", String.valueOf(fi.getFormOnlineId()));

                Log.e(TAG, "uploadUnsyncedForms: user_token ->" + (userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken));
                Log.e("SYNC_API","FORM_SAVE_API");

                AndroidNetworking
                        .post(BASE_URL + FORM_SAVE_API)
                        .addBodyParameter(hashMap)
                        .setTag("save-form-api")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int success = response.getInt("success");
                                    String message = response.getString("message");
                                    Log.e(TAG, "uploadUnsyncedForms: onResponse: message -> " + message);
//                                    dialogL.dismiss();
                                    if (success == 1) {
                                        long onlineId = response.getJSONObject("data").getLong("form_id");
                                        dbHandler.updateOnlineIdAndUniqueIdOfForm(fi.getFormId(), onlineId, true);
                                    }

                                    totalForms[0] -= 1;

                                    if (totalForms[0] == 0) {
                                        SyncData.syncForms(TAG, PAGE_START);
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "uploadUnsyncedForms: onResponse: " + e.getLocalizedMessage());
                                    Safra.isFormSyncing = false;
//                                    dialogL.dismiss();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "uploadUnsyncedForms: onError: " + anError.getErrorCode());
                                Log.e(TAG, "uploadUnsyncedForms: onError: " + anError.getErrorDetail());
                                Log.e(TAG, "uploadUnsyncedForms: onError: " + anError.getErrorBody());
                                Safra.isFormSyncing = false;
//                                dialogL.dismiss();
                            }
                        });
            }
        }

        else {
            SyncData.syncForms(TAG, PAGE_START);
        }
    }

    public static void syncForms(String TAG, int pageNumber) {
        AndroidNetworking
                .post(BASE_URL + FORM_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNumber))
                .setTag("form-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray forms = data.getJSONArray("form_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (forms.length() > 0) {
                                    List<FormItem> formList = new ArrayList<>();

                                    for (int i = 0; i < forms.length(); i++) {
                                        JSONObject form = forms.getJSONObject(i);
                                        FormItem formItem = new FormItem();
                                        formItem.setFormOnlineId(form.getLong("form_id"));
                                        formItem.setFormUniqueId(form.getString("form_unique_id"));
                                        formItem.setFormName(form.getString("form_name"));
                                        formItem.setFormLanguageId(form.getInt("form_language_id"));
                                        formItem.setFormLanguageName(form.getString("language_title"));

                                        if (form.has("form_description") && !form.isNull("form_description"))
                                            formItem.setFormDescription(form.getString("form_description"));

                                        if (form.has("form_json") && !form.isNull("form_json"))
                                            formItem.setFormJson(new JSONArray(form.getString("form_json")).toString());

                                        if (form.has("form_expiry_date") && !form.isNull("form_expiry_date"))
                                            formItem.setFormExpiryDate(form.getString("form_expiry_date"));

                                        if (form.has("form_access") && !form.isNull("form_access"))
                                            formItem.setFormAccess(form.getInt("form_access"));

                                        if (form.has("form_type") && !form.isNull("form_type"))
                                            formItem.setFormType(form.getLong("form_type"));

                                        if (form.has("form_mcq_marks") && !form.isNull("form_mcq_marks"))
                                            formItem.setTotalMarks(form.getInt("form_mcq_marks"));

                                        if (form.has("form_status") && !form.isNull("form_status"))
                                            formItem.setFormStatus(form.getInt("form_status"));

                                        if (form.has("form_link") && !form.isNull("form_link"))
                                            formItem.setFormLink(form.getString("form_link"));

                                        if (form.has("form_user_ids") && !form.isNull("form_user_ids"))
                                            formItem.setUserIds(GeneralExtension.toLongArray(form.getString("form_user_ids"), ","));

                                        if (form.has("form_group_ids") && !form.isNull("form_group_ids"))
                                            formItem.setGroupIds(GeneralExtension.toLongArray(form.getString("form_group_ids"), ","));

                                        if (form.has("form_group_user_ids") && !form.isNull("form_group_user_ids"))
                                            formItem.setGroupUserIds(GeneralExtension.toLongArray(form.getString("form_group_user_ids"), ","));

                                        formItem.setDelete(form.getInt("is_delete") == 1);

                                        formItem.setFormUserId(form.getLong("form_master_id"));

                                        formList.add(formItem);
                                    }

                                    dbHandler.addForms(formList);
                                    EventBus.getDefault().post(new FormListSyncEvent(formList));
                                }

                                if (currentPage < totalPage) {
                                    syncForms(TAG, ++currentPage);
                                } else {
                                    Safra.isFormSyncing = false;
                                    Log.e(TAG, "syncForms: onResponse: end form syncing...");
                                }
                            } else {
                                Log.e(TAG, "syncForms: onResponse: " + message);
                                Safra.isFormSyncing = false;
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "syncForms: onResponse: " + e.getLocalizedMessage());
                            Safra.isFormSyncing = false;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "syncForms: onError: " + anError.getErrorCode());
                        Log.e(TAG, "syncForms: onError: " + anError.getErrorDetail());
                        Log.e(TAG, "syncForms: onError: " + anError.getErrorBody());
                        Safra.isFormSyncing = false;
                    }
                });
    }

    public static void uploadUnsyncedTasks(String TAG) {
        Safra.isTaskSyncing = true;
        Log.e(TAG, "uploadUnsyncedTasks: start syncing...");
        SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        List<TaskItem> tasks = dbHandler.getUnsyncedTasks();
        if (tasks.size() > 0) {
            final int[] totalTasks = {tasks.size()};
            for (TaskItem ti : tasks) {
                if (!ti.isSynced()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("task_title", ti.getTaskName());
                    if (!ti.getTaskDetail().isEmpty())
                        hashMap.put("task_details", ti.getTaskDetail());
                    hashMap.put("task_priority", String.valueOf(ti.getPriority()));
                    hashMap.put("task_start_date", sdfForServer.format(new Date(ti.getStartDate())));
                    hashMap.put("task_end_date", sdfForServer.format(new Date(ti.getEndDate())));
                    if (ti.getUserIds().length > 0)
                        hashMap.put("task_user_ids", GeneralExtension.toString(ti.getUserIds()));
                    if (ti.getGroupIds().length > 0)
                        hashMap.put("task_group_ids", GeneralExtension.toString(ti.getGroupIds()));
                    if (ti.getTaskOnlineId() > 0)
                        hashMap.put("task_id", String.valueOf(ti.getTaskOnlineId()));

                    Log.e("SYNC_API","TASK_SAVE_API");
                    AndroidNetworking
                            .post(BASE_URL + TASK_SAVE_API)
                            .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                            .addBodyParameter(hashMap)
                            .setTag("save-task-api")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int success = response.getInt("success");
                                        String message = response.getString("message");
                                        Log.e(TAG, "uploadUnsyncedTask: onResponse: message -> " + message);
//                                    dialogL.dismiss();
                                        if (success == 1) {
                                            long onlineId = response.getJSONObject("data").getLong("task_id");
                                            dbHandler.updateOnlineIdOfTask(ti.getTaskId(), onlineId, true);

                                            if (!ti.isStatusSynced()) {
                                                Log.e("SYNC_API","TASK_STATUS_API");
                                                AndroidNetworking
                                                        .post(BASE_URL + TASK_STATUS_API)
                                                        .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                                                        .addBodyParameter("task_id", String.valueOf(ti.getTaskOnlineId()))
                                                        .addBodyParameter("task_status", ti.getTaskStatus())
                                                        .setTag("change-task-status-api")
                                                        .build()
                                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    int success = response.getInt("success");
                                                                    String message = response.getString("message");
                                                                    Log.e(TAG, "uploadUnsyncedTasks: uploadUnsyncedTaskStatus: onResponse: " + message);
                                                                    if (success == 1) {
                                                                        dbHandler.updateTaskStatusSync(ti.getTaskOnlineId(), true);
                                                                    }
                                                                    totalTasks[0] -= 1;
                                                                    if (totalTasks[0] == 0) {
                                                                        SyncData.syncTasks(TAG, PAGE_START);
                                                                    }
                                                                } catch (JSONException e) {
                                                                    Log.e(TAG, "uploadUnsyncedTasks: uploadUnsyncedTaskStatus: onResponse: " + e.getLocalizedMessage());
                                                                    Safra.isTaskSyncing = false;
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(ANError anError) {
                                                                Log.e(TAG, "uploadUnsyncedTasks: uploadUnsyncedTaskStatus: onError: " + anError.getErrorCode());
                                                                Log.e(TAG, "uploadUnsyncedTasks: uploadUnsyncedTaskStatus: onError: " + anError.getErrorDetail());
                                                                Log.e(TAG, "uploadUnsyncedTasks: uploadUnsyncedTaskStatus: onError: " + anError.getErrorBody());
                                                                Safra.isTaskSyncing = false;
                                                            }
                                                        });
                                            } else {
                                                totalTasks[0] -= 1;
                                            }
                                        } else {
                                            totalTasks[0] -= 1;
                                        }

                                        if (totalTasks[0] == 0) {
                                            SyncData.syncTasks(TAG, PAGE_START);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "uploadUnsyncedTask: onResponse: " + e.getLocalizedMessage());
//                                    dialogL.dismiss();
                                        Safra.isTaskSyncing = false;
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "uploadUnsyncedTask: onError: " + anError.getErrorCode());
                                    Log.e(TAG, "uploadUnsyncedTask: onError: " + anError.getErrorDetail());
                                    Log.e(TAG, "uploadUnsyncedTask: onError: " + anError.getErrorBody());
//                                dialogL.dismiss();
                                    Safra.isTaskSyncing = false;
                                }
                            });
                } else if (!ti.isStatusSynced()) {
                    Log.e("SYNC_API","TASK_STATUS_API");
                    AndroidNetworking
                            .post(BASE_URL + TASK_STATUS_API)
                            .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                            .addBodyParameter("task_id", String.valueOf(ti.getTaskOnlineId()))
                            .addBodyParameter("task_status", ti.getTaskStatus())
                            .setTag("change-task-status-api")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int success = response.getInt("success");
                                        String message = response.getString("message");
                                        Log.e(TAG, "uploadUnsyncedTaskStatus: onResponse: " + message);
                                        if (success == 1) {
                                            dbHandler.updateTaskStatusSync(ti.getTaskOnlineId(), true);
                                        }

                                        totalTasks[0] -= 1;
                                        if (totalTasks[0] == 0) {
                                            SyncData.syncTasks(TAG, PAGE_START);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "uploadUnsyncedTaskStatus: onResponse: " + e.getLocalizedMessage());
                                        Safra.isTaskSyncing = false;
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "uploadUnsyncedTaskStatus: onError: " + anError.getErrorCode());
                                    Log.e(TAG, "uploadUnsyncedTaskStatus: onError: " + anError.getErrorDetail());
                                    Log.e(TAG, "uploadUnsyncedTaskStatus: onError: " + anError.getErrorBody());
                                    Safra.isTaskSyncing = false;
                                }
                            });
                }
            }
        } else {
            SyncData.syncTasks(TAG, PAGE_START);
        }
    }

    public static void syncTasks(String TAG, int pageNumber) {
        Log.e("SYNC_API","TASK_LIST_API");
        AndroidNetworking
                .post(BASE_URL + TASK_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNumber))
                .setTag("task-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray tasks = data.getJSONArray("task_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (tasks.length() > 0) {
                                    List<TaskItem> taskList = new ArrayList<>();

                                    for (int i = 0; i < tasks.length(); i++) {
                                        JSONObject task = tasks.getJSONObject(i);
                                        TaskItem taskItem = new TaskItem();
                                        taskItem.setTaskOnlineId(task.getInt("task_id"));
                                        taskItem.setTaskName(task.getString("task_title"));
//                                        taskItem.setUserStatus(task.getInt("task_details"));

                                        if (task.has("task_priority") && !task.isNull("task_priority")) {
                                            taskItem.setPriorityName(task.getString("task_priority"));
                                            switch (taskItem.getPriorityName().toLowerCase()) {
                                                case "low":
                                                    taskItem.setPriority(3);
                                                    break;
                                                case "medium":
                                                    taskItem.setPriority(2);
                                                    break;
                                                case "high":
                                                default:
                                                    taskItem.setPriority(1);

                                            }
                                        }

                                        if (task.has("task_details") && !task.isNull("task_details"))
                                            taskItem.setTaskDetail(task.getString("task_details"));

                                        if (task.has("task_start_date") && !task.isNull("task_start_date"))
                                            taskItem.setStartDate(task.getLong("task_start_date") * 1000);

                                        if (task.has("task_end_date") && !task.isNull("task_end_date"))
                                            taskItem.setEndDate(task.getLong("task_end_date") * 1000);

                                        if (task.has("added_by_user") && !task.isNull("added_by_user"))
                                            taskItem.setAddedByName(task.getString("added_by_user"));

                                        if (task.has("added_by") && !task.isNull("added_by"))
                                            taskItem.setAddedBy(task.getLong("added_by"));

                                        if (task.has("status") && !task.isNull("status")) {
                                            taskItem.setTaskStatus(task.getString("status"));
                                        }

                                        taskItem.setMasterId(task.getInt("task_master_id"));

                                        if (task.has("task_group_ids") && !task.isNull("task_group_ids"))
                                            taskItem.setGroupIds(GeneralExtension.toLongArray(task.getString("task_group_ids"), ","));

                                        if (task.has("task_user_ids") && !task.isNull("task_user_ids"))
                                            taskItem.setUserIds(GeneralExtension.toLongArray(task.getString("task_user_ids"), ","));

                                        if (task.has("assigned_users") && !task.isNull("assigned_users")) {
                                            taskItem.setTaskUserStatus(task.getJSONArray("assigned_users").toString());
                                            JSONArray jsonArray = task.getJSONArray("assigned_users");
                                            Long[] allUserIds = new Long[jsonArray.length()];
                                            for (int j = 0; j < jsonArray.length(); j++) {
                                                allUserIds[j] = jsonArray.getJSONObject(j).getLong("user_id");
                                            }

                                            taskItem.setAllUserIds(allUserIds);
                                        }

                                        taskList.add(taskItem);
                                    }

                                    dbHandler.addTasks(taskList);
                                    EventBus.getDefault().post(new TaskListSyncEvent(taskList));
                                }

                                if (currentPage < totalPage) {
                                    syncTasks(TAG, ++currentPage);
                                } else {
                                    Safra.isTaskSyncing = false;
                                    Log.e(TAG, "syncTasks: onResponse: end task syncing...");
                                }
                            } else {
                                Log.e(TAG, "syncTasks: onResponse: " + message);
                                Safra.isTaskSyncing = false;
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "syncTasks: onResponse: " + e.getLocalizedMessage());
                            Safra.isTaskSyncing = false;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "syncTasks: onError: " + anError.getErrorCode());
                        Log.e(TAG, "syncTasks: onError: " + anError.getErrorDetail());
                        Log.e(TAG, "syncTasks: onError: " + anError.getErrorBody());
                        Safra.isTaskSyncing = false;
                    }
                });
    }

    public static void uploadUnsyncedUsers(String TAG) {
        Safra.isUserSyncing = true;
        Log.e(TAG, "uploadUnsyncedUsers: start syncing...");
        List<UserItem> users = dbHandler.getUnsyncedUsers();
        if (users.size() > 0) {
            final int[] totalUsers = {users.size()};
            for (UserItem ui : users) {
                if (!ui.isSynced()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken);
                    hashMap.put("user_name", ui.getUserName());
                    hashMap.put("user_email", ui.getUserEmail());
                    hashMap.put("user_phone_no", ui.getUserPhone());
                    if (ui.getModuleIds() != null)
                        hashMap.put("user_module_ids", GeneralExtension.toString(ui.getModuleIds()));
                    if (ui.getPermissionIds() != null)
                        hashMap.put("user_permission_ids", GeneralExtension.toString(ui.getPermissionIds()));
                    if (!ui.getUserPassword().isEmpty())
                        hashMap.put("user_password", ui.getUserPassword());
                    if (ui.getRoleId() > 0)
                        hashMap.put("user_role_id", String.valueOf(ui.getRoleId()));
                    if (ui.getUserOnlineId() > 0) {
                        hashMap.put("user_id", String.valueOf(ui.getUserOnlineId()));
                    }

                    Log.e("SYNC_API","USER_SAVE_API");
                    AndroidNetworking
                            .post(BASE_URL + USER_SAVE_API)
                            .addBodyParameter(hashMap)
                            .setTag("save-user-api")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int success = response.getInt("success");
                                        String message = response.getString("message");
                                        Log.e(TAG, "uploadUnsyncedUsers: onResponse: message -> " + message);
//                                    dialogL.dismiss();
                                        if (success == 1) {
                                            long onlineId = response.getJSONObject("data").getLong("user_id");
                                            dbHandler.updateOnlineIdOfUser(ui.getUserId(), onlineId, true);

                                            if (!ui.isStatusSynced()) {
                                                AndroidNetworking
                                                        .post(BASE_URL + USER_STATUS_API)
                                                        .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                                                        .addBodyParameter("user_id", String.valueOf(ui.getUserOnlineId()))
                                                        .addBodyParameter("user_status", String.valueOf(ui.getUserStatus()))
                                                        .setTag("change-user-status-api")
                                                        .build()
                                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    int success = response.getInt("success");
                                                                    String message = response.getString("message");
                                                                    Log.e(TAG, "uploadUnsyncedUsersStatus: onResponse: " + message);
                                                                    if (success == 1) {
                                                                        dbHandler.updateUserStatusSync(ui.getUserOnlineId(), true);
                                                                    }
                                                                    totalUsers[0] -= 1;
                                                                    if (totalUsers[0] == 0) {
                                                                        SyncData.syncUsers(TAG, PAGE_START);
                                                                    }
                                                                } catch (JSONException e) {
                                                                    Log.e(TAG, "uploadUnsyncedUsersStatus: onResponse: " + e.getLocalizedMessage());
                                                                    Safra.isUserSyncing = false;
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(ANError anError) {
                                                                Log.e(TAG, "uploadUnsyncedUsersStatus: onError: " + anError.getErrorCode());
                                                                Log.e(TAG, "uploadUnsyncedUsersStatus: onError: " + anError.getErrorDetail());
                                                                Log.e(TAG, "uploadUnsyncedUsersStatus: onError: " + anError.getErrorBody());
                                                                Safra.isUserSyncing = false;
                                                            }
                                                        });
                                            } else {
                                                totalUsers[0] -= 1;
                                            }
                                        } else {
                                            totalUsers[0] -= 1;
                                        }

                                        if (totalUsers[0] == 0) {
                                            SyncData.syncUsers(TAG, PAGE_START);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "uploadUnsyncedUsers: onResponse: " + e.getLocalizedMessage());
                                        Safra.isUserSyncing = false;
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "uploadUnsyncedUsers: onError: " + anError.getErrorCode());
                                    Log.e(TAG, "uploadUnsyncedUsers: onError: " + anError.getErrorDetail());
                                    Log.e(TAG, "uploadUnsyncedUsers: onError: " + anError.getErrorBody());
                                    Safra.isUserSyncing = false;
                                }
                            });
                } else if (!ui.isStatusSynced()) {
                    AndroidNetworking
                            .post(BASE_URL + USER_STATUS_API)
                            .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                            .addBodyParameter("user_id", String.valueOf(ui.getUserOnlineId()))
                            .addBodyParameter("user_status", String.valueOf(ui.getUserStatus()))
                            .setTag("change-user-status-api")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int success = response.getInt("success");
                                        String message = response.getString("message");
                                        Log.e(TAG, "uploadUnsyncedUsersStatus: onResponse: " + message);
                                        if (success == 1) {
                                            dbHandler.updateUserStatusSync(ui.getUserOnlineId(), true);
                                        }
                                        totalUsers[0] -= 1;
                                        if (totalUsers[0] == 0) {
                                            SyncData.syncUsers(TAG, PAGE_START);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "uploadUnsyncedUsersStatus: onResponse: " + e.getLocalizedMessage());
                                        Safra.isUserSyncing = false;
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "uploadUnsyncedUsersStatus: onError: " + anError.getErrorCode());
                                    Log.e(TAG, "uploadUnsyncedUsersStatus: onError: " + anError.getErrorDetail());
                                    Log.e(TAG, "uploadUnsyncedUsersStatus: onError: " + anError.getErrorBody());
                                    Safra.isUserSyncing = false;
                                }
                            });
                }
            }
        } else {
            SyncData.syncUsers(TAG, PAGE_START);
        }
    }

    public static void syncUsers(String TAG, int pageNumber) {
        AndroidNetworking
                .post(BASE_URL + USER_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNumber))
                .setTag("user-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray users = data.getJSONArray("user_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (users.length() > 0) {
                                    List<UserItem> userList = new ArrayList<>();
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
                                        UserItem userItem = new UserItem();
                                        userItem.setUserOnlineId(user.getInt("user_id"));
                                        userItem.setUserName(user.getString("user_name"));
                                        userItem.setUserStatus(user.getInt("user_status"));
                                        userItem.setUserAddedBy(user.getLong("user_master_id"));

                                        if (user.has("user_email") && !user.isNull("user_email")) {
                                            userItem.setUserEmail(user.getString("user_email"));
                                        }

                                        if (user.has("user_phone_no") && !user.isNull("user_phone_no")) {
                                            userItem.setUserPhone(user.getString("user_phone_no"));
                                        }

                                        if (user.has("user_password") && !user.isNull("user_password")) {
                                            userItem.setUserPassword(user.getString("user_password"));
                                        } else {
                                            userItem.setUserPassword("");
                                        }

                                        if (user.has("role_id") && !user.isNull("role_id")) {
                                            userItem.setRoleId(user.getInt("role_id"));
                                        }

                                        if (user.has("role_name") && !user.isNull("role_name")) {
                                            userItem.setRoleName(user.getString("role_name"));
                                        }

                                        if (user.has("user_image_url") && !user.isNull("user_image_url")) {
                                            userItem.setUserProfile(user.getString("user_image_url"));
                                        }

                                        if (user.has("user_module_ids") && !user.isNull("user_module_ids")) {
                                            userItem.setModuleIds(GeneralExtension
                                                    .toLongArray(user.getString("user_module_ids"), ","));
                                        }

                                        if (user.has("user_permission_ids") && !user.isNull("user_permission_ids")) {
                                            userItem.setPermissionIds(GeneralExtension
                                                    .toLongArray(user.getString("user_permission_ids"), ","));
                                        }

                                        userList.add(userItem);
                                        dbHandler.addUser(userItem);
                                    }

                                    dbHandler.addUsers(userList);
                                    EventBus.getDefault().post(new UserListSyncEvent(userList));
                                }

                                if (currentPage < totalPage) {
                                    syncUsers(TAG, ++currentPage);
                                } else {
                                    Safra.isUserSyncing = false;
                                    Log.e(TAG, "syncUser: onResponse: end user syncing...");
                                }

                            } else {
                                Log.e(TAG, "syncUser: onResponse: " + message);
                                Safra.isUserSyncing = false;
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "syncUser: onResponse: " + e.getLocalizedMessage());
                            Safra.isUserSyncing = false;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "syncUser: onError: " + anError.getErrorCode());
                        Log.e(TAG, "syncUser: onError: " + anError.getErrorDetail());
                        Log.e(TAG, "syncUser: onError: " + anError.getErrorBody());
                        Safra.isUserSyncing = false;
                    }
                });
    }

    public static void uploadUnsyncedGroups(String TAG) {
        Safra.isGroupSyncing = true;
        Log.e(TAG, "uploadUnsyncedGroups: start syncing...");
        List<RoleItem> groups = dbHandler.getUnsyncedGroups();
        if (groups.size() > 0) {
            final int[] totalGroups = {groups.size()};
            for (RoleItem ri : groups) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken);
                hashMap.put("role_name", ri.getRoleName());
                hashMap.put("role_module_ids", GeneralExtension.toString(ri.getModuleIds()));
                hashMap.put("role_permission_ids", GeneralExtension.toString(ri.getPermissionIds()));
                if (ri.getRoleOnlineId() > 0)
                    hashMap.put("role_id", String.valueOf(ri.getRoleOnlineId()));

                AndroidNetworking
                        .post(BASE_URL + GROUP_SAVE_API)
                        .addBodyParameter(hashMap)
                        .setTag("save-group-api")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int success = response.getInt("success");
                                    String message = response.getString("message");
                                    Log.e(TAG, "uploadUnsyncedGroups: onResponse: message -> " + message);
                                    if (success == 1) {
                                        long onlineId = response.getJSONObject("data").getLong("role_id");
                                        dbHandler.updateOnlineIdOfGroup(ri.getRoleId(), onlineId, true);
                                    }

                                    totalGroups[0] -= 1;
                                    if (totalGroups[0] == 0) {
                                        SyncData.syncGroups(TAG, PAGE_START);
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "uploadUnsyncedGroups: onResponse: " + e.getLocalizedMessage());
                                    Safra.isGroupSyncing = false;
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "uploadUnsyncedGroups: onError: code -> " + anError.getErrorCode());
                                Log.e(TAG, "uploadUnsyncedGroups: onError: detail -> " + anError.getErrorDetail());
                                Log.e(TAG, "uploadUnsyncedGroups: onError: body -> " + anError.getErrorBody());
                                Safra.isGroupSyncing = false;
                            }
                        });
            }
        } else {
            SyncData.syncGroups(TAG, PAGE_START);
        }
    }

    public static void syncGroups(String TAG, int pageNumber) {
        AndroidNetworking
                .post(BASE_URL + GROUP_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNumber))
                .setTag("group-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray roles = data.getJSONArray("role_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (roles.length() > 0) {
                                    List<RoleItem> roleList = new ArrayList<>();
                                    for (int i = 0; i < roles.length(); i++) {
                                        JSONObject role = roles.getJSONObject(i);
                                        RoleItem roleItem = new RoleItem();
                                        roleItem.setRoleOnlineId(role.getInt("role_id"));
                                        roleItem.setRoleName(role.getString("role_name"));

                                        if (role.has("role_module_ids") && !role.isNull("role_module_ids"))
                                            roleItem.setModuleIds(GeneralExtension
                                                    .toLongArray(role.getString("role_module_ids"), ","));

                                        if (role.has("role_permission_ids") && !role.isNull("role_permission_ids"))
                                            roleItem.setPermissionIds(GeneralExtension
                                                    .toLongArray(role.getString("role_permission_ids"), ","));

                                        if (role.has("added_by") && !role.isNull("added_by"))
                                            roleItem.setAddedBy(role.getLong("added_by"));

                                        roleList.add(roleItem);
                                    }

                                    dbHandler.addGroups(roleList);
                                    EventBus.getDefault().post(new GroupListSyncEvent(roleList));
                                }

                                if (currentPage < totalPage) {
                                    syncGroups(TAG, ++currentPage);
                                } else {
                                    Safra.isUserSyncing = false;
                                    Log.e(TAG, "syncGroups: onResponse: end group syncing...");
                                }
                            } else {
                                Log.e(TAG, "syncGroups: onResponse: " + message);
                                Safra.isGroupSyncing = false;
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "syncGroups: onResponse: " + e.getLocalizedMessage());
                            Safra.isGroupSyncing = false;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "syncGroups: onError: " + anError.getErrorCode());
                        Log.e(TAG, "syncGroups: onError: " + anError.getErrorDetail());
                        Log.e(TAG, "syncGroups: onError: " + anError.getErrorBody());
                        Safra.isGroupSyncing = false;
                    }
                });
    }

    public static void uploadUnsyncedResponses(String TAG) {
        Safra.isResponseSyncing = true;
        Log.e(TAG, "uploadUnsyncedResponses: start syncing...");
        List<ResponseItem> responses = dbHandler.getUnsyncedResponses();
        if (responses.size() > 0) {
            final int[] totalResponses = {responses.size()};
            Log.e(TAG, "uploadUnsyncedResponses: start syncing. totalResponses"+responses.size());
            for (ResponseItem ri : responses) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken);
                hashMap.put("user_data", ri.getResponseData());
                hashMap.put("form_id", String.valueOf(ri.getFormId()));
                if(ri.getOnlineId() > 0)
                    hashMap.put("response_id", String.valueOf(ri.getOnlineId()));

                if (ri.getResponseFiles() != null) {
                    Log.e(TAG, "uploadResponses: responses with files");
                    HashMap<String, List<File>> fileHashMap = new HashMap<>();
                    for (FileItem fi : ri.getResponseFiles()) {
                        List<File> fileList = new ArrayList<>();
                        fileList.add(new File(fi.getFileUrl()));
                        fileHashMap.put(fi.getParentFieldName(), fileList);
                    }

                    Log.e("SYNC_API","=== "+FORM_FILL_PRIVATE_API);
                    AndroidNetworking
                            .upload(BASE_URL + FORM_FILL_PRIVATE_API)
                            .addMultipartFileList(fileHashMap)
                            .addMultipartParameter(hashMap)
                            .setTag("fill-form-private-api")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int success = response.getInt("success");
                                        String message = response.getString("message");
                                        Log.e(TAG, "onResponse: message -> " + message);
//                                        Toast.makeText(FillForm.this, message, Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onResponse: " + success);
                                        Log.e(TAG, "onResponse: uploaded responses of " + ri.getFormId());
                                        if (success == 1) {
                                            long onlineId = response.getJSONObject("data").getLong("response_id");
                                            dbHandler.updateOnlineIdOfResponse(ri.getResponseId(), onlineId, true);
                                        }

                                        totalResponses[0] -= 1;
                                        if (totalResponses[0] == 0) {
                                            SyncData.syncResponses(TAG);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                                        Safra.isResponseSyncing = false;
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                                    Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                                    Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                                    Safra.isResponseSyncing = false;
                                }
                            });

                } else {
                    Log.e(TAG, "SYNC_API: fill-form-private-api");

                    AndroidNetworking
                            .post(BASE_URL + FORM_FILL_PRIVATE_API)
                            .addBodyParameter(hashMap)
                            .setTag("fill-form-private-api")
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int success = response.getInt("success");
                                        String message = response.getString("message");
                                        Log.e(TAG, "onResponse: message -> " + message);
//                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        if (success == 1) {
                                            long onlineId = response.getJSONObject("data").getLong("response_id");
                                            dbHandler.updateOnlineIdOfResponse(ri.getResponseId(), onlineId, true);
                                        }

                                        totalResponses[0] -= 1;
                                        if (totalResponses[0] == 0) {
                                            SyncData.syncResponses(TAG);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                                        Safra.isResponseSyncing = false;
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                                    Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                                    Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                                    Safra.isResponseSyncing = false;
                                }
                            });
                }
            }
        } else {
            SyncData.syncResponses(TAG);
        }
    }

    public static void syncResponses(String TAG) {
        List<Long> formIds = dbHandler.getFormIds();

        if (formIds.size() > 0) {
            final int[] forms = {formIds.size()};
            for (Long formId : formIds) {
                getResponses(TAG, formId, PAGE_START, forms);
            }
        }
    }

    public static void getResponses(String TAG, Long formId, int pageNumber, int[] forms) {
        Log.e("SYNC_API","getResponses");
        AndroidNetworking
                .post(BASE_URL + FORM_RESPONSE_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("form_id", String.valueOf(formId))
                .addBodyParameter("page_no", String.valueOf(pageNumber))
                .setTag("response-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject serverResponse) {
//                        Log.e(TAG, "onResponse: " + serverResponse);
                        try {
                            int success = serverResponse.getInt("success");
                            String message = serverResponse.getString("message");
                            if (success == 1) {
                                JSONObject data = serverResponse.getJSONObject("data");
                                JSONArray responses = data.getJSONArray("response_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (responses.length() > 0) {
                                    List<ResponseItem> responseList = new ArrayList<>();
                                    for (int i = 0; i < responses.length(); i++) {
                                        JSONObject response = responses.getJSONObject(i);
                                        ResponseItem responseItem = new ResponseItem();
                                        responseItem.setOnlineId(response.getLong("response_id"));
                                        responseItem.setFormId(response.getLong("response_form_id"));
                                        responseItem.setUserId(response.getLong("response_user_id"));
                                        responseItem.setSubmitDate(response.getString("response_date"));

                                        if (!response.isNull("user_name"))
                                            responseItem.setUserName(response.getString("user_name"));
                                        else
                                            responseItem.setUserName("Anonymous");

                                        responseItem.setResponseData(new JSONArray(response.getString("response_user_data")).toString());

                                        JSONArray files = response.getJSONArray("files");
                                        if (files.length() > 0) {
                                            ArrayList<FileItem> fileList = new ArrayList<>();
                                            for (int j = 0; j < files.length(); j++) {
                                                JSONObject file = files.getJSONObject(j);
                                                FileItem fileItem = new FileItem();
                                                fileItem.setFileId(file.getLong("file_id"));
                                                fileItem.setFileUrl(file.getString("file_url"));

                                                HashMap<String, String> hashMap = GeneralExtension
                                                        .getKeyFromJSONObject(new JSONObject(file.getString("file_other_data")));

                                                for (String key : hashMap.keySet()) {
                                                    if (fileItem.getFileUrl().substring(fileItem.getFileUrl().lastIndexOf("/") + 1)
                                                            .contains(key)) {
                                                        fileItem.setParentFieldName(hashMap.get(key));
                                                        Log.e(TAG, "onResponse: " + hashMap.get(key));
                                                    }
                                                }

                                                fileList.add(fileItem);
                                            }
                                            responseItem.setResponseFiles(fileList);
                                        }

                                        responseList.add(responseItem);

                                    }

                                    dbHandler.addResponses(responseList);
                                    EventBus.getDefault().post(new ResponseListSyncEvent(responseList));
                                }

                                if (currentPage < totalPage) {
                                    getResponses(TAG, formId, ++currentPage, forms);
                                } else {
                                    forms[0] -= 1;
                                }

                            } else {
                                Log.e(TAG, "onResponse: " + message);
                                forms[0] -= 1;
                            }

                            if (forms[0] == 0) {
                                Safra.isResponseSyncing = false;
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            forms[0] -= 1;

                            if (forms[0] == 0) {
                                Safra.isResponseSyncing = false;
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                        forms[0] -= 1;

                        if (forms[0] == 0) {
                            Safra.isResponseSyncing = false;
                        }
                    }
                });
    }

    public static void syncTemplates(String TAG, long languageId, int pageNumber) {
        Log.e("SYNC_API","TEMPLATE_LIST_API");
        AndroidNetworking
                .post(BASE_URL + TEMPLATE_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNumber))
                .addBodyParameter("language_id", String.valueOf(languageId))
                .setTag("template-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray templates = data.getJSONArray("template_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (templates.length() > 0) {
                                    List<TemplateItem> templateList = new ArrayList<>();
                                    for (int i = 0; i < templates.length(); i++) {
                                        JSONObject template = templates.getJSONObject(i);
                                        TemplateItem templateItem = new TemplateItem();
                                        templateItem.setTemplateId(template.getInt("template_id"));
                                        templateItem.setTemplateUniqueId(template.getString("template_unique_id"));
                                        templateItem.setTemplateName(template.getString("template_name"));
                                        templateItem.setTemplateType(template.getInt("template_type"));
                                        templateItem.setTemplateLanguageId(template.getLong("template_language_id"));

                                        templateItem.setTemplateJson(new JSONArray(template.getString("template_json")).toString());

                                        templateItem.setTemplateImage(template.getString("template_image_url"));

                                        templateList.add(templateItem);
                                    }

                                    dbHandler.addTemplates(templateList);
                                    EventBus.getDefault().post(new TemplateListSyncEvent(templateList));
                                }

                                if (currentPage < totalPage) {
                                    syncTemplates(TAG, languageId, ++currentPage);
                                } else {
                                    Safra.isTemplateSyncing = false;
                                    Log.e(TAG, "syncTemplates: onResponse: end template syncing...");
                                }
                            } else {
                                Log.e(TAG, "syncTemplates: onResponse: " + message);
                                Safra.isTemplateSyncing = false;
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "syncTemplates: onResponse: " + e.getLocalizedMessage());
                            Safra.isTemplateSyncing = false;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "syncTemplates: onError: " + anError.getErrorCode());
                        Log.e(TAG, "syncTemplates: onError: " + anError.getErrorDetail());
                        Log.e(TAG, "syncTemplates: onError: " + anError.getErrorBody());
                        Safra.isTemplateSyncing = false;
                    }
                });
    }
}
