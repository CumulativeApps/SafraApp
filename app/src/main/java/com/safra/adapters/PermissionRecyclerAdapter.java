package com.safra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.databinding.RecyclerPermissionBinding;
import com.safra.models.PermissionItem;

import java.util.List;

public class PermissionRecyclerAdapter extends RecyclerView.Adapter<PermissionRecyclerAdapter.PermissionViewHolder> {

    private final List<PermissionItem> permissionList;

    public PermissionRecyclerAdapter(List<PermissionItem> permissionList) {
        this.permissionList = permissionList;
    }

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerPermissionBinding binding = RecyclerPermissionBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PermissionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionViewHolder holder, int position) {
        holder.bindView(permissionList.get(position));
    }

    @Override
    public int getItemCount() {
        return permissionList.size();
    }

    static class PermissionViewHolder extends RecyclerView.ViewHolder{
        RecyclerPermissionBinding binding;

        public PermissionViewHolder(@NonNull RecyclerPermissionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(PermissionItem permissionItem) {
            binding.tvPermissionName.setText(permissionItem.getPermissionName());
        }
    }

}
