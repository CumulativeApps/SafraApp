package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerFormsBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.FormItem;

import java.util.ArrayList;
import java.util.List;

public class FormsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_FORM = 1;

    private final Context context;
    private final List<FormItem> formList = new ArrayList<>();
    private final List<FormItem> formData = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void showMoreOption(View view, FormItem item, int position);

        void onEdit(FormItem item, int position);

        void onView(FormItem item, int position);

        void onFill(FormItem item, int position);
    }

    public FormsRecyclerAdapter(Context context, OnItemClickListener listener) {
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
            RecyclerFormsBinding binding = RecyclerFormsBinding.inflate(inflater, parent, false);
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
        RecyclerFormsBinding binding;

        public FormViewHolder(@NonNull RecyclerFormsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(FormItem item) {
            binding.tvLanguageTitle.setText(LanguageExtension.setText("language", context.getString(R.string.language)));
            binding.tvExpiryDateTitle.setText(LanguageExtension.setText("expired_on", context.getString(R.string.expired_on)));
            binding.tvFormAccessTitle.setText(LanguageExtension.setText("form_access", context.getString(R.string.form_access)));
            binding.tvFormStatusTitle.setText(LanguageExtension.setText("status", context.getString(R.string.status)));
            binding.tvEditForm.setText(LanguageExtension.setText("edit_form", context.getString(R.string.edit_form)));
            binding.tvViewForm.setText(LanguageExtension.setText("view_form", context.getString(R.string.view_form)));
            binding.tvFillForm.setText(LanguageExtension.setText("fill_form", context.getString(R.string.fill_form)));

            binding.tvFormName.setText(item.getFormName());
            binding.tvLanguage.setText(item.getFormLanguageName());

            if (item.getFormExpiryDate() != null)
                binding.tvExpiryDate.setText(item.getFormExpiryDate());
            else
                binding.tvExpiryDate.setText("-");

            if (item.getFormAccess() == 1) {
                binding.tvFormAccess.setText(LanguageExtension.setText("private_access", context.getString(R.string.private_access)));
            } else if (item.getFormAccess() == 2) {
                binding.tvFormAccess.setText(LanguageExtension.setText("public_access", context .getString(R.string.public_access)));
            }

            switch (item.getFormStatus()){
                case 1:
                    binding.tvFormStatus.setText(LanguageExtension.setText("published", context.getString(R.string.published)));
                    break;
                case 0:
                default:
                    binding.tvFormStatus.setText(LanguageExtension.setText("saved", context.getString(R.string.saved)));

            }

            ViewExtension.makeVisible(binding.tvEditForm, item.isEditable());
            ViewExtension.makeVisible(binding.tvFillForm, item.isFillable());
            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

            itemView.setOnClickListener(v -> {
                if(!item.isExpanded()){
                    ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                    item.setExpanded(ViewExtension.toggleArrow(binding.ivExpandDetail, !item.isExpanded()));
                }
            });

            binding.ivMoreOption.setOnClickListener(v -> listener.showMoreOption(v, item, getAbsoluteAdapterPosition()));

            binding.ivExpandDetail.setOnClickListener(v -> {
                ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                item.setExpanded(ViewExtension.toggleArrow(v, !item.isExpanded()));
//                listener.onExpand(item, getAbsoluteAdapterPosition());
            });

            binding.tvEditForm.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

            binding.tvViewForm.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));

            binding.tvFillForm.setOnClickListener(v -> listener.onFill(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
