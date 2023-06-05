package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.HEALTH_RECORD_ACTIVE_VISIT_ADD;
import static com.safra.utilities.Common.HEALTH_RECORD_ACTIVE_VISIT_DELETE;
import static com.safra.utilities.Common.HEALTH_RECORD_DELETE_PATIENT;
import static com.safra.utilities.Common.HEALTH_RECORD_OVERVIEW_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_DELETE_ACTIVE_VISITS_DELETE;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.safra.AddPatient;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.OverviewActiveVisitListRecyclerAdapter;
import com.safra.databinding.FragmentOverviewActiveVisitsBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.OverviewDataModel;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class OverviewActiveVisitsFragment extends DialogFragment {
    public static final String TAG = "overview_visits";
    private FragmentActivity mActivity = null;



    private FragmentOverviewActiveVisitsBinding binding;
    private final List<OverviewDataModel.Data.Visit> visitsList = new ArrayList<>();
    private OverviewActiveVisitListRecyclerAdapter visitsAdapter;
    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;
    private long patientId = -1;
    private boolean isRemembered;
    private boolean isLoadedOnline = false;
    private Calendar calendarStart, calendarEnd;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
    private TextView tvStartDateTitle, tvTimeTitle;
    private TextInputEditText etStartDate, etTime;
    private PopupWindow popupWindow;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOverviewActiveVisitsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();
        binding.ivBack.setOnClickListener(v -> dismiss());
        patientId = getArguments().getLong("patientId", -1);
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        setText();

        binding.rvActiveVisits.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvActiveVisits.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        visitsAdapter = new OverviewActiveVisitListRecyclerAdapter(mActivity,getChildFragmentManager(), new OverviewActiveVisitListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onDelete(OverviewDataModel.Data.Visit item, int position) {
                DeleteDialog dialogD = new DeleteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("request_key", REQUEST_DELETE_ACTIVE_VISITS_DELETE);
                bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", getString(R.string.do_you_want_to_delete_this_user)));
                bundle.putLong("id", item.getId());
//                bundle.putLong("online_id", item.getUserOnlineId());
                bundle.putInt("position", position);
                bundle.putString("type", "user");
                dialogD.setArguments(bundle);
                dialogD.show(getChildFragmentManager(), DeleteDialog.TAG);
            }

            @Override
            public void onEdit(OverviewDataModel.Data.Visit item, int position) {
                Intent i = new Intent(mActivity, AddPatient.class);
                i.putExtra("heading", LanguageExtension.setText("edit_patient", getString(R.string.edit_patient)));
                i.putExtra("is_new", false);
                i.putExtra("patient_Id", item.getId());

//                i.putExtra("online_id", item.getUserOnlineId());
                startActivity(i);
            }

            @Override
            public void onView(OverviewDataModel.Data.Visit item, int position) {
//                UserDetailFragment dialogD = new UserDetailFragment();
//                Bundle bundle = new Bundle();
//                bundle.putLong("user_id", item.getId());
////                bundle.putLong("online_id", item.getUserOnlineId());
//                dialogD.setArguments(bundle);
//                dialogD.show(mActivity.getSupportFragmentManager(), UserDetailFragment.TAG);
            }

            @Override
            public void changeStatus(View view, OverviewDataModel.Data.Visit item, int position) {
//                setPopUpWindowForChangeStatus(view, item.getUserId(), item.getUserOnlineId(), item.getUserStatus());
            }
        });
        binding.rvActiveVisits.setAdapter(visitsAdapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
//            getUsersFromDB();
            getOverviewDetails(pPosition);
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
                getOverviewDetails(pPosition);
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
//                    getOverviewDetails(pPosition);
//                } else {
//                    adapter.searchUser(searchText);
//                    checkForEmptyState();
//                }
//            }
//        });

        binding.fabAdd.setOnClickListener(v -> {
            showDialog(getContext());

//
//            Intent i = new Intent(mActivity, AddPatient.class);
//            i.putExtra("heading", LanguageExtension.setText("add_patient", getString(R.string.add_patient)));
//            i.putExtra("is_new", true);
//            startActivity(i);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_DELETE_ACTIVE_VISITS_DELETE, this,
                (requestKey, result) -> {
                    long userId = result.getLong("id");
//                    long onlineId = result.getLong("online_id");
                    int position = result.getInt("position");
                    if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                        deleteActiveVisits(userId, position);
//                        deleteUserOffline(userId, position);
                    } else
                        deleteActiveVisits(userId, position);

//                    deleteUserOffline(userId, position);
                });

        return binding.getRoot();
    }

    public void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.add_active_visits_dialog, null);
        builder.setView(dialogView);


        tvStartDateTitle = dialogView.findViewById(R.id.tvStartDateTitle);
        tvTimeTitle = dialogView.findViewById(R.id.tvTimeTitle);
        etStartDate = dialogView.findViewById(R.id.etStartDate);
        etTime = dialogView.findViewById(R.id.etTime);

        etStartDate.setFocusableInTouchMode(false);
        etTime.setFocusableInTouchMode(false);

        etStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendarStart.set(Calendar.YEAR, year);
                calendarStart.set(Calendar.MONTH, month);
                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etStartDate.setText(sdfToShow.format(calendarStart.getTime()));
            }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                etTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle OK button click
            String startDate = etStartDate.getText().toString();
            String time = etTime.getText().toString();

            String inputDate = startDate;
            String outputFormat = "yyyy-MM-dd";

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);

            Date date;
            String formattedDate = "";

            try {
                date = inputDateFormat.parse(inputDate);
                formattedDate = outputDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            System.out.println(formattedDate); // Output: 2023-06-21


            saveActiveVisits(formattedDate,time);
            // Do something with the selected values
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle Cancel button click
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveActiveVisits(String startDate, String time) {

        LoadingDialogExtension.showLoading(getContext(), LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));


        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ACTIVE_VISIT_ADD)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(patientId))
                .addBodyParameter("start_date", startDate)
                .addBodyParameter("start_time",time )
//                .setTag("save-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                              getOverviewDetails(pPosition);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

//                        dialogL.dismiss();
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

    private void setText() {
//        binding.etSearch.setHint(LanguageExtension.setText("search_the_user", getString(R.string.search_the_user)));
        binding.tvEmptyState.setText(LanguageExtension.setText("no_user_found", getString(R.string.no_user_found)));
    }


    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(visitsList, visitsAdapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
//        getOverviewDetails(p);
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

                            if (currentPage == PAGE_START) {
                                visitsList.clear();
                                visitsAdapter.clearLists();
//                                    pPosition = -1;
                            }
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject vital = data.getJSONObject("vital");
                                JSONArray visits = data.getJSONArray("visits");


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

    public void deleteUserOffline(long userId, int position) {
        int i = dbHandler.deleteUserOffline(userId);

        if (i > 0) {
            visitsList.remove(position);
            visitsAdapter.removeUser(position);
            checkForEmptyState();
        }
    }

    public void deleteActiveVisits(long userId, int position) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ACTIVE_VISIT_DELETE)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("visit_id", String.valueOf(userId))
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
                                visitsList.remove(position);
                                visitsAdapter.removeUser(position);
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
        if (visitsAdapter != null) {
            if (visitsAdapter.getItemCount() > 0) {
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
            getOverviewDetails(pPosition);
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