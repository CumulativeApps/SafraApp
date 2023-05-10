package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PERMISSION_LIST_API;
import static com.safra.utilities.Common.USER_SAVE_API;
import static com.safra.utilities.Common.USER_VIEW_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.adapters.GroupSpinnerAdapter;
import com.safra.adapters.ModuleRecyclerAdapter;
import com.safra.databinding.ActivityAddUserBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.events.UserAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.RoleItem;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddUser extends AppCompatActivity {

    public static final String TAG = "add_user_activity";

    private ActivityAddUserBinding binding;

    private final List<RoleItem> groupList = new ArrayList<>();
    private GroupSpinnerAdapter adapterG;

    private final List<ModuleItem> moduleList = new ArrayList<>();
    private ModuleRecyclerAdapter adapterM;

    private boolean isRemembered;

    private long selectGroupId = -1;

    private boolean isNew;
    private long userId = -1, onlineId = -1;
    private UserItem userItem = null;
    private List<Long> currentPermissions = new ArrayList<>();

    private boolean isGroupsReceived = false, isPermissionReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        if (getIntent() != null) {
            binding.tvAddUserHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {
                    getPermissions();
                    getGroups(PAGE_START);
                } else {
                    moduleList.addAll(dbHandler.getModules());
                    RoleItem roleItem = new RoleItem();
                    roleItem.setRoleId(-1);
                    roleItem.setRoleName(LanguageExtension.setText("select_group", getString(R.string.select_group)));
                    groupList.add(roleItem);
                    groupList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));
                }
            } else {
                userId = getIntent().getLongExtra("user_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                Log.e(TAG, "onCreate: " + userId);
                if (ConnectivityReceiver.isConnected())
                    getEditData();
//                    getEditDataFromDB();
                else
                    getEditDataFromDB();
            }
        }

        adapterG = new GroupSpinnerAdapter(this, groupList);
        binding.spnUserGroup.setAdapter(adapterG);

        if (userItem != null)
            binding.spnUserGroup.setSelection(adapterG.getPosition(userItem.getRoleId()));

        binding.rvModule.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvModule.addItemDecoration(new SpaceItemDecoration(this, RecyclerView.VERTICAL,
                1, R.dimen._2dp, R.dimen._0dp, false));
        adapterM = new ModuleRecyclerAdapter(this, moduleList, true);
        binding.rvModule.setAdapter(adapterM);

        binding.spnUserGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectGroupId = ((RoleItem) parent.getSelectedItem()).getRoleOnlineId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnSave.setOnClickListener(v -> {
            validateInputs();
//            Log.e(TAG, "onCreate: module_ids -> " + hashMap.get("module_ids"));
//            Log.e(TAG, "onCreate: permission_ids -> " + hashMap.get("permission_ids"));
        });
    }

    private void setText() {
        binding.tvUserNameTitle.setText(LanguageExtension.setText("enter_user_s_name_mandatory", getString(R.string.enter_user_s_name_mandatory)));
        binding.tvPhoneTitle.setText(LanguageExtension.setText("enter_phone_number_mandatory", getString(R.string.enter_phone_number_mandatory)));
        binding.tvEmailTitle.setText(LanguageExtension.setText("enter_email_id_mandatory", getString(R.string.enter_email_id_mandatory)));
        binding.tvPasswordTitle.setText(LanguageExtension.setText("enter_password_mandatory", getString(R.string.enter_password_mandatory)));
        binding.tvUserGroupTitle.setText(LanguageExtension.setText("select_user_group", getString(R.string.select_user_group)));
        binding.tvPermissionsTitle.setText(LanguageExtension.setText("permissions", getString(R.string.permissions)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void getEditDataFromDB() {
        userItem = dbHandler.getUserDetails(userId);
        setUserDetails();
        setPermissions();

        Log.e(TAG, "getEditDataFromDB: " + userItem.getRoleId());

        groupList.clear();
        RoleItem roleItem = new RoleItem();
        roleItem.setRoleId(-1);
        roleItem.setRoleName(LanguageExtension.setText("select_group", getString(R.string.select_group)));
        groupList.add(roleItem);
        groupList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));

        if (adapterG != null)
            binding.spnUserGroup.setSelection(adapterG.getPosition(userItem.getRoleId()));
    }

    private void setPermissions() {
        moduleList.clear();
        moduleList.addAll(dbHandler.getModules());

        currentPermissions = GeneralExtension.toLongList(userItem.getPermissionIds());

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
                .post(BASE_URL + USER_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("user_id", String.valueOf(onlineId))
                .setTag("user-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject userData = response.getJSONObject("data").getJSONObject("user_data");
                                userItem = new UserItem();
                                userItem.setUserOnlineId(userData.getLong("user_id"));
                                userItem.setUserName(userData.getString("user_name"));

                                if (!userData.isNull("user_phone_no"))
                                    userItem.setUserPhone(userData.getString("user_phone_no"));

                                if (!userData.isNull("user_email"))
                                    userItem.setUserEmail(userData.getString("user_email"));

                                if (!userData.isNull("role_id"))
                                    userItem.setRoleId(userData.getLong("role_id"));

                                if (!userData.isNull("user_permission_ids")) {
                                    currentPermissions = GeneralExtension.toLongList(
                                            userData.getString("user_permission_ids"), ",");
//                                    currentPermissions = new ArrayList<>(Arrays.asList(permissions));
                                }

                                setUserDetails();

                                getGroups(PAGE_START);
                                getPermissions();

                            } else {
                                Toast.makeText(AddUser.this, message, Toast.LENGTH_SHORT).show();
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

//                        dialogL.dismiss();
                    }
                });
    }

    private void setUserDetails() {
        userId = userItem.getUserId();
        onlineId = userItem.getUserOnlineId();
        binding.etUserName.setText(userItem.getUserName());
        if (userItem.getUserPhone() != null)
            binding.etPhone.setText(userItem.getUserPhone());
        if (userItem.getUserEmail() != null)
            binding.etEmail.setText(userItem.getUserEmail());
    }

    private void validateInputs() {
        String uName = binding.etUserName.getText() != null ? binding.etUserName.getText().toString() : "";
        String pNo = binding.etPhone.getText() != null ? binding.etPhone.getText().toString() : "";
        String eMail = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
        String pWord = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

        HashMap<String, String> hashMap = adapterM.getSelectedModulesAndProperties();
        String moduleIds = hashMap.get("module_ids");
        String permissionIds = hashMap.get("permission_ids");

        if (uName.isEmpty() || pNo.isEmpty() || eMail.isEmpty() ||
                moduleIds.isEmpty() || permissionIds.isEmpty()) {
            if (permissionIds.isEmpty() || moduleIds.isEmpty()) {
                Toast.makeText(this,
                        LanguageExtension.setText("select_at_least_one_permission", getString(R.string.select_at_least_one_permission)),
                        Toast.LENGTH_SHORT).show();
            }

            if (eMail.isEmpty()) {
                binding.etEmail.setError(LanguageExtension.setText("enter_email", getString(R.string.enter_email)));
                binding.etEmail.requestFocus();
            }
            if (pNo.isEmpty()) {
                binding.etPhone.setError(LanguageExtension.setText("enter_phone_number", getString(R.string.enter_phone_number)));
                binding.etPhone.requestFocus();
            }
            if (uName.isEmpty()) {
                binding.etUserName.setError(LanguageExtension.setText("enter_user_name", getString(R.string.enter_user_name)));
                binding.etUserName.requestFocus();
            }
        } else if (isNew && pWord.isEmpty()) {
            binding.etPassword.setError(LanguageExtension.setText("enter_password", getString(R.string.enter_password)));
            binding.etPassword.requestFocus();
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("user_name", uName);
                parameters.put("user_email", eMail);
                parameters.put("user_phone_no", pNo);
                parameters.put("user_module_ids", moduleIds);
                parameters.put("user_permission_ids", permissionIds);
                if (selectGroupId != -1)
                    parameters.put("user_role_id", String.valueOf(selectGroupId));
                if (!pWord.isEmpty())
                    parameters.put("user_password", pWord);
                if (isNew) {
                    saveUser(parameters);
                } else {
                    parameters.put("user_id", String.valueOf(onlineId));
                    editUser(parameters);
                }
            } else {
            if (userItem == null)
                userItem = new UserItem();

            userItem.setUserName(uName);
            userItem.setUserEmail(eMail);
            userItem.setUserPhone(pNo);
            userItem.setModuleIds(GeneralExtension.toLongArray(moduleIds, ","));
            userItem.setPermissionIds(GeneralExtension.toLongArray(permissionIds, ","));
            if (selectGroupId != -1)
                userItem.setRoleId(selectGroupId);
            if (!pWord.isEmpty())
                userItem.setUserPassword(pWord);
            if (isNew) {
                userItem.setUserAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                long i = dbHandler.addUserOffline(userItem);
                if (i > 0) {
                    EventBus.getDefault().post(new UserAddedEvent());
//                        setResult(RESULT_SUCCESS_EDIT_USER);
                    finish();
                }
            } else {
                userItem.setUserId(userId);
                userItem.setUserOnlineId(onlineId);
                long i = dbHandler.updateUserOffline(userItem);
                if (i > 0) {
                    EventBus.getDefault().post(new UserAddedEvent());
//                        setResult(RESULT_SUCCESS_EDIT_USER);
                    finish();
                }
            }
        }
    }
    }

    private void getPermissions() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
        isPermissionReceived = false;
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
                                Toast.makeText(AddUser.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isPermissionReceived = true;
                        if (isGroupsReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        isPermissionReceived = true;
                        if (isGroupsReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void getGroups(int pageNo) {
        if (pageNo == PAGE_START) {
            LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
            isGroupsReceived = false;
        }
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + GROUP_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNo))
                .setTag("group-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray roles = data.getJSONArray("role_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    groupList.clear();
                                    RoleItem roleItem = new RoleItem();
                                    roleItem.setRoleId(-1);
                                    roleItem.setRoleName(LanguageExtension.setText("select_group", getString(R.string.select_group)));
                                    groupList.add(roleItem);
                                }

                                if (roles.length() > 0) {
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

                                        groupList.add(roleItem);
                                        dbHandler.addGroup(roleItem);
                                    }
                                }

                                adapterG.notifyDataSetChanged();

                                if (userItem != null) {
                                    binding.spnUserGroup.setSelection(adapterG.getPosition(userItem.getRoleId()));
                                }

                                if (currentPage < totalPage) {
                                    getGroups(++currentPage);
                                } else {
                                    isGroupsReceived = true;
                                    if (isPermissionReceived)
                                        LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(AddUser.this, message, Toast.LENGTH_SHORT).show();
                                isGroupsReceived = true;
                                if (isPermissionReceived)
                                    LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isGroupsReceived = true;
                            if (isPermissionReceived)
                                LoadingDialogExtension.hideLoading();
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        isGroupsReceived = true;
                        if (isPermissionReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void saveUser(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_user_progress", getString(R.string.saving_user_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_user_progress", getString(R.string.saving_user_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + USER_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("save-user-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddUser.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new UserAddedEvent());
//                                setResult(RESULT_SUCCESS_EDIT_USER);
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

    private void editUser(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_user_progress", getString(R.string.updating_user_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_user_progress", getString(R.string.updating_user_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + USER_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("edit-user-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddUser.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new UserAddedEvent());
//                                setResult(RESULT_SUCCESS_EDIT_USER);
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