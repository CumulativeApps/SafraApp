package com.safra.viewholders;

import android.Manifest;
import android.app.Activity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.safra.R;
import com.safra.databinding.FormElementLocationBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.ReloadListener;
import com.safra.interfaces.RequestLocationInterface;
import com.safra.models.formElements.BaseFormElement;

import java.util.List;

public class LocationFieldViewHolder extends BaseFieldViewHolder {

    FormElementLocationBinding binding;

    private final ReloadListener listener;
    private final HandlerClickListener handlerClickListener;
    private final RequestLocationInterface locationListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public LocationFieldViewHolder(@NonNull FormElementLocationBinding binding, ReloadListener reloadListener,
                                   HandlerClickListener handleListener,
                                   boolean isPreview, boolean isReadOnly,
                                   RequestLocationInterface locationListener) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = reloadListener;
        handlerClickListener = handleListener;
        this.locationListener = locationListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            binding.mcvLocationElement.setCardElevation(0f);
        } else
            binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
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

        binding.etField.setOnClickListener(v -> {
            if (locationListener != null) {
                Dexter.withContext(context)
                        .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    locationListener.requestLocation(baseFormElement, getAbsoluteAdapterPosition());
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
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
