package com.safra;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;
import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_LIST_API;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.GROUP_VIEW_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REPORT_URL;
import static com.safra.utilities.Common.REQUEST_APP_UPDATE;
import static com.safra.utilities.Common.REQUEST_DELETE_TASK;
import static com.safra.utilities.Common.TASK_DELETE_API;
import static com.safra.utilities.Common.TASK_LIST_API;
import static com.safra.utilities.Common.TASK_STATUS_API;
import static com.safra.utilities.Common.TASK_VIEW_API;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.LanguageManager.languageManager;
import static com.safra.utilities.UserPermissions.FORM_ASSIGN;
import static com.safra.utilities.UserPermissions.FORM_LIST;
import static com.safra.utilities.UserPermissions.FORM_RESPONSES;
import static com.safra.utilities.UserPermissions.FORM_STATUS;
import static com.safra.utilities.UserPermissions.FORM_SUBMIT;
import static com.safra.utilities.UserPermissions.FORM_UPDATE;
import static com.safra.utilities.UserPermissions.GROUP_DELETE;
import static com.safra.utilities.UserPermissions.GROUP_LIST;
import static com.safra.utilities.UserPermissions.GROUP_UPDATE;
import static com.safra.utilities.UserPermissions.REPORT_LIST;
import static com.safra.utilities.UserPermissions.TASK_DELETE;
import static com.safra.utilities.UserPermissions.TASK_LIST;
import static com.safra.utilities.UserPermissions.TASK_UPDATE;
import static com.safra.utilities.UserPermissions.USER_DELETE;
import static com.safra.utilities.UserPermissions.USER_LIST;
import static com.safra.utilities.UserPermissions.USER_STATUS;
import static com.safra.utilities.UserPermissions.USER_UPDATE;
import static com.safra.utilities.UserPermissions.USER_VIEW;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.safra.adapters.ModuleRecyclerAdapter;
import com.safra.adapters.TaskRecyclerAdapter;
import com.safra.databinding.ActivityDashboardBinding;
import com.safra.databinding.PopupChangeTaskStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.dialogs.SignOutDialog;
import com.safra.events.ConnectivityChangedEvent;
import com.safra.events.LanguageChangedEvent;
import com.safra.events.LanguagesReceivedEvent;
import com.safra.events.ProfileReceivedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.fragments.ActionPlanFragment;
import com.safra.fragments.ActiveVisitsFragment;
import com.safra.fragments.AppointmentListFragment;
import com.safra.fragments.AppointmentScheduleFragment;
import com.safra.fragments.BudgetFragment;
import com.safra.fragments.CaptureVitalsFragment;
import com.safra.fragments.DashboardFragment;
import com.safra.fragments.DiagnosticsFragment;
import com.safra.fragments.FormsFragment;
import com.safra.fragments.HealthRecordFragment;
import com.safra.fragments.MedicalReportsFragment;
import com.safra.fragments.MedicineFragment;
import com.safra.fragments.PatientFragment;
import com.safra.fragments.PlannerFragment;
import com.safra.fragments.ProjectsFragment;
import com.safra.fragments.AllergiesFragment;
import com.safra.fragments.SchedulesFragment;
import com.safra.fragments.TaskDetailFragment;
import com.safra.fragments.TasksFragment;
import com.safra.fragments.UserGroupsFragment;
import com.safra.fragments.UsersFragment;
import com.safra.models.FormItem;
import com.safra.models.LanguageItem;
import com.safra.models.ModuleItem;
import com.safra.models.PermissionItem;
import com.safra.models.RoleItem;
import com.safra.models.TaskAssignedMemberItem;
import com.safra.models.TaskItem;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.GeneralApiCallAndUse;
import com.safra.utilities.SyncData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "dashboard_activity";

    private ActivityDashboardBinding binding;

    private ImageView appLogo, userProfile;
    private TextView userName, userEmail;
    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean doubleBackToExitPressedOnce = false;
    private final List<TaskItem> taskList = new ArrayList<>();
    private TaskRecyclerAdapter adapter;
    private Fragment fragment;
    private String tag;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;
    private PopupWindow popupWindow;
    private boolean isRemembered, isAgency;
    private boolean isLoadedOnline = false;
    private long taskId, onlineId;
    private final List<TaskAssignedMemberItem> memberList = new ArrayList<>();
    private final List<ModuleItem> moduleList = new ArrayList<>();
    private final List<RoleItem> roleList = new ArrayList<>();
    private final List<RoleItem> roleData = new ArrayList<>();
    private ModuleRecyclerAdapter adapterM;
    private final List<UserItem> userList = new ArrayList<>();
    private final List<FormItem> formList = new ArrayList<>();


    private boolean isOnlineLoaded = false;


    private boolean isNew;
    private long roleId = -1;
    private RoleItem roleItem = null;
    private final List<Long> currentPermissions = new ArrayList<>();
