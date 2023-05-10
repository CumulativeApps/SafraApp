package com.safra.viewholders;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import com.safra.R;
import com.safra.databinding.FormElementHeaderBinding;
import com.safra.interfaces.HandlerClickListener;
import com.safra.models.formElements.BaseFormElement;

public class HeaderFieldViewHolder extends BaseFieldViewHolder {

    public static final String TAG = "header_field_viewholder";

    FormElementHeaderBinding binding;

    private final HandlerClickListener handlerClickListener;

    public HeaderFieldViewHolder(@NonNull FormElementHeaderBinding binding, HandlerClickListener handleListener,
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
        Log.e(TAG, "bind: binding");
        String l = baseFormElement.getFieldLabel();
//        switch (baseFormElement.getFieldSubType()) {
//            case "h1":
//                binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
//                break;
//            case "h2":
//                binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
//                break;
//            case "h3":
//                binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
//                break;
//            case "h4":
//                binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);
//                break;
//            case "h5":
//                binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
//                break;
//            case "h6":
//                binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
//                break;
//        }
//        if (baseFormElement.isRequired()) {
//            l = l + context.getString(R.string.mandatory_field);
//        }
        binding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        binding.tvLabel.setText(Html.fromHtml(l));

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
