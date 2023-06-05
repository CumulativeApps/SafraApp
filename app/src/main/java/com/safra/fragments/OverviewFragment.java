package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_ALLERGIES_DELETE;
import static com.safra.utilities.Common.HEALTH_RECORD_ALLERGIES_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_DIAGNOSTICS_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_OVERVIEW_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_DELETE_HEALTH_ALLERGIES_DELETE;
import static com.safra.utilities.Common.REQUEST_DELETE_USER;
import static com.safra.utilities.Common.USER_STATUS_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.safra.AddAllergies;
import com.safra.AddUser;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.AllergiesListRecyclerAdapter;
import com.safra.adapters.CaptureVitalListRecyclerAdapter;
import com.safra.adapters.OverviewActiveVisitsRecyclerAdapter;
import com.safra.adapters.OverviewDiagnosticsListAdapter;
import com.safra.databinding.FragmentOverviewBinding;
import com.safra.databinding.PopupChangeUserStatusBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ActiveVisitsModel;
import com.safra.models.AllergiesListModel;
import com.safra.models.DiagnosticsListModel;
import com.safra.models.OverviewDataModel;
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

public class OverviewFragment extends DialogFragment {

    public static final String TAG = "overview_fragment";
    private FragmentActivity mActivity = null;

    private FragmentOverviewBinding binding;

    private OverviewDiagnosticsListAdapter adapter;
    private OverviewActiveVisitsRecyclerAdapter visitsAdapter;
    private AllergiesListRecyclerAdapter allergiesAdapter;
    private CaptureVitalListRecyclerAdapter vitalsAdapter;
    private boolean isRemembered;
    private long patientId = -1;

    private final List<DiagnosticsListModel.Data.Patient.Diagnostic> userList = new ArrayList<>();
    private final List<OverviewDataModel.Data.Visit> visitsList = new ArrayList<>();
    private final List<AllergiesListModel.Data.Patient.Allergy> allergiesList = new ArrayList<>();


    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;
    String fName, mName, lName;
    String fullName;

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
        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();

        binding.ivBack.setOnClickListener(v -> dismiss());

        setText();


        patientId = getArguments().getLong("overview_id", -1);


        fName = getArguments().getString("f_name");
        mName = getArguments().getString("m_name");
        lName = getArguments().getString("l_name");


        fullName = " " + fName + " " + mName + " " + lName;
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OverviewActiveVisitsFragment dialogD = new OverviewActiveVisitsFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("patientId" , patientId);

                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), OverviewActiveVisitsFragment.TAG);
            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllergiesListFragment dialogD = new AllergiesListFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("patient_id", patientId);
                dialogD.setArguments(bundle);
                bundle.putString("f_name", fName);
                bundle.putString("m_name", mName);
                bundle.putString("l_name", lName);
                dialogD.show(mActivity.getSupportFragmentManager(), AllergiesListFragment.TAG);
            }
        });


        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
//            getAllergies(pPosition);
            getOverviewDetails(pPosition);

            getDiagnosticsList(pPosition);
            getAllergies(pPosition);

//            isLoadedOnline = true;
        } else {
            isLoadedOnline = false;
            getUsersFromDB();
        }

