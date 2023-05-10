package com.safra.viewholders;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.databinding.FormElementHeaderBinding;
import com.safra.databinding.FormElementSeparatorBinding;
import com.safra.interfaces.HandlerClickListener;
import com.safra.models.formElements.BaseFormElement;

public class SeparatorFieldViewHolder extends BaseFieldViewHolder {

    FormElementSeparatorBinding binding;

    private final HandlerClickListener handlerClickListener;

    public SeparatorFieldViewHolder(@NonNull FormElementSeparatorBinding binding, HandlerClickListener handleListener,
                                    boolean isPreview) {
        super(binding.getRoot());

        this.binding = binding;
        handlerClickListener = handleListener;

        if (isPreview) {
            binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            binding.mcvHeaderElement.setCardElevation(0f);
        } else {
            binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        if(baseFormElement.getFieldLabel() != null) {
            String l = baseFormElement.getFieldLabel();
            binding.tvLabel.setText(Html.fromHtml(l));
            binding.tvLabel.setVisibility(View.VISIBLE);
        } else {
            binding.tvLabel.setVisibility(View.GONE);
        }

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
