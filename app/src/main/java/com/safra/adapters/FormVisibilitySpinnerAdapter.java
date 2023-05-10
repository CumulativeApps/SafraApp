package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safra.R;
import com.safra.databinding.SpinnerFormTypeBinding;
import com.safra.models.FormTypeItem;
import com.safra.models.FormVisibilityItem;

import java.util.List;
import java.util.Objects;

public class FormVisibilitySpinnerAdapter extends ArrayAdapter<FormVisibilityItem> {

    private final List<FormVisibilityItem> typeList;

    public FormVisibilitySpinnerAdapter(@NonNull Context context, @NonNull List<FormVisibilityItem> objects) {
        super(context, R.layout.spinner_form_type, objects);
        this.typeList = objects;
    }

    @Nullable
    @Override
    public FormVisibilityItem getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public int getPosition(@Nullable FormVisibilityItem item) {
        if(item != null) {
            for (int i = 0; i < typeList.size(); i++) {
                if (item.getVisibilityId() == typeList.get(i).getVisibilityId())
                    return i;
            }

            return -1;
        }
        return -1;
    }

    public int getPosition(long id){
        for(int i=0; i<typeList.size(); i++){
            if(typeList.get(i).getVisibilityId() == id){
                return i;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return typeList.get(position).getVisibilityId();
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeViewHolder typeViewHolder;
        if(convertView == null){
            SpinnerFormTypeBinding binding = SpinnerFormTypeBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            typeViewHolder = new TypeViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(typeViewHolder);
        } else {
            typeViewHolder = (TypeViewHolder) convertView.getTag();
        }

        typeViewHolder.binding.tvFormType.setText(Objects.requireNonNull(getItem(position)).getVisibilityName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeViewHolder typeViewHolder;
        if(convertView == null){
            SpinnerFormTypeBinding binding = SpinnerFormTypeBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            typeViewHolder = new TypeViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(typeViewHolder);
        } else {
            typeViewHolder = (TypeViewHolder) convertView.getTag();
        }

        typeViewHolder.binding.tvFormType.setText(Objects.requireNonNull(getItem(position)).getVisibilityName());

        return convertView;
    }

    static class TypeViewHolder {
        SpinnerFormTypeBinding binding;

        public TypeViewHolder(SpinnerFormTypeBinding binding) {
            this.binding = binding;
        }
    }
}
