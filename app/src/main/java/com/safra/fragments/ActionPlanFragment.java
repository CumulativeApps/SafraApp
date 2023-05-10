package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PROJECT;
import static com.safra.utilities.Common.PROJECT_DELETE;
import static com.safra.utilities.Common.PROJECT_STATUS;
import static com.safra.utilities.Common.REQUEST_DELETE_PROJECT;
import static com.safra.utilities.UserPermissions.USER_ADD;
import static com.safra.utilities.UserPermissions.USER_DELETE;
import static com.safra.utilities.UserPermissions.USER_STATUS;
import static com.safra.utilities.UserPermissions.USER_UPDATE;
import static com.safra.utilities.UserPermissions.USER_VIEW;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.AddProjects;
import com.safra.AddTask;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ActionPlanRecyclerAdapter;
import com.safra.adapters.ProjectListSpinnerAdapter;
import com.safra.adapters.ProjectRecyclerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.FragmentActionPlanBinding;
import com.safra.databinding.FragmentProjectsBinding;
import com.safra.databinding.PopupChangeProjectStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.UserAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ProjectListResponseModel;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionPlanFragment extends Fragment {
    public static final String TAG = "action_plan_fragment";
    private FragmentActivity mActivity = null;

    private FragmentActionPlanBinding binding;

    //    private final List<UserItem> userList = new ArrayList<>();
    private final List<ProjectListResponseModel> userList = new ArrayList<>();
    private ActionPlanRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;
    private boolean isLoadedOnline = false;

    private PopupWindow popupWindow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActionPlanBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        setText();

//        if (PermissionExtension.checkForPermission(USER_ADD)) {
//            binding.fabAdd.setVisibility(View.VISIBLE);
//        } else {
//            binding.fabAdd.setVisibility(View.VISIBLE);
//        }

        binding.rvProjects.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvProjects.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new ActionPlanRecyclerAdapter(mActivity, new ActionPlanRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ProjectListResponseModel item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_PROJECT);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putLong("online_id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "project");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(ProjectListResponseModel item, int position) {
                Intent i = new Intent(mActivity, AddProjects.class);
                i.putExtra("heading", LanguageExtension.setText("edit_project", getString(R.string.edit_project)));
                i.putExtra("is_new", false);
                i.putExtra("user_id", item.getUserId());
//                i.putExtra("online_id", item.getUserOnlineId());
                startActivity(i);
            }

            @Override
            public void onView(ProjectListResponseModel item, int position) {
                ActionPlanListFragment dialogD = new ActionPlanListFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("project_id", item.getId());
                System.out.println("project_id"+item.getId());
//                bundle.putLong("online_id", item.getUserOnlineId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), ActionPlanListFragment.TAG);
            }

            @Override
            public void changeStatus(View view, ProjectListResponseModel item, int position) {
                setPopUpWindowForChangeStatus(view, item.getId(), item.getStatus().toString());
            }
        });
        binding.rvProjects.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getProjects(pPosition);
            isLoadedOnline = true;
        } else {
            isLoadedOnline = false;
//            getUsersFromDB();
        }

        binding.srlManageUser.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
//                getUsersFromDB();

                getProjects(pPosition);
            } else {
                isLoadedOnline = false;
//                getUsersFromDB();
            }

        });

        binding.rvProjects.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(1)) {
                        // Post a task to the main thread to modify the adapter data
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                // Modify the adapter data here
                            }
                        });
                    }

//                    if (isLoadedOnline && !isLastPage && !isNextPageCalled) {
//                        if (ConnectivityReceiver.isConnected())
//                            loadMoreItems();
////                        else
////                            Toast.makeText(ProductList.this, "Looks like you're not connected with internet!", Toast.LENGTH_LONG).show();
//                    }
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
                    getProjects(pPosition);
                } else {
                    adapter.searchUser(searchText);
                    checkForEmptyState();
                }
            }
        });

