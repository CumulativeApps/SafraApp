package com.safra.adapters;

import static com.safra.utilities.LanguageManager.languageManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safra.R;
import com.safra.databinding.SpinnerFormTypeBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.FormTypeItem;

import java.util.List;
import java.util.Objects;

public class FormTypeSpinnerAdapter extends ArrayAdapter<FormTypeItem> {

    private final List<FormTypeItem> typeList;

    public FormTypeSpinnerAdapter(@NonNull Context context, @NonNull List<FormTypeItem> objects) {
        super(context, R.layout.spinner_form_type, objects);
        this.typeList = objects;
    }

    @Nullable
    @Override
    public FormTypeItem getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public int getPosition(@Nullable FormTypeItem item) {
        if (item != null) {
            for (int i = 0; i < typeList.size(); i++) {
                if (item.getTypeId() == typeList.get(i).getTypeId())
                    return i;
            }

            return -1;
        }
        return -1;
    }

    public int getPosition(long id) {
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).getTypeId() == id) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return typeList.get(position).getTypeId();
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeViewHolder typeViewHolder;
        if (convertView == null) {
            SpinnerFormTypeBinding binding = SpinnerFormTypeBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            typeViewHolder = new TypeViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(typeViewHolder);
        } else {
            typeViewHolder = (TypeViewHolder) convertView.getTag();
        }

        typeViewHolder.binding.tvFormType.setText(languageManager.getLanguage() == 2
                ? Objects.requireNonNull(getItem(position)).getPtTypeName()
                : Objects.requireNonNull(getItem(position)).getTypeName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeViewHolder typeViewHolder;
        if (convertView == null) {
            SpinnerFormTypeBinding binding = SpinnerFormTypeBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            typeViewHolder = new TypeViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(typeViewHolder);
        } else {
            typeViewHolder = (TypeViewHolder) convertView.getTag();
        }

        typeViewHolder.binding.tvFormType.setText(languageManager.getLanguage() == 2
                ? Objects.requireNonNull(getItem(position)).getPtTypeName()
                : Objects.requireNonNull(getItem(position)).getTypeName());

        return convertView;
    }

    static class TypeViewHolder {
        SpinnerFormTypeBinding binding;

        public TypeViewHolder(SpinnerFormTypeBinding binding) {
            this.binding = binding;
        }
    }
}
