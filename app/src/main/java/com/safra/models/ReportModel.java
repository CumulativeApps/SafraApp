package com.safra.models;

public class ReportModel {
    private String value;
    private String name;
    private String pt_name;

    public ReportModel(String value, String name, String pt_name) {
        this.value = value;
        this.name = name;
        this.pt_name = pt_name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getPtName() {
        return pt_name;
    }

    @Override
    public String toString() {
        return name;
    }
}
