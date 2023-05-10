package com.safra.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.databinding.RecyclerAccessBinding;
import com.safra.models.AccessItem;

import java.util.ArrayList;
import java.util.List;

public class AccessRecyclerAdapter extends RecyclerView.Adapter<AccessRecyclerAdapter.AccessViewHolder> {

    private final List<AccessItem> accessList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(AccessItem item, int position);
    }

    public AccessRecyclerAdapter(List<AccessItem> accessList, OnItemClickListener listener) {
        this.accessList = accessList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAccessBinding binding = RecyclerAccessBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AccessViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessViewHolder holder, int position) {
        holder.bindView(accessList.get(position));
    }

    @Override
    public int getItemCount() {
        return accessList.size();
    }

    class AccessViewHolder extends RecyclerView.ViewHolder{
        RecyclerAccessBinding binding;

        public AccessViewHolder(@NonNull RecyclerAccessBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(AccessItem accessItem) {
            binding.cbAccess.setText(accessItem.getAccessName());

            binding.cbAccess.setChecked(accessItem.isSelected());

            binding.cbAccess.setOnClickListener(v -> updateList(getAbsoluteAdapterPosition()));
        }
    }

    private void updateList(int position){
        accessList.get(position).setSelected(!accessList.get(position).isSelected());
        notifyItemChanged(position);
        listener.onClick(accessList.get(position), position);
    }

    public List<AccessItem> getSelectedList(){
        List<AccessItem> selectedList = new ArrayList<>();
        for (AccessItem a : accessList){
            if(a.isSelected())
                selectedList.add(a);
        }

        return selectedList;
    }
}
