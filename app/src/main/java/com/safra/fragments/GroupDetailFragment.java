package com.safra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ModuleRecyclerAdapter;
import com.safra.databinding.FragmentGroupDetailBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.RoleItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.GROUP_VIEW_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class GroupDetailFragment extends DialogFragment {

    public static final String TAG = "group_detail_dialog";
    private static final int LOADING_CONDITION = 0;
    private static final int SUCCESS_CONDITION = 1;
    private static final int ERROR_CONDITION = 2;

    private FragmentActivity mActivity = null;

    private FragmentGroupDetailBinding binding;

    private final List<ModuleItem> moduleList = new ArrayList<>();
    private ModuleRecyclerAdapter adapter;

    private boolean isRemembered;

    private long roleId, onlineId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGroupDetailBinding.inflate(inflater, container, false);

        binding.ivClose.setOnClickListener(v -> dismiss());

        isRemembered = userSessionManager.isRemembered();

        binding.tvGroupInfoTitle.setText(LanguageExtension.setText("group_information", getString(R.string.group_information)));
        binding.tvLoading.setText(LanguageExtension.setText("loading_content", getString(R.string.loading_content)));
        binding.tvNoData.setText(LanguageExtension.setText("no_data_found", getString(R.string.no_data_found)));
        binding.tvRoleNameTitle.setText(LanguageExtension.setText("group_name", getString(R.string.group_name)));
        binding.tvPermissionsTitle.setText(LanguageExtension.setText("permissions", getString(R.string.permissions)));

        if (getArguments() != null) {
            roleId = getArguments().getLong("role_id");
            onlineId = getArguments().getLong("online_id");
        }

        binding.rvModule.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvModule.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen._2dp, R.dimen._0dp, false));
        adapter = new ModuleRecyclerAdapter(mActivity, moduleList, false);
        binding.rvModule.setAdapter(adapter);

//        if (ConnectivityReceiver.isConnected())
//            getGroupDetails();
//        else
        getGroupDetailsFromDB();

        return binding.getRoot();
    }

    private void getGroupDetailsFromDB() {
        showLayout(LOADING_CONDITION);

        RoleItem roleItem = dbHandler.getGroupDetails(roleId);
        setGroupDetails(roleItem);

        List<Long> moduleIds = Arrays.asList(roleItem.getModuleIds());
        List<Long> permissionIds = Arrays.asList(roleItem.getPermissionIds());
        for (ModuleItem mi : dbHandler.getModules()) {
            if (moduleIds.contains(mi.getModuleId())) {
                ModuleItem moduleItem = new ModuleItem();
                moduleItem.setModuleId(mi.getModuleId());
                moduleItem.setModuleName(mi.getModuleName());
                moduleItem.setPtModuleName(mi.getPtModuleName());
                List<PermissionItem> permissionList = new ArrayList<>();
                for (PermissionItem pi : mi.getPermissionList()) {
                    if (permissionIds.contains(pi.getPermissionId())) {
                        permissionList.add(pi);
                    }
                }
                moduleItem.setPermissionList(permissionList);

                moduleList.add(moduleItem);
            }
        }
        adapter.notifyDataSetChanged();

        showLayout(SUCCESS_CONDITION);
    }

    private void getGroupDetails() {
        showLayout(LOADING_CONDITION);

        AndroidNetworking
                .post(BASE_URL + GROUP_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("role_id", String.valueOf(onlineId))
                .setTag("group-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject roleData = response.getJSONObject("data").getJSONObject("role_data");
                                RoleItem roleItem = new RoleItem();
                                roleItem.setRoleOnlineId(roleData.getLong("role_id"));
                                roleItem.setRoleName(roleData.getString("role_name"));

                                JSONArray modules = roleData.getJSONArray("moduless");
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

                                    adapter.notifyDataSetChanged();

                                }
                                setGroupDetails(roleItem);

                                showLayout(SUCCESS_CONDITION);
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                                showLayout(ERROR_CONDITION);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());

                            showLayout(ERROR_CONDITION);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        showLayout(ERROR_CONDITION);
                    }
                });
    }

    private void setGroupDetails(RoleItem roleItem) {
        binding.tvRoleName.setText(roleItem.getRoleName());
    }

    private void showLayout(int condition) {
        switch (condition) {
            case LOADING_CONDITION:
                binding.clProgress.setVisibility(View.VISIBLE);
                binding.clNoData.setVisibility(View.GONE);
                binding.clData.setVisibility(View.GONE);
                break;
            case SUCCESS_CONDITION:
                binding.clProgress.setVisibility(View.GONE);
                binding.clNoData.setVisibility(View.GONE);
                binding.clData.setVisibility(View.VISIBLE);
                break;
            case ERROR_CONDITION:
                binding.clProgress.setVisibility(View.GONE);
                binding.clNoData.setVisibility(View.VISIBLE);
                binding.clData.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
