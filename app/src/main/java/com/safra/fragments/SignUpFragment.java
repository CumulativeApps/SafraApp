package com.safra.fragments;

import static android.app.Activity.RESULT_OK;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.REGISTRATION_API;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.safra.R;
import com.safra.WebActivity;
import com.safra.databinding.FragmentSignUpBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.utilities.IImageCompressTaskListener;
import com.safra.utilities.ImageCompressTask;
import com.safra.utilities.PathFinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class SignUpFragment extends DialogFragment {

    public static final String TAG = "sign_up_fragment";

    private final ActivityResultLauncher<Intent> imagePickLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                if (result.getData() != null) {
                                    selectedImageUri = result.getData().getData();
                                    Log.e(TAG, "onActivityResult: " + selectedImageUri);
                                    Glide.with(mActivity).load(selectedImageUri).override(800).centerCrop().circleCrop()
                                            .into(binding.ivProfileImage);
                                } else {
                                    Log.e(TAG, "onActivityResult: image picker -> Could not get image");
                                }
                            } else {
                                Log.e(TAG, "onActivityResult: user cancelled selection");
                            }
                        }
                    });

    private FragmentActivity mActivity = null;

    private FragmentSignUpBinding binding;

    private boolean acceptTerms = false;
    private Uri selectedImageUri = null;
    private File selectedImageFile = null;

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