//        binding.fabAdd.setOnClickListener(v -> {
//            Intent i = new Intent(mActivity, AddProjects.class);
//            i.putExtra("heading", LanguageExtension.setText("add_projects", getString(R.string.add_projects)));
//            i.putExtra("is_new", true);
//            startActivity(i);
//        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_PROJECT, this,
                (requestKey, result) -> {
//                    long userId = result.getLong("id");
                    long onlineId = result.getLong("id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteProject(onlineId, position);
//                        deleteUserOffline(userId, position);
                    } else
                        deleteProject(onlineId, position);

//                    deleteUserOffline(userId, position);
                });

        return binding.getRoot();
    }

    private void setText() {
        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));
        binding.tvEmptyState.setText(LanguageExtension.setText("no_user_found", getString(R.string.no_user_found)));
    }

//    private void getUsersFromDB() {
//        userList.clear();
//
//        userList.addAll(dbHandler.getUsers(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        for (ProjectListResponseModel ProjectListResponseModel : userList) {
//            if (PermissionExtension.checkForPermission(USER_VIEW))
//                ProjectListResponseModel.setViewable(true);
//
//            if (PermissionExtension.checkForPermission(USER_DELETE))
//                ProjectListResponseModel.setDeletable(true);
//
//            if (PermissionExtension.checkForPermission(USER_UPDATE))
//                ProjectListResponseModel.setEditable(true);
//
//            if (PermissionExtension.checkForPermission(USER_STATUS))
//                ProjectListResponseModel.setChangeable(true);
//        }
//
//        adapter.clearLists();
//        adapter.addUserList(userList);
//        Log.e(TAG, "getUsersFromDB: " + adapter.getItemCount());
//
//        checkForEmptyState();
//
//        if (binding.srlManageProject.isRefreshing())
//            binding.srlManageProject.setRefreshing(false);
//    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getProjects(p);
    }

//    private void addLoadingAnimation() {
//        userList.add(null);
//        pPosition = userList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//    }

    private void getProjects(int pPosition) {
//        binding.srlManageUser.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + PROJECT)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("API CALL123" +response);
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            System.out.println("success" +success);

                            String message = response.getString("message");
                            System.out.println("message" +message);

                            if (success == 1) {

                                JSONArray users = response.getJSONArray("data");
                                System.out.println("users"+ users.length());

                                if (response.length() > 0) {
                                    List<ProjectListResponseModel> uList = new ArrayList<>();
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
                                        ProjectListResponseModel projectListResponseModel = new ProjectListResponseModel();
                                        projectListResponseModel.setId(user.getLong("id"));
                                        projectListResponseModel.setUserId(user.getInt("user_id"));
                                        projectListResponseModel.setName(user.getString("name"));
//                                        projectListResponseModel.setStatus(user.getInt("status"));
                                        projectListResponseModel.setMasterId(user.getInt("master_id"));

                                        if (user.has("start_date") && !user.isNull("start_date")) {
                                            projectListResponseModel.setStartDate(user.getString("start_date"));
                                        }

                                        if (user.has("end_date") && !user.isNull("end_date")) {
                                            projectListResponseModel.setEndDate(user.getString("end_date"));
                                        }

                                        if (user.has("financier") && !user.isNull("financier")) {
                                            projectListResponseModel.setFinancier(user.getString("financier"));
                                        }

                                        if (user.has("currency") && !user.isNull("currency")) {
                                            projectListResponseModel.setPriorityName(user.getString("currency"));
                                            switch (projectListResponseModel.getPriorityName().toLowerCase()) {
                                                case "MZN":
                                                    projectListResponseModel.setCurrency(4);
                                                    break;
                                                case "USA":
                                                    projectListResponseModel.setCurrency(3);
                                                    break;
                                                case "EUR":
                                                    projectListResponseModel.setCurrency(2);
                                                    break;
                                                case "ZAR":
                                                default:
                                                    projectListResponseModel.setCurrency(1);
                                            }
                                        }

                                        if (user.has("status") && !user.isNull("status")) {
                                            projectListResponseModel.setStatus(user.getInt("status"));
                                        }



                                        if (PermissionExtension.checkForPermission(USER_VIEW))
                                            projectListResponseModel.setViewable(true);

                                        if (PermissionExtension.checkForPermission(USER_DELETE))
                                            projectListResponseModel.setDeletable(true);

                                        if (PermissionExtension.checkForPermission(USER_UPDATE))
                                            projectListResponseModel.setEditable(true);

                                        if (PermissionExtension.checkForPermission(USER_STATUS))
                                            projectListResponseModel.setChangeable(true);

                                        uList.add(projectListResponseModel);
//                                        dbHandler.AddProjects(ProjectListResponseModel);
                                    }

                                    userList.addAll(uList);
                                    adapter.addUserList(uList);
                                }


                                checkForEmptyState();

                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

                        if (binding.srlManageUser.isRefreshing())
                            binding.srlManageUser.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                        if (binding.srlManageUser.isRefreshing())
                            binding.srlManageUser.setRefreshing(false);
                    }
                });

