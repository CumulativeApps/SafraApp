package com.safra;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.CHANGE_PASSWORD_API;
import static com.safra.utilities.Common.UPDATE_USER_PROFILE_API;
import static com.safra.utilities.Common.USER_PROFILE_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.safra.databinding.ActivityAccountBinding;
import com.safra.events.ConnectivityChangedEvent;
import com.safra.extensions.Extension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.ViewExtension;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.IImageCompressTaskListener;
import com.safra.utilities.ImageCompressTask;
import com.safra.utilities.PathFinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Account extends AppCompatActivity {

    public static final String TAG = "account_activity";

    private final ActivityResultLauncher<String> permissionRequester =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                Intent intentPick = new Intent(Intent.ACTION_PICK);
                                intentPick.setType("image/*");
                                String[] mimeTypes = {"image/jpeg", "image/png"};
                                intentPick.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                imagePickLauncher.launch(intentPick);
                            } else {
//                                Toast.makeText(Account.this, "Please grant permission to view images", Toast.LENGTH_SHORT).show();
                                Extension.showSettingsDialog(Account.this);
                            }
                        }
                    });

    private final ActivityResultLauncher<Intent> imagePickLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                if (result.getData() != null) {
                                    selectedImageUri = result.getData().getData();
                                    Log.e(TAG, "onActivityResult: " + selectedImageUri);
                                    Glide.with(Account.this).load(selectedImageUri).override(800).centerCrop().circleCrop()
                                            .into(binding.ivProfileImage);
                                } else {
                                    Log.e(TAG, "onActivityResult: image picker -> Could not get image");
                                }
                            } else {
                                Log.e(TAG, "onActivityResult: user cancelled selection");
                            }
                        }
                    });

    private ActivityAccountBinding binding;

    private Uri selectedImageUri = null;
    private File selectedImageFile = null;

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private boolean isRemembered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        binding.btnSubmit.setOnClickListener(v -> validateInputsForAccount());

        binding.btnUpdatePassword.setOnClickListener(v -> validateInputsForPassword());

        if (ConnectivityReceiver.isConnected())
            getUserAccount();
        else
            setOfflineData();

        binding.ivEditUserName.setOnClickListener(v -> {
            if (binding.etUserName.isEnabled()) {
                binding.etUserName.setEnabled(false);
            } else {
                binding.etUserName.setEnabled(true);
                binding.etUserName.requestFocus();
            }
        });

        binding.rlProfile.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionRequester.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    Intent intentPick = new Intent(Intent.ACTION_PICK);
                    intentPick.setType("image/*");
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intentPick.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    imagePickLauncher.launch(intentPick);
                }
            } else {
                Intent intentPick = new Intent(Intent.ACTION_PICK);
                intentPick.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intentPick.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                imagePickLauncher.launch(intentPick);
            }
        });

        binding.ivExpand.setOnClickListener(v -> {
            if (binding.clChangePasswordFields.getVisibility() == View.GONE) {
                binding.clChangePasswordFields.setVisibility(View.VISIBLE);
            } else {
                binding.clChangePasswordFields.setVisibility(View.GONE);
            }
            binding.clChangePassword.animate().setDuration(200);
            ViewExtension.toggleArrow(v, binding.clChangePasswordFields.getVisibility() == View.VISIBLE);
        });
    }

    private void setText() {
        binding.tvAccountHeading.setText(LanguageExtension.setText("account", getString(R.string.account)));

        binding.tvPhoneTitle.setText(LanguageExtension.setText("change_phone_no", getString(R.string.change_phone_no)));
        binding.tvEmailTitle.setText(LanguageExtension.setText("change_email_id", getString(R.string.change_email_id)));
        binding.btnSubmit.setText(LanguageExtension.setText("submit", getString(R.string.submit)));

        binding.tvChangePasswordTitle.setText(LanguageExtension.setText("change_password", getString(R.string.change_password)));
        binding.tvPasswordTitle.setText(LanguageExtension.setText("current_password", getString(R.string.current_password)));
        binding.tvNewPasswordTitle.setText(LanguageExtension.setText("new_password", getString(R.string.new_password)));
        binding.tvConfirmPasswordTitle.setText(LanguageExtension.setText("re_enter_your_new_password", getString(R.string.re_enter_your_new_password)));
        binding.btnUpdatePassword.setText(LanguageExtension.setText("update", getString(R.string.update)));

    }

    private void getUserAccount() {
        LoadingDialogExtension.showLoading(this,
                LanguageExtension.setText("getting_account_details_progress", getString(R.string.getting_account_details_progress)));
        AndroidNetworking
                .post(BASE_URL + USER_PROFILE_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("user-account-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject userProfile = response.getJSONObject("data").getJSONObject("user_profile");
                                String uName = userProfile.getString("user_name");
                                String uImage = userProfile.getString("user_image_url");
                                String uPhone = userProfile.getString("user_phone_no");

                                if (!uName.isEmpty() && !uName.equals("null"))
                                    binding.etUserName.setText(uName);

                                if (!uImage.isEmpty() && !uImage.equals("null"))
                                    Glide.with(Account.this).load(uImage).centerCrop().circleCrop().into(binding.ivProfileImage);

                                if (!uPhone.isEmpty() && !uPhone.equals("null"))
                                    binding.etPhone.setText(uPhone);

                                binding.etEmail.setText(isRemembered ? userSessionManager.getUserEmail() : Safra.userEmail);

                                binding.clChangePassword.setVisibility(View.VISIBLE);

                            } else {
                                Toast.makeText(Account.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        LoadingDialogExtension.hideLoading();
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

    private void setOfflineData() {
        Glide.with(Account.this).load(isRemembered ? userSessionManager.getUserProfile() : Safra.userProfile)
                .centerCrop().circleCrop().into(binding.ivProfileImage);
        binding.etUserName.setText(isRemembered ? userSessionManager.getUserName() : Safra.userName);
        binding.etPhone.setText(isRemembered ? userSessionManager.getUserMobileNo() : Safra.userPhoneNo);
        binding.etEmail.setText(isRemembered ? userSessionManager.getUserEmail() : Safra.userEmail);

        binding.clChangePassword.setVisibility(View.GONE);
    }

    private void validateInputsForAccount() {
        String uName = binding.etUserName.getText().toString();
        String uPhone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString() : "";

        if (uName.isEmpty()) {
            binding.etUserName.setError(LanguageExtension.setText("enter_name", getString(R.string.enter_name)));
            binding.etUserName.requestFocus();
        } else {
            LoadingDialogExtension.showLoading(this,
                    LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));

            if (selectedImageUri == null) {
                updateProfile(uName, uPhone);
            } else {
                Log.d(TAG, "selectedImageUri -> " + selectedImageUri);
                String path = new PathFinder(this).getPath(selectedImageUri);
                Log.d(TAG, "path -> " + path);
                ImageCompressTask imageCompressTask = new ImageCompressTask(this,
                        path, 250, 250,
                                new IImageCompressTaskListener() {
                                    @Override
                                    public void onComplete(List<File> compressed) {
                                        if(compressed != null) {
                                            if (compressed.get(0) != null) {
                                                selectedImageFile = compressed.get(0);
                                                Log.d(TAG, "New photo size ==> " + selectedImageFile.length());
                                                updateProfile(selectedImageFile, uName, uPhone);
                                            }
                                        } else {
                                            Log.e("ImageCompressor", "onComplete: received result is null");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        Log.e(TAG, "onError: " + error.getLocalizedMessage());
                                    }
                                });
                mExecutorService.execute(imageCompressTask);
            }
        }
    }

    private void validateInputsForPassword() {
        String cPword = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
        String nPword = binding.etNewPassword.getText() != null ? binding.etNewPassword.getText().toString() : "";
        String cfPword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString() : "";
        if (cPword.isEmpty() || nPword.isEmpty() || cfPword.isEmpty()) {
            if (cfPword.isEmpty()) {
                binding.etConfirmPassword.setError(LanguageExtension.setText("re_enter_your_new_password", getString(R.string.re_enter_your_new_password)));
                binding.etConfirmPassword.requestFocus();
            }
            if (nPword.isEmpty()) {
                binding.etNewPassword.setError(LanguageExtension.setText("enter_new_password", getString(R.string.enter_new_password)));
                binding.etNewPassword.requestFocus();
            }
            if (cPword.isEmpty()) {
                binding.etPassword.setError(LanguageExtension.setText("enter_current_password", getString(R.string.enter_current_password)));
                binding.etPassword.requestFocus();
            }
        } else if (!nPword.equals(cfPword)) {
            binding.etConfirmPassword.setError(LanguageExtension.setText("couldn_t_match_with_new_password", getString(R.string.couldn_t_match_with_new_password)));
            binding.etConfirmPassword.requestFocus();
        } else {
            changePassword(cPword, nPword, cfPword);
        }
    }

    private void updateProfile(String userName, String mobileNo) {
        AndroidNetworking
                .post(BASE_URL + UPDATE_USER_PROFILE_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("user_name", userName)
                .addBodyParameter("user_phone_no", mobileNo)
                .setTag("update-profile-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(Account.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                finish();
//                                dismiss();
//                                goToVerification(email);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
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

    private void updateProfile(File profileImage, String userName, String mobileNo) {
        AndroidNetworking
                .upload(BASE_URL + UPDATE_USER_PROFILE_API)
                .addMultipartFile("user_profile_image", profileImage)
                .addMultipartParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addMultipartParameter("user_name", userName)
                .addMultipartParameter("user_phone_no", mobileNo)
                .setTag("update-profile-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(Account.this, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
//                                dismiss();
//                                goToVerification(email);
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
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

    private void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_password_progress", getString(R.string.updating_password_progress)));
        AndroidNetworking
                .post(BASE_URL + CHANGE_PASSWORD_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("old_password", currentPassword)
                .addBodyParameter("new_password", newPassword)
                .addBodyParameter("new_repeat_password", confirmPassword)
                .setTag("change-password-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(Account.this, message, Toast.LENGTH_SHORT).show();
//                            if (success == 1) {
//                                dismiss();
//                                sendResult(RESULT_SUCCESS_RESET_PASSWORD);
//                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        LoadingDialogExtension.hideLoading();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectivityChanged(ConnectivityChangedEvent event) {
        Log.e(TAG, "onConnectivityChanged: " + event.isConnected());
        if (event.isConnected())
            getUserAccount();
        else
            setOfflineData();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Common.REQUEST_IMAGE_PICKER) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    selectedImageUri = data.getData();
//                    Log.e(TAG, "onActivityResult: " + selectedImageUri);
//                    Glide.with(this).load(selectedImageUri).override(800).centerCrop().circleCrop().into(binding.ivProfileImage);
//                } else {
//                    Log.e(TAG, "onActivityResult: image picker -> Could not get image");
//                }
//            } else {
//                Log.e(TAG, "onActivityResult: user cancelled selection");
//            }
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        LoadingDialogExtension.hideLoading();
    }
}