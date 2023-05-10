package com.safra.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.safra.Safra;
import com.safra.events.ConnectivityChangedEvent;

import org.greenrobot.eventbus.EventBus;

import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static final String TAG = "connectivity_receiver";

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e(TAG, "onReceive: received");
//
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//
//        Log.e(TAG, "onReceive: " + isConnected);
//        if (isConnected) {
//            boolean isRemembered = userSessionManager.isRemembered();
//
//            if (userSessionManager.getUserId() > 0 || Safra.userId > 0) {
//                if (!Safra.isFormSyncing)
//                    SyncData.uploadUnsyncedForms(TAG);
//                if (!Safra.isTaskSyncing)
//                    SyncData.uploadUnsyncedTasks(TAG);
//                if (!Safra.isUserSyncing)
//                    SyncData.uploadUnsyncedUsers(TAG);
//                if (!Safra.isGroupSyncing)
//                    SyncData.uploadUnsyncedGroups(TAG);
//                if(!Safra.isResponseSyncing)
//                    SyncData.uploadUnsyncedResponses(TAG);
//                if(!Safra.isTemplateSyncing)
//                    SyncData.syncTemplates(TAG, LanguageManager.getInstance().getLanguage(), PAGE_START);
//               /* if (!Safra.isResponseSyncing) {
//                    SyncData.uploadUnsyncedResponses(TAG);
//
//                }*/
//            }
//        }
//
//        EventBus.getDefault().post(new ConnectivityChangedEvent(isConnected));
////        if(connectivityReceiverListener != null)
////            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) Safra.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

}
