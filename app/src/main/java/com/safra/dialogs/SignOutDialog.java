package com.safra.dialogs;

import android.content.Context;
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

import com.safra.R;
import com.safra.databinding.DialogSignOutBinding;
import com.safra.extensions.Extension;
import com.safra.extensions.LanguageExtension;

public class SignOutDialog extends DialogFragment {

    public static final String TAG = "sign_out_dialog";

    private FragmentActivity mActivity = null;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DialogSignOutBinding binding = DialogSignOutBinding.inflate(inflater, container, false);

        binding.tvDialogTitle.setText(LanguageExtension.setText("sign_out", getString(R.string.sign_out)));
        binding.tvSignOutMessage.setText(LanguageExtension.setText("are_you_sure_you_want_to_sign_out", getString(R.string.are_you_sure_you_want_to_sign_out)));
        binding.btnCancel.setText(LanguageExtension.setText("cancel", getString(R.string.cancel)));
        binding.btnSignOut.setText(LanguageExtension.setText("sign_out", getString(R.string.sign_out)));

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSignOut.setOnClickListener(v -> {
//            signOutUser();
            dismiss();
            Extension.signOutUser(mActivity);
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
