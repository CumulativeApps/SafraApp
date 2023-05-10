package com.safra;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.CREATE_ACTION_PLANNER;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.safra.adapters.AimGoalListRecyclerAdapter;
import com.safra.events.TaskAddedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddActionPlan extends AppCompatActivity {
    public static final String TAG = "add_action_plan";
    private ListView listView;
    private TextInputEditText editText,edAim;
    private FloatingActionButton addButton;
    private ArrayList<String> goalsList;
//    private ArrayAdapter<String> adapter;
    private boolean isRemembered;
    private String tvAim;
    private List<String> data = new ArrayList<>();
    private AimGoalListRecyclerAdapter adapter;
    private long projectId = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action_plan);

        ListView listView = findViewById(R.id.list_view);
        adapter = new AimGoalListRecyclerAdapter(this, data);
        listView.setAdapter(adapter);
        edAim = findViewById(R.id.etAimName);

        isRemembered = userSessionManager.isRemembered();
        projectId = getIntent().getLongExtra("planner_project_id", -1);

        TextInputEditText editText = findViewById(R.id.edit_text);
        FloatingActionButton addButton = findViewById(R.id.fabAdd);
        addButton.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {
                data.add(text);
                adapter.notifyDataSetChanged();
                editText.setText("");
            }
        });


        Button sendButton = findViewById(R.id.btnSave);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActionPlan();
            }
        });
    }
    private void saveActionPlan() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_task_progress", getString(R.string.saving_task_progress)));
        String ID = String.valueOf(projectId);

        JSONArray jsonArray = new JSONArray(data);
        System.out.println("saveActionPlan ID"+ ID);

        tvAim = edAim.getText().toString();
        System.out.println("tvAim"+tvAim);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
            requestBody.put("planner_project_id", projectId);
            requestBody.put("aim", tvAim);
            requestBody.put("goals", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(BASE_URL + CREATE_ACTION_PLANNER)
                .addJSONObjectBody(requestBody)

                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "response: " + response);
                        LoadingDialogExtension.hideLoading();
                        try {
                            String message = response.getString("message");
                            Toast.makeText(AddActionPlan.this, message, Toast.LENGTH_SHORT).show();
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