package com.safra.models;

public class FormVisibilityItem {

    private long visibilityId;
    private String visibilityName;

    public FormVisibilityItem() {
    }

    public FormVisibilityItem(long visibilityId, String typeName) {
        this.visibilityId = visibilityId;
        this.visibilityName = typeName;
    }

    public long getVisibilityId() {
        return visibilityId;
    }

    public void setVisibilityId(long visibilityId) {
        this.visibilityId = visibilityId;
    }

    public String getVisibilityName() {
        return visibilityName;
    }

    public void setVisibilityName(String visibilityName) {
        this.visibilityName = visibilityName;
    }
}
