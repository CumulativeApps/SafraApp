package com.safra.models;

public class FormTypeItem {

    private long typeId;
    private String typeName;
    private String ptTypeName;

    public FormTypeItem() {
    }

    public FormTypeItem(long typeId, String typeName, String ptTypeName) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.ptTypeName = ptTypeName;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPtTypeName() {
        return ptTypeName;
    }

    public void setPtTypeName(String ptTypeName) {
        this.ptTypeName = ptTypeName;
    }
}
