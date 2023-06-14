package com.safra.viewholders;

import android.app.Activity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.R;
import com.safra.databinding.FormElementCheckboxBinding;
import com.safra.databinding.FormElementCheckboxGroupBinding;
import com.safra.databinding.FormElementTextBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.HandlerClickListener;
import com.safra.models.formElements.BaseFormElement;

import org.jsoup.Jsoup;

public class CheckBoxFieldViewHolder extends BaseFieldViewHolder {

    FormElementCheckboxBinding binding;

    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public  CheckBoxFieldViewHolder(@NonNull FormElementCheckboxBinding binding, HandlerClickListener handleListener,
                               boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            this.binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            this.binding.mcvCheckBoxElement.setCardElevation(0f);
        } else {
            this.binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        String l = baseFormElement.getFieldLabel();
        if (baseFormElement.isRequired()) {
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.tvLabel.setText(Html.fromHtml(l));

        binding.checkboxField.setChecked(baseFormElement.getFieldValue() != null && Boolean.parseBoolean(baseFormElement.getFieldValue()));

        binding.checkboxField.setEnabled(!isReadOnly);

        if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0) {
            binding.checkboxField.setChecked(Boolean.parseBoolean(baseFormElement.getUserData().get(0)));
        }

        if (baseFormElement.isHaveError()) {
            if (!binding.checkboxField.isChecked()) {
//                binding.tvLabel.setErrorEnabled(true);
                binding.tvLabel.setError(LanguageExtension.setText("please_fill_this_field",
                        context.getString(R.string.please_fill_this_field)));
            }
        }

        binding.checkboxField.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isPreview) {
                baseFormElement.setFieldValue(String.valueOf(isChecked));
                Log.e("TextFieldViewHolder", "onCheckedChanged: " + baseFormElement.getFieldValue());
            }
        });

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
