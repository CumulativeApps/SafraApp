package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_ALLERGIES_UPDATE;
import static com.safra.utilities.Common.HEALTH_RECORD_AVALIABLE_MEDICINES_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_MEDICATION_ADD;
import static com.safra.utilities.Common.HEALTH_RECORD_MEDICATION_UPDATE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.databinding.ActivityAddMedicationBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.GetAvaliableMedicineList;
import com.safra.models.TaskItem;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddMedication extends AppCompatActivity {
    public static final String TAG = "add_medication_fragment";

    private ActivityAddMedicationBinding binding;

    private boolean isRemembered;
    String spinnerStatusName;

    private ArrayList<GetAvaliableMedicineList.Data.Medicine> medicines;

    private int selectedPriority = 1;
    private long patientId = -1;
    private final int pPosition = -1;
    int selectedMedicineId = -1;
    private boolean isNew;
    private int quantity;

    private long taskId = -1, onlineId = -1;

    String medName, instruction, note;
    int allergies_id;
    private TaskItem taskItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar1);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBacck.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();
        patientId = getIntent().getLongExtra("allergies_patient_id", -1);

        setText();
        getMedicineList(pPosition);


        if (getIntent() != null) {
            binding.tvAddMedicationHeading.setText(LanguageExtension.setText("add_medication", getString(R.string.add_medication)));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {
                if (ConnectivityReceiver.isConnected()) {


                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {
                binding.tvAddMedicationHeading.setText(LanguageExtension.setText("edit_medication", getString(R.string.edit_medication)));
                taskId = getIntent().getLongExtra("task_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
                allergies_id = getIntent().getIntExtra("allergy_id", -1);
                medName = getIntent().getStringExtra("medName");
                quantity = getIntent().getIntExtra("quantity", -1);
                instruction = getIntent().getStringExtra("instruction");
                note = getIntent().getStringExtra("note");

                binding.etQuantity.setText(String.valueOf(quantity));
                binding.etInstructions.setText(instruction);
                binding.etNote.setText(note);
//                spinnerStatusName = severity;


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
        binding.tvMedicineTitle.setText(LanguageExtension.setText("medicine", getString(R.string.medicine)));
        binding.tvQuantityTitle.setText(LanguageExtension.setText("quantity", getString(R.string.quantity)));
        binding.tvInstructionsTitle.setText(LanguageExtension.setText("instructions", getString(R.string.instructions)));
        binding.tvNoteTitle.setText(LanguageExtension.setText("note", getString(R.string.note)));

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
//        binding.etAllergen.setText(taskItem.getTaskName());
//        if (taskItem.getTaskDetail() != null)
//            binding.etReaction.setText(taskItem.getTaskDetail());
//        if (taskItem.getTaskDetail() != null)
//            binding.etComment.setText(taskItem.getTaskDetail());
    }

    private void getMedicineList(int pPosition) {
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_AVALIABLE_MEDICINES_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray medicinesArray = data.getJSONArray("medicines");

                                if (medicinesArray.length() > 0) {
                                    // Create an ArrayList to store the medicine names
                                    ArrayList<String> medicineNames = new ArrayList<>();
                                    ArrayList<Integer> medicineIds = new ArrayList<>(); // Added to store medicine IDs

                                    // Iterate through the medicines array
                                    for (int i = 0; i < medicinesArray.length(); i++) {
                                        JSONObject medicineObject = medicinesArray.getJSONObject(i);

                                        // Get the name from the medicine object
                                        String medicineName = medicineObject.getString("name");
                                        // Get the ID from the medicine object
                                        int medicineId = medicineObject.getInt("id");

                                        // Add the medicine name and ID to the respective lists
                                        medicineNames.add(medicineName);
                                        medicineIds.add(medicineId);
                                    }

                                    Spinner spinner = findViewById(R.id.spnMedicine); // Replace "R.id.spnMedicine" with your actual spinner ID
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMedication.this, R.layout.spinner_form_type, medicineNames);
                                    adapter.setDropDownViewResource(R.layout.spinner_form_type);
                                    spinner.setAdapter(adapter);

                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            // Get the selected medicine ID
                                            selectedMedicineId = medicineIds.get(position);

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            // Handle the case when no item is selected (optional)
                                        }
                                    });
                                    String medName = getIntent().getStringExtra("medName");

                                    int index = medicineNames.indexOf(medName);

                                    if (index != -1) {
                                        spinner.setSelection(index);
                                    }

                                    // Use the medicine names as required
                                    // For example, you can log the names
                                    for (String name : medicineNames) {
                                        Log.d(TAG, "Medicine Name: " + name);
                                    }
                                }
                            } else {
                                Toast.makeText(AddMedication.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void validateInputs() {
        String tName = binding.etQuantity.getText() != null ? binding.etQuantity.getText().toString() : "";
        String tDetail = binding.etInstructions.getText() != null ? binding.etInstructions.getText().toString() : "";
        String sDate = binding.etNote.getText() != null ? binding.etNote.getText().toString() : "";


        if (tName.isEmpty() || tDetail.isEmpty() || sDate.isEmpty()) {
            if (tName.isEmpty()) {
                binding.etQuantity.setError(LanguageExtension.setText("enter_end_date", getString(R.string.enter_end_date)));
                binding.etQuantity.requestFocus();
            }
            if (sDate.isEmpty()) {
                binding.etInstructions.setError(LanguageExtension.setText("enter_start_date", getString(R.string.enter_start_date)));
                binding.etInstructions.requestFocus();
            }
            if (tDetail.isEmpty()) {
                binding.etNote.setError(LanguageExtension.setText("enter_task_name", getString(R.string.enter_task_name)));
                binding.etNote.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("patient_id", String.valueOf(patientId));
                parameters.put("medicine_id", String.valueOf(selectedMedicineId));
                parameters.put("quantity", tName);
                parameters.put("instructions", tDetail);
                parameters.put("note", sDate);
                parameters.put("status", "1");


                if (isNew) {
                    saveMedication(parameters);
                } else {
                    parameters.put("medication_id", String.valueOf(allergies_id));

                    editMedication(parameters);
                }
            } else {
                if (taskItem == null)
                    taskItem = new TaskItem();

                taskItem.setTaskName(tName);
                taskItem.setTaskDetail(tDetail);
                taskItem.setPriority(selectedPriority);
//                taskItem.setStartDate(calendarStart.getTimeInMillis());
//                taskItem.setEndDate(calendarEnd.getTimeInMillis());
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

    private void saveMedication(HashMap<String, String> parameters) {

        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_MEDICATION_ADD)
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
                            Toast.makeText(AddMedication.this, message, Toast.LENGTH_SHORT).show();
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

    private void editMedication(HashMap<String, String> parameters) {
        System.out.println("parameters:-"+parameters);
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_MEDICATION_UPDATE)
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
                            Toast.makeText(AddMedication.this, message, Toast.LENGTH_SHORT).show();
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