package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_ACTIVE_VITAL_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_PATIENT_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_DELETE_USER;
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
import com.safra.AddUser;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ActiveVisitsRecyclerAdapter;
import com.safra.adapters.UsersRecyclerAdapter;
import com.safra.databinding.FragmentActiveVisitsBinding;
import com.safra.databinding.FragmentUsersBinding;
import com.safra.databinding.PopupChangeUserStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.UserAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ActiveVisitsModel;
import com.safra.models.PatientListModel;
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


public class ActiveVisitsFragment extends Fragment {
    public static final String TAG = "active_visits_fragment";
    private FragmentActivity mActivity = null;

    private FragmentActiveVisitsBinding binding;

    private final List<ActiveVisitsModel.Datum> userList = new ArrayList<>();
    private final List<PatientListModel.Data.Patient> userList1 = new ArrayList<>();
    private ActiveVisitsRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;
    private boolean isLoadedOnline = false;

    private PopupWindow popupWindow;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActiveVisitsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        setText();

//        if (PermissionExtension.checkForPermission(USER_ADD)) {
//            binding.fabAdd.setVisibility(View.VISIBLE);
//        } else {
//            binding.fabAdd.setVisibility(View.VISIBLE);
//        }

        binding.rvActiveVisits.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvActiveVisits.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new ActiveVisitsRecyclerAdapter(mActivity, getChildFragmentManager(),new ActiveVisitsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ActiveVisitsModel.Datum item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_USER);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());

                bundle.putInt("position", position);
                bundle.putString("type", "user");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(ActiveVisitsModel.Datum item, int position) {
                Intent i = new Intent(mActivity, AddUser.class);
                i.putExtra("heading", LanguageExtension.setText("edit_user", getString(R.string.edit_user)));
                i.putExtra("is_new", false);
                i.putExtra("user_id", item.getId());
                startActivity(i);
            }

            @Override
            public void onView(ActiveVisitsModel.Datum item, int position) {
                UserDetailFragment dialogD = new UserDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", item.getId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), UserDetailFragment.TAG);
            }

            @Override
            public void changeStatus(View view, ActiveVisitsModel.Datum item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getUserId(), item.getUserOnlineId(), item.getUserStatus());
            }
        });
        binding.rvActiveVisits.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getActiveVisits(pPosition);
//            getPatients(pPosition);
//            getActiveVisits(pPosition);
//            getPatients(pPosition);
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
//                getUsersFromDB();
//                getActiveVisits(pPosition);
            } else {
                isLoadedOnline = false;
//                getUsersFromDB();
            }

        });

        binding.rvActiveVisits.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                    getActiveVisits(pPosition);
//                } else {
//                    adapter.searchUser(searchText);
//                    checkForEmptyState();
//                }
//            }
//        });
//
//        binding.fabAdd.setOnClickListener(v -> {
//            Intent i = new Intent(mActivity, AddUser.class);
//            i.putExtra("heading", LanguageExtension.setText("add_user", getString(R.string.add_user)));
//            i.putExtra("is_new", true);
//            startActivity(i);
//        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_USER, this,
                (requestKey, result) -> {
                    long userId = result.getLong("id");
                    long onlineId = result.getLong("online_id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteUser(onlineId, position);
                        deleteUserOffline(userId, position);
                    } else
                        deleteUser(onlineId, position);

                    deleteUserOffline(userId, position);
                });

        return binding.getRoot();
    }

    private void setText() {
//        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));
        binding.tvEmptyState.setText(LanguageExtension.setText("no_active_visit", getString(R.string.no_active_visit)));
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

        if (binding.srlManageUser.isRefreshing())
            binding.srlManageUser.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getActiveVisits(p);
    }

//    private void addLoadingAnimation() {
//        userList.add(null);
//        pPosition = userList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//    }

    private void getPatients(int pPosition) {
        Log.e(TAG, "API CALL " + pPosition);


        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_PATIENT_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
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
                                JSONObject data = response.getJSONObject("data");
                                JSONArray patients = data.getJSONArray("patients");
                                Log.e(TAG, "onResponse Success: " + patients);


                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (patients.length() > 0) {
//                                    List<UserItem> uList = new ArrayList<>();
                                    List<PatientListModel.Data.Patient> uList = new ArrayList<>();
                                    for (int i = 0; i < patients.length(); i++) {
                                        JSONObject user = patients.getJSONObject(i);
//                                        UserItem userItem = new UserItem();
                                        PatientListModel.Data.Patient userItem = new PatientListModel.Data.Patient();
                                        userItem.setId(user.getInt("id"));
                                        userItem.setUser_id(user.getInt("user_id"));
                                        userItem.setFirst_name(user.getString("first_name"));
                                        userItem.setMiddle_name(user.getString("middle_name"));
                                        userItem.setLast_name(user.getString("last_name"));
                                        userItem.setGender(user.getString("gender"));
                                        userItem.setBirthdate(user.getString("birthdate"));
//                                        userItem.setPhone(user.getInt("phone"));
                                        userItem.setAddress(user.getString("address"));


                                        if (user.has("phone") && !user.isNull("phone"))
                                            userItem.setPhone(user.getString("phone"));
                                        if (user.has("mobile") && !user.isNull("mobile"))
                                            userItem.setMobile(user.getString("mobile"));

                                        uList.add(userItem);
//                                        dbHandler.AddPatient(userItem);
                                    }

                                    userList1.addAll(uList);
                                    adapter.addUserList1(uList);
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
                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                    }
                });

    }


    private void getActiveVisits(int pPosition) {
        binding.srlManageUser.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ACTIVE_VITAL_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
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
//                            String message = response.getString("message");
                            if (success == 1) {
                                JSONArray data = response.getJSONArray("data");
//                                JSONArray users = data.getJSONArray("user_list");
//                                int totalPage = data.getInt("total_page");
//                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (data.length() > 0) {
                                    List<ActiveVisitsModel.Datum> uList = new ArrayList<>();
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject user = data.getJSONObject(i);
                                        ActiveVisitsModel.Datum userItem = new ActiveVisitsModel.Datum();

                                        if (user.has("patient_name") && !user.isNull("patient_name"))
                                            userItem.setPatient_name(user.getString("patient_name"));

                                        if (user.has("start_date") && !user.isNull("start_date"))
                                            userItem.setStart_date(user.getString("start_date"));


                                        if (user.has("start_time") && !user.isNull("start_time"))
                                            userItem.setStart_time(user.getString("start_time"));

                                        if (user.has("patient_id") && !user.isNull("patient_id"))
                                            userItem.setPatient_id(user.getInt("patient_id"));


//                                        user.getString(userItem.getStart_date());



                                        uList.add(userItem);
//                                        dbHandler.addUser(userItem);
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
                                Toast.makeText(mActivity, "API Issue ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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
//        userList.add(new ActiveVisitsModel.Datum(1, "John Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//        userList.add(new ActiveVisitsModel.Datum(2, "Jane Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
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

    public void deleteUser(long userId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + USER_DELETE_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("user_id", String.valueOf(userId))
                .setTag("delete-user-api")
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
//                getActiveVisits(pPosition);
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
                                    getActiveVisits(pPosition);
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
//                getActiveVisits(pPosition);
//            } else {
//                isLoadedOnline = false;
//                getUsersFromDB();
//            }
//        }

//        if (requestCode == REQUEST_DELETE_USER && resultCode == RESULT_SUCCESS_DELETE_USER) {
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
            getActiveVisits(pPosition);
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
