package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_APPOINTMENT_ADD_NOTE;
import static com.safra.utilities.Common.HEALTH_RECORD_APPOINTMENT_DELETE;
import static com.safra.utilities.Common.HEALTH_RECORD_APPOINTMENT_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_APPOINTMENT_UPDATE_STATUS;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PLANNER_EDIT_AIM;
import static com.safra.utilities.Common.REQUEST_DELETE_HEALTH_PATIENT_APPOINTMENT_DELETE;
import static com.safra.utilities.Common.USER_DELETE_API;
import static com.safra.utilities.Common.USER_STATUS_API;
import static com.safra.utilities.UserPermissions.USER_ADD;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.safra.AddAppointment;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.AppointmentListRecyclerAdapter;
import com.safra.adapters.ProjectPlanListModel;
import com.safra.databinding.FragmentAppointmentListBinding;
import com.safra.databinding.PopupChangeUserStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.AppointmentListModel;
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


public class AppointmentListFragment extends DialogFragment {

    public static final String TAG = "appoint_list_fragment";
    private FragmentActivity mActivity = null;

    private FragmentAppointmentListBinding binding;

    private AppointmentListRecyclerAdapter adapter;

    private boolean isRemembered;


    private final List<AppointmentListModel.Data.Patient.Appointment> userList = new ArrayList<>();

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;
    private long patientId = -1;
    String fname, mname, lname;
    String fullName = "";

    private boolean isLoadedOnline = false;

    private PopupWindow popupWindow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentListBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();

        binding.ivBack.setOnClickListener(v -> dismiss());

        setText();

        patientId = getArguments().getLong("appointment_patient_id", -1);
        fname = getArguments().getString("f_name", "");
        mname = getArguments().getString("m_name", "");
        lname = getArguments().getString("l_name", "");
        fullName = " " + fname + " " + mname + " " + lname;


        if (PermissionExtension.checkForPermission(USER_ADD)) {
            binding.fabAdd.setVisibility(View.VISIBLE);
        } else {
            binding.fabAdd.setVisibility(View.VISIBLE);
        }

        binding.rvAppointmentList.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvAppointmentList.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new AppointmentListRecyclerAdapter(mActivity, new AppointmentListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(AppointmentListModel.Data.Patient.Appointment item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_HEALTH_PATIENT_APPOINTMENT_DELETE);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "user");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(AppointmentListModel.Data.Patient.Appointment item, int position) {
                System.out.println("ON EDIT EVENT");

                AddNoteDialog(position);

            }

            public void AddNoteDialog(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getLayoutInflater().inflate(R.layout.add_note_dialog, null);
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

                        AddNoteAPI(text, position);


                        // Do something with the text
                        dialog.dismiss();
                    }

                    private void AddNoteAPI(String text, int position) {
                        LoadingDialogExtension.showLoading(getContext(), LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
                        String id = String.valueOf(userList.get(position).getId());
                        System.out.println("saveActionPlan ID" + position);

                        JSONObject requestBody = new JSONObject();
                        try {
                            requestBody.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                            requestBody.put("appointment_id", id);
                            requestBody.put("note", text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AndroidNetworking.post(BASE_URL + HEALTH_RECORD_APPOINTMENT_ADD_NOTE)
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

            @Override
            public void onView(AppointmentListModel.Data.Patient.Appointment item, int position) {
                AppointmentDetailFragment dialogD = new AppointmentDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", item.getId());
                bundle.putInt("status", item.getStatus());
                bundle.putString("appointmentDate", item.getStart_date());
                bundle.putString("appointmentTime", item.getStart_time());
                bundle.putLong("patientId", patientId);
                bundle.putString("Note", item.getNote());

                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), AppointmentDetailFragment.TAG);
            }

            @Override
            public void changeStatus(View view, AppointmentListModel.Data.Patient.Appointment item, int position) {

                changeAppointmentStatus(item.getId(), position);
//                setPopUpWindowForChangeStatus(view, item.getUserId(), item.getUserOnlineId(), item.getUserStatus());
            }

            private void changeAppointmentStatus(int appointmentId, int position) {
                LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

                AndroidNetworking
                        .post(BASE_URL + HEALTH_RECORD_APPOINTMENT_UPDATE_STATUS)
                        .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                        .addBodyParameter("appointment_id", String.valueOf(appointmentId))
//                .setTag("delete-user-api")
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
                                        getAppointment(pPosition);
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
        });
        binding.rvAppointmentList.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            getUsersFromDB();
            getAppointment(pPosition);
//            isLoadedOnline = true;
        } else {
            isLoadedOnline = false;
            getUsersFromDB();
        }

