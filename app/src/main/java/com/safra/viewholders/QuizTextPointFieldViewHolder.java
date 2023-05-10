package com.safra.viewholders;

import android.app.Activity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.R;
import com.safra.databinding.FormElementQuizTextPointBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.HandlerClickListener;
import com.safra.models.formElements.BaseFormElement;

public class QuizTextPointFieldViewHolder extends BaseFieldViewHolder {

    FormElementQuizTextPointBinding binding;

    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public QuizTextPointFieldViewHolder(@NonNull FormElementQuizTextPointBinding binding, HandlerClickListener handleListener,
                                        boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            binding.ivProperties.setVisibility(View.GONE);
        } else
            binding.ivProperties.setVisibility(View.VISIBLE);
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        String l = baseFormElement.getFieldLabel();
        if (baseFormElement.isRequired()) {
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.tvLabel.setText(Html.fromHtml(l));

        if (baseFormElement.getFieldValue() != null)
        binding.etField.setText(Html.fromHtml(baseFormElement.getFieldValue()));

        binding.etField.setEnabled(!isReadOnly);
        if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0) {
            binding.etField.setText(baseFormElement.getUserData().get(0));
        }

        if (baseFormElement.isHaveError()) {
            if (binding.etField.getText() == null || binding.etField.getText().toString().isEmpty()) {
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
                if (isPreview) {
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
                if (isPreview) {
                    if (baseFormElement.isRequired()) {
                        if (s.toString().isEmpty()) {
                            binding.tilField.setErrorEnabled(true);
                            binding.tilField.setError(LanguageExtension.setText("please_fill_this_field",
                                    context.getString(R.string.please_fill_this_field)));
                            baseFormElement.setHaveError(true);
                        } else {
                            binding.tilField.setErrorEnabled(false);
                            baseFormElement.setHaveError(false);

                            try {
                                int number = Integer.parseInt(s.toString());

                                if (!baseFormElement.getMin().isEmpty()) {
                                    int mn = Integer.parseInt(baseFormElement.getMin());
                                    if (number < mn) {
                                        baseFormElement.setHaveError(true);
                                        binding.tilField.setErrorEnabled(true);
                                        binding.tilField.setError("Enter number >= " + mn);
                                    } else {
                                        baseFormElement.setHaveError(false);
                                        binding.tilField.setErrorEnabled(false);
                                    }
                                }

                                if (!baseFormElement.isHaveError() && !baseFormElement.getMax().isEmpty()) {
                                    int mx = Integer.parseInt(baseFormElement.getMax());
                                    if (number > mx) {
                                        baseFormElement.setHaveError(true);
                                        binding.tilField.setErrorEnabled(true);
                                        binding.tilField.setError("Enter number <= " + mx);
                                    } else {
                                        baseFormElement.setHaveError(false);
                                        binding.tilField.setErrorEnabled(false);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                binding.tilField.setErrorEnabled(true);
                                binding.tilField.setError("Invalid number");
                            }
                        }
                    }
                }
            }
        });

        binding.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), getAbsoluteAdapterPosition()));
    }
}
