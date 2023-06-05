package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PLANNER_TASK_DELETE;
import static com.safra.utilities.Common.PLANNER_TASK_LIST;
import static com.safra.utilities.Common.REQUEST_DELETE_ACTION_TASK;
import static com.safra.utilities.UserPermissions.USER_ADD;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.safra.AddActionPlanTask;
import com.safra.AddResourceActivity;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ActionPlanTaskListAdapter;
import com.safra.databinding.FragmentActionPlanTaskListBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ActionTaskListModel;
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

public class ActionPlanTaskListFragment extends DialogFragment {
    public static final String TAG = "action_task_list";
    private FragmentActivity mActivity = null;


    private FragmentActionPlanTaskListBinding binding;

    private List<String> data = new ArrayList<>();


    //    private final List<UserItem> userList = new ArrayList<>();
    private final List<ActionTaskListModel.Data.Goal.Task> userList = new ArrayList<>();
    private final List<ActionTaskListModel.Data.User> userList1 = new ArrayList<>();
    //    private final List<ActionTaskListModel.Data.User> userList1 = new ArrayList<>();
    //    private final List<ProjectListResponseModel.Goal> goalList = new ArrayList<>();
    private ActionPlanTaskListAdapter adapter;


//    ProjectListResponseModel actionPlanListModel1 = new ProjectListResponseModel();


    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;
    private long projectId = -1;
    private boolean isRemembered;
    private boolean isLoadedOnline = false;

    private PopupWindow popupWindow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActionPlanTaskListBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();
        projectId = getArguments().getLong("goal_id", -1);

        binding.ivBack.setOnClickListener(v -> dismiss());

        setText();

        if (PermissionExtension.checkForPermission(USER_ADD)) {
            binding.fabAdd.setVisibility(View.VISIBLE);
        } else {
            binding.fabAdd.setVisibility(View.VISIBLE);
        }


        binding.rvActionPlanTaskList.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvActionPlanTaskList.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new ActionPlanTaskListAdapter(this, getChildFragmentManager(), mActivity, userList1, new ActionPlanTaskListAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ActionTaskListModel.Data.Goal.Task item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_ACTION_TASK);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putLong("online_id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "project");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(ActionTaskListModel.Data.Goal.Task item, int position) {
                long ID = projectId;

                Intent i = new Intent(mActivity, AddActionPlanTask.class);
                i.putExtra("action_task_id", item.getId());
                i.putExtra("planner_project_id", ID);
                i.putExtra("heading", LanguageExtension.setText("edit_task", getString(R.string.edit_task)));
                startActivity(i);


            }

            @Override
            public void onView(ActionTaskListModel.Data.Goal.Task item, int position) {
                long taskId = item.getId();
                String taskName = item.getTitle();
                System.out.println("user_id");
                Intent i = new Intent(mActivity, AddResourceActivity.class);
                i.putExtra("heading", LanguageExtension.setText("add_goal", getString(R.string.add_goal)));
                i.putExtra("is_new", true);
                i.putExtra("task_id", taskId);
                i.putExtra("task_name", taskName);
//                i.putExtra("online_id", item.getUserOnlineId());
                startActivity(i);
            }


            @Override
            public void changeStatus(View view, ActionTaskListModel.Data.Goal.Task item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getId(), item.getStatus().toString());
            }
        });
        binding.rvActionPlanTaskList.setAdapter(adapter);


        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getTaskList(pPosition);
            isLoadedOnline = true;
        } else {
            isLoadedOnline = false;
//            getUsersFromDB();
        }

        binding.srlManageProject.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
//                getUsersFromDB();

//                getTaskList(pPosition);
                if (binding.srlManageProject.isRefreshing())
                    binding.srlManageProject.setRefreshing(false);
            } else {
                isLoadedOnline = false;
//                getUsersFromDB();
            }

        });

        binding.rvActionPlanTaskList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

