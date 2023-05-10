package com.safra.models;

public class PermissionItem {

    private Long permissionId;
    private String permissionName;
    private String ptPermissionName;

    private boolean isSelected;

    public PermissionItem() {
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPtPermissionName() {
        return ptPermissionName;
    }

    public void setPtPermissionName(String ptPermissionName) {
        this.ptPermissionName = ptPermissionName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
