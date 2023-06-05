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
import com.safra.databinding.RecyclerActiveVisitsBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.fragments.AllergiesListFragment;
import com.safra.fragments.AppointmentListFragment;
import com.safra.fragments.CaptureVitalListFragment;
import com.safra.fragments.DiagnosticsListFragment;
import com.safra.models.ActionTaskListModel;
import com.safra.models.ActiveVisitsModel;
import com.safra.models.PatientListModel;

import java.util.ArrayList;
import java.util.List;

public class ActiveVisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ACTIVE_VISITS = 1;

    private final Context context;
    private final List<ActiveVisitsModel.Datum> userList = new ArrayList<>();
    private final List<ActiveVisitsModel.Datum> userData = new ArrayList<>();
    FragmentManager fragmentManager;
    private final List<PatientListModel.Data.Patient> userList1 = new ArrayList<>();
    private final List<PatientListModel.Data.Patient> userData1 = new ArrayList<>();
    private final ActiveVisitsRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(ActiveVisitsModel.Datum item, int position);

        void onEdit(ActiveVisitsModel.Datum item, int position);

        void onView(ActiveVisitsModel.Datum item, int position);

        void changeStatus(View itemView, ActiveVisitsModel.Datum item, int position);
    }

    public ActiveVisitsRecyclerAdapter(Context context, FragmentManager fragmentManager, ActiveVisitsRecyclerAdapter.OnItemClickListener listener) {
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
            RecyclerActiveVisitsBinding binding = RecyclerActiveVisitsBinding.inflate(inflater, parent, false);
            return new ActiveVisitsRecyclerAdapter.ActiveVisitsViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ActiveVisitsRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ActiveVisitsRecyclerAdapter.ActiveVisitsViewHolder)
            ((ActiveVisitsRecyclerAdapter.ActiveVisitsViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<ActiveVisitsModel.Datum> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void addUserList1(List<PatientListModel.Data.Patient> userList) {
        this.userList1.addAll(userList);
        this.userData1.addAll(userList);
    }

    public void removeUser(int position) {
        ActiveVisitsModel.Datum userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public ActiveVisitsModel.Datum getItem(int position) {
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
            for (ActiveVisitsModel.Datum ui : userData) {
                if (ui.getPatient_name().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class ActiveVisitsViewHolder extends RecyclerView.ViewHolder {
        RecyclerActiveVisitsBinding binding;

        public ActiveVisitsViewHolder(@NonNull RecyclerActiveVisitsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ActiveVisitsModel.Datum item) {
            binding.tvActiveVisitsTitle.setText(LanguageExtension.setText("active_visits", context.getString(R.string.active_visits)));
//            binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date", context.getString(R.string.end_date)));
//            binding.tvFinancierTitle.setText(LanguageExtension.setText("financier", context.getString(R.string.financier)));
//            binding.tvCurrencyTitle.setText(LanguageExtension.setText("currency", context.getString(R.string.currency)));
//            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            binding.tvPatientName.setText(item.getPatient_name());

            if (item.getStart_date() != null)
                binding.tvActiveVisits.setText(item.getStart_date() + " " + item.getStart_time());
            else
                binding.tvActiveVisits.setText("-");

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
            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

//            String[] data = {"Select Action", "Appointment Scheduling", "Diagnostics", "Capture Vitals", "Allergies", "Medication"};
//
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            binding.spinner.setAdapter(adapter);

            PatientListModel.Data.Patient patientList = new PatientListModel.Data.Patient();

//            List<PatientListModel.Data.Patient> userList2 = userList1;
//
//            for (PatientListModel.Data.Patient user : userList2) {
//                String name = user.getFirst_name();
////                String id = String.valueOf(user.getUserId());
////                namesList.add(name);
////                namesList.add(id);
//
//
//
//            }
//            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedItem = (String) parent.getItemAtPosition(position);
//                    if (selectedItem.equals("Appointment Scheduling")) {
//                        AppointmentListFragment dialogD = new AppointmentListFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putLong("appointment_patient_id", item.getPatient_id());
//                        bundle.putString("f_name", patientList.getFirst_name());
//                        System.out.println("patientList.getFirst_name():-" +patientList.getFirst_name());
//                        bundle.putString("m_name", patientList.getMiddle_name());
//                        bundle.putString("l_name", patientList.getLast_name());
//                        dialogD.setArguments(bundle);
//                        dialogD.show(fragmentManager, AppointmentListFragment.TAG);
//                    } else if (selectedItem.equals("Diagnostics")) {
//                        DiagnosticsListFragment dialogD = new DiagnosticsListFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putLong("patient_id", item.getPatient_id());
//                        bundle.putString("f_name", patientList.getFirst_name());
//                        bundle.putString("m_name", patientList.getMiddle_name());
//                        bundle.putString("l_name", patientList.getLast_name());
////                bundle.putLong("online_id", item.getUserOnlineId());
//                        dialogD.setArguments(bundle);
//                        dialogD.show(fragmentManager, DiagnosticsListFragment.TAG);
//                    } else if (selectedItem.equals("Capture Vitals")) {
//                        CaptureVitalListFragment dialogD = new CaptureVitalListFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putLong("capture_patient_id", item.getPatient_id());
//                        bundle.putString("f_name", patientList.getFirst_name());
//                        bundle.putString("m_name", patientList.getMiddle_name());
//                        bundle.putString("l_name", patientList.getLast_name());
//                        dialogD.setArguments(bundle);
//                        dialogD.show(fragmentManager, CaptureVitalListFragment.TAG);
//                    } else if (selectedItem.equals("Allergies")) {
//                        AllergiesListFragment dialogD = new AllergiesListFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putLong("patient_id", item.getPatient_id());
//                        dialogD.setArguments(bundle);
//                        bundle.putString("f_name", patientList.getFirst_name());
//                        bundle.putString("m_name", patientList.getMiddle_name());
//                        bundle.putString("l_name", patientList.getLast_name());
//                        dialogD.show(fragmentManager, AllergiesListFragment.TAG);
//                        // Handle other options if needed
//                    } else if (selectedItem.equals("Medication")) {
//                        // Handle other options if needed
//                    }
//                    // Perform your desired action with the selected patientList
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                    // Handle the case when no item is selected
//                }
//            });

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

