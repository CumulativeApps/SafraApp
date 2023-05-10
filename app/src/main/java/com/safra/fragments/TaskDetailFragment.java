package com.safra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.TaskAssignedMembersRecyclerAdapter;
import com.safra.databinding.FragmentTaskDetailBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.TaskAssignedMemberItem;
import com.safra.models.TaskItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DATE_FORMAT;
import static com.safra.utilities.Common.TASK_VIEW_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class TaskDetailFragment extends DialogFragment {

    public static final String TAG = "task_detail_dialog";
    private static final int LOADING_CONDITION = 0;
    private static final int SUCCESS_CONDITION = 1;
    private static final int ERROR_CONDITION = 2;

    private final SimpleDateFormat sdfToShow = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    private FragmentActivity mActivity = null;

    private FragmentTaskDetailBinding binding;

    private final List<TaskAssignedMemberItem> memberList = new ArrayList<>();
    private TaskAssignedMembersRecyclerAdapter adapter;

    private boolean isRemembered;

    private long taskId, onlineId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);

        binding.ivClose.setOnClickListener(v -> dismiss());

        isRemembered = userSessionManager.isRemembered();

        setText();

        if (getArguments() != null) {
            taskId = getArguments().getLong("task_id");
            onlineId = getArguments().getLong("online_id");
        }

        binding.rvAssignedTo.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvAssignedTo.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen._2dp, R.dimen._0dp, false));
        adapter = new TaskAssignedMembersRecyclerAdapter(mActivity, memberList, (view, item, position) -> {

        });
        binding.rvAssignedTo.setAdapter(adapter);

        if (ConnectivityReceiver.isConnected())
            getTaskDetails();
        else {
            getTaskDetailsFromDB();
        }

        return binding.getRoot();
    }

    private void setText() {
        binding.tvTaskInfoTitle.setText(LanguageExtension.setText("task_information", getString(R.string.task_information)));
        binding.tvLoading.setText(LanguageExtension.setText("loading_content", getString(R.string.loading_content)));
        binding.tvNoData.setText(LanguageExtension.setText("no_data_found", getString(R.string.no_data_found)));
        binding.tvPriorityTitle.setText(LanguageExtension.setText("priority", getString(R.string.priority)));
        binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date", getString(R.string.start_date)));
        binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date", getString(R.string.end_date)));
        binding.tvTaskStatusTitle.setText(LanguageExtension.setText("status", getString(R.string.status)));
        binding.tvTaskDetailTitle.setText(LanguageExtension.setText("details", getString(R.string.details)));
        binding.tvAssignedToTitle.setText(LanguageExtension.setText("assign_to", getString(R.string.assign_to)));
    }

    private void getTaskDetailsFromDB() {
        showLayout(LOADING_CONDITION);
        TaskItem taskItem = dbHandler.getTaskDetails(taskId);
        setTaskDetail(taskItem);

        try {
            JSONArray members = new JSONArray(taskItem.getTaskDetail());
            if (members.length() > 0) {
                memberList.clear();
                for (int i = 0; i < members.length(); i++) {
                    JSONObject member = members.getJSONObject(i);
                    TaskAssignedMemberItem memberItem = new TaskAssignedMemberItem();
                    memberItem.setMemberId(member.getLong("user_id"));
                    memberItem.setMemberName(member.getString("user_name"));
                    memberItem.setMemberStatus(member.getString("status"));

                    memberList.add(memberItem);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.e(TAG, "getTaskDetailsFromDB: " + e.getLocalizedMessage());
        }

        showLayout(SUCCESS_CONDITION);
    }

    private void getTaskDetails() {
        showLayout(LOADING_CONDITION);

        AndroidNetworking
                .post(BASE_URL + TASK_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("task_id", String.valueOf(onlineId))
                .setTag("task-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject taskData = response.getJSONObject("data").getJSONObject("task_data");
                                TaskItem taskItem = new TaskItem();
                                taskItem.setTaskId(taskData.getLong("task_id"));
                                taskItem.setTaskName(taskData.getString("task_title"));
                                taskItem.setPriority(taskData.getInt("task_priority"));

                                taskItem.setStartDate(taskData.getLong("task_start_date") * 1000);
                                taskItem.setEndDate(taskData.getLong("task_end_date") * 1000);

                                if (!taskData.isNull("task_details"))
                                    taskItem.setTaskDetail(taskData.getString("task_details"));

                                if (!taskData.isNull("status"))
                                    taskItem.setTaskStatus(taskData.getString("status"));

                                JSONArray members = taskData.getJSONArray("assigned_users");
                                if (members.length() > 0) {
                                    memberList.clear();
                                    for (int i = 0; i < members.length(); i++) {
                                        JSONObject member = members.getJSONObject(i);
                                        TaskAssignedMemberItem memberItem = new TaskAssignedMemberItem();
                                        memberItem.setMemberId(member.getLong("user_id"));
                                        memberItem.setMemberName(member.getString("user_name"));
                                        memberItem.setMemberStatus(member.getString("status"));

                                        memberList.add(memberItem);
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                setTaskDetail(taskItem);

                                showLayout(SUCCESS_CONDITION);
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                                showLayout(ERROR_CONDITION);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());

                            showLayout(ERROR_CONDITION);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        showLayout(ERROR_CONDITION);
                    }
                });
    }

    private void setTaskDetail(TaskItem taskItem) {
        binding.tvTaskName.setText(taskItem.getTaskName());

        switch (taskItem.getPriority()) {
            case 1:
                binding.tvPriority.setText(R.string.high);
                break;
            case 2:
                binding.tvPriority.setText(R.string.medium);
                break;
            case 3:
                binding.tvPriority.setText(R.string.low);
                break;
        }

        if (taskItem.getTaskDetail() != null) {
            binding.tvTaskDetail.setText(taskItem.getTaskDetail());
            binding.clTaskDetail.setVisibility(View.VISIBLE);
        } else
            binding.clTaskDetail.setVisibility(View.GONE);

        if (taskItem.getTaskStatus() != null) {
            binding.tvTaskStatus.setText(taskItem.getTaskStatus());
            binding.clTaskStatus.setVisibility(View.VISIBLE);
        } else
            binding.clTaskStatus.setVisibility(View.GONE);

        binding.tvStartDate.setText(sdfToShow.format(new Date(taskItem.getStartDate())));
        binding.tvEndDate.setText(sdfToShow.format(new Date(taskItem.getEndDate())));
    }

    private void showLayout(int condition) {
        switch (condition) {
            case LOADING_CONDITION:
                binding.clProgress.setVisibility(View.VISIBLE);
                binding.clNoData.setVisibility(View.GONE);
                binding.clData.setVisibility(View.GONE);
                break;
            case SUCCESS_CONDITION:
                binding.clProgress.setVisibility(View.GONE);
                binding.clNoData.setVisibility(View.GONE);
                binding.clData.setVisibility(View.VISIBLE);
                break;
            case ERROR_CONDITION:
                binding.clProgress.setVisibility(View.GONE);
                binding.clNoData.setVisibility(View.VISIBLE);
                binding.clData.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }
}
