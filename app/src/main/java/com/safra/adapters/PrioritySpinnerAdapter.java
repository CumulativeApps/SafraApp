package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safra.R;
import com.safra.databinding.SpinnerGroupBinding;
import com.safra.models.PriorityItem;

import java.util.List;
import java.util.Objects;

public class PrioritySpinnerAdapter extends ArrayAdapter<PriorityItem> {

    private final List<PriorityItem> priorityList;

    public PrioritySpinnerAdapter(@NonNull Context context, @NonNull List<PriorityItem> objects) {
        super(context, R.layout.spinner_group, objects);
        this.priorityList = objects;
    }

    @Nullable
    @Override
    public PriorityItem getItem(int position) {
        return priorityList.get(position);
    }

    @Override
    public int getPosition(@Nullable PriorityItem item) {
        if(item != null) {
            for (int i = 0; i < priorityList.size(); i++) {
                if (item.getPriorityStatus() == priorityList.get(i).getPriorityStatus())
                    return i;
            }

            return -1;
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return priorityList.get(position).getPriorityStatus();
    }

    @Override
    public int getCount() {
        return priorityList.size();
    }

    public int getPosition(long roleId){
        for(int i = 0; i< priorityList.size() ; i++){
            if(priorityList.get(i).getPriorityStatus() == roleId){
                return i;
            }
        }

        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PriorityViewHolder priorityViewHolder;
        if(convertView == null){
            SpinnerGroupBinding binding = SpinnerGroupBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            priorityViewHolder = new PriorityViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(priorityViewHolder);
        } else {
            priorityViewHolder = (PriorityViewHolder) convertView.getTag();
        }

        priorityViewHolder.binding.tvGroupName.setText(Objects.requireNonNull(getItem(position)).getPriorityName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PriorityViewHolder priorityViewHolder;
        if(convertView == null){
            SpinnerGroupBinding binding = SpinnerGroupBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            priorityViewHolder = new PriorityViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(priorityViewHolder);
        } else {
            priorityViewHolder = (PriorityViewHolder) convertView.getTag();
        }

        priorityViewHolder.binding.tvGroupName.setText(Objects.requireNonNull(getItem(position)).getPriorityName());

        return convertView;
    }

    static class PriorityViewHolder {
        SpinnerGroupBinding binding;

        public PriorityViewHolder(SpinnerGroupBinding binding) {
            this.binding = binding;
        }
    }
}
