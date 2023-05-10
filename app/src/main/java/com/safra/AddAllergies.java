package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.Common.TASK_SAVE_API;
import static com.safra.utilities.Common.TASK_VIEW_API;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.adapters.GroupCustomSpinnerAdapter;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.ActivityAddAllergiesBinding;
import com.safra.databinding.ActivityAddProjectsBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.PriorityItem;
import com.safra.models.RoleItem;
import com.safra.models.TaskItem;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddAllergies extends AppCompatActivity {
    public static final String TAG = "add_allergies_fragment";

    private ActivityAddAllergiesBinding binding;

    private final List<UserItem> userList = new ArrayList<>();
    private UserCustomSpinnerAdapter adapterU;

    private final List<RoleItem> groupList = new ArrayList<>();
    private GroupCustomSpinnerAdapter adapterG;

    private boolean isRemembered;

    private Calendar calendarStart, calendarEnd;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

    private int selectedPriority = 1;

    private boolean isNew;
    private long taskId = -1, onlineId = -1;
    private TaskItem taskItem = null;
    private List<Long> currentUsers = new ArrayList<>();
    private List<Long> currentGroups = new ArrayList<>();

    private boolean isUserDataReceived = false, isGroupDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAllergiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBacck.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();



        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        if (getIntent() != null) {
            binding.tvAddAllergiesHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                    getUsers(PAGE_START);
                    getGroups(PAGE_START);
                } else {
                    getUserListFromDB();
                    getGroupListFromDB();
                }
            } else {
                taskId = getIntent().getLongExtra("task_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                Log.e(TAG, "onCreate: " + taskId);
                if (ConnectivityReceiver.isConnected())
                    getEditData();
//                getEditDataFromDB();
                else
                    getEditDataFromDB();
            }
        }

        PrioritySpinnerAdapter adapterP = new PrioritySpinnerAdapter(this, getPriorityList());
        binding.spnPriority.setAdapter(adapterP);

        binding.spnPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPriority = ((PriorityItem) parent.getSelectedItem()).getPriorityStatus();
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
        binding.tvAllergenTitle.setText(LanguageExtension.setText("allergen", getString(R.string.allergen)));
        binding.tvReactionTitle.setText(LanguageExtension.setText("reaction", getString(R.string.reaction)));
        binding.tvSeverityTitle.setText(LanguageExtension.setText("severity", getString(R.string.severity)));
        binding.tvCommentTitle.setText(LanguageExtension.setText("comment", getString(R.string.comment)));

        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void getEditDataFromDB() {
        taskItem = dbHandler.getTaskDetails(taskId);
        setTaskDetails();

        currentUsers = GeneralExtension.toLongList(taskItem.getUserIds());
        currentGroups = GeneralExtension.toLongList(taskItem.getGroupIds());

        getUserListFromDB();
        getGroupListFromDB();
    }

    private void getUserListFromDB() {
        userList.clear();

        UserItem selfItem = new UserItem();
        selfItem.setUserOnlineId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
        selfItem.setUserName("Assign To Self");
        userList.add(selfItem);
        userList.addAll(dbHandler.getUsers(isRemembered ? userSessionManager.getUserId() : Safra.userId));

        if (currentUsers.size() > 0) {
            for (UserItem ui : userList) {
                Log.e(TAG, "getUserListFromDB: " + ui.getUserOnlineId());
                ui.setSelected(currentUsers.contains(ui.getUserOnlineId()));
            }

            for (UserItem ui : userList) {
                if (currentUsers.contains(ui.getUserOnlineId())) {
                    Chip chip = new Chip(AddAllergies.this);
                    chip.setText(ui.getUserName());


                }
            }


        }
    }

    private void getGroupListFromDB() {
        groupList.clear();
        groupList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));

        if (currentGroups.size() > 0) {
            for (RoleItem ri : groupList) {
                ri.setSelected(currentGroups.contains(ri.getRoleOnlineId()));
            }


            for (RoleItem ri : groupList) {
                if (currentGroups.contains(ri.getRoleOnlineId())) {
                    Chip chip = new Chip(AddAllergies.this);
                    chip.setText(ri.getRoleName());


                }
            }


        }
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
                .post(BASE_URL + TASK_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("task_id", String.valueOf(onlineId))
                .setTag("task-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject taskData = response.getJSONObject("data").getJSONObject("task_data");
                                taskItem = new TaskItem();
                                taskItem.setTaskOnlineId(taskData.getLong("task_id"));
                                taskItem.setTaskName(taskData.getString("task_title"));

                                if (!taskData.isNull("task_details"))
                                    taskItem.setTaskDetail(taskData.getString("task_details"));

                                if (!taskData.isNull("task_priority"))
                                    taskItem.setPriority(taskData.getInt("task_priority"));

                                if (!taskData.isNull("task_start_date"))
                                    taskItem.setStartDate(taskData.getLong("task_start_date") * 1000);

                                if (!taskData.isNull("task_end_date"))
                                    taskItem.setEndDate(taskData.getLong("task_end_date") * 1000);

                                taskItem.setAddedBy(taskData.getLong("added_by"));

                                if (!taskData.isNull("task_group_ids")) {
                                    Long[] groups = GeneralExtension.toLongArray(taskData.getString("task_group_ids"), ",");
                                    currentGroups = GeneralExtension.toLongList(groups);
                                }

                                if (!taskData.isNull("task_user_ids")) {
                                    Long[] users = GeneralExtension.toLongArray(taskData.getString("task_user_ids"), ",");
                                    currentUsers = GeneralExtension.toLongList(users);
                                }

                                setTaskDetails();

                                getUsers(PAGE_START);
                                getGroups(PAGE_START);

                            } else {
                                Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
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


    private void setTaskDetails() {
        taskId = taskItem.getTaskId();
        onlineId = taskItem.getTaskOnlineId();
        binding.etAllergen.setText(taskItem.getTaskName());
        if (taskItem.getTaskDetail() != null)
            binding.etReaction.setText(taskItem.getTaskDetail());
        if (taskItem.getTaskDetail() != null)
            binding.etComment.setText(taskItem.getTaskDetail());
    }

    private void validateInputs() {
        String tName = binding.etAllergen.getText() != null ? binding.etAllergen.getText().toString() : "";
        String tDetail = binding.etReaction.getText() != null ? binding.etReaction.getText().toString() : "";
        String sDate = binding.etComment.getText() != null ? binding.etComment.getText().toString() : "";


        String cUser = GeneralExtension.toString(currentUsers);
        String cGroup = GeneralExtension.toString(currentGroups);

        if (tName.isEmpty() || tDetail.isEmpty() || sDate.isEmpty()) {
            if (tName.isEmpty()) {
                binding.etAllergen.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etAllergen.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etReaction.setError(LanguageExtension.setText("enter_start_date", getString(R.string.enter_start_date)));
                binding.etReaction.requestFocus();
            }
            if (tName.isEmpty()) {
                binding.etComment.setError(LanguageExtension.setText("enter_task_name", getString(R.string.enter_task_name)));
                binding.etComment.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("task_title", tName);
                if (!tDetail.isEmpty())
                    parameters.put("task_details", tDetail);
                parameters.put("task_priority", String.valueOf(selectedPriority));
                parameters.put("task_start_date", sdfForServer.format(new Date(calendarStart.getTimeInMillis())));
                parameters.put("task_end_date", sdfForServer.format(new Date(calendarEnd.getTimeInMillis())));
                if (!cUser.isEmpty())
                    parameters.put("task_user_ids", cUser);
                if (!cGroup.isEmpty())
                    parameters.put("task_group_ids", cGroup);
                if (isNew) {
                    saveTask(parameters);
                } else {
                    parameters.put("task_id", String.valueOf(taskId));

                    editTask(parameters);
                }
            } else {
                if (taskItem == null)
                    taskItem = new TaskItem();

                taskItem.setTaskName(tName);
                taskItem.setTaskDetail(tDetail);
                taskItem.setPriority(selectedPriority);
                taskItem.setStartDate(calendarStart.getTimeInMillis());
                taskItem.setEndDate(calendarEnd.getTimeInMillis());
                taskItem.setAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                taskItem.setAddedByName(isRemembered ? userSessionManager.getUserName() : Safra.userName);
                if (!cUser.isEmpty())
                    taskItem.setUserIds(GeneralExtension.toLongArray(cUser, ","));
                if (!cGroup.isEmpty())
                    taskItem.setGroupIds(GeneralExtension.toLongArray(cGroup, ","));
                if (isRemembered ? userSessionManager.isAgency() : Safra.isAgency)
                    taskItem.setMasterId(isRemembered ? userSessionManager.getUserId() : Safra.userId);

                if (isNew) {
                    if (currentUsers.contains(isRemembered ? userSessionManager.getUserId() : Safra.userId)
                            || currentGroups.contains(isRemembered ? userSessionManager.getUserRoleId() : Safra.userRoleId)) {
                        taskItem.setTaskStatus("Pending");
                    } else {
                        taskItem.setTaskStatus("-");
                    }
                    long i = dbHandler.addTaskOffline(taskItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new TaskAddedEvent());
                        finish();
                    }
                } else {
                    taskItem.setTaskId(taskId);
                    taskItem.setTaskOnlineId(onlineId);
                    long i = dbHandler.updateTaskOffline(taskItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new TaskAddedEvent());
                        finish();
                    }
                }
            }
        }
    }

    private void getUsers(int pageNo) {
        if (pageNo == PAGE_START) {
            LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
            isUserDataReceived = false;
        }
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

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

                                    UserItem selfItem = new UserItem();
                                    selfItem.setUserId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                                    selfItem.setUserName(LanguageExtension.setText("assign_to_self", getString(R.string.assign_to_self)));
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

                                        if (currentUsers.size() > 0)
                                            userItem.setSelected(currentUsers.contains(userItem.getUserOnlineId()));

                                        userList.add(userItem);
                                    }
                                }

                                if (currentUsers.size() > 0) {

                                    for (UserItem ui : userList) {
                                        if (currentUsers.contains(ui.getUserOnlineId())) {
                                            Chip chip = new Chip(AddAllergies.this);
                                            chip.setText(ui.getUserName());


                                        }
                                    }


                                }

                                if (currentPage < totalPage) {
                                    getUsers(++currentPage);
                                } else {
                                    isUserDataReceived = true;
                                    if (isGroupDataReceived)
                                        LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
                                isUserDataReceived = true;
                                if (isGroupDataReceived)
                                    LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isUserDataReceived = true;
                            if (isGroupDataReceived)
                                LoadingDialogExtension.hideLoading();
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        isUserDataReceived = true;
                        if (isGroupDataReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    public void getGroups(int pageNo) {
        if (pageNo == PAGE_START) {
            LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
            isGroupDataReceived = false;
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

                                    for (RoleItem ri : groupList) {
                                        if (currentGroups.contains(ri.getRoleOnlineId())) {
                                            Chip chip = new Chip(AddAllergies.this);
                                            chip.setText(ri.getRoleName());


                                        }
                                    }


                                }

                                if (currentPage < totalPage) {
                                    getGroups(++currentPage);
                                } else {
                                    isGroupDataReceived = true;
                                    if(isUserDataReceived)
                                        LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
                                isGroupDataReceived = true;
                                if(isUserDataReceived)
                                    LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isGroupDataReceived = true;
                            if(isUserDataReceived)
                                LoadingDialogExtension.hideLoading();
                        }

//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        isGroupDataReceived = true;
                        if(isUserDataReceived)
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
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

            currentUsers.clear();
            if (adapterU.getSelected().size() > 0) {
                for (UserItem ui : adapterU.getSelected()) {
                    currentUsers.add(ui.getUserOnlineId());
                    Chip chip = new Chip(this);
                    chip.setText(ui.getUserName());


                }
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

            currentGroups.clear();
            if (adapterG.getSelected().size() > 0) {
                for (RoleItem ri : adapterG.getSelected()) {
                    currentGroups.add(ri.getRoleOnlineId());
                    Chip chip = new Chip(this);
                    chip.setText(ri.getRoleName());


                }
            }


        });
        optionRecycler.setAdapter(adapterG);
    }

    private List<PriorityItem> getPriorityList() {
        List<PriorityItem> priorityList = new ArrayList<>();
        priorityList.add(new PriorityItem(1, LanguageExtension.setText("high", getString(R.string.high))));
        priorityList.add(new PriorityItem(2, LanguageExtension.setText("medium", getString(R.string.medium))));
        priorityList.add(new PriorityItem(3, LanguageExtension.setText("low", getString(R.string.low))));

        return priorityList;
    }

    private void saveTask(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + TASK_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("save-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new TaskAddedEvent());
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

    private void editTask(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + TASK_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("edit-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new TaskAddedEvent());
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