package com.safra.viewholders;

import android.app.Activity;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.R;
import com.safra.databinding.FormElementTextareaBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.HandlerClickListener;
import com.safra.models.formElements.BaseFormElement;

public class TextAreaFieldViewHolder extends BaseFieldViewHolder {

    FormElementTextareaBinding binding;

    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public TextAreaFieldViewHolder(@NonNull FormElementTextareaBinding binding, HandlerClickListener handleListener,
                                   boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if(isPreview) {
            this.binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            this.binding.mcvTextAreaElement.setCardElevation(0f);
        } else
            this.binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        String l = baseFormElement.getFieldLabel();
        if(baseFormElement.isRequired()){
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.tvLabel.setText(Html.fromHtml(l));

        if (baseFormElement.getFieldValue() != null)
        binding.etField.setText(Html.fromHtml(baseFormElement.getFieldValue()));

        binding.etField.setEnabled(!isReadOnly);

        if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0) {
            binding.etField.setText(baseFormElement.getUserData().get(0));
        }

        if(baseFormElement.isHaveError()){
            if(binding.etField.getText() == null || binding.etField.getText().toString().isEmpty()){
                binding.tilField.setErrorEnabled(true);
                binding.tilField.setError(LanguageExtension.setText("please_fill_this_field",
                        context.getString(R.string.please_fill_this_field)));
            }
        }

        binding.etField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isPreview) {
                    String currentValue = baseFormElement.getFieldValue();
                    String newValue = s.toString();

                    if (currentValue != null) {
                        if (!currentValue.equals(newValue)) {
                            baseFormElement.setFieldValue(newValue);

                            Log.e("TextFieldViewHolder", "onTextChanged: " + baseFormElement.getFieldValue());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isPreview && baseFormElement.isRequired()) {
                    if (s.toString().isEmpty()) {
                        binding.tilField.setErrorEnabled(true);
                        binding.tilField.setError(LanguageExtension.setText("please_fill_this_field",
                                context.getString(R.string.please_fill_this_field)));
                        baseFormElement.setHaveError(true);
                    } else {
                        binding.tilField.setErrorEnabled(false);
                        baseFormElement.setHaveError(false);
                    }
                }
            }
        });

        if (baseFormElement.getRows() > 0)
            binding.etField.setLines(baseFormElement.getRows());
        else
            binding.etField.setLines(3);

        if(baseFormElement.getMaxLength() > 0)
            binding.etField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(baseFormElement.getMaxLength())});

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
