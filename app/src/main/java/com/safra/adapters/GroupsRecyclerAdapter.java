package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerGroupsBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.RoleItem;

import java.util.ArrayList;
import java.util.List;

public class GroupsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_GROUP = 1;

    private final Context context;
    private final List<RoleItem> roleList = new ArrayList<>();
    private final List<RoleItem> roleData = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onEdit(RoleItem item, int position);

        void viewGroup(RoleItem item, int position);
    }

    public GroupsRecyclerAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return roleList.get(position) != null ? VIEW_GROUP : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_GROUP) {
            RecyclerGroupsBinding binding = RecyclerGroupsBinding.inflate(inflater, parent, false);
            return new GroupViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder)
            ((GroupViewHolder)holder).bindView(roleList.get(position));
    }

    @Override
    public int getItemCount() {
        return roleList.size();
    }

    public void addGroupList(List<RoleItem> roleList){
        this.roleList.addAll(roleList);
        this.roleData.addAll(roleList);
    }

    public void removeGroup(int position){
        RoleItem roleItem = getItem(position);
        roleList.remove(position);
        notifyItemRemoved(position);
        roleData.remove(roleItem);
    }

    public RoleItem getItem(int position){
        return roleList.get(position);
    }

    public void clearLists(){
        roleList.clear();
        roleData.clear();

        notifyDataSetChanged();
    }

    public void searchGroup(String searchText){
        searchText = searchText.toLowerCase();
        roleList.clear();
        if(searchText.isEmpty()){
            roleList.addAll(roleData);
        } else {
            for(RoleItem ri : roleData){
                if(ri.getRoleName().toLowerCase().contains(searchText))
                    roleList.add(ri);
            }
        }

        notifyDataSetChanged();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder{
        RecyclerGroupsBinding binding;

        public GroupViewHolder(@NonNull RecyclerGroupsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(RoleItem item) {
            binding.tvEditGroup.setText(LanguageExtension.setText("edit_group", context.getString(R.string.edit_group)));
            binding.tvViewGroup.setText(LanguageExtension.setText("view_group", context.getString(R.string.view_group)));

            binding.tvGroupName.setText(item.getRoleName());

            ViewExtension.makeVisible(binding.tvEditGroup, item.isEditable());
            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

            itemView.setOnClickListener(v -> {
                if(!item.isExpanded()){
                    ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                    item.setExpanded(ViewExtension.toggleArrow(binding.ivExpandDetail, !item.isExpanded()));
                }
            });

            binding.ivExpandDetail.setOnClickListener(v -> {
                ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                item.setExpanded(ViewExtension.toggleArrow(v, !item.isExpanded()));
            });

            binding.tvEditGroup.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

            binding.tvViewGroup.setOnClickListener(v -> listener.viewGroup(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
