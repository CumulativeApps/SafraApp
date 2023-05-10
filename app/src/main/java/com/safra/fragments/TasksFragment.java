package com.safra.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.AddTask;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.TaskRecyclerAdapter;
import com.safra.databinding.FragmentTasksBinding;
import com.safra.databinding.PopupChangeTaskStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.TaskItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_DELETE_TASK;
import static com.safra.utilities.Common.TASK_DELETE_API;
import static com.safra.utilities.Common.TASK_STATUS_API;
import static com.safra.utilities.Common.TASK_LIST_API;

import static com.safra.utilities.UserPermissions.TASK_ADD;
import static com.safra.utilities.UserPermissions.TASK_DELETE;
import static com.safra.utilities.UserPermissions.TASK_UPDATE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class TasksFragment extends Fragment {

    public static final String TAG = "tasks_fragment";

    private FragmentActivity mActivity = null;

    private FragmentTasksBinding binding;

    private final List<TaskItem> taskList = new ArrayList<>();
    private TaskRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;
    private boolean isLoadedOnline = false;

    private PopupWindow popupWindow;
    public void deleteTask(long taskId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
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
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 0) {
                                taskList.remove(position);
                                adapter.removeTask(position);
                                checkForEmptyState();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        setText();

        if (PermissionExtension.checkForPermission(TASK_ADD)) {
            binding.fabAdd.setVisibility(View.VISIBLE);
        } else {
            binding.fabAdd.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            if (getArguments().containsKey("reference_id") && getArguments().getLong("reference_id") > -1) {
                TaskDetailFragment dialogD = new TaskDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("online_id", getArguments().getLong("reference_id"));
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), TaskDetailFragment.TAG);
            }
        }

        binding.rvTasks.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvTasks.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new TaskRecyclerAdapter(mActivity, new TaskRecyclerAdapter.OnItemClickListener() {
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
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(TaskItem item, int position) {
                Intent i = new Intent(mActivity, AddTask.class);
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
                dialogD.show(mActivity.getSupportFragmentManager(), TaskDetailFragment.TAG);
            }
        });
        binding.rvTasks.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            getTasksFromDB();
        } else {
            isLoadedOnline = false;
            getTasksFromDB();
        }

        binding.srlTasks.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
                getTasksFromDB();

//                getTasks(pPosition);
            } else {
                isLoadedOnline = false;
                getTasksFromDB();
            }
        });

        binding.rvTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (isLoadedOnline && !isLastPage && !isNextPageCalled) {
//                        if (detector.isConnectingToInternet())
                        loadMoreItems();
//                        else
//                            Toast.makeText(ProductList.this, "Looks like you're not connected with internet!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString();
                if (isLoadedOnline) {
                    currentPage = PAGE_START;
                    getTasks(pPosition);
                } else {
                    adapter.searchTask(searchText);
                    checkForEmptyState();
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, AddTask.class);
            i.putExtra("heading", LanguageExtension.setText("add_task", getString(R.string.add_task)));
            i.putExtra("is_new", true);
            startActivity(i);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_TASK, this,
                (requestKey, result) -> {
                    long taskId = result.getLong("id");
                    long onlineId = result.getLong("online_id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {
                        deleteTask(onlineId, position);
                        deleteTaskOffline(taskId, position);
//                        deleteTaskOffline(onlineId,taskId, position);
                    }   else
                    deleteTask(onlineId, position);
                        deleteTaskOffline(taskId, position);
                });

        return binding.getRoot();
    }

    private void setText() {
        binding.etSearch.setHint(LanguageExtension.setText("search_the_task", getString(R.string.search_the_task)));
        binding.tvEmptyStateTitle.setText(LanguageExtension.setText("no_tasks", getString(R.string.no_tasks)));
        binding.tvEmptyState.setText(LanguageExtension.setText("currently_there_are_no_tasks_you_will_be_notified_when_new_task_is_assigned", getString(R.string.currently_there_are_no_tasks_you_will_be_notified_when_new_task_is_assigned)));
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
                    || cAllUserIds.contains(isRemembered ? userSessionManager.getUserId() : Safra.userId)){
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

        checkForEmptyState();

        if (binding.srlTasks.isRefreshing())
            binding.srlTasks.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(taskList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
//        getTasks(p);
    }

//    private void addLoadingAnimation() {
//        taskList.add(null);
//        int pPosition = taskList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//    }

    private void getTasks(int pPosition) {
        binding.srlTasks.setRefreshing(currentPage == PAGE_START);

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

                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

                        if (binding.srlTasks.isRefreshing())
                            binding.srlTasks.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                        if (binding.srlTasks.isRefreshing())
                            binding.srlTasks.setRefreshing(false);
                    }
                });
