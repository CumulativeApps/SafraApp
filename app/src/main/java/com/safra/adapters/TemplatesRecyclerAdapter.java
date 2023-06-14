package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerTemplatesBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.TemplateItem;
import com.safra.models.TemplateItem.Data.TemplateList;

import java.util.List;

public class TemplatesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TEMPLATE = 1;

    private final Context context;
    private final List<TemplateItem.Data.TemplateList> templateList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(TemplateItem.Data.TemplateList item, int position);
        void showPreview(TemplateItem.Data.TemplateList item, int position);
    }

    public TemplatesRecyclerAdapter(Context context, List<TemplateItem.Data.TemplateList> templateList,
                                    OnItemClickListener listener) {
        this.context = context;
        this.templateList = templateList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return templateList.get(position) != null ? VIEW_TEMPLATE : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == VIEW_TEMPLATE) {
            RecyclerTemplatesBinding binding = RecyclerTemplatesBinding.inflate(inflater, parent, false);
            return new TemplateViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TemplateViewHolder)
            ((TemplateViewHolder)holder).bindView(templateList.get(position));
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    class TemplateViewHolder extends RecyclerView.ViewHolder{
        RecyclerTemplatesBinding binding;

        public TemplateViewHolder(@NonNull RecyclerTemplatesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(TemplateItem.Data.TemplateList item) {
            binding.tvPreview.setText(LanguageExtension.setText("preview", context.getString(R.string.preview)));

            binding.tvTemplateTitle.setText(item.getTemplate_name());

            if(item.getTemplate_id() == -1)
                binding.tvPreview.setVisibility(View.GONE);
            else
                binding.tvPreview.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(v -> listener.onClick(item, getAbsoluteAdapterPosition()));

            binding.tvPreview.setOnClickListener(v -> listener.showPreview(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder{

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
