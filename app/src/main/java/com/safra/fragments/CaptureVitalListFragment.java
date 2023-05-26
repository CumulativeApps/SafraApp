package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_CAPTURE_VITAL_DELETE;
import static com.safra.utilities.Common.HEALTH_RECORD_CAPTURE_VITAL_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_DELETE_HEALTH_PATIENT_CAPTURE_VITAL_LIST;
import static com.safra.utilities.Common.USER_DELETE_API;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.Common.USER_STATUS_API;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
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
import android.widget.PopupWindow;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.safra.AddAllergies;
import com.safra.AddCaptureVitals;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.AllergiesListRecyclerAdapter;
import com.safra.adapters.CaptureVitalListRecyclerAdapter;
import com.safra.databinding.FragmentAllergiesListBinding;
import com.safra.databinding.FragmentCaptureVitalListBinding;
import com.safra.databinding.PopupChangeUserStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.events.UserAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.AppointmentListModel;
import com.safra.models.CaptureVitalListModel;
import com.safra.models.UserItem;
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


public class CaptureVitalListFragment extends DialogFragment {
    public static final String TAG = "vitals_list_fragment";
    private FragmentActivity mActivity = null;

    private FragmentCaptureVitalListBinding binding;

    private CaptureVitalListRecyclerAdapter adapter;

    private boolean isRemembered;

    String fname, mname, lname;
    String fullName = "";
    private final List<CaptureVitalListModel.Data.Vital> userList = new ArrayList<>();
    private long patientId;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;


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
        binding = FragmentCaptureVitalListBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();

        binding.ivBack.setOnClickListener(v -> dismiss());

        setText();
        patientId = getArguments().getLong("capture_patient_id");
        fname = getArguments().getString("f_name", "");
        mname = getArguments().getString("m_name", "");
        lname = getArguments().getString("l_name", "");
        fullName = " " + fname + " " + mname + " " + lname;


        binding.rvCaptureVitalsList.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvCaptureVitalsList.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new CaptureVitalListRecyclerAdapter(mActivity, new CaptureVitalListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(CaptureVitalListModel.Data.Vital item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_HEALTH_PATIENT_CAPTURE_VITAL_LIST);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "user");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(CaptureVitalListModel.Data.Vital item, int position) {

            }

            @Override
            public void onView(CaptureVitalListModel.Data.Vital item, int position) {

            }

            @Override
            public void changeStatus(View view, CaptureVitalListModel.Data.Vital item, int position) {
            }
        });
        binding.rvCaptureVitalsList.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            getUsersFromDB();
            getCaptureVitalDetail(pPosition);
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
                getCaptureVitalDetail(pPosition);
            } else {
                isLoadedOnline = false;
                getUsersFromDB();
            }

        });

        binding.rvCaptureVitalsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    getCaptureVitalDetail(pPosition);
                } else {
                    adapter.searchUser(searchText);
                    checkForEmptyState();
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, AddCaptureVitals.class);
            i.putExtra("heading", LanguageExtension.setText("add_capture_vitals", getString(R.string.add_capture_vitals)));
            i.putExtra("is_new", true);
            i.putExtra("patientId", patientId);
            startActivity(i);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_HEALTH_PATIENT_CAPTURE_VITAL_LIST, this,
                (requestKey, result) -> {
                    long vitalId = result.getLong("id");
//                    long onlineId = result.getLong("online_id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteCaptureVital(vitalId, position);
//                        deleteUserOffline(userId, position);
                    } else
                        deleteCaptureVital(vitalId, position);

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
//
//        adapter.clearLists();
//        adapter.addUserList(userList);
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
        getCaptureVitalDetail(p);
    }


    private void getCaptureVitalDetail(int pPosition) {
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_CAPTURE_VITAL_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(patientId))
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
                                JSONArray users = data.getJSONArray("vitals");
//                                int totalPage = data.getInt("total_page");
//                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (users.length() > 0) {
                                    List<CaptureVitalListModel.Data.Vital> uList = new ArrayList<>();
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
//                                        CaptureVitalListModel.Data.Vital userItem = new CaptureVitalListModel.Data.Vital();
                                        CaptureVitalListModel.Data.Vital userItem = new Gson().fromJson(user.toString(), CaptureVitalListModel.Data.Vital.class);

                                        userItem.setFullName(fullName);
                                        uList.add(userItem);
//                                        dbHandler.AddCaptureVitals(userItem);
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

    }

    public void deleteUserOffline(long userId, int position) {
        int i = dbHandler.deleteUserOffline(userId);

        if (i > 0) {
            userList.remove(position);
            adapter.removeUser(position);
            checkForEmptyState();
        }
    }

    public void deleteCaptureVital(long vitalId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_CAPTURE_VITAL_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("vital_id", String.valueOf(vitalId))
                .addBodyParameter("patient_id", String.valueOf(patientId))
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
            getCaptureVitalDetail(pPosition);
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