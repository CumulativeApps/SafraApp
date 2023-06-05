package com.safra;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_ADD_MEDICINE;
import static com.safra.utilities.Common.HEALTH_RECORD_PROVIDER_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_UPDATE_MEDICINE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.databinding.ActivityAddMedicineBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMedicine extends AppCompatActivity {
    public static final String TAG = "add_medicine_activity";

    private ActivityAddMedicineBinding binding;

    private boolean isRemembered;
    private final int pPosition = -1;
    private boolean isNew;
    private long projectId = -1;
    private int status = -1;
    private int medicine_id = -1;
    private String name, chemical, provider_name;


    int selectedProviderId = -1; // Default value if ID is not found
    int undefinedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();
        CheckBox checkboxUnidentified = findViewById(R.id.stock_available);
        checkboxUnidentified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                undefinedValue = isChecked ? 1 : 0;

                if (isChecked) {
                    // Checkbox is selected, hide the constraint layouts

                } else {
                    // Checkbox is not selected, unhide the constraint layouts

                }
                ;
            }
        });
        if (getIntent() != null) {
            binding.tvAddMedicineHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (isNew) {

                if (ConnectivityReceiver.isConnected()) {
//                    getUserListFromDB();
//                    getGroupListFromDB();
//                    getUsers(PAGE_START);
                    getProviderList(pPosition);
//                    getGroups(PAGE_START);
                } else {
//                    getUserListFromDB();
//                    getGroupListFromDB();
                }
            } else {
                getProviderList(pPosition);
                projectId = getIntent().getLongExtra("project_id", -1);

                name = getIntent().getStringExtra("name");
                medicine_id = getIntent().getIntExtra("medicine_id", -1);
                chemical = getIntent().getStringExtra("chemical");
                provider_name = getIntent().getStringExtra("providerName");
                status = getIntent().getIntExtra("status", -1);
                binding.etProjectName.setText(name);
                binding.etChemicalName.setText(chemical);
                if (status == 1) {
                    // Set the checkbox as checked
                    binding.stockAvailable.setChecked(true);
                } else {
                    // Set the checkbox as unchecked
                    binding.stockAvailable.setChecked(false);
                }


//                onlineId = getIntent().getLongExtra("online_id", -1);
//                Log.e(TAG, "onCreate: " + projectId);
//                if (ConnectivityReceiver.isConnected()) {
//                    getEditProject();
//
//                }
//                    getEditData();
//                getEditDataFromDB();
//                else
//                    getEditDataFromDB();
            }
        }


        binding.btnSave.setOnClickListener(v -> {
            validateInputs();

        });

    }

    private void getProviderList(int pPosition) {

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_PROVIDER_LIST)
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
                                JSONArray providers = data.getJSONArray("providers");

                                // Create a list to hold provider names
                                List<String> providerNames = new ArrayList<>();
                                // Create a map to associate provider IDs with names
                                Map<Integer, String> providerIdMap = new HashMap<>();

                                for (int i = 0; i < providers.length(); i++) {
                                    JSONObject provider = providers.getJSONObject(i);
                                    int id = provider.getInt("id");
                                    String name = provider.getString("name");

                                    // Add provider name to the list
                                    providerNames.add(name);
                                    // Associate provider ID with name in the map
                                    providerIdMap.put(id, name);
                                }

                                // Display the provider names in a spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMedicine.this, android.R.layout.simple_spinner_dropdown_item, providerNames);
                                binding.spnPriority.setAdapter(adapter);

                                // Set a listener to handle spinner item selection
                                binding.spnPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        // Get the selected provider name using the position in the spinner
                                        String selectedProviderName = providerNames.get(position);

                                        // Get the associated provider ID from the providerIdMap using the selected name
                                        for (Map.Entry<Integer, String> entry : providerIdMap.entrySet()) {
                                            if (entry.getValue().equals(selectedProviderName)) {
                                                selectedProviderId = entry.getKey();

                                                break;
                                            }
                                        }

                                        // Pass the selected provider ID to the desired method or perform any other action
//                                        yourMethodToHandleSelectedProvider(selectedProviderId);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Handle the case when no provider is selected
                                    }
                                });

                            } else {
                                String errorMessage = "API request unsuccessful: " + message;
                                // Handle the error message
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
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


    private void setText() {
        binding.tvProjectNameTitle.setText(LanguageExtension.setText("name", getString(R.string.name)));
        binding.tvChemicalNameTitle.setText(LanguageExtension.setText("chemical_name", getString(R.string.chemical_name)));
        binding.tvProviderTitle.setText(LanguageExtension.setText("provider", getString(R.string.provider)));

        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }


    private void validateInputs() {
        String tName = binding.etProjectName.getText() != null ? binding.etProjectName.getText().toString() : "";
        String tDetail = binding.etChemicalName.getText() != null ? binding.etChemicalName.getText().toString() : "";

        if (tName.isEmpty()) {

            if (tName.isEmpty()) {
                binding.etProjectName.setError(LanguageExtension.setText("enter_task_name", getString(R.string.enter_task_name)));
                binding.etProjectName.requestFocus();
            }
        } else {
            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("name", tName);
                parameters.put("chemical", tDetail);
                parameters.put("providor_id", String.valueOf(selectedProviderId));
                parameters.put("status", String.valueOf(undefinedValue));

                if (isNew) {
                    saveMedicine(parameters);
                } else {

                    parameters.put("id", String.valueOf(medicine_id));

                    editMedicine(parameters);
                }
            } else {

            }
        }
    }

    private void saveMedicine(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_ADD_MEDICINE)
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
                            Toast.makeText(AddMedicine.this, message, Toast.LENGTH_SHORT).show();
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

    private void editMedicine(HashMap<String, String> parameters) {


        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_task_progress", getString(R.string.updating_task_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_UPDATE_MEDICINE)
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
                            Toast.makeText(AddMedicine.this, message, Toast.LENGTH_SHORT).show();
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