//    private LoadingDialog dialogL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        if (getDialog() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.tvSignUpTitle.setText(LanguageExtension.setText("let_s_register_you_in", getString(R.string.let_s_register_you_in)));
        binding.tvSignUpSubtitle.setText(LanguageExtension.setText("welcome_we_re_glad_that_you_re_joining_with_us", getString(R.string.welcome_we_re_glad_that_you_re_joining_with_us)));
        binding.tvSignInTitle.setText(LanguageExtension.setText("already_have_an_account", getString(R.string.already_have_an_account)));
        binding.tvTerms.setText(Html.fromHtml(LanguageExtension.setText("i_have_read_and_agree_to_terms", getString(R.string.i_have_read_and_agree_to_terms))));
        BetterLinkMovementMethod.linkifyHtml(binding.tvTerms).setOnLinkClickListener((textView, url) -> {
//            Toast.makeText(mActivity, url, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(mActivity, WebActivity.class);
            i.putExtra("heading", LanguageExtension.setText("terms_and_conditions", getString(R.string.terms_and_conditions)));
            i.putExtra("url", url);
            startActivity(i);
            return true;
        });

        binding.tilUserName.setHint(LanguageExtension.setText("name", getString(R.string.name)));
        binding.tilEmail.setHint(LanguageExtension.setText("email", getString(R.string.email)));
        binding.tilPassword.setHint(LanguageExtension.setText("password", getString(R.string.password)));
        binding.tilMobileNo.setHint(LanguageExtension.setText("mobile_no", getString(R.string.mobile_no)));
        binding.btnSignUp.setText(LanguageExtension.setText("register", getString(R.string.register)));
        binding.tvSignIn.setText(LanguageExtension.setText("sign_in", getString(R.string.sign_in)));

        Glide.with(this).load(R.mipmap.ic_launcher).override(800).centerCrop().circleCrop()
                .into(binding.ivProfileImage);

        binding.tvSignIn.setOnClickListener(v -> dismiss());

        binding.btnSignUp.setOnClickListener(v -> {
//            Intent dashboardIntent = new Intent(mActivity, Dashboard.class);
//            startActivity(dashboardIntent);
//            mActivity.finish();
            validateInputs();
        });

        setTextChangedListeners();

        binding.cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> acceptTerms = isChecked);

        binding.rlProfile.setOnClickListener(v ->
                Dexter.withContext(mActivity)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    Intent intentPick = new Intent(Intent.ACTION_PICK);
                                    intentPick.setType("image/*");
                                    String[] mimeTypes = {"image/jpeg", "image/png"};
                                    intentPick.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                    imagePickLauncher.launch(intentPick);
//                                    startActivityForResult(intentPick, Common.REQUEST_IMAGE_PICKER);
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
                                                                           PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check());

        return binding.getRoot();
    }

    private void validateInputs() {
        String uName = binding.etUserName.getText() != null ? binding.etUserName.getText().toString() : "";
        String eMail = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
        String pWord = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
        String mNo = binding.etMobileNo.getText() != null ? binding.etMobileNo.getText().toString() : "";

        if (uName.isEmpty() || eMail.isEmpty() || pWord.isEmpty() || mNo.isEmpty()) {
            if (mNo.isEmpty()) {
                binding.tilMobileNo.setErrorEnabled(true);
                binding.tilMobileNo.setError(LanguageExtension.setText("enter_mobile_number", getString(R.string.enter_mobile_number)));
                binding.tilMobileNo.requestFocus();
            }
            if (pWord.isEmpty()) {
                binding.tilPassword.setErrorEnabled(true);
                binding.tilPassword.setError(LanguageExtension.setText("enter_password", getString(R.string.enter_password)));
                binding.tilPassword.requestFocus();
            }
            if (eMail.isEmpty()) {
                binding.tilEmail.setErrorEnabled(true);
                binding.tilEmail.setError(LanguageExtension.setText("enter_email", getString(R.string.enter_email)));
                binding.tilEmail.requestFocus();
            }
            if (uName.isEmpty()) {
                binding.tilUserName.setErrorEnabled(true);
                binding.tilUserName.setError(LanguageExtension.setText("enter_name", getString(R.string.enter_name)));
                binding.tilUserName.requestFocus();
            }
        } else {
            LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("registering_progress", getString(R.string.registering_progress)));
//            dialogL = new LoadingDialog();
//            dialogL.setCancelable(false);
//            Bundle bundle = new Bundle();
//            bundle.putString("loading_message", LanguageExtension.setText("registering_progress", getString(R.string.registering_progress)));
//            dialogL.setArguments(bundle);
//            dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

            if (selectedImageUri != null) {
                ImageCompressTask imageCompressTask =
                        new ImageCompressTask(mActivity, new PathFinder(mActivity).getPath(selectedImageUri), 250, 250,
                                new IImageCompressTaskListener() {
                                    @Override
                                    public void onComplete(List<File> compressed) {
                                        if (compressed.get(0) != null) {
                                            selectedImageFile = compressed.get(0);
                                            Log.d(TAG, "New photo size ==> " + selectedImageFile.length());
                                            registerUser(selectedImageFile, uName, eMail, pWord, mNo);
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
            } else {
                registerUser(uName, eMail, pWord, mNo);
            }
        }
    }

    private void registerUser(String username, String email, String password, String mobileNo) {
        AndroidNetworking
                .post(BASE_URL + REGISTRATION_API)
                .addBodyParameter("user_name", username)
                .addBodyParameter("user_email", email)
                .addBodyParameter("user_password", password)
                .addBodyParameter("user_phone_no", mobileNo)
                .addBodyParameter("terms_check", acceptTerms ? "1" : "0")
                .setTag("register-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                dismiss();
                                goToVerification(email);
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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

    private void registerUser(File profileImage, String username, String email, String password, String mobileNo) {
        AndroidNetworking
                .upload(BASE_URL + REGISTRATION_API)
                .addMultipartFile("user_profile_image", profileImage)
                .addMultipartParameter("user_name", username)
                .addMultipartParameter("user_email", email)
                .addMultipartParameter("user_password", password)
                .addMultipartParameter("user_phone_no", mobileNo)
                .addMultipartParameter("terms_check", acceptTerms ? "1" : "0")
                .setTag("register-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                dismiss();
                                goToVerification(email);
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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

    private void goToVerification(String email) {
        VerificationFragment dialogV = new VerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("email_id", email);
        dialogV.setArguments(bundle);
        dialogV.setCancelable(false);
        dialogV.show(mActivity.getSupportFragmentManager(), VerificationFragment.TAG);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Common.REQUEST_IMAGE_PICKER) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    selectedImageUri = data.getData();
//                    Log.e(TAG, "onActivityResult: " + selectedImageUri);
//                    Glide.with(this).load(selectedImageUri).override(800).centerCrop().circleCrop()
//                            .into(binding.ivProfileImage);
//                } else {
//                    Log.e(TAG, "onActivityResult: image picker -> Could not get image");
//                }
//            } else {
//                Log.e(TAG, "onActivityResult: user cancelled selection");
//            }
//        }
//    }

    private void setTextChangedListeners() {
        binding.etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.tilUserName.setErrorEnabled(false);
            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.tilEmail.setErrorEnabled(false);
            }
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.tilPassword.setErrorEnabled(false);
            }
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.tilMobileNo.setErrorEnabled(false);
            }
        });
    }

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
}
