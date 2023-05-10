package com.safra.fragments;

import android.content.Context;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.databinding.FragmentForgotPasswordBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;

import org.json.JSONException;
import org.json.JSONObject;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.REQUEST_RESET_PASSWORD;
import static com.safra.utilities.Common.SEND_OTP_API;
import static com.safra.utilities.LanguageManager.languageManager;

public class ForgotPasswordFragment extends DialogFragment {

    public static final String TAG = "forgot_pword_fragment";

    private FragmentForgotPasswordBinding binding;

    private FragmentActivity mActivity = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);

        binding.ivAppLogo.setImageResource(languageManager.getLanguage() == 2 ? R.drawable.pt_app_logo : R.drawable.app_logo);
        binding.tvForgotPasswordTitle.setText(LanguageExtension.setText("forgot_your_password", getString(R.string.forgot_your_password)));
        binding.tvForgotPasswordSubtitle.setText(LanguageExtension.setText("enter_your_email_address_and_we_will_send_you_an_otp_to_reset_your_password", getString(R.string.enter_your_email_address_and_we_will_send_you_an_otp_to_reset_your_password)));
        binding.tilEmail.setHint(LanguageExtension.setText("email_address", getString(R.string.email_address)));
        binding.btnSend.setText(LanguageExtension.setText("send_email", getString(R.string.send_email)));

        binding.btnSend.setOnClickListener(v -> {
            String e = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
            if (e.isEmpty()) {
                binding.tilEmail.setErrorEnabled(true);
                binding.tilEmail.setError(LanguageExtension.setText("enter_email", getString(R.string.enter_email)));
                binding.tilEmail.requestFocus();
            } else {
                sendOTP(e);
            }
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.flContainer, new CheckMailFragment(), CheckMailFragment.TAG)
//                    .addToBackStack(CheckMailFragment.TAG)
//                    .commit();
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
                if (!s.toString().isEmpty()) {
                    binding.tilEmail.setErrorEnabled(false);
                }
            }
        });

        binding.ivClose.setOnClickListener(v -> dismiss());

        getChildFragmentManager().setFragmentResultListener(REQUEST_RESET_PASSWORD, this,
                (requestKey, result) -> {
                    if (requestKey.equalsIgnoreCase(REQUEST_RESET_PASSWORD))
                        dismiss();
                });

        return binding.getRoot();
    }

    private void sendOTP(String email) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("sending_progress", getString(R.string.sending_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("sending_progress", getString(R.string.sending_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + SEND_OTP_API)
                .addBodyParameter("user_email", email)
                .setTag("forgot-password-api")
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
                                ResetPasswordFragment dialogR = new ResetPasswordFragment();
                                Bundle b = new Bundle();
                                b.putString("request_key", REQUEST_RESET_PASSWORD);
                                b.putString("email_id", email);
                                dialogR.setArguments(b);
                                dialogR.setCancelable(false);
//                                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//                                dialogR.setTargetFragment(ForgotPasswordFragment.this, REQUEST_RESET_PASSWORD);
                                dialogR.show(getChildFragmentManager(), ResetPasswordFragment.TAG);
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQUEST_RESET_PASSWORD && resultCode == RESULT_SUCCESS_RESET_PASSWORD)
//            dismiss();
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
