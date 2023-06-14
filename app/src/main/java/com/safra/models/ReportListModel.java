package com.safra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportListModel {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public ReportListModel withSuccess(Integer success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReportListModel withMessage(String message) {
        this.message = message;
        return this;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ReportListModel withData(Data data) {
        this.data = data;
        return this;
    }

    public class Data {

        @SerializedName("data_per_mouth")
        @Expose
        private List<DataPerMouth> dataPerMouth;
        @SerializedName("chart_type")
        @Expose
        private String chartType;
        @SerializedName("data")
        @Expose
        private List<Datum> data;
        @SerializedName("chart_data")
        @Expose
        private List<ChartDatum> chartData;

        public List<DataPerMouth> getDataPerMouth() {
            return dataPerMouth;
        }

        public void setDataPerMouth(List<DataPerMouth> dataPerMouth) {
            this.dataPerMouth = dataPerMouth;
        }

        public Data withDataPerMouth(List<DataPerMouth> dataPerMouth) {
            this.dataPerMouth = dataPerMouth;
            return this;
        }

        public String getChartType() {
            return chartType;
        }

        public void setChartType(String chartType) {
            this.chartType = chartType;
        }

        public Data withChartType(String chartType) {
            this.chartType = chartType;
            return this;
        }

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
            this.data = data;
        }

        public Data withData(List<Datum> data) {
            this.data = data;
            return this;
        }

        public List<ChartDatum> getChartData() {
            return chartData;
        }

        public void setChartData(List<ChartDatum> chartData) {
            this.chartData = chartData;
        }

        public Data withChartData(List<ChartDatum> chartData) {
            this.chartData = chartData;
            return this;
        }

        public class ChartDatum {

            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("total")
            @Expose
            private Integer total;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public ChartDatum withName(String name) {
                this.name = name;
                return this;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public ChartDatum withTotal(Integer total) {
                this.total = total;
                return this;
            }

        }

        public class DataPerMouth {

            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("total")
            @Expose
            private Integer total;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public DataPerMouth withName(String name) {
                this.name = name;
                return this;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public DataPerMouth withTotal(Integer total) {
                this.total = total;
                return this;
            }

        }

        public class Datum {

            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("total")
            @Expose
            private String total;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Datum withName(String name) {
                this.name = name;
                return this;
            }

            public String getTotal() {
                return total;
            }

            public void setTotal(String total) {
                this.total = total;
            }

            public Datum withTotal(String total) {
                this.total = total;
                return this;
            }

        }


    }


}





