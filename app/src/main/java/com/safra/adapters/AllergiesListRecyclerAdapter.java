package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerAllergiesListBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.AllergiesListModel;
import com.safra.models.AllergiesListModel.Data.Patient.Allergy;

import java.util.ArrayList;
import java.util.List;

public class AllergiesListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ALLERGIES_LIST = 1;

    private final Context context;
    private final List<AllergiesListModel.Data.Patient.Allergy> userList = new ArrayList<>();
    private final List<AllergiesListModel.Data.Patient.Allergy> userData = new ArrayList<>();
    private final AllergiesListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(AllergiesListModel.Data.Patient.Allergy item, int position);

        void onEdit(AllergiesListModel.Data.Patient.Allergy item, int position);

        void onView(AllergiesListModel.Data.Patient.Allergy item, int position);

        void changeStatus(View itemView, AllergiesListModel.Data.Patient.Allergy item, int position);
    }

    public AllergiesListRecyclerAdapter(Context context, AllergiesListRecyclerAdapter.OnItemClickListener listener) {
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
            RecyclerAllergiesListBinding binding = RecyclerAllergiesListBinding.inflate(inflater, parent, false);
            return new AllergiesListRecyclerAdapter.AppointmentListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new AllergiesListRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AllergiesListRecyclerAdapter.AppointmentListViewHolder)
            ((AllergiesListRecyclerAdapter.AppointmentListViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<AllergiesListModel.Data.Patient.Allergy> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        AllergiesListModel.Data.Patient.Allergy userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public AllergiesListModel.Data.Patient.Allergy getItem(int position){
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
            for(AllergiesListModel.Data.Patient.Allergy ui : userData){
                if(ui.getAllergen().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class AppointmentListViewHolder extends RecyclerView.ViewHolder {
        RecyclerAllergiesListBinding binding;

        public AppointmentListViewHolder(@NonNull RecyclerAllergiesListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(AllergiesListModel.Data.Patient.Allergy item) {
            binding.tvReactionTitle.setText(LanguageExtension.setText("reaction", context.getString(R.string.reaction)));
            binding.tvSeverityTitle.setText(LanguageExtension.setText("severity", context.getString(R.string.severity)));
            binding.tvCommentTitle.setText(LanguageExtension.setText("comment", context.getString(R.string.comment)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            binding.tvAllergenName.setText(item.getAllergen());

            if (item.getReaction() != null)
                binding.tvReaction.setText(item.getReaction());
            else
                binding.tvReaction.setText("-");

            if (item.getSeverity() != null)
                binding.tvSeverity.setText(item.getSeverity());
            else
                binding.tvSeverity.setText("-");

            if (item.getComment() != null)
                binding.tvComment.setText(item.getComment());
            else
                binding.tvComment.setText("-");

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

//            binding.tvChangeStatus.setOnClickListener(v -> listener.changeStatus(v, item, getAbsoluteAdapterPosition()));

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

