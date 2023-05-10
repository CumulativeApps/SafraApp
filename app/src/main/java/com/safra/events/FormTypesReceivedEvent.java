package com.safra.events;

import com.safra.models.FormTypeItem;

import java.util.List;

public class FormTypesReceivedEvent {

    List<FormTypeItem> formTypeList;

    public FormTypesReceivedEvent(List<FormTypeItem> formTypeList) {
        this.formTypeList = formTypeList;
    }

    public List<FormTypeItem> getFormTypeList() {
        return formTypeList;
    }

    public void setFormTypeList(List<FormTypeItem> formTypeList) {
        this.formTypeList = formTypeList;
    }
}
