package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.SpinnerOptionBinding;
import com.safra.models.LanguageItem;

import java.util.List;

public class LanguageRecyclerAdapter extends RecyclerView.Adapter<LanguageRecyclerAdapter.LanguageViewHolder> {

    private final Context context;
    private final List<LanguageItem> languages;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(LanguageItem item, int position);
    }

    public LanguageRecyclerAdapter(@NonNull Context context, @NonNull List<LanguageItem> objects,
                                   OnItemClickListener listener) {
        this.context = context;
        this.languages = objects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SpinnerOptionBinding binding = SpinnerOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LanguageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        holder.bindView(languages.get(position));
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {
        SpinnerOptionBinding binding;

        public LanguageViewHolder(@NonNull SpinnerOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(LanguageItem item) {
            binding.tvOption.setText(item.getLanguageName());

            if (item.isSelected()) {
                binding.tvOption.setTextColor(context.getResources().getColor(R.color.color_secondary));
                binding.ivSelected.setVisibility(View.VISIBLE);
            } else {
                binding.tvOption.setTextColor(context.getResources().getColor(R.color.text_color));
                binding.ivSelected.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if(!item.isSelected())
                    updateList(getAbsoluteAdapterPosition());
            });

        }
    }

    private void updateList(int position) {
        for (LanguageItem option : languages) {
            option.setSelected(false);
        }
        languages.get(position).setSelected(true);
        notifyItemRangeChanged(0, languages.size());
        listener.onClick(languages.get(position), position);
    }
}
