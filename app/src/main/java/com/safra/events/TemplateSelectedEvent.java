package com.safra.events;

public class TemplateSelectedEvent {

    long languageId;
    String formFields;

    public TemplateSelectedEvent(long languageId, String formFields) {
        this.languageId = languageId;
        this.formFields = formFields;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    public String getFormFields() {
        return formFields;
    }

    public void setFormFields(String formFields) {
        this.formFields = formFields;
    }
}
