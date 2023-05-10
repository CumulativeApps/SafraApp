package com.safra.viewholders;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.adapters.SpinnerRecyclerAdapter;
import com.safra.databinding.DialogSpinnerBinding;
import com.safra.databinding.FormElementCascadingSelectBinding;
import com.safra.extensions.GeneralExtension;
import com.safra.interfaces.CascadeValueSelectorListener;
import com.safra.interfaces.HandlerClickListener;
import com.safra.models.CascadeOptionItem;
import com.safra.models.OptionItem;
import com.safra.models.formElements.BaseFormElement;

import java.util.ArrayList;
import java.util.List;

public class CascadingSelectFieldViewHolder extends BaseFieldViewHolder {

    public static final String TAG = "cascading_select_field_viewholder";

    FormElementCascadingSelectBinding binding;

    public SpinnerRecyclerAdapter adapter;

    public CascadeValueSelectorListener listener;
    public HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public CascadingSelectFieldViewHolder(@NonNull FormElementCascadingSelectBinding binding,
                                          CascadeValueSelectorListener listener,
                                          HandlerClickListener handleListener, boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = listener;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            binding.ivProperties.setVisibility(View.GONE);
            binding.mcvSelectElement.setCardElevation(0f);
        } else
            binding.ivProperties.setVisibility(View.VISIBLE);
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        String l = baseFormElement.getFieldLabel();
        if (baseFormElement.isRequired()) {
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.etLabel.setText(Html.fromHtml(l));

        if (baseFormElement.isHaveError()) {
            if (TextUtils.isEmpty(baseFormElement.getFieldValue())) {
                binding.ivError.setVisibility(View.VISIBLE);
            }
        } else {
            binding.ivError.setVisibility(View.GONE);
        }

        binding.spnSelect.setText("");
        Log.e("cascading_select", "bind: fieldValue -> " + baseFormElement.getFieldValue());
        List<CascadeOptionItem> options = new ArrayList<>(baseFormElement.getCascadeOptions());

        int level = GeneralExtension.findLevelFromClassName(baseFormElement.getClassName());


        for (CascadeOptionItem oi : options) {
            oi.setSelected(false);
            if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0
                    && baseFormElement.getUserData().contains(String.valueOf(oi.getId()))) {
                oi.setSelected(true);
                baseFormElement.setFieldValue(String.valueOf(oi.getId()));
                baseFormElement.getUserData().clear();
            } else if(baseFormElement.getFieldValue() != null
                    && baseFormElement.getFieldValue().equalsIgnoreCase(String.valueOf(oi.getId()))){
                oi.setSelected(true);
            }
        }

        for (CascadeOptionItem o : options) {
            if (o.isSelected()) {
                Log.e("cascading_select", "bind: selected -> " + o.getOption());
                binding.spnSelect.setText(o.getOption());
            }
        }
        binding.etLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                    binding.etLabel.setError(null);
            }
        });

        binding.spnSelect.setOnClickListener(v -> {
            if (isPreview && !isReadOnly)
                openSpinnerDialog(context, baseFormElement, options, level);
        });
//
        binding.ivProperties.setOnClickListener(v -> {
            if(binding.etLabel.isEnabled()){
                String lbl = binding.etLabel.getText() != null ? binding.etLabel.getText().toString() : "";
                if(lbl.isEmpty()){
                    binding.etLabel.setError("Please enter label");
                } else {
                    binding.etLabel.clearFocus();
                    binding.etLabel.setEnabled(false);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.etLabel.getWindowToken(), 0);
                    baseFormElement.setFieldLabel(lbl);
                }
            } else {
                binding.etLabel.setEnabled(true);
                binding.etLabel.requestFocus();
                binding.etLabel.setSelection(binding.etLabel.getText().length());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.etLabel, InputMethodManager.SHOW_IMPLICIT);
            }
//            handlerClickListener
//                    .openProperties(baseFormElement, getAbsoluteAdapterPosition());
        });

    }

    private void openSpinnerDialog(Context context, BaseFormElement baseFormElement, List<CascadeOptionItem> options,
                                   int level) {
        List<OptionItem> spinnerOptions = new ArrayList<>();
        for (CascadeOptionItem ci : options) {
            spinnerOptions.add(new OptionItem(ci.getOption(), String.valueOf(ci.getId()), ci.isSelected()));
        }

        DialogSpinnerBinding dialogBinding = DialogSpinnerBinding.inflate(LayoutInflater.from(context));
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(dialogBinding.getRoot());

//        final RecyclerView optionRecycler = view.findViewById(R.id.rvOptions);
        dialogBinding.rvOptions.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        optionRecycler.addItemDecoration(new LineHorizontalItemDecoration(this, R.dimen.recycler_bottom_offset, false));
        final AlertDialog alertDialog = alertDialogBuilder.create();

//        ImageView close = view.findViewById(R.id.ivClose);

        alertDialog.show();
        alertDialog.getWindow().setLayout(context.getResources().getDimensionPixelSize(R.dimen.dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogBinding.ivClose.setOnClickListener(v -> alertDialog.dismiss());

        adapter = new SpinnerRecyclerAdapter(context, spinnerOptions, false, true, (item, position) -> {
//            if (!isMultipleAllowed) {
            alertDialog.dismiss();
            binding.spnSelect.setText(item.getOptionKey());
            baseFormElement.setFieldValue(item.getOptionValue());
            listener.onSelected(Integer.parseInt(item.getOptionValue()), level, getAbsoluteAdapterPosition());

            for (CascadeOptionItem ci : options) {
                ci.setSelected(false);
            }
            options.get(position).setSelected(true);
//            } else {
//                StringBuilder sb = new StringBuilder();
//                if (adapter.getSelected().size() > 0) {
//                    for (OptionItem o : adapter.getSelected()) {
//                        if (sb.toString().isEmpty()) {
//                            sb.append(o.getOptionKey());
//                        } else {
//                            sb.append("\n").append(o.getOptionKey());
//                        }
//                    }
//                    selectSpinner.setText(sb.toString());
//                } else {
//                    selectSpinner.setText((placeHolder!=null&&!placeHolder.isEmpty()) ? placeHolder : "");
//                }
//            }
        });
        dialogBinding.rvOptions.setAdapter(adapter);
    }
}