//        taskList.clear();
////        taskList.add(new TaskItem(1, "E-Commerce Form", "Assignee 1", "Feb 25, 2021", 1));
////        taskList.add(new TaskItem(2, "Survey Form", "Assignee 2", "Feb 28, 2021", 0));
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    public void deleteTaskOffline( long taskId, int position) {
        int i = dbHandler.deleteTaskOffline(taskId);

        if (i > 0) {
            taskList.remove(position);
            adapter.removeTask(position);
            checkForEmptyState();
        }
    }


    public void changeTaskStatusOffline(long taskId, long onlineId, String taskStatus) {
        long i = dbHandler.updateTaskStatusOffline(taskId, taskStatus);
//        if (i > 0) {
//            if (ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
//                getTasksFromDB();
//                getTasks(pPosition);
//            } else {
//                isLoadedOnline = false;
        getTasksFromDB();
    }
//        }
//    }

    public void changeTaskStatus(long taskId, String taskStatus) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
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
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                if (ConnectivityReceiver.isConnected()) {
                                    isLoadedOnline = true;
                                    currentPage = PAGE_START;
//                                    getTasks(pPosition);
                                } else {
                                    isLoadedOnline = false;
                                    getTasksFromDB();
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
//            if (ConnectivityReceiver.isConnected())
//                changeTaskStatus(onlineId, "A");
////                changeTaskStatusOffline(taskId,"A");
//
//            else
            changeTaskStatusOffline(taskId,onlineId,"A");
            popupWindow.dismiss();
        });
        popupBinding.tvReject.setOnClickListener(v -> {
//            if (ConnectivityReceiver.isConnected())
////                changeTaskStatusOffline(taskId,"A");
//
//                changeTaskStatus(onlineId, "R");
//            else
            changeTaskStatusOffline(taskId, onlineId, "R");
            popupWindow.dismiss();
        });
        popupBinding.tvComplete.setOnClickListener(v -> {
//            if (ConnectivityReceiver.isConnected())
//                changeTaskStatus(onlineId, "C");
////                changeTaskStatusOffline(taskId,"A");
//
//            else
            changeTaskStatusOffline(taskId, onlineId, "C");
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

    private void checkForEmptyState() {
        if (adapter != null) {
            if (adapter.getItemCount() > 0) {
                binding.clData.setVisibility(View.VISIBLE);
                binding.clEmptyState.setVisibility(View.GONE);
            } else {
                binding.clData.setVisibility(View.GONE);
                binding.clEmptyState.setVisibility(View.VISIBLE);
            }
        } else {
            binding.clData.setVisibility(View.GONE);
            binding.clEmptyState.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_EDIT_TASK && resultCode == RESULT_SUCCESS_EDIT_TASK) {
//            if (ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
//                getTasks(pPosition);
//            } else {
//                isLoadedOnline = false;
//                getTasksFromDB();
//            }
//        }

//        if (requestCode == REQUEST_DELETE_TASK && resultCode == RESULT_SUCCESS_DELETE_TASK) {
//            if (data != null) {
//                Bundle bundle = data.getExtras();
//                long taskId = bundle.getLong("id");
//                long onlineId = bundle.getLong("online_id");
//                int position = bundle.getInt("position");
//                if (ConnectivityReceiver.isConnected())
//                    deleteTask(onlineId, position);
//                else
//                    deleteTaskOffline(taskId, position);
//            }
//        }
//    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onTaskAdded(TaskAddedEvent event){
//        if (ConnectivityReceiver.isConnected()) {
//            isLoadedOnline = true;
//            currentPage = PAGE_START;
////            getTasks(pPosition);
//        } else {
        isLoadedOnline = false;
        getTasksFromDB();
    }
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
