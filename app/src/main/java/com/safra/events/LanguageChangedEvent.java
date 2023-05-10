package com.safra.events;

public class LanguageChangedEvent {

    long langCode;

    public LanguageChangedEvent(long langCode) {
        this.langCode = langCode;
    }

    public long getLangCode() {
        return langCode;
    }

    public void setLangCode(long langCode) {
        this.langCode = langCode;
    }
}
