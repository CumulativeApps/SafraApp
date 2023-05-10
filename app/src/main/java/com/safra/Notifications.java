package com.safra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.adapters.NotificationRecyclerAdapter;
import com.safra.databinding.ActivityNotificationsBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.NotificationItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.NOTIFICATION_LIST_API;
import static com.safra.utilities.Common.NOTIFICATION_READ_API;
import static com.safra.utilities.Common.SERVER_DATE_TIME_FORMAT;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class Notifications extends AppCompatActivity {

    public static final String TAG = "notifications_activity";

    private ActivityNotificationsBinding binding;

    private final List<NotificationItem> notificationList = new ArrayList<>();
    private NotificationRecyclerAdapter adapter;

    private final SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.getDefault());
    private final SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    private boolean isRemembered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        setText();

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvNotifications.addItemDecoration(new SpaceItemDecoration(this, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new NotificationRecyclerAdapter(this, notificationList, (item, position) -> {
            if (ConnectivityReceiver.isConnected())
                readNotification(item.getNotificationId());

            Intent intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("fragment_to_launch", item.getModuleId());
            intent.putExtra("reference_id", item.getReferenceId());
            startActivity(intent);
        });
        binding.rvNotifications.setAdapter(adapter);

        checkForEmptyState();
        getNotifications(true);
    }

    private void setText() {
        binding.tvNotificationHeading.setText(LanguageExtension.setText("notifications", getString(R.string.notifications)));
        binding.tvEmptyState.setText(LanguageExtension.setText("currently_there_are_no_notifications", getString(R.string.currently_there_are_no_notifications)));
    }

    private void getNotifications(boolean showMessage) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + NOTIFICATION_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("notification-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONArray notifications = response.getJSONObject("data").getJSONArray("notifications");
                                if (notifications.length() > 0) {
                                    notificationList.clear();

                                    for (int i = 0; i < notifications.length(); i++) {
                                        JSONObject notification = notifications.getJSONObject(i);
                                        NotificationItem notificationItem = new NotificationItem();
                                        notificationItem.setNotificationId(notification.getLong("noti_id"));
                                        notificationItem.setNotificationTitle(notification.getString("noti_title"));

                                        notificationItem.setNotificationDate(sdf.parse(notification.getString("created_at")).getTime());
                                        notificationItem.setReferenceId(notification.getLong("noti_reference_id"));
                                        notificationItem.setModuleId(notification.getLong("noti_module"));
                                        notificationItem.setStatus(notification.getInt("noti_status"));

                                        notificationList.add(notificationItem);
                                    }

                                    adapter.notifyDataSetChanged();
                                }

                                checkForEmptyState();
                            } else {
                                if (showMessage)
                                    Toast.makeText(Notifications.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | ParseException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
//                        dialogL.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
//        notificationList.clear();
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    private void readNotification(long notificationId) {
        AndroidNetworking
                .post(BASE_URL + NOTIFICATION_READ_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("noti_id", String.valueOf(notificationId))
                .setTag("read-notification-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                getNotifications(false);
                            } else {
                                Toast.makeText(Notifications.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                    }
                });
    }

    private void checkForEmptyState() {
        if (notificationList.size() > 0) {
            binding.clData.setVisibility(View.VISIBLE);
            binding.clEmptyState.setVisibility(View.GONE);
        } else {
            binding.clData.setVisibility(View.GONE);
            binding.clEmptyState.setVisibility(View.VISIBLE);
        }
    }
}