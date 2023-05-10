package com.safra.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.databinding.FormElementCascadingSelectBinding;
import com.safra.interfaces.CascadeValueSelectorListener;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.OpenPropertiesInterface;
import com.safra.models.formElements.BaseFormElement;
import com.safra.viewholders.BaseFieldViewHolder;
import com.safra.viewholders.CascadingSelectFieldViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CascadingAdapter extends RecyclerView.Adapter<BaseFieldViewHolder>
        implements HandlerClickListener {

    public static final String TAG = "cascading_adapter";

    private final Activity context;
    private List<BaseFormElement> elementList;

    private final OpenPropertiesInterface propertiesListener;
    private final CascadeValueSelectorListener listener;

    private final boolean isPreview;
    private final boolean isReadOnly;

    public CascadingAdapter(Activity context, OpenPropertiesInterface propertiesListener,
                            CascadeValueSelectorListener listener, boolean isPreview, boolean isReadOnly) {
        this.context = context;
        this.elementList = new ArrayList<>();
        this.isPreview = isPreview;
        this.isReadOnly = isReadOnly;
        this.propertiesListener = propertiesListener;
        this.listener = listener;
    }

    public void addElements(List<BaseFormElement> formElements) {
        this.elementList = formElements;
        notifyDataSetChanged();
        Log.e(TAG, "addElements: replacing whole list");
    }

    public void addElement(BaseFormElement formElement, boolean isComputing) {
        this.elementList.add(formElement);
        if (!isComputing)
            notifyDataSetChanged();
        Log.e(TAG, "addElement: adding element...");
    }

    public void addElement(BaseFormElement formElement, int position) {
        this.elementList.add(position, formElement);
        notifyItemInserted(position);
        Log.e(TAG, "addElement: adding element at " + position + "...");
    }

    public void removeElements(int position) {
        int count = elementList.size() - position;
        this.elementList.subList(position, elementList.size()).clear();
        notifyItemRangeRemoved(position, count);
    }

    public void setValueAtIndex(int position, String value) {
        BaseFormElement baseFormElement = elementList.get(position);
        baseFormElement.setFieldValue(value);
        notifyItemChanged(position);

    }

    public void setValueAtName(String fieldName, String value) {
        int position = -1;
        for (int i = 0; i < elementList.size(); i++) {
            if (elementList.get(i).getFieldName().equals(fieldName)) {
                elementList.get(i).setFieldValue(value);
                position = i;
                break;
            }
        }
        notifyItemChanged(position);
    }

    public BaseFormElement getValueAtIndex(int index) {
        return elementList.get(index);
    }

    public BaseFormElement getValueAtName(String fieldName) {
        for (BaseFormElement e : elementList) {
            if (e.getFieldName().equals(fieldName)) {
                return e;
            }
        }
        return null;
    }

    public List<BaseFormElement> getElementList() {
        return elementList;
    }

    @Override
    public int getItemViewType(int position) {
        return elementList.get(position).getType();
    }

    @NonNull
    @Override
    public BaseFieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder: creating holder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FormElementCascadingSelectBinding selectBinding = FormElementCascadingSelectBinding.inflate(inflater, parent, false);
        return new CascadingSelectFieldViewHolder(selectBinding, listener, this, isPreview, isReadOnly);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseFieldViewHolder holder, int position) {
        if (holder.getListener() != null) {
            holder.getListener().updatePosition(holder.getAbsoluteAdapterPosition());
        }

        BaseFormElement formElement = elementList.get(position);
        holder.bind(context, formElement);
        Log.e(TAG, "onBindViewHolder: binding view holder");
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

//    @Override
//    public void updateValue(String fieldValue, int position) {
//        BaseFormElement baseFormElement = elementList.get(position);
//        Log.e("FormAdapter", "updateValue: " + baseFormElement + "->" + fieldValue);
//        baseFormElement.setFieldValue(fieldValue);
//        baseFormElement.setHaveError(baseFormElement.isRequired() && TextUtils.isEmpty(fieldValue));
//        notifyItemChanged(position);
//    }

    public void onItemMove(int initialPosition, int finalPosition) {
        if (initialPosition < elementList.size() && finalPosition < elementList.size()) {
            if (initialPosition < finalPosition) {
                for (int i = initialPosition; i < finalPosition; i++) {
                    Collections.swap(elementList, i, i + 1);
                }
            } else {
                for (int i = initialPosition; i > finalPosition; i--) {
                    Collections.swap(elementList, i, i - 1);
                }
            }
        }
        notifyItemMoved(initialPosition, finalPosition);
    }

    @Override
    public void openProperties(BaseFormElement baseFormElement, int position, int childPosition) {
        if (propertiesListener != null)
            propertiesListener.openPropertiesDialog(baseFormElement, position, childPosition);
    }

    @Override
    public void duplicateItem(BaseFormElement baseFormElement, int position) {
        if (elementList.get(position) == baseFormElement) {
            String n = baseFormElement.getFieldName().substring(0, baseFormElement.getFieldName().indexOf("-") + 1);
            baseFormElement.setFieldName(n + Calendar.getInstance().getTimeInMillis());
            addElement(baseFormElement, position + 1);
        }
    }

    @Override
    public void deleteItem(BaseFormElement baseFormElement, int position) {
        if (elementList.get(position) == baseFormElement) {
            elementList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position - 1);
        }
    }

    public void updateFormElement(BaseFormElement baseFormElement, int position) {
        elementList.set(position, baseFormElement);
        notifyItemChanged(position);
    }

    public void updateFormElement(BaseFormElement baseFormElement, String fieldName) {
        for (int i = 0; i < elementList.size(); i++) {
            if (elementList.get(i).getFieldName().equals(fieldName)) {
                Log.e(TAG, "updateFormElement: " + i);
                elementList.set(i, baseFormElement);
                notifyItemChanged(i);
                return;
            }
        }
    }
}
