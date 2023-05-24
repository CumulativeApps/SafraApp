package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerPatientBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.fragments.AllergiesFragment;
import com.safra.models.PatientListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatientRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_PATIENT = 1;

    private final Context context;
    private final List<PatientListModel.Data.Patient> userList = new ArrayList<>();
    private final List<PatientListModel.Data.Patient> userData = new ArrayList<>();
    private final PatientRecyclerAdapter.OnItemClickListener listener;
    public PatientRecyclerAdapter(Context context, PatientRecyclerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDelete(PatientListModel.Data.Patient item, int position);

        void onEdit(PatientListModel.Data.Patient item, int position);

        void onView(PatientListModel.Data.Patient item, int position);

        void changeStatus(View itemView, PatientListModel.Data.Patient item, int position);
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
            RecyclerPatientBinding binding = RecyclerPatientBinding.inflate(inflater, parent, false);
            return new PatientRecyclerAdapter.PatientViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new PatientRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PatientRecyclerAdapter.PatientViewHolder)
            ((PatientRecyclerAdapter.PatientViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<PatientListModel.Data.Patient> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position) {
        PatientListModel.Data.Patient userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public PatientListModel.Data.Patient getItem(int position) {
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
            for (PatientListModel.Data.Patient ui : userData) {
                if (ui.getFirst_name().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class PatientViewHolder extends RecyclerView.ViewHolder {
        RecyclerPatientBinding binding;

        public PatientViewHolder(@NonNull RecyclerPatientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(PatientListModel.Data.Patient item) {
            binding.tvGenderTitle.setText(LanguageExtension.setText("gender", context.getString(R.string.gender)));
            binding.tvAgeTitle.setText(LanguageExtension.setText("age", context.getString(R.string.age)));
            binding.tvBirthDateTitle.setText(LanguageExtension.setText("birthdate", context.getString(R.string.birthdate)));
            binding.tvPhoneNoTitle.setText(LanguageExtension.setText("phone_no", context.getString(R.string.phone_no)));
            binding.tvAddressTitle.setText(LanguageExtension.setText("address", context.getString(R.string.address)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            String fullName = " "+ item.first_name +" " +item.middle_name + " " + item.last_name;


            if (item.first_name == "null" && item.middle_name == "null" && item.last_name == "null") {

                binding.tvPatientName.setText("Unidentified patient");

            }  else {
                binding.tvPatientName.setText(fullName);
            }


            if (item.getGender() != null)
                binding.tvGender.setText(item.getGender());
            else
                binding.tvGender.setText("-");

            binding.tvBirthDate.setText(item.getBirthdate());
            binding.tvPhoneNo.setText(item.getPhone());

            if (item.getAddress() != null)
                binding.tvAddress.setText(item.getAddress());
            else
                binding.tvAddress.setText("-");


            String birthdateString = item.birthdate;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                // Parse the birthdate string to a Date object
                Date birthdate = format.parse(birthdateString);

                // Get the current date
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();

                // Calculate the difference between the current date and the birthdate
                long differenceInMillis = currentDate.getTime() - birthdate.getTime();

                // Convert the difference from milliseconds to years
                calendar.setTimeInMillis(differenceInMillis);
                int age = calendar.get(Calendar.YEAR) - 1970;
                binding.tvAge.setText(String.valueOf(age));
                // Now you can use the 'age' variable for further processing
                System.out.println("Age: " + age);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());

//            ViewExtension.makeVisible(binding.tvViewDetails, item.isViewable());
            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

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

