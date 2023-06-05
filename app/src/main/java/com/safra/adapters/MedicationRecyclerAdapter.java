package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerMedicationListBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.AllergiesListModel;
import com.safra.models.MedicationListModel;

import java.util.ArrayList;
import java.util.List;

public class MedicationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ALLERGIES_LIST = 1;

    private final Context context;
    private final List<MedicationListModel.Data.Medication> userList = new ArrayList<>();
    private final List<MedicationListModel.Data.Medication> userData = new ArrayList<>();
    private final MedicationRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(MedicationListModel.Data.Medication item, int position);

        void onEdit(MedicationListModel.Data.Medication item, int position);

        void onView(MedicationListModel.Data.Medication item, int position);

        void changeStatus(View itemView, MedicationListModel.Data.Medication item, int position);
    }

    public MedicationRecyclerAdapter(Context context, MedicationRecyclerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_ALLERGIES_LIST : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_ALLERGIES_LIST) {
            RecyclerMedicationListBinding binding = RecyclerMedicationListBinding.inflate(inflater, parent, false);
            return new MedicationRecyclerAdapter.MedicationViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new MedicationRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MedicationRecyclerAdapter.MedicationViewHolder)
            ((MedicationRecyclerAdapter.MedicationViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<MedicationListModel.Data.Medication> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        MedicationListModel.Data.Medication userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public MedicationListModel.Data.Medication getItem(int position){
        return userList.get(position);
    }

    public void clearLists(){
        userList.clear();
        userData.clear();

        notifyDataSetChanged();
    }

    public void searchUser(String searchText){
        searchText = searchText.toLowerCase();
        userList.clear();
        if(searchText.isEmpty()){
            userList.addAll(userData);
        } else {
            for(MedicationListModel.Data.Medication ui : userData){
                if(ui.getInstructions().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class MedicationViewHolder extends RecyclerView.ViewHolder {
        RecyclerMedicationListBinding binding;

        public MedicationViewHolder(@NonNull RecyclerMedicationListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(MedicationListModel.Data.Medication item) {
            binding.tvQuantityTitle.setText(LanguageExtension.setText("quantity", context.getString(R.string.quantity)));
            binding.tvInstructionsTitle.setText(LanguageExtension.setText("instructions", context.getString(R.string.instructions)));
            binding.tvStatusTitle.setText(LanguageExtension.setText("status", context.getString(R.string.status)));
            binding.tvNoteTitle.setText(LanguageExtension.setText("note", context.getString(R.string.note)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            binding.tvAllergenName.setText(item.getMedicine().getName());

                binding.tvQuantity.setText(String.valueOf(item.getQuantity()));


                binding.tvInstructions.setText(item.getInstructions());


                if(item.getStatus() == 0){
                    binding.tvStatus.setText("Waived");

                }else{
                    binding.tvStatus.setText("Medication");

                }
                binding.tvNote.setText(item.getNote());


//            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
//            ViewExtension.makeVisible(binding.tvChangeStatus, item.isChangeable());
//            ViewExtension.makeVisible(binding.tvViewDetails, item.isViewable());
            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

            itemView.setOnClickListener(v -> {
                if (!item.isExpanded()) {
                    ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                    item.setExpanded(ViewExtension.toggleArrow(binding.ivExpandDetail, !item.isExpanded()));
                }
            });

            binding.ivExpandDetail.setOnClickListener(v -> {
                ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                item.setExpanded(ViewExtension.toggleArrow(v, !item.isExpanded()));
//                listener.onExpand(item, getAbsoluteAdapterPosition());
            });

            binding.ivDelete.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));

            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

            binding.tvChangeStatus.setOnClickListener(v -> listener.changeStatus(v, item, getAbsoluteAdapterPosition()));

//            binding.tvViewDetails.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}