//    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.layoutAppBar.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        isRemembered = userSessionManager.isRemembered();
        isAgency = isRemembered ? userSessionManager.isAgency() : Safra.isAgency;

        Log.e(TAG, "onCreate: " + (isRemembered ? userSessionManager.getUserToken() : Safra.userToken));
        Log.e(TAG, "onCreate: " + userSessionManager.getFcmToken());

        adapter = new TaskRecyclerAdapter(Dashboard.this, new TaskRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(TaskItem item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_TASK);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_task", getString(R.string.do_you_want_to_delete_this_task)));
                bundle.putLong("id", item.getTaskId());
                bundle.putLong("online_id", item.getTaskOnlineId());
                bundle.putInt("position", position);
                bundle.putString("type", "task");
                dialogD.setArguments(bundle);
//                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            //            public void addFormList(List<FormItem> formList) {
//                this.formList.addAll(formList);
//                this.formData.addAll(formList);
//            }
            public void setPopUpWindowForChangeStatus(View parentView, long taskId, long onlineId, String currentStatus) {
                Log.e(TAG, "setPopUpWindowForChangeStatus: starting...");
                PopupChangeTaskStatusBinding popupBinding = PopupChangeTaskStatusBinding.inflate(getLayoutInflater());

                Log.e(TAG, "setPopUpWindowForChangeStatus: currentStatus -> " + currentStatus);
                if (currentStatus.equalsIgnoreCase("pending")) {
                    popupBinding.tvApprove.setVisibility(View.VISIBLE);
                    popupBinding.tvReject.setVisibility(View.VISIBLE);
                    popupBinding.tvComplete.setVisibility(View.GONE);
                } else if (currentStatus.equalsIgnoreCase("approved")) {
                    popupBinding.tvComplete.setVisibility(View.VISIBLE);
                    popupBinding.tvApprove.setVisibility(View.GONE);
                    popupBinding.tvReject.setVisibility(View.GONE);
                }

//        switch(currentStatus){
//            case "Pending":
//                approve.setVisibility(View.VISIBLE);
//                reject.setVisibility(View.VISIBLE);
//                complete.setVisibility(View.GONE);
//                break;
//            case "Approved":
//                complete.setVisibility(View.VISIBLE);
//                approve.setVisibility(View.GONE);
//                reject.setVisibility(View.GONE);
//                break;
//        }

                popupBinding.tvApprove.setOnClickListener(v -> {
                    if (ConnectivityReceiver.isConnected())
                        changeTaskStatus(onlineId, "A");
                    else
                        changeTaskStatusOffline(taskId, "A");
                    popupWindow.dismiss();
                });
                popupBinding.tvReject.setOnClickListener(v -> {
                    if (ConnectivityReceiver.isConnected())
                        changeTaskStatus(onlineId, "R");
                    else
                        changeTaskStatusOffline(taskId, "R");
                    popupWindow.dismiss();
                });
                popupBinding.tvComplete.setOnClickListener(v -> {
                    if (ConnectivityReceiver.isConnected())
                        changeTaskStatus(onlineId, "C");

                    else
                        changeTaskStatusOffline(taskId, "C");
                    popupWindow.dismiss();
                });

                Log.e(TAG, "setPopUpWindowForChangeStatus: setting up...");
                popupWindow = new PopupWindow(popupBinding.getRoot(), getResources().getDimensionPixelSize(R.dimen.group_edit_popup_width), ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

                popupWindow.setOutsideTouchable(true);
                // Removes default background.
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                popupWindow.setElevation(10f);

                popupWindow.showAsDropDown(parentView, getResources().getDimensionPixelOffset(R.dimen._0dp), getResources().getDimensionPixelOffset(R.dimen._0dp), Gravity.TOP | Gravity.END);
            }

            //            public void addUserList(List<UserItem> userList) {
//                this.userList.addAll(userList);
//                this.userData.addAll(userList);
//            }
            public void changeTaskStatusOffline(long taskId, String taskStatus) {
                long i = dbHandler.updateTaskStatusOffline(taskId, taskStatus);
                if (i > 0) {
                    if (ConnectivityReceiver.isConnected()) {
                        isLoadedOnline = true;
                        currentPage = PAGE_START;
                        getTasks(pPosition);
                    } else {
                        isLoadedOnline = false;
                        getTasksFromDB();
                    }
                }
            }

            private void getTasksFromDB() {
                taskList.clear();

                taskList.addAll(dbHandler.getTasks((isRemembered ? userSessionManager.getUserId() : Safra.userId),
                        (isRemembered ? userSessionManager.getUserRoleId() : Safra.userRoleId)));

                for (TaskItem taskItem : taskList) {
                    if (taskItem.getAddedBy() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)
                            || taskItem.getMasterId() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                        taskItem.setEditable(true);
                        taskItem.setDeletable(true);
                    }

                    List<Long> cUserIds = Arrays.asList(taskItem.getUserIds());
                    List<Long> cGroupIds = Arrays.asList(taskItem.getGroupIds());
                    List<Long> cAllUserIds = Arrays.asList(taskItem.getAllUserIds());

                    if (cUserIds.contains(isRemembered ? userSessionManager.getUserId() : Safra.userId)
                            || cGroupIds.contains(isRemembered ? userSessionManager.getUserRoleId() : Safra.userRoleId)
                            || cAllUserIds.contains(isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                        taskItem.setChangeable(true);
                    }

                    if (PermissionExtension.checkForPermission(TASK_UPDATE))
                        taskItem.setEditable(true);

                    if (PermissionExtension.checkForPermission(TASK_DELETE))
                        taskItem.setDeletable(true);
//
//            if (PermissionExtension.checkForPermission(TASK_STATUS))
//                taskItem.setChangeable(true);
                }

                adapter.clearLists();
                adapter.addTaskList(taskList);
                Log.e(TAG, "getTasksFromDB: " + adapter.getItemCount());

//                checkForEmptyState();
//
//                if (binding.srlTasks.isRefreshing())
//                    binding.srlTasks.setRefreshing(false);
            }

            @Override
            public void onEdit(TaskItem item, int position) {
                Intent i = new Intent(Dashboard.this, AddTask.class);
                i.putExtra("heading", LanguageExtension.setText("edit_task", getString(R.string.edit_task)));
                i.putExtra("is_new", false);
                i.putExtra("task_id", item.getTaskId());
                i.putExtra("online_id", item.getTaskOnlineId());
                startActivity(i);
            }

            @Override
            public void changeStatus(View view, TaskItem item, int position) {
                setPopUpWindowForChangeStatus(view, item.getTaskId(), item.getTaskOnlineId(), item.getTaskStatus());
            }

            @Override
            public void viewTask(TaskItem item, int position) {
                TaskDetailFragment dialogD = new TaskDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("task_id", item.getTaskId());
                bundle.putLong("online_id", item.getTaskOnlineId());
                dialogD.setArguments(bundle);
                dialogD.show(Dashboard.this.getSupportFragmentManager(), TaskDetailFragment.TAG);
            }
        });


