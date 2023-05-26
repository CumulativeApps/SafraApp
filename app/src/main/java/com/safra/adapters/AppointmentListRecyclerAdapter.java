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
import com.safra.models.AppointmentListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AppointmentListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_APPOINTMENT_LIST = 1;

    private final Context context;
    private final List<AppointmentListModel.Data.Patient.Appointment> userList = new ArrayList<>();
    private final List<AppointmentListModel.Data.Patient.Appointment> userData = new ArrayList<>();
    private final AppointmentListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(AppointmentListModel.Data.Patient.Appointment item, int position);

        void onEdit(AppointmentListModel.Data.Patient.Appointment item, int position);

        void onView(AppointmentListModel.Data.Patient.Appointment item, int position);

        void changeStatus(View itemView, AppointmentListModel.Data.Patient.Appointment item, int position);
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

    public void addUserList(List<AppointmentListModel.Data.Patient.Appointment> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position) {
        AppointmentListModel.Data.Patient.Appointment userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public AppointmentListModel.Data.Patient.Appointment getItem(int position) {
        return userList.get(position);
    }

    public void clearLists() {
        userList.clear();
        userData.clear();

        notifyDataSetChanged();
    }

    public void searchUser(String searchText) {
        searchText = searchText.toLowerCase();
        userList.clear();
        if (searchText.isEmpty()) {
            userList.addAll(userData);
        } else {
            for (AppointmentListModel.Data.Patient.Appointment ui : userData) {
                if (ui.getStart_date().toLowerCase().contains(searchText))
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

        public void bindView(AppointmentListModel.Data.Patient.Appointment item) {
            binding.tvAppointmentTitle.setText(LanguageExtension.setText("appointment", context.getString(R.string.appointment)));
            binding.tvNoteTitle.setText(LanguageExtension.setText("note", context.getString(R.string.note)));
            binding.tvStatusTitle.setText(LanguageExtension.setText("status", context.getString(R.string.status)));
            binding.tvCreatedDateTitle.setText(LanguageExtension.setText("created_date", context.getString(R.string.created_date)));
            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            if (item.getFullName().equals(" null null null")) {
                binding.tvPatientName.setText("Unidentified patient");
            } else {
                binding.tvPatientName.setText(item.getFullName());
            }


            binding.tvAppointment.setText(item.getStart_date() + " " + item.getStart_time());

            binding.tvNote.setText(item.getNote());

            if (item.getStatus() == 0) {
                binding.tvStatus.setText("Active");
            } else {
                binding.tvStatus.setText("Completed");
            }

            String inputDate1 = String.valueOf(item.getCreated_at());
            String inputFormat1 = "EEE MMM dd HH:mm:ss 'GMT'Z yyyy";
            String outputFormat1 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

            SimpleDateFormat inputDateFormat1 = new SimpleDateFormat(inputFormat1, Locale.US);
            SimpleDateFormat outputDateFormat1 = new SimpleDateFormat(outputFormat1, Locale.US);
            outputDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                Date date1 = inputDateFormat1.parse(inputDate1);
                String outputDate1 = outputDateFormat1.format(date1);


                String inputDate2 = outputDate1;
                String inputFormat2 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
                String outputFormat2 = "yyyy-MM-dd HH:mm:ss";

                SimpleDateFormat inputDateFormat2 = new SimpleDateFormat(inputFormat2, Locale.US);
                SimpleDateFormat outputDateFormat2 = new SimpleDateFormat(outputFormat2, Locale.US);

                Date date2 = inputDateFormat2.parse(inputDate2);
                String outputDate2 = outputDateFormat2.format(date2);

                binding.tvCreatedDate.setText(outputDate2);
            } catch (ParseException e) {
                e.printStackTrace();
            }


//            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
//            ViewExtension.makeVisible(binding.tvChangeStatus, item.isChangeable());
//            ViewExtension.makeVisible(binding.ivView, item.isViewable());
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

            binding.ivView.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));
        }

    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
