package com.safra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.safra.databinding.RecyclerFieldListBinding;
import com.safra.models.FieldItem;

import java.util.List;

public class FieldRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<FieldItem> fieldList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(FieldItem item, int position);
    }

    public FieldRecyclerAdapter(List<FieldItem> fieldList, OnItemClickListener listener) {
        this.fieldList = fieldList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerFieldListBinding binding = RecyclerFieldListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FieldListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FieldListViewHolder)holder).bindView(fieldList.get(position));
    }

    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    class FieldListViewHolder extends RecyclerView.ViewHolder{
        RecyclerFieldListBinding binding;

        public FieldListViewHolder(@NonNull RecyclerFieldListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(FieldItem fieldItem) {
            binding.tvField.setText(fieldItem.getFieldName());

            Glide.with(itemView).load(fieldItem.getFieldIcon()).into(binding.ivField);

            itemView.setOnClickListener(v -> listener.onClick(fieldItem, getAbsoluteAdapterPosition()));

        }
    }
}