//        if (ConnectivityReceiver.isConnected()) {
//            GeneralApiCallAndUse.getLanguages(this, TAG);
//            GeneralApiCallAndUse.getFormTypes(this, TAG);
//            GeneralApiCallAndUse.getModulesAndPermissions(this, TAG,
//                    isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
//            if (!Safra.isFormSyncing)
//                SyncData.uploadUnsyncedForms(TAG);
//            if (!Safra.isTaskSyncing)
//                SyncData.uploadUnsyncedTasks(TAG);
//            if (!Safra.isUserSyncing)
//                SyncData.uploadUnsyncedUsers(TAG);
//            if (!Safra.isGroupSyncing)
//                SyncData.uploadUnsyncedGroups(TAG);
//            if (!Safra.isResponseSyncing)
//                SyncData.uploadUnsyncedResponses(TAG);
//            if (!Safra.isTemplateSyncing)
//                SyncData.syncTemplates(TAG, languageManager.getLanguage(), PAGE_START);
//        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.layoutAppBar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
        binding.navView.setNavigationItemSelectedListener(this);

        View hView = binding.navView.getHeaderView(0);
        appLogo = hView.findViewById(R.id.ivAppLogo);
        userProfile = hView.findViewById(R.id.ivProfileImage);
        userName = hView.findViewById(R.id.tvUserName);
        userEmail = hView.findViewById(R.id.tvUserEmail);

        if (getIntent().hasExtra("fragment_to_launch")) {
            Bundle bundle = new Bundle();
            bundle.putLong("reference_id", getIntent().getLongExtra("reference_id", -1));
            switch ((int) getIntent().getLongExtra("fragment_to_launch", 0)) {
                case 2:
                    fragment = new FormsFragment();
                    fragment.setArguments(bundle);
                    tag = FormsFragment.TAG;
                    binding.navView.getMenu().findItem(R.id.nav_forms).setChecked(true);
                    break;
                case 3:
                    fragment = new TasksFragment();
                    fragment.setArguments(bundle);
                    tag = TasksFragment.TAG;
                    binding.navView.getMenu().findItem(R.id.nav_tasks).setChecked(true);
                    break;
            }
            setTitle();
            loadFragment(fragment, tag);
        } else {
            recoverToPreviousState(savedInstanceState);
        }
        manageNavigationMenuItems();
        checkForAppUpdate(binding.navView);

        binding.layoutAppBar.ivsync.setOnClickListener(v -> {

            if (ConnectivityReceiver.isConnected()) {
                LoadingDialogExtension.showLoading(Dashboard.this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
                getTasks(pPosition);
                getUsers(pPosition);
                getGroups(pPosition);
                getForms(pPosition);

                if (!Safra.isFormSyncing)
                    SyncData.uploadUnsyncedForms(TAG);
                if (!Safra.isTaskSyncing)
                    SyncData.uploadUnsyncedTasks(TAG);
                if (!Safra.isUserSyncing)
                    SyncData.uploadUnsyncedUsers(TAG);
                if (!Safra.isGroupSyncing)
                    SyncData.uploadUnsyncedGroups(TAG);
                if (!Safra.isResponseSyncing)
                    SyncData.uploadUnsyncedResponses(TAG);
                if (!Safra.isTemplateSyncing)
                    SyncData.syncTemplates(TAG, languageManager.getLanguage(), PAGE_START);

                LoadingDialogExtension.hideLoading();

            }

        });

        binding.layoutAppBar.ivNotification.setOnClickListener(v -> {
            Intent notificationIntent = new Intent(this, Notifications.class);
            startActivity(notificationIntent);
        });

    }

    public void changeTaskStatus(long taskId, String taskStatus) {
        LoadingDialogExtension.showLoading(Dashboard.this, LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + TASK_STATUS_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("task_id", String.valueOf(taskId))
                .addBodyParameter("task_status", taskStatus)
                .setTag("change-task-status-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                if (ConnectivityReceiver.isConnected()) {
                                    isLoadedOnline = true;
                                    currentPage = PAGE_START;
                                    getTasks(pPosition);
                                } else {
                                    isLoadedOnline = false;
//                                    getTasksFromDB();
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                            dialogL.dismiss();
                        }
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

    public void getTasks(int pPosition) {
//        Safra.isGetTaskSyncing = true;
//        binding.srlTasks.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + TASK_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("search_text", searchText)
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
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    taskList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (tasks.length() > 0) {
                                    List<TaskItem> tList = new ArrayList<>();
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
                                            switch (taskItem.getTaskStatus()) {
                                                case "Pending":
                                                case "Approved":
                                                    taskItem.setChangeable(true);
                                                    break;
                                                default:
                                                    taskItem.setChangeable(false);
                                            }
                                        }

                                        taskItem.setMasterId(task.getInt("task_master_id"));

                                        if (task.has("task_group_ids") && !task.isNull("task_group_ids"))
                                            taskItem.setGroupIds(GeneralExtension.toLongArray(task.getString("task_group_ids"), ","));

                                        if (task.has("task_user_ids") && !task.isNull("task_user_ids"))
                                            taskItem.setUserIds(GeneralExtension.toLongArray(task.getString("task_user_ids"), ","));

                                        if (task.has("assigned_users") && !task.isNull("assigned_users")) {
                                            taskItem.setTaskUserStatus(task.getJSONArray("assigned_users").toString());
                                        }

                                        if (taskItem.getMasterId() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)
                                                || taskItem.getAddedBy() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                                            taskItem.setEditable(true);
                                            taskItem.setDeletable(true);
                                        }

                                        if (PermissionExtension.checkForPermission(TASK_UPDATE))
                                            taskItem.setEditable(true);

                                        if (PermissionExtension.checkForPermission(TASK_DELETE))
                                            taskItem.setDeletable(true);
//
//                                        if (PermissionExtension.checkForPermission(TASK_STATUS))
//                                            taskItem.setChangeable(true);

                                        tList.add(taskItem);
                                        dbHandler.addTask(taskItem);
                                    }

                                    taskList.addAll(tList);
                                    adapter.addTaskList(tList);
                                }

                                if (pPosition > 1 && pPosition <= taskList.size() - 1) {
                                    taskList.remove(pPosition);
                                    adapter.removeTask(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

//                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

//                        if (binding.srlTasks.isRefreshing())
//                            binding.srlTasks.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;
//                        Safra.isGetTaskSyncing = false;
//                        if (binding.srlTasks.isRefreshing())
//                            binding.srlTasks.setRefreshing(false);
                    }
                });
