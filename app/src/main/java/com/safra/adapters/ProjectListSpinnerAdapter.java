package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.SpinnerOptionBinding;
import com.safra.models.ProjectListResponseModel;


import java.util.ArrayList;
import java.util.List;

public class ProjectListSpinnerAdapter extends RecyclerView.Adapter<ProjectListSpinnerAdapter.OptionViewHolder> {

    private final Context context;
    private final List<ProjectListResponseModel> options;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSelect(ProjectListResponseModel item, int position);
    }

    public ProjectListSpinnerAdapter(@NonNull Context context, @NonNull List<ProjectListResponseModel> objects, OnItemClickListener listener) {
        this.context = context;
        this.options = objects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SpinnerOptionBinding binding = SpinnerOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OptionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        holder.bindView(options.get(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

//    private String showSelected() {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (ProjectListResponseModel o : options) {
//            if (o.isSelected()) {
//                if (stringBuilder.toString().isEmpty())
//                    stringBuilder.append(o.getOptionKey());
//                else
//                    stringBuilder.append(",").append(o.getOptionKey());
//            }
//        }
//
//        return stringBuilder.toString();
//    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        SpinnerOptionBinding binding;

        public OptionViewHolder(@NonNull SpinnerOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ProjectListResponseModel item) {
            binding.tvOption.setText(item.getName());

            if (item.isSelected()) {
                binding.tvOption.setTextColor(context.getResources().getColor(R.color.color_secondary));
                binding.ivSelected.setVisibility(View.VISIBLE);
            } else {
                binding.tvOption.setTextColor(context.getResources().getColor(R.color.text_color));
                binding.ivSelected.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(v -> updateList(getAbsoluteAdapterPosition()));
        }
    }

    private void updateList(int position) {
        if (options.get(position).getUserId() != -1) {
            options.get(position).setSelected(!options.get(position).isSelected());
            notifyItemChanged(position);
        }
        listener.onSelect(options.get(position), position);
    }

    public List<ProjectListResponseModel> getSelected() {
        List<ProjectListResponseModel> selectedOptions = new ArrayList<>();
        for (ProjectListResponseModel o : options) {
            if (o.isSelected())
                selectedOptions.add(o);
        }

        return selectedOptions;
    }
}
