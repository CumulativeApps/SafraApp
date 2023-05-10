package com.safra.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safra.Dashboard;
import com.safra.R;
import com.safra.Safra;
import com.safra.databinding.FragmentSignInBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.SEND_OTP_API;
import static com.safra.utilities.Common.SIGN_IN_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class SignInFragment extends Fragment {

    public static final String TAG = "sign_in_fragment";

    private FragmentActivity mActivity = null;

    private FragmentSignInBinding binding;

    private boolean rememberUser = false;

    private String fcmToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        fcmToken = userSessionManager.getFcmToken();

        if (fcmToken == null && ConnectivityReceiver.isConnected()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
//                    return;
                }
                Log.e(TAG, "onCreate: task completed");

                // Get new Instance ID token
//                sessionManager.upsertFcmToken(Objects.requireNonNull(task.getResult()));
                Log.e(TAG, "onComplete: " + userSessionManager.getFcmToken());
                fcmToken = task.getResult();

            });
        } else {
            Log.e(TAG, "onCreate: checking for token -> " + userSessionManager.getFcmToken());
        }

        binding.tvSignInTitle.setText(LanguageExtension.setText("let_s_sign_you_in", getString(R.string.let_s_sign_you_in)));
        binding.tvSignInSubtitle.setText(LanguageExtension.setText("welcome_back_you_ve_been_missed", getString(R.string.welcome_back_you_ve_been_missed)));
        binding.tilUserName.setHint(LanguageExtension.setText("username_or_email", getString(R.string.username_or_email)));
        binding.tilPassword.setHint(LanguageExtension.setText("password", getString(R.string.password)));
        binding.cbRemember.setText(LanguageExtension.setText("remember_me", getString(R.string.remember_me)));
        binding.tvForgotPassword.setText(LanguageExtension.setText("forgot_password", getString(R.string.forgot_password)));
        binding.btnSignIn.setText(LanguageExtension.setText("sign_in", getString(R.string.sign_in)));
        binding.tvBuySubscriptionTitle.setText(LanguageExtension.setText("don_t_have_account", getString(R.string.don_t_have_account)));
        binding.tvBuySubscription.setText(LanguageExtension.setText("register_here", getString(R.string.register_here)));

        binding.tvForgotPassword.setOnClickListener(v -> {
            ForgotPasswordFragment dialogF = new ForgotPasswordFragment();
            dialogF.setCancelable(false);
            dialogF.show(mActivity.getSupportFragmentManager(), ForgotPasswordFragment.TAG);
        });

        binding.tvBuySubscription.setOnClickListener(v -> {
            SignUpFragment dialogR = new SignUpFragment();
            dialogR.setCancelable(false);
            dialogR.show(mActivity.getSupportFragmentManager(), SignUpFragment.TAG);
        });

        binding.btnSignIn.setOnClickListener(v -> validateInputs());

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

        binding.cbRemember.setOnCheckedChangeListener((buttonView, isChecked) -> rememberUser = isChecked);

        return binding.getRoot();
    }

    private void validateInputs() {
        String uName = binding.etUserName.getText() != null ? binding.etUserName.getText().toString() : "";
        String pWord = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

        if (uName.isEmpty() || pWord.isEmpty()) {
            if (pWord.isEmpty()) {
                binding.tilPassword.setErrorEnabled(true);
                binding.tilPassword.setError(LanguageExtension.setText("enter_password", getString(R.string.enter_password)));
                binding.tilPassword.requestFocus();
            }

            if (uName.isEmpty()) {
                binding.tilUserName.setErrorEnabled(true);
                binding.tilUserName.setError(LanguageExtension.setText("enter_email", getString(R.string.enter_email)));
                binding.tilUserName.requestFocus();
            }
        } else {
//            Toast.makeText(mActivity, "OTP is 1234", Toast.LENGTH_SHORT).show();

            if (ConnectivityReceiver.isConnected()) {
                fcmToken = userSessionManager.getFcmToken();
                if (fcmToken == null) {
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        Log.e(TAG, "onCreate: task completed");

                        // Get new Instance ID token
//                sessionManager.upsertFcmToken(Objects.requireNonNull(task.getResult()));
                        Log.e(TAG, "onComplete: " + userSessionManager.getFcmToken());
                        fcmToken = task.getResult();
                        logInUser(uName, pWord);
                    });
                } else {
                    logInUser(uName, pWord);
                }

            } else
                logInUserFromDB(uName, pWord);

