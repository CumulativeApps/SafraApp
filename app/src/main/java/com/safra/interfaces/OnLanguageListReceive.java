package com.safra.interfaces;

import com.safra.models.LanguageItem;

import java.util.List;

public interface OnLanguageListReceive {

    void getLanguages(List<LanguageItem> languages);

}
