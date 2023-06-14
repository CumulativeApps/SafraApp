package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.HEALTH_RECORD_REPORT_DATA_LIST;
import static com.safra.utilities.Common.HEALTH_RECORD_REPORT_LIST;
import static com.safra.utilities.Common.SERVER_DATE_FORMAT;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ProvidersReportAdapter;
import com.safra.databinding.FragmentMedicalReportsBinding;
import com.safra.models.ReportListModel;
import com.safra.models.ReportModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MedicalReportsFragment extends Fragment {
    public static final String TAG = "medical_reports";

    private boolean isRemembered;
    private FragmentMedicalReportsBinding binding;
  private FragmentActivity mActivity = null;
    private final int pPosition = -1;
    String name;
    int total;
    String selectedValue;


    private Calendar calendarStart, calendarEnd;
//    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    SimpleDateFormat sdfForServer = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    SimpleDateFormat sdfToShow = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    double percentage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentMedicalReportsBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();
        getReportList(pPosition);
        List<ReportListModel.Data.ChartDatum> chartDataList = new ArrayList<>();
        binding.etStartDate.setFocusableInTouchMode(false);
        binding.etEndDate.setFocusableInTouchMode(false);

        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
//        createPieChart(chartDataList);
        binding.etStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendarStart.set(year, month, dayOfMonth);
                binding.etStartDate.setText(sdfToShow.format(new Date(calendarStart.getTimeInMillis())));
            }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        binding.etEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendarEnd.set(year, month, dayOfMonth);
                binding.etEndDate.setText(sdfToShow.format(new Date(calendarEnd.getTimeInMillis())));
            }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
            datePickerDialog.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            getReportList(pPosition);
