package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.HEALTH_RECORD_APPOINTMENT_SAVE;
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
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.adapters.GroupCustomSpinnerAdapter;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.ActivityAddAppointmentBinding;
import com.safra.databinding.ActivityAddProjectsBinding;
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

public class AddAppointment extends AppCompatActivity {
    public static final String TAG = "add_appoint_activity";
    private ActivityAddAppointmentBinding binding;


    private boolean isRemembered;

    private Calendar calendarStart, calendarEnd;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
    private int mYear, mMonth, mDay, mHour, mMinute;

    private int selectedPriority = 1;

    private boolean isNew;
    private long taskId = -1, onlineId = -1;
    private TaskItem taskItem = null;
    private long patientId = -1;
    private List<Long> currentUsers = new ArrayList<>();
    private List<Long> currentGroups = new ArrayList<>();

    private boolean isUserDataReceived = false, isGroupDataReceived = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();
        patientId = getIntent().getLongExtra("patientId", -1);
        setText();

        binding.etStartDate.setFocusableInTouchMode(false);
        binding.etTime.setFocusableInTouchMode(false);


        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        if (getIntent() != null) {
            binding.tvAddAppointmentHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
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



        binding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddAppointment.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                binding.etTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        binding.etStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendarStart.set(year, month, dayOfMonth);
                binding.etStartDate.setText(sdfToShow.format(new Date(calendarStart.getTimeInMillis())));
            }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