//        binding.etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                searchText = s.toString();
//                if (isLoadedOnline) {
//                    currentPage = PAGE_START;
//                    getTaskList(pPosition);
//                } else {
//                    adapter.searchUser(searchText);
//
//                    checkForEmptyState();
//                }
//            }
//        });

        binding.fabAdd.setOnClickListener(v -> {
            long ID = projectId;

            Intent i = new Intent(mActivity, AddActionPlanTask.class);
            i.putExtra("planner_project_id", ID);

            i.putExtra("heading", LanguageExtension.setText("create_new_task", getString(R.string.create_new_task)));
            i.putExtra("is_new", true);
            startActivity(i);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_ACTION_TASK, ActionPlanTaskListFragment.this,
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

    public void deleteProject(long projectId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
        AndroidNetworking
                .post(BASE_URL + PLANNER_TASK_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("goal_task_id", String.valueOf(projectId))
//                .setTag("delete-user-api")
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

    private void setText() {
//        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));
        binding.tvEmptyState.setText(LanguageExtension.setText("no_user_found", getString(R.string.no_user_found)));
    }


    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getTaskList(p);
    }

//    private void addLoadingAnimation() {
//        taskList.add(null);
//        pPosition = taskList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//
//    }

    private void getTaskList(int pPosition) {
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);
        String ID = String.valueOf(projectId);
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + PLANNER_TASK_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("goal_id", ID)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");

                            ActionTaskListModel actionTaskListModel = new Gson().fromJson(response.toString(), ActionTaskListModel.class);


                            if (success == 1) {
//                                JSONObject data = response.getJSONObject("data").getJSONObject("goal");
                                JSONObject data = response.getJSONObject("data");
                                JSONObject goal = data.getJSONObject("goal");
                                JSONArray tasks = goal.getJSONArray("tasks");
                                JSONArray users = data.getJSONArray("users");
                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (response.length() > 0) {
                                    userList.addAll(actionTaskListModel.getData().getGoal().getTasks());
                                    userList1.addAll(actionTaskListModel.getData().getUsers());

                                    adapter.addUserList(userList);
                                    adapter.addUserList1(userList1);

                                }

                                if (pPosition > 1 && pPosition <= userList.size() - 1) {
                                    userList.remove(pPosition);
                                    adapter.removeUser(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START) {
                                    adapter.notifyDataSetChanged();
                                } else
                                    adapter.notifyItemRangeInserted(pPosition, response.length());

                                checkForEmptyState();

                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

                        if (binding.srlManageProject.isRefreshing())
                            binding.srlManageProject.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                        if (binding.srlManageProject.isRefreshing())
                            binding.srlManageProject.setRefreshing(false);
                    }
                });
    }


//    private void getProjects(int pPosition) {
//        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);
//        String ID = String.valueOf(projectId);
//        isNextPageCalled = true;
//        AndroidNetworking
//                .post(BASE_URL + PLANNER_TASK_LIST)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("goal_id", ID)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            if (success == 1) {
//                                JSONObject data = response.getJSONObject("data");
//                                System.out.println("DATA"+data);
//                                if (currentPage == PAGE_START) {
//                                    taskList.clear();
//                                    adapter.clearLists();
//
//                                }
//
//
//
//                                if (response.length() > 0) {
//
//                                    JSONObject goal = data.getJSONObject("goal");
//                                    JSONArray tasks = goal.getJSONArray("tasks");
//                                    JSONArray users = data.getJSONArray("users");
//                                    ActionTaskListModel actionTaskListModel = new Gson().fromJson(response.toString(), ActionTaskListModel.class);
//                                    taskList.addAll(actionTaskListModel.getData().getGoal().getTasks());
//
//
//                                    ArrayList<ActionTaskListModel.Data.User> userList = new ArrayList<>();
//                                    for (int i = 0; i < users.length(); i++) {
//                                        JSONObject userObj = users.getJSONObject(i);
//                                        int id = userObj.getInt("id");
//                                        String name = userObj.getString("name");
//                                        String phoneNumber = userObj.getString("phone_number");
//                                        // Extract other properties as needed
//                                        ActionTaskListModel.Data.User user = new ActionTaskListModel.Data.User();
//                                        userList.add(user);
//                                    }
//
//                                    adapter.addUserList(taskList);
//
//                                }
//
//
//
//                                if (pPosition > 1 && pPosition <= taskList.size() - 1) {
//                                    taskList.remove(pPosition);
//                                    adapter.removeUser(pPosition);
//                                    adapter.notifyItemChanged(pPosition - 1);
//                                }
//
//
//                                if (currentPage == PAGE_START) {
//                                    adapter.notifyDataSetChanged();
//                                } else
//                                    adapter.notifyItemRangeInserted(pPosition, response.length());
//
//                                checkForEmptyState();
//
//                            } else {
//                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
//                        }
//
//                        isNextPageCalled = false;
//
//                        if (binding.srlManageProject.isRefreshing())
//                            binding.srlManageProject.setRefreshing(false);
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.e(TAG, "onError: " + anError.getErrorCode());
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//                        Log.e(TAG, "onError: " + anError.getErrorBody());
//
//                        isNextPageCalled = false;
//
//                        if (binding.srlManageProject.isRefreshing())
//                            binding.srlManageProject.setRefreshing(false);
//                    }
//                });
//    }


//    public void deleteUserOffline(long userId, int position) {
//        int i = dbHandler.deleteUserOffline(userId);
//
//        if (i > 0) {
//            taskList.remove(position);
//            adapter.removeUser(position);
//            checkForEmptyState();
//        }
//    }
//
//    public void deleteProject(long projectId, int position) {
//        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        AndroidNetworking
//                .post(BASE_URL + PLANNER_AIM_DELETE)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("planner_aims_id", String.valueOf(projectId))
////                .setTag("delete-user-api")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        LoadingDialogExtension.hideLoading();
//                        try {
////                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
////                            dialogL.dismiss();
////                            if (success == 1) {
//                            taskList.remove(position);
//                            adapter.removeUser(position);
//                            checkForEmptyState();
////                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
////                            dialogL.dismiss();
//                        }
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
//
//    public void changeUserStatusOffline(long userId, int userStatus) {
//        long i = dbHandler.updateUserStatusOffline(userId, userStatus);
//        if (i > 0) {
//            if (ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
////                getUsers(pPosition);
////                getUsersFromDB();
//            } else {
//                isLoadedOnline = false;
////                getUsersFromDB();
//            }
//        }
//    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAdded(TaskAddedEvent event) {
        if (ConnectivityReceiver.isConnected()) {
            isLoadedOnline = true;
            currentPage = PAGE_START;
            getTaskList(pPosition);
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