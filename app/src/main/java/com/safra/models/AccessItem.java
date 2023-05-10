package com.safra.models;

public class AccessItem {

    private long accessId;
    private String accessName;
    private boolean isSelected;

    public AccessItem() {
    }

    public AccessItem(long accessId, String accessName, boolean isSelected) {
        this.accessId = accessId;
        this.accessName = accessName;
        this.isSelected = isSelected;
    }

    public long getAccessId() {
        return accessId;
    }

    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    public String getAccessName() {
        return accessName;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
