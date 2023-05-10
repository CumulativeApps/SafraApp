package com.safra.events;

import com.safra.models.TemplateItem;

import java.util.List;

public class TemplateListSyncEvent {

    List<TemplateItem> templateList;

    public TemplateListSyncEvent(List<TemplateItem> templateList) {
        this.templateList = templateList;
    }

    public List<TemplateItem> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<TemplateItem> templateList) {
        this.templateList = templateList;
    }
}
