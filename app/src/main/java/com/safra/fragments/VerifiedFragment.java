package com.safra.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.safra.Dashboard;
import com.safra.R;
import com.safra.databinding.FragmentVerifiedBinding;
import com.safra.extensions.LanguageExtension;

public class VerifiedFragment extends DialogFragment {

    public static final String TAG = "verified_fragment";

    private FragmentActivity mActivity = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentVerifiedBinding binding = FragmentVerifiedBinding.inflate(inflater, container, false);

        binding.tvVerifiedTitle.setText(LanguageExtension.setText("verified", getString(R.string.verified)));
        binding.tvVerifiedSubtitle.setText(LanguageExtension.setText("congratulations_your_phone_number_has_been_verified_your_input_has_been_accepted", getString(R.string.congratulations_your_phone_number_has_been_verified_your_input_has_been_accepted)));
        binding.btnContinue.setText(LanguageExtension.setText("verified_continue", getString(R.string.verified_continue)));

        binding.btnContinue.setOnClickListener(v -> {
            Intent dashboardIntent = new Intent(mActivity, Dashboard.class);
            startActivity(dashboardIntent);
            mActivity.finish();
        });

        return binding.getRoot();
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
