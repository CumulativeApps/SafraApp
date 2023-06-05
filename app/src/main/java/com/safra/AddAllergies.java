package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.HEALTH_RECORD_ALLERGIES_ADD;
import static com.safra.utilities.Common.HEALTH_RECORD_ALLERGIES_UPDATE;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.adapters.SeverityListAdapter;
import com.safra.adapters.GroupCustomSpinnerAdapter;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.ActivityAddAllergiesBinding;
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

public class AddAllergies extends AppCompatActivity {
    public static final String TAG = "add_allergies_fragment";

    private ActivityAddAllergiesBinding binding;

    private boolean isRemembered;
    String spinnerStatusName;
    private Calendar calendarStart, calendarEnd;
    ArrayList<AddAllergies.ShipmentStatus> shipmentStatusList = new ArrayList<>();
    Spinner shipmentStatusSpinner;
    private int selectedPriority = 1;
    private long patientId = -1;

    private boolean isNew;
    private long taskId = -1, onlineId = -1;

    String allergen,reaction,comment,severity;
    int allergies_id;
    private TaskItem taskItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAllergiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBacck.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();
        patientId = getIntent().getLongExtra("allergies_patient_id",-1);

        setText();
        shipmentStatusList.add(new AddAllergies.ShipmentStatus("Mild"));
        shipmentStatusList.add(new AddAllergies.ShipmentStatus("Moderate"));
        shipmentStatusList.add(new AddAllergies.ShipmentStatus("High"));


        shipmentStatusSpinner = findViewById(R.id.spnSeverity);
        SeverityListAdapter adapter = new SeverityListAdapter(this, shipmentStatusList);
        shipmentStatusSpinner.setAdapter(adapter);

        shipmentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddAllergies.ShipmentStatus selectedStatus = (AddAllergies.ShipmentStatus) parent.getItemAtPosition(position);
                spinnerStatusName = selectedStatus.getStatusName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        if (getIntent() != null) {
            binding.tvAddAllergiesHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {


                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {
                taskId = getIntent().getLongExtra("task_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                allergies_id = getIntent().getIntExtra("allergy_id", -1);
                allergen = getIntent().getStringExtra("allergen");
                reaction = getIntent().getStringExtra("reaction");
                comment = getIntent().getStringExtra("comment");
                severity = getIntent().getStringExtra("severity");

                binding.etAllergen.setText(allergen);
                binding.etReaction.setText(reaction);
                binding.etComment.setText(comment);
                spinnerStatusName = severity;


                Log.e(TAG, "onCreate: " + taskId);
//                if (ConnectivityReceiver.isConnected())
////                getEditDataFromDB();
//                else
//                    getEditDataFromDB();
            }
        }

        binding.btnSave.setOnClickListener(v -> {
            validateInputs();
//            Log.e(TAG, "onCreate: module_ids -> " + hashMap.get("module_ids"));
//            Log.e(TAG, "onCreate: permission_ids -> " + hashMap.get("permission_ids"));
        });
    }

    private void setText() {
        binding.tvAllergenTitle.setText(LanguageExtension.setText("allergen", getString(R.string.allergen)));
        binding.tvReactionTitle.setText(LanguageExtension.setText("reaction", getString(R.string.reaction)));
        binding.tvSeverityTitle.setText(LanguageExtension.setText("severity", getString(R.string.severity)));
        binding.tvCommentTitle.setText(LanguageExtension.setText("comment", getString(R.string.comment)));

        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void getEditDataFromDB() {
        taskItem = dbHandler.getTaskDetails(taskId);
        setTaskDetails();


//        getUserListFromDB();
//        getGroupListFromDB();
    }



    private void setTaskDetails() {
        taskId = taskItem.getTaskId();
        onlineId = taskItem.getTaskOnlineId();
        binding.etAllergen.setText(taskItem.getTaskName());
        if (taskItem.getTaskDetail() != null)
            binding.etReaction.setText(taskItem.getTaskDetail());
        if (taskItem.getTaskDetail() != null)
            binding.etComment.setText(taskItem.getTaskDetail());
    }

    private void validateInputs() {
        String tName = binding.etAllergen.getText() != null ? binding.etAllergen.getText().toString() : "";
        String tDetail = binding.etReaction.getText() != null ? binding.etReaction.getText().toString() : "";
        String sDate = binding.etComment.getText() != null ? binding.etComment.getText().toString() : "";


        if (tName.isEmpty() || tDetail.isEmpty() || sDate.isEmpty()) {
            if (tName.isEmpty()) {
                binding.etAllergen.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etAllergen.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etReaction.setError(LanguageExtension.setText("enter_start_date", getString(R.string.enter_start_date)));
                binding.etReaction.requestFocus();
            }
            if (tDetail.isEmpty()) {
                binding.etComment.setError(LanguageExtension.setText("enter_task_name", getString(R.string.enter_task_name)));
                binding.etComment.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("patient_id", String.valueOf(patientId));
                parameters.put("allergen", tName);
                parameters.put("reaction", tDetail);
                parameters.put("severity", spinnerStatusName);
                parameters.put("comment", sDate);



                if (isNew) {
                    saveAllergies(parameters);
                } else {
                    parameters.put("id", String.valueOf(allergies_id));

                    editAllergies(parameters);
                }
            } else {
                if (taskItem == null)
                    taskItem = new TaskItem();

                taskItem.setTaskName(tName);
                taskItem.setTaskDetail(tDetail);
                taskItem.setPriority(selectedPriority);
                taskItem.setStartDate(calendarStart.getTimeInMillis());
                taskItem.setEndDate(calendarEnd.getTimeInMillis());
                taskItem.setAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                taskItem.setAddedByName(isRemembered ? userSessionManager.getUserName() : Safra.userName);

                if (isRemembered ? userSessionManager.isAgency() : Safra.isAgency)
                    taskItem.setMasterId(isRemembered ? userSessionManager.getUserId() : Safra.userId);

//                if (isNew) {
//                    if (currentUsers.contains(isRemembered ? userSessionManager.getUserId() : Safra.userId)
//                            || currentGroups.contains(isRemembered ? userSessionManager.getUserRoleId() : Safra.userRoleId)) {
//                        taskItem.setTaskStatus("Pending");
//                    } else {
//                        taskItem.setTaskStatus("-");
//                    }
//                    long i = dbHandler.addTaskOffline(taskItem);
//                    if (i > 0) {
//                        EventBus.getDefault().post(new TaskAddedEvent());
//                        finish();
//                    }
//                } else {
//                    taskItem.setTaskId(taskId);
//                    taskItem.setTaskOnlineId(onlineId);
//                    long i = dbHandler.updateTaskOffline(taskItem);
//                    if (i > 0) {
//                        EventBus.getDefault().post(new TaskAddedEvent());
//                        finish();
//                    }
//                }
            }
        }
    }

    private void saveAllergies(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ALLERGIES_ADD)
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
                            Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
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

    private void editAllergies(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ALLERGIES_UPDATE)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("edit-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddAllergies.this, message, Toast.LENGTH_SHORT).show();
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
    public class ShipmentStatus {
        private String statusName;

        public ShipmentStatus(String statusName) {
            this.statusName = statusName;
        }

        public String getStatusName() {
            return statusName;
        }
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
//                    Chip chip = new Chip(AddAllergies.this);
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
//                    Chip chip = new Chip(AddAllergies.this);
//                    chip.setText(ri.getRoleName());
//
//
//                }
//            }
//
//
//        }
//    }
