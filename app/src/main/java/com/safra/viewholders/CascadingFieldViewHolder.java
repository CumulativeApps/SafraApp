package com.safra.viewholders;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.adapters.CascadingAdapter;
import com.safra.databinding.FormElementCascadingBinding;
import com.safra.interfaces.CascadeValueSelectorListener;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.OpenPropertiesInterface;
import com.safra.models.CascadeOptionItem;
import com.safra.models.formElements.BaseFormElement;

import java.util.ArrayList;
import java.util.List;

public class CascadingFieldViewHolder extends BaseFieldViewHolder {

    public static final String TAG = "cascading_view_holder";

    FormElementCascadingBinding binding;
    CascadeOptionItem selected;

    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    private CascadingAdapter adapter;



    public CascadingFieldViewHolder(@NonNull FormElementCascadingBinding binding,
                                    HandlerClickListener handleListener,
                                    boolean isPreview, boolean isReadOnly) {
        super(binding.getRoot());

        this.binding = binding;
        handlerClickListener = handleListener;
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;

        if (isPreview) {
            this.binding.layoutHandlers.clHandlers.setVisibility(View.GONE);
            this.binding.mcvTextElement.setCardElevation(0f);
        } else {
            this.binding.layoutHandlers.clHandlers.setVisibility(View.VISIBLE);
        }
    }

    private CascadeOptionItem isSelected(List<CascadeOptionItem> cascadeOptions){
        CascadeOptionItem result = null;
        for (CascadeOptionItem cascadeOptionItem: cascadeOptions){
            if(cascadeOptionItem.isSelected()){
                result = cascadeOptionItem;
                break;
            }
        }
        return result;
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
        if (baseFormElement.getFieldLabel() != null) {
            String l = baseFormElement.getFieldLabel();
            binding.tvLabel.setText(Html.fromHtml(l));
            binding.tvLabel.setVisibility(View.VISIBLE);
        } else {
            binding.tvLabel.setVisibility(View.GONE);
        }

        binding.rvCascadingFields.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        adapter = new CascadingAdapter(context, new OpenPropertiesInterface() {
            @Override
            public void openPropertiesDialog(BaseFormElement baseFormElement, int position, int childPosition) {
                handlerClickListener.openProperties(baseFormElement, position, childPosition);
            }
        }, new CascadeValueSelectorListener() {
            @Override
            public void onSelected(int optionId, int levelId, int position) {
                Log.e(TAG, "onSelected: optionId -> " + optionId + " levelId -> " + levelId + " position -> " + position);
                baseFormElement.getElementList().get(position).setFieldValue(String.valueOf(optionId));

                if (!binding.rvCascadingFields.isComputingLayout()) {

                    adapter.removeElements(position + 1);

                    adapter.notifyItemChanged(position);
                }

                Log.e(TAG, "onSelected: optionValue -> " + adapter.getValueAtIndex(position));
//                if (position < adapter.getItemCount() - 1) {
//                }
                if (levelId < baseFormElement.getElementList().size()) {
                    try {
                        BaseFormElement baseFormElement1 = baseFormElement.getElementList().get(levelId).clone();
                        List<CascadeOptionItem> cascadeOptions = new ArrayList<>();
                        Log.e(TAG, "onSelected: get all level" + (levelId + 1) + " options -> " + baseFormElement1.getCascadeOptions().size());

                        try {
                            selected = isSelected(baseFormElement.getElementList().get(position).getCascadeOptions());
                        }catch (NullPointerException e){
                            selected = null;
                        }

                        for (CascadeOptionItem ci : baseFormElement1.getCascadeOptions()) {
                            if (ci.getParentId() == optionId) {
                                ci.setSelected(false);

                                if (baseFormElement1.getUserData() != null && baseFormElement1.getUserData().size() > 0) {
                                    if (baseFormElement1.getUserData().contains(String.valueOf(ci.getId())))
                                        ci.setSelected(true);
                                }

                                cascadeOptions.add(ci);
                            }
                        }


                        Log.e(TAG, "onSelected: new options -> " + cascadeOptions.size());
                        baseFormElement1.setCascadeOptions(cascadeOptions);

                        Log.e(TAG, "onSelected: value -> " + baseFormElement1.getFieldValue());
                        if (cascadeOptions.size() > 0){
                            adapter.addElement(baseFormElement1, binding.rvCascadingFields.isComputingLayout());
                        }


                    } catch (CloneNotSupportedException e) {
                        Log.e(TAG, "onSelected: " + e.getLocalizedMessage());
                    }
                }
            }
        }, isPreview, isReadOnly);
        binding.rvCascadingFields.setAdapter(adapter);

        if (baseFormElement.getElementList() != null && baseFormElement.getElementList().size() > 0) {
            adapter.addElement(baseFormElement.getElementList().get(0), binding.rvCascadingFields.isComputingLayout());
//            adapter.addElements(baseFormElement.getElementList());
            Log.e(TAG, "bind: " + baseFormElement.getElementList());
            Log.e(TAG, "bind: " + adapter.getItemCount());
        }

        if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0) {
            for(int i= 1; i< baseFormElement.getElementList().size(); i++) {
                try {
                    BaseFormElement baseFormElement1 = baseFormElement.getElementList().get(i).clone();
                    List<CascadeOptionItem> cascadeOptions = new ArrayList<>();
                    for (CascadeOptionItem ci : baseFormElement1.getCascadeOptions()) {
                        ci.setSelected(false);

                        if(baseFormElement.getElementList().get(i-1).getUserData() != null
                                && baseFormElement.getElementList().get(i-1).getUserData().size() > 0){
                            if (baseFormElement.getElementList().get(i-1).getUserData().contains(String.valueOf(ci.getParentId()))) {
                                cascadeOptions.add(ci);
                            }
                        }
                    }
                    Log.e(TAG, "onSelected: new options -> " + cascadeOptions.size());
                    baseFormElement1.setCascadeOptions(cascadeOptions);

                    Log.e(TAG, "onSelected: value -> " + baseFormElement1.getFieldValue());
                    if (cascadeOptions.size() > 0)
                        adapter.addElement(baseFormElement1, binding.rvCascadingFields.isComputingLayout());
                } catch (CloneNotSupportedException e) {
                    Log.e(TAG, "onSelected: " + e.getLocalizedMessage());
                }
            }
        }

        if(!isPreview){
            for(int i= 1; i< baseFormElement.getElementList().size(); i++) {
//                try {
                    BaseFormElement baseFormElement1 = baseFormElement.getElementList().get(i);
                    adapter.addElement(baseFormElement1, binding.rvCascadingFields.isComputingLayout());
//                } catch (CloneNotSupportedException e) {
//                    Log.e(TAG, "onSelected: " + e.getLocalizedMessage());
//                }
            }
        }

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

        binding.layoutHandlers.ivDuplicate.setVisibility(View.GONE);

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));

        baseFormElement.getElementList();
    }
}
