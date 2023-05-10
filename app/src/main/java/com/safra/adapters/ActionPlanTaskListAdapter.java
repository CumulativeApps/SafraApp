package com.safra.adapters;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PLANNER_AIM_DELETE;
import static com.safra.utilities.Common.PLANNER_TASK_DELETE_RESOURCE;
import static com.safra.utilities.Common.REQUEST_DELETE_ACTION_RESOURCE;
import static com.safra.utilities.Common.REQUEST_DELETE_ACTION_TASK;
import static com.safra.utilities.Common.REQUEST_DELETE_AIM;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerActionplanTasklistBinding;
import com.safra.databinding.RecyclerActionplanTasklistBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.ViewExtension;
import com.safra.fragments.ActionPlanTaskListFragment;
import com.safra.models.ActionTaskListModel;
import com.safra.models.ProjectListResponseModel;
import com.safra.utilities.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionPlanTaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_PROJECT = 1;
    FragmentManager fragmentManager;
    LifecycleOwner lifecycleOwner;
    public static final String TAG = "task_list_adapter";
    private boolean isRemembered;

    private final Context context;
    private final List<ActionTaskListModel.Data.Goal.Task> userList = new ArrayList<>();
    private final List<ActionTaskListModel.Data.Goal.Task.Resource> userList1 = new ArrayList<>();
    private final List<ActionTaskListModel.Data.Goal.Task> userData = new ArrayList<>();
    private final ActionPlanTaskListAdapter.OnItemClickListener listener;

    public ActionPlanTaskListAdapter(LifecycleOwner lifecycleOwner,FragmentManager fragmentManager,Context context, ActionPlanTaskListAdapter.OnItemClickListener listener) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDelete(ActionTaskListModel.Data.Goal.Task item, int position);

        void onEdit(ActionTaskListModel.Data.Goal.Task item, int position);

        void onView(ActionTaskListModel.Data.Goal.Task item, int position);

        void changeStatus(View itemView, ActionTaskListModel.Data.Goal.Task item, int position);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_PROJECT : VIEW_PROGRESS;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_PROJECT) {
            RecyclerActionplanTasklistBinding binding = RecyclerActionplanTasklistBinding.inflate(inflater, parent, false);
            return new ActionPlanTaskListAdapter.ActionPlanTaskListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ActionPlanTaskListAdapter.ProgressViewHolder(binding);
        }    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ActionPlanTaskListAdapter.ActionPlanTaskListViewHolder)
            ((ActionPlanTaskListAdapter.ActionPlanTaskListViewHolder) holder).bindView(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUserList(List<ActionTaskListModel.Data.Goal.Task> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        ActionTaskListModel.Data.Goal.Task ProjectListResponseModel = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(ProjectListResponseModel);

    }

    public ActionTaskListModel.Data.Goal.Task getItem(int position){
        return userList.get(position);
    }

    public void clearLists(){
        userList.clear();
        userData.clear();

        notifyDataSetChanged();
    }

    public void searchUser(String searchText){
        searchText = searchText.toLowerCase();
        userList.clear();
        if(searchText.isEmpty()){
            userList.addAll(userData);
        } else {
            for(ActionTaskListModel.Data.Goal.Task ui : userData){
                if(ui.getTitle().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }

        notifyDataSetChanged();
    }

    class ActionPlanTaskListViewHolder extends RecyclerView.ViewHolder {
        RecyclerActionplanTasklistBinding binding;
        ActionPlanTaskResourcesAdapter actionPlanTaskResourcesAdapter;
        public ActionPlanTaskListViewHolder(@NonNull RecyclerActionplanTasklistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.innerRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        }

        public void bindView(ActionTaskListModel.Data.Goal.Task item) {
            isRemembered = userSessionManager.isRemembered();


            binding.tvStartDateTitle.setText(LanguageExtension.setText("start_date", context.getString(R.string.start_date)));
            binding.tvEndDateTitle.setText(LanguageExtension.setText("end_date", context.getString(R.string.end_date)));
            binding.tvPriorityTitle.setText(LanguageExtension.setText("priority", context.getString(R.string.priority)));
            binding.tvResponseLevelTitle.setText(LanguageExtension.setText("response_level", context.getString(R.string.response_level)));
            binding.tvStatusTitle.setText(LanguageExtension.setText("status", context.getString(R.string.status)));
//            binding.tvChangeStatus.setText(LanguageExtension.setText("change_status", context.getString(R.string.change_status)));
            binding.tvViewDetails.setText(LanguageExtension.setText("add_resource", context.getString(R.string.add_resource)));

            binding.tvTitleName.setText(item.getTitle());

            if (item.getStartDate() != null)
                binding.tvStartDate.setText(item.getStartDate());
            else
                binding.tvStartDate.setText("-");

            if (item.getEndDate() != null)
                binding.tvEndDate.setText(item.getEndDate());
            else
                binding.tvEndDate.setText("-");

            if (item.getResponsible() != null)
                binding.tvResponseLevel.setText(item.getResponsible());
            else
                binding.tvResponseLevel.setText("-");

                actionPlanTaskResourcesAdapter = new ActionPlanTaskResourcesAdapter(context, new ActionPlanTaskResourcesAdapter.OnItemClickListener() {
                @Override
                public void onDelete(ActionTaskListModel.Data.Goal.Task.Resource item, int position) {

                    System.out.println("DELETE RESOURCE");
                    DeleteDialog dialogD = new DeleteDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("request_key", REQUEST_DELETE_ACTION_RESOURCE);
                    bundle.putString("message", LanguageExtension.setText("do_you_want_to_delete_this_user", context.getString(R.string.do_you_want_to_delete_this_user)));
                    bundle.putLong("id", item.getId());
                    bundle.putLong("online_id", item.getId());
                    bundle.putInt("position", position);
                    bundle.putString("type", "project");
                    System.out.println("POSITION :- "+position);
                    dialogD.setArguments(bundle);
                    dialogD.show(fragmentManager, DeleteDialog.TAG);

                }

                @Override
                public void onEdit(ActionTaskListModel.Data.Goal.Task.Resource item, int position) {

                }

                @Override
                public void onView(ActionTaskListModel.Data.Goal.Task.Resource item, int position) {

                }

                @Override
                public void changeStatus(View itemView, ActionTaskListModel.Data.Goal.Task.Resource item, int position) {

                }
            });
            fragmentManager.setFragmentResultListener(REQUEST_DELETE_ACTION_RESOURCE, lifecycleOwner,
                    (requestKey, result) -> {
//                    long userId = result.getLong("id");
                        long onlineId = result.getLong("id");
                        int position = result.getInt("position");
                        if (ConnectivityReceiver.isConnected()) {

//                        deleteUserOffline(userId,onlineId, position);
                            deleteProject(onlineId, position);
//                        deleteUserOffline(userId, position);
                        } else
                            deleteProject(onlineId, position);

//                    deleteUserOffline(userId, position);
                    });
            actionPlanTaskResourcesAdapter.addUserList(item.getResources());
            binding.innerRecyclerView.setAdapter(actionPlanTaskResourcesAdapter);
            binding.innerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        if (!recyclerView.canScrollVertically(1)) {
                            // Post a task to the main thread to modify the adapter data
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Modify the adapter data here
                                }
                            });
                        }

                    }
                }
            });
            ViewExtension.makeVisible(binding.clExpandLayout, item.isExpanded());
            ViewExtension.toggleArrow(binding.ivExpandDetail, item.isExpanded());

            itemView.setOnClickListener(v -> {
                if (!item.isExpanded()) {
                    ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                    item.setExpanded(ViewExtension.toggleArrow(binding.ivExpandDetail, !item.isExpanded()));
                }
            });

            binding.ivExpandDetail.setOnClickListener(v -> {
                ViewExtension.expandLayout(binding.clExpandLayout, !item.isExpanded());
                item.setExpanded(ViewExtension.toggleArrow(v, !item.isExpanded()));
            });

            binding.ivDeleteAim.setOnClickListener(v -> listener.onDelete(item, getAbsoluteAdapterPosition()));

            binding.ivEditAim.setOnClickListener(v -> listener.onEdit(item, getAbsoluteAdapterPosition()));

            binding.tvViewDetails.setOnClickListener(v -> listener.onView(item, getAbsoluteAdapterPosition()));

            switch (item.getStatus()){
                case 0:
                    binding.tvStatus.setText(LanguageExtension.setText("pending", context.getString(R.string.pending)));
                    break;
                case 1:
                    binding.tvStatus.setText(LanguageExtension.setText("in_progress", context.getString(R.string.in_progress)));
                    break;
                default:
                    binding.tvStatus.setText(LanguageExtension.setText("completed", context.getString(R.string.completed)));

            }
            switch (item.getPriority()){
                case 1:
                    binding.tvPriority.setText(LanguageExtension.setText("high", context.getString(R.string.high)));
                    break;
                case 2:
                    binding.tvPriority.setText(LanguageExtension.setText("medium", context.getString(R.string.medium)));
                    break;
                default:
                    binding.tvPriority.setText(LanguageExtension.setText("low", context.getString(R.string.low)));

            }
        }

        public void deleteProject(long projectId, int position) {
            LoadingDialogExtension.showLoading(context, LanguageExtension.setText("deleting_progress", context.getString(R.string.deleting_progress)));
            AndroidNetworking
                    .post(BASE_URL + PLANNER_TASK_DELETE_RESOURCE)
                    .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                    .addBodyParameter("goal_task_resource_id", String.valueOf(projectId))
//                .setTag("delete-user-api")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LoadingDialogExtension.hideLoading();
                            try {
//                            int success = response.getInt("success");
                                String message = response.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
//                            if (success == 1) {
//                                actionPlanTaskResourcesAdapter.removeItem(position);
                                actionPlanTaskResourcesAdapter.removeUser(position);
//                                actionPlanTaskResourcesAdapter.removeItem(position);
//                                notifyItemChanged(position);
//
//                                actionPlanTaskResourcesAdapter.removeUser(position);
//                                notifyItemChanged(position);
//                            }
                            } catch (JSONException e) {
                                Log.e(TAG, "onResponseError: " + e.getLocalizedMessage());
//                            dialogL.dismiss();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e(TAG, "onError: " + anError.getErrorCode());
                            Log.e(TAG, "onError: " + anError.getErrorDetail());
                            Log.e(TAG, "onError: " + anError.getErrorBody());
                            LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                        }
                    });
        }


    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull ItemLoadingBinding binding) {
            super(binding.getRoot());
            binding.tvLoading.setText(LanguageExtension.setText("loading_content", context.getString(R.string.loading_content)));
        }
    }
}
