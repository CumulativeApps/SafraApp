package com.safra.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.safra.R;
import com.safra.databinding.DialogPackageDetailBinding;
import com.safra.extensions.LanguageExtension;

public class PackageDetailDialog extends DialogFragment {

    public static final String TAG = "package_detail_dialog";

    private DialogPackageDetailBinding binding;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogPackageDetailBinding.inflate(inflater, container, false);

        binding.tvPackageTitle.setText(LanguageExtension.setText("membership_plan", getString(R.string.membership_plan)));
        binding.tvDescriptionTitle.setText(LanguageExtension.setText("description", getString(R.string.description)));
        binding.tvTermsConditionsTitle.setText(LanguageExtension.setText("terms_and_conditions", getString(R.string.terms_and_conditions)));
        binding.tvContactTitle.setText(LanguageExtension.setText("contact_us", getString(R.string.contact_us)));

        if (getArguments() != null) {
            binding.tvPackageName.setText(getArguments().getString("plan_name"));
            binding.tvExpiryDate.setText(getArguments().getString("plan_expiry"));
            if(!getArguments().getString("description").equals("")) {
                binding.tvDescription.setText(getArguments().getString("description"));
            } else {
                binding.viewLine1.setVisibility(View.GONE);
                binding.clDescription.setVisibility(View.GONE);
            }
            if(!getArguments().getString("terms").equals("")) {
                binding.tvTermsConditions.setText(getArguments().getString("terms"));
            } else {
                binding.viewLine2.setVisibility(View.GONE);
                binding.clTermsConditions.setVisibility(View.GONE);
            }
            binding.tvContact.setText(getArguments().getString("contact_phone"));
            binding.tvEmail.setText(getArguments().getString("contact_email"));
        }

        binding.ivClose.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
