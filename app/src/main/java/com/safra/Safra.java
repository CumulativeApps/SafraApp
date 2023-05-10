package com.safra;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.androidnetworking.AndroidNetworking;
import com.safra.db.DBHandler;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.DetailsManager;
import com.safra.utilities.LanguageManager;
import com.safra.utilities.UserSessionManager;

import java.util.List;

public class Safra extends Application {

    public static long userId = -1;
    public static String userToken = null;
    public static long userRoleId = 0;
    public static String userName = null;
    public static String userPhoneNo = null;
    public static String userEmail = null;
    public static String userProfile = null;
    public static boolean isAgency = false;

    public static List<String> permissionList = null;

    public static boolean isFormSyncing = false;
    public static boolean isTaskSyncing = false;
    public static boolean isUserSyncing = false;
    public static boolean isGroupSyncing = false;
    public static boolean isResponseSyncing = false;
    public static boolean isTemplateSyncing = false;

    public static Safra mInstance;

    ConnectivityReceiver connectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        AndroidNetworking.initialize(getApplicationContext());
        connectivityReceiver = new ConnectivityReceiver();
        LanguageManager.getInstance();
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        DBHandler.getInstance();
        DetailsManager.getInstance();

        if(sessionManager.isRemembered()){
            setPermissionList(sessionManager.getUserPermissions());
        }

//        LanguageExtension.readLanguageFile(getApplicationContext(), "Safra", 1, "english.json");

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    public static synchronized Safra getInstance(){
        return mInstance;
    }

    public static void signInUser(UserItem userItem){
        Safra.userId = userItem.getUserOnlineId();
        Safra.userToken = userItem.getUserToken();
        Safra.userRoleId = userItem.getRoleId();
        Safra.userName = userItem.getUserName();
        Safra.userPhoneNo = userItem.getUserPhone();
        Safra.userEmail = userItem.getUserEmail();
        Safra.userProfile = userItem.getUserProfile();
        Safra.isAgency = userItem.isAgency();
    }

    public static void updateUserProfile(String userName, String userPhoneNo, String userProfile){
        Safra.userName = userName;
        Safra.userPhoneNo = userPhoneNo;
        Safra.userProfile = userProfile;
    }

    public static void setPermissionList(List<String> permissionList){
        Safra.permissionList = permissionList;
    }

    public static void signOutUser(){
        Safra.userId = -1;
        Safra.userToken = null;
        Safra.userRoleId = 0;
        Safra.userName = null;
        Safra.userPhoneNo = null;
        Safra.userEmail = null;
        Safra.userProfile = null;
        Safra.permissionList = null;
        Safra.isAgency = false;
    }
}
