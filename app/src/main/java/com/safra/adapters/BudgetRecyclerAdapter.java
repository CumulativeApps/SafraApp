package com.safra.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.extensions.ViewExtension;
import com.safra.models.BudgetListModel;

import java.text.DecimalFormat;
import java.util.List;

public class BudgetRecyclerAdapter extends RecyclerView.Adapter<BudgetRecyclerAdapter.TaskViewHolder> {
    private List<BudgetListModel.Datum> taskList;

    public BudgetRecyclerAdapter(List<BudgetListModel.Datum> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        BudgetListModel.Datum task = taskList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        holder.txtResourceName.setText(task.getName());
        holder.txtTaskQuantity.setText(String.valueOf(task.getQuantity()));

        String formattedUnitPrice = decimalFormat.format(task.getPrice());
        holder.txtUnitPrice.setText(formattedUnitPrice + " MZN");

        String formattedSubTotal = decimalFormat.format(task.getSubtotal());
        holder.txtSubTotal.setText(formattedSubTotal + " MZN");

        ViewExtension.makeVisible(holder.clExpandLayout, task.isExpanded());
        ViewExtension.toggleArrow(holder.ivExpandDetail, task.isExpanded());
        holder.itemView.setOnClickListener(v -> {
            if (!task.isExpanded()) {
                ViewExtension.expandLayout(holder.clExpandLayout, !task.isExpanded());
                task.setExpanded(ViewExtension.toggleArrow(holder.ivExpandDetail, !task.isExpanded()));
            }
        });
        holder.ivExpandDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewExtension.expandLayout(holder.clExpandLayout, !task.isExpanded());
                task.setExpanded(ViewExtension.toggleArrow(view, !task.isExpanded()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtResourceName, txtTaskQuantity, txtUnitPrice,txtSubTotal,txtTotal;
        ImageView ivExpandDetail;
        ConstraintLayout clExpandLayout;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtResourceName = itemView.findViewById(R.id.tvResourcesName);
            txtTaskQuantity = itemView.findViewById(R.id.tvQuantity);
            txtUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            txtSubTotal = itemView.findViewById(R.id.tvSubTotal);
            ivExpandDetail = itemView.findViewById(R.id.ivExpandDetail);
            clExpandLayout = itemView.findViewById(R.id.clExpandLayout);
        }
    }
}