//        taskList.clear();
////        taskList.add(new TaskItem(1, "E-Commerce Form", "Assignee 1", "Feb 25, 2021", 1));
////        taskList.add(new TaskItem(2, "Survey Form", "Assignee 2", "Feb 28, 2021", 0));
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    private void recoverToPreviousState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            tag = savedInstanceState.getString("fragment_tag");
            switch (tag) {
                case DashboardFragment.TAG:
                    fragment = new DashboardFragment();
                    binding.navView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
                    break;
                case UsersFragment.TAG:
                    fragment = new UsersFragment();
                    binding.navView.getMenu().findItem(R.id.nav_users).setChecked(true);
                    break;
                case UserGroupsFragment.TAG:
                    fragment = new UserGroupsFragment();
                    binding.navView.getMenu().findItem(R.id.nav_user_groups).setChecked(true);
                    break;
                case FormsFragment.TAG:
                    fragment = new FormsFragment();
                    binding.navView.getMenu().findItem(R.id.nav_forms).setChecked(true);
                    break;
                case TasksFragment.TAG:
                    fragment = new TasksFragment();
                    binding.navView.getMenu().findItem(R.id.nav_tasks).setChecked(true);
                    break;
                case ProjectsFragment.TAG:
                    fragment = new ProjectsFragment();
                    binding.navView.getMenu().findItem(R.id.nav_project).setChecked(true);
                    break;
                case ActionPlanFragment.TAG:
                    fragment = new ActionPlanFragment();
                    binding.navView.getMenu().findItem(R.id.nav_action_plan).setChecked(true);
                    break;
                case SchedulesFragment.TAG:
                    fragment = new SchedulesFragment();
                    binding.navView.getMenu().findItem(R.id.nav_schedule).setChecked(true);
                    break;
                case BudgetFragment.TAG:
                    fragment = new BudgetFragment();
                    binding.navView.getMenu().findItem(R.id.nav_budget).setChecked(true);
                    break;
//                case FormReportsFragment.TAG:
//                    fragment = new FormReportsFragment();
//                    navigationView.getMenu().findItem(R.id.nav_reports).setChecked(true);
//                    break;
            }
        } else {
            if (isAgency) {
                fragment = new DashboardFragment();
                tag = DashboardFragment.TAG;
                binding.navView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
            } else {
                fragment = new FormsFragment();
                tag = FormsFragment.TAG;
                binding.navView.getMenu().findItem(R.id.nav_forms).setChecked(true);
            }
        }
        setTitle();
        loadFragment(fragment, tag);
    }

    private void manageNavigationMenuItems() {
        appLogo.setImageResource(languageManager.getLanguage() == 2
                ? R.drawable.safra_logo_pt : R.drawable.safra_logo_en);

        Menu menu = binding.navView.getMenu();

        menu.findItem(R.id.nav_dashboard).setTitle(LanguageExtension.setText("dashboard", getString(R.string.dashboard)));
        menu.findItem(R.id.nav_account).setTitle(LanguageExtension.setText("account", getString(R.string.account)));
        menu.findItem(R.id.nav_users).setTitle(LanguageExtension.setText("users", getString(R.string.users)));
        menu.findItem(R.id.nav_user_groups).setTitle(LanguageExtension.setText("user_groups", getString(R.string.user_groups)));
        menu.findItem(R.id.nav_tasks).setTitle(LanguageExtension.setText("tasks", getString(R.string.tasks)));
        menu.findItem(R.id.nav_forms).setTitle(LanguageExtension.setText("forms", getString(R.string.forms)));
        menu.findItem(R.id.nav_reports).setTitle(LanguageExtension.setText("reports", getString(R.string.reports)));
        menu.findItem(R.id.nav_logout).setTitle(LanguageExtension.setText("logout", getString(R.string.logout)));
        menu.findItem(R.id.nav_project).setTitle(LanguageExtension.setText("project", getString(R.string.project)));
        menu.findItem(R.id.nav_action_plan).setTitle(LanguageExtension.setText("action_plan", getString(R.string.action_plan)));
        menu.findItem(R.id.nav_schedule).setTitle(LanguageExtension.setText("schedule", getString(R.string.schedule)));
        menu.findItem(R.id.nav_budget).setTitle(LanguageExtension.setText("budget", getString(R.string.budget)));


        menu.findItem(R.id.nav_patient).setTitle(LanguageExtension.setText("Patients List", getString(R.string.patient)));
        menu.findItem(R.id.nav_active_visit).setTitle(LanguageExtension.setText("Active Visits", getString(R.string.active_visits)));
        menu.findItem(R.id.nav_appointment_schedule).setTitle(LanguageExtension.setText("Appointment Schedule", getString(R.string.appointment_schedule)));
        menu.findItem(R.id.nav_reg_patient).setTitle(LanguageExtension.setText("Allergies", getString(R.string.allergies)));
        menu.findItem(R.id.nav_capture_vitals).setTitle(LanguageExtension.setText("Capture Vitals", getString(R.string.capture_vitals)));
        menu.findItem(R.id.nav_health_reports).setTitle(LanguageExtension.setText("Health Record", getString(R.string.health_record)));
        menu.findItem(R.id.nav_medicine).setTitle(LanguageExtension.setText("Medicine", getString(R.string.medicine)));
        menu.findItem(R.id.nav_diagnostics).setTitle(LanguageExtension.setText("Diagnostics", getString(R.string.diagnostics)));




        if (Safra.permissionList.size() > 0) {
            if (!Safra.permissionList.contains(USER_LIST))
                menu.findItem(R.id.nav_users).setVisible(false);

            if (!Safra.permissionList.contains(GROUP_LIST))
                menu.findItem(R.id.nav_user_groups).setVisible(false);

            if (!Safra.permissionList.contains(FORM_LIST))
                menu.findItem(R.id.nav_forms).setVisible(false);

            if (!Safra.permissionList.contains(TASK_LIST))
                menu.findItem(R.id.nav_tasks).setVisible(false);

            if (!Safra.permissionList.contains(REPORT_LIST))
                menu.findItem(R.id.nav_reports).setVisible(false);
        }
        return;
    }

    private void setTitle() {
        String s;
        switch (tag) {
            case UsersFragment.TAG:
                s = LanguageExtension.setText("users", getString(R.string.users));
                break;
            case UserGroupsFragment.TAG:
                s = LanguageExtension.setText("user_groups", getString(R.string.user_groups));
                break;
            case FormsFragment.TAG:
                s = LanguageExtension.setText("forms", getString(R.string.forms));
                break;
            case TasksFragment.TAG:
                s = LanguageExtension.setText("tasks", getString(R.string.tasks));
                break;
            case PlannerFragment.TAG:
                s = LanguageExtension.setText("Planner", getString(R.string.planner));
                break;
            case ProjectsFragment.TAG:
                s = LanguageExtension.setText("project", getString(R.string.project));
                break;
            case ActionPlanFragment.TAG:
                s = LanguageExtension.setText("action_plan", getString(R.string.action_plan));
                break;
            case SchedulesFragment.TAG:
                s = LanguageExtension.setText("schedule", getString(R.string.schedule));
                break;
            case BudgetFragment.TAG:
                s = LanguageExtension.setText("budget", getString(R.string.budget));
                break;
            case HealthRecordFragment.TAG:
                s = LanguageExtension.setText("Health Record", getString(R.string.health_record));
                break;
            case PatientFragment.TAG:
                s = LanguageExtension.setText("Patients List", getString(R.string.patient));
                break;
            case ActiveVisitsFragment.TAG:
                s = LanguageExtension.setText("Action Visits", getString(R.string.active_visits));
                break;
            case AppointmentScheduleFragment.TAG:
                s = LanguageExtension.setText("Appointment Schedule", getString(R.string.appointment_schedule));
                break;
            case AllergiesFragment.TAG:
                s = LanguageExtension.setText("Allergies", getString(R.string.allergies));
                break;
            case CaptureVitalsFragment.TAG:
                s = LanguageExtension.setText("Capture Vitals", getString(R.string.capture_vitals));
                break;
            case MedicalReportsFragment.TAG:
                s = LanguageExtension.setText("Reports", getString(R.string.reports));
                break;
            case MedicineFragment.TAG:
                s = LanguageExtension.setText("Medicine", getString(R.string.medicine));
                break;
            case DiagnosticsFragment.TAG:
                s = LanguageExtension.setText("Diagnostics", getString(R.string.diagnostics));
                break;
            case AppointmentListFragment.TAG:
                s = LanguageExtension.setText("appointment_schedule", getString(R.string.appointment_schedule));
                break;
//            case FormReportsFragment.TAG:
//                s = LanguageExtension.setText("reports", getString(R.string.reports));
//                break;
            case DashboardFragment.TAG:
            default:
                s = LanguageExtension.setText("dashboard", getString(R.string.dashboard));
        }
        binding.layoutAppBar.tvDashboardHeading.setText(s);
    }

    private void setGroupDetails() {
        roleId = roleItem.getRoleId();
        onlineId = roleItem.getRoleOnlineId();
//        binding.etRoleName.setText(roleItem.getRoleName());
    }

    private void checkForAppUpdate(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.e(TAG, "checkForAppUpdate: checked");
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                menu.findItem(R.id.nav_update_app).setVisible(true);
            }
        });
        appUpdateInfoTask.addOnFailureListener(e -> Log.e(TAG, "checkForAppUpdate: " + e.getLocalizedMessage()));
    }

    private void getTaskDetails() {
//        showLayout(LOADING_CONDITION);

        AndroidNetworking
                .post(BASE_URL + TASK_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("task_id", String.valueOf(onlineId))
                .setTag("task-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject taskData = response.getJSONObject("data").getJSONObject("task_data");
                                TaskItem taskItem = new TaskItem();
                                taskItem.setTaskId(taskData.getLong("task_id"));
                                taskItem.setTaskName(taskData.getString("task_title"));
                                taskItem.setPriority(taskData.getInt("task_priority"));

                                taskItem.setStartDate(taskData.getLong("task_start_date") * 1000);
                                taskItem.setEndDate(taskData.getLong("task_end_date") * 1000);

                                if (!taskData.isNull("task_details"))
                                    taskItem.setTaskDetail(taskData.getString("task_details"));

                                if (!taskData.isNull("status"))
                                    taskItem.setTaskStatus(taskData.getString("status"));

                                JSONArray members = taskData.getJSONArray("assigned_users");
                                if (members.length() > 0) {
                                    memberList.clear();
                                    for (int i = 0; i < members.length(); i++) {
                                        JSONObject member = members.getJSONObject(i);
                                        TaskAssignedMemberItem memberItem = new TaskAssignedMemberItem();
                                        memberItem.setMemberId(member.getLong("user_id"));
                                        memberItem.setMemberName(member.getString("user_name"));
                                        memberItem.setMemberStatus(member.getString("status"));

                                        memberList.add(memberItem);
                                    }
                                    adapter.notifyDataSetChanged();
                                }

//                                setTaskDetail(taskItem);

//                                showLayout(SUCCESS_CONDITION);
                            } else {
                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
//                                showLayout(ERROR_CONDITION);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());

//                            showLayout(ERROR_CONDITION);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

//                        showLayout(ERROR_CONDITION);
                    }
                });
    }

    public void deleteTask(long taskId, int position) {
        LoadingDialogExtension.showLoading(Dashboard.this, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + TASK_DELETE_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("task_id", String.valueOf(taskId))
                .setTag("delete-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                taskList.remove(position);
                                adapter.removeTask(position);
//                                checkForEmptyState();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                            dialogL.dismiss();
                        }
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

    private void updateApp() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                            this, REQUEST_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "updateApp: " + e.getLocalizedMessage());
                }
            }
        });
    }