        binding.btnSave.setOnClickListener(v -> {
            validateInputs();
//            Log.e(TAG, "onCreate: module_ids -> " + hashMap.get("module_ids"));
//            Log.e(TAG, "onCreate: permission_ids -> " + hashMap.get("permission_ids"));
        });
    }

    private void setText() {
        binding.tvTimeTitle.setText(LanguageExtension.setText("time", getString(R.string.time)));
        binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date_mandatory", getString(R.string.date)));
        binding.tvNoteTitle.setText(LanguageExtension.setText("note", getString(R.string.note)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void getEditDataFromDB() {
        taskItem = dbHandler.getTaskDetails(taskId);
        setTaskDetails();

        currentUsers = GeneralExtension.toLongList(taskItem.getUserIds());
        currentGroups = GeneralExtension.toLongList(taskItem.getGroupIds());

//        getUserListFromDB();
//        getGroupListFromDB();
    }




    private void setTaskDetails() {
        taskId = taskItem.getTaskId();
        onlineId = taskItem.getTaskOnlineId();
        if (taskItem.getTaskDetail() != null)
            binding.etNote.setText(taskItem.getTaskDetail());
        if (taskItem.getTaskDetail() != null)
            binding.etTime.setText(taskItem.getTaskDetail());
        if (taskItem.getStartDate() != 0) {
            calendarStart.setTimeInMillis(taskItem.getStartDate());
            binding.etStartDate.setText(sdfToShow.format(new Date(taskItem.getStartDate())));
        }

    }

    private void validateInputs() {
        String tName = binding.etTime.getText() != null ? binding.etTime.getText().toString() : "";
        String sDate = binding.etStartDate.getText() != null ? binding.etStartDate.getText().toString() : "";
        String eDate = binding.etNote.getText() != null ? binding.etNote.getText().toString() : "";


        if (tName.isEmpty() || sDate.isEmpty() || eDate.isEmpty()) {
            if (eDate.isEmpty()) {
                binding.etTime.setError("Enter Time");
                binding.etTime.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etStartDate.setError("Enter Start Date");
                binding.etStartDate.requestFocus();
            }
            if (tName.isEmpty()) {
                binding.etNote.setError("Enter Note");
                binding.etNote.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("patient_id", String.valueOf(patientId));
                parameters.put("start_time",tName);

                parameters.put("note", eDate);
                parameters.put("start_date", sdfForServer.format(new Date(calendarStart.getTimeInMillis())));

                if (isNew) {
                    saveAppointment(parameters);
                } else {
                    parameters.put("task_id", String.valueOf(taskId));

                    editTask(parameters);
                }
            } else {
                if (taskItem == null)
                    taskItem = new TaskItem();

                taskItem.setTaskName(tName);
                taskItem.setTaskDetail(sDate);
                taskItem.setPriority(selectedPriority);
                taskItem.setStartDate(calendarStart.getTimeInMillis());
                taskItem.setEndDate(calendarEnd.getTimeInMillis());
                taskItem.setAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                taskItem.setAddedByName(isRemembered ? userSessionManager.getUserName() : Safra.userName);

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

    private void saveAppointment(HashMap<String, String> parameters) {
        System.out.println("parameters" +parameters);
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_APPOINTMENT_SAVE)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("save-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddAppointment.this, message, Toast.LENGTH_SHORT).show();
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

    private void editTask(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + TASK_SAVE_API)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("edit-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddAppointment.this, message, Toast.LENGTH_SHORT).show();
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
//                    Chip chip = new Chip(AddAppointment.this);
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
//                    Chip chip = new Chip(AddAppointment.this);
//                    chip.setText(ri.getRoleName());
//
//
//                }
//            }
//
//
//        }
//    }
//private void getEditData() {
//    LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
////        LoadingDialog dialogL = new LoadingDialog();
////        dialogL.setCancelable(false);
////        Bundle bundle = new Bundle();
////        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
////        dialogL.setArguments(bundle);
////        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);
//
//    AndroidNetworking
//            .post(BASE_URL + TASK_VIEW_API)
//            .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//            .addBodyParameter("task_id", String.valueOf(onlineId))
//            .setTag("task-detail-api")
//            .build()
//            .getAsJSONObject(new JSONObjectRequestListener() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    LoadingDialogExtension.hideLoading();
//                    try {
//                        int success = response.getInt("success");
//                        String message = response.getString("message");
//                        if (success == 1) {
//                            JSONObject taskData = response.getJSONObject("data").getJSONObject("task_data");
//                            taskItem = new TaskItem();
//                            taskItem.setTaskOnlineId(taskData.getLong("task_id"));
//                            taskItem.setTaskName(taskData.getString("task_title"));
//
//                            if (!taskData.isNull("task_details"))
//                                taskItem.setTaskDetail(taskData.getString("task_details"));
//
//                            if (!taskData.isNull("task_priority"))
//                                taskItem.setPriority(taskData.getInt("task_priority"));
//
//                            if (!taskData.isNull("task_start_date"))
//                                taskItem.setStartDate(taskData.getLong("task_start_date") * 1000);
//
//                            if (!taskData.isNull("task_end_date"))
//                                taskItem.setEndDate(taskData.getLong("task_end_date") * 1000);
//
//                            taskItem.setAddedBy(taskData.getLong("added_by"));
//
//                            if (!taskData.isNull("task_group_ids")) {
//                                Long[] groups = GeneralExtension.toLongArray(taskData.getString("task_group_ids"), ",");
//                                currentGroups = GeneralExtension.toLongList(groups);
//                            }
//
//                            if (!taskData.isNull("task_user_ids")) {
//                                Long[] users = GeneralExtension.toLongArray(taskData.getString("task_user_ids"), ",");
//                                currentUsers = GeneralExtension.toLongList(users);
//                            }
//
//                            setTaskDetails();
//
//                            getUsers(PAGE_START);
//                            getGroups(PAGE_START);
//
//                        } else {
//                            Toast.makeText(AddAppointment.this, message, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                    }
//
////                        dialogL.dismiss();
//                }
//
//                @Override
//                public void onError(ANError anError) {
//                    Log.e(TAG, "onError: " + anError.getErrorCode());
//                    Log.e(TAG, "onError: " + anError.getErrorDetail());
//                    Log.e(TAG, "onError: " + anError.getErrorBody());
//                    LoadingDialogExtension.hideLoading();
////                        dialogL.dismiss();
//                }
//            });
//}
