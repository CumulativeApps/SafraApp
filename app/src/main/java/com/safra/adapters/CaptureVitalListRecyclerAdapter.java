package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerCapturevitalListBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.CaptureVitalListModel;
import com.safra.models.UserItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CaptureVitalListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ALLERGIES_LIST = 1;

    private final Context context;
    private final List<CaptureVitalListModel.Data.Vital> userList = new ArrayList<>();
    private final List<CaptureVitalListModel.Data.Vital> userData = new ArrayList<>();
    private final CaptureVitalListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(CaptureVitalListModel.Data.Vital item, int position);

        void onEdit(CaptureVitalListModel.Data.Vital item, int position);

        void onView(CaptureVitalListModel.Data.Vital item, int position);

        void changeStatus(View itemView, CaptureVitalListModel.Data.Vital item, int position);
    }

    public CaptureVitalListRecyclerAdapter(Context context, CaptureVitalListRecyclerAdapter.OnItemClickListener listener) {
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
            RecyclerCapturevitalListBinding binding = RecyclerCapturevitalListBinding.inflate(inflater, parent, false);
            return new CaptureVitalListRecyclerAdapter.CaptureVitalListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new CaptureVitalListRecyclerAdapter.ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CaptureVitalListRecyclerAdapter.CaptureVitalListViewHolder)
            ((CaptureVitalListRecyclerAdapter.CaptureVitalListViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<CaptureVitalListModel.Data.Vital> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        CaptureVitalListModel.Data.Vital userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);

    }

    public CaptureVitalListModel.Data.Vital getItem(int position){
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
            for(CaptureVitalListModel.Data.Vital ui : userData){
                if(ui.getBlood_pressure().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class CaptureVitalListViewHolder extends RecyclerView.ViewHolder {
        RecyclerCapturevitalListBinding binding;

        public CaptureVitalListViewHolder(@NonNull RecyclerCapturevitalListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(CaptureVitalListModel.Data.Vital item) {
            binding.tvHeightTitle.setText(LanguageExtension.setText("height", context.getString(R.string.height)));
            binding.tvWeightTitle.setText(LanguageExtension.setText("weight", context.getString(R.string.weight)));
            binding.tvBMITitle.setText(LanguageExtension.setText("bmi", context.getString(R.string.bmi)));
            binding.tvTemperatureTitle.setText(LanguageExtension.setText("temperature", context.getString(R.string.temperature)));
            binding.tvPulseTitle.setText(LanguageExtension.setText("pulse", context.getString(R.string.pulse)));
            binding.tvRespiratoryRateTitle.setText(LanguageExtension.setText("respiratory_rate", context.getString(R.string.respiratory_rate)));
            binding.tvBloodPressureTitle.setText(LanguageExtension.setText("blood_pressure", context.getString(R.string.blood_pressure)));
            binding.tvBloodOxygenTitle.setText(LanguageExtension.setText("blood_oxygen", context.getString(R.string.blood_oxygen)));
            binding.tvCreatedDateTitle.setText(LanguageExtension.setText("created_date", context.getString(R.string.created_date)));

            if (item.getFullName().equals(" null null null")) {
                binding.tvPatientName.setText("Unidentified patient");
            } else {
                binding.tvPatientName.setText(item.getFullName());
            }



            binding.tvHeight.setText(String.valueOf(item.getHeight()+ " cm"));
            binding.tvWeight.setText(String.valueOf(item.getWeight()+ " kg"));
            binding.tvBMI.setText(String.valueOf(String.valueOf(item.getbMI())));
            binding.tvTemperature.setText(String.valueOf(item.getTemperature()+ " °C"));
            binding.tvPulse.setText(String.valueOf(item.getPulse()+ "/min"));
            binding.tvRespiratoryRate.setText(String.valueOf(item.getRespiratory_rate()+ "/min"));
            binding.tvBloodPressure.setText(String.valueOf(item.getBlood_pressure()));
            binding.tvBloodOxygen.setText(String.valueOf(item.getBlood_oxygen_saturation()+ " %"));

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
