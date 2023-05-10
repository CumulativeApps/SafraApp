package com.safra.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.GoalItemBinding;
import com.safra.databinding.ItemLoadingBinding;

import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ActionPlanListModel;
import com.safra.models.UserItem;

import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_GOAL = 1;

    private final Context context;
    private final List<ProjectPlanListModel.Data.AimGoals.Aim.Goal> userList = new ArrayList<>();
    private final List<ProjectPlanListModel.Data.AimGoals.Aim.Goal> userData = new ArrayList<>();
    private final GoalAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position);

        void onEdit(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position);

        void onView(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position);

        void changeStatus(View itemView, ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position);
    }

    public GoalAdapter(Context context, GoalAdapter.OnItemClickListener listener) {
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
            GoalItemBinding binding = GoalItemBinding.inflate(inflater, parent, false);
            return new GoalAdapter.GoalListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new GoalAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("MyAdapter", "Binding data to views for position: " + position);

        if (holder instanceof GoalAdapter.GoalListViewHolder)
            ((GoalAdapter.GoalListViewHolder) holder).bindView(userList.get(position));

    }

    @Override
    public int getItemCount() {

        return userList.size();
    }

    public void addUserList(List<ProjectPlanListModel.Data.AimGoals.Aim.Goal> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        ProjectPlanListModel.Data.AimGoals.Aim.Goal userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public ProjectPlanListModel.Data.AimGoals.Aim.Goal getItem(int position){
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
            for(ProjectPlanListModel.Data.AimGoals.Aim.Goal ui : userData){
                if(ui.getGoal().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class GoalListViewHolder extends RecyclerView.ViewHolder {
        GoalItemBinding binding;

        public GoalListViewHolder(@NonNull GoalItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ProjectPlanListModel.Data.AimGoals.Aim.Goal item) {


            binding.goalText.setText(item.getGoal());
            System.out.println("userList.size()"+ item.getGoal());
//            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
//            ViewExtension.makeVisible(binding.ivView, item.isEditable());
            binding.ivDelete.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));
            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));
            binding.ivView.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));

        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}

