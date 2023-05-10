package com.safra.models;

public class LanguageItem {

    private long languageId;
    private String languageName;
    private String languageSlug;
    private String langFileUrl;
    private boolean isSelected;

    public LanguageItem() {
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageSlug() {
        return languageSlug;
    }

    public void setLanguageSlug(String languageSlug) {
        this.languageSlug = languageSlug;
    }

    public String getLangFileUrl() {
        return langFileUrl;
    }

    public void setLangFileUrl(String langFileUrl) {
        this.langFileUrl = langFileUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
