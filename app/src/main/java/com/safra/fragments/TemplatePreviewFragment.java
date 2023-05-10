package com.safra.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.safra.R;
import com.safra.databinding.DialogTemplatePreviewBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.utilities.TextDrawable;

public class TemplatePreviewFragment extends DialogFragment {

    public static final String TAG = "template_preview_dialog";

    private FragmentActivity mActivity = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogTemplatePreviewBinding binding = DialogTemplatePreviewBinding.inflate(inflater, container, false);

        if (getDialog() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (getArguments() != null)
            Glide.with(mActivity)
                    .load(getArguments().getString("image_url"))
                    .placeholder(new TextDrawable(LanguageExtension.setText("loading_progress", getString(R.string.loading_progress))))
                    .error(new TextDrawable(LanguageExtension.setText("no_preview_found", getString(R.string.no_preview_found))))
                    .into(binding.ivPreview);

        binding.tvClose.setOnClickListener(v -> dismiss());

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
