package com.safra.extensions;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.Safra;
import com.safra.utilities.LanguageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.CHANGE_LANGUAGE_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class LanguageExtension {

    public static void changeLanguage(String TAG, long langCode){
        AndroidNetworking
                .post(BASE_URL + CHANGE_LANGUAGE_API)
                .addBodyParameter("user_token", userSessionManager.isRemembered() ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("language_id", String.valueOf(langCode))
                .setTag("change-language-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Log.d(TAG, "onResponse: " + success + " -> " + message);
                        } catch (JSONException e){
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    public static void downloadLanguageFile(Context context, String TAG, long langCode, String url, String fileName){
        try {
            new File(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName).delete();
        } catch (Exception e){
            Log.e(TAG, "downloadLanguageFile: " + e.getLocalizedMessage());
        }
        AndroidNetworking
                .download(url, context.getExternalFilesDir(null).getAbsolutePath(), fileName)
                .doNotCacheResponse()
                .setTag("download-language-file")
                .build()
                .setDownloadProgressListener((bytesDownloaded, totalBytes) ->
                        Log.e(TAG, "downloadAndSaveLanguageFile: bytesDownloaded -> " + bytesDownloaded))
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.e(TAG, "onDownloadComplete: -> Hurray");

                        readLanguageFile(context, TAG, langCode, fileName);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());
                    }
                });
    }

    public static void readLanguageFile(Context context, String TAG, long langCode, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null){
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "readFileAndSaveLanguage: " + e.getLocalizedMessage());
        } finally {
            String content = stringBuilder.toString();
            saveLanguageWords(context, TAG, langCode, content);
        }
    }

    public static void saveLanguageWords(Context context, String TAG, long langCode, String content){
        try {
            JSONObject jsonObject = new JSONObject(content);

            if(jsonObject.length() > 0) {
                Log.e(TAG, "saveLanguageWords: " + jsonObject.length());
                LanguageManager lm = new LanguageManager(context);

                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
//                    Log.e(TAG, "saveLanguageWords: " + key);
//                    Log.e(TAG, "saveLanguageWords: " + jsonObject.getString(key));

                    lm.addString(langCode, key, jsonObject.getString(key));
                }
            }

        } catch (JSONException e){
            Log.e(TAG, "saveLanguageWords: " + e.getLocalizedMessage());
        }
    }

    public static String setText(String jsonString, String xmlString){
        String s = LanguageManager.languageManager.getString(jsonString);
        if(s != null) return s;
        else return xmlString;
    }

    public static String setText(long languageId, String jsonString, String xmlString){
        String s = LanguageManager.languageManager.getString(languageId, jsonString);
        if(s != null) return s;
        else return xmlString;
    }

}
