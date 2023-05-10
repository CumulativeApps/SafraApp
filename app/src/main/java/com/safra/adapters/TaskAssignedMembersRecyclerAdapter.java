package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.RecyclerTaskAssignedMemberBinding;
import com.safra.models.TaskAssignedMemberItem;
import com.safra.models.UserItem;

import java.util.List;

public class TaskAssignedMembersRecyclerAdapter
        extends RecyclerView.Adapter<TaskAssignedMembersRecyclerAdapter.TaskAssignedMemberViewHolder> {

    private final List<TaskAssignedMemberItem> memberList;

    public interface OnItemClickListener{
        void showOption(View view, UserItem item, int position);
    }

    public TaskAssignedMembersRecyclerAdapter(Context context, List<TaskAssignedMemberItem> memberList,
                                              OnItemClickListener listener) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public TaskAssignedMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerTaskAssignedMemberBinding binding = RecyclerTaskAssignedMemberBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskAssignedMemberViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAssignedMemberViewHolder holder, int position) {
        holder.bindView(memberList.get(position));
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class TaskAssignedMemberViewHolder extends RecyclerView.ViewHolder{
        RecyclerTaskAssignedMemberBinding binding;

        public TaskAssignedMemberViewHolder(@NonNull RecyclerTaskAssignedMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(TaskAssignedMemberItem item) {
            binding.tvMemberName.setText(item.getMemberName());
            binding.tvMemberStatus.setText(item.getMemberStatus());

//            moreOption.setOnClickListener(v -> {
//                listener.showOption(v, item, getAbsoluteAdapterPosition());
//            });

        }
    }
}