        binding.srlManageProject.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
//                getUsersFromDB();
                getUsersFromDB();
                getAppointment(pPosition);
            } else {
                isLoadedOnline = false;
                getUsersFromDB();
            }

        });

        binding.rvAppointmentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (isLoadedOnline && !isLastPage && !isNextPageCalled) {
                        if (ConnectivityReceiver.isConnected())
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
                    getAppointment(pPosition);
                } else {
                    adapter.searchUser(searchText);
                    checkForEmptyState();
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, AddAppointment.class);
            i.putExtra("heading", LanguageExtension.setText("add_appointment", getString(R.string.add_appointment)));
            i.putExtra("is_new", true);
            i.putExtra("patientId", patientId);
            startActivity(i);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_HEALTH_PATIENT_APPOINTMENT_DELETE, this,
                (requestKey, result) -> {
                    long appointmentId = result.getLong("id");
                    long onlineId = result.getLong("online_id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteAppointment(appointmentId, position);
//                        deleteUserOffline(userId, position);
                    } else
                        deleteAppointment(appointmentId, position);

//                    deleteUserOffline(userId, position);
                });

        return binding.getRoot();
    }

    private void setText() {
        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));
        binding.tvEmptyState.setText(LanguageExtension.setText("no_user_found", getString(R.string.no_user_found)));
    }

    private void getUsersFromDB() {
        userList.clear();

//        userList.addAll(dbHandler.getUsers(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        for (UserItem userItem : userList) {
//            if (PermissionExtension.checkForPermission(USER_VIEW))
//                userItem.setViewable(true);
//
//            if (PermissionExtension.checkForPermission(USER_DELETE))
//                userItem.setDeletable(true);
//
//            if (PermissionExtension.checkForPermission(USER_UPDATE))
//                userItem.setEditable(true);
//
//            if (PermissionExtension.checkForPermission(USER_STATUS))
//                userItem.setChangeable(true);
//        }

        adapter.clearLists();
        adapter.addUserList(userList);
        Log.e(TAG, "getUsersFromDB: " + adapter.getItemCount());

        checkForEmptyState();

        if (binding.srlManageProject.isRefreshing())
            binding.srlManageProject.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getAppointment(p);
    }

//    private void addLoadingAnimation() {
//        userList.add(null);
//        pPosition = userList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//    }

    private void getAppointment(int pPosition) {
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_APPOINTMENT_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(patientId))
//                .addBodyParameter("page_no", String.valueOf(currentPage))
//                .addBodyParameter("search_text", searchText)
//                .setTag("user-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data").getJSONObject("patient");
                                JSONArray appointments = data.getJSONArray("appointments");
//                                int totalPage = data.getInt("total_page");
//                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (appointments.length() > 0) {
                                    List<AppointmentListModel.Data.Patient.Appointment> uList = new ArrayList<>();
                                    for (int i = 0; i < appointments.length(); i++) {
                                        JSONObject user = appointments.getJSONObject(i);
                                        AppointmentListModel.Data.Patient.Appointment userItem = new Gson().fromJson(user.toString(), AppointmentListModel.Data.Patient.Appointment.class);
                                        System.out.println(new Gson().fromJson(user.toString(), AppointmentListModel.Data.Patient.Appointment.class));

                                        userItem.setFullName(fullName);


                                        uList.add(userItem);
//                                        dbHandler.AddAppointment(userItem);
                                    }

                                    userList.addAll(uList);
                                    adapter.addUserList(uList);
                                }

                                if (pPosition > 1 && pPosition <= userList.size() - 1) {
                                    userList.remove(pPosition);
                                    adapter.removeUser(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

                                checkForEmptyState();

//                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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

//        userList.clear();
//        userList.add(new UserItem(1, "John Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//        userList.add(new UserItem(2, "Jane Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
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

    public void deleteAppointment(long appointmentId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_APPOINTMENT_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("appointment_id", String.valueOf(appointmentId))
//                .setTag("delete-user-api")
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
                                userList.remove(position);
                                adapter.removeUser(position);
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

    public void changeUserStatusOffline(long userId, int userStatus) {
        long i = dbHandler.updateUserStatusOffline(userId, userStatus);
        if (i > 0) {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
//                getAppointment(pPosition);
                getUsersFromDB();
            } else {
                isLoadedOnline = false;
                getUsersFromDB();
            }
        }
    }

    public void changeUserStatus(long userId, int userStatus) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + USER_STATUS_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("user_id", String.valueOf(userId))
                .addBodyParameter("user_status", String.valueOf(userStatus))
                .setTag("change-user-status-api")
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
                                    getAppointment(pPosition);
                                } else {
                                    isLoadedOnline = false;
                                    getUsersFromDB();
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

    private void setPopUpWindowForChangeStatus(View parentView, long userId, long onlineId, int currentStatus) {
        PopupChangeUserStatusBinding popupBinding = PopupChangeUserStatusBinding.inflate(getLayoutInflater());

        if (currentStatus == 1)
            popupBinding.tvActivate.setVisibility(View.GONE);
        else if (currentStatus == 0)
            popupBinding.tvBlock.setVisibility(View.GONE);

        popupBinding.tvActivate.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
//                changeUserStatusOffline(userId, 1);
                changeUserStatus(onlineId, 1);
            else
                changeUserStatusOffline(userId, 1);
            popupWindow.dismiss();
        });
        popupBinding.tvBlock.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
                changeUserStatus(onlineId, 0);
//                changeUserStatusOffline(userId, 0);}
            else
                changeUserStatusOffline(userId, 0);
            popupWindow.dismiss();
        });

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
//                getAppointment(pPosition);
//            } else {
//                isLoadedOnline = false;
//                getUsersFromDB();
//            }
//        }

//        if (requestCode == REQUEST_DELETE_HEALTH_PATIENT_APPOINTMENT_DELETE && resultCode == RESULT_SUCCESS_DELETE_USER) {
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
    public void onUserAdded(TaskAddedEvent event) {
        if (ConnectivityReceiver.isConnected()) {
            isLoadedOnline = true;
            currentPage = PAGE_START;
            getAppointment(pPosition);
        } else {
            isLoadedOnline = false;
            getUsersFromDB();
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