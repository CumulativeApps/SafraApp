package com.safra.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.safra.Safra;
import com.safra.models.UserItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailsManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME ="UserSession";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_COMPANY_EMAIL = "company_email";
    public static final String KEY_COMPANY_PHONE = "company_phone";
    public static final String KEY_COMPANY_IMAGE = "company_image";

    public static final String KEY_PLAN_NAME = "plan_name";
    public static final String KEY_USER_ALLOWED = "user_allowed";
    public static final String KEY_PLAN_EXPIRY = "plan_expiry";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PT_DESCRIPTION = "pt_description";
    public static final String KEY_TERMS = "terms";
    public static final String KEY_PT_TERMS = "pt_terms";
    public static final String KEY_CONTACT_EMAIL = "contact_email";
    public static final String KEY_CONTACT_PHONE = "contact_phone";

    public static DetailsManager detailsManager;

    public static DetailsManager getInstance(){
        if(detailsManager == null)
            detailsManager = new DetailsManager(Safra.getInstance().getApplicationContext());

        return detailsManager;
    }

    public DetailsManager(Context context) {
        pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        //editor1=pref.edit();
    }

    public void updateCompanyDetails(String companyName, String companyEmail, String companyPhone, String companyImage) {
        editor.putString(KEY_COMPANY_NAME, companyName);
        editor.putString(KEY_COMPANY_EMAIL, companyEmail);
        editor.putString(KEY_COMPANY_PHONE, companyPhone);
        editor.putString(KEY_COMPANY_IMAGE, companyImage);
        editor.commit();
    }

    public void updatePlanDetails(String planName, String userAllowed, String planExpiry, String description, String terms,
                                  String contactEmail, String contactPhone, String ptDescription, String ptTerms) {
        editor.putString(KEY_PLAN_NAME, planName);
        editor.putString(KEY_USER_ALLOWED, userAllowed);
        editor.putString(KEY_PLAN_EXPIRY, planExpiry);
        editor.putString(KEY_DESCRIPTION, description);
        editor.putString(KEY_TERMS, terms);
        editor.putString(KEY_CONTACT_EMAIL, contactEmail);
        editor.putString(KEY_CONTACT_PHONE, contactPhone);
        editor.putString(KEY_PT_DESCRIPTION, ptDescription);
        editor.putString(KEY_PT_TERMS, ptTerms);
        editor.commit();
    }

    public String getCompanyName(){
        return pref.getString(KEY_COMPANY_NAME, "");
    }

    public String getCompanyEmail(){
        return pref.getString(KEY_COMPANY_EMAIL, "");
    }

    public String getCompanyPhone(){
        return pref.getString(KEY_COMPANY_PHONE, "");
    }

    public String getCompanyImage(){
        return pref.getString(KEY_COMPANY_IMAGE, "");
    }

    public String getPlanName(){
        return pref.getString(KEY_PLAN_NAME, "");
    }

    public String getUserAllowed(){
        return pref.getString(KEY_USER_ALLOWED, "");
    }

    public String getPlanExpiry(){
        return pref.getString(KEY_PLAN_EXPIRY, "");
    }

    public String getDescription(){
        return pref.getString(KEY_DESCRIPTION, "");
    }

    public String getTerms(){
        return pref.getString(KEY_TERMS, "");
    }

    public String getContactEmail(){
        return pref.getString(KEY_CONTACT_EMAIL, "");
    }

    public String getContactPhone(){
        return pref.getString(KEY_CONTACT_PHONE, "");
    }

    public String getPtDescription(){
        return pref.getString(KEY_PT_DESCRIPTION, "");
    }

    public String getPtTerms(){
        return pref.getString(KEY_PT_TERMS, "");
    }

}
