package com.safra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.safra.databinding.ActivityFillFormBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.FormExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.interfaces.FileSelectionInterface;
import com.safra.interfaces.RequestLocationInterface;
import com.safra.models.FileItem;
import com.safra.models.ResponseItem;
import com.safra.models.formElements.BaseFormElement;
import com.safra.utilities.Common;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.FormBuilder;
import com.safra.utilities.PathFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_FILL_PRIVATE_API;
import static com.safra.utilities.Common.REQUEST_GPS;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_SELECT_BOXES_GROUP;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class FillForm extends AppCompatActivity
        implements FileSelectionInterface, RequestLocationInterface {

    public static final String TAG = "fill_form_activity";

    private ActivityFillFormBinding binding;

    private FormBuilder formBuilder;

    private final List<BaseFormElement> formElementList = new ArrayList<>();
    private final HashMap<String, List<File>> fileHashMap = new HashMap<>();

    private BaseFormElement fileSelectionElement = null;
    private int fileSelectionPosition = -1;

    private final ActivityResultLauncher<Intent> selectFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Uri uri = result.getData().getData();
                                String path = new PathFinder(FillForm.this).getPath(uri);
                                Log.e(TAG, "onActivityResult: " + uri.toString());
                                fileSelectionElement.setFieldValue(path.substring(path.lastIndexOf("/") + 1));
                                List<File> fileList = new ArrayList<>();
                                fileList.add(new File(path));
                                fileHashMap.put(fileSelectionElement.getFieldName(), fileList);
                                formBuilder.updateFormElement(fileSelectionElement, fileSelectionPosition);
                            }
                            fileSelectionElement = null;
                            fileSelectionPosition = -1;
                        }
                    });

    private BaseFormElement locationElement = null;
    private int locationElementPosition = -1;

    FusedLocationProviderClient locationProvider;
    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
            if (locationResult.getLocations().size() > 0) {
                Location location = locationResult.getLocations().get(0);
                Log.e(TAG, "onComplete: accuracy -> " + location.getAccuracy());
                Log.e(TAG, "onComplete: latitude -> " + location.getLatitude());
                Log.e(TAG, "onComplete: longitude -> " + location.getLongitude());
                Log.e(TAG, "onComplete: provider -> " + location.getProvider());
                Log.e(TAG, "onComplete: time -> " + location.getTime());
//                locationElement.setFieldValue();
                locationElement.setFieldValue("" + location.getLatitude() + "," + location.getLongitude());
                formBuilder.updateFormElement(locationElement, locationElementPosition);
            }
        }
    };
    LocationRequest locationRequest = LocationRequest.create()
            .setFastestInterval(Common.FASTEST_INTERVAL)
            .setInterval(Common.UPDATE_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private boolean isRemembered;

    private long formId, responseId = -1, responseOnlineId = -1;
    private String reasonToCome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFillFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        isRemembered = userSessionManager.isRemembered();

        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));

        formBuilder = new FormBuilder(this, binding.rvFormFields, null,
                this, this);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            reasonToCome = bundle.getString("reason_to_come");
            binding.tvFormTitleHeading.setText(bundle.getString("form_title"));
            formId = bundle.getLong("form_id");
            String formFieldsJson = bundle.getString("form_fields");
            if (reasonToCome.equalsIgnoreCase("edit_response")) {
                responseId = bundle.getLong("response_id");
                responseOnlineId = bundle.getLong("online_id");
                if (bundle.containsKey("files")) {
                    ArrayList<FileItem> fileList = bundle.getParcelableArrayList("files");
                    convertFieldsWithResponseToList(formFieldsJson, fileList);
                } else {
                    convertFieldsWithResponseToList(formFieldsJson);
                }
            } else {
                convertFieldsToList(formFieldsJson);
            }
//            long formAccess = bundle.getLong("form_access");

