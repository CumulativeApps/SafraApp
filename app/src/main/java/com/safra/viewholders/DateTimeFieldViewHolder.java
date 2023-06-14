package com.safra.viewholders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.R;
import com.safra.databinding.FormElementWeekBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.ReloadListener;
import com.safra.models.formElements.BaseFormElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DateTimeFieldViewHolder extends BaseFieldViewHolder {

    FormElementWeekBinding binding;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Calendar currentDateTime;
    private SimpleDateFormat sdfDateTime;

    private final ReloadListener reloadListener;
    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public DateTimeFieldViewHolder(@NonNull FormElementWeekBinding binding, ReloadListener reloadListener,
                                   HandlerClickListener handleListener, boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        this.reloadListener = reloadListener;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            binding.mcvWeekElement.setCardElevation(0f);
        } else
            binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        currentDateTime = Calendar.getInstance();
        if (baseFormElement.getFieldValue() != null) {
            try {
                String fieldValue = baseFormElement.getFieldValue();
                currentDateTime.setTime(Objects.requireNonNull(sdfDateTime.parse(fieldValue)));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("DateField", "bind: " + e.getLocalizedMessage());
            }
        }

        String l = baseFormElement.getFieldLabel();
        if (baseFormElement.isRequired()) {
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.tvLabel.setText(Html.fromHtml(l));

        if (baseFormElement.getFieldValue() != null)
            binding.etField.setText(Html.fromHtml(sdfDateTime.format(currentDateTime.getTime())));

        binding.etField.setFocusableInTouchMode(false);

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
                if (isPreview && baseFormElement.isRequired()) {
                    if (s.toString().isEmpty()) {
                        binding.tilField.setErrorEnabled(true);
                        binding.tilField.setError("Please fill this field");
                        baseFormElement.setHaveError(true);
                    } else {
                        binding.tilField.setErrorEnabled(false);
                        baseFormElement.setHaveError(false);
                    }
                }
            }
        });

        datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    currentDateTime.set(Calendar.YEAR, year);
                    currentDateTime.set(Calendar.MONTH, month);
                    currentDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.etField.setText(Html.fromHtml(sdfDateTime.format(currentDateTime.getTime())));
                },
                currentDateTime.get(Calendar.YEAR),
                currentDateTime.get(Calendar.MONTH),
                currentDateTime.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    currentDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    currentDateTime.set(Calendar.MINUTE, minute);
                    binding.etField.setText(Html.fromHtml(sdfDateTime.format(currentDateTime.getTime())));
                },
                currentDateTime.get(Calendar.HOUR_OF_DAY),
                currentDateTime.get(Calendar.MINUTE),
                false
        );

        binding.etField.setOnClickListener(v -> {
            datePickerDialog.show();
            timePickerDialog.show();
        });

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
