package com.safra.models;

import java.util.List;

public class TaskItem {

    private long taskId;
    private long taskOnlineId;
    private String taskName;
    private String taskDetail;
    private int priority;
    private String priorityName;
    private long startDate;
    private long endDate;
    private long addedBy;
    private String addedByName;
    private long masterId;
    private String taskStatus;
    private Long[] groupIds;
    private Long[] userIds;
    private Long[] allUserIds;
    private String taskUserStatus;

    private boolean isSynced;
    private boolean isStatusSynced;
    private boolean isDelete;

    private boolean isEditable;
    private boolean isDeletable;
    private boolean isChangeable;
    private boolean isAssignable;

    private boolean isExpanded;

    public TaskItem() {
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        this.taskDetail = taskDetail;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(long addedBy) {
        this.addedBy = addedBy;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long[] getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Long[] groupIds) {
        this.groupIds = groupIds;
    }

    public Long[] getUserIds() {
        return userIds;
    }

    public void setUserIds(Long[] userIds) {
        this.userIds = userIds;
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

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }

    public boolean isChangeable() {
        return isChangeable;
    }

    public void setChangeable(boolean changeable) {
        isChangeable = changeable;
    }

    public boolean isAssignable() {
        return isAssignable;
    }

    public void setAssignable(boolean assignable) {
        isAssignable = assignable;
    }

    public long getTaskOnlineId() {
        return taskOnlineId;
    }

    public void setTaskOnlineId(long taskOnlineId) {
        this.taskOnlineId = taskOnlineId;
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

    public String getTaskUserStatus() {
        return taskUserStatus;
    }

    public void setTaskUserStatus(String taskUserStatus) {
        this.taskUserStatus = taskUserStatus;
    }

    public Long[] getAllUserIds() {
        return allUserIds;
    }

    public void setAllUserIds(Long[] allUserIds) {
        this.allUserIds = allUserIds;
    }

    public boolean isStatusSynced() {
        return isStatusSynced;
    }

    public void setStatusSynced(boolean statusSynced) {
        isStatusSynced = statusSynced;
    }

    public long getMasterId() {
        return masterId;
    }

    public void setMasterId(long masterId) {
        this.masterId = masterId;
    }
}
