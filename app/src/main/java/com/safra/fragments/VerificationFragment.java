package com.safra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.databinding.FragmentVerificationBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.SEND_OTP_API;
import static com.safra.utilities.Common.VERIFY_OTP_API;

public class VerificationFragment extends DialogFragment {

    public static final String TAG = "verification_fragment";

    private FragmentActivity mActivity = null;

    private FragmentVerificationBinding binding;

    private final long initialTimeInMilliSeconds = 2 * 60000L;
    private long timeLeftInSeconds;

    private String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerificationBinding.inflate(inflater, container, false);

        binding.tvVerificationTitle.setText(LanguageExtension.setText("enter_4_digit_code_sent_to", getString(R.string.enter_4_digit_code_sent_to)));
        binding.tvVerificationSubtitle.setText(LanguageExtension.setText("we_ve_sent_a_4_digit_code_to_your_email_address_please_enter_the_verification_code", getString(R.string.we_ve_sent_a_4_digit_code_to_your_email_address_please_enter_the_verification_code)));
        binding.tvResendTitle.setText(LanguageExtension.setText("didn_t_receive_the_sms", getString(R.string.didn_t_receive_the_sms)));

        if (getArguments() != null) {
            email = getArguments().getString("email_id");
            binding.tvVerificationEmail.setText(email);
        }

        startTimer();

        binding.btnResend.setOnClickListener(v -> resendOtp());

        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                verifyOtp(otp);
            }
        });

        binding.ivClose.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    private void verifyOtp(String otp) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("verifying_progress", getString(R.string.verifying_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("verifying_progress", getString(R.string.verifying_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + VERIFY_OTP_API)
                .addBodyParameter("user_email", email)
                .addBodyParameter("user_otp", otp)
                .setTag("verify-otp-api")
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
                                VerifiedFragment dialogV = new VerifiedFragment();
                                dialogV.show(mActivity.getSupportFragmentManager(), VerifiedFragment.TAG);
                            } else {
                                binding.otpView.showError();
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

                String t = getString(R.string.request_new_code_in) + min + ":" + sec;

                binding.tvResendTime.setText(t);
            }

            @Override
            public void onFinish() {
                binding.btnResend.setEnabled(true);
//                otpTimer.setVisibility(View.GONE);
            }
        }.start();
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
