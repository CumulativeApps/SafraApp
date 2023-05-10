package com.safra.models;

public class RoleItem {

    private long roleId;
    private long roleOnlineId;
    private String roleName;
    private Long[] moduleIds;
    private Long[] permissionIds;
    private long addedBy;

    private boolean isSynced;
    private boolean isDelete;

    private boolean isEditable;
    private boolean isDeletable;

    private boolean isExpanded;

    private boolean isSelected;

    public RoleItem() {
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public long getRoleOnlineId() {
        return roleOnlineId;
    }

    public void setRoleOnlineId(long roleOnlineId) {
        this.roleOnlineId = roleOnlineId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long[] getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(Long[] moduleIds) {
        this.moduleIds = moduleIds;
    }

    public Long[] getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(Long[] permissionIds) {
        this.permissionIds = permissionIds;
    }

    public long getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(long addedBy) {
        this.addedBy = addedBy;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
