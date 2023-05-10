package com.safra.models;

public class UserItem {

    private long userId;
    private long userOnlineId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userPassword;
    private String userProfile;
    private long roleId;
    private String roleName;
    private int userStatus;
    private Long[] moduleIds;
    private Long[] permissionIds;
    private long userAddedBy;
    private boolean isAgency;
    private boolean isSynced;
    private boolean isStatusSynced;
    private boolean isDelete;

    private String userToken;
    private String userAccessJson;

    private boolean isEditable;
    private boolean isDeletable;
    private boolean isViewable;
    private boolean isChangeable;

    private boolean isExpanded;

    private boolean isSelected;

    public UserItem() {
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserOnlineId() {
        return userOnlineId;
    }

    public void setUserOnlineId(long userOnlineId) {
        this.userOnlineId = userOnlineId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
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

    public long getUserAddedBy() {
        return userAddedBy;
    }

    public void setUserAddedBy(long userAddedBy) {
        this.userAddedBy = userAddedBy;
    }

    public boolean isAgency() {
        return isAgency;
    }

    public void setAgency(boolean agency) {
        isAgency = agency;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isStatusSynced() {
        return isStatusSynced;
    }

    public void setStatusSynced(boolean statusSynced) {
        isStatusSynced = statusSynced;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserAccessJson() {
        return userAccessJson;
    }

    public void setUserAccessJson(String userAccessJson) {
        this.userAccessJson = userAccessJson;
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

    public boolean isViewable() {
        return isViewable;
    }

    public void setViewable(boolean viewable) {
        isViewable = viewable;
    }

    public boolean isChangeable() {
        return isChangeable;
    }

    public void setChangeable(boolean changeable) {
        isChangeable = changeable;
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
}