//    private void getPermissions() {
//        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
////        LoadingDialog dialogL = new LoadingDialog();
////        dialogL.setCancelable(false);
////        Bundle bundle = new Bundle();
////        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
////        dialogL.setArguments(bundle);
////        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);
//
//        AndroidNetworking
//                .post(BASE_URL + PERMISSION_LIST_API)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("permission-list-api")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        LoadingDialogExtension.hideLoading();
//                        try {
//                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            if (success == 1) {
//                                JSONArray modules = response.getJSONObject("data").getJSONArray("modules");
//                                if (modules.length() > 0) {
//                                    moduleList.clear();
//
//                                    for (int i = 0; i < modules.length(); i++) {
//                                        JSONObject module = modules.getJSONObject(i);
//                                        ModuleItem moduleItem = new ModuleItem();
//                                        moduleItem.setModuleId(module.getLong("module_id"));
//                                        moduleItem.setModuleName(module.getString("module_name"));
//                                        if (module.has("pt_module_name"))
//                                            moduleItem.setPtModuleName(module.getString("pt_module_name"));
//
//                                        JSONArray permissions = module.getJSONArray("permissions");
//                                        if (permissions.length() > 0) {
//                                            List<PermissionItem> permissionList = new ArrayList<>();
//                                            for (int j = 0; j < permissions.length(); j++) {
//                                                JSONObject permission = permissions.getJSONObject(j);
//                                                PermissionItem permissionItem = new PermissionItem();
//                                                permissionItem.setPermissionId(permission.getLong("permission_id"));
//                                                permissionItem.setPermissionName(permission.getString("permission_name"));
//                                                if (permission.has("pt_permission_name"))
//                                                    permissionItem.setPtPermissionName(permission.getString("pt_permission_name"));
//
//                                                if (currentPermissions.size() > 0)
//                                                    permissionItem.setSelected(currentPermissions
//                                                            .contains(permissionItem.getPermissionId()));
//
//                                                permissionList.add(permissionItem);
//                                            }
//
//                                            moduleItem.setPermissionList(permissionList);
//                                        }
//
//                                        moduleList.add(moduleItem);
//                                    }
//
//                                    adapterM.notifyDataSetChanged();
//                                }
//                            } else {
//                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                        }
////                        dialogL.dismiss();
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
//                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
//                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
//                        LoadingDialogExtension.hideLoading();
////                        dialogL.dismiss();
//                    }
//                });
//    }

    //    private void getEditData() {
