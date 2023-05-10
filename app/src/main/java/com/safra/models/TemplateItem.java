package com.safra.models;

public class TemplateItem {

    private long templateId;
    private String templateName;
    private String templateImage;
    private String templateJson;
    private long templateLanguageId;
    private String templateUniqueId;
    private int templateType;
    private boolean templateStatus;
    private boolean isDelete;
    private String createdAt;
    private String updatedAt;
    private long languageId;
    private String languageTitle;
    private String languageFile;
    private String languageSlug;

    public TemplateItem() {
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateImage() {
        return templateImage;
    }

    public void setTemplateImage(String templateImage) {
        this.templateImage = templateImage;
    }

    public String getTemplateJson() {
        return templateJson;
    }

    public void setTemplateJson(String templateJson) {
        this.templateJson = templateJson;
    }

    public long getTemplateLanguageId() {
        return templateLanguageId;
    }

    public void setTemplateLanguageId(long templateLanguageId) {
        this.templateLanguageId = templateLanguageId;
    }

    public String getTemplateUniqueId() {
        return templateUniqueId;
    }

    public void setTemplateUniqueId(String templateUniqueId) {
        this.templateUniqueId = templateUniqueId;
    }

    public int getTemplateType() {
        return templateType;
    }

    public void setTemplateType(int templateType) {
        this.templateType = templateType;
    }

    public boolean getTemplateStatus() {
        return templateStatus;
    }

    public void setTemplateStatus(boolean templateStatus) {
        this.templateStatus = templateStatus;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
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

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
    }

    public String getLanguageFile() {
        return languageFile;
    }

    public void setLanguageFile(String languageFile) {
        this.languageFile = languageFile;
    }

    public String getLanguageSlug() {
        return languageSlug;
    }

    public void setLanguageSlug(String languageSlug) {
        this.languageSlug = languageSlug;
    }
}
