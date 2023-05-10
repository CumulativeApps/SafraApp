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
import com.safra.models.RoleItem;

import java.util.List;
import java.util.Objects;

public class GroupSpinnerAdapter extends ArrayAdapter<RoleItem> {

    private final List<RoleItem> groupList;

    public GroupSpinnerAdapter(@NonNull Context context, @NonNull List<RoleItem> objects) {
        super(context, R.layout.spinner_group, objects);
        this.groupList = objects;
    }

    @Nullable
    @Override
    public RoleItem getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public int getPosition(@Nullable RoleItem item) {
        if(item != null) {
            for (int i = 0; i < groupList.size(); i++) {
                if (item.getRoleId() == groupList.get(i).getRoleId())
                    return i;
            }
            return -1;
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return groupList.get(position).getRoleOnlineId();
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    public int getPosition(long roleId){
        for(int i=0 ; i<groupList.size() ; i++){
            if(groupList.get(i).getRoleOnlineId() == roleId){
                return i;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if(convertView == null){
            SpinnerGroupBinding binding = SpinnerGroupBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            groupViewHolder = new GroupViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.binding.tvGroupName.setText(Objects.requireNonNull(getItem(position)).getRoleName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if(convertView == null){
            SpinnerGroupBinding binding = SpinnerGroupBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            groupViewHolder = new GroupViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.binding.tvGroupName.setText(Objects.requireNonNull(getItem(position)).getRoleName());

        return convertView;
    }

    static class GroupViewHolder {
        SpinnerGroupBinding binding;

        public GroupViewHolder(SpinnerGroupBinding binding) {
            this.binding = binding;
        }
    }
}
