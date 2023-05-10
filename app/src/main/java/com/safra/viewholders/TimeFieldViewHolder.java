package com.safra.viewholders;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.R;
import com.safra.databinding.FormElementTimeBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.ReloadListener;
import com.safra.models.formElements.BaseFormElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.safra.utilities.Common.TIME_FORMAT;

public class TimeFieldViewHolder extends BaseFieldViewHolder {

    FormElementTimeBinding binding;

    private TimePickerDialog timePickerDialog;
    private Calendar currentTime;
    private SimpleDateFormat sdfTime;

    private final ReloadListener reloadListener;
    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public TimeFieldViewHolder(@NonNull FormElementTimeBinding binding, ReloadListener reloadListener,
                               HandlerClickListener handleListener, boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        this.reloadListener = reloadListener;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if(isPreview) {
            this.binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            this.binding.mcvTimeElement.setCardElevation(0f);
        } else
            this.binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        sdfTime = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        currentTime = Calendar.getInstance();
        if (baseFormElement.getFieldValue() != null) {
            try {
                currentTime.setTime(Objects.requireNonNull(sdfTime.parse(baseFormElement.getFieldValue())));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("TimeField", "bind: " + e.getLocalizedMessage());
            }
        }

        String l = baseFormElement.getFieldLabel();
        if(baseFormElement.isRequired()){
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.tvLabel.setText(Html.fromHtml(l));

        if (baseFormElement.getFieldValue() != null)
        binding.etField.setText(Html.fromHtml(baseFormElement.getFieldValue()));

        binding.etField.setFocusableInTouchMode(false);

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

        timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    currentTime.set(Calendar.MINUTE, minute);

                    String currentValue = baseFormElement.getFieldValue();
                    String newValue = sdfTime.format(currentTime.getTime());

                    // trigger event only if the value is changed
                    if (currentValue != null) {
                        if (!currentValue.equals(newValue)) {
                            reloadListener.updateValue(newValue, getAbsoluteAdapterPosition());
                        }
                    } else {
                        reloadListener.updateValue(newValue, getAbsoluteAdapterPosition());
                    }
                },
                currentTime.get(Calendar.HOUR),
                currentTime.get(Calendar.MINUTE),
                false);

        binding.etField.setOnClickListener(v -> timePickerDialog.show());

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
