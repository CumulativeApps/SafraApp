package com.safra.models;

import java.util.ArrayList;
import java.util.List;

public class ResponseItem {

    private long responseId;
    private long onlineId;
    private long formId;
    private long userId;
    private String userName;
    private String submitDate;
    private String responseData;
    private ArrayList<FileItem> responseFiles;
    private boolean isDelete;

    public ResponseItem() {
    }

    public long getResponseId() {
        return responseId;
    }

    public void setResponseId(long responseId) {
        this.responseId = responseId;
    }

    public long getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(long onlineId) {
        this.onlineId = onlineId;
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public ArrayList<FileItem> getResponseFiles() {
        return responseFiles;
    }

    public void setResponseFiles(ArrayList<FileItem> responseFiles) {
        this.responseFiles = responseFiles;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
