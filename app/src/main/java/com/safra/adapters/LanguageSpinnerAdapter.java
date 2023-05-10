package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safra.R;
import com.safra.databinding.SpinnerLanguageBinding;
import com.safra.models.LanguageItem;

import java.util.List;
import java.util.Objects;

public class LanguageSpinnerAdapter extends ArrayAdapter<LanguageItem> {

    private final List<LanguageItem> languageList;

    public LanguageSpinnerAdapter(@NonNull Context context, @NonNull List<LanguageItem> objects) {
        super(context, R.layout.spinner_language, objects);
        this.languageList = objects;
    }

    @Nullable
    @Override
    public LanguageItem getItem(int position) {
        return languageList.get(position);
    }

    @Override
    public int getPosition(@Nullable LanguageItem item) {
        if (item != null) {
            for (int i = 0; i < languageList.size(); i++) {
                if (item.getLanguageId() == languageList.get(i).getLanguageId())
                    return i;
            }
            return -1;
        }
        return -1;
    }

    public int getPosition(long id) {
        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).getLanguageId() == id) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return languageList.get(position).getLanguageId();
    }

    @Override
    public int getCount() {
        return languageList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeViewHolder typeViewHolder;
        if (convertView == null) {
            SpinnerLanguageBinding binding = SpinnerLanguageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            typeViewHolder = new TypeViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(typeViewHolder);
        } else {
            typeViewHolder = (TypeViewHolder) convertView.getTag();
        }

        typeViewHolder.binding.tvLanguage.setText(Objects.requireNonNull(getItem(position)).getLanguageName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeViewHolder typeViewHolder;
        if (convertView == null) {
            SpinnerLanguageBinding binding = SpinnerLanguageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            typeViewHolder = new TypeViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(typeViewHolder);
        } else {
            typeViewHolder = (TypeViewHolder) convertView.getTag();
        }

        typeViewHolder.binding.tvLanguage.setText(Objects.requireNonNull(getItem(position)).getLanguageName());

        return convertView;
    }

    static class TypeViewHolder {
        SpinnerLanguageBinding binding;

        public TypeViewHolder(SpinnerLanguageBinding binding) {
            this.binding = binding;
        }
    }
}
