package com.safra.models;

public class PriorityItem1 {

    private String priorityStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String priorityName;

    public PriorityItem1(int id ,String priorityStatus, String priorityName) {
        this.id = id;
        this.priorityStatus = priorityStatus;
        this.priorityName = priorityName;
    }

    public String getPriorityStatus() {
        return priorityStatus;
    }

    public void setPriorityStatus(String priorityStatus) {
        this.priorityStatus = priorityStatus;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }
}
