package com.safra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.databinding.FragmentResetPasswordBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import in.aabhasjindal.otptextview.OTPListener;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.RESET_PASSWORD_API;
import static com.safra.utilities.Common.SEND_OTP_API;

public class ResetPasswordFragment extends DialogFragment {

    public static final String TAG = "reset_password_fragment";

    private FragmentActivity mActivity = null;

    private FragmentResetPasswordBinding binding;

    private final long initialTimeInMilliSeconds = 2 * 60000L;
    private long timeLeftInSeconds;

    private String email;

    private String requestKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);

        binding.tvVerificationTitle.setText(LanguageExtension.setText("enter_4_digit_code_sent_to", getString(R.string.enter_4_digit_code_sent_to)));
        binding.tvVerificationSubtitle.setText(LanguageExtension.setText("we_ve_sent_a_4_digit_code_to_your_email_address_please_enter_the_verification_code",
                getString(R.string.we_ve_sent_a_4_digit_code_to_your_email_address_please_enter_the_verification_code)));
        binding.tilNewPassword.setHint(LanguageExtension.setText("new_password", getString(R.string.new_password)));
        binding.tilRepeatPassword.setHint(LanguageExtension.setText("re_enter_your_new_password", getString(R.string.re_enter_your_new_password)));
        binding.btnReset.setText(LanguageExtension.setText("reset_password", getString(R.string.reset_password)));
        binding.tvResendTitle.setText(LanguageExtension.setText("didn_t_receive_the_sms", getString(R.string.didn_t_receive_the_sms)));
        binding.btnResend.setText(LanguageExtension.setText("request_new_code", getString(R.string.request_new_code)));

        if (getArguments() != null) {
            requestKey = getArguments().getString("request_key");
            email = getArguments().getString("email_id");
            binding.tvVerificationEmail.setText(email);
        }

        startTimer();

        binding.btnReset.setOnClickListener(v -> validateInputs());

        binding.btnResend.setOnClickListener(v -> resendOtp());

        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

            }
        });

        binding.ivClose.setOnClickListener(v -> {
            dismiss();
//            sendResult(RESULT_FAILED_RESET_PASSWORD);
        });

        return binding.getRoot();
    }

    private void validateInputs() {
        String o = binding.otpView.getOTP();
        String pWord = binding.etNewPassword.getText() != null ? binding.etNewPassword.getText().toString() : "";
        String rptPword = binding.etRepeatPassword.getText() != null ? binding.etRepeatPassword.getText().toString() : "";

        if (o.isEmpty() || pWord.isEmpty() || rptPword.isEmpty()) {
            if (rptPword.isEmpty()) {
                binding.tilRepeatPassword.setErrorEnabled(true);
                binding.tilRepeatPassword.setError(LanguageExtension.setText("enter_new_password_again", getString(R.string.enter_new_password_again)));
                binding.tilRepeatPassword.requestFocus();
            }
            if (pWord.isEmpty()) {
                binding.tilNewPassword.setErrorEnabled(true);
                binding.tilNewPassword.setError(LanguageExtension.setText("enter_new_password", getString(R.string.enter_new_password)));
                binding.tilNewPassword.requestFocus();
            }
            if (o.isEmpty()) {
                binding.otpView.showError();
                binding.otpView.requestFocusOTP();
            }
        } else if (!pWord.equals(rptPword)) {
            binding.tilRepeatPassword.setErrorEnabled(true);
            binding.tilRepeatPassword.setError(LanguageExtension.setText("couldn_t_match_with_new_password", getString(R.string.couldn_t_match_with_new_password)));
            binding.tilRepeatPassword.requestFocus();
        } else {
            resetPassword(o, pWord);
        }
    }

    private void resetPassword(String otp, String password) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("resetting_password_progress", getString(R.string.resetting_password_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("resetting_password_progress", getString(R.string.resetting_password_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + RESET_PASSWORD_API)
                .addBodyParameter("user_email", email)
                .addBodyParameter("user_otp", otp)
                .addBodyParameter("new_password", password)
                .addBodyParameter("new_repeat_password", password)
                .setTag("reset-password-api")
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
                                dismiss();
                                getParentFragmentManager().setFragmentResult(requestKey, new Bundle());
//                                sendResult(RESULT_SUCCESS_RESET_PASSWORD);
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

    private void resendOtp() {
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
                .setTag("resend-otp-api")
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
                                binding.otpView.resetState();
                                startTimer();
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

    private void startTimer() {
        binding.btnResend.setEnabled(false);
        new CountDownTimer(initialTimeInMilliSeconds, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInSeconds = millisUntilFinished / 1000;

                String min = new DecimalFormat("00").format(timeLeftInSeconds / 60);
                String sec = new DecimalFormat("00").format(timeLeftInSeconds % 60);

                String t = LanguageExtension.setText("request_new_code_in", getString(R.string.request_new_code_in)) + min + ":" + sec;

                binding.tvResendTime.setText(t);
            }

            @Override
            public void onFinish() {
                binding.btnResend.setEnabled(true);
//                otpTimer.setVisibility(View.GONE);
            }
        }.start();
    }

//    private void sendResult(int resultCode) {
//        getParentFragmentManager().setFragmentResult(requestKey, new Bundle());
//        if (getTargetFragment() != null)
//            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
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
}
