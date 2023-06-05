package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.HEALTH_RECORD_CAPTURE_VITAL_SAVE;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.Common.TASK_SAVE_API;
import static com.safra.utilities.Common.TASK_VIEW_API;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.adapters.GroupCustomSpinnerAdapter;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.ActivityAddCaptureVitalsBinding;
import com.safra.databinding.ActivityAddPatientBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.PriorityItem;
import com.safra.models.RoleItem;
import com.safra.models.TaskItem;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddCaptureVitals extends AppCompatActivity {
    public static final String TAG = "add_capture_fragment";

    private ActivityAddCaptureVitalsBinding binding;

    private boolean isRemembered;


    private int selectedPriority = 1;
    private long patientId;
    private boolean isNew;
    private long taskId = -1, onlineId = -1;
    private TaskItem taskItem = null;
    private List<Long> currentUsers = new ArrayList<>();
    private List<Long> currentGroups = new ArrayList<>();

    private boolean isUserDataReceived = false, isGroupDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCaptureVitalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();


        if (getIntent() != null) {
            binding.tvAddCaptureVitalsHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            patientId = getIntent().getLongExtra("patientId", -1);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {
//                    getUserListFromDB();
//                    getGroupListFromDB();

                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {
                taskId = getIntent().getLongExtra("task_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                Log.e(TAG, "onCreate: " + taskId);
//                if (ConnectivityReceiver.isConnected())
////                    getEditData();
////                getEditDataFromDB();
//                else
//                    getEditDataFromDB();
            }
        }




        binding.btnSave.setOnClickListener(v -> {
            validateInputs();

        });
    }

    private void setText() {
        binding.tvHeightTitle.setText(LanguageExtension.setText("height_cm", getString(R.string.height_cm)));
        binding.tvWeightTitle.setText(LanguageExtension.setText("weight_kg", getString(R.string.weight_kg)));
        binding.tvTemperatureTitle.setText(LanguageExtension.setText("temperature", getString(R.string.temperature)));
        binding.tvPulseMinTitle.setText(LanguageExtension.setText("pulse_min", getString(R.string.pulse_min)));
        binding.tvRespiratoryRateTitle.setText(LanguageExtension.setText("respiratory_rate_min", getString(R.string.respiratory_rate_min)));
        binding.tvBloodPressureTitle.setText(LanguageExtension.setText("blood_pressure", getString(R.string.blood_pressure)));
        binding.tvBloodOxygenTitle.setText(LanguageExtension.setText("blood_oxygen", getString(R.string.blood_oxygen)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }



    private void validateInputs() {
        String fName = binding.etHeight.getText() != null ? binding.etHeight.getText().toString() : "";
        String mName = binding.etWeight.getText() != null ? binding.etWeight.getText().toString() : "";
        String lName = binding.etTemperature.getText() != null ? binding.etTemperature.getText().toString() : "";
        String tPhone = binding.etPulseMin.getText() != null ? binding.etPulseMin.getText().toString() : "";
        String tPhone2 = binding.etRespiratoryRate.getText() != null ? binding.etRespiratoryRate.getText().toString() : "";
        String sDate = binding.etBloodPressure.getText() != null ? binding.etBloodPressure.getText().toString() : "";
        String eAddress = binding.etBloodOxygen.getText() != null ? binding.etBloodOxygen.getText().toString() : "";


        if (fName.isEmpty() || mName.isEmpty() || lName.isEmpty() || sDate.isEmpty() || tPhone.isEmpty()|| tPhone2.isEmpty()|| eAddress.isEmpty()) {
            if (fName.isEmpty()) {
                binding.etHeight.setError(LanguageExtension.setText("enter_first_name", getString(R.string.enter_first_name)));
                binding.etHeight.requestFocus();
            }
            if (mName.isEmpty()) {
                binding.etWeight.setError(LanguageExtension.setText("enter_middle_name", getString(R.string.enter_middle_name)));
                binding.etWeight.requestFocus();
            }
            if (lName.isEmpty()) {
                binding.etTemperature.setError(LanguageExtension.setText("enter_last_name", getString(R.string.enter_last_name)));
                binding.etTemperature.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etPulseMin.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etPulseMin.requestFocus();
            }
            if (tPhone.isEmpty()) {
                binding.etRespiratoryRate.setError(LanguageExtension.setText("enter_phone_number", getString(R.string.enter_phone_number)));
                binding.etRespiratoryRate.requestFocus();
            }

//            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("patient_id", String.valueOf(patientId));
                parameters.put("height", fName);
                parameters.put("weight", mName);
                parameters.put("temperature", lName);
                parameters.put("pulse", tPhone);
                parameters.put("respiratory_rate", tPhone2);
                parameters.put("blood_pressure", sDate);
                parameters.put("blood_oxygen_saturation", eAddress);


                if (isNew) {
                    saveCaptureVitals(parameters);
                } else {
                    parameters.put("task_id", String.valueOf(taskId));

//                    editTask(parameters);
                }
            } else {
                if (taskItem == null)
                    taskItem = new TaskItem();

                taskItem.setTaskName(fName);
                taskItem.setTaskDetail(mName);
                taskItem.setPriority(selectedPriority);
//                taskItem.setStartDate(calendarStart.getTimeInMillis());
//                taskItem.setEndDate(calendarEnd.getTimeInMillis());
                taskItem.setAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                taskItem.setAddedByName(isRemembered ? userSessionManager.getUserName() : Safra.userName);
//                if (!cUser.isEmpty())
//                    taskItem.setUserIds(GeneralExtension.toLongArray(cUser, ","));
//                if (!cGroup.isEmpty())
//                    taskItem.setGroupIds(GeneralExtension.toLongArray(cGroup, ","));
                if (isRemembered ? userSessionManager.isAgency() : Safra.isAgency)
                    taskItem.setMasterId(isRemembered ? userSessionManager.getUserId() : Safra.userId);

                if (isNew) {
                    if (currentUsers.contains(isRemembered ? userSessionManager.getUserId() : Safra.userId)
                            || currentGroups.contains(isRemembered ? userSessionManager.getUserRoleId() : Safra.userRoleId)) {
                        taskItem.setTaskStatus("Pending");
                    } else {
                        taskItem.setTaskStatus("-");
                    }
                    long i = dbHandler.addTaskOffline(taskItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new TaskAddedEvent());
                        finish();
                    }
                } else {
                    taskItem.setTaskId(taskId);
                    taskItem.setTaskOnlineId(onlineId);
                    long i = dbHandler.updateTaskOffline(taskItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new TaskAddedEvent());
                        finish();
                    }
                }
            }
        }
    }

    private void saveCaptureVitals(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_CAPTURE_VITAL_SAVE)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddCaptureVitals.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                EventBus.getDefault().post(new TaskAddedEvent());
                                finish();
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
//                        dialogL.dismiss();
                    }
                });
    }

}

