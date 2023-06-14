package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_DIAGNOSTICS_ADD;
import static com.safra.utilities.Common.HEALTH_RECORD_DIAGNOSTICS_DELETE;
import static com.safra.utilities.Common.HEALTH_RECORD_DIAGNOSTICS_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_DELETE_HEALTH_DIAGNOSIS_DELETE;
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
import com.safra.AddMedicine;
import com.safra.AddResourceActivity;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.DiagnosticsListAdapter;
import com.safra.adapters.ProjectPlanListModel;
import com.safra.databinding.FragmentDiagnosticsListBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.DiagnosticsListModel;
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
import java.util.Objects;


public class DiagnosticsListFragment extends DialogFragment {
    public static final String TAG = "diagnostics_list";
    private FragmentActivity mActivity = null;
    private FragmentDiagnosticsListBinding binding;

    private List<String> data = new ArrayList<>();

    private final List<DiagnosticsListModel.Data.Patient.Diagnostic> userList = new ArrayList<>();

    private DiagnosticsListAdapter adapter;


    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;
    private long PatientId = -1;
    private boolean isRemembered;
    private boolean isLoadedOnline = false;
    String fName, mName, lName;
    String fullName;
    private PopupWindow popupWindow;
    private String patientName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiagnosticsListBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();
        PatientId = getArguments().getLong("patient_id", -1);
        fName = getArguments().getString("f_name");
        mName = getArguments().getString("m_name");
        lName = getArguments().getString("l_name");


        fullName = " " + fName + " " + mName + " " + lName;

        if (Objects.equals(fName, "null") && Objects.equals(mName, "null") && Objects.equals(lName, "null")) {
            binding.tvUserName.setText("Unidentified patient");
        } else {
            StringBuilder fullNameBuilder = new StringBuilder();
            if (!Objects.equals(fName, "null")) {
                fullNameBuilder.append(fName);
            }
            if (!Objects.equals(mName, "null")) {
                if (fullNameBuilder.length() > 0) {
                    fullNameBuilder.append(" ");
                }
                fullNameBuilder.append(mName);
            }
            if (!Objects.equals(lName, "null")) {
                if (fullNameBuilder.length() > 0) {
                    fullNameBuilder.append(" ");
                }
                fullNameBuilder.append(lName);
            }
            String fullName = fullNameBuilder.toString();
            binding.tvUserName.setText(fullName);
        }
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
        adapter = new DiagnosticsListAdapter(mActivity, new DiagnosticsListAdapter.OnItemClickListener() {
            @Override
            public void onDelete(DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_HEALTH_DIAGNOSIS_DELETE);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putLong("online_id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "project");
                System.out.println("POSITION :- " + position);
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {
                long ID = PatientId;

                Intent i = new Intent(mActivity, AddMedicine.class);
                i.putExtra("medicine_id", item.getId());
//                i.putExtra("name", item.getName());
                System.out.println("medicine_id:- " + item.getId());
                i.putExtra("heading", LanguageExtension.setText("edit_Medicine", getString(R.string.edit_Medicine)));
                startActivity(i);


            }

            @Override
            public void onView(DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {
                long taskId = item.getId();
//                String taskName = item.getTitle();
                System.out.println("user_id");
                Intent i = new Intent(mActivity, AddResourceActivity.class);
                i.putExtra("heading", LanguageExtension.setText("add_goal", getString(R.string.add_goal)));
                i.putExtra("is_new", true);
                i.putExtra("task_id", taskId);
//                i.putExtra("task_name", taskName);
//                System.out.println("item.getId()"+ aimName);

//                i.putExtra("online_id", item.getUserOnlineId());
                startActivity(i);
            }


            @Override
            public void changeStatus(View view, DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getId(), item.getStatus().toString());
            }
        });
        binding.rvActionPlanTaskList.setAdapter(adapter);


        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getDiagnosticsList(pPosition);
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

//                getDiagnosticsList(pPosition);
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
//                    getDiagnosticsList(pPosition);
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

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_HEALTH_DIAGNOSIS_DELETE, DiagnosticsListFragment.this,
                (requestKey, result) -> {
//                    long userId = result.getLong("id");
                    long onlineId = result.getLong("id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteDiagnosis(onlineId, position);
//                        deleteUserOffline(userId, position);
                    } else
                        deleteDiagnosis(onlineId, position);

//                    deleteUserOffline(userId, position);
                });

        return binding.getRoot();
    }

    public void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.add_diagnosis_dialog, null);
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

                addDiagnostics(text);


                // Do something with the text
                dialog.dismiss();
            }

            private void addDiagnostics(String text) {
                LoadingDialogExtension.showLoading(getContext(), LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));

                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                    requestBody.put("patient_id", PatientId);
                    requestBody.put("result", text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AndroidNetworking.post(BASE_URL + HEALTH_RECORD_DIAGNOSTICS_ADD)
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


    public void deleteDiagnosis(long PatientId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_DIAGNOSTICS_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(PatientId))
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
        binding.tvEmptyState.setText(LanguageExtension.setText("no_diagnostics_found", getString(R.string.no_diagnostics_found)));
    }


    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getDiagnosticsList(p);
    }

    private void getDiagnosticsList(int pPosition) {
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_DIAGNOSTICS_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(PatientId))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            int success = response.getInt("success");
                            String message = response.getString("message");

                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data").getJSONObject("patient");
                                JSONArray providers = data.getJSONArray("diagnostics");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }
                                List<DiagnosticsListModel.Data.Patient.Diagnostic> uList = new ArrayList<>();
                                for (int i = 0; i < providers.length(); i++) {
                                    JSONObject medicine = providers.getJSONObject(i);

                                    DiagnosticsListModel.Data.Patient.Diagnostic userItem = new DiagnosticsListModel.Data.Patient.Diagnostic();

                                    userItem.setResult(medicine.getString("result"));
                                    userItem.setId(medicine.getInt("id"));

//                                    if (Objects.equals(fName, "null") && Objects.equals(mName, "null") && Objects.equals(lName, "null")) {
//
//                                        binding.tvUserName.setText("Unidentified patient");
//
//                                    }  else {
//                                        binding.tvUserName.setText(fullName);
//                                    }


//                                    userItem.setFullName(fullName);


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
            getDiagnosticsList(pPosition);
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