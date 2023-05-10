package com.safra.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.RecyclerOptionBinding;
import com.safra.models.OptionItem;

import java.util.List;

public class OptionRecyclerAdapter extends RecyclerView.Adapter<OptionRecyclerAdapter.OptionViewHolder> {

    public static final String TAG = "option_recycler_adapter";

    private final List<OptionItem> options;
    private final OnItemClickListener listener;
    private final int selectedPosition = -1;

    private boolean isMultipleAllowed, isEditable = true;

    public interface OnItemClickListener {
        default void onSelect(OptionItem item, int position) {
            Log.d(TAG, "onSelect: " + item + " at " + position);
        }

        void onRemove(OptionItem item, int position);
    }

    public OptionRecyclerAdapter(List<OptionItem> options, OnItemClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerOptionBinding binding = RecyclerOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    class OptionViewHolder extends RecyclerView.ViewHolder {
        RecyclerOptionBinding binding;

        public OptionViewHolder(@NonNull RecyclerOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(OptionItem optionItem) {
            binding.tvOptionLabel.setText(optionItem.getOptionKey());
            binding.tvOptionValue.setText(optionItem.getOptionValue());

            if (optionItem.isSelected()) {
                binding.ivSelect.setImageResource(R.drawable.ic_round_check_box);
            } else {
                binding.ivSelect.setImageResource(R.drawable.ic_check_box_outline);
            }

            binding.ivSelect.setOnClickListener(v -> {
//                optionItem.setSelected(!optionItem.isSelected());
//                listener.onSelect(optionItem, getAbsoluteAdapterPosition());
                updateList(getAbsoluteAdapterPosition());
            });

            binding.ivRemove.setOnClickListener(v -> {
                if (isEditable && listener != null)
                    listener.onRemove(optionItem, getAbsoluteAdapterPosition());
            });

//            option.setOnClickListener(v -> {
//                selectedPosition = getAbsoluteAdapterPosition();
//                updateList(getAbsoluteAdapterPosition());
//            });
        }
    }

    public List<OptionItem> getList() {
        return options;
    }

    public void setMultipleAllowed(boolean isMultipleAllowed) {
        this.isMultipleAllowed = isMultipleAllowed;
    }

    public boolean isMultipleAllowed() {
        return isMultipleAllowed;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    private void updateList(int position) {
        if (isMultipleAllowed) {
            options.get(position).setSelected(!options.get(position).isSelected());
            notifyItemChanged(position);
        } else {
            boolean isSelect = options.get(position).isSelected();
            for (OptionItem option : options) {
                option.setSelected(false);
            }
            options.get(position).setSelected(!isSelect);
            notifyItemRangeChanged(0, options.size());
        }
        if (isEditable && listener != null)
            listener.onSelect(options.get(position), position);
    }
}
