package com.safra.viewholders;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.adapters.CheckboxRecyclerAdapter;
import com.safra.databinding.FormElementCheckboxGroupBinding;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.ReloadListener;
import com.safra.models.OptionItem;
import com.safra.models.formElements.BaseFormElement;

import java.util.List;

public class CheckboxFieldViewHolder extends BaseFieldViewHolder {

    FormElementCheckboxGroupBinding binding;

    public ReloadListener listener;
    public HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    public CheckboxFieldViewHolder(@NonNull FormElementCheckboxGroupBinding binding, ReloadListener listener,
                                   HandlerClickListener handleListener,
                                   boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = listener;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if(isPreview) {
            binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            binding.mcvCheckboxGroupElement.setCardElevation(0f);
        } else
            binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        String l = baseFormElement.getFieldLabel();
        if(baseFormElement.isRequired()){
            l = l + context.getString(R.string.mandatory_field);
        }
        binding.tvLabel.setText(Html.fromHtml(l));

        if(baseFormElement.isHaveError()){
            if(TextUtils.isEmpty(baseFormElement.getFieldValue())){
                binding.ivError.setVisibility(View.VISIBLE);
            }
        } else {
            binding.ivError.setVisibility(View.GONE);
        }

        List<OptionItem> options = baseFormElement.getOptions();

        if(baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0){
            for(OptionItem oi : options){
                oi.setSelected(false);
                if(baseFormElement.getUserData().contains(oi.getOptionValue())){
                    oi.setSelected(true);
                }
            }
        }

        binding.rvOptions.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        CheckboxRecyclerAdapter adapter = new CheckboxRecyclerAdapter(options, !isReadOnly,
                (item, position1) -> {
                    if(item.isSelected())
                        listener.updateValue(item.getOptionKey(), getAbsoluteAdapterPosition());
                    else
                        listener.updateValue("", getAbsoluteAdapterPosition());
        });
        binding.rvOptions.setAdapter(adapter);

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
