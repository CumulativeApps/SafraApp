package com.safra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectListResponseModel {


        @SerializedName("id")
        @Expose
        public Long id;
        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("master_id")
        @Expose
        public Integer masterId;
        @SerializedName("created_at")
        @Expose
        public String createdAt;
        @SerializedName("updated_at")
        @Expose
        public String updatedAt;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("financier")
        @Expose
        public String financier;
        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("currency")
        @Expose
        public int currency;

    private String priorityName;

    private boolean isEditable;
    private boolean isDeletable;
    private boolean isViewable;
    private boolean isChangeable;

    private boolean isExpanded;

    private boolean isSelected;

    public ProjectListResponseModel(Long id, Integer userId, String name, Integer masterId, String createdAt, String updatedAt, String startDate, String endDate, String financier, Integer status, int currency) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.masterId = masterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.financier = financier;
        this.status = status;
        this.currency = currency;
    }

    public ProjectListResponseModel(Long id, Integer userId, String name, Integer masterId, String createdAt, String updatedAt, String startDate, String endDate, String financier, Integer status, int currency, boolean isEditable, boolean isDeletable, boolean isViewable, boolean isChangeable, boolean isExpanded, boolean isSelected) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.masterId = masterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.financier = financier;
        this.status = status;
        this.currency = currency;
        this.isEditable = isEditable;
        this.isDeletable = isDeletable;
        this.isViewable = isViewable;
        this.isChangeable = isChangeable;
        this.isExpanded = isExpanded;
        this.isSelected = isSelected;
    }

    public ProjectListResponseModel() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String financier) {
        this.financier = financier;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
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

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    @Override
    public String toString() {
        return "ProjectListResponseModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", masterId=" + masterId +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", financier='" + financier + '\'' +
                ", status=" + status +
                ", currency='" + currency + '\'' +
                ", isEditable=" + isEditable +
                ", isDeletable=" + isDeletable +
                ", isViewable=" + isViewable +
                ", isChangeable=" + isChangeable +
                ", isExpanded=" + isExpanded +
                ", isSelected=" + isSelected +
                '}';
    }
}