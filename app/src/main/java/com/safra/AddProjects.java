package com.safra;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PROJECT;
import static com.safra.utilities.Common.PROJECT_EDIT;
import static com.safra.utilities.Common.PROJECT_STORE;
import static com.safra.utilities.Common.PROJECT_UPDATE;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.UserPermissions.USER_DELETE;
import static com.safra.utilities.UserPermissions.USER_STATUS;
import static com.safra.utilities.UserPermissions.USER_UPDATE;
import static com.safra.utilities.UserPermissions.USER_VIEW;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.adapters.PrioritySpinnerAdapter;
import com.safra.adapters.ProjectRecyclerAdapter;
import com.safra.databinding.ActivityAddProjectsBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.models.PriorityItem;
import com.safra.models.ProjectDataModel;
import com.safra.models.ProjectListResponseModel;
import com.safra.models.TaskItem;
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

public class AddProjects extends AppCompatActivity {
    public static final String TAG = "add_project_activity";

    private ActivityAddProjectsBinding binding;

    private boolean isRemembered;

    private Calendar calendarStart, calendarEnd;
    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdfForServer = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

    private String selectedPriority = "1";

    private boolean isNew;
    private long projectId = -1, onlineId = -1;
//    private TaskItem taskItem = null;
    private ProjectDataModel projectDataModel = null;
    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isLoadedOnline = false;
    private final List<ProjectListResponseModel> userList = new ArrayList<>();
    private ProjectRecyclerAdapter adapter;
    private PopupWindow popupWindow;



    private boolean isUserDataReceived = false, isGroupDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        binding.etStartDate.setFocusableInTouchMode(false);
        binding.etEndDate.setFocusableInTouchMode(false);

        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        if (getIntent() != null) {
            binding.tvAddProjectHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {
//                    getUserListFromDB();
//                    getGroupListFromDB();
//                    getUsers(PAGE_START);
//                    getGroups(PAGE_START);
                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {
                projectId = getIntent().getLongExtra("project_id", -1);
//                onlineId = getIntent().getLongExtra("online_id", -1);
                Log.e(TAG, "onCreate: " + projectId);
                if (ConnectivityReceiver.isConnected()) {
                    getEditProject();

                }
//                    getEditData();
//                getEditDataFromDB();
//                else
//                    getEditDataFromDB();
            }
        }

        PrioritySpinnerAdapter adapterP = new PrioritySpinnerAdapter(this, getPriorityList());
        binding.spnPriority.setAdapter(adapterP);

        binding.spnPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPriority = ((PriorityItem) parent.getSelectedItem()).getPriorityName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.etStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendarStart.set(year, month, dayOfMonth);
                binding.etStartDate.setText(sdfToShow.format(new Date(calendarStart.getTimeInMillis())));
            }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        binding.etEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendarEnd.set(year, month, dayOfMonth);
                binding.etEndDate.setText(sdfToShow.format(new Date(calendarEnd.getTimeInMillis())));
            }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
            datePickerDialog.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            validateInputs();
