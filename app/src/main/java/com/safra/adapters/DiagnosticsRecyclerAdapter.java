package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerAllergiesBinding;
import com.safra.databinding.RecyclerDiagnosticsBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.PatientListModel;
import com.safra.models.UserItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DiagnosticsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_DIAGNOSTICS = 1;

    private final Context context;
    private final List<PatientListModel.Data.Patient> userList = new ArrayList<>();
    private final List<PatientListModel.Data.Patient> userData = new ArrayList<>();
    private final DiagnosticsRecyclerAdapter.OnItemClickListener listener;

    public DiagnosticsRecyclerAdapter(Context context, DiagnosticsRecyclerAdapter.OnItemClickListener listener) {
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
        return userList.get(position) != null ? VIEW_DIAGNOSTICS : VIEW_PROGRESS;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_DIAGNOSTICS) {
            RecyclerDiagnosticsBinding binding = RecyclerDiagnosticsBinding.inflate(inflater, parent, false);
            return new DiagnosticsRecyclerAdapter.DiagnosticsViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new DiagnosticsRecyclerAdapter.ProgressViewHolder(binding);
        }    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DiagnosticsRecyclerAdapter.DiagnosticsViewHolder)
            ((DiagnosticsRecyclerAdapter.DiagnosticsViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<PatientListModel.Data.Patient> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        PatientListModel.Data.Patient userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public PatientListModel.Data.Patient getItem(int position){
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
            for(PatientListModel.Data.Patient ui : userData){
                if(ui.getFirst_name().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class DiagnosticsViewHolder extends RecyclerView.ViewHolder {
        RecyclerDiagnosticsBinding binding;

        public DiagnosticsViewHolder(@NonNull RecyclerDiagnosticsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(PatientListModel.Data.Patient item) {
            binding.tvGenderTitle.setText(LanguageExtension.setText("gender", context.getString(R.string.gender)));
            binding.tvAgeTitle.setText(LanguageExtension.setText("age", context.getString(R.string.age)));
            binding.tvBirthDateTitle.setText(LanguageExtension.setText("birthdate", context.getString(R.string.birthdate)));
            binding.tvPhoneNoTitle.setText(LanguageExtension.setText("phone_no", context.getString(R.string.phone_no)));
            binding.tvAddressTitle.setText(LanguageExtension.setText("address", context.getString(R.string.address)));
            binding.tvViewDetails.setText(LanguageExtension.setText("diagnostics", context.getString(R.string.diagnostics)));


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