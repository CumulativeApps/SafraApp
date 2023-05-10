package com.safra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.databinding.RecyclerCheckboxBinding;
import com.safra.models.OptionItem;

import java.util.List;

public class CheckboxRecyclerAdapter extends RecyclerView.Adapter<CheckboxRecyclerAdapter.CheckboxViewHolder> {

    private final List<OptionItem> options;
    private final boolean isClickable;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(OptionItem item, int position);
    }

    public CheckboxRecyclerAdapter(List<OptionItem> options, boolean isClickable,
                                   OnItemClickListener listener) {
        this.options = options;
        this.isClickable = isClickable;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CheckboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerCheckboxBinding binding = RecyclerCheckboxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CheckboxViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxViewHolder holder, int position) {
        holder.bindView(options.get(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class CheckboxViewHolder extends RecyclerView.ViewHolder{
        RecyclerCheckboxBinding binding;

        public CheckboxViewHolder(@NonNull RecyclerCheckboxBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(OptionItem optionItem) {
            binding.cbOption.setText(optionItem.getOptionKey());

            binding.cbOption.setChecked(optionItem.isSelected());

            binding.cbOption.setEnabled(isClickable);

            if(isClickable) {
                binding.cbOption.setOnClickListener(v -> updateList(getAbsoluteAdapterPosition()));
            }
        }
    }

    private void updateList(int position){
        options.get(position).setSelected(!options.get(position).isSelected());
        notifyItemChanged(position);
        listener.onClick(options.get(position), position);
    }
}