//    private void getEditDataFromDB() {
//        taskItem = dbHandler.getTaskDetails(taskId);
////        setTaskDetails();
//
//        currentUsers = GeneralExtension.toLongList(taskItem.getUserIds());
//        currentGroups = GeneralExtension.toLongList(taskItem.getGroupIds());
//
//        getUserListFromDB();
//        getGroupListFromDB();
//    }
//
//    private void getUserListFromDB() {
//        userList.clear();
//
//        UserItem selfItem = new UserItem();
//        selfItem.setUserOnlineId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
//        selfItem.setUserName("Assign To Self");
//        userList.add(selfItem);
//        userList.addAll(dbHandler.getUsers(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        if (currentUsers.size() > 0) {
//            for (UserItem ui : userList) {
//                Log.e(TAG, "getUserListFromDB: " + ui.getUserOnlineId());
//                ui.setSelected(currentUsers.contains(ui.getUserOnlineId()));
//            }
//
//            for (UserItem ui : userList) {
//                if (currentUsers.contains(ui.getUserOnlineId())) {
//                    Chip chip = new Chip(AddCaptureVitals.this);
//                    chip.setText(ui.getUserName());
//
//
//                }
//            }
//
//
//        }
//    }
//
//    private void getGroupListFromDB() {
//        groupList.clear();
//        groupList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));
//
//        if (currentGroups.size() > 0) {
//            for (RoleItem ri : groupList) {
//                ri.setSelected(currentGroups.contains(ri.getRoleOnlineId()));
//            }
//
//
//            for (RoleItem ri : groupList) {
//                if (currentGroups.contains(ri.getRoleOnlineId())) {
//                    Chip chip = new Chip(AddCaptureVitals.this);
//                    chip.setText(ri.getRoleName());
//
//
//                }
//            }
//
//
//        }
//    }