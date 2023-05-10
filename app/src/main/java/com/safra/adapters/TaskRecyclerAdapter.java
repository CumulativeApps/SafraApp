package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerTasksBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.TaskItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.safra.utilities.Common.DATE_FORMAT;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TASK = 1;

    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    private final Context context;
    private final List<TaskItem> taskList = new ArrayList<>();
    private final List<TaskItem> taskData = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onDelete(TaskItem item, int position);
        void onEdit(TaskItem item, int position);
        void changeStatus(View view, TaskItem item, int position);
        void viewTask(TaskItem item, int position);
    }

    public TaskRecyclerAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return taskList.get(position) != null ? VIEW_TASK : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_TASK) {
            RecyclerTasksBinding binding = RecyclerTasksBinding.inflate(inflater, parent, false);
            return new TaskViewHolder(binding);
        }else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TaskViewHolder)
            ((TaskViewHolder)holder).bindView(taskList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void addTaskList(List<TaskItem> taskList) {
        this.taskList.addAll(taskList);
        this.taskData.addAll(taskList);
    }

    public void removeTask(int position){
        TaskItem taskItem = getItem(position);
        taskList.remove(position);
        notifyItemRemoved(position);
        taskData.remove(taskItem);

    }

    public TaskItem getItem(int position){
        return taskList.get(position);
    }

    public void clearLists(){
        taskList.clear();
        taskData.clear();

        notifyDataSetChanged();
    }

    public void searchTask(String searchText){
        searchText = searchText.toLowerCase();
        taskList.clear();
        if(searchText.isEmpty()){
            taskList.addAll(taskData);
        } else {
            for(TaskItem ti : taskData){
                if(ti.getTaskName().toLowerCase().contains(searchText))
                    taskList.add(ti);
            }
        }

        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder{
        RecyclerTasksBinding binding;

        public TaskViewHolder(@NonNull RecyclerTasksBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(TaskItem item) {
            binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date", context.getString(R.string.start_date)));
            binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date", context.getString(R.string.end_date)));
            binding.tvPriorityTitle.setText(LanguageExtension.setText("priority", context.getString(R.string.priority)));
            binding.tvAddedByTitle.setText(LanguageExtension.setText("added_by", context.getString(R.string.added_by)));
            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
            binding.tvViewTask.setText(LanguageExtension.setText("view_task", context.getString(R.string.view_task)));

            binding.tvTaskTitle.setText(item.getTaskName());

            binding.tvStartDate.setText(sdfToShow.format(new Date(item.getStartDate())));
            binding.tvEndDate.setText(sdfToShow.format(new Date(item.getEndDate())));

            if(!item.getAddedByName().isEmpty())
                binding.tvAddedBy.setText(item.getAddedByName());

            if(item.getPriorityName() != null && !item.getPriorityName().isEmpty())
                binding.tvPriority.setText(item.getPriorityName());
            else if(item.getPriority() > 0){
                switch (item.getPriority()){
                    case 3:
                        binding.tvPriority.setText(LanguageExtension.setText("low", context.getString(R.string.low)));
                        break;
                    case 2:
                        binding.tvPriority.setText(LanguageExtension.setText("medium", context.getString(R.string.medium)));
                        break;
                    case 1:
                        binding.tvPriority.setText(LanguageExtension.setText("high", context.getString(R.string.high)));
                        break;
                }
            }

            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
            ViewExtension.makeVisible(binding.tvChangeStatus, item.isChangeable());

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
//                listener.onExpand(item, getAbsoluteAdapterPosition());
            });

            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

            binding.ivDelete.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));

            binding.tvViewTask.setOnClickListener(v -> listener.viewTask(item, getAbsoluteAdapterPosition()));

            binding.tvChangeStatus.setOnClickListener(v -> listener.changeStatus(v, item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }

}
