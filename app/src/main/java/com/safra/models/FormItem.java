package com.safra.models;

public class FormItem {

    private long formId;
    private String formUniqueId;
    private long formOnlineId;
    private long formUserId;
    private long formLanguageId;
    private String formLanguageName;
    private String formName;
    private String formDescription;
    private String formExpiryDate;
    private int formStatus;
    private long formType;
    private long formAccess;
    private String formJson;
    private String formLink;
    private int totalMarks;

    private Long[] userIds;
    private Long[] groupIds;
    private Long[] groupUserIds;

    private boolean isSynced;
    private boolean isDelete;
    private String createdAt;
    private String updatedAt;

    private boolean isExpanded;
    private boolean isEditable;
    private boolean isResponseViewable;
    private boolean isFillable;
    private boolean isStatusChangeable;
    private boolean isAssignable;

    public FormItem() {
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public String getFormUniqueId() {
        return formUniqueId;
    }

    public void setFormUniqueId(String formUniqueId) {
        this.formUniqueId = formUniqueId;
    }

    public long getFormOnlineId() {
        return formOnlineId;
    }

    public void setFormOnlineId(long formOnlineId) {
        this.formOnlineId = formOnlineId;
    }

    public long getFormUserId() {
        return formUserId;
    }

    public void setFormUserId(long formUserId) {
        this.formUserId = formUserId;
    }

    public long getFormLanguageId() {
        return formLanguageId;
    }

    public void setFormLanguageId(long formLanguageId) {
        this.formLanguageId = formLanguageId;
    }

    public String getFormLanguageName() {
        return formLanguageName;
    }

    public void setFormLanguageName(String formLanguageName) {
        this.formLanguageName = formLanguageName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormDescription() {
        return formDescription;
    }

    public void setFormDescription(String formDescription) {
        this.formDescription = formDescription;
    }

    public String getFormExpiryDate() {
        return formExpiryDate;
    }

    public void setFormExpiryDate(String formExpiryDate) {
        this.formExpiryDate = formExpiryDate;
    }

    public int getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(int formStatus) {
        this.formStatus = formStatus;
    }

    public long getFormType() {
        return formType;
    }

    public void setFormType(long formType) {
        this.formType = formType;
    }

    public long getFormAccess() {
        return formAccess;
    }

    public void setFormAccess(long formAccess) {
        this.formAccess = formAccess;
    }

    public String getFormJson() {
        return formJson;
    }

    public void setFormJson(String formJson) {
        this.formJson = formJson;
    }

    public String getFormLink() {
        return formLink;
    }

    public void setFormLink(String formLink) {
        this.formLink = formLink;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Long[] getUserIds() {
        return userIds;
    }

    public void setUserIds(Long[] userIds) {
        this.userIds = userIds;
    }

    public Long[] getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Long[] groupIds) {
        this.groupIds = groupIds;
    }

    public Long[] getGroupUserIds() {
        return groupUserIds;
    }

    public void setGroupUserIds(Long[] groupUserIds) {
        this.groupUserIds = groupUserIds;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isResponseViewable() {
        return isResponseViewable;
    }

    public void setResponseViewable(boolean responseViewable) {
        isResponseViewable = responseViewable;
    }

    public boolean isFillable() {
        return isFillable;
    }

    public void setFillable(boolean fillable) {
        isFillable = fillable;
    }

    public boolean isStatusChangeable() {
        return isStatusChangeable;
    }

    public void setStatusChangeable(boolean statusChangeable) {
        isStatusChangeable = statusChangeable;
    }

    public boolean isAssignable() {
        return isAssignable;
    }

    public void setAssignable(boolean assignable) {
        isAssignable = assignable;
    }
}
