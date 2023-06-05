package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PLANNER_AIM_DELETE;
import static com.safra.utilities.Common.PLANNER_EDIT_AIM;
import static com.safra.utilities.Common.PLANNER_PROJECT_AIM_LIST;
import static com.safra.utilities.Common.REQUEST_DELETE_AIM;
import static com.safra.utilities.UserPermissions.USER_ADD;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.safra.AddActionPlan;
import com.safra.AddGoalActvity;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ActionPlanListRecyclerAdapter;
import com.safra.adapters.ProjectPlanListModel;
import com.safra.databinding.FragmentActionPlanListBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
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


public class ActionPlanListFragment extends DialogFragment {

    public static final String TAG = "action_list_fragment";
    private FragmentActivity mActivity = null;
    private List<String> data = new ArrayList<>();

    private FragmentActionPlanListBinding binding;

    //    private final List<UserItem> userList = new ArrayList<>();
    private final List<ProjectPlanListModel.Data.AimGoals.Aim> userList = new ArrayList<>();
    //    private final List<ProjectPlanListModel.Data.AimGoals.Aim.Goal> goalList = new ArrayList<>();
    private ActionPlanListRecyclerAdapter adapter;


//    ProjectPlanListModel.Data.AimGoals.Aim actionPlanListModel1 = new ProjectPlanListModel.Data.AimGoals.Aim();


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
        binding = FragmentActionPlanListBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();
        projectId = getArguments().getLong("project_id", -1);

        binding.ivBack.setOnClickListener(v -> dismiss());

        setText();

        if (PermissionExtension.checkForPermission(USER_ADD)) {
            binding.fabAdd.setVisibility(View.VISIBLE);
        } else {
            binding.fabAdd.setVisibility(View.VISIBLE);
        }


        binding.rvActionPlanList.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvActionPlanList.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new ActionPlanListRecyclerAdapter(this, getChildFragmentManager(), mActivity, new ActionPlanListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ProjectPlanListModel.Data.AimGoals.Aim item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_AIM);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putLong("online_id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "project");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(ProjectPlanListModel.Data.AimGoals.Aim item, int position) {
                Bundle bundle = new Bundle();
                bundle.putLong("online_id", item.getId());
                bundle.putInt("position", position);

                showMyDialog(position);


            }

            public void showMyDialog(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getLayoutInflater().inflate(R.layout.add_aim_dialog, null);
                builder.setView(view);

                EditText editText = view.findViewById(R.id.edit_Aim_text);
                Button closeButton = view.findViewById(R.id.close_Aim_button);
                Button saveButton = view.findViewById(R.id.save_Aim_button);

                AlertDialog dialog = builder.create();

                ProjectPlanListModel.Data.AimGoals.Aim user = new ProjectPlanListModel.Data.AimGoals.Aim();
                editText.setText(userList.get(position).getAim());


                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = editText.getText().toString();
                        editPlannerAim(text, position);


                        // Do something with the text
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            public void editPlannerAim(String text, int position) {
                LoadingDialogExtension.showLoading(getContext(), LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//                String abc = String.valueOf(userList.get(0).getId());
                String id = String.valueOf(userList.get(position).getId());
//                passAim = editText.getText().toString();
                JSONArray jsonArray = new JSONArray(data);

//        tvAim = edAim.getText().toString();
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                    requestBody.put("planner_aims_id", id);
                    requestBody.put("aim", text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AndroidNetworking.post(BASE_URL + PLANNER_EDIT_AIM)
                        .addJSONObjectBody(requestBody)

                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e(TAG, "response: " + response);
                                LoadingDialogExtension.hideLoading();
                                try {
                                    String message = response.getString("message");
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new TaskAddedEvent());
//                                    finish();
                                } catch (JSONException e) {
                                    Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                                Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                                Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                                LoadingDialogExtension.hideLoading();
                            }
                        });
            }

            ;


            @Override
            public void onView(ProjectPlanListModel.Data.AimGoals.Aim item, int position) {
                long aimId = item.getId();
                String aimName = item.getAim();
                Intent i = new Intent(mActivity, AddGoalActvity.class);
                i.putExtra("heading", LanguageExtension.setText("add_goal", getString(R.string.add_goal)));
                i.putExtra("is_new", false);
                i.putExtra("aim_id", aimId);
                i.putExtra("aim_name", aimName);

//                i.putExtra("online_id", item.getUserOnlineId());
                startActivity(i);
            }


