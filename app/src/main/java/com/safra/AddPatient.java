package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.HEALTH_RECORD_PATIENT_LIST_UPDATE;
import static com.safra.utilities.Common.HEALTH_RECORD_PATIENT_REGISTER;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.Common.TASK_SAVE_API;
import static com.safra.utilities.Common.TASK_VIEW_API;
import static com.safra.utilities.Common.USER_LIST_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.chip.Chip;
import com.safra.adapters.GenderListAdapter;
import com.safra.adapters.GroupCustomSpinnerAdapter;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.UserCustomSpinnerAdapter;
import com.safra.databinding.ActivityAddPatientBinding;
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

public class AddPatient extends AppCompatActivity {

    public static final String TAG = "add_patient_fragment";

    private ActivityAddPatientBinding binding;
    private boolean isRemembered;

    private Calendar calendarStart, calendarEnd;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

    private int selectedPriority = 1;
    String spinnerStatusName;
    private boolean isNew;
    private long patientId = -1, onlineId = -1;
    private TaskItem taskItem = null;
    String fName,mName,lName,b_Date,phone1,phone2,address;
    private List<Long> currentUsers = new ArrayList<>();
    private List<Long> currentGroups = new ArrayList<>();
    ArrayList<ShipmentStatus> shipmentStatusList = new ArrayList<>();
    Spinner shipmentStatusSpinner;
    int undefinedValue;

    private boolean isUserDataReceived = false, isGroupDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        binding.etBirthDate.setFocusableInTouchMode(false);

        CheckBox checkboxUnidentified = findViewById(R.id.checkbox_unidentified);
        ConstraintLayout clPatientFName = findViewById(R.id.clPatientFName);
        ConstraintLayout clPatientMName = findViewById(R.id.clPatientMName);
        ConstraintLayout clPatientLName = findViewById(R.id.clPatientLName);

        checkboxUnidentified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                undefinedValue = isChecked ? 1 : 0;

