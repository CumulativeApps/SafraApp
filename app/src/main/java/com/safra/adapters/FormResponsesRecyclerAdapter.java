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
import com.safra.databinding.RecyclerFormResponsesBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.ResponseItem;

import java.util.List;

public class FormResponsesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_RESPONSE = 1;

    private final Context context;
    private final List<ResponseItem> responseList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onView(ResponseItem item, int position);
    }

    public FormResponsesRecyclerAdapter(Context context, List<ResponseItem> responseList,
                                        OnItemClickListener listener) {
        this.context = context;
        this.responseList = responseList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return responseList.get(position) != null ? VIEW_RESPONSE : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == VIEW_RESPONSE) {
            RecyclerFormResponsesBinding binding = RecyclerFormResponsesBinding.inflate(inflater, parent, false);
            return new ResponseViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ResponseViewHolder)
            ((ResponseViewHolder)holder).bindView(responseList.get(position));
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }

    class ResponseViewHolder extends RecyclerView.ViewHolder {
        RecyclerFormResponsesBinding binding;
        TextView userName, language, submitDateTitle, submitDate;
        TextView view;

        public ResponseViewHolder(@NonNull RecyclerFormResponsesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ResponseItem item) {
            binding.tvSubmitDateTitle.setText(LanguageExtension.setText("submit_on", context.getString(R.string.submit_on)));
            binding.tvViewForm.setText(LanguageExtension.setText("view_response", context.getString(R.string.view_response)));

            binding.tvUserName.setText(item.getUserName());
//            language.setText(item.getLanguageTitle());

            if (item.getSubmitDate() != null)
                binding.tvSubmitDate.setText(item.getSubmitDate());

            binding.tvViewForm.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
