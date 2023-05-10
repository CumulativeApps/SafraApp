package com.safra.extensions;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.safra.Safra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.safra.utilities.UserPermissions.FORM_ADD;
import static com.safra.utilities.UserPermissions.FORM_ASSIGN;
import static com.safra.utilities.UserPermissions.FORM_LIST;
import static com.safra.utilities.UserPermissions.FORM_RESPONSES;
import static com.safra.utilities.UserPermissions.FORM_STATUS;
import static com.safra.utilities.UserPermissions.FORM_SUBMIT;
import static com.safra.utilities.UserPermissions.FORM_UPDATE;
import static com.safra.utilities.UserPermissions.GROUP_ADD;
import static com.safra.utilities.UserPermissions.GROUP_ASSIGN;
import static com.safra.utilities.UserPermissions.GROUP_DELETE;
import static com.safra.utilities.UserPermissions.GROUP_LIST;
import static com.safra.utilities.UserPermissions.GROUP_UPDATE;
import static com.safra.utilities.UserPermissions.REPORT_LIST;
import static com.safra.utilities.UserPermissions.REPORT_VIEW;
import static com.safra.utilities.UserPermissions.TASK_ADD;
import static com.safra.utilities.UserPermissions.TASK_ASSIGN;
import static com.safra.utilities.UserPermissions.TASK_DELETE;
import static com.safra.utilities.UserPermissions.TASK_LIST;
import static com.safra.utilities.UserPermissions.TASK_STATUS;
import static com.safra.utilities.UserPermissions.TASK_UPDATE;
import static com.safra.utilities.UserPermissions.TEMPLATE_USE;
import static com.safra.utilities.UserPermissions.USER_ADD;
import static com.safra.utilities.UserPermissions.USER_ASSIGN;
import static com.safra.utilities.UserPermissions.USER_DELETE;
import static com.safra.utilities.UserPermissions.USER_LIST;
import static com.safra.utilities.UserPermissions.USER_STATUS;
import static com.safra.utilities.UserPermissions.USER_UPDATE;
import static com.safra.utilities.UserPermissions.USER_VIEW;

public class PermissionExtension {

    /**
     * Convert JSONArray of permission to list of permissions
     * @param jsonArray - JSONArray of permissions
     * @return - List of permissions
     * @throws JSONException - if invalid JSONArray
     */
    public static Bundle makePermissionJsonToList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> permissionList = new ArrayList<>();
        ArrayList<Long> moduleIds = new ArrayList<>();
        ArrayList<Long> permissionIds = new ArrayList<>();

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject module = jsonArray.getJSONObject(i);
                moduleIds.add(module.getLong("module_id"));
                JSONArray permissions = module.getJSONArray("permissions");
                switch (module.getString("module_name")){
                    case "user":
                        if(permissions.length() > 0){
                            for(int j=0; j<permissions.length();j++){
                                JSONObject permission = permissions.getJSONObject(j);
                                permissionIds.add(permission.getLong("permission_id"));
                                switch (permission.getString("permission_name")){
                                    case "view":
                                        permissionList.add(USER_VIEW);
                                        break;
                                    case "update":
                                        permissionList.add(USER_UPDATE);
                                        break;
                                    case "add":
                                        permissionList.add(USER_ADD);
                                        break;
                                    case "delete":
                                        permissionList.add(USER_DELETE);
                                        break;
                                    case "list":
                                        permissionList.add(USER_LIST);
                                        break;
                                    case "status":
                                        permissionList.add(USER_STATUS);
                                        break;
                                    case "assign":
                                        permissionList.add(USER_ASSIGN);
                                        break;
                                }
                            }
                        }
                        break;
                    case "form":
                        if(permissions.length() > 0){
                            for(int j=0; j<permissions.length();j++){
                                JSONObject permission = permissions.getJSONObject(j);
                                permissionIds.add(permission.getLong("permission_id"));
                                switch (permission.getString("permission_name")){
                                    case "list":
                                        permissionList.add(FORM_LIST);
                                        break;
                                    case "add":
                                        permissionList.add(FORM_ADD);
                                        break;
                                    case "update":
                                        permissionList.add(FORM_UPDATE);
                                        break;
                                    case "submit":
                                        permissionList.add(FORM_SUBMIT);
                                        break;
                                    case "responses":
                                        permissionList.add(FORM_RESPONSES);
                                        break;
                                    case "status":
                                        permissionList.add(FORM_STATUS);
                                        break;
                                    case "assign":
                                        permissionList.add(FORM_ASSIGN);
                                        break;
                                }
                            }
                        }
                        break;
                    case "task":
                        if(permissions.length() > 0){
                            for(int j=0; j<permissions.length();j++){
                                JSONObject permission = permissions.getJSONObject(j);
                                permissionIds.add(permission.getLong("permission_id"));
                                switch (permission.getString("permission_name")){
                                    case "list":
                                        permissionList.add(TASK_LIST);
                                        break;
                                    case "add":
                                        permissionList.add(TASK_ADD);
                                        break;
                                    case "update":
                                        permissionList.add(TASK_UPDATE);
                                        break;
                                    case "delete":
                                        permissionList.add(TASK_DELETE);
                                        break;
                                    case "status":
                                        permissionList.add(TASK_STATUS);
                                        break;
                                    case "assign":
                                        permissionList.add(TASK_ASSIGN);
                                        break;
                                }
                            }
                        }
                        break;
                    case "report":
                        if(permissions.length() > 0){
                            for(int j=0; j<permissions.length();j++){
                                JSONObject permission = permissions.getJSONObject(j);
                                permissionIds.add(permission.getLong("permission_id"));
                                switch (permission.getString("permission_name")){
                                    case "list":
                                        permissionList.add(REPORT_LIST);
                                        break;
                                    case "view":
                                        permissionList.add(REPORT_VIEW);
                                        break;
                                }
                            }
                        }
                        break;
                    case "group":
                        if(permissions.length() > 0){
                            for(int j=0; j<permissions.length();j++){
                                JSONObject permission = permissions.getJSONObject(j);
                                permissionIds.add(permission.getLong("permission_id"));
                                switch (permission.getString("permission_name")){
                                    case "list":
                                        permissionList.add(GROUP_LIST);
                                        break;
                                    case "add":
                                        permissionList.add(GROUP_ADD);
                                        break;
                                    case "update":
                                        permissionList.add(GROUP_UPDATE);
                                        break;
                                    case "delete":
                                        permissionList.add(GROUP_DELETE);
                                        break;
                                    case "assign":
                                        permissionList.add(GROUP_ASSIGN);
                                        break;
                                }
                            }
                        }
                        break;
                    case "template":
                        if(permissions.length() > 0){
                            for(int j=0; j<permissions.length();j++){
                                JSONObject permission = permissions.getJSONObject(j);
                                permissionIds.add(permission.getLong("permission_id"));
                                if ("use".equals(permission.getString("permission_name"))) {
                                    permissionList.add(TEMPLATE_USE);
                                }
                            }
                        }
                        break;
                }
            }
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("permission_list", permissionList);
        bundle.putString("module_ids", GeneralExtension.toString(moduleIds));
        bundle.putString("permission_ids", GeneralExtension.toString(permissionIds));

        return bundle;
    }

    public static boolean checkForPermission(String permissionName){
        return Safra.permissionList.contains(permissionName);
    }

}
