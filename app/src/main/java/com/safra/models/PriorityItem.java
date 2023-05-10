package com.safra.models;

public class PriorityItem {

    private int priorityStatus;
    private String priorityName;

    public PriorityItem(int priorityStatus, String priorityName) {
        this.priorityStatus = priorityStatus;
        this.priorityName = priorityName;
    }

    public int getPriorityStatus() {
        return priorityStatus;
    }

    public void setPriorityStatus(int priorityStatus) {
        this.priorityStatus = priorityStatus;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }
}