//        binding.srlManageProject.setOnRefreshListener(() -> {
//            if (ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
////                getUsersFromDB();
//                getUsersFromDB();
//                getAllergies(pPosition);
//            } else {
//                isLoadedOnline = false;
//                getUsersFromDB();
//            }
//
//        });

        binding.visitsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.visitsRecyclerView.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        visitsAdapter = new OverviewActiveVisitsRecyclerAdapter(mActivity, getChildFragmentManager(), new OverviewActiveVisitsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(OverviewDataModel.Data.Visit item, int position) {
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
            public void onEdit(OverviewDataModel.Data.Visit item, int position) {
                Intent i = new Intent(mActivity, AddUser.class);
                i.putExtra("heading", LanguageExtension.setText("edit_user", getString(R.string.edit_user)));
                i.putExtra("is_new", false);
                i.putExtra("user_id", item.getId());
                startActivity(i);
            }

            @Override
            public void onView(OverviewDataModel.Data.Visit item, int position) {
                UserDetailFragment dialogD = new UserDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", item.getId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), UserDetailFragment.TAG);
            }

            @Override
            public void changeStatus(View view, OverviewDataModel.Data.Visit item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getUserId(), item.getUserOnlineId(), item.getUserStatus());
            }
        });
        binding.visitsRecyclerView.setAdapter(visitsAdapter);

        binding.visitsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        binding.diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.diagnosisRecyclerView.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new OverviewDiagnosticsListAdapter(mActivity, new OverviewDiagnosticsListAdapter.OnItemClickListener() {
            @Override
            public void onDelete(DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {

            }

            @Override
            public void onEdit(DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {

            }

            @Override
            public void onView(DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {

            }


            @Override
            public void changeStatus(View view, DiagnosticsListModel.Data.Patient.Diagnostic item, int position) {
            }
        });
        binding.diagnosisRecyclerView.setAdapter(adapter);
        binding.diagnosisRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        binding.allergiesRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.allergiesRecyclerView.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        allergiesAdapter = new AllergiesListRecyclerAdapter(mActivity, new AllergiesListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(AllergiesListModel.Data.Patient.Allergy item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_HEALTH_ALLERGIES_DELETE);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
                bundle.putInt("position", position);
                bundle.putString("type", "user");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(AllergiesListModel.Data.Patient.Allergy item, int position) {
                Intent i = new Intent(mActivity, AddAllergies.class);
                i.putExtra("heading", LanguageExtension.setText("edit_allergies", getString(R.string.edit_allergies)));
                i.putExtra("is_new", false);
                i.putExtra("allergies_patient_id", patientId);
                i.putExtra("allergy_id", item.getId());
                i.putExtra("allergen", item.getAllergen());
                i.putExtra("reaction", item.getReaction());
                i.putExtra("comment", item.getComment());
                i.putExtra("severity", item.getSeverity());

                startActivity(i);
            }

            @Override
            public void onView(AllergiesListModel.Data.Patient.Allergy item, int position) {
                UserDetailFragment dialogD = new UserDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", item.getId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), UserDetailFragment.TAG);
            }

            @Override
            public void changeStatus(View view, AllergiesListModel.Data.Patient.Allergy item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getUserId(), item.getUserOnlineId(), item.getUserStatus());
            }
        });
        binding.allergiesRecyclerView.setAdapter(allergiesAdapter);
        binding.allergiesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        return binding.getRoot();
    }

    private void getOverviewDetails(int pPosition) {


        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_OVERVIEW_LIST)
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
                                JSONObject vital = data.getJSONObject("vital");
                                JSONArray visits = data.getJSONArray("visits");

//                                OverviewDataModel.Data.Vital userItem = new OverviewDataModel.Data.Vital();
                                OverviewDataModel.Data.Vital userItem = new Gson().fromJson(vital.toString(), OverviewDataModel.Data.Vital.class);

                                binding.tvHeight.setText(String.valueOf(userItem.getHeight() + " cm"));
                                binding.tvWeight.setText(String.valueOf(userItem.getWeight() + " kg"));
                                binding.tvBMI.setText(String.valueOf(String.valueOf(userItem.getbMI())));
                                binding.tvTemperature.setText(String.valueOf(userItem.getTemperature() + " °C"));
                                binding.tvPulse.setText(String.valueOf(userItem.getPulse() + "/min"));
                                binding.tvRespiratoryRate.setText(String.valueOf(userItem.getRespiratory_rate() + "/min"));
                                binding.tvBloodPressure.setText(String.valueOf(userItem.getBlood_pressure()));
                                binding.tvBloodOxygen.setText(String.valueOf(userItem.getBlood_oxygen_saturation() + " %"));



