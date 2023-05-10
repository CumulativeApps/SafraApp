package com.safra.viewholders;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.interfaces.BaseViewHolderInterface;
import com.safra.interfaces.FormElementEditTextListener;
import com.safra.models.formElements.BaseFormElement;

public class BaseFieldViewHolder extends RecyclerView.ViewHolder implements BaseViewHolderInterface {

    public BaseFieldViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public FormElementEditTextListener getListener() {
        return null;
    }

    @Override
    public void bind(Activity context, BaseFormElement baseFormElement) {

    }
}