            @Override
            public void changeStatus(View view, ProjectPlanListModel.Data.AimGoals.Aim item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getId(), item.getStatus().toString());
            }
        });
        binding.rvActionPlanList.setAdapter(adapter);


        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getProjects(pPosition);
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

                getProjects(pPosition);
            } else {
                isLoadedOnline = false;
//                getUsersFromDB();
            }

        });

        binding.rvActionPlanList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                    getProjects(pPosition);
//                } else {
//                    adapter.searchUser(searchText);
//
//                    checkForEmptyState();
//                }
//            }
//        });

        binding.fabAdd.setOnClickListener(v -> {
            long ID = projectId;

            Intent i = new Intent(mActivity, AddActionPlan.class);
            i.putExtra("planner_project_id", ID);
            i.putExtra("heading", LanguageExtension.setText("add_action_plan", getString(R.string.add_action_plan)));
            i.putExtra("is_new", true);
            startActivity(i);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_AIM, ActionPlanListFragment.this,
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
//        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));
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
//
//    }

    private void getProjects(int pPosition) {
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);
        String ID = String.valueOf(projectId);
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + PLANNER_PROJECT_AIM_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("project_id", ID)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data").getJSONObject("aimGoals");
                                JSONArray outerArray = data.getJSONArray("aims");
                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();

                                }
                                ProjectPlanListModel projectPlanListModel = new Gson().fromJson(response.toString(), ProjectPlanListModel.class);
                                if (response.length() > 0) {
                                    userList.addAll(projectPlanListModel.getData().getAimGoals().getAims());
//                                    userList1.addAll(projectPlanListModel.getData().getAimGoals().getAims().get(0).getGoals());


//                                    for (int i = 0; i < outerArray.length(); i++) {
//                                        JSONObject user = outerArray.getJSONObject(i);
//                                        ProjectPlanListModel.Data.AimGoals.Aim projectListResponseModel = new ProjectPlanListModel.Data.AimGoals.Aim();
//                                        ProjectPlanListModel.Data.AimGoals.Aim.Goal goal = new ProjectPlanListModel.Data.AimGoals.Aim.Goal();
//
////                                        projectListResponseModel.setId(user.getInt("id"));
////                                        projectListResponseModel.setUserId(user.getInt("user_id"));
////                                        projectListResponseModel.setAim(user.getString("aim"));
//////                                        projectListResponseModel.setStatus(user.getInt("status"));
////                                        projectListResponseModel.setMasterId(user.getInt("master_id"));
////                                        projectListResponseModel.setPlannerProjectId(user.getInt("planner_project_id"));
////                                        if (user.has("created_at") && !user.isNull("created_at")) {
////                                            projectListResponseModel.setCreatedAt(user.getString("created_at"));
////                                        }
////
////                                        if (user.has("updated_at") && !user.isNull("updated_at")) {
////                                            projectListResponseModel.setUpdatedAt(user.getString("updated_at"));
////                                        }
//
////                                        if (user.has("updated_at") && !user.isNull("updated_at")) {
////                                            projectListResponseModel.setUpdatedAt(user.getString("updated_at"));
////                                        }
//
////                                        if (user.has("goals") && !user.isNull("goals")) {
////                                            JSONArray goalsArray = user.getJSONArray("goals");
////                                            for (int d = 0; d < goalsArray.length(); d++) {
////                                                JSONObject goalObject = goalsArray.getJSONObject(d);
////                                                if (goalObject.has("goal") && !goalObject.isNull("goal")) {
////                                                    goal.setGoal(goalObject.getString("goal"));
////                                                }
//////
////                                                goalList.add(goal);
////                                            }

////
////                                            projectListResponseModel.setGoals(goalList);
////                                        }
////
//
//                                        uList.add(projectListResponseModel);
////                                        uList1.add(goal);
//
////                                        dbHandler.AddProjects(ProjectListResponseModel);
////                                        }
//                                    }


//                                    userList.addAll(uList);
//                                    goalList.addAll(uList1);
                                    adapter.addUserList(userList);

//                                    goalAdapter.addUserList(uList1);

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
        AndroidNetworking
                .post(BASE_URL + PLANNER_AIM_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("planner_aims_id", String.valueOf(projectId))
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