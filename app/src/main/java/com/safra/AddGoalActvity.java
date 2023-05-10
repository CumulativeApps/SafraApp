package com.safra;

import static com.safra.utilities.Common.ADD_PLANNER_GOAL;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddGoalActvity extends AppCompatActivity {
    public static final String TAG = "add_Goal";
    private List<String> data = new ArrayList<>();
    TextInputEditText editText;
    Button closeButton;
    Button saveButton;
    TextView aimTitle;

    String passAim;
    private boolean isRemembered;
    private long aimId = -1;
    private String aimName = "Hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal_actvity);
        editText   = findViewById(R.id.edit_Aim_text);
        closeButton  = findViewById(R.id.close_Aim_button);
        saveButton  = findViewById(R.id.save_Aim_button);
        aimTitle =  findViewById(R.id.aimTitle);
//        editText.setText(userList.get(0).getAim());

        isRemembered = userSessionManager.isRemembered();
        aimId = getIntent().getLongExtra("aim_id", -1);
        aimName = getIntent().getStringExtra("aim_name");
        System.out.println("AIMID"+aimName);
        passAim = editText.getText().toString();
        aimTitle.setText(aimName);

//        String Name= aimName;



        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoals();
//                        String text = editText.getText().toString();
                // Do something with the text

            }
        });


    }
    private void saveGoals() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
        String ID = String.valueOf(aimId);
        passAim = editText.getText().toString();
        JSONArray jsonArray = new JSONArray(data);
//        System.out.println("saveActionPlan ID"+ ID);

//        tvAim = edAim.getText().toString();
//        System.out.println("tvAim"+tvAim);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
            requestBody.put("planner_aim_id", ID);
            requestBody.put("goal", passAim);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(BASE_URL + ADD_PLANNER_GOAL)
                .addJSONObjectBody(requestBody)

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "response: " + response);
                        LoadingDialogExtension.hideLoading();
                        try {
                            String message = response.getString("message");
                            Toast.makeText(AddGoalActvity.this, message, Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new TaskAddedEvent());
                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
                    }
                });
    }

}