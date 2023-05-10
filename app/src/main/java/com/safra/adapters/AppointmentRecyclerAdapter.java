package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;

import com.safra.databinding.RecyclerAppointmentBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.UserItem;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_APPOINTMENT = 1;

    private final Context context;
    private final List<UserItem> userList = new ArrayList<>();
    private final List<UserItem> userData = new ArrayList<>();
    private final AppointmentRecyclerAdapter.OnItemClickListener listener;

    public AppointmentRecyclerAdapter(Context context, AppointmentRecyclerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDelete(UserItem item, int position);

        void onEdit(UserItem item, int position);

        void onView(UserItem item, int position);

        void changeStatus(View itemView, UserItem item, int position);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_APPOINTMENT : VIEW_PROGRESS;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_APPOINTMENT) {
            RecyclerAppointmentBinding binding = RecyclerAppointmentBinding.inflate(inflater, parent, false);
            return new AppointmentRecyclerAdapter.AppointmentViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new AppointmentRecyclerAdapter.ProgressViewHolder(binding);
        }    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppointmentRecyclerAdapter.AppointmentViewHolder)
            ((AppointmentRecyclerAdapter.AppointmentViewHolder) holder).bindView(userList.get(position));
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

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        RecyclerAppointmentBinding binding;

        public AppointmentViewHolder(@NonNull RecyclerAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(UserItem item) {
            binding.tvGenderTitle.setText(LanguageExtension.setText("gender", context.getString(R.string.gender)));
            binding.tvAgeTitle.setText(LanguageExtension.setText("age", context.getString(R.string.age)));
            binding.tvBirthDateTitle.setText(LanguageExtension.setText("birthdate", context.getString(R.string.birthdate)));
            binding.tvPhoneNoTitle.setText(LanguageExtension.setText("phone_no", context.getString(R.string.phone_no)));
            binding.tvAddressTitle.setText(LanguageExtension.setText("address", context.getString(R.string.address)));
            binding.tvViewDetails.setText(LanguageExtension.setText("appointment_schedule", context.getString(R.string.appointment_schedule)));

            binding.tvPatientName.setText(item.getUserName());

            if (item.getUserEmail() != null)
                binding.tvGender.setText(item.getUserEmail());
            else
                binding.tvGender.setText("-");

            if (item.getUserPhone() != null)
                binding.tvAge.setText(item.getUserPhone());
            else
                binding.tvAge.setText("-");

            if (item.getRoleName() != null)
                binding.tvBirthDate.setText(item.getRoleName());
            else
                binding.tvBirthDate.setText("-");



            ViewExtension.makeVisible(binding.tvViewDetails, item.isViewable());
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




            binding.tvViewDetails.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
