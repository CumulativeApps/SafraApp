package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerOverviewDisgnosisListBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.DiagnosticsListModel;

import java.util.ArrayList;
import java.util.List;

public class OverviewDiagnosticsListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_PATIENT = 1;

    private final Context context;
    private final List<DiagnosticsListModel.Data.Patient.Diagnostic> userList = new ArrayList<>();
    private final List<DiagnosticsListModel.Data.Patient.Diagnostic> userData = new ArrayList<>();
    private final OverviewDiagnosticsListAdapter.OnItemClickListener listener;

    public OverviewDiagnosticsListAdapter(Context context, OverviewDiagnosticsListAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDelete(DiagnosticsListModel.Data.Patient.Diagnostic item, int position);

        void onEdit(DiagnosticsListModel.Data.Patient.Diagnostic item, int position);

        void onView(DiagnosticsListModel.Data.Patient.Diagnostic item, int position);

        void changeStatus(View itemView, DiagnosticsListModel.Data.Patient.Diagnostic item, int position);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_PATIENT : VIEW_PROGRESS;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_PATIENT) {
            RecyclerOverviewDisgnosisListBinding binding = RecyclerOverviewDisgnosisListBinding.inflate(inflater, parent, false);
            return new OverviewDiagnosticsListAdapter.OverviewDiagnosticsListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new OverviewDiagnosticsListAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OverviewDiagnosticsListAdapter.OverviewDiagnosticsListViewHolder)
            ((OverviewDiagnosticsListAdapter.OverviewDiagnosticsListViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<DiagnosticsListModel.Data.Patient.Diagnostic> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position) {
        DiagnosticsListModel.Data.Patient.Diagnostic userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public DiagnosticsListModel.Data.Patient.Diagnostic getItem(int position) {
        System.out.println("userList.get(position):-" +userList.get(position));
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
            for (DiagnosticsListModel.Data.Patient.Diagnostic ui : userData) {
                if (ui.getResult().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class OverviewDiagnosticsListViewHolder extends RecyclerView.ViewHolder {
        RecyclerOverviewDisgnosisListBinding binding;

        public OverviewDiagnosticsListViewHolder(@NonNull RecyclerOverviewDisgnosisListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(DiagnosticsListModel.Data.Patient.Diagnostic item) {
            binding.tvDiagnosticsNameTitle.setText(LanguageExtension.setText("chemical_name", context.getString(R.string.chemical_name)));


//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            binding.tvDiagnosticsNameTitle.setText(item.getResult());
//
//
//        binding.tvChemicalName.setText(item.getChemical());
//        binding.tvProvider.setText(item.getProviderName());
//
//        if (item.getStatus() == 0) {
//            binding.tvStatus.setText("unavailable");
//        } else {
//            binding.tvStatus.setText("Available");
//        }




//            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());

//            ViewExtension.makeVisible(binding.tvViewDetails, item.isViewable());
//        ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
//        ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

//            String[] data = {"","Appointment Scheduling", "Diagnostics", "Capture Vitals", "Allergies","Medication"};
//
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            binding.spinner.setAdapter(adapter);
//
//
//            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedItem = (String) parent.getItemAtPosition(position);
//                    if (selectedItem.equals("Allergies")) {
//                        // Redirect to Allergies Fragment
//                        AllergiesFragment allergiesFragment = new AllergiesFragment();
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.fragment_container, allergiesFragment)
//                                .commit();
//                    } else {
//                        // Handle other options if needed
//                    }
//                    // Perform your desired action with the selected item
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                    // Handle the case when no item is selected
//                }
//            });

//        itemView.setOnClickListener(v -> {
//            if (!item.isExpanded()) {
//                ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
//                item.setExpanded(ViewExtension.toggleArrow(binding.ivExpandDetail, !item.isExpanded()));
//            }
//        });
//
//        binding.ivExpandDetail.setOnClickListener(v -> {
//            ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
//            item.setExpanded(ViewExtension.toggleArrow(v, !item.isExpanded()));
////                listener.onExpand(item, getAbsoluteAdapterPosition());
//        });

//            binding.ivDelete.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));

//        binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));


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
