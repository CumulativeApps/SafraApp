package com.safra.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.safra.R;
import com.safra.databinding.DialogChooseForNewFormBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.PermissionExtension;

import static com.safra.utilities.UserPermissions.TEMPLATE_USE;

public class ChooserForNewFormDialog extends DialogFragment {

    public static final String TAG = "choose_new_form_dialog";

    private String requestKey;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogChooseForNewFormBinding binding = DialogChooseForNewFormBinding.inflate(inflater, container, false);

        binding.tvDialogTitle.setText(LanguageExtension.setText("create_new_form", getString(R.string.create_new_form)));
        binding.tvCreateNewTitle.setText(LanguageExtension.setText("create_from_scratch", getString(R.string.create_from_scratch)));
        binding.tvCreateNewSubtitle.setText(LanguageExtension.setText("a_blank_slate_to_create_your_own_form", getString(R.string.a_blank_slate_to_create_your_own_form)));
        binding.tvUseTemplateTitle.setText(LanguageExtension.setText("use_template", getString(R.string.use_template)));
        binding.tvUseTemplateSubtitle.setText(LanguageExtension.setText("choose_from_pre_made_forms", getString(R.string.choose_from_pre_made_forms)));

        if(!PermissionExtension.checkForPermission(TEMPLATE_USE))
            binding.clUseTemplate.setVisibility(View.GONE);
        else
            binding.clUseTemplate.setVisibility(View.VISIBLE);

        if(getArguments() != null){
            requestKey = getArguments().getString("request_key");
        }

        binding.clCreateNew.setOnClickListener(v -> {
            sendResult(false);
            dismiss();
        });

        binding.clUseTemplate.setOnClickListener(v -> {
            sendResult(true);
            dismiss();
        });


        return binding.getRoot();
    }

    private void sendResult(boolean useTemplate) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("use_template", useTemplate);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
//        if (getTargetFragment() != null)
//            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_ADD_FORM_OPTION, i);
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
