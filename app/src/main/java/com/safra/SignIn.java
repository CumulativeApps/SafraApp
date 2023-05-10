package com.safra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.messaging.FirebaseMessaging;
import com.safra.databinding.ActivitySignInBinding;
import com.safra.events.ConnectivityChangedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.fragments.SignInFragment;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import static com.safra.utilities.LanguageManager.languageManager;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class SignIn extends AppCompatActivity {

    public static final String TAG = "sign_in_activity";

    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignInBinding binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        fcmToken = userSessionManager.getFcmToken();

        if (userSessionManager.isRemembered() && userSessionManager.getUserId() > -1) {
            Intent dashboardIntent = new Intent(this, Dashboard.class);
            startActivity(dashboardIntent);
            finish();
        }

        binding.tvSignInHeading.setText(LanguageExtension.setText("sign_in", getString(R.string.sign_in)));
        binding.ivAppLogo.setImageResource(languageManager.getLanguage() == 2 ? R.drawable.pt_app_logo : R.drawable.app_logo);

        Fragment fragment = new SignInFragment();
        String tag = SignInFragment.TAG;
        loadFragment(fragment, tag/*, true*/);

        if (fcmToken == null && ConnectivityReceiver.isConnected()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed -> " + task.getException().getLocalizedMessage());
                    return;
                }
                Log.e(TAG, "onCreate: task completed");

                // Get new Instance ID token
                userSessionManager.updateFcmToken(Objects.requireNonNull(task.getResult()));
                Log.e(TAG, "onComplete: " + userSessionManager.getFcmToken());
                fcmToken = task.getResult();

            });
        } else {
            Log.e(TAG, "onCreate: checking for token -> " + userSessionManager.getFcmToken());
        }
    }

    //Fragment Loading
    private void loadFragment(Fragment fragment, String TAG/*, boolean isActivityCreated*/) {
        //switching fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            if(isActivityCreated){
            ft.replace(R.id.flContainer, fragment, TAG).commit();
//            }else{
//                ft.add(R.id.flContainer, fragment, TAG).addToBackStack(TAG).commit();
//            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectivityChanged(ConnectivityChangedEvent event) {
        if (event.isConnected()) {
            if (fcmToken == null) {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("getInstanceId failed", task.getException());
                        return;
                    }
                    Log.e(TAG, "onCreate: task completed");

                    // Get new Instance ID token
                    userSessionManager.updateFcmToken(Objects.requireNonNull(task.getResult()));
                    Log.e("MainActivity", "onComplete: " + userSessionManager.getFcmToken());
                    fcmToken = task.getResult();

                });
            } else {
                Log.e("MainActivity", "onCreate: checking for token -> " + userSessionManager.getFcmToken());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}