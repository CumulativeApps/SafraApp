package com.safra.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerOverviewActiveVisitsBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.fragments.AllergiesListFragment;
import com.safra.fragments.AppointmentListFragment;
import com.safra.fragments.CaptureVitalListFragment;
import com.safra.fragments.DiagnosticsListFragment;
import com.safra.models.ActiveVisitsModel;
import com.safra.models.OverviewDataModel;
import com.safra.models.PatientListModel;

import java.util.ArrayList;
import java.util.List;

public class OverviewActiveVisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ACTIVE_VISITS = 1;

    private final Context context;
    private final List<OverviewDataModel.Data.Visit> userList = new ArrayList<>();
    private final List<OverviewDataModel.Data.Visit> userData = new ArrayList<>();
    FragmentManager fragmentManager;
    private final List<PatientListModel.Data.Patient> userList1 = new ArrayList<>();
    private final List<PatientListModel.Data.Patient> userData1 = new ArrayList<>();
    private final OverviewActiveVisitsRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(OverviewDataModel.Data.Visit item, int position);

        void onEdit(OverviewDataModel.Data.Visit item, int position);

        void onView(OverviewDataModel.Data.Visit item, int position);

        void changeStatus(View itemView, OverviewDataModel.Data.Visit item, int position);
    }

    public OverviewActiveVisitsRecyclerAdapter(Context context, FragmentManager fragmentManager, OverviewActiveVisitsRecyclerAdapter.OnItemClickListener listener) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_ACTIVE_VISITS : VIEW_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_ACTIVE_VISITS) {
            RecyclerOverviewActiveVisitsBinding binding = RecyclerOverviewActiveVisitsBinding.inflate(inflater, parent, false);
            return new OverviewActiveVisitsRecyclerAdapter.OverviewActiveVisitsViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new OverviewActiveVisitsRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OverviewActiveVisitsRecyclerAdapter.OverviewActiveVisitsViewHolder)
            ((OverviewActiveVisitsRecyclerAdapter.OverviewActiveVisitsViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        System.out.println("userList.size()-" + userList.size());
        return userList.size();
    }

    public void addUserList(List<OverviewDataModel.Data.Visit> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void addUserList1(List<PatientListModel.Data.Patient> userList) {
        this.userList1.addAll(userList);
        this.userData1.addAll(userList);
    }

    public void removeUser(int position) {
        OverviewDataModel.Data.Visit userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public OverviewDataModel.Data.Visit getItem(int position) {
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
//            for (OverviewDataModel.Data.Visit ui : userData) {
//                if (ui.start_date().toLowerCase().contains(searchText))
//                    userList.add(ui);
//            }
        }

        notifyDataSetChanged();
    }

    class OverviewActiveVisitsViewHolder extends RecyclerView.ViewHolder {
        RecyclerOverviewActiveVisitsBinding binding;

        public OverviewActiveVisitsViewHolder(@NonNull RecyclerOverviewActiveVisitsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(OverviewDataModel.Data.Visit item) {
            binding.tvActiveVisitsTitle.setText(LanguageExtension.setText("active_visits", context.getString(R.string.active_visits)));
//            binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date", context.getString(R.string.end_date)));
//            binding.tvFinancierTitle.setText(LanguageExtension.setText("financier", context.getString(R.string.financier)));
//            binding.tvCurrencyTitle.setText(LanguageExtension.setText("currency", context.getString(R.string.currency)));
//            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));

//            binding.tvPatientName.setText(item.getPatient_name());
            System.out.println("userItem1.getStart_date():-" + item.getStart_date());

            if (item.getStart_date() != null)
                binding.tvActiveVisits.setText(item.getStart_date() + " " + item.getStart_time());
            else
                binding.tvActiveVisits.setText("-");


            if (item.getStatus() == 0)
                binding.tvActiveVisitsTitle.setText("Not done");

            else
                binding.tvActiveVisitsTitle.setText("Completed");


//            if (item.getUserPhone() != null)
//                binding.tvEndDate.setText(item.getUserPhone());
//            else
//                binding.tvEndDate.setText("-");
//
//            if (item.getRoleName() != null)
//                binding.tvFinancier.setText(item.getRoleName());
//            else
//                binding.tvFinancier.setText("-");

//            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
//            ViewExtension.makeVisible(binding.tvChangeStatus, item.isChangeable());
//            ViewExtension.makeVisible(binding.tvViewDetails, item.isViewable());
//            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
//            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());


            PatientListModel.Data.Patient patientList = new PatientListModel.Data.Patient();



//            itemView.setOnClickListener(v -> {
//                if (!item.isExpanded()) {
//                    ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
//                    item.setExpanded(ViewExtension.toggleArrow(binding.ivExpandDetail, !item.isExpanded()));
//                }
//            });
//
//            binding.ivExpandDetail.setOnClickListener(v -> {
//                ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
//                item.setExpanded(ViewExtension.toggleArrow(v, !item.isExpanded()));
////                listener.onExpand(item, getAbsoluteAdapterPosition());
//            });

//            binding.ivDelete.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));
//
//            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));
//
//            binding.tvChangeStatus.setOnClickListener(v -> listener.changeStatus(v, item, getAbsoluteAdapterPosition()));

//            binding.tvViewDetails.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));
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