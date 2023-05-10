package com.safra.viewholders;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.safra.BuildConfig;
import com.safra.R;
import com.safra.WebActivity;
import com.safra.databinding.FormElementFileBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.FileSelectionInterface;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.ReloadListener;
import com.safra.models.formElements.BaseFormElement;

import java.io.File;

public class FileFieldViewHolder extends BaseFieldViewHolder {

    FormElementFileBinding binding;

    private final ReloadListener reloadListener;
    private final HandlerClickListener handlerClickListener;
    private final FileSelectionInterface fileSelector;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public FileFieldViewHolder(@NonNull FormElementFileBinding binding, ReloadListener reloadListener,
                               HandlerClickListener handleListener,
                               boolean isPreview, boolean isReadOnly,
                               FileSelectionInterface fileSelector) {
        super(binding.getRoot());

        this.binding = binding;
        this.reloadListener = reloadListener;
        handlerClickListener = handleListener;
        this.fileSelector = fileSelector;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            binding.mcvFileElement.setCardElevation(0f);
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

            Log.e("FileFieldViewHolder", "bind: " + baseFormElement.getUserData().size());

            binding.etField.setText(baseFormElement.getUserData().get(0).substring(baseFormElement.getUserData().get(0).lastIndexOf("/") + 1));
        }

        binding.btnViewFile.setVisibility(isReadOnly ? View.VISIBLE : View.GONE);

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
            if (fileSelector != null)
                fileSelector.selectFileFor(getAbsoluteAdapterPosition(), baseFormElement);
        });

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.btnViewFile.setOnClickListener(v -> {
            if (baseFormElement.getUserData().size() > 0) {
                if (baseFormElement.getUserData().get(0).toLowerCase().contains("http://")
                        || baseFormElement.getUserData().get(0).toLowerCase().contains("https://")) {

                    Intent i = new Intent(context, WebActivity.class);
                    i.putExtra("heading", baseFormElement.getUserData().get(0).substring(baseFormElement.getUserData().get(0).lastIndexOf("/") + 1));
                    i.putExtra("url", baseFormElement.getUserData().get(0));
                    context.startActivity(i);

//                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                    CustomTabColorSchemeParams.Builder customTabColorSchemeParamsBuilder =
//                            new CustomTabColorSchemeParams.Builder();
//                    customTabColorSchemeParamsBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary));
//                    customTabColorSchemeParamsBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.white));
//                    builder.setDefaultColorSchemeParams(customTabColorSchemeParamsBuilder.build());
//                    builder.setShowTitle(false);
//                    builder.setUrlBarHidingEnabled(true);
//                    CustomTabsIntent intent = builder.build();
//                    intent.launchUrl(context, Uri.parse(baseFormElement.getUserData().get(0)));
                } else {
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(baseFormElement.getUserData().get(0)));
                    String mime = context.getContentResolver().getType(uri);
                    Log.e("FileFieldViewHolder", "bind: " + mime);

                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    newIntent.setDataAndType(uri, mime);
                    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(Intent.createChooser(newIntent, "Open file with"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                    }
                }
//                intent.launchUrl(context, Uri.parse("https://safra.co.mz"));
            }
        });
    }

    private String getFileExtension(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}
