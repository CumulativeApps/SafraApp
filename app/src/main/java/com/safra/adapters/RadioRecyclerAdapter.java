package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.databinding.RecyclerRadioBinding;
import com.safra.models.OptionItem;

import java.util.List;

public class RadioRecyclerAdapter extends RecyclerView.Adapter<RadioRecyclerAdapter.RadioViewHolder> {

    private final Context context;
    private final List<OptionItem> options;
    private boolean isClickable;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onClick(OptionItem item, int position);
    }

    public RadioRecyclerAdapter(Context context, List<OptionItem> options, boolean isClickable, OnItemClickListener listener) {
        this.context = context;
        this.options = options;
        this.listener = listener;
        this.isClickable = isClickable;
    }

    public RadioRecyclerAdapter(Context context, List<OptionItem> options, int selectedPosition) {
        this.context = context;
        this.options = options;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public RadioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRadioBinding binding = RecyclerRadioBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RadioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RadioViewHolder holder, int position) {
        holder.bindView(options.get(position));
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class RadioViewHolder extends RecyclerView.ViewHolder {
        RecyclerRadioBinding binding;

        public RadioViewHolder(@NonNull RecyclerRadioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(OptionItem optionItem) {
            binding.rbOption.setText(optionItem.getOptionKey());
            binding.rbOption.setChecked(optionItem.isSelected());

            binding.rbOption.setEnabled(isClickable);

            if (isClickable) {
                binding.rbOption.setOnClickListener(v -> {
                    selectedPosition = getAbsoluteAdapterPosition();
                    updateList(getAbsoluteAdapterPosition());
                });
            }
        }
    }

    private void updateList(int position) {
        for (OptionItem option : options) {
            option.setSelected(false);
        }
        options.get(position).setSelected(true);
        notifyItemRangeChanged(0, options.size());
        listener.onClick(options.get(position), position);
    }
}
