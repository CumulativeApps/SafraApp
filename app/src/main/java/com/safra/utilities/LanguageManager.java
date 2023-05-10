package com.safra.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.safra.Safra;
import com.safra.events.LanguageChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

public class LanguageManager {

    private final SharedPreferences pref;
    private final SharedPreferences enPref;
    private final SharedPreferences ptPref;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences.Editor enEditor;
    private final SharedPreferences.Editor ptEditor;

    private static final String LANG_PREFERENCE_NAME = "lang_pref";
    private static final String EN_PREFERENCE_NAME = "en_pref";
    private static final String PT_PREFERENCE_NAME = "pt_pref";

    private static final String KEY_SELECTED_LANG = "selected_lang";
//    1 -> English, 2 -> Portuguese

    public static LanguageManager languageManager;

    public static LanguageManager getInstance(){
        if (languageManager == null) {
            languageManager = new LanguageManager(Safra.getInstance().getApplicationContext());
        }
        return languageManager;
    }

    public LanguageManager(Context context) {
        pref = context.getSharedPreferences(LANG_PREFERENCE_NAME, Context.MODE_PRIVATE);
        enPref = context.getSharedPreferences(EN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        ptPref = context.getSharedPreferences(PT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        enEditor = enPref.edit();
        ptEditor = ptPref.edit();
    }

    public void changeLanguage(long language) {
        editor.putLong(KEY_SELECTED_LANG, language);
        editor.apply();

        EventBus.getDefault().post(new LanguageChangedEvent(language));
    }

    public long getLanguage(){
        return pref.getLong(KEY_SELECTED_LANG, 1);
    }

    public void addString(long langCode, String key, String value) {
        if (langCode == 2) {
            ptEditor.putString(key, value);
            ptEditor.apply();
        } else {
            enEditor.putString(key, value);
            enEditor.apply();
        }
    }

    public void addStrings(long langCode, HashMap<String, String> hashMap) {
        if (langCode == 2) {
            for (String key : hashMap.keySet()) {
                ptEditor.putString(key, hashMap.get(key));
            }
            ptEditor.apply();
        } else {
            for (String key : hashMap.keySet()) {
                enEditor.putString(key, hashMap.get(key));
            }
            enEditor.apply();
        }
    }

    public String getString(String key) {
        if (getLanguage() == 2)
            return ptPref.getString(key, null);
        else
            return enPref.getString(key, null);
    }

    public String getString(long languageId, String key){
        if (languageId == 2)
            return ptPref.getString(key, null);
        else
            return enPref.getString(key, null);
    }

    public void removeLanguage(){
        editor.remove(KEY_SELECTED_LANG);
        editor.commit();
    }
}
