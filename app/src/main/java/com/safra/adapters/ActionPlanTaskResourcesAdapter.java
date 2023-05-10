package com.safra.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ResourceItemBinding;
import com.safra.databinding.ItemLoadingBinding;

import com.safra.databinding.ResourceItemBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ActionPlanListModel;
import com.safra.models.ActionTaskListModel;
import com.safra.models.UserItem;

import java.util.ArrayList;
import java.util.List;

public class ActionPlanTaskResourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_GOAL = 1;

    private final Context context;
    private final List<ActionTaskListModel.Data.Goal.Task.Resource> userList = new ArrayList<>();
    private final List<ActionTaskListModel.Data.Goal.Task.Resource> userData = new ArrayList<>();
    private final ActionPlanTaskResourcesAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(ActionTaskListModel.Data.Goal.Task.Resource item, int position);

        void onEdit(ActionTaskListModel.Data.Goal.Task.Resource item, int position);

        void onView(ActionTaskListModel.Data.Goal.Task.Resource item, int position);

        void changeStatus(View itemView, ActionTaskListModel.Data.Goal.Task.Resource item, int position);
    }

    public ActionPlanTaskResourcesAdapter(Context context, ActionPlanTaskResourcesAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_GOAL : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_GOAL) {
            ResourceItemBinding binding = ResourceItemBinding.inflate(inflater, parent, false);
            return new ActionPlanTaskResourcesAdapter.ResourcesViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ActionPlanTaskResourcesAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("MyAdapter", "Binding data to views for position: " + position);

        if (holder instanceof ActionPlanTaskResourcesAdapter.ResourcesViewHolder)
            ((ActionPlanTaskResourcesAdapter.ResourcesViewHolder) holder).bindView(userList.get(position));

    }

    @Override
    public int getItemCount() {

        return userList.size();
    }

    public void addUserList(List<ActionTaskListModel.Data.Goal.Task.Resource> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        ActionTaskListModel.Data.Goal.Task.Resource userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

//    public void removeItem(int position) {
//        ActionTaskListModel.Data.Goal.Task.Resource userItem = getItem(position);
//
//        userList.remove(position);
//        userData.remove(user);
//
//        notifyItemRemoved(position);
//    }



    public ActionTaskListModel.Data.Goal.Task.Resource getItem(int position){
        return userList.get(position);
    }

    public void clearLists(){
        userList.clear();
        userData.clear();

        notifyDataSetChanged();
    }

    public void searchUser(String searchText){
        searchText = searchText.toLowerCase();
        userList.clear();
        if(searchText.isEmpty()){
            userList.addAll(userData);
        } else {
            for(ActionTaskListModel.Data.Goal.Task.Resource ui : userData){
                if(ui.getName().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class ResourcesViewHolder extends RecyclerView.ViewHolder {
        ResourceItemBinding binding;

        public ResourcesViewHolder(@NonNull ResourceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ActionTaskListModel.Data.Goal.Task.Resource item) {


            binding.goalText.setText(item.getName());
            System.out.println("userList.size()"+ item.getName());

            binding.ivDelete.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));
            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}

