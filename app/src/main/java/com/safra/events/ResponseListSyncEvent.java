package com.safra.events;

import com.safra.models.ResponseItem;

import java.util.List;

public class ResponseListSyncEvent {

    List<ResponseItem> responseList;

    public ResponseListSyncEvent(List<ResponseItem> responseList) {
        this.responseList = responseList;
    }

    public List<ResponseItem> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<ResponseItem> responseList) {
        this.responseList = responseList;
    }
}
