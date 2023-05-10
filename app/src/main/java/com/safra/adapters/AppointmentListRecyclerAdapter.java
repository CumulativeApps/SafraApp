package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerAppointmentListBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.UserItem;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_APPOINTMENT_LIST = 1;

    private final Context context;
    private final List<UserItem> userList = new ArrayList<>();
    private final List<UserItem> userData = new ArrayList<>();
    private final AppointmentListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(UserItem item, int position);

        void onEdit(UserItem item, int position);

        void onView(UserItem item, int position);

        void changeStatus(View itemView, UserItem item, int position);
    }

    public AppointmentListRecyclerAdapter(Context context, AppointmentListRecyclerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_APPOINTMENT_LIST : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_APPOINTMENT_LIST) {
            RecyclerAppointmentListBinding binding = RecyclerAppointmentListBinding.inflate(inflater, parent, false);
            return new AppointmentListRecyclerAdapter.AppointmentListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new AppointmentListRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppointmentListRecyclerAdapter.AppointmentListViewHolder)
            ((AppointmentListRecyclerAdapter.AppointmentListViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<UserItem> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        UserItem userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public UserItem getItem(int position){
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
            for(UserItem ui : userData){
                if(ui.getUserName().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class AppointmentListViewHolder extends RecyclerView.ViewHolder {
        RecyclerAppointmentListBinding binding;

        public AppointmentListViewHolder(@NonNull RecyclerAppointmentListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(UserItem item) {
            binding.tvAppointmentTitle.setText(LanguageExtension.setText("appointment", context.getString(R.string.appointment)));
            binding.tvNoteTitle.setText(LanguageExtension.setText("note", context.getString(R.string.note)));
            binding.tvStatusTitle.setText(LanguageExtension.setText("status", context.getString(R.string.status)));
            binding.tvCreatedDateTitle.setText(LanguageExtension.setText("created_date", context.getString(R.string.created_date)));
            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            binding.tvPatientName.setText(item.getUserName());

            if (item.getUserEmail() != null)
                binding.tvAppointment.setText(item.getUserEmail());
            else
                binding.tvAppointment.setText("-");

            if (item.getUserPhone() != null)
                binding.tvNote.setText(item.getUserPhone());
            else
                binding.tvNote.setText("-");

            if (item.getRoleName() != null)
                binding.tvStatus.setText(item.getRoleName());
            else
                binding.tvStatus.setText("-");

            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
            ViewExtension.makeVisible(binding.tvChangeStatus, item.isChangeable());
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

//            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

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