                if (isChecked) {
                    // Checkbox is selected, hide the constraint layouts
                    clPatientFName.setVisibility(View.GONE);
                    clPatientMName.setVisibility(View.GONE);
                    clPatientLName.setVisibility(View.GONE);
                } else {
                    // Checkbox is not selected, unhide the constraint layouts
                    clPatientFName.setVisibility(View.VISIBLE);
                    clPatientMName.setVisibility(View.VISIBLE);
                    clPatientLName.setVisibility(View.VISIBLE);
                }
            }
        });




        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        if (getIntent() != null) {
            binding.tvAddProjectHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                System.out.println("NEW");
                if (ConnectivityReceiver.isConnected()) {
//                    getUserListFromDB();
//                    getGroupListFromDB();

                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {

                System.out.println("EDIT");
                patientId = getIntent().getIntExtra("patient_Id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                fName = getIntent().getStringExtra("f_name");
                mName = getIntent().getStringExtra("m_name");
                lName = getIntent().getStringExtra("l_name");
                b_Date = getIntent().getStringExtra("b_date");
                phone1 = getIntent().getStringExtra("phone1");
                phone2 = getIntent().getStringExtra("phone2");
                address = getIntent().getStringExtra("address");
                binding.etPatientFName.setText(fName);
                binding.etPatientMName.setText(mName);
                binding.etPatientLName.setText(lName);
                binding.etBirthDate.setText(b_Date);
                binding.etPhoneNo.setText(phone1);
                binding.etPhoneNo1.setText(phone2);
                binding.etAddress.setText(address);
                Log.e(TAG, "onCreate: " + patientId);
//                if (ConnectivityReceiver.isConnected())
//                    getEditData();
//                getEditDataFromDB();
//                else
//                    getEditDataFromDB();
//            }
            }
        }

        shipmentStatusList.add(new ShipmentStatus("M"));
        shipmentStatusList.add(new ShipmentStatus("F"));
        shipmentStatusList.add(new ShipmentStatus("Other"));


        shipmentStatusSpinner = findViewById(R.id.spnGender);
        GenderListAdapter adapter = new GenderListAdapter(this, shipmentStatusList);
        shipmentStatusSpinner.setAdapter(adapter);

        shipmentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ShipmentStatus selectedStatus = (ShipmentStatus) parent.getItemAtPosition(position);
                spinnerStatusName = selectedStatus.getStatusName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
//        PrioritySpinnerAdapter adapterP = new PrioritySpinnerAdapter(this, getPriorityList());
//        binding.spnGender.setAdapter(adapterP);
//
//        binding.spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedPriority = ((PriorityItem) parent.getSelectedItem()).getPriorityStatus();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        binding.etBirthDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendarStart.set(year, month, dayOfMonth);
                binding.etBirthDate.setText(sdfToShow.format(new Date(calendarStart.getTimeInMillis())));
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
        binding.tvPatientFNameTitle.setText(LanguageExtension.setText("first_name", getString(R.string.first_name)));
        binding.tvPatientMNameTitle.setText(LanguageExtension.setText("middle_name", getString(R.string.middle_name)));
        binding.tvPatientLNameTitle.setText(LanguageExtension.setText("last_name", getString(R.string.last_name)));
        binding.tvPhoneNoTitle.setText(LanguageExtension.setText("phone_number", getString(R.string.phone_number)));
        binding.tvPhoneNo1Title.setText(LanguageExtension.setText("phone_number_2", getString(R.string.phone_number_2)));
        binding.tvAddressTitle.setText(LanguageExtension.setText("address", getString(R.string.address)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void getEditDataFromDB() {
        taskItem = dbHandler.getTaskDetails(patientId);
        setTaskDetails();

        currentUsers = GeneralExtension.toLongList(taskItem.getUserIds());
        currentGroups = GeneralExtension.toLongList(taskItem.getGroupIds());

//        getUserListFromDB();
//        getGroupListFromDB();
    }





    private void setTaskDetails() {
        patientId = taskItem.getTaskId();
        onlineId = taskItem.getTaskOnlineId();
        binding.etPatientFName.setText(taskItem.getTaskName());
        binding.etPatientMName.setText(taskItem.getTaskName());
        binding.etPatientLName.setText(taskItem.getTaskName());
        if (taskItem.getTaskDetail() != null)
            binding.etPhoneNo.setText(taskItem.getTaskDetail());
        if (taskItem.getTaskDetail() != null)
            binding.etPhoneNo1.setText(taskItem.getTaskDetail());
        if (taskItem.getTaskDetail() != null)
            binding.etAddress.setText(taskItem.getTaskDetail());
        if (taskItem.getStartDate() != 0) {
            calendarStart.setTimeInMillis(taskItem.getStartDate());
            binding.etBirthDate.setText(sdfToShow.format(new Date(taskItem.getStartDate())));
        }
//        if (taskItem.getEndDate() != 0) {
//            calendarEnd.setTimeInMillis(taskItem.getEndDate());
//            binding.etEndDate.setText(sdfToShow.format(new Date(taskItem.getEndDate())));
//        }
    }

    private void validateInputs() {
        String fName = binding.etPatientFName.getText() != null ? binding.etPatientFName.getText().toString() : "";
        String mName = binding.etPatientMName.getText() != null ? binding.etPatientMName.getText().toString() : "";
        String lName = binding.etPatientLName.getText() != null ? binding.etPatientLName.getText().toString() : "";
        String tPhone = binding.etPhoneNo.getText() != null ? binding.etPhoneNo.getText().toString() : "";
        String tPhone2 = binding.etPhoneNo1.getText() != null ? binding.etPhoneNo1.getText().toString() : "";
        String sDate = binding.etBirthDate.getText() != null ? binding.etBirthDate.getText().toString() : "";
        String eAddress = binding.etAddress.getText() != null ? binding.etAddress.getText().toString() : "";



        if (sDate.isEmpty() || tPhone.isEmpty()) {

            if (sDate.isEmpty()) {
                binding.etBirthDate.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etBirthDate.requestFocus();
            }
            if (tPhone.isEmpty()) {
                binding.etPhoneNo.setError(LanguageExtension.setText("enter_phone_number", getString(R.string.enter_phone_number)));
                binding.etPhoneNo.requestFocus();
            }
//            if (tPhone2.isEmpty()) {
//                binding.etPhoneNo1.setError(LanguageExtension.setText("enter_phone_number", getString(R.string.enter_phone_number)));
//                binding.etPhoneNo1.requestFocus();
//            }
//            if (eAddress.isEmpty()) {
//                binding.etAddress.setError(LanguageExtension.setText("enter_end_date", getString(R.string.addre)));
//                binding.etAddress.requestFocus();
//            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();

                parameters.put("address", eAddress);
                parameters.put("mobile", tPhone);
                parameters.put("phone", tPhone2);
                parameters.put("gender", String.valueOf(spinnerStatusName));
                parameters.put("birthdate", sdfForServer.format(new Date(calendarStart.getTimeInMillis())));
                parameters.put("undefined", String.valueOf(undefinedValue));

                if(undefinedValue == 1){
                    System.out.println("PASS NULL");

                }else if(undefinedValue == 0){
                    System.out.println("PASS VALUE");
                    parameters.put("first_name", fName);
                    parameters.put("middle_name", mName);
                    parameters.put("last_name", lName);
                }

                if (isNew) {
                    savePatient(parameters);
                } else {

                    parameters.put("patientId", String.valueOf(patientId));

                    editPatient(parameters);
                }
            } else {
                if (taskItem == null)
                    taskItem = new TaskItem();

                taskItem.setTaskName(fName);
                taskItem.setTaskDetail(mName);
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
                    taskItem.setTaskId(patientId);
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



//    private List<PriorityItem> getPriorityList() {
//        List<PriorityItem> priorityList = new ArrayList<>();
//        priorityList.add(new PriorityItem(1, LanguageExtension.setText("male", getString(R.string.male))));
//        priorityList.add(new PriorityItem(2, LanguageExtension.setText("female", getString(R.string.female))));
//        priorityList.add(new PriorityItem(3, LanguageExtension.setText("other", getString(R.string.other))));
//
//        return priorityList;
//    }

    private void savePatient(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_PATIENT_REGISTER)
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
                            Toast.makeText(AddPatient.this, message, Toast.LENGTH_SHORT).show();
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

    private void editPatient(HashMap<String, String> parameters) {
        System.out.println("EDIT API CALL:- "+parameters);
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_PATIENT_LIST_UPDATE)
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
                            Toast.makeText(AddPatient.this, message, Toast.LENGTH_SHORT).show();
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
//                    Chip chip = new Chip(AddPatient.this);
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
//                    Chip chip = new Chip(AddPatient.this);
//                    chip.setText(ri.getRoleName());
//
//
//                }
//            }
//
//
//        }
//    }