//        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
////        LoadingDialog dialogL = new LoadingDialog();
////        dialogL.setCancelable(false);
////        Bundle bundle = new Bundle();
////        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
////        dialogL.setArguments(bundle);
////        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);
//
//        AndroidNetworking
//                .post(BASE_URL + GROUP_VIEW_API)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("role_id", String.valueOf(onlineId))
//                .setTag("role-detail-api")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        LoadingDialogExtension.hideLoading();
//                        try {
//                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            if (success == 1) {
//                                JSONObject roleData = response.getJSONObject("data").getJSONObject("role_data");
//                                roleItem = new RoleItem();
//                                roleItem.setRoleOnlineId(roleData.getLong("role_id"));
//                                roleItem.setRoleName(roleData.getString("role_name"));
//                                roleItem.setAddedBy(roleData.getLong("added_by"));
//
//                                JSONArray modules = roleData.getJSONArray("moduless");
//                                if (modules.length() > 0) {
//                                    currentPermissions.clear();
//
//                                    for (int i = 0; i < modules.length(); i++) {
//                                        JSONObject module = modules.getJSONObject(i);
//                                        JSONArray permissions = module.getJSONArray("permissions");
//                                        if (permissions.length() > 0) {
//                                            for (int j = 0; j < permissions.length(); j++) {
//                                                JSONObject permission = permissions.getJSONObject(j);
//                                                currentPermissions.add(permission.getLong("permission_id"));
//                                            }
//                                        }
//                                    }
//                                }
//
//                                setGroupDetails();
//
////                                getPermissions();
//
//                            } else {
//                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                        }
//
////                        dialogL.dismiss();
//                    }
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
    private void getGroups(int pPosition) {
//        binding.srlManageGroup.setRefreshing(currentPage == PAGE_START);
//        Safra.isGetGroupSyncing = true;
        isNextPageCalled = true;

        AndroidNetworking
                .post(BASE_URL + GROUP_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("search_text", searchText)
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
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    roleList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (roles.length() > 0) {
                                    List<RoleItem> gList = new ArrayList<>();
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

                                        if (roleItem.getAddedBy() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                                            roleItem.setEditable(true);
                                            roleItem.setDeletable(true);
                                        }

                                        if (PermissionExtension.checkForPermission(GROUP_UPDATE))
                                            roleItem.setEditable(true);

                                        if (PermissionExtension.checkForPermission(GROUP_DELETE))
                                            roleItem.setDeletable(true);

                                        gList.add(roleItem);
                                        dbHandler.addGroup(roleItem);
                                    }

                                    roleList.addAll(gList);
//                                    adapter.addGroupList(gList);
                                }

                                if (pPosition > 1 && pPosition <= roleList.size() - 1) {
                                    roleList.remove(pPosition);
//                                    adapter.removeGroup(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

                                Log.e(TAG, "onResponse: " + adapter.getItemCount());

//                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

//                        if (binding.srlManageGroup.isRefreshing())
//                            binding.srlManageGroup.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;
//                        Safra.isGetGroupSyncing = false;
//                        if (binding.srlManageGroup.isRefreshing())
//                            binding.srlManageGroup.setRefreshing(false);
                    }
                });

//        groupList.clear();
//        groupList.add(new GroupItem(1, "E-Commerce Group", 10, 200, false));
//        groupList.add(new GroupItem(2, "Survey Group", 5, 100, false));

//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    private void getGroupDetails() {
//        showLayout(LOADING_CONDITION);

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

//                                showLayout(SUCCESS_CONDITION);
                            } else {
                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
//                                showLayout(ERROR_CONDITION);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());

//                            showLayout(ERROR_CONDITION);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

//                        showLayout(ERROR_CONDITION);
                    }
                });
    }

    private void getUsers(int pPosition) {
//        binding.srlManageUser.setRefreshing(currentPage == PAGE_START);
//        Safra.isGetUserSyncing = true;
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + USER_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("search_text", searchText)
                .setTag("user-list-api")
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
                                JSONArray users = data.getJSONArray("user_list");
                                int totalPage = data.getInt("total_page");
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (users.length() > 0) {
                                    List<UserItem> uList = new ArrayList<>();
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

                                        if (PermissionExtension.checkForPermission(USER_VIEW))
                                            userItem.setViewable(true);

                                        if (PermissionExtension.checkForPermission(USER_DELETE))
                                            userItem.setDeletable(true);

                                        if (PermissionExtension.checkForPermission(USER_UPDATE))
                                            userItem.setEditable(true);

                                        if (PermissionExtension.checkForPermission(USER_STATUS))
                                            userItem.setChangeable(true);

                                        uList.add(userItem);
                                        dbHandler.addUser(userItem);
                                    }

                                    userList.addAll(uList);
//                                    adapter.addUserList(uList);
                                }

                                if (pPosition > 1 && pPosition <= userList.size() - 1) {
                                    userList.remove(pPosition);
//                                    adapter.removeUser(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

//                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;
//
//                        if (binding.srlManageUser.isRefreshing())
//                            binding.srlManageUser.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;
//                        Safra.isGetGroupSyncing = false;
//                        if (binding.srlManageUser.isRefreshing())
//                            binding.srlManageUser.setRefreshing(false);
                    }
                });