//            convertFieldsToList(formFieldsJson);
        }

        binding.btnSave.setOnClickListener(v -> {
            if (!formBuilder.isValidForm()) {
                Toast.makeText(this, LanguageExtension.setText("please_fill_all_fields_with_mandatory", getString(R.string.please_fill_all_fields_with_mandatory)), Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "You can submit form now", Toast.LENGTH_SHORT).show();

                formElementList.clear();
                formElementList.addAll(formBuilder.getFormElements());

                makeResponseData(formBuilder, formElementList);
            }
        });
    }

    private void makeResponseData(FormBuilder formBuilder, List<BaseFormElement> formElementList) {
        for (int i = 0; i < formElementList.size(); i++) {
            BaseFormElement responseFormElement = formElementList.get(i);
            BaseFormElement baseFormElement = formBuilder.getFormElement(i);

            List<String> userDataList = new ArrayList<>();
            if (baseFormElement.getType() == TYPE_SELECT_BOXES_GROUP) {
                if (baseFormElement.getFieldValue() != null) {
                    String[] data = baseFormElement.getFieldValue().split(",");
                    userDataList.addAll(Arrays.asList(data));
                }
            } else if (baseFormElement.getType() == TYPE_CASCADING) {
                for(int j=0; j<baseFormElement.getElementList().size(); j++){
                    BaseFormElement responseCascadeSelectElement = responseFormElement.getElementList().get(j);
                    BaseFormElement cascadeSelectElement = baseFormElement.getElementList().get(j);
                    List<String> cascadeUserDataList = new ArrayList<>();
//                  if(cascadeSelectElement.getFieldValue() != null) {
                        cascadeUserDataList.add(cascadeSelectElement.getFieldValue());
//                  }
                    responseCascadeSelectElement.setUserData(cascadeUserDataList);
                }
                userDataList.add(baseFormElement.getElementJsonArray());
            } else if(baseFormElement.getType() == TYPE_QUIZ_TEXT) {
                for(int j=0; j<baseFormElement.getElementList().size(); j++){
                    BaseFormElement responseChildElement = responseFormElement.getElementList().get(j);
                    BaseFormElement childElement = baseFormElement.getElementList().get(j);
                    List<String> childUserDataList = new ArrayList<>();
                    if (childElement.getFieldValue() != null) {
                        childUserDataList.add(childElement.getFieldValue());
                    }else if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size()>0)
                        childUserDataList.add(baseFormElement.getUserData().get(0));

                    responseChildElement.setUserData(childUserDataList);
                }
            } else {
                if (baseFormElement.getFieldValue() != null && !baseFormElement.getFieldValue().toString().isEmpty())
                    userDataList.add(baseFormElement.getFieldValue());
                else if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size()>0)
                    userDataList.add(baseFormElement.getUserData().get(0));
            }
            responseFormElement.setUserData(userDataList);
        }

        try {
            Log.e(TAG, "makeResponseData: " + convertFieldsToJson());
            Log.e(TAG, "makeResponseData: " + fileHashMap.size());

            if (fileHashMap.size() > 0) {

                for (String key : fileHashMap.keySet()) {
                    List<File> files = fileHashMap.get(key);
                    Log.e(TAG, "makeResponseData: " + files.get(0).getName());
                    Log.e(TAG, "makeResponseData: " + files.get(0).getPath());
                }

                if (ConnectivityReceiver.isConnected()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                    hashMap.put("user_data", convertFieldsToJson());
                    hashMap.put("form_id", String.valueOf(formId));
                    if (reasonToCome.equalsIgnoreCase("edit_response")) {
                        hashMap.put("response_id", String.valueOf(responseOnlineId));
                    }
                    uploadResponseWithFiles(fileHashMap, hashMap);
                } else {
                    Log.e("offline","edit");
                    ResponseItem responseItem = new ResponseItem();
                    responseItem.setFormId(formId);
                    responseItem.setUserId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                    responseItem.setUserName(isRemembered ? userSessionManager.getUserName() : Safra.userName);
                    responseItem.setSubmitDate(new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
                            .format(new Date(Calendar.getInstance().getTimeInMillis())));
                    responseItem.setResponseData(convertFieldsToJson());
                    responseItem.setResponseFiles(convertFileListToResponseFiles());
                    responseItem.setDelete(false);
                    if (reasonToCome.equalsIgnoreCase("edit_response")) {
                        responseItem.setResponseId(responseId);
                        responseItem.setOnlineId(responseOnlineId);
                        int i = dbHandler.updateResponseOffline(responseItem);
                        if (i > 0)
                            finish();
                    } else {
                        long i = dbHandler.addResponseOffline(responseItem);
                        if (i > 0) {
                            finish();
                        }
                    }
                }
            } else {
                if (ConnectivityReceiver.isConnected()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                    hashMap.put("user_data", convertFieldsToJson());
                    hashMap.put("form_id", String.valueOf(formId));
                    if (reasonToCome.equalsIgnoreCase("edit_response")) {
                        hashMap.put("response_id", String.valueOf(responseOnlineId));
                    }
                    uploadResponse(hashMap);
                } else {
                    ResponseItem responseItem = new ResponseItem();
                    responseItem.setFormId(formId);
                    responseItem.setUserId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                    responseItem.setUserName(isRemembered ? userSessionManager.getUserName() : Safra.userName);
                    responseItem.setSubmitDate(new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
                            .format(new Date(Calendar.getInstance().getTimeInMillis())));
                    responseItem.setResponseData(convertFieldsToJson());
                    responseItem.setDelete(false);

                    if (reasonToCome.equalsIgnoreCase("edit_response")) {
                        responseItem.setResponseId(responseId);
                        responseItem.setOnlineId(responseOnlineId);
                        int i = dbHandler.updateResponseOffline(responseItem);
                        if (i > 0)
                            finish();
                    } else {
                        long i = dbHandler.addResponseOffline(responseItem);
                        if (i > 0) {
                            finish();
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "makeResponseData: " + e.getLocalizedMessage());
        }
    }

    private ArrayList<FileItem> convertFileListToResponseFiles() {
        ArrayList<FileItem> fileList = new ArrayList<>();
        long i = 0;
        for (String key : fileHashMap.keySet()) {
            List<File> files = fileHashMap.get(key);
            Log.e(TAG, "makeResponseData: " + files.get(0).getName());
            Log.e(TAG, "makeResponseData: " + files.get(0).getPath());
            FileItem fileItem = new FileItem();
            fileItem.setFileId(i);
            fileItem.setFileUrl(files.get(0).getPath());
            fileItem.setParentFieldName(key);
            i++;

            fileList.add(fileItem);
        }

        return fileList;
    }

    private void uploadResponseWithFiles(HashMap<String, List<File>> files, HashMap<String, String> hashMap) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_response_progress", getString(R.string.saving_response_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_response_progress", getString(R.string.saving_response_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .upload(BASE_URL + FORM_FILL_PRIVATE_API)
                .addMultipartFileList(files)
                .addMultipartParameter(hashMap)
                .setTag("fill-form-private-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(FillForm.this, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                            dialogL.dismiss();
                        }
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

    private void uploadResponse(HashMap<String, String> hashMap) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_response_progress", getString(R.string.saving_response_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_response_progress", getString(R.string.saving_response_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + FORM_FILL_PRIVATE_API)
                .addBodyParameter(hashMap)
                .setTag("fill-form-private-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(FillForm.this, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                            dialogL.dismiss();
                        }
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

    private void convertFieldsWithResponseToList(String formFieldsJson, ArrayList<FileItem> fileList) {
        Log.e(TAG, "convertFieldsWithResponseToList: " + formFieldsJson);
        try {
            JSONArray jsonArray = new JSONArray(formFieldsJson);
            FormExtension.convertJSONToElement(formElementList, jsonArray);
            FormExtension.convertJSONToElementForResponse(formBuilder, jsonArray, fileList);
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                }
//            }
        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsWithResponseToList: " + je.getLocalizedMessage());
        }
    }

    private void convertFieldsWithResponseToList(String formFieldsJson) {
        Log.e(TAG, "convertFieldsWithResponseToList: " + formFieldsJson);
        try {
            JSONArray jsonArray = new JSONArray(formFieldsJson);
            FormExtension.convertJSONToElement(formElementList, jsonArray);
            FormExtension.convertJSONToElementForResponse(formBuilder, jsonArray, new ArrayList<>());
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                }
//            }
        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsWithResponseToList: " + je.getLocalizedMessage());
        }
    }

    private void convertFieldsToList(String formFieldsJson) {
        Log.e(TAG, "convertFieldsToList: " + formFieldsJson);
        try {
            JSONArray jsonArray = new JSONArray(formFieldsJson);
            FormExtension.convertJSONToElement(formBuilder, jsonArray);
            FormExtension.convertJSONToElement(formElementList, jsonArray);
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                }
//            }
        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsToList: " + je.getLocalizedMessage());
        }
    }

    private String convertFieldsToJson() throws JSONException {
        JSONArray jsonArray = new JSONArray();
//        List<BaseFormElement> elementList = formBuilder.getFormElements();
        jsonArray = FormExtension.convertElementToJSON(formElementList);
//        for (BaseFormElement be : formElementList) {
//        }

        return jsonArray.toString();
    }

    @Override
    public void selectFileFor(int position, BaseFormElement baseFormElement) {
        fileSelectionElement = baseFormElement;
        fileSelectionPosition = position;
        Intent iGI = new Intent(Intent.ACTION_GET_CONTENT);
        iGI.setType("*/*");
        iGI.addCategory(Intent.CATEGORY_OPENABLE);
        iGI.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        iGI.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        // Launching the Intent
        try {
            selectFileLauncher.launch(Intent.createChooser(iGI,
                    LanguageExtension.setText("select_a_file_to_upload", getString(R.string.select_a_file_to_upload))));
//            startActivityForResult(
//                    Intent.createChooser(iGI, LanguageExtension.setText("select_a_file_to_upload", getString(R.string.select_a_file_to_upload))),
//                    5001);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, LanguageExtension.setText("please_install_a_file_manager", getString(R.string.please_install_a_file_manager)),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void requestLocation(BaseFormElement baseFormElement, int position) {
        Log.e(TAG, "requestLocation: requesting location");
        locationElement = baseFormElement;
        locationElementPosition = position;

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder()
                                    .addLocationRequest(locationRequest).setAlwaysShow(true);

                            SettingsClient client = LocationServices.getSettingsClient(FillForm.this);
                            Task<LocationSettingsResponse> task = client.checkLocationSettings(locationSettingBuilder.build());
                            task.addOnSuccessListener(FillForm.this, locationSettingsResponse -> {
                                locationProvider.getLastLocation().addOnCompleteListener(task1 -> {
                                    Log.e(TAG, "onComplete: " + task1.isSuccessful());
                                    Log.e(TAG, "onComplete: " + task1.getException());
                                    if (task1.getResult() != null) {
                                        Location location = task1.getResult();
                                        Log.e(TAG, "onComplete: accuracy -> " + location.getAccuracy());
                                        Log.e(TAG, "onComplete: latitude -> " + location.getLatitude());
                                        Log.e(TAG, "onComplete: longitude -> " + location.getLongitude());
                                        Log.e(TAG, "onComplete: provider -> " + location.getProvider());
                                        Log.e(TAG, "onComplete: time -> " + location.getTime());
//                locationElement.setFieldValue();
                                        locationElement.setFieldValue("" + location.getLatitude() + "," + location.getLongitude());
                                        formBuilder.updateFormElement(locationElement, locationElementPosition);
                                    }
                                });
                                startLocationUpdates(locationRequest);
                            });
                            task.addOnFailureListener(FillForm.this, e -> {
                                if (e instanceof ResolvableApiException) {
                                    try {
                                        ResolvableApiException resolvable = (ResolvableApiException) e;
                                        resolvable.startResolutionForResult(FillForm.this, REQUEST_GPS);
                                    } catch (IntentSender.SendIntentException sendIntentException) {
                                        sendIntentException.printStackTrace();
                                        Log.e(TAG, "onFailure: Pending Intent unable to execute Request");
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                        Toast.makeText(FillForm.this,
                                LanguageExtension.setText("we_need_location_permission_to_fill_field", getString(R.string.we_need_location_permission_to_fill_field)), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void startLocationUpdates(LocationRequest locationRequest) {
        Log.e(TAG, "startLocationUpdates: starting location updates");

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            locationProvider.requestLocationUpdates(locationRequest, callback, Looper.myLooper());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
                                                                   PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                        Toast.makeText(FillForm.this,
                                LanguageExtension.setText("we_need_location_permission_to_fill_field", getString(R.string.we_need_location_permission_to_fill_field)), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: creating field");
        if (requestCode == 5001) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                Log.e(TAG, "onActivityResult: " + uri.toString());
                String path = new PathFinder(this).getPath(uri);
                Log.e(TAG, "onActivityResult: " + path);
                fileSelectionElement.setFieldValue(path.substring(path.lastIndexOf("/") + 1));
                List<File> fileList = new ArrayList<>();
                fileList.add(new File(path));
                fileHashMap.put(fileSelectionElement.getFieldName(), fileList);
                formBuilder.updateFormElement(fileSelectionElement, fileSelectionPosition);
            }
            fileSelectionElement = null;
            fileSelectionPosition = -1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationElementPosition > -1) {
            startLocationUpdates(locationRequest);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationProvider.removeLocationUpdates(callback);
    }
}