//            VerificationFragment dialogV = new VerificationFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("email_id", uName);
//            dialogV.setArguments(bundle);
//            dialogV.show(((SignIn) mActivity).getSupportFragmentManager(), VerificationFragment.TAG);
        }
    }

    private void logInUserFromDB(String username, String password) {
        UserItem userItem = dbHandler.signInUser(username, password);
        if (userItem != null) {
            userSessionManager.rememberUser(rememberUser);
            Bundle bundle = new Bundle();
            try {
                bundle = PermissionExtension.makePermissionJsonToList(new JSONArray(userItem.getUserAccessJson()));
            } catch (JSONException e) {
                Log.e(TAG, "logInUserFromDB: " + e.getLocalizedMessage());
            }
            if (rememberUser) {
                userSessionManager.signInUser(userItem, bundle.getStringArrayList("permission_list"));
            } else {
                Safra.signInUser(userItem);
            }
            Safra.setPermissionList(bundle.getStringArrayList("permission_list"));

            startActivity(new Intent(mActivity, Dashboard.class));
            mActivity.finish();
        } else {
            Toast.makeText(mActivity, LanguageExtension.setText("no_sign_in_in_offline_mode", getString(R.string.no_sign_in_in_offline_mode)), Toast.LENGTH_LONG).show();
        }
    }

    private void logInUser(String email, String password) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("signing_in_progress", getString(R.string.signing_in_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("signing_in_progress", getString(R.string.signing_in_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);
        AndroidNetworking
                .post(BASE_URL + SIGN_IN_API)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("fcm_token", fcmToken)
                .setTag("log-in-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                long userId = data.getLong("user_id");
                                String userToken = data.getString("user_token");
                                Log.e(TAG, "onResponse: " + userToken);
                                String userName = data.getString("user_name");
                                String userPhoneNo = data.getString("user_phone_no");

                                String userProfile = data.getString("user_image_url");
                                long roleId;
                                if (data.has("user_role_id") && !data.isNull("user_role_id"))
                                    roleId = data.getLong("user_role_id");
                                else
                                    roleId = 0;
                                boolean isAgency = data.getInt("user_is_agency") == 1;

                                JSONArray userAccess = data.getJSONArray("user_access");
                                Bundle b = PermissionExtension.makePermissionJsonToList(userAccess);

                                UserItem userItem = new UserItem();
                                userItem.setUserOnlineId(userId);
                                userItem.setRoleId(roleId);
                                userItem.setUserToken(userToken);
                                userItem.setUserName(userName);
                                userItem.setUserEmail(email);
                                userItem.setUserPassword(password);
                                userItem.setUserPhone(userPhoneNo);
                                userItem.setUserProfile(userProfile);
                                userItem.setAgency(isAgency);
                                userItem.setModuleIds(GeneralExtension.toLongArray(b.getString("module_ids"), ","));
                                userItem.setPermissionIds(GeneralExtension.toLongArray(b.getString("permission_ids"), ","));
                                userItem.setUserAccessJson(userAccess.toString());

                                dbHandler.addUserViaSignIn(userItem);

                                userSessionManager.rememberUser(rememberUser);
                                if (rememberUser) {
                                    userSessionManager.signInUser(userItem, b.getStringArrayList("permission_list"));
                                } else {
                                    Safra.signInUser(userItem);
                                }
                                Safra.setPermissionList(b.getStringArrayList("permission_list"));

                                startActivity(new Intent(mActivity, Dashboard.class));
                                mActivity.finish();
                            } else if (success == 4) {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                                sendOtp(email);
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

    private void sendOtp(String email) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("resending_progress", getString(R.string.resending_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("resending_progress", getString(R.string.resending_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + SEND_OTP_API)
                .addBodyParameter("user_email", email)
                .setTag("send-otp-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            if (success == 1) {
                                goToVerification(email);
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

    private void goToVerification(String username) {
        VerificationFragment dialogV = new VerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("email_id", username);
        dialogV.setArguments(bundle);
        dialogV.show(mActivity.getSupportFragmentManager(), VerificationFragment.TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
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