//        userList.clear();
//        userList.add(new UserItem(1, "John Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//        userList.add(new UserItem(2, "Jane Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    private void getForms(int pPosition) {
//        binding.srlForms.setRefreshing(currentPage == PAGE_START);
//        Safra.isGetFormSyncing = true;
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + FORM_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("search_text", searchText)
                .setTag("form-list-api")
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
                                JSONArray forms = data.getJSONArray("form_list");
                                int totalPage = data.getInt("total_page");
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    formList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (forms.length() > 0) {
                                    List<FormItem> fList = new ArrayList<>();
                                    for (int i = 0; i < forms.length(); i++) {
                                        JSONObject form = forms.getJSONObject(i);
                                        FormItem formItem = new FormItem();
                                        formItem.setFormOnlineId(form.getLong("form_id"));
                                        formItem.setFormUniqueId(form.getString("form_unique_id"));
                                        formItem.setFormName(form.getString("form_name"));
                                        Log.e(TAG, "onResponse: " + formItem.getFormName());
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

                                        if (formItem.getFormStatus() == 1 &&
                                                PermissionExtension.checkForPermission(FORM_SUBMIT))
                                            formItem.setFillable(true);

                                        formItem.setDelete(form.getInt("is_delete") == 1);

                                        formItem.setFormUserId(form.getLong("form_master_id"));
                                        if (formItem.getFormUserId() ==
                                                (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                                            formItem.setEditable(true);
                                            formItem.setResponseViewable(true);
                                        }

                                        if (PermissionExtension.checkForPermission(FORM_UPDATE))
                                            formItem.setEditable(true);

                                        if (PermissionExtension.checkForPermission(FORM_RESPONSES))
                                            formItem.setResponseViewable(true);

                                        if (PermissionExtension.checkForPermission(FORM_STATUS))
                                            formItem.setStatusChangeable(true);

                                        if (PermissionExtension.checkForPermission(FORM_ASSIGN))
                                            formItem.setAssignable(true);

                                        fList.add(formItem);
                                        dbHandler.addForm(formItem);
                                    }

                                    formList.addAll(fList);
//                                    adapter.addFormList(fList);
                                }

                                if (pPosition > 1 && pPosition <= formList.size() - 1) {
                                    Log.e(TAG, "onResponse: removing progress bar -> " + pPosition);
                                    formList.remove(pPosition);
//                                    adapter.removeForm(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

//                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;

                                if (!isLastPage)
                                    if (ConnectivityReceiver.isConnected())
//                                        loadMoreItems();
//                            } else {
                                        Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

//                        if (binding.srlForms.isRefreshing())
//                            binding.srlForms.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;
//                        Safra.isGetFormSyncing = false;
//                        if (binding.srlForms.isRefreshing())
//                            binding.srlForms.setRefreshing(false);
                    }
                });

//        formList.clear();
//        formList.add(new FormItem());
//        formList.add(new FormItem());
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("fragment_tag", tag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!tag.equals(DashboardFragment.TAG)) {
            fragment = new DashboardFragment();
            tag = DashboardFragment.TAG;
            binding.navView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
            setTitle();
            loadFragment(fragment, tag);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(findViewById(R.id.container_dashboard),
                    LanguageExtension.setText("click_on_back_again_to_exit", getString(R.string.click_on_back_again_to_exit)),
                    Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (id == R.id.nav_dashboard) {
            if (!tag.equals(DashboardFragment.TAG)) {
                fragment = new DashboardFragment();
                tag = DashboardFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }

        } else if (id == R.id.nav_account) {
            startActivity(new Intent(Dashboard.this, Account.class));
//            return false;
//            if (!tag.equals(QuoteListFragment.TAG)) {
//                fragment = new QuoteListFragment();
//                TAG = QuoteListFragment.TAG;
//            }
        } else if (id == R.id.nav_users) {
            if (!tag.equals(UsersFragment.TAG)) {
                fragment = new UsersFragment();
                tag = UsersFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        } else if (id == R.id.nav_user_groups) {
            if (!tag.equals(UserGroupsFragment.TAG)) {
                fragment = new UserGroupsFragment();
                tag = UserGroupsFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        } else if (id == R.id.nav_forms) {
            if (!tag.equals(FormsFragment.TAG)) {
                fragment = new FormsFragment();
                tag = FormsFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        } else if (id == R.id.nav_tasks) {
            if (!tag.equals(TasksFragment.TAG)) {
                fragment = new TasksFragment();
                tag = TasksFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        } else if (id == R.id.nav_planner) {
            if (!tag.equals(PlannerFragment.TAG)) {
                fragment = new PlannerFragment();
                tag = PlannerFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        } else if (id == R.id.nav_health_record) {
            if (!tag.equals(HealthRecordFragment.TAG)) {
                fragment = new HealthRecordFragment();
                tag = HealthRecordFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        }else if (id == R.id.nav_project) {
            if (!tag.equals(ProjectsFragment.TAG)) {
                fragment = new ProjectsFragment();
                tag = ProjectsFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        }
        else if (id == R.id.nav_action_plan) {
            if (!tag.equals(ActionPlanFragment.TAG)) {
                fragment = new ActionPlanFragment();
                tag = ActionPlanFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        }
        else if (id == R.id.nav_schedule) {
            if (!tag.equals(SchedulesFragment.TAG)) {
                fragment = new SchedulesFragment();
                tag = SchedulesFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        }
        else if (id == R.id.nav_budget) {
            if (!tag.equals(BudgetFragment.TAG)) {
                fragment = new BudgetFragment();
                tag = BudgetFragment.TAG;
                setTitle();
                return loadFragmentAndReturn(drawer);
            }
        }

        else if (id == R.id.nav_reports) {
//            if (!tag.equals(FormReportsFragment.TAG)) {
//                fragment = new FormReportsFragment();
//                tag = FormReportsFragment.TAG;
//                setTitle();
//                return loadFragmentAndReturn(drawer);
//            }
            if (ConnectivityReceiver.isConnected()) {
                String s = REPORT_URL + (isRemembered ? userSessionManager.getUserToken() : Safra.userToken);

                Intent i = new Intent(this, WebActivity.class);
                i.putExtra("heading", LanguageExtension.setText("reports", getString(R.string.reports)));
                i.putExtra("url", s);
                startActivity(i);

//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                CustomTabColorSchemeParams.Builder customTabColorSchemeParamsBuilder =
//                        new CustomTabColorSchemeParams.Builder();
//                customTabColorSchemeParamsBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.color_primary));
//                customTabColorSchemeParamsBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.color_primary));
//                builder.setDefaultColorSchemeParams(customTabColorSchemeParamsBuilder.build());
//                builder.setShowTitle(false);
//                builder.setUrlBarHidingEnabled(false);
//                CustomTabsIntent intent = builder.build();
//                intent.launchUrl(this, Uri.parse(s));
            } else {
                Toast.makeText(this, LanguageExtension.setText("you_need_to_connect_to_internet_to_view_reports", getString(R.string.you_need_to_connect_to_internet_to_view_reports)), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_update_app) {
            updateApp();
        } else if (id == R.id.nav_logout) {
            SignOutDialog dialogS = new SignOutDialog();
            dialogS.show(getSupportFragmentManager(), SignOutDialog.TAG);
        }
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    public void changeFragment(Fragment fragment, String tag) {
        this.fragment = fragment;
        this.tag = tag;
        switch (tag) {
            case UserGroupsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_user_groups).setChecked(true);
                break;
            case UsersFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_users).setChecked(true);
                break;
            case TasksFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_tasks).setChecked(true);
                break;
            case FormsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_forms).setChecked(true);
                break;
            case ProjectsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_project).setChecked(true);
                break;
            case ActionPlanFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_action_plan).setChecked(true);
                break;
            case SchedulesFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_schedule).setChecked(true);
                break;
            case BudgetFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_budget).setChecked(true);
                break;
        }

        setTitle();
        loadFragment(fragment, tag);

    }
    public void changeHealthFragment(Fragment fragment, String tag) {
        this.fragment = fragment;
        this.tag = tag;
        switch (tag) {
            case PatientFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_patient).setChecked(true);
                break;
            case ActiveVisitsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_active_visit).setChecked(true);
                break;
            case AppointmentScheduleFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_appointment_schedule).setChecked(true);
                break;
            case AllergiesFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_reg_patient).setChecked(true);
                break;
            case CaptureVitalsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_capture_vitals).setChecked(true);
                break;
            case MedicalReportsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_health_reports).setChecked(true);
                break;
            case MedicineFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_medicine).setChecked(true);
                break;
            case DiagnosticsFragment.TAG:
                binding.navView.getMenu().findItem(R.id.nav_budget).setChecked(true);
                break;
        }

        setTitle();
        loadFragment(fragment, tag);

    }

