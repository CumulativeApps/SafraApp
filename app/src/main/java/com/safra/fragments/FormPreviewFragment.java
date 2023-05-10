package com.safra.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

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
import com.safra.R;
import com.safra.databinding.FragmentFormPreviewBinding;
import com.safra.extensions.FormExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.FileSelectionInterface;
import com.safra.interfaces.RequestLocationInterface;
import com.safra.models.formElements.BaseFormElement;
import com.safra.utilities.Common;
import com.safra.utilities.FormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.safra.utilities.Common.REQUEST_GPS;

public class FormPreviewFragment extends DialogFragment
        implements FileSelectionInterface, RequestLocationInterface {

    public static final String TAG = "form_preview_fragment";

    private final ActivityResultLauncher<Intent> selectFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                Log.e(TAG, "onActivityResult: " + uri.toString());
                fileSelectionElement.setFieldValue(uri.toString());
                formBuilder.updateFormElement(fileSelectionElement, fileSelectionPosition);
            }
            fileSelectionElement = null;
            fileSelectionPosition = -1;
        }
    });

    private FragmentActivity mActivity = null;

    private FormBuilder formBuilder;

    private BaseFormElement fileSelectionElement = null;
    private int fileSelectionPosition = -1;

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
                locationElement.setFieldValue("lat - " + location.getLatitude() + ", lan - " + location.getLongitude());
                formBuilder.updateFormElement(locationElement, locationElementPosition);
            }
        }
    };
    LocationRequest locationRequest = LocationRequest.create()
            .setFastestInterval(Common.FASTEST_INTERVAL)
            .setInterval(Common.UPDATE_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentFormPreviewBinding binding = FragmentFormPreviewBinding.inflate(inflater, container, false);

        locationProvider = LocationServices.getFusedLocationProviderClient(mActivity);

        formBuilder = new FormBuilder(getActivity(), binding.rvFormFields,
                null, this, this);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            binding.tvFormTitle.setText(bundle.getString("form_title"));
            String formFieldsJson = bundle.getString("form_fields");
            convertFieldsToList(formFieldsJson);
        }

        binding.ivClose.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    private void convertFieldsToList(String formFieldsJson) {
        int maxLogSize = 2000;
        for(int i = 0; i <= formFieldsJson.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = Math.min(end, formFieldsJson.length());
            Log.e(TAG, formFieldsJson.substring(start, end));
        }
        try {
            JSONArray jsonArray = new JSONArray(formFieldsJson);
            if (jsonArray.length() > 0) {
                System.out.println("formBuilder:-"  +  jsonArray);

                FormExtension.convertJSONToElement(formBuilder, jsonArray);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                }
            }
        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsToList: " + je.getLocalizedMessage());
        }
    }

    @Override
    public void selectFileFor(int position, BaseFormElement baseFormElement) {
        fileSelectionElement = baseFormElement;
        fileSelectionPosition = position;
        Intent iGI = new Intent(Intent.ACTION_GET_CONTENT);
        iGI.setType("*/*");
        iGI.addCategory(Intent.CATEGORY_OPENABLE);
        iGI.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        // Launching the Intent
        try {
            selectFileLauncher.launch(Intent.createChooser(iGI,
                    LanguageExtension.setText("select_a_file_to_upload", getString(R.string.select_a_file_to_upload))));
//            startActivityForResult(
//                    Intent.createChooser(iGI,
//                            LanguageExtension.setText("select_a_file_to_upload", getString(R.string.select_a_file_to_upload))),
//                    5001);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mActivity,
                    LanguageExtension.setText("please_install_a_file_manager", getString(R.string.please_install_a_file_manager)),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void requestLocation(BaseFormElement baseFormElement, int position) {
        Log.e(TAG, "requestLocation: requesting location");
        locationElement = baseFormElement;
        locationElementPosition = position;

        Dexter.withContext(mActivity)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            LocationSettingsRequest.Builder locationSettingBuilder = new LocationSettingsRequest.Builder()
                                    .addLocationRequest(locationRequest).setAlwaysShow(true);

                            SettingsClient client = LocationServices.getSettingsClient(mActivity);
                            Task<LocationSettingsResponse> task = client.checkLocationSettings(locationSettingBuilder.build());
                            task.addOnSuccessListener(mActivity, locationSettingsResponse -> {
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
                                        locationElement.setFieldValue("lat - " + location.getLatitude() + ", lan - " + location.getLongitude());
                                        formBuilder.updateFormElement(locationElement, locationElementPosition);
                                    }
                                });
                                startLocationUpdates(locationRequest);
                            });
                            task.addOnFailureListener(mActivity, e -> {
                                if (e instanceof ResolvableApiException) {
                                    try {
                                        ResolvableApiException resolvable = (ResolvableApiException) e;
                                        resolvable.startResolutionForResult(mActivity, REQUEST_GPS);
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
                        Toast.makeText(mActivity,
                                LanguageExtension.setText("we_need_location_permission_to_fill_field",
                                getString(R.string.we_need_location_permission_to_fill_field)), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void startLocationUpdates(LocationRequest locationRequest) {
        Log.e(TAG, "startLocationUpdates: starting location updates");

        Dexter.withContext(mActivity)
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
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                        Toast.makeText(mActivity,
                                LanguageExtension.setText("we_need_location_permission_to_fill_field",
                                        getString(R.string.we_need_location_permission_to_fill_field)), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "onActivityResult: creating field");
//        if (requestCode == 5001) {
//            if (resultCode == RESULT_OK && data != null) {
//                Uri uri = data.getData();
//                Log.e(TAG, "onActivityResult: " + uri.toString());
//                fileSelectionElement.setFieldValue(uri.toString());
//                formBuilder.updateFormElement(fileSelectionElement, fileSelectionPosition);
//            }
//            fileSelectionElement = null;
//            fileSelectionPosition = -1;
//        }
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
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
