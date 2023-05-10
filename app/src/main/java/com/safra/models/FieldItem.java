package com.safra.models;

import com.google.gson.annotations.SerializedName;

public class FieldItem {

    private int fieldType;
    private String fieldName;
    private int fieldIcon;

    public FieldItem() {
    }

    public FieldItem(int fieldType, String fieldName, int fieldIcon) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.fieldIcon = fieldIcon;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getFieldIcon() {
        return fieldIcon;
    }

    public void setFieldIcon(int fieldIcon) {
        this.fieldIcon = fieldIcon;
    }
}
