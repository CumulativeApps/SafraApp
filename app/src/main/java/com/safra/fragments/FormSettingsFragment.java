package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.LanguageManager.languageManager;
import static com.safra.utilities.UserPermissions.FORM_ASSIGN;
import static com.safra.utilities.UserPermissions.GROUP_LIST;
import static com.safra.utilities.UserPermissions.USER_LIST;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.CreateForm;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.FormTypeSpinnerAdapter;
import com.safra.adapters.FormVisibilitySpinnerAdapter;
import com.safra.adapters.GroupCustomSpinnerAdapter;
import com.safra.adapters.LanguageSpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.FragmentFormSettingsBinding;
import com.safra.events.FieldListChangedEvent;
import com.safra.events.FormTypesReceivedEvent;
import com.safra.events.LanguagesReceivedEvent;
import com.safra.events.MarksChangedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.models.FormItem;
import com.safra.models.FormTypeItem;
import com.safra.models.FormVisibilityItem;
import com.safra.models.LanguageItem;
import com.safra.models.RoleItem;
import com.safra.models.UserItem;
import com.safra.utilities.Common;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.GeneralApiCallAndUse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FormSettingsFragment extends Fragment {

    public static final String TAG = "form_settings_fragment";

    private FragmentActivity mActivity = null;

    private FragmentFormSettingsBinding binding;

    private final List<LanguageItem> languageList = new ArrayList<>();
    private LanguageSpinnerAdapter adapterL;

    private final List<FormTypeItem> formTypeList = new ArrayList<>();
    private FormTypeSpinnerAdapter adapterT;

    private final List<UserItem> userList = new ArrayList<>();
    private UserCustomSpinnerAdapter adapterU;

    private final List<RoleItem> groupList = new ArrayList<>();
    private GroupCustomSpinnerAdapter adapterG;

    private Calendar calendar;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

    private boolean isRemembered;

    private long formTypeId = 0, formVisibilityId = 1, languageId;
    private long selectedLanguageId = 1;

    private List<Long> currentUsers = new ArrayList<>();
    private List<Long> currentGroups = new ArrayList<>();

    private boolean isUsersReceived = true, isGroupsReceived = true, isEssentialDataReceived = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormSettingsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        binding.etFormTitle.setText(LanguageExtension.setText("untitled_form", getString(R.string.untitled_form)));
        setText();

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() + (24 * 60 * 60 * 1000L));
        if (getArguments() != null) {
            boolean isNew = getArguments().getBoolean("is_new");
            if (isNew) {
                languageId = ((CreateForm) mActivity).formLanguageId = languageManager.getLanguage();
                if (PermissionExtension.checkForPermission(USER_LIST)) {
                    if (ConnectivityReceiver.isConnected())
                        getUsers(PAGE_START);
                    else
                        getUserListFromDB();
                    binding.clAssignUsers.setVisibility(View.VISIBLE);
                } else
                    binding.clAssignUsers.setVisibility(View.GONE);
                if (PermissionExtension.checkForPermission(GROUP_LIST)) {
                    if (ConnectivityReceiver.isConnected())
                        getGroups(PAGE_START);
                    else
                        getGroupListFromDB();
                    binding.clAssignGroups.setVisibility(View.VISIBLE);
                } else
                    binding.clAssignGroups.setVisibility(View.GONE);
                getEssentialData();
            } else {
                if (CreateForm.formItem != null)
                    setFormSettings(CreateForm.formItem);
            }
//            else {
//                if (getArguments().containsKey("form_title"))
//                    binding.etFormTitle.setText(getArguments().getString("form_title"));
//
//                if (getArguments().containsKey("form_description")) {
//                    binding.etFormDescription.setText(getArguments().getString("form_description"));
//                }
//
//                if (getArguments().containsKey("expiry_date")) {
//                    calendar.setTimeInMillis(getArguments().getLong("expiry_date"));
//                    binding.etExpiryDate.setText(sdfToShow.format(new Date(calendar.getTimeInMillis())));
//                }
//
//                if (getArguments().containsKey("language_id"))
//                    languageId = getArguments().getLong("language_id");
//                selectedLanguageId = languageId;
//
//                if (getArguments().containsKey("form_type_id"))
//                    formTypeId = getArguments().getLong("form_type_id");
//            }
        }

        adapterL = new LanguageSpinnerAdapter(mActivity, languageList);
        binding.spnFormLanguage.setAdapter(adapterL);

        adapterT = new FormTypeSpinnerAdapter(mActivity, formTypeList);
        binding.spnFormType.setAdapter(adapterT);

        if (adapterL.getCount() > 0)
            binding.spnFormLanguage.setSelection(adapterL.getPosition(languageId));

        if (adapterT.getCount() > 0)
            binding.spnFormType.setSelection(adapterT.getPosition(formTypeId));

        if (PermissionExtension.checkForPermission(FORM_ASSIGN)) {
            binding.clAssignUserAndGroup.setVisibility(View.VISIBLE);
        } else {
            binding.clAssignUserAndGroup.setVisibility(View.GONE);
        }

        binding.etExpiryDate.setFocusableInTouchMode(false);

        binding.etExpiryDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(mActivity,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        binding.etExpiryDate.setText(sdfToShow.format(new Date(calendar.getTimeInMillis())));
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dialog.show();
        });

        binding.spnFormType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formTypeId = id;
                showHideTotalMarks(formTypeId == Common.FORM_TYPE_MCQ);
                EventBus.getDefault().post(new FieldListChangedEvent());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spnFormVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formVisibilityId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spnFormLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguageId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.etFormTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.tilFormTitle.setErrorEnabled(false);
            }
        });

        binding.etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.tilExpiryDate.setErrorEnabled(false);
            }
        });

        binding.spnUsers.setOnClickListener(v -> openUserDialog(mActivity, userList));

        binding.spnGroups.setOnClickListener(v -> openGroupDialog(mActivity, groupList));

        return binding.getRoot();
    }

    private void setText() {
        binding.tvFormTitleHeading.setText(LanguageExtension.setText("form_title_mandatory", getString(R.string.form_title_mandatory)));
        binding.tvFormLanguageHeading.setText(LanguageExtension.setText("language", getString(R.string.language)));
        binding.tvFormTypeHeading.setText(LanguageExtension.setText("form_type", getString(R.string.form_type)));
        binding.tvTotalMarksHeading.setText(LanguageExtension.setText("form_mcq_marks", getString(R.string.form_mcq_marks)));
        binding.tvFormVisibilityHeading.setText(LanguageExtension.setText("form_visibility", getString(R.string.form_visibility)));
        binding.tvExpiryDateHeading.setText(LanguageExtension.setText("expiry_date", getString(R.string.expiry_date)));
        binding.tvFormDescriptionHeading.setText(LanguageExtension.setText("form_description", getString(R.string.form_description)));
        binding.tvUsersTitle.setText(LanguageExtension.setText("assign_users", getString(R.string.assign_users)));
        binding.tvEmptyUser.setText(LanguageExtension.setText("select_user", getString(R.string.select_user)));
        binding.tvGroupsTitle.setText(LanguageExtension.setText("assign_groups", getString(R.string.assign_groups)));
        binding.tvEmptyGroup.setText(LanguageExtension.setText("select_group", getString(R.string.select_group)));
    }

    public void setFormSettings(FormItem formItem) {
        if (formItem.getFormName() != null)
            binding.etFormTitle.setText(formItem.getFormName());

        if (formItem.getFormDescription() != null) {
            binding.etFormDescription.setText(formItem.getFormDescription());
        }

        if (formItem.getFormExpiryDate() != null) {
            try {
                calendar.setTimeInMillis(GeneralExtension
                        .convertStringToMilliSeconds(formItem.getFormExpiryDate(), SERVER_DATE_FORMAT));
                binding.etExpiryDate.setText(sdfToShow.format(new Date(calendar.getTimeInMillis())));
            } catch (ParseException e) {
                Log.e(TAG, "setFormSettings: " + e.getLocalizedMessage());
            }
        }

        if (formItem.getFormLanguageId() > 0) {
            languageId = ((CreateForm) mActivity).formLanguageId = formItem.getFormLanguageId();
            selectedLanguageId = languageId;
        }

        if (formItem.getFormType() > 0) {
            formTypeId = formItem.getFormType();
            showHideTotalMarks(formTypeId == Common.FORM_TYPE_MCQ);
        }

        if (formItem.getFormAccess() > 0)
            formVisibilityId = formItem.getFormAccess();

        if (formItem.getTotalMarks() > 0)
            CreateForm.totalMarks = formItem.getTotalMarks();

        if (formItem.getUserIds() != null) {
            currentUsers = GeneralExtension.toLongList(formItem.getUserIds());
        }

        if (formItem.getGroupIds() != null) {
            currentGroups = GeneralExtension.toLongList(formItem.getGroupIds());
        }

        getEssentialData();
        if (PermissionExtension.checkForPermission(USER_LIST)) {
            if (ConnectivityReceiver.isConnected())
                getUsers(PAGE_START);
            else
                getUserListFromDB();
            binding.clAssignUsers.setVisibility(View.VISIBLE);
        } else {
            binding.clAssignUsers.setVisibility(View.GONE);
        }
        if (PermissionExtension.checkForPermission(GROUP_LIST)) {
            if (ConnectivityReceiver.isConnected())
                getGroups(PAGE_START);
            else
                getGroupListFromDB();
            binding.clAssignGroups.setVisibility(View.VISIBLE);
        } else {
            binding.clAssignGroups.setVisibility(View.GONE);
        }
    }

    private void getUserListFromDB() {
        userList.clear();
        UserItem selfItem = new UserItem();
        selfItem.setUserOnlineId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
        selfItem.setUserName(LanguageExtension.setText("assign_to_self", getString(R.string.assign_to_self)));
        userList.add(selfItem);

        userList.addAll(dbHandler.getUsers(isRemembered ? userSessionManager.getUserId() : Safra.userId));

        if (currentUsers.size() > 0) {
            for (UserItem ui : userList) {
                ui.setSelected(currentUsers.contains(ui.getUserOnlineId()));
            }

            binding.cgUsers.removeAllViews();
            for (UserItem ui : userList) {
                if (currentUsers.contains(ui.getUserOnlineId())) {
                    Chip chip = new Chip(mActivity);
                    chip.setText(ui.getUserName());

                    binding.cgUsers.addView(chip);
                }
            }
            binding.tvEmptyUser.setVisibility(View.INVISIBLE);
            binding.cgUsers.setVisibility(View.VISIBLE);
        }
    }

    private void getGroupListFromDB() {
        groupList.clear();

        groupList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));

        if (currentGroups.size() > 0) {
            for (RoleItem ri : groupList) {
                ri.setSelected(currentGroups.contains(ri.getRoleOnlineId()));
            }

            binding.cgGroups.removeAllViews();
            for (RoleItem ri : groupList) {
                if (currentGroups.contains(ri.getRoleOnlineId())) {
                    Chip chip = new Chip(mActivity);
                    chip.setText(ri.getRoleName());

                    binding.cgGroups.addView(chip);
                }
            }
            binding.tvEmptyGroup.setVisibility(View.INVISIBLE);
            binding.cgGroups.setVisibility(View.VISIBLE);
        }
    }

    public boolean validateInputs() {
        String fTitle = binding.etFormTitle.getText() != null ? binding.etFormTitle.getText().toString() : "";

        if (fTitle.isEmpty()) {
            binding.tilFormTitle.setErrorEnabled(true);
            binding.tilFormTitle.setError(LanguageExtension.setText("enter_form_title", getString(R.string.enter_form_title)));
            binding.tilFormTitle.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public HashMap<String, String> getFieldValues() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("form_title", binding.etFormTitle.getText() != null ? binding.etFormTitle.getText().toString() : "");
        hashMap.put("form_type_id", String.valueOf(formTypeId));
        hashMap.put("form_visibility_id", String.valueOf(formVisibilityId));
        hashMap.put("language_id", String.valueOf(selectedLanguageId));
        if (binding.etExpiryDate.getText() != null && !binding.etExpiryDate.getText().toString().isEmpty())
            hashMap.put("expiry_date", sdfForServer.format(new Date(calendar.getTimeInMillis())));
        hashMap.put("form_description", binding.etFormDescription.getText() != null ? binding.etFormDescription.getText().toString() : "");

        if (formTypeId == Common.FORM_TYPE_MCQ && CreateForm.totalMarks > 0)
            hashMap.put("mcq_marks", String.valueOf(CreateForm.totalMarks));

        String cUser = GeneralExtension.toString(currentUsers);
        String cGroup = GeneralExtension.toString(currentGroups);

        hashMap.put("form_user_ids", cUser);
        hashMap.put("form_group_ids", cGroup);

        return hashMap;
    }

    private void getEssentialData() {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
        isEssentialDataReceived = false;
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        FormVisibilitySpinnerAdapter adapterV = new FormVisibilitySpinnerAdapter(mActivity, getFormVisibilities());
        binding.spnFormVisibility.setAdapter(adapterV);
        binding.spnFormVisibility.setSelection(adapterV.getPosition(formVisibilityId));

        languageList.clear();
        languageList.addAll(dbHandler.getLanguages());
        if (adapterL != null) {
            Log.e(TAG, "getEssentialData: " + languageList.size());
            adapterL.notifyDataSetChanged();
            binding.spnFormLanguage.setSelection(adapterL.getPosition(languageId));
        }

        formTypeList.clear();
        formTypeList.addAll(dbHandler.getFormTypeList());
        if (adapterT != null) {
            adapterT.notifyDataSetChanged();
            binding.spnFormType.setSelection(adapterT.getPosition(formTypeId));
        }

        binding.etTotalMarks.setText(String.valueOf(CreateForm.totalMarks));

        isEssentialDataReceived = true;
        if(isUsersReceived && isGroupsReceived)
        LoadingDialogExtension.hideLoading();
//        dialogL.dismiss();

        if (ConnectivityReceiver.isConnected()) {
            GeneralApiCallAndUse.getLanguages(mActivity, TAG);
            GeneralApiCallAndUse.getFormTypes(mActivity, TAG);
        }
    }

    private void getUsers(int pageNo) {
        if (pageNo == PAGE_START) {
            LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
            isUsersReceived = false;
        }
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + USER_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNo))
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

                                if (currentPage == PAGE_START) {
                                    userList.clear();
//                                    UserItem userItem = new UserItem();
//                                    userItem.setUserId(-1);
//                                    userItem.setUserName("Select User");
//                                    userList.add(userItem);

                                    UserItem selfItem = new UserItem();
                                    selfItem.setUserOnlineId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                                    selfItem.setUserName(LanguageExtension.setText("assign_to_self", getString(R.string.assign_to_self)));

                                    if (currentUsers.size() > 0)
                                        selfItem.setSelected(currentUsers.contains(selfItem.getUserOnlineId()));

                                    userList.add(selfItem);
                                }

                                if (users.length() > 0) {
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
                                        UserItem userItem = new UserItem();
                                        userItem.setUserOnlineId(user.getInt("user_id"));
                                        userItem.setUserName(user.getString("user_name"));
                                        userItem.setUserStatus(user.getInt("user_status"));

                                        if (user.has("user_email") && !user.isNull("user_email"))
                                            userItem.setUserEmail(user.getString("user_email"));

                                        if (user.has("user_phone_no") && !user.isNull("user_phone_no"))
                                            userItem.setUserPhone(user.getString("user_phone_no"));

                                        if (user.has("role_id") && !user.isNull("role_id"))
                                            userItem.setRoleId(user.getInt("role_id"));

                                        if (user.has("role_name") && !user.isNull("role_name"))
                                            userItem.setRoleName(user.getString("role_name"));

                                        if (currentUsers.size() > 0) {
                                            userItem.setSelected(currentUsers.contains(userItem.getUserOnlineId()));
                                        }

                                        userList.add(userItem);
                                    }
                                }

                                if (currentUsers.size() > 0) {
                                    binding.cgUsers.removeAllViews();
                                    for (UserItem ui : userList) {
                                        if (currentUsers.contains(ui.getUserOnlineId())) {
                                            Chip chip = new Chip(mActivity);
                                            chip.setText(ui.getUserName());

                                            binding.cgUsers.addView(chip);
                                        }
                                    }
                                    binding.tvEmptyUser.setVisibility(View.INVISIBLE);
                                    binding.cgUsers.setVisibility(View.VISIBLE);
                                }

                                if (currentPage < totalPage) {
                                    getUsers(++currentPage);
                                } else {
                                    isUsersReceived = true;
                                    if (isGroupsReceived && isEssentialDataReceived)
                                        LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                                isUsersReceived = true;
                                if (isGroupsReceived && isEssentialDataReceived)
                                    LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isUsersReceived = true;
                            if (isGroupsReceived && isEssentialDataReceived)
                                LoadingDialogExtension.hideLoading();
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        isUsersReceived = true;
                        if (isGroupsReceived && isEssentialDataReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void getGroups(int pageNo) {
        if (pageNo == PAGE_START) {
            LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
            isGroupsReceived = false;
        }
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

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
//                                    RoleItem roleItem = new RoleItem();
//                                    roleItem.setRoleId(-1);
//                                    roleItem.setRoleName("Select Group");
//                                    groupList.add(roleItem);
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

                                        if (currentGroups.size() > 0)
                                            roleItem.setSelected(currentGroups.contains(roleItem.getRoleOnlineId()));

                                        groupList.add(roleItem);
                                    }
                                }

                                if (currentGroups.size() > 0) {
                                    binding.cgGroups.removeAllViews();
                                    for (RoleItem ri : groupList) {
                                        if (currentGroups.contains(ri.getRoleOnlineId())) {
                                            Chip chip = new Chip(mActivity);
                                            chip.setText(ri.getRoleName());

                                            binding.cgGroups.addView(chip);
                                        }
                                    }
                                    binding.tvEmptyGroup.setVisibility(View.INVISIBLE);
                                    binding.cgGroups.setVisibility(View.VISIBLE);
                                }

                                if (currentPage < totalPage) {
                                    getGroups(++currentPage);
                                } else {
                                    isGroupsReceived = true;
                                    if (isUsersReceived && isEssentialDataReceived)
                                        LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                                isGroupsReceived = true;
                                if (isUsersReceived && isEssentialDataReceived)
                                    LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isGroupsReceived = true;
                            if (isUsersReceived && isEssentialDataReceived)
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
                        if (isUsersReceived && isEssentialDataReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

//    private List<FormTypeItem> getFormTypes() {
//        List<FormTypeItem> formTypeList = new ArrayList<>();
//        formTypeList.add(new FormTypeItem(Common.FORM_TYPE_GENERAL, LanguageExtension.setText("general_form", getString(R.string.general_form))));
//        formTypeList.add(new FormTypeItem(Common.FORM_TYPE_CASHCHEW, LanguageExtension.setText("cashchew_form", getString(R.string.cashchew_form))));
//        formTypeList.add(new FormTypeItem(Common.FORM_TYPE_MCQ, LanguageExtension.setText("mcq_form", getString(R.string.mcq_form))));
//
//        return formTypeList;
//    }

    private List<FormVisibilityItem> getFormVisibilities() {
        List<FormVisibilityItem> formTypeList = new ArrayList<>();
        formTypeList.add(new FormVisibilityItem(1, LanguageExtension.setText("private_type", getString(R.string.private_type))));
        formTypeList.add(new FormVisibilityItem(2, LanguageExtension.setText("public_type", getString(R.string.public_type))));

        return formTypeList;
    }

    private void showHideTotalMarks(boolean isMCQForm) {
        binding.clTotalMarks.setVisibility(isMCQForm ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguagesReceived(LanguagesReceivedEvent event) {
        Log.e(TAG, "onLanguagesReceived: " + event.getLanguages().size());
        languageList.clear();
        languageList.addAll(event.getLanguages());
        if (adapterL != null) {
            adapterL.notifyDataSetChanged();
            binding.spnFormLanguage.setSelection(adapterL.getPosition(languageId));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFormTypesReceived(FormTypesReceivedEvent event) {
        Log.e(TAG, "onFormTypesReceived: " + event.getFormTypeList().size());
        formTypeList.clear();
        formTypeList.addAll(event.getFormTypeList());
        if (adapterT != null) {
            adapterT.notifyDataSetChanged();
            binding.spnFormType.setSelection(adapterT.getPosition(formTypeId));
        }
    }

    private void openUserDialog(Context context, List<UserItem> options) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null, false);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);

        final RecyclerView optionRecycler = view.findViewById(R.id.rvOptions);
        optionRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        optionRecycler.addItemDecoration(new LineHorizontalItemDecoration(this, R.dimen.recycler_bottom_offset, false));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        TextView dialogTitle = view.findViewById(R.id.tvDialogTitle);
        dialogTitle.setText(LanguageExtension.setText("select_user", getString(R.string.select_user)));
        ImageView close = view.findViewById(R.id.ivClose);

        alertDialog.show();
        alertDialog.getWindow().setLayout(context.getResources().getDimensionPixelSize(R.dimen.dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);

        close.setOnClickListener(v -> alertDialog.dismiss());

        adapterU = new UserCustomSpinnerAdapter(context, options, (item, position) -> {
            binding.cgUsers.removeAllViews();
            currentUsers.clear();
            if (adapterU.getSelected().size() > 0) {
                for (UserItem ui : adapterU.getSelected()) {
                    currentUsers.add(ui.getUserOnlineId());
                    Chip chip = new Chip(context);
                    chip.setText(ui.getUserName());

                    binding.cgUsers.addView(chip);
                }
            }

            if (currentUsers.size() > 0) {
                binding.tvEmptyUser.setVisibility(View.INVISIBLE);
                binding.cgUsers.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyUser.setVisibility(View.VISIBLE);
                binding.cgUsers.setVisibility(View.GONE);
            }
        });
        optionRecycler.setAdapter(adapterU);
    }

    private void openGroupDialog(Context context, List<RoleItem> options) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null, false);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);

        final RecyclerView optionRecycler = view.findViewById(R.id.rvOptions);
        optionRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        optionRecycler.addItemDecoration(new LineHorizontalItemDecoration(this, R.dimen.recycler_bottom_offset, false));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        TextView dialogTitle = view.findViewById(R.id.tvDialogTitle);
        dialogTitle.setText(LanguageExtension.setText("select_group", getString(R.string.select_group)));
        ImageView close = view.findViewById(R.id.ivClose);

        alertDialog.show();
        alertDialog.getWindow().setLayout(context.getResources().getDimensionPixelSize(R.dimen.dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);

        close.setOnClickListener(v -> alertDialog.dismiss());

        adapterG = new GroupCustomSpinnerAdapter(context, options, (item, position) -> {
            binding.cgGroups.removeAllViews();
            currentGroups.clear();
            if (adapterG.getSelected().size() > 0) {
                for (RoleItem ri : adapterG.getSelected()) {
                    currentGroups.add(ri.getRoleOnlineId());
                    Chip chip = new Chip(context);
                    chip.setText(ri.getRoleName());

                    binding.cgGroups.addView(chip);
                }
            }

            if (currentGroups.size() > 0) {
                binding.tvEmptyGroup.setVisibility(View.INVISIBLE);
                binding.cgGroups.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyGroup.setVisibility(View.VISIBLE);
                binding.cgGroups.setVisibility(View.GONE);
            }
        });
        optionRecycler.setAdapter(adapterG);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "onActivityResult: creating field");
//        if(requestCode == REQUEST_SELECT_FORM_ELEMENT && resultCode == RESULT_SUCCESS_SELECT_FORM_ELEMENT){
//            if(data.getExtras() != null){
//                Bundle bundle = data.getExtras();
//                switch (bundle.getInt("type")){
//                    case SUB_TYPE_TEXT:
//                        Log.e(TAG, "onActivityResult: creating field");
//                        formBuilder.addFormElement(TextFormElement.createInstance()
//                                .setFieldLabel(bundle.getString("label"))
//                                .setFieldName("text-"+ (Calendar.getInstance().getTimeInMillis())));
//                        break;
//                }
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMCQFieldChange(MarksChangedEvent event) {
        Log.e(TAG, "onMCQFieldChange: ");
        binding.etTotalMarks.setText(String.valueOf(CreateForm.totalMarks));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        EventBus.getDefault().unregister(this);
    }
}