//            Log.e(TAG, "onCreate: module_ids -> " + hashMap.get("module_ids"));
//            Log.e(TAG, "onCreate: permission_ids -> " + hashMap.get("permission_ids"));
        });

    }

    private void setText() {
        binding.tvProjectNameTitle.setText(LanguageExtension.setText("enter_project_name", getString(R.string.enter_project_name)));
        binding.tvProjectFinancierTitle.setText(LanguageExtension.setText("financier", getString(R.string.financier)));
        binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date_mandatory", getString(R.string.start_date_mandatory)));
        binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date_mandatory", getString(R.string.end_date_mandatory)));
        binding.tvCurrencyTitle.setText(LanguageExtension.setText("currency", getString(R.string.currency)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

//    private void getEditDataFromDB() {
//        taskItem = dbHandler.getTaskDetails(taskId);
//        setTaskDetails();
//
//        currentUsers = GeneralExtension.toLongList(taskItem.getUserIds());
//        currentGroups = GeneralExtension.toLongList(taskItem.getGroupIds());
//
////        getUserListFromDB();
////        getGroupListFromDB();
//    }

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
//                    Chip chip = new Chip(AddProjects.this);
//                    chip.setText(ui.getUserName());
//
//
//                }
//            }
//
//
//        }
//    }

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
//                    Chip chip = new Chip(AddProjects.this);
//                    chip.setText(ri.getRoleName());
//
//
//                }
//            }
//
//
//        }
//    }

    private void getEditProject() {
        String ID = String.valueOf(projectId);
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PROJECT_EDIT + ID)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("task_id", String.valueOf(projectId))
//                .setTag("task-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE:-"+response);
                        LoadingDialogExtension.hideLoading();
                        try {
//                            int success = response.getInt("success");
                            String message = response.getString("message");
//                            if (success == 1) {
                                JSONObject taskData = response.getJSONObject("data");
                                projectDataModel = new ProjectDataModel();
//                                projectDataModel.setTaskOnlineId(taskData.getLong("id"));
                                projectDataModel.setId(taskData.getInt("id"));
                                projectDataModel.setName(taskData.getString("name"));


                                if (!taskData.isNull("financier"))
                                    projectDataModel.setFinancier(taskData.getString("financier"));



                                if (!taskData.isNull("start_date"))
                                    projectDataModel.setStartDate(taskData.getString("start_date"));

                                if (!taskData.isNull("end_date"))
                                    projectDataModel.setEndDate(taskData.getString("end_date"));
//

                            if (!taskData.isNull("currency"))
                                projectDataModel.setCurrency(taskData.getString("currency"));
//                                taskItem.setAddedBy(taskData.getLong("added_by"));


//                                if (!taskData.isNull("task_user_ids")) {
//                                    Long[] users = GeneralExtension.toLongArray(taskData.getString("task_user_ids"), ",");
//                                    currentUsers = GeneralExtension.toLongList(users);
//                                }

                                setTaskDetails();



//                            } else {
//                                Toast.makeText(AddProjects.this, message, Toast.LENGTH_SHORT).show();
//                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

//                        dialogL.dismiss();
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


    private void setTaskDetails() {
        projectId = projectDataModel.getId();
//        onlineId = projectDataModel.getTaskOnlineId();
        binding.etProjectName.setText(projectDataModel.getName());
        if (projectDataModel.getFinancier() != null)
            binding.etProjectFinancier.setText(projectDataModel.getFinancier());

        if (projectDataModel.getStartDate() != null) {
            binding.etStartDate.setText(projectDataModel.getStartDate());
        }
        if (projectDataModel.getEndDate() != null) {
            binding.etEndDate.setText(projectDataModel.getEndDate());
        }



    }

    private void validateInputs() {
        String tName = binding.etProjectName.getText() != null ? binding.etProjectName.getText().toString() : "";
        String tDetail = binding.etProjectFinancier.getText() != null ? binding.etProjectFinancier.getText().toString() : "";
        String sDate = binding.etStartDate.getText() != null ? binding.etStartDate.getText().toString() : "";
        String eDate = binding.etEndDate.getText() != null ? binding.etEndDate.getText().toString() : "";


        if (tName.isEmpty() || sDate.isEmpty() || eDate.isEmpty()) {
            if (eDate.isEmpty()) {
                binding.etEndDate.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etEndDate.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etStartDate.setError(LanguageExtension.setText("enter_start_date", getString(R.string.enter_start_date)));
                binding.etStartDate.requestFocus();
            }
            if (tName.isEmpty()) {
                binding.etProjectName.setError(LanguageExtension.setText("enter_task_name", getString(R.string.enter_task_name)));
                binding.etProjectName.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("name", tName);
                if (!tDetail.isEmpty())
                    parameters.put("financier", tDetail);
                parameters.put("currency", String.valueOf(selectedPriority));

                parameters.put("start_date", sdfForServer.format(new Date(calendarStart.getTimeInMillis())));
                parameters.put("end_date", sdfForServer.format(new Date(calendarEnd.getTimeInMillis())));


                if (isNew) {
                    saveProject(parameters);
                } else {

                    parameters.put("project_id", String.valueOf(projectId));

                    editProject(parameters);
                }
            } else {
//                if (taskItem == null)
//                    taskItem = new TaskItem();
//
//                taskItem.setTaskName(tName);
//                taskItem.setTaskDetail(tDetail);
//                taskItem.setPriority(selectedPriority);
//                taskItem.setStartDate(calendarStart.getTimeInMillis());
//                taskItem.setEndDate(calendarEnd.getTimeInMillis());
//                taskItem.setAddedBy(isRemembered ? userSessionManager.getUserId() : Safra.userId);
//                taskItem.setAddedByName(isRemembered ? userSessionManager.getUserName() : Safra.userName);
//
//                if (isRemembered ? userSessionManager.isAgency() : Safra.isAgency)
//                    taskItem.setMasterId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
//
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

    private List<PriorityItem> getPriorityList() {
        List<PriorityItem> priorityList = new ArrayList<>();
        priorityList.add(new PriorityItem(1, LanguageExtension.setText("metical_mzn", getString(R.string.metical_mzn))));
        priorityList.add(new PriorityItem(2, LanguageExtension.setText("dolar_usd", getString(R.string.dolar_usd))));
        priorityList.add(new PriorityItem(3, LanguageExtension.setText("euro_eur", getString(R.string.euro_eur))));
        priorityList.add(new PriorityItem(4, LanguageExtension.setText("rand_zar", getString(R.string.rand_zar))));

        return priorityList;
    }

    private void saveProject(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PROJECT_STORE)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("save-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "response: " + response);
                        LoadingDialogExtension.hideLoading();
                        try {
//                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddProjects.this, message, Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new TaskAddedEvent());
                            finish();

//                            if (success == 1) {
//                                EventBus.getDefault().post(new TaskAddedEvent());
//                                finish();
//                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
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

    private void editProject(HashMap<String, String> parameters) {
        String ID = String.valueOf(projectId);

        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + PROJECT_UPDATE + ID)
                .addBodyParameter(parameters)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .setTag("edit-task-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
//                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(AddProjects.this, message, Toast.LENGTH_SHORT).show();
//                            if (success == 1) {
                                EventBus.getDefault().post(new TaskAddedEvent());
                                finish();
//                            }
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