package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PLANNER_GET_TASK_DATA;
import static com.safra.utilities.Common.PLANNER_SAVE_TASK;
import static com.safra.utilities.Common.PLANNER_SAVE_TASK_EDIT;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.UserPermissions.TASK_ASSIGN;
import static com.safra.utilities.UserSessionManager.userSessionManager;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.safra.adapters.ActionPlanUserSpinnerAdapter;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.ActivityAddActionPlanTaskBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.models.GetEditTaskData;
import com.safra.models.PlannerDashBoardModel;
import com.safra.models.PriorityItem;
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

public class AddActionPlanTask extends AppCompatActivity {
    public static final String TAG = "add_action_task";


    private ActivityAddActionPlanTaskBinding binding;

    private final List<UserItem> userList = new ArrayList<>();
    private final List<UserItem> userList1 = new ArrayList<>();
//    private final List<GetEditTaskData.Data.User> userList12 = new ArrayList<>();

    private UserCustomSpinnerAdapter adapterU;
    private ActionPlanUserSpinnerAdapter adapterG;

    private List<PlannerDashBoardModel.Data.Project.Aim.Goal.Task> taskList = new ArrayList<>();


    private boolean isRemembered;

    private Calendar calendarStart, calendarEnd;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

    private int selectedPriority = 1;

    private boolean isNew;
    private long taskId = -1, onlineId = -1;
    private long projectId = -1;
    private TaskItem taskItem = null;
    private int actionTaskId = 1;
    private List<Long> currentUsers = new ArrayList<>();
    private List<Long> currentUsers1 = new ArrayList<>();

    private boolean isUserDataReceived = false, isGroupDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddActionPlanTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        binding.etStartDate.setFocusableInTouchMode(false);
        binding.etEndDate.setFocusableInTouchMode(false);
        projectId = getIntent().getLongExtra("planner_project_id", -1);
        actionTaskId = getIntent().getIntExtra("action_task_id", -1);
        System.out.println("Project ID:-" + projectId);
        System.out.println("TASK ID:-" + actionTaskId);
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        if (PermissionExtension.checkForPermission(TASK_ASSIGN)) {
            binding.clAssignUserAndGroup.setVisibility(View.VISIBLE);
        } else {
            binding.clAssignUserAndGroup.setVisibility(View.GONE);
        }

        if (getIntent() != null) {
            binding.tvAddTaskHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                    getUsers(PAGE_START);
                    getUsers1(PAGE_START);
                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {
                taskId = getIntent().getLongExtra("task_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                System.out.println("onlineId" + onlineId);
                Log.e(TAG, "onCreate: " + taskId);
                if (ConnectivityReceiver.isConnected())
                    getUsers(PAGE_START);
                getUsers1(PAGE_START);
                getEditData();
//                getEditDataFromDB();
//                else
//                    getEditDataFromDB();
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

        binding.spnUsers.setOnClickListener(v -> openUserDialog(this, userList));

        binding.spnGroups.setOnClickListener(v -> openGroupDialog(this, userList1));

        binding.etStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendarStart.set(year, month, dayOfMonth);
                binding.etStartDate.setText(sdfToShow.format(new Date(calendarStart.getTimeInMillis())));
            }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        binding.etEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendarEnd.set(year, month, dayOfMonth);
                binding.etEndDate.setText(sdfToShow.format(new Date(calendarEnd.getTimeInMillis())));
            }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
            datePickerDialog.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            validateInputs();

        });
    }

    private void setText() {
        binding.tvTaskNameTitle.setText(LanguageExtension.setText("title", getString(R.string.title)));
        binding.tvTaskDetailTitle.setText(LanguageExtension.setText("observation", getString(R.string.observation)));
        binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date_mandatory", getString(R.string.start_date_mandatory)));
        binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date_mandatory", getString(R.string.end_date_mandatory)));
        binding.tvPriorityTitle.setText(LanguageExtension.setText("priority", getString(R.string.priority)));
        binding.tvUsersTitle.setText(LanguageExtension.setText("responsible", getString(R.string.responsible)));
        binding.tvEmptyUser.setText(LanguageExtension.setText("select_responsible", getString(R.string.select_responsible)));
        binding.tvGroupsTitle.setText(LanguageExtension.setText("supervisor", getString(R.string.supervisor)));
        binding.tvEmptyGroup.setText(LanguageExtension.setText("select_supervisor", getString(R.string.select_supervisor)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }
    private void getEditData() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
        AndroidNetworking
                .post(BASE_URL + PLANNER_GET_TASK_DATA)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("planner_task_id", String.valueOf(actionTaskId))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("add_action_task", "response " + response);
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject goal = data.getJSONObject("goal");
                                JSONArray users = data.getJSONArray("users");

                                // Parse the goal object
                                GetEditTaskData.Goal parsedGoal = new Gson().fromJson(goal.toString(), GetEditTaskData.Goal.class);
                                binding.etTaskName.setText(parsedGoal.getTitle());
                                binding.etTaskDetail.setText(parsedGoal.getObservation());
                                binding.etStartDate.setText(parsedGoal.getStart_date());
                                binding.etEndDate.setText(parsedGoal.getEnd_date());
                                binding.etMetrics.setText(parsedGoal.getMetrics());

                                // Parse the users array
                                List<GetEditTaskData.User> userList = new ArrayList<>();
                                for (int i = 0; i < users.length(); i++) {
                                    JSONObject userObj = users.getJSONObject(i);
                                    GetEditTaskData.User user = new Gson().fromJson(userObj.toString(), GetEditTaskData.User.class);
                                    userList.add(user);

                                    // Check if responsible value matches user_id
                                    if (parsedGoal.getResponsible().equals(String.valueOf(user.getUser_id()))) {
                                        binding.tvEmptyUser.setText(user.getUser_name());
                                    }else if(parsedGoal.getResponsible().equals(String.valueOf(parsedGoal.getUser_id()))) {
                                        binding.tvEmptyUser.setText("Assign To Self");
                                    }
                                    if (parsedGoal.getSupervisor().equals(String.valueOf(user.getUser_id()))) {
                                        binding.tvEmptyGroup.setText(user.getUser_name());
                                    }else if(parsedGoal.getSupervisor().equals(String.valueOf(parsedGoal.getUser_id()))) {
                                        binding.tvEmptyGroup.setText("Assign To Self");
                                    }
                                }

//                                binding.tvEmptyGroup.setText(parsedGoal.getResponsible());

                                // Use the userList as needed, e.g., display it in a RecyclerView or ListView
                            } else {
                                Toast.makeText(AddActionPlanTask.this, message, Toast.LENGTH_SHORT).show();
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
                        LoadingDialogExtension.hideLoading();
                    }
                });
    }


//    private void getEditData() {
//        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        AndroidNetworking
//                .post(BASE_URL + PLANNER_GET_TASK_DATA)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("planner_task_id", String.valueOf(actionTaskId))
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("add_action_task", "response " + response);
//                        LoadingDialogExtension.hideLoading();
//                        try {
//                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            if (success == 1) {
//                                JSONObject data = response.getJSONObject("data");
//                                JSONObject goal = data.getJSONObject("goal");
//                                JSONArray user = data.getJSONArray("users");
//                                GetEditTaskData.Data.Goal getEditTaskData = new Gson().fromJson(goal.toString(), GetEditTaskData.Data.Goal.class);
//                                binding.etTaskName.setText(getEditTaskData.title);
//                                binding.etTaskDetail.setText(getEditTaskData.observation);
//                                binding.etStartDate.setText(getEditTaskData.startDate);
//                                binding.etEndDate.setText(getEditTaskData.endDate);
//                                binding.etMetrics.setText(getEditTaskData.metrics);
//                                binding.tvEmptyUser.setText(getEditTaskData.responsible);
//                                binding.tvEmptyGroup.setText(getEditTaskData.responsible);
//                            } else {
//                                Toast.makeText(AddActionPlanTask.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                        }
//
////                        dialogL.dismiss();
//                    }
//
//
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.e(TAG, "onError: " + anError.getErrorCode());
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//                        Log.e(TAG, "onError: " + anError.getErrorBody());
//                        LoadingDialogExtension.hideLoading();
////                        dialogL.dismiss();
//                    }
//                });
//    }


    private void setTaskDetails() {
        taskId = taskItem.getTaskId();
        onlineId = taskItem.getTaskOnlineId();
        binding.etTaskName.setText(taskItem.getTaskName());
        if (taskItem.getTaskDetail() != null)
            binding.etTaskDetail.setText(taskItem.getTaskDetail());
        if (taskItem.getStartDate() != 0) {
            calendarStart.setTimeInMillis(taskItem.getStartDate());
            binding.etStartDate.setText(sdfToShow.format(new Date(taskItem.getStartDate())));
        }
        if (taskItem.getEndDate() != 0) {
            calendarEnd.setTimeInMillis(taskItem.getEndDate());
            binding.etEndDate.setText(sdfToShow.format(new Date(taskItem.getEndDate())));
        }
    }

    private void validateInputs() {
        String tName = binding.etTaskName.getText() != null ? binding.etTaskName.getText().toString() : "";
        String tDetail = binding.etTaskDetail.getText() != null ? binding.etTaskDetail.getText().toString() : "";
        String tMetrics = binding.etMetrics.getText() != null ? binding.etMetrics.getText().toString() : "";
        String sDate = binding.etStartDate.getText() != null ? binding.etStartDate.getText().toString() : "";
        String eDate = binding.etEndDate.getText() != null ? binding.etEndDate.getText().toString() : "";

        String cUser = GeneralExtension.toString(currentUsers) != null ? GeneralExtension.toString(currentUsers).toString() : "";
        String cGroup = GeneralExtension.toString(currentUsers1) != null ? GeneralExtension.toString(currentUsers1).toString() : "";
        ;

        if (tName.isEmpty() || sDate.isEmpty() || eDate.isEmpty()) {
            if (eDate.isEmpty()) {
                binding.etEndDate.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etEndDate.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etStartDate.setError(LanguageExtension.setText("enter_start_date", getString(R.string.enter_start_date)));
                binding.etStartDate.requestFocus();
            }
            if (tName.isEmpty()) {
                binding.etTaskName.setError(LanguageExtension.setText("enter_task_name", getString(R.string.enter_task_name)));
                binding.etTaskName.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("title", tName);
                parameters.put("planner_goal_id", String.valueOf(projectId));

                if (!tDetail.isEmpty())
                    parameters.put("observation", tDetail);
                if (!tMetrics.isEmpty())
                    parameters.put("metrics", tMetrics);
                parameters.put("priority", String.valueOf(selectedPriority));
                parameters.put("start_date", sdfForServer.format(new Date(calendarStart.getTimeInMillis())));
                parameters.put("end_date", sdfForServer.format(new Date(calendarEnd.getTimeInMillis())));
//                if (!cUser.isEmpty())
                parameters.put("responsible", cUser);
//                if (!cGroup.isEmpty())
                parameters.put("supervisor", cGroup);
                if (isNew) {
                    saveTask(parameters);
                } else {
                    parameters.put("goal_task_id", String.valueOf(actionTaskId));

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
                            || currentUsers1.contains(isRemembered ? userSessionManager.getUserId() : Safra.userRoleId)) {
                        taskItem.setTaskStatus("Pending");
                    } else {
                        taskItem.setTaskStatus("-");
                    }
                    long i = dbHandler.addTask(taskItem);
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
//

                                    UserItem selfItem = new UserItem();
                                    selfItem.setUserId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                                    selfItem.setUserName(LanguageExtension.setText("assign_to_self", getString(R.string.assign_to_self)));
                                    userList.add(selfItem);
//
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
//
//                                        if (currentUsers1.size() > 0)
//                                            userItem.setSelected(currentUsers1.contains(userItem.getUserOnlineId()));

                                        userList.add(userItem);
//
                                    }
                                }

                                if (currentUsers.size() > 0) {
                                    binding.cgUsers.removeAllViews();
                                    for (UserItem ui : userList) {
                                        if (currentUsers.contains(ui.getUserOnlineId())) {
                                            Chip chip = new Chip(AddActionPlanTask.this);
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
                                    isUserDataReceived = true;
//                                    if (isGroupDataReceived)
                                    LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(AddActionPlanTask.this, message, Toast.LENGTH_SHORT).show();
                                isUserDataReceived = true;
//                                if (isGroupDataReceived)
                                LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isUserDataReceived = true;
//                            if (isGroupDataReceived)
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
//                        if (isGroupDataReceived)
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void getUsers1(int pageNo) {
        if (pageNo == PAGE_START) {
            LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
            isUserDataReceived = false;
        }


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
                                    userList1.clear();
//

                                    UserItem selfItem = new UserItem();
                                    selfItem.setUserId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                                    selfItem.setUserName(LanguageExtension.setText("assign_to_self", getString(R.string.assign_to_self)));
                                    userList1.add(selfItem);
//
                                }

                                if (users.length() > 0) {
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
                                        UserItem userItem1 = new UserItem();
                                        userItem1.setUserOnlineId(user.getInt("user_id"));
                                        userItem1.setUserName(user.getString("user_name"));
                                        userItem1.setUserStatus(user.getInt("user_status"));

                                        if (user.has("user_email") && !user.isNull("user_email"))
                                            userItem1.setUserEmail(user.getString("user_email"));

                                        if (user.has("user_phone_no") && !user.isNull("user_phone_no"))
                                            userItem1.setUserPhone(user.getString("user_phone_no"));

                                        if (user.has("role_id") && !user.isNull("role_id"))
                                            userItem1.setRoleId(user.getInt("role_id"));

                                        if (user.has("role_name") && !user.isNull("role_name"))
                                            userItem1.setRoleName(user.getString("role_name"));

                                        if (currentUsers1.size() > 0)
                                            userItem1.setSelected(currentUsers1.contains(userItem1.getUserOnlineId()));
//
//                                        if (currentUsers1.size() > 0)
//                                            userItem.setSelected(currentUsers1.contains(userItem.getUserOnlineId()));

                                        userList1.add(userItem1);
//
                                    }
                                }

                                if (currentUsers1.size() > 0) {
                                    binding.cgGroups.removeAllViews();
                                    for (UserItem ui : userList1) {
                                        if (currentUsers1.contains(ui.getUserOnlineId())) {
                                            Chip chip = new Chip(AddActionPlanTask.this);
                                            chip.setText(ui.getUserName());

                                            binding.cgGroups.addView(chip);
                                        }
                                    }

                                    binding.tvEmptyGroup.setVisibility(View.INVISIBLE);
                                    binding.cgGroups.setVisibility(View.VISIBLE);
                                }


                                if (currentPage < totalPage) {
                                    getUsers1(++currentPage);
                                } else {
                                    isUserDataReceived = true;
//                                    if (isGroupDataReceived)
                                    LoadingDialogExtension.hideLoading();
                                }
                            } else {
                                Toast.makeText(AddActionPlanTask.this, message, Toast.LENGTH_SHORT).show();
                                isUserDataReceived = true;
//                                if (isGroupDataReceived)
                                LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            isUserDataReceived = true;
//                            if (isGroupDataReceived)
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
//                        if (isGroupDataReceived)
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
            binding.cgUsers.removeAllViews();
            currentUsers.clear();
            currentUsers.add(item.getUserOnlineId());
            Chip chip = new Chip(this);
            chip.setText(item.getUserName());

            binding.cgUsers.addView(chip);

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

    private void openGroupDialog(Context context, List<UserItem> options) {
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

        adapterG = new ActionPlanUserSpinnerAdapter(context, options, (item, position) -> {
            binding.cgGroups.removeAllViews();
            currentUsers1.clear();
            currentUsers1.add(item.getUserOnlineId());
            Chip chip = new Chip(this);
            chip.setText(item.getUserName());

            binding.cgGroups.addView(chip);

            if (currentUsers1.size() > 0) {
                binding.tvEmptyGroup.setVisibility(View.INVISIBLE);
                binding.cgGroups.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyGroup.setVisibility(View.VISIBLE);
                binding.cgGroups.setVisibility(View.GONE);
            }

            alertDialog.dismiss();
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
        System.out.println("String.valueOf(projectId)" + String.valueOf(projectId));
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PLANNER_SAVE_TASK)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("save-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", "Response of ADDTASK" + response);

                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddActionPlanTask.this, message, Toast.LENGTH_SHORT).show();
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
        System.out.println("parameters:-" + parameters);
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PLANNER_SAVE_TASK_EDIT)
                .addBodyParameter(parameters)

                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("save-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", "Response of ADDTASK" + response);

                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddActionPlanTask.this, message, Toast.LENGTH_SHORT).show();
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


//    private void getEditDataFromDB() {
//        taskItem = dbHandler.getTaskDetails(taskId);
//        setTaskDetails();
//
//        currentUsers = GeneralExtension.toLongList(taskItem.getUserIds());
//        currentGroups = GeneralExtension.toLongList(taskItem.getGroupIds());
//
//        getUserListFromDB();
////        getGroupListFromDB();
//    }

//    private void getUserListFromDB() {
//        userList.clear();
//
//        UserItem selfItem = new UserItem();
//        selfItem.setUserOnlineId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
//        selfItem.setUserName("Assign To Self");
//        userList.add(selfItem);
//        userList.addAll(dbHandler.getUsers(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        if (currentUsers.size() > 0) {
//            for (UserItem ui : userList) {
//                Log.e(TAG, "getUserListFromDB: " + ui.getUserOnlineId());
//                ui.setSelected(currentUsers.contains(ui.getUserOnlineId()));
//            }
//
//            binding.cgUsers.removeAllViews();
//            for (UserItem ui : userList) {
//                if (currentUsers.contains(ui.getUserOnlineId())) {
//                    Chip chip = new Chip(AddActionPlanTask.this);
//                    chip.setText(ui.getUserName());
//
//                    binding.cgUsers.addView(chip);
//                }
//            }
//            binding.tvEmptyUser.setVisibility(View.INVISIBLE);
//            binding.cgUsers.setVisibility(View.VISIBLE);
//        }
//    }

//    private void getGroupListFromDB() {
//        groupList.clear();
//        groupList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        if (currentGroups.size() > 0) {
//            for (RoleItem ri : groupList) {
//                ri.setSelected(currentGroups.contains(ri.getRoleOnlineId()));
//            }
//
//            binding.cgGroups.removeAllViews();
//            for (RoleItem ri : groupList) {
//                if (currentGroups.contains(ri.getRoleOnlineId())) {
//                    Chip chip = new Chip(AddActionPlanTask.this);
//                    chip.setText(ri.getRoleName());
//
//                    binding.cgGroups.addView(chip);
//                }
//            }
//            binding.tvEmptyGroup.setVisibility(View.INVISIBLE);
//            binding.cgGroups.setVisibility(View.VISIBLE);
//        }
//    }