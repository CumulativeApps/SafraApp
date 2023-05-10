package com.safra.viewholders;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.adapters.QuizTextAdapter;
import com.safra.databinding.FormElementQuizTextBinding;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.OpenPropertiesInterface;
import com.safra.models.formElements.BaseFormElement;

public class QuizTextViewHolder extends BaseFieldViewHolder {

    public static final String TAG = "text_marks_view_holder";

    FormElementQuizTextBinding binding;

    private final HandlerClickListener handlerClickListener;
    private final boolean isPreview;
    private final boolean isReadOnly;

    private QuizTextAdapter adapter;

    public QuizTextViewHolder(@NonNull FormElementQuizTextBinding binding,
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

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {
//        if (baseFormElement.getFieldLabel() != null) {
//            String l = baseFormElement.getFieldLabel();
//            binding.tvLabel.setText(Html.fromHtml(l));
//            binding.tvLabel.setVisibility(View.VISIBLE);
//        } else {
//            binding.tvLabel.setVisibility(View.GONE);
//        }

        binding.rvTextFieldMarks.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        adapter = new QuizTextAdapter(context, new OpenPropertiesInterface() {
            @Override
            public void openPropertiesDialog(BaseFormElement baseFormElement, int position, int childPosition) {
                handlerClickListener.openProperties(baseFormElement, getAbsoluteAdapterPosition(), childPosition);
            }
        }, isPreview, isReadOnly);
        binding.rvTextFieldMarks.setAdapter(adapter);

        if (baseFormElement.getElementList() != null && baseFormElement.getElementList().size() > 0) {
            adapter.addElements(baseFormElement.getElementList());
//            adapter.addElements(baseFormElement.getElementList());
            Log.e(TAG, "bind: " + baseFormElement.getElementList());
            Log.e(TAG, "bind: " + adapter.getItemCount());
        }

//        if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0) {
//            for(int i= 1; i< baseFormElement.getElementList().size(); i++) {
//                try {
//                    BaseFormElement baseFormElement1 = baseFormElement.getElementList().get(i).clone();
//                    List<CascadeOptionItem> cascadeOptions = new ArrayList<>();
//                    for (CascadeOptionItem ci : baseFormElement1.getCascadeOptions()) {
//                        ci.setSelected(false);
//                        if(baseFormElement.getElementList().get(i-1).getUserData() != null
//                                && baseFormElement.getElementList().get(i-1).getUserData().size() > 0){
//                            if (baseFormElement.getElementList().get(i-1).getUserData().contains(String.valueOf(ci.getParentId()))) {
//                                cascadeOptions.add(ci);
//                            }
//                        }
//                    }
//                    Log.e(TAG, "onSelected: new options -> " + cascadeOptions.size());
//                    baseFormElement1.setCascadeOptions(cascadeOptions);
//
//                    Log.e(TAG, "onSelected: value -> " + baseFormElement1.getFieldValue());
//                    if (cascadeOptions.size() > 0)
//                        adapter.addElement(baseFormElement1, binding.rvTextFieldMarks.isComputingLayout());
//                } catch (CloneNotSupportedException e) {
//                    Log.e(TAG, "onSelected: " + e.getLocalizedMessage());
//                }
//            }
//        }
//
//        if(!isPreview){
//            for(int i= 1; i< baseFormElement.getElementList().size(); i++) {
////                try {
//                    BaseFormElement baseFormElement1 = baseFormElement.getElementList().get(i);
//                    adapter.addElement(baseFormElement1, binding.rvTextFieldMarks.isComputingLayout());
////                } catch (CloneNotSupportedException e) {
////                    Log.e(TAG, "onSelected: " + e.getLocalizedMessage());
////                }
//            }
//        }

        binding.layoutHandlers.ivProperties.setVisibility(View.GONE);

        binding.layoutHandlers.ivProperties.setOnClickListener(v -> handlerClickListener
                .openProperties(baseFormElement, getAbsoluteAdapterPosition(), -1));

//        binding.layoutHandlers.ivDuplicate.setVisibility(View.GONE);

        binding.layoutHandlers.ivDuplicate.setOnClickListener(v -> handlerClickListener
                .duplicateItem(baseFormElement, getAbsoluteAdapterPosition()));

        binding.layoutHandlers.ivDelete.setOnClickListener(v -> handlerClickListener
                .deleteItem(baseFormElement, getAbsoluteAdapterPosition()));
    }
}
