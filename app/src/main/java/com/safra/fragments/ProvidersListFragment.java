package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_ADD_PROVIDER;
import static com.safra.utilities.Common.HEALTH_RECORD_DELETE_MEDICINE;
import static com.safra.utilities.Common.HEALTH_RECORD_DELETE_PROVIDER;
import static com.safra.utilities.Common.HEALTH_RECORD_MEDICINE_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_PROVIDER_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PLANNER_EDIT_AIM;
import static com.safra.utilities.Common.REQUEST_DELETE_HEALTH_PROVIDER_DELETE;
import static com.safra.utilities.UserPermissions.USER_ADD;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.AddMedicine;
import com.safra.AddResourceActivity;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ProjectPlanListModel;
import com.safra.adapters.PrviderListAdapter;
import com.safra.databinding.FragmentMedicineListBinding;
import com.safra.databinding.FragmentProvidersListBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.MedicineListModel;
import com.safra.models.ProviderListModel;
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

public class ProvidersListFragment extends DialogFragment {
    public static final String TAG = "provider_list_fragment";
    private FragmentActivity mActivity = null;
    private FragmentProvidersListBinding binding;

    private List<String> data = new ArrayList<>();

    private final List<ProviderListModel.Data.Provider> userList = new ArrayList<>();

    private PrviderListAdapter adapter;


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
        binding = FragmentProvidersListBinding.inflate(inflater, container, false);
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
        adapter = new PrviderListAdapter(mActivity, new PrviderListAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ProviderListModel.Data.Provider item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_HEALTH_PROVIDER_DELETE);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putLong("online_id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "project");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(ProviderListModel.Data.Provider item, int position) {
                long ID = projectId;

                Intent i = new Intent(mActivity, AddMedicine.class);
                i.putExtra("medicine_id", item.getId());
                i.putExtra("name", item.getName());


                i.putExtra("heading", LanguageExtension.setText("edit_Medicine", getString(R.string.edit_Medicine)));

                startActivity(i);


            }

            @Override
            public void onView(ProviderListModel.Data.Provider item, int position) {
                long taskId = item.getId();
//                String taskName = item.getTitle();
                Intent i = new Intent(mActivity, AddResourceActivity.class);
                i.putExtra("heading", LanguageExtension.setText("add_goal", getString(R.string.add_goal)));
                i.putExtra("is_new", true);
                i.putExtra("task_id", taskId);
//                i.putExtra("task_name", taskName);

//                i.putExtra("online_id", item.getUserOnlineId());
                startActivity(i);
            }


            @Override
            public void changeStatus(View view, ProviderListModel.Data.Provider item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getId(), item.getStatus().toString());
            }
        });
        binding.rvActionPlanTaskList.setAdapter(adapter);


        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getProviderList(pPosition);
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

//                getProviderList(pPosition);
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
//                    getProviderList(pPosition);
//                } else {
//                    adapter.searchUser(searchText);
//
//                    checkForEmptyState();
//                }
//            }
//        });

        binding.fabAdd.setOnClickListener(v -> {
            showMyDialog();
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_HEALTH_PROVIDER_DELETE, ProvidersListFragment.this,
                (requestKey, result) -> {
//                    long userId = result.getLong("id");
                    long onlineId = result.getLong("id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteProvider(onlineId, position);
//                        deleteUserOffline(userId, position);
                    } else
                        deleteProvider(onlineId, position);

//                    deleteUserOffline(userId, position);
                });

        return binding.getRoot();
    }

    public void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_provider_dialog, null);
        builder.setView(view);

        EditText editText = view.findViewById(R.id.edit_Aim_text);
        Button closeButton = view.findViewById(R.id.close_Aim_button);
        Button saveButton = view.findViewById(R.id.save_Aim_button);

        AlertDialog dialog = builder.create();

        ProjectPlanListModel.Data.AimGoals.Aim user = new ProjectPlanListModel.Data.AimGoals.Aim();



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

                addProvider(text);


                // Do something with the text
                dialog.dismiss();
            }

            private void addProvider(String text) {
                LoadingDialogExtension.showLoading(getContext(), LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));

                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                    requestBody.put("name", text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AndroidNetworking.post(BASE_URL + HEALTH_RECORD_ADD_PROVIDER)
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
        });

        dialog.show();
    }


    public void deleteProvider(long projectId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_DELETE_PROVIDER)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("providerId", String.valueOf(projectId))
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
        binding.tvEmptyState.setText(LanguageExtension.setText("no_provider_found", getString(R.string.no_provider_found)));
    }


    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getProviderList(p);
    }

    private void getProviderList(int pPosition) {
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);
        String ID = String.valueOf(projectId);
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_PROVIDER_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            int success = response.getInt("success");
                            String message = response.getString("message");

                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray providers = data.getJSONArray("providers");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }
                                List<ProviderListModel.Data.Provider> uList = new ArrayList<>();
                                for (int i = 0; i < providers.length(); i++) {
                                    JSONObject medicine = providers.getJSONObject(i);

                                    ProviderListModel.Data.Provider userItem = new ProviderListModel.Data.Provider();

                                    userItem.setName(medicine.getString("name"));
                                    userItem.setId(medicine.getInt("id"));
                               


                                    uList.add(userItem);
//                                    adapter.addUserList(userList);

                                }
                                userList.addAll(uList);
                                adapter.addUserList(uList);


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
                                String errorMessage = "API request unsuccessful: " + message;
                                // Handle the error message
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
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
            getProviderList(pPosition);
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