package com.safra.utilities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.Safra;
import com.safra.events.FormTypesReceivedEvent;
import com.safra.events.LanguagesReceivedEvent;
import com.safra.events.ProfileReceivedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.interfaces.OnRoleListReceive;
import com.safra.models.AccessItem;
import com.safra.models.FileItem;
import com.safra.models.FormTypeItem;
import com.safra.models.LanguageItem;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.ResponseItem;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_FILL_PRIVATE_API;
import static com.safra.utilities.Common.FORM_TYPE_LIST;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.LANGUAGE_LIST_API;
import static com.safra.utilities.Common.PERMISSION_LIST_API;
import static com.safra.utilities.Common.USER_PROFILE_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class GeneralApiCallAndUse {

    public static void getLanguages(Context context, String TAG) {
        List<LanguageItem> languageList = new ArrayList<>();
        AndroidNetworking
                .post(BASE_URL + LANGUAGE_LIST_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("language-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONArray languages = response.getJSONObject("data").getJSONArray("language_list");

                                if (languages.length() > 0) {
                                    for (int i = 0; i < languages.length(); i++) {
                                        JSONObject language = languages.getJSONObject(i);
                                        LanguageItem languageItem = new LanguageItem();
                                        languageItem.setLanguageId(language.getLong("language_id"));
                                        languageItem.setLanguageName(GeneralExtension
                                                .capitalizeString(language.getString("language_title")));
                                        languageItem.setLanguageSlug(language.getString("language_slug"));
                                        languageItem.setLangFileUrl(language.getString("language_url"));

                                        languageList.add(languageItem);
                                    }

                                    dbHandler.addLanguages(languageList);
                                }

                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        EventBus.getDefault().post(new LanguagesReceivedEvent(languageList));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });

    }

    public static void getFormTypes(Context context, String TAG) {
        List<FormTypeItem> formTypeList = new ArrayList<>();
        AndroidNetworking
                .post(BASE_URL + FORM_TYPE_LIST)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("language-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONArray formTypes = response.getJSONObject("data").getJSONArray("form_list");
                                if (formTypes.length() > 0) {
                                    for (int i = 0; i < formTypes.length(); i++) {
                                        JSONObject formType = formTypes.getJSONObject(i);
                                        FormTypeItem formTypeItem = new FormTypeItem();
                                        formTypeItem.setTypeId(formType.getLong("value"));
                                        formTypeItem.setTypeName(formType.getString("name"));
                                        if (formType.has("pt_name"))
                                            formTypeItem.setPtTypeName(formType.getString("pt_name"));
                                        else
                                            formTypeItem.setPtTypeName(formType.getString("name"));

                                        formTypeList.add(formTypeItem);
                                    }

                                    dbHandler.addFormTypes(formTypeList);
                                }

                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        EventBus.getDefault().post(new FormTypesReceivedEvent(formTypeList));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    public static void getModulesAndPermissions(Context context, String TAG, String userToken) {
        List<ModuleItem> moduleList = new ArrayList<>();
        AndroidNetworking
                .post(BASE_URL + PERMISSION_LIST_API)
                .addBodyParameter("user_token", userToken)
                .setTag("module-permission-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONArray modules = response.getJSONObject("data").getJSONArray("modules");
                                if (modules.length() > 0) {
                                    moduleList.clear();

                                    for (int i = 0; i < modules.length(); i++) {
                                        JSONObject module = modules.getJSONObject(i);
                                        ModuleItem moduleItem = new ModuleItem();
                                        moduleItem.setModuleId(module.getLong("module_id"));
                                        moduleItem.setModuleName(module.getString("module_name"));
                                        if (module.has("pt_module_name"))
                                            moduleItem.setPtModuleName(module.getString("pt_module_name"));

                                        JSONArray permissions = module.getJSONArray("permissions");
                                        if (permissions.length() > 0) {
                                            List<PermissionItem> permissionList = new ArrayList<>();
                                            for (int j = 0; j < permissions.length(); j++) {
                                                JSONObject permission = permissions.getJSONObject(j);
                                                PermissionItem permissionItem = new PermissionItem();
                                                permissionItem.setPermissionId(permission.getLong("permission_id"));
                                                permissionItem.setPermissionName(permission.getString("permission_name"));
                                                if (permission.has("pt_permission_name"))
                                                    permissionItem.setPtPermissionName(permission.getString("pt_permission_name"));

                                                permissionList.add(permissionItem);
                                            }

                                            moduleItem.setPermissionList(permissionList);
                                        }

                                        moduleList.add(moduleItem);
                                    }

                                    dbHandler.addModules(moduleList);
                                }
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });

    }

    public static void getRoles(Context context, String TAG, String userToken, OnRoleListReceive receiver) {
        List<AccessItem> roleList = new ArrayList<>();
        AndroidNetworking
//                .post(BASE_URL + ROLE_LIST_API)
                .post(BASE_URL + GROUP_LIST_API)
                .addBodyParameter("user_token", userToken)
                .setTag("role-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONArray roles = response.getJSONObject("data").getJSONArray("role_list");

                                if (roles.length() > 0) {
                                    for (int i = 0; i < roles.length(); i++) {
                                        JSONObject role = roles.getJSONObject(i);
                                        AccessItem accessItem = new AccessItem();
                                        accessItem.setAccessId(role.getLong("role_id"));
                                        accessItem.setAccessName(GeneralExtension
                                                .capitalizeString(role.getString("role_name")));

                                        roleList.add(accessItem);
                                    }
                                }

                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        receiver.getRoles(roleList);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        receiver.getRoles(roleList);
                    }
                });
    }

    public static void getUserProfile(Context context, String TAG, String userToken) {
        AndroidNetworking
                .post(BASE_URL + USER_PROFILE_API)
                .addBodyParameter("user_token", userToken)
                .setTag("user-account-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject userProfile = response.getJSONObject("data").getJSONObject("user_profile");
                                String uName = userProfile.getString("user_name");
                                String uImage = userProfile.getString("user_image_url");
                                String uPhone = userProfile.getString("user_phone_no");

                                JSONArray userAccess = userProfile.getJSONArray("user_access");
                                Bundle bundle = PermissionExtension.makePermissionJsonToList(userAccess);

                                boolean rememberUser = userSessionManager.isRemembered();
                                if (rememberUser) {
                                    userSessionManager.updateUserProfile(uName, uPhone, uImage, bundle.getStringArrayList("permission_list"));
                                } else {
                                    Safra.updateUserProfile(uName, uPhone, uImage);
                                }
                                Safra.setPermissionList(bundle.getStringArrayList("permission_list"));

                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        EventBus.getDefault().post(new ProfileReceivedEvent());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                    }
                });
    }

}