//    public void changePlannerFragment(Fragment fragment, String tag) {
//        this.fragment = fragment;
//        this.tag = tag;
//        switch (tag) {
//            case ProjectsFragment.TAG:
//                binding.navView.getMenu().findItem(R.id.nav_project).setChecked(true);
//                break;
//            case ActionPlanFragment.TAG:
//                binding.navView.getMenu().findItem(R.id.nav_action_plan).setChecked(true);
//                break;
//            case SchedulesFragment.TAG:
//                binding.navView.getMenu().findItem(R.id.nav_schedule).setChecked(true);
//                break;
//            case BudgetFragment.TAG:
//                binding.navView.getMenu().findItem(R.id.nav_budget).setChecked(true);
//                break;
//        }
//        setTitle();
//        loadFragment(fragment, tag);
//
//    }

    private boolean loadFragmentAndReturn(DrawerLayout drawer) {
        if (loadFragment(fragment, tag)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    private boolean loadFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_dashboard, fragment, tag)
                    .commitAllowingStateLoss();
            return true;
        }

        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserProfileReceived(ProfileReceivedEvent event) {
        Glide.with(this)
                .load(isRemembered ? userSessionManager.getUserProfile() : Safra.userProfile)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .circleCrop()
                .into(userProfile);
        userName.setText(isRemembered ? userSessionManager.getUserName() : Safra.userName);
        userEmail.setText(isRemembered ? userSessionManager.getUserEmail() : Safra.userEmail);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectivityChanged(ConnectivityChangedEvent event) {
        Log.e(TAG, "onConnectivityChanged: " + event.isConnected());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageReceived(LanguagesReceivedEvent event) {
        List<LanguageItem> languageList = dbHandler.getLanguages();
        for (LanguageItem li : languageList) {
            LanguageExtension.downloadLanguageFile(this, TAG,
                    li.getLanguageId(), li.getLangFileUrl(),
                    li.getLangFileUrl().substring(li.getLangFileUrl().lastIndexOf("/") + 1));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChanged(LanguageChangedEvent event) {
        manageNavigationMenuItems();
        setTitle();
        if (ConnectivityReceiver.isConnected())
            GeneralApiCallAndUse.getModulesAndPermissions(this, TAG,
                    isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: " + resultCode);

        if (requestCode == REQUEST_APP_UPDATE) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "App updated successfully", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "You have cancelled update", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_IN_APP_UPDATE_FAILED:
                    Toast.makeText(this, "There is some problem updating app, try to update app from play store.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("fragment_to_launch")) {
            Bundle bundle = new Bundle();
            bundle.putLong("reference_id", intent.getLongExtra("reference_id", -1));
            switch ((int) intent.getLongExtra("fragment_to_launch", 0)) {
                case 2:
                    fragment = new FormsFragment();
                    fragment.setArguments(bundle);
                    tag = FormsFragment.TAG;
                    binding.navView.getMenu().findItem(R.id.nav_forms).setChecked(true);
                    break;
                case 3:
                    fragment = new TasksFragment();
                    fragment.setArguments(bundle);
                    tag = TasksFragment.TAG;
                    binding.navView.getMenu().findItem(R.id.nav_tasks).setChecked(true);
                    break;
            }
            setTitle();

            loadFragment(fragment, tag);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(connectivityReceiver, filter);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Safra.getInstance().setConnectivityListener(this);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "onResume: " + e.getLocalizedMessage());
                }
            }
        });

        Glide.with(this)
                .load(isRemembered ? userSessionManager.getUserProfile() : Safra.userProfile)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .circleCrop()
                .into(userProfile);
        userName.setText(isRemembered ? userSessionManager.getUserName() : Safra.userName);
        userEmail.setText(isRemembered ? userSessionManager.getUserEmail() : Safra.userEmail);

        if (ConnectivityReceiver.isConnected()) {
            GeneralApiCallAndUse.getUserProfile(this, TAG,
                    isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(connectivityReceiver);
        EventBus.getDefault().unregister(this);
    }
}