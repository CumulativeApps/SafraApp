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
import com.bumptech.glide.Glide;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ModuleRecyclerAdapter;
import com.safra.databinding.FragmentUserDetailBinding;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.UserItem;
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
import static com.safra.utilities.Common.USER_VIEW_API;
import static com.safra.utilities.LanguageManager.languageManager;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class UserDetailFragment extends DialogFragment {

    public static final String TAG = "user_detail_dialog";
    private static final int LOADING_CONDITION = 0;
    private static final int SUCCESS_CONDITION = 1;
    private static final int ERROR_CONDITION = 2;

    private FragmentActivity mActivity = null;

    private FragmentUserDetailBinding binding;

    private final List<ModuleItem> moduleList = new ArrayList<>();
    private ModuleRecyclerAdapter adapter;

    private boolean isRemembered;

    private long userId, onlineId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);

        binding.ivClose.setOnClickListener(v -> dismiss());

        isRemembered = userSessionManager.isRemembered();

        setText();

        if (getArguments() != null) {
            userId = getArguments().getLong("user_id");
            onlineId = getArguments().getLong("online_id");
        }

        binding.rvModule.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvModule.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen._2dp, R.dimen._0dp, false));
        adapter = new ModuleRecyclerAdapter(mActivity, moduleList, false);
        binding.rvModule.setAdapter(adapter);

        if (ConnectivityReceiver.isConnected())
            getUserDetails();
        else
            getUserDetailsFromDB();

        return binding.getRoot();
    }

    private void setText() {
        binding.tvUserInfoTitle.setText(LanguageExtension.setText("user_information", getString(R.string.user_information)));
        binding.tvLoading.setText(LanguageExtension.setText("loading_content", getString(R.string.loading_content)));
        binding.tvNoData.setText(LanguageExtension.setText("no_data_found", getString(R.string.no_data_found)));
        binding.tvUserNameTitle.setText(LanguageExtension.setText("user_name", getString(R.string.user_name)));
        binding.tvUserPhoneTitle.setText(LanguageExtension.setText("phone_no", getString(R.string.phone_no)));
        binding.tvEmailTitle.setText(LanguageExtension.setText("email_id", getString(R.string.email_id)));
        binding.tvUserRoleTitle.setText(LanguageExtension.setText("user_role", getString(R.string.user_role)));
        binding.tvPermissionsTitle.setText(LanguageExtension.setText("permissions", getString(R.string.permissions)));
    }

    private void getUserDetailsFromDB() {
        showLayout(LOADING_CONDITION);
        UserItem userItem = dbHandler.getUserDetails(userId);
        setUserDetails(userItem);

        Glide.with(mActivity).load(userItem.getUserProfile())
                .error(languageManager.getLanguage() == 2 ? R.drawable.pt_app_logo : R.drawable.app_logo)
                .into(binding.ivUserProfile);

        List<Long> moduleIds = Arrays.asList(userItem.getModuleIds());
        List<Long> permissionIds = Arrays.asList(userItem.getPermissionIds());
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

    private void getUserDetails() {
        showLayout(LOADING_CONDITION);

        AndroidNetworking
                .post(BASE_URL + USER_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("user_id", String.valueOf(onlineId))
                .setTag("user-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject userData = response.getJSONObject("data").getJSONObject("user_data");
                                UserItem userItem = new UserItem();
                                userItem.setUserOnlineId(userData.getLong("user_id"));
                                userItem.setUserName(userData.getString("user_name"));

                                if (!userData.isNull("user_phone_no"))
                                    userItem.setUserPhone(userData.getString("user_phone_no"));

                                if (!userData.isNull("user_email"))
                                    userItem.setUserEmail(userData.getString("user_email"));

                                if (!userData.isNull("role_name"))
                                    userItem.setRoleName(userData.getString("role_name"));

                                if (!userData.isNull("user_image_url"))
                                    Glide.with(mActivity)
                                            .load(userData.getString("user_image_url"))
                                            .fitCenter()
                                            .into(binding.ivUserProfile);

                                if (!userData.isNull("user_module_ids"))
                                    userItem.setModuleIds(GeneralExtension.toLongArray(userData.getString("user_module_ids"), ","));

                                if (!userData.isNull("user_permission_ids"))
                                    userItem.setPermissionIds(GeneralExtension.toLongArray(userData.getString("user_permission_ids"), ","));

                                JSONArray modules = userData.getJSONArray("modules");
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

                                setUserDetails(userItem);

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

    private void setUserDetails(UserItem userItem) {
        binding.tvUserName.setText(userItem.getUserName());

        if (userItem.getUserPhone() != null)
            binding.tvUserPhone.setText(userItem.getUserPhone());
        else
            binding.tvUserPhone.setText("-");

        if (userItem.getUserEmail() != null)
            binding.tvUserEmail.setText(userItem.getUserEmail());
        else
            binding.tvUserEmail.setText("-");

        if (userItem.getRoleName() != null)
            binding.tvUserRole.setText(userItem.getRoleName());
        else
            binding.tvUserRole.setText("-");
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
        mActivity = null;
        super.onDetach();
    }
}
