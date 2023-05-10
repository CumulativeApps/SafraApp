package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerProjectsBinding;
import com.safra.databinding.RecyclerUsersBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.ProjectListResponseModel;
import com.safra.models.UserItem;

import java.util.ArrayList;
import java.util.List;

public class ProjectRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_PROJECT = 1;

    private final Context context;
    private final List<ProjectListResponseModel> userList = new ArrayList<>();
    private final List<ProjectListResponseModel> userData = new ArrayList<>();
    private final ProjectRecyclerAdapter.OnItemClickListener listener;

    public ProjectRecyclerAdapter(Context context, ProjectRecyclerAdapter.OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDelete(ProjectListResponseModel item, int position);

        void onEdit(ProjectListResponseModel item, int position);

        void onView(ProjectListResponseModel item, int position);

        void changeStatus(View itemView, ProjectListResponseModel item, int position);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_PROJECT : VIEW_PROGRESS;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_PROJECT) {
            RecyclerProjectsBinding binding = RecyclerProjectsBinding.inflate(inflater, parent, false);
            return new ProjectRecyclerAdapter.ProjectViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProjectRecyclerAdapter.ProgressViewHolder(binding);
        }    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectRecyclerAdapter.ProjectViewHolder)
            ((ProjectRecyclerAdapter.ProjectViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();

    }

    public void addUserList(List<ProjectListResponseModel> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        ProjectListResponseModel ProjectListResponseModel = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(ProjectListResponseModel);

    }

    public ProjectListResponseModel getItem(int position){
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
            for(ProjectListResponseModel ui : userData){
                if(ui.getName().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {
        RecyclerProjectsBinding binding;

        public ProjectViewHolder(@NonNull RecyclerProjectsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ProjectListResponseModel item) {
            System.out.println("userList.size()" + userList.size());
            binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date", context.getString(R.string.start_date)));
            binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date", context.getString(R.string.end_date)));
            binding.tvFinancierTitle.setText(LanguageExtension.setText("financier", context.getString(R.string.financier)));
            binding.tvCurrencyTitle.setText(LanguageExtension.setText("currency", context.getString(R.string.currency)));
            binding.tvStatusTitle.setText(LanguageExtension.setText("status", context.getString(R.string.status)));
            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
//            binding.tvViewDetails.setText(LanguageExtension.setText("view_details", context.getString(R.string.view_details)));

            binding.tvProjectName.setText(item.getName());

            if (item.getStartDate() != null)
                binding.tvStartDate.setText(item.getStartDate());
            else
                binding.tvStartDate.setText("-");

            if (item.getEndDate() != null)
                binding.tvEndDate.setText(item.getEndDate());
            else
                binding.tvEndDate.setText("-");

            if (item.getFinancier() != null)
                binding.tvFinancier.setText(item.getFinancier());
            else
                binding.tvFinancier.setText("-");




//            if (item.getStatus() != null)
//                binding.tvStatus.setIn(item.getStatus());
//            else
//                binding.tvStatus.setText("-");


            ViewExtension.makeVisible(binding.ivDelete, item.isDeletable());
            ViewExtension.makeVisible(binding.ivEdit, item.isEditable());
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

            binding.ivEdit.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

            binding.tvChangeStatus.setOnClickListener(v -> listener.changeStatus(v, item, getAbsoluteAdapterPosition()));

//            binding.tvViewDetails.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));

            if(item.getPriorityName() != null && !item.getPriorityName().isEmpty())
                binding.tvCurrency.setText(item.getPriorityName());
            else if(item.getCurrency() > 0){
                switch (item.getCurrency()){
                    case 4:
                        binding.tvCurrency.setText(LanguageExtension.setText("metical_mzn", context.getString(R.string.metical_mzn)));
                        break;
                    case 3:
                        binding.tvCurrency.setText(LanguageExtension.setText("dolar_usd", context.getString(R.string.dolar_usd)));
                        break;
                    case 2:
                        binding.tvCurrency.setText(LanguageExtension.setText("euro_eur", context.getString(R.string.euro_eur)));
                        break;
                    case 1:
                        binding.tvCurrency.setText(LanguageExtension.setText("rand_zar", context.getString(R.string.rand_zar)));
                        break;
                }
            }
            switch (item.getStatus()){
                case 0:
                    binding.tvStatus.setText(LanguageExtension.setText("pending", context.getString(R.string.pending)));
                    break;
                case 1:
                    binding.tvStatus.setText(LanguageExtension.setText("in_progress", context.getString(R.string.in_progress)));
                    break;
                default:
                    binding.tvStatus.setText(LanguageExtension.setText("completed", context.getString(R.string.completed)));

            }
        }


    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