//        userList.clear();
//        userList.add(new ProjectListResponseModel(1, "John Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//        userList.add(new ProjectListResponseModel(2, "Jane Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    public void deleteUserOffline(long userId, int position) {
        int i = dbHandler.deleteUserOffline(userId);

        if (i > 0) {
            userList.remove(position);
            adapter.removeUser(position);
            checkForEmptyState();
        }
    }

    public void deleteProject(long projectId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PROJECT_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("id", String.valueOf(projectId))
                .setTag("delete-user-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
//                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
//                            if (success == 1) {
                            userList.remove(position);
                            adapter.removeUser(position);
                            checkForEmptyState();
//                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
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

    public void changeUserStatusOffline(long userId, int userStatus) {
        long i = dbHandler.updateUserStatusOffline(userId, userStatus);
        if (i > 0) {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
//                getUsers(pPosition);
//                getUsersFromDB();
            } else {
                isLoadedOnline = false;
//                getUsersFromDB();
            }
        }
    }

    public void changeProjectStatus(long projectId, String projectStatus) {
        String ID = String.valueOf(projectId);
        System.out.println("id:- " + ID);
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PROJECT_STATUS + ID)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("id", String.valueOf(taskId))
                .addBodyParameter("status", projectStatus)
//                .setTag("change-task-status-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
//                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
//                            if (success == 1) {
                            if (ConnectivityReceiver.isConnected()) {
                                isLoadedOnline = true;
                                currentPage = PAGE_START;
                                getProjects(pPosition);
                            } else {
                                isLoadedOnline = false;
//                                    getTasksFromDB();
//                                }
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
    private void setPopUpWindowForChangeStatus(View parentView, long projectId, String currentStatus) {
        Log.e(TAG, "setPopUpWindowForChangeStatus: starting...");
        PopupChangeProjectStatusBinding popupBinding = PopupChangeProjectStatusBinding.inflate(getLayoutInflater());

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

        popupBinding.tvReject.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
                changeProjectStatus(projectId, "0");
            else
//                changeTaskStatusOffline(taskId, "A");
                popupWindow.dismiss();
        });
        popupBinding.tvApprove.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
                changeProjectStatus(projectId, "1");
            else
//                changeTaskStatusOffline(taskId, "R");
                popupWindow.dismiss();
        });
        popupBinding.tvComplete.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
                changeProjectStatus(projectId, "2");
            else
//                changeTaskStatusOffline(taskId, "C");
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
//        if (requestCode == REQUEST_EDIT_USER && resultCode == RESULT_SUCCESS_EDIT_USER) {
//            if (ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
//                getUsers(pPosition);
//            } else {
//                isLoadedOnline = false;
//                getUsersFromDB();
//            }
//        }

//        if (requestCode == REQUEST_DELETE_PROJECT && resultCode == RESULT_SUCCESS_DELETE_USER) {
//            if (data != null) {
//                Bundle bundle = data.getExtras();
//                long userId = bundle.getLong("id");
//                long onlineId = bundle.getLong("online_id");
//                int position = bundle.getInt("position");
//                if (ConnectivityReceiver.isConnected())
//                    deleteUser(onlineId, position);
//                else
//                    deleteUserOffline(userId, position);
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAdded(UserAddedEvent event) {
        if (ConnectivityReceiver.isConnected()) {
            isLoadedOnline = true;
            currentPage = PAGE_START;
            getProjects(pPosition);
        } else {
            isLoadedOnline = false;
//            getUsersFromDB();
        }
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