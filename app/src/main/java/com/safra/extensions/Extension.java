package com.safra.extensions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.safra.Safra;
import com.safra.SignIn;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.LanguageManager.languageManager;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import androidx.appcompat.app.AlertDialog;

public class Extension {

    /**
     * Sign out user from any activity or fragment
     * @param context context of activity/fragment
     */
    public static void signOutUser(Context context) {
        userSessionManager.signOutUser();
        Safra.signOutUser();
        dbHandler.signOutUser(userSessionManager.isRemembered() ? userSessionManager.getUserId() : Safra.userId);
        languageManager.removeLanguage();
        Intent signOutIntent = new Intent(context, SignIn.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(signOutIntent);
    }

    public static void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings(context);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

}
