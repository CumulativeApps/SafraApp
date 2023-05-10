package com.safra.viewholders;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.adapters.SpinnerRecyclerAdapter;
import com.safra.databinding.DialogSpinnerBinding;
import com.safra.databinding.FormElementSelectBinding;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.ReloadListener;
import com.safra.models.OptionItem;
import com.safra.models.formElements.BaseFormElement;

import java.util.ArrayList;
import java.util.List;

public class SelectFieldViewHolder extends BaseFieldViewHolder {

    public static final String TAG = "select_field_viewholder";

    FormElementSelectBinding binding;

    public SpinnerRecyclerAdapter adapter;

    public ReloadListener listener;
    public HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public SelectFieldViewHolder(@NonNull FormElementSelectBinding binding, ReloadListener listener,
                                 HandlerClickListener handleListener, boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = listener;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            binding.mcvSelectElement.setCardElevation(0f);
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

        if (baseFormElement.isHaveError()) {
            if (TextUtils.isEmpty(baseFormElement.getFieldValue())) {
                binding.ivError.setVisibility(View.VISIBLE);
            }
        } else {
            binding.ivError.setVisibility(View.GONE);
        }

        List<OptionItem> options = new ArrayList<>(baseFormElement.getOptions());

        if(baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0){
            for(OptionItem oi : options){
                oi.setSelected(false);
                if(baseFormElement.getUserData().contains(oi.getOptionValue()))
                    oi.setSelected(true);
            }
        }

//        if (baseFormElement.getFieldPlaceholder() != null && !baseFormElement.getFieldPlaceholder().isEmpty()) {
//            selectSpinner.setText(baseFormElement.getFieldPlaceholder());
//            options.add(0, new OptionItem(baseFormElement.getFieldPlaceholder(), "", false));
//        }

//        if(!baseFormElement.isMultipleAllowed()){
        for (OptionItem o : options) {
            if (o.isSelected())
                binding.spnSelect.setText(o.getOptionKey());
        }
//        }else{
//            StringBuilder sb = new StringBuilder();
//            for (OptionItem o : options) {
//                if(o.isSelected()) {
//                    if (sb.toString().isEmpty()) {
//                        sb.append(o.getOptionKey());
//                    } else {
//                        sb.append("\n").append(o.getOptionKey());
//                    }
//                }
//            }
//            if(!sb.toString().isEmpty())
//                selectSpinner.setText(sb.toString());
//        }
        binding.spnSelect.setOnClickListener(v -> {
                    if (!isReadOnly)
                        openSpinnerDialog(context, options, false);
                }
        );

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));

    }

    private void openSpinnerDialog(Context context, List<OptionItem> options, boolean isMultipleAllowed) {
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

        adapter = new SpinnerRecyclerAdapter(context, options, isMultipleAllowed, true, (item, position) -> {
//            if (!isMultipleAllowed) {
            alertDialog.dismiss();
            binding.spnSelect.setText(item.getOptionKey());
            listener.updateValue(item.getOptionValue(), getAbsoluteAdapterPosition());
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
