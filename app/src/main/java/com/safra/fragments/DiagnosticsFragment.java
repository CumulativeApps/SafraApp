package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_PATIENT_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.DiagnosticsRecyclerAdapter;
import com.safra.databinding.FragmentDiagnosticsBinding;
import com.safra.events.UserAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.PatientListModel;
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


public class DiagnosticsFragment extends Fragment {

    public static final String TAG = "diagnostics_fragment";

    private FragmentActivity mActivity = null;

    private FragmentDiagnosticsBinding binding;

    private final List<PatientListModel.Data.Patient> userList = new ArrayList<>();

    private DiagnosticsRecyclerAdapter adapter;

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

        binding = FragmentDiagnosticsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        setText();
        binding.rvDiagnostics.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvDiagnostics.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new DiagnosticsRecyclerAdapter(mActivity, new DiagnosticsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(PatientListModel.Data.Patient item, int position) {

            }

            @Override
            public void onEdit(PatientListModel.Data.Patient item, int position) {
                OverviewFragment dialogD = new OverviewFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("overview_id", item.getId());
                dialogD.setArguments(bundle);
                bundle.putString("f_name", item.getFirst_name());
                bundle.putString("m_name", item.getMiddle_name());
                bundle.putString("l_name", item.getLast_name());
                dialogD.show(mActivity.getSupportFragmentManager(), OverviewFragment.TAG);
            }

            @Override
            public void onView(PatientListModel.Data.Patient item, int position) {
                DiagnosticsListFragment dialogD = new DiagnosticsListFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("patient_id", item.getId());
                bundle.putString("f_name", item.getFirst_name());
                bundle.putString("m_name", item.getMiddle_name());
                bundle.putString("l_name", item.getLast_name());
//                bundle.putLong("online_id", item.getUserOnlineId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), DiagnosticsListFragment.TAG);
            }

            @Override
            public void changeStatus(View view, PatientListModel.Data.Patient item, int position) {
            }
        });
        binding.rvDiagnostics.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getPatients(pPosition);
//            isLoadedOnline = true;
        } else {
            isLoadedOnline = false;
//            getUsersFromDB();
        }

        binding.srlManageProject.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isLoadedOnline = true;
                currentPage = PAGE_START;
//                getUsersFromDB();
//                getUsersFromDB();
                getPatients(pPosition);
            } else {
                isLoadedOnline = false;
//                getUsersFromDB();
            }

        });

        binding.rvDiagnostics.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
//                    getPatients(pPosition);
//                } else {
//                    adapter.searchUser(searchText);
//                    checkForEmptyState();
//                }
//            }
//        });


        return binding.getRoot();
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
        getPatients(p);
    }

    private void getPatients(int pPosition) {
        Log.e(TAG, "API CALL " + pPosition);
        binding.srlManageProject.setRefreshing(currentPage == PAGE_START);

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
    public void onUserAdded(UserAddedEvent event) {
        if (ConnectivityReceiver.isConnected()) {
            isLoadedOnline = true;
            currentPage = PAGE_START;
            getPatients(pPosition);
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