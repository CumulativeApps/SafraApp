package com.safra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.safra.databinding.ActivityWebBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.utilities.AppWebViewClient;
import com.safra.utilities.PathFinder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WebActivity extends AppCompatActivity {

    public static final String TAG = "web_activity";

    private ActivityWebBinding binding;

    private DownloadManager downloadManager;

    private String url;

    private List<Long> downloadList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i("WebActivity", "BUX Activity");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        if (BuildConfig.DEBUG)
            WebView.setWebContentsDebuggingEnabled(true);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        binding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        binding.webView.setScrollbarFadingEnabled(false);

        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebViewClient(new AppWebViewClient(binding.progressWebView));

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (getIntent() != null) {
            binding.tvWebActivityHeading.setText(getIntent().getStringExtra("heading"));
            url = getIntent().getStringExtra("url");

            binding.webView.loadUrl(url);
        }

        binding.webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                Dexter.withContext(WebActivity.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    startDownload(url, userAgent, contentDisposition, mimeType);
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
                                                                           PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long downloadCompletedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (isValidDownload(downloadCompletedId) && downloadList.contains(downloadCompletedId)) {
                downloadList.remove(downloadCompletedId);

                Uri fileUri = downloadManager.getUriForDownloadedFile(downloadCompletedId);
                Log.e(TAG, "onReceive -> " + fileUri);
                String filePath = new PathFinder(WebActivity.this).getPath(fileUri);
                Log.e(TAG, "onReceive -> " + filePath);
                Uri contentUri = FileProvider.getUriForFile(WebActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
                Log.e(TAG, "onReceive -> " + contentUri);

                Toast.makeText(WebActivity.this, LanguageExtension.setText("file_downloaded_at", getString(R.string.file_downloaded_at)) + "\n" + filePath, Toast.LENGTH_LONG)
//                Snackbar.make(webView, LanguageExtension.setText("file_downloaded_at", getString(R.string.file_downloaded_at)) + "\n" + filePath, Snackbar.LENGTH_LONG)
//                        .setAction("View", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent myIntent = new Intent(Intent.ACTION_VIEW);
//                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                                    myIntent.setData(fileUri);
//                                    myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    Intent j = Intent.createChooser(myIntent,
//                                            LanguageExtension.setText("choose_an_application_to_open_with", getString(R.string.choose_an_application_to_open_with)));
//                                    startActivity(j);
//                                } else {
//                                    myIntent.setData(Uri.fromFile(new File(filePath)));
//                                    Intent j = Intent.createChooser(myIntent,
//                                            LanguageExtension.setText("choose_an_application_to_open_with", getString(R.string.choose_an_application_to_open_with)));
//                                    startActivity(j);
//                                }
//                            }
//                        })
                        .show();
            }
        }
    };

    private void startDownload(String url, String userAgent, String contentDisposition, String mimeType) {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String cookie = CookieManager.getInstance().getCookie(url);

        // add cookie to request
        request.addRequestHeader("Cookie", cookie);
        // add user agent to request
        request.addRequestHeader("User-Agent", userAgent);

        // file scanned by MediaScanner
        request.allowScanningByMediaScanner();

        // download is visible and its progress, after completion too
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // download manager created
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // saving file in "download" folder
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // enqueue download request
        Long downloadId = downloadManager.enqueue(request);
        downloadList.add(downloadId);
    }

    @SuppressLint("Range")
    private boolean isValidDownload(long downloadId) {
        Cursor c = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));

        if (c.moveToFirst()) {
             int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                return true;
            } else {
                int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                Log.e(TAG, "Download not correct, status [" + status + "] reason [" + reason + "]");
                return false;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}