package com.safra.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;

import com.safra.models.ReportListModel;


import java.util.List;

public class ProvidersReportAdapter extends RecyclerView.Adapter<ProvidersReportAdapter.ViewHolder> {
    private List<ReportListModel.Data.Datum> chartDataList;

    public ProvidersReportAdapter(List<ReportListModel.Data.Datum> chartDataList) {
        this.chartDataList = chartDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_report_provider_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportListModel.Data.Datum chartData = chartDataList.get(position);
        holder.bind(chartData);
    }

    @Override
    public int getItemCount() {
        return chartDataList.size();
    }

    public void setChartDataList(List<ReportListModel.Data.Datum> chartDataList) {
        this.chartDataList = chartDataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView totalTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvReportName);
            totalTextView = itemView.findViewById(R.id.tvReportTotal);
        }

        public void bind(ReportListModel.Data.Datum chartData) {
            nameTextView.setText(chartData.getName());
            totalTextView.setText(String.valueOf(chartData.getTotal()));
        }
    }
}
