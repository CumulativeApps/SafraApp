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

public class UserSessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME ="UserSession";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_TOKEN = "user_token";
    public static final String KEY_USER_ROLE_ID = "user_role_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_MOBILE_NO = "user_mobile_no";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_ABOUT = "user_about";
    public static final String KEY_USER_PROFILE = "user_profile";
    public static final String KEY_USER_PERMISSION = "user_permission";
    public static final String KEY_IS_AGENCY = "is_agency";

    public static final String KEY_REMEMBER_ME = "remember_me";

    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static UserSessionManager userSessionManager;

    public static UserSessionManager getInstance(){
        if(userSessionManager == null)
            userSessionManager = new UserSessionManager(Safra.getInstance().getApplicationContext());

        return userSessionManager;
    }

    public UserSessionManager(Context context) {
        pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        //editor1=pref.edit();
    }

    public void updateFcmToken(String fcmToken){
        editor.putString(KEY_FCM_TOKEN, fcmToken);
        editor.commit();
    }

    public void rememberUser(boolean rememberMe){
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        editor.commit();
    }

    public void signInUser(UserItem userItem, @Nullable List<String> permissionList) {
        Log.e("createUserLoginSession","createUserLoginSession");
        editor.putLong(KEY_USER_ID, userItem.getUserOnlineId());
        editor.putString(KEY_USER_TOKEN, userItem.getUserToken());
        editor.putLong(KEY_USER_ROLE_ID, userItem.getRoleId());
        editor.putString(KEY_USER_NAME, userItem.getUserName());
        editor.putString(KEY_USER_EMAIL, userItem.getUserEmail());
        editor.putString(KEY_USER_MOBILE_NO, userItem.getUserPhone());
        editor.putString(KEY_USER_PROFILE, userItem.getUserProfile());
        editor.putBoolean(KEY_IS_AGENCY, userItem.isAgency());
        if(permissionList != null) {
            Set<String> set = new HashSet<>(permissionList);
            editor.putStringSet(KEY_USER_PERMISSION, set);
        }
        editor.commit();
    }

    public void updateUserProfile(String userName, String mobileNo, String userProfile,
                                  @Nullable List<String> permissionList) {
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_MOBILE_NO, mobileNo);
        editor.putString(KEY_USER_PROFILE, userProfile);
        if(permissionList != null) {
            Set<String> set = new HashSet<>(permissionList);
            editor.putStringSet(KEY_USER_PERMISSION, set);
        }
        editor.commit();
    }

    public boolean isRemembered(){
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }

    public long getUserId(){
        return pref.getLong(KEY_USER_ID, -1);
    }

    public String getUserToken(){
        return pref.getString(KEY_USER_TOKEN, null);
    }

    public long getUserRoleId() {
        return pref.getLong(KEY_USER_ROLE_ID, 0);
    }

    public String getUserName(){
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail(){
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public String getUserMobileNo(){
        return pref.getString(KEY_USER_MOBILE_NO, null);
    }

    public int getUserType(){
        return pref.getInt(KEY_USER_TYPE, -1);
    }

    public String getUserAbout(){
        return pref.getString(KEY_USER_ABOUT, null);
    }

    public String getUserProfile(){
        return pref.getString(KEY_USER_PROFILE, null);
    }

    public boolean isAgency() {
        return pref.getBoolean(KEY_IS_AGENCY, false);
    }

    public List<String> getUserPermissions(){
        return new ArrayList<>(pref.getStringSet(KEY_USER_PERMISSION, new HashSet<>()));
    }

    public String getFcmToken(){
        return pref.getString(KEY_FCM_TOKEN, null);
    }

    public void signOutUser(){
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_TOKEN);
        editor.remove(KEY_USER_ROLE_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_MOBILE_NO);
        editor.remove(KEY_USER_TYPE);
        editor.remove(KEY_USER_ABOUT);
        editor.remove(KEY_USER_PROFILE);
        editor.remove(KEY_IS_AGENCY);
        editor.remove(KEY_FCM_TOKEN);
        editor.remove(KEY_USER_PERMISSION);
        editor.putBoolean(KEY_REMEMBER_ME, false);
        editor.commit();
    }

}
