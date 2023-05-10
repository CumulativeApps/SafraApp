package com.safra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.adapters.ModuleRecyclerAdapter;
import com.safra.databinding.ActivityAddGroupBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.events.GroupAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.RoleItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.GROUP_SAVE_API;
import static com.safra.utilities.Common.GROUP_VIEW_API;
import static com.safra.utilities.Common.PERMISSION_LIST_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class AddGroup extends AppCompatActivity {

    public static final String TAG = "add_group_activity";

    private ActivityAddGroupBinding binding;

    private final List<ModuleItem> moduleList = new ArrayList<>();
    private ModuleRecyclerAdapter adapterM;

    private boolean isRemembered;

    private boolean isNew;
    private long roleId = -1, onlineId = -1;
    private RoleItem roleItem = null;
    private final List<Long> currentPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        if (getIntent() != null) {
            binding.tvAddGroupHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected())
                    getPermissions();
                else
                    moduleList.addAll(dbHandler.getModules());
            } else {
                roleId = getIntent().getLongExtra("role_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                Log.e(TAG, "onCreate: " + roleId);
                if (ConnectivityReceiver.isConnected())
                    getEditData();
                else
                    getEditDataFromDB();
            }
        }

        binding.rvModule.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvModule.addItemDecoration(new SpaceItemDecoration(this, RecyclerView.VERTICAL,
                1, R.dimen._2dp, R.dimen._0dp, false));
        adapterM = new ModuleRecyclerAdapter(this, moduleList, true);
        binding.rvModule.setAdapter(adapterM);

        binding.btnSave.setOnClickListener(v -> {
            validateInputs();
//            Log.e(TAG, "onCreate: module_ids -> " + hashMap.get("module_ids"));
//            Log.e(TAG, "onCreate: permission_ids -> " + hashMap.get("permission_ids"));
        });
    }

    private void setText() {
        binding.tvRoleNameTitle.setText(LanguageExtension.setText("enter_role_name_mandatory", getString(R.string.enter_role_name_mandatory)));
        binding.tvPermissionsTitle.setText(LanguageExtension.setText("permissions_mandatory", getString(R.string.permissions_mandatory)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void getEditDataFromDB() {
        roleItem = dbHandler.getGroupDetails(roleId);
        setGroupDetails();
        setPermissions();
    }

    private void setPermissions() {
        moduleList.clear();
        moduleList.addAll(dbHandler.getModules());

        currentPermissions.clear();
        currentPermissions.addAll(Arrays.asList(roleItem.getPermissionIds()));

        Log.e(TAG, "setPermissions: " + currentPermissions.size());

        for (ModuleItem mi : moduleList) {
            for (PermissionItem pi : mi.getPermissionList()) {
                if (currentPermissions.size() > 0)
                    pi.setSelected(currentPermissions.contains(pi.getPermissionId()));
            }
        }

        if (adapterM != null)
            adapterM.notifyDataSetChanged();
    }

    private void getEditData() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + GROUP_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("role_id", String.valueOf(onlineId))
                .setTag("role-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject roleData = response.getJSONObject("data").getJSONObject("role_data");
                                roleItem = new RoleItem();
                                roleItem.setRoleOnlineId(roleData.getLong("role_id"));
                                roleItem.setRoleName(roleData.getString("role_name"));
                                roleItem.setAddedBy(roleData.getLong("added_by"));

                                JSONArray modules = roleData.getJSONArray("moduless");
                                if (modules.length() > 0) {
                                    currentPermissions.clear();

                                    for (int i = 0; i < modules.length(); i++) {
                                        JSONObject module = modules.getJSONObject(i);
                                        JSONArray permissions = module.getJSONArray("permissions");
                                        if (permissions.length() > 0) {
                                            for (int j = 0; j < permissions.length(); j++) {
                                                JSONObject permission = permissions.getJSONObject(j);
                                                currentPermissions.add(permission.getLong("permission_id"));
                                            }
                                        }
                                    }
                                }

                                setGroupDetails();

                                getPermissions();

                            } else {
                                Toast.makeText(AddGroup.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void setGroupDetails() {
//        roleId = roleItem.getRoleId();
        onlineId = roleItem.getRoleOnlineId();
        binding.etRoleName.setText(roleItem.getRoleName());
    }

    private void validateInputs() {
        String rName = binding.etRoleName.getText() != null ? binding.etRoleName.getText().toString() : "";

        HashMap<String, String> hashMap = adapterM.getSelectedModulesAndProperties();
        String moduleIds = hashMap.containsKey("module_ids") ? hashMap.get("module_ids") : "";
        String permissionIds = hashMap.containsKey("permission_ids") ? hashMap.get("permission_ids") : "";

        if (rName.isEmpty() || moduleIds.isEmpty() || permissionIds.isEmpty()) {
            if (permissionIds.isEmpty() || moduleIds.isEmpty()) {
                Toast.makeText(this,
                        LanguageExtension.setText("select_at_least_one_permission", getString(R.string.select_at_least_one_permission)),
                        Toast.LENGTH_SHORT).show();
            }

            if (rName.isEmpty()) {
                binding.etRoleName.setError(LanguageExtension.setText("enter_role_name", getString(R.string.enter_role_name)));
                binding.etRoleName.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("role_name", rName);
                parameters.put("role_module_ids", moduleIds);
                parameters.put("role_permission_ids", permissionIds);
                if (isNew) {
                    saveGroup(parameters);
                } else {
                    parameters.put("role_id", String.valueOf(onlineId));
                    editGroup(parameters);
                }
            } else {
                if (roleItem == null)
                    roleItem = new RoleItem();

                roleItem.setRoleName(rName);
                roleItem.setModuleIds(GeneralExtension.toLongArray(moduleIds, ","));
                roleItem.setPermissionIds(GeneralExtension.toLongArray(permissionIds, ","));
                if (isNew) {
                    roleItem.setAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                    long i = dbHandler.addGroupOffline(roleItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new GroupAddedEvent());
                        finish();
                    }
                } else {
                    roleItem.setRoleId(roleId);
                    roleItem.setRoleOnlineId(onlineId);
                    long i = dbHandler.updateGroupOffline(roleItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new GroupAddedEvent());
                        finish();
                    }
                }
            }
        }
    }

    private void getPermissions() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PERMISSION_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("permission-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
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

                                                if (currentPermissions.size() > 0)
                                                    permissionItem.setSelected(currentPermissions
                                                            .contains(permissionItem.getPermissionId()));

                                                permissionList.add(permissionItem);
                                            }

                                            moduleItem.setPermissionList(permissionList);
                                        }

                                        moduleList.add(moduleItem);
                                    }

                                    adapterM.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(AddGroup.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void saveGroup(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_group_progress", getString(R.string.saving_group_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_group_progress", getString(R.string.saving_group_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + GROUP_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("save-group-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddGroup.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new GroupAddedEvent());
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void editGroup(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_group_progress", getString(R.string.updating_group_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_group_progress", getString(R.string.updating_group_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + GROUP_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("edit-group-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddGroup.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new GroupAddedEvent());
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }
}