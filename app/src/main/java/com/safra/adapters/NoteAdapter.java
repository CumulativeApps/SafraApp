package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.NoteItemLayoutBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.AppointmentListModel;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_APPOINTMENT_LIST = 1;

    private final Context context;
    private final List<AppointmentListModel.Data.Patient.Appointment.Note> userList = new ArrayList<>();
    private final List<AppointmentListModel.Data.Patient.Appointment.Note> userData = new ArrayList<>();
    private final NoteAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(AppointmentListModel.Data.Patient.Appointment.Note item, int position);

        void onEdit(AppointmentListModel.Data.Patient.Appointment.Note item, int position);

        void onView(AppointmentListModel.Data.Patient.Appointment.Note item, int position);

        void changeStatus(View itemView, AppointmentListModel.Data.Patient.Appointment.Note item, int position);
    }

    public NoteAdapter(Context context, NoteAdapter.OnItemClickListener listener) {
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
            NoteItemLayoutBinding binding = NoteItemLayoutBinding.inflate(inflater, parent, false);
            return new NoteAdapter.NoteAdapterViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new NoteAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoteAdapter.NoteAdapterViewHolder)
            ((NoteAdapter.NoteAdapterViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        System.out.println("userList.size()"+userList.size());
        return userList.size();
    }

    public void addUserList(List<AppointmentListModel.Data.Patient.Appointment.Note> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position) {
        AppointmentListModel.Data.Patient.Appointment.Note userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public AppointmentListModel.Data.Patient.Appointment.Note getItem(int position) {
        return userList.get(position);
    }

    public void clearLists() {
        userList.clear();
        userData.clear();

        notifyDataSetChanged();
    }


    class NoteAdapterViewHolder extends RecyclerView.ViewHolder {
        NoteItemLayoutBinding binding;

        public NoteAdapterViewHolder(@NonNull NoteItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(AppointmentListModel.Data.Patient.Appointment.Note item) {
            binding.noteTextView.setText(LanguageExtension.setText("appointment", context.getString(R.string.appointment)));
            System.out.println("item.note:- "+ item.getNote());
            binding.noteTextView.setText(item.getNote());


        }

    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