//                                if (visits.length() > 0) {
//                                    for (int i = 0; i < visits.length(); i++) {
//                                        JSONObject user = visits.getJSONObject(i);
//
//                                        userItem1.setStart_date(user.getString("start_date"));
//                                        userItem1.setStart_time(user.getString("start_time"));
//                                        System.out.println("userItem1.getStart_date():-" +userItem1.getStart_date());
//
//
//
//                                        vitalsList.add(userItem1);
////
//                                    }
//                                }
                                if (visits.length() > 0) {
                                    List<OverviewDataModel.Data.Visit> uList = new ArrayList<>();

                                    for (int i = 0; i < visits.length(); i++) {
                                        JSONObject user = visits.getJSONObject(i);
                                        OverviewDataModel.Data.Visit visit = new Gson().fromJson(user.toString(), OverviewDataModel.Data.Visit.class);
                                        uList.add(visit);
                                        System.out.println("USER-" + user);




                                    }
                                    visitsList.addAll(uList);
                                    visitsAdapter.addUserList(uList);
                                }


//                                if (currentPage == PAGE_START) {
//                                    visitsList.clear();
//                                    visitsAdapter.clearLists();
////                                    pPosition = -1;
//                                }


//                                if (users.length() > 0) {
//                                    List<AllergiesListModel.Data.Patient.Allergy> uList = new ArrayList<>();
//                                    for (int i = 0; i < users.length(); i++) {
//                                        JSONObject user = users.getJSONObject(i);
////                                        AllergiesListModel.Data.Patient.Allergy userItem = new AllergiesListModel.Data.Patient.Allergy();
//                                        AllergiesListModel.Data.Patient.Allergy userItem = new Gson().fromJson(user.toString(), AllergiesListModel.Data.Patient.Allergy.class);
//
//
//                                        uList.add(userItem);
////                                        dbHandler.AddAllergies(userItem);
//                                    }
//
//                                    allergiesList.addAll(uList);
//                                    allergiesAdapter.addUserList(uList);
//                                }

                                if (pPosition > 1 && pPosition <= visitsList.size() - 1) {
                                    visitsList.remove(pPosition);
                                    visitsAdapter.removeUser(pPosition);
                                    visitsAdapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    visitsAdapter.notifyDataSetChanged();
                                else
                                    visitsAdapter.notifyItemRangeInserted(pPosition, data.length());

                                checkForEmptyState();

//                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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


//    private void getOverviewDetails(int pPosition) {
//        isNextPageCalled = true;
//
//        AndroidNetworking
//                .post(BASE_URL + HEALTH_RECORD_OVERVIEW_LIST)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("patient_id", String.valueOf(patientId))
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            if (success == 1) {
//                                JSONObject data = response.getJSONObject("data");
//                                JSONObject vital = data.getJSONObject("vital");
//                                JSONArray visits = data.getJSONArray("visits");
//
//                                OverviewDataModel.Data.Vital userItem = new Gson().fromJson(vital.toString(), OverviewDataModel.Data.Vital.class);
//
//                                // Set vital data
//                                binding.tvHeight.setText(String.valueOf(userItem.getHeight() + " cm"));
//                                binding.tvWeight.setText(String.valueOf(userItem.getWeight() + " kg"));
//                                binding.tvBMI.setText(String.valueOf(userItem.getbMI()));
//                                binding.tvTemperature.setText(String.valueOf(userItem.getTemperature() + " °C"));
//                                binding.tvPulse.setText(String.valueOf(userItem.getPulse() + "/min"));
//                                binding.tvRespiratoryRate.setText(String.valueOf(userItem.getRespiratory_rate() + "/min"));
//                                binding.tvBloodPressure.setText(String.valueOf(userItem.getBlood_pressure()));
//                                binding.tvBloodOxygen.setText(String.valueOf(userItem.getBlood_oxygen_saturation() + " %"));
//
//                                if (visits.length() > 0) {
//                                    for (int i = 0; i < visits.length(); i++) {
//                                        JSONObject user = visits.getJSONObject(i);
//                                        OverviewDataModel.Data.Visit visit = new Gson().fromJson(user.toString(), OverviewDataModel.Data.Visit.class);
//                                        visitsList.add(visit);
//                                        visitsAdapter.addUserList(visitsList);
//
//
//
//                                    }
//                                }
//
//                                // Use the visitList as needed
//
////                                if (currentPage == PAGE_START) {
////                                    vitalsList.clear();
////                                    visitsAdapter.clearLists();
//////                                    pPosition = -1;
////                                }
//
//                                if (pPosition > 1 && pPosition <= visitsList.size() - 1) {
//                                    visitsList.remove(pPosition);
//                                    visitsAdapter.removeUser(pPosition);
//                                    visitsAdapter.notifyItemChanged(pPosition - 1);
//                                }
//
//                                if (currentPage == PAGE_START)
//                                    visitsAdapter.notifyDataSetChanged();
//                                else
//                                    visitsAdapter.notifyItemRangeInserted(pPosition, data.length());
//
//                                checkForEmptyState();
//
////                                isLastPage = totalPage <= currentPage;
//                            } else {
//                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                        }
//
//                        isNextPageCalled = false;
//
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
//                    }
//                });
//
//    }


    private void setText() {
//        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));

//        binding.tvEmptyState.setText(LanguageExtension.setText("no_user_found", getString(R.string.no_user_found)));
    }

    private void getUsersFromDB() {
        userList.clear();

//        userList.addAll(dbHandler.getAllergies(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        for (AllergiesListModel.Data.Patient.Allergy userItem : userList) {
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

//        if (binding.srlManageProject.isRefreshing())
//            binding.srlManageProject.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(userList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getAllergies(p);
    }

    //    private void addLoadingAnimation() {
//        userList.add(null);
//        pPosition = userList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//    }
    private void getDiagnosticsList(int pPosition) {
//    binding.srlManageProject.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_DIAGNOSTICS_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(patientId))
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
//
//                    if (binding.srlManageProject.isRefreshing())
//                        binding.srlManageProject.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

//                    if (binding.srlManageProject.isRefreshing())
//                        binding.srlManageProject.setRefreshing(false);
                    }
                });
    }


    private void getAllergies(int pPosition) {


        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ALLERGIES_LIST)
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
                                JSONObject data = response.getJSONObject("data").getJSONObject("patient");
                                JSONArray users = data.getJSONArray("allergies");


                                if (currentPage == PAGE_START) {
                                    allergiesList.clear();
                                    allergiesAdapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (users.length() > 0) {
                                    List<AllergiesListModel.Data.Patient.Allergy> uList = new ArrayList<>();
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
//                                        AllergiesListModel.Data.Patient.Allergy userItem = new AllergiesListModel.Data.Patient.Allergy();
                                        AllergiesListModel.Data.Patient.Allergy userItem = new Gson().fromJson(user.toString(), AllergiesListModel.Data.Patient.Allergy.class);


                                        uList.add(userItem);
//                                        dbHandler.AddAllergies(userItem);
                                    }

                                    allergiesList.addAll(uList);
                                    allergiesAdapter.addUserList(uList);
                                }

                                if (pPosition > 1 && pPosition <= allergiesList.size() - 1) {
                                    allergiesList.remove(pPosition);
                                    allergiesAdapter.removeUser(pPosition);
                                    allergiesAdapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    allergiesAdapter.notifyDataSetChanged();
                                else
                                    allergiesAdapter.notifyItemRangeInserted(pPosition, data.length());

                                checkForEmptyState();

//                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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


    public void deleteUserOffline(long userId, int position) {
        int i = dbHandler.deleteUserOffline(userId);

        if (i > 0) {
            userList.remove(position);
            adapter.removeUser(position);
            checkForEmptyState();
        }
    }

    public void deleteAllergies(long userId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ALLERGIES_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("allergie_id", String.valueOf(userId))
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
//                getAllergies(pPosition);
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
                                    getAllergies(pPosition);
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
//        if (adapter != null) {
//            if (adapter.getItemCount() > 0) {
//                binding.clData.setVisibility(View.VISIBLE);
//                binding.clEmptyState.setVisibility(View.GONE);
//            } else {
//                binding.clData.setVisibility(View.GONE);
//                binding.clEmptyState.setVisibility(View.VISIBLE);
//            }
//        } else {
//            binding.clData.setVisibility(View.GONE);
//            binding.clEmptyState.setVisibility(View.VISIBLE);
//        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_EDIT_USER && resultCode == RESULT_SUCCESS_EDIT_USER) {
//            if (ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
//                getAllergies(pPosition);
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
    public void onUserAdded(TaskAddedEvent event) {
        if (ConnectivityReceiver.isConnected()) {
            isLoadedOnline = true;
            currentPage = PAGE_START;
            getAllergies(pPosition);
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