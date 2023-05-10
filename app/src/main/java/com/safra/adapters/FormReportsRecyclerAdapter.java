package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerFormReportBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.FormItem;

import java.util.ArrayList;
import java.util.List;

public class FormReportsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_FORM = 1;

    private final Context context;
    private final List<FormItem> formList = new ArrayList<>();
    private final List<FormItem> formData = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void viewReport(FormItem item, int position);
    }

    public FormReportsRecyclerAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return formList.get(position) != null ? VIEW_FORM : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_FORM) {
            RecyclerFormReportBinding binding = RecyclerFormReportBinding.inflate(inflater, parent, false);
            return new FormViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FormViewHolder)
            ((FormViewHolder)holder).bindView(formList.get(position));
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    public void addFormList(List<FormItem> formList) {
        this.formList.addAll(formList);
        this.formData.addAll(formList);
    }

    public void removeForm(int position){
        FormItem formItem = getItem(position);
        formList.remove(position);
        notifyItemRemoved(position);
        formData.remove(formItem);

    }

    public FormItem getItem(int position){
        return formList.get(position);
    }

    public void clearLists(){
        formList.clear();
        formData.clear();

        notifyDataSetChanged();
    }

    public void searchForm(String searchText){
        searchText = searchText.toLowerCase();
        formList.clear();
        if(searchText.isEmpty()){
            formList.addAll(formData);
        } else {
            for(FormItem fi : formData){
                if(fi.getFormName().toLowerCase().contains(searchText))
                    formList.add(fi);
            }
        }

        notifyDataSetChanged();
    }

    class FormViewHolder extends RecyclerView.ViewHolder {
        RecyclerFormReportBinding binding;

        public FormViewHolder(@NonNull RecyclerFormReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(FormItem item) {
            binding.btnViewReport.setText(LanguageExtension.setText("view_report", context.getString(R.string.view_report)));

            binding.tvFormName.setText(item.getFormName());


            binding.btnViewReport.setOnClickListener(v -> listener.viewReport(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
