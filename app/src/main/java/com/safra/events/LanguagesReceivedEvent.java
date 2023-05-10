package com.safra.events;

import com.safra.models.LanguageItem;

import java.util.List;

public class LanguagesReceivedEvent {

    List<LanguageItem> languages;

    public LanguagesReceivedEvent(List<LanguageItem> languages) {
        this.languages = languages;
    }

    public List<LanguageItem> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageItem> languages) {
        this.languages = languages;
    }
}
