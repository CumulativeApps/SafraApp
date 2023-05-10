package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.SpinnerOptionBinding;
import com.safra.models.OptionItem;

import java.util.ArrayList;
import java.util.List;

public class SpinnerRecyclerAdapter extends RecyclerView.Adapter<SpinnerRecyclerAdapter.OptionViewHolder> {

    private final Context context;
    private final List<OptionItem> options;
    private final OnItemClickListener listener;

    private final boolean isMultipleAllowed;
    private final boolean isClickable;

    public interface OnItemClickListener {
        void onClick(OptionItem item, int position);
    }

    public SpinnerRecyclerAdapter(@NonNull Context context, @NonNull List<OptionItem> objects,
                                  boolean isMultipleAllowed, boolean isClickable, OnItemClickListener listener) {
        this.context = context;
        this.options = objects;
        this.listener = listener;
        this.isMultipleAllowed = isMultipleAllowed;
        this.isClickable = isClickable;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SpinnerOptionBinding binding = SpinnerOptionBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    private String showSelected() {
        StringBuilder stringBuilder = new StringBuilder();
        for (OptionItem o : options) {
            if (o.isSelected()) {
                if (stringBuilder.toString().isEmpty())
                    stringBuilder.append(o.getOptionKey());
                else
                    stringBuilder.append(",").append(o.getOptionKey());
            }
        }

        return stringBuilder.toString();
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        SpinnerOptionBinding binding;

        public OptionViewHolder(@NonNull SpinnerOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(OptionItem item) {
            binding.tvOption.setText(item.getOptionKey());

            if (item.isSelected()) {
                binding.tvOption.setTextColor(context.getResources().getColor(R.color.color_secondary));
                binding.ivSelected.setVisibility(View.VISIBLE);
            } else {
                binding.tvOption.setTextColor(context.getResources().getColor(R.color.text_color));
                binding.ivSelected.setVisibility(View.INVISIBLE);
            }

            if(isClickable) {
                itemView.setOnClickListener(v -> updateList(getAbsoluteAdapterPosition()));
            }
        }
    }

    private void updateList(int position) {
        if (isMultipleAllowed) {
            if (!options.get(position).getOptionValue().isEmpty() && position != 0) {
                options.get(position).setSelected(!options.get(position).isSelected());
                notifyItemChanged(position);
            }
        } else {
            for (OptionItem option : options) {
                option.setSelected(false);
            }
            options.get(position).setSelected(!options.get(position).getOptionValue().isEmpty() || position != 0);
            notifyItemRangeChanged(0, options.size());
        }
        listener.onClick(options.get(position), position);
    }

    public List<OptionItem> getSelected() {
        List<OptionItem> selectedOptions = new ArrayList<>();
        for (OptionItem o : options) {
            if (o.isSelected())
                selectedOptions.add(o);
        }

        return selectedOptions;
    }
}
