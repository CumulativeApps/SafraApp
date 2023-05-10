package com.safra.events;

import com.safra.models.FormItem;

import java.util.List;

public class FormListSyncEvent {

    List<FormItem> formList;

    public FormListSyncEvent(List<FormItem> formList) {
        this.formList = formList;
    }

    public List<FormItem> getFormList() {
        return formList;
    }

    public void setFormList(List<FormItem> formList) {
        this.formList = formList;
    }
}
