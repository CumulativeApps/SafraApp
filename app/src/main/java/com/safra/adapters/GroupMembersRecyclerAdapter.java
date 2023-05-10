package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.RecyclerGroupMemberBinding;
import com.safra.models.UserItem;

import java.util.List;

public class GroupMembersRecyclerAdapter extends RecyclerView.Adapter<GroupMembersRecyclerAdapter.GroupMemberViewHolder> {

    private final List<UserItem> userList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void showOption(View view, UserItem item, int position);
    }

    public GroupMembersRecyclerAdapter(List<UserItem> userList,
                                       OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerGroupMemberBinding binding = RecyclerGroupMemberBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GroupMemberViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberViewHolder holder, int position) {
        holder.bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class GroupMemberViewHolder extends RecyclerView.ViewHolder{
        RecyclerGroupMemberBinding binding;

        public GroupMemberViewHolder(@NonNull RecyclerGroupMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(UserItem item) {
            binding.tvMemberName.setText(item.getUserName());
//            addedDate.setText(item.getAddedDate());

            binding.ivMoreOption.setOnClickListener(v -> listener.showOption(v, item, getAbsoluteAdapterPosition()));

        }
    }
}