//            validateInputs();
//            Log.e(TAG, "onCreate: module_ids -> " + hashMap.get("module_ids"));
//            Log.e(TAG, "onCreate: permission_ids -> " + hashMap.get("permission_ids"));
        });

        return binding.getRoot();
    }

    private void createBarChart(List<ReportListModel.Data.DataPerMouth> chartDataList, int totalSum) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < chartDataList.size(); i++) {
            ReportListModel.Data.DataPerMouth data = chartDataList.get(i);
            int monthIndex = i;
            int total = data.getTotal();

            entries.add(new BarEntry(monthIndex, total));
        }

        // Create a BarDataSet with the entries and label it
        BarDataSet dataSet = new BarDataSet(entries, "Dynamic Data");

        // Set the color of the bars
        dataSet.setColor(Color.BLUE);

        // Create a BarData object and add the dataSet to it
        BarData barData = new BarData(dataSet);

        // Customize the appearance of the chart
        binding.barChart.setData(barData);
        binding.barChart.setFitBars(true);
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.getLegend().setEnabled(false);

        // Customize the X-axis
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // set the interval to 1
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int month = (int) value;
                if (month >= 0 && month < chartDataList.size()) {
                    String monthName = getMonthName(month + 1);
                    return monthName;
                }
                return "";
            }
        });

        // Customize the Y-axis
        YAxis yAxisLeft = binding.barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f); // set the minimum value
        // Set the maximum value based on the totalSum
        yAxisLeft.setAxisMaximum(totalSum + 1); // Add some padding to the maximum value

        YAxis yAxisRight = binding.barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Refresh the chart
        binding.barChart.invalidate();
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }

    private void getReportList(int pPosition) {
        Log.e(TAG, "API CALL " + pPosition);
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_REPORT_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("page_no", String.valueOf(currentPage))
//                .addBodyParameter("search_text", searchText)
//                .setTag("user-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {

                            JSONObject data = response.getJSONObject("data");
                            JSONArray reportList = data.getJSONArray("report_list");

                            List<ReportModel> reports = new ArrayList<>();

                            for (int i = 0; i < reportList.length(); i++) {
                                JSONObject report = reportList.getJSONObject(i);
                                String value = report.getString("value");
                                String name = report.getString("name");
                                String ptName = report.getString("pt_name");

                                ReportModel reportModel = new ReportModel(value, name, ptName);
                                reports.add(reportModel);
                            }


                            ArrayAdapter<ReportModel> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_form_type, reports);
                            adapter.setDropDownViewResource(R.layout.spinner_form_type);
                            binding.spnReportSpinner.setAdapter(adapter);
                            // Now you have the list of reports ready to be displayed in the spinner

                            binding.spnReportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ReportModel selectedItem = (ReportModel) parent.getItemAtPosition(position);
                                    selectedValue = selectedItem.getValue();

                                    switch (selectedValue) {
                                        case "providers":
                                            // Call method for "List of providers"
                                            getReportProviders(pPosition);
                                            binding.providerlistview.setVisibility(View.VISIBLE);
                                            binding.providerlistview1.setVisibility(View.GONE);
                                            binding.providerlistview2.setVisibility(View.GONE);
                                            binding.providerlistview3.setVisibility(View.GONE);
                                            binding.barChart.setVisibility(View.GONE);
                                            break;
                                        case "diagnosis":
                                            // Call method for "List of diagnostics"
                                            getReportProviders(pPosition);
                                            binding.providerlistview1.setVisibility(View.VISIBLE);
                                            binding.providerlistview.setVisibility(View.GONE);
                                            binding.providerlistview2.setVisibility(View.GONE);
                                            binding.providerlistview3.setVisibility(View.GONE);
                                            binding.barChart.setVisibility(View.GONE);
                                            break;
                                        case "patients":
                                            // Call method for "List of patients"
                                            getReportProviders(pPosition);
//                                            createBarChart();
                                            binding.providerlistview2.setVisibility(View.VISIBLE);
                                            binding.barChart.setVisibility(View.VISIBLE);
                                            binding.providerlistview1.setVisibility(View.GONE);
                                            binding.providerlistview.setVisibility(View.GONE);
                                            binding.providerlistview3.setVisibility(View.GONE);
                                            break;
                                        case "number":
                                            // Call method for "Number of Visits"
                                            getReportProviders(pPosition);
                                            binding.providerlistview3.setVisibility(View.VISIBLE);
                                            binding.providerlistview1.setVisibility(View.GONE);
                                            binding.providerlistview2.setVisibility(View.GONE);
                                            binding.providerlistview.setVisibility(View.GONE);
                                            binding.barChart.setVisibility(View.GONE);
                                            break;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // Handle when nothing is selected (optional)
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                    }
                });

    }


    private void getReportProviders(int pPosition) {
        Log.e(TAG, "API CALL " + pPosition);

        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_REPORT_DATA_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("report", selectedValue)
                .addBodyParameter("start_date", sdfForServer.format(new Date(calendarStart.getTimeInMillis())))
                .addBodyParameter("end_date", sdfForServer.format(new Date(calendarEnd.getTimeInMillis())))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            JSONObject data = response.getJSONObject("data");
                            JSONArray chartDataArray = data.getJSONArray("chart_data");
                            JSONArray chartDataArray1 = data.getJSONArray("data_per_mouth");
                            int totalSum = 0;
                            int totalSum1 = 0;

                            if (success == 1) {
                                ReportListModel reportListModel = new Gson().fromJson(response.toString(), ReportListModel.class);
                                List<ReportListModel.Data.Datum> chartDataList = reportListModel.getData().getData();
                                List<ReportListModel.Data.ChartDatum> chartDataList1 = reportListModel.getData().getChartData();
                                List<ReportListModel.Data.DataPerMouth> chartDataList2 = reportListModel.getData().getDataPerMouth();

                                // Set layout manager
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                                // Initialize the adapter
                                ProvidersReportAdapter adapter = new ProvidersReportAdapter(chartDataList);
                                binding.recyclerView.setAdapter(adapter);

                                // Calculate total sum of "total" values
                                for (int i = 0; i < chartDataArray.length(); i++) {
                                    JSONObject item = chartDataArray.getJSONObject(i);
                                    int total = item.getInt("total");
                                    totalSum += total;
                                }

                                for (int i = 0; i < chartDataArray1.length(); i++) {
                                    JSONObject item1 = chartDataArray1.getJSONObject(i);
                                    int total1 = item1.getInt("total");
                                    totalSum1 += total1;
                                }

                                // Create the PieChart
                                createPieChart(chartDataList1, totalSum);
                                createBarChart(chartDataList2,totalSum1);
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void createPieChart(List<ReportListModel.Data.ChartDatum> chartDataList, int totalSum) {
        List<PieEntry> entries = new ArrayList<>();

        // Calculate percentages and add entries
        for (ReportListModel.Data.ChartDatum chartDatum : chartDataList) {
            String name = chartDatum.getName();
            int total = chartDatum.getTotal();
            float percentage = (float) (total * 100) / totalSum;

            entries.add(new PieEntry(percentage, name));
        }
        int[] colors = new int[] {
                Color.parseColor("#FFD1D9E0"),  // Light Gray
                Color.parseColor("#FFC8E6C9"),  // Light Green
                Color.parseColor("#FFBBDEFB"),  // Light Blue
                Color.parseColor("#FFFFF9C4"),  // Light Yellow
                Color.parseColor("#FFB2EBF2"),  // Light Cyan
                Color.parseColor("#FFF8BBD0"),  // Light Pink
                Color.parseColor("#FFE1BEE7"),  // Light Purple
                Color.parseColor("#FFD7CCC8"),  // Light Brown
                Color.parseColor("#FFEEEEEE"),  // Light Silver
                Color.parseColor("#FFFFE0B2")   // Light Orange
        };


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new CustomValueFormatter());

        PieData data = new PieData(dataSet);

        PieChart pieChart = binding.pieChart;
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setRotationAngle(270f);

        // Add a legend to the pie chart
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);

        pieChart.invalidate();
    }


    private static class CustomValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(value) + "%";
        }

        @Override
        public String getPieLabel(float value, PieEntry pieEntry) {
            return pieEntry.getLabel() + " " + getFormattedValue(value);
        }
    }

}