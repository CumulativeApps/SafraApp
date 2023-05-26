


package com.safra.adapters;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PLANNER_AIM_DELETE;
import static com.safra.utilities.Common.PLANNER_GOAL_DELETE;
import static com.safra.utilities.Common.PLANNER_PROJECT_AIM_LIST;
import static com.safra.utilities.Common.REQUEST_DELETE_AIM;
import static com.safra.utilities.Common.REQUEST_DELETE_GOAL;
import static com.safra.utilities.Common.REQUEST_DELETE_PROJECT;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.safra.AddGoalActvity;
import com.safra.AddProjects;
import com.safra.EditGoalActivity;
import com.safra.R;
import com.safra.Safra;
import com.safra.databinding.ItemLoadingBinding;
import com.safra.databinding.RecyclerActionplanListBinding;
import com.safra.databinding.RecyclerActionplanListBinding;
import com.safra.dialogs.DeleteDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.ViewExtension;
import com.safra.fragments.ActionPlanListFragment;
import com.safra.fragments.ActionPlanTaskListFragment;
import com.safra.fragments.UserDetailFragment;
import com.safra.models.ActionPlanListModel;
import com.safra.models.UserItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionPlanListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "action_aim_adapter";
    private final int VIEW_ACTION_PLAN_LIST = 1;
    private GoalAdapter goalAdapter;
    private boolean loading = true;
    private final Context context;
    private final List<ProjectPlanListModel.Data.AimGoals.Aim> userList = new ArrayList<>();
    private final List<ProjectPlanListModel.Data.AimGoals.Aim.Goal> userList1;
    private final List<ProjectPlanListModel.Data.AimGoals.Aim> userData = new ArrayList<>();
    private final OnItemClickListener listener;
    private FragmentManager fragmentManager;
    private LifecycleOwner lifecycleOwner;

    private boolean isRemembered;
    public interface OnItemClickListener {
        void onDelete(ProjectPlanListModel.Data.AimGoals.Aim item, int position);
        void onEdit(ProjectPlanListModel.Data.AimGoals.Aim item, int position);
        void onView(ProjectPlanListModel.Data.AimGoals.Aim item, int position);
        void changeStatus(View itemView, ProjectPlanListModel.Data.AimGoals.Aim item, int position);
    }


    public ActionPlanListRecyclerAdapter(LifecycleOwner lifecycleOwner,FragmentManager fragmentManager,Context context, OnItemClickListener listener) {
        this.fragmentManager = fragmentManager;
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.listener = listener;
        userList1 = new ArrayList<>();
        this.goalAdapter = new GoalAdapter(context, new GoalAdapter.OnItemClickListener() {
            @Override
            public void onDelete(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {

            }

            @Override
            public void onEdit(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {

            }

            @Override
            public void onView(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {
            }

            @Override
            public void changeStatus(View itemView, ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {

            }
        }
        );
    }
    @Override
    public int getItemViewType(int position) {
        int VIEW_PROGRESS = 0;
        return userList.get(position) != null ? VIEW_ACTION_PLAN_LIST : VIEW_PROGRESS;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_ACTION_PLAN_LIST) {
            RecyclerActionplanListBinding binding = RecyclerActionplanListBinding.inflate(inflater, parent, false);
            return new ActionPlanListViewHolder(binding);
        } else {
            ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
            return new ProgressViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ActionPlanListViewHolder) {
            ActionPlanListViewHolder viewHolder = (ActionPlanListViewHolder) holder;
            viewHolder.bindView(userList.get(position));
        }
        if (holder instanceof GoalAdapter.GoalListViewHolder)
            ((GoalAdapter.GoalListViewHolder) holder).bindView(userList1.get(position));


    }


    @Override
    public int getItemCount() {
        return userList.size();
    }



    public void addUserList(List<ProjectPlanListModel.Data.AimGoals.Aim> userList) {
        this.userList.addAll(userList);
        this.userData.addAll(userList);
    }

    public void removeUser(int position){
        ProjectPlanListModel.Data.AimGoals.Aim userItem = getItem(position);
        userList.remove(position);
        notifyItemRemoved(position);
        userData.remove(userItem);
    }

    public ProjectPlanListModel.Data.AimGoals.Aim getItem(int position){
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
            for(ProjectPlanListModel.Data.AimGoals.Aim ui : userData){
                if(ui.getAim().toLowerCase().contains(searchText))
                    userList.add(ui);
            }
        }
        notifyDataSetChanged();
    }

    class ActionPlanListViewHolder extends RecyclerView.ViewHolder {
        RecyclerActionplanListBinding binding;

        public ActionPlanListViewHolder(@NonNull RecyclerActionplanListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.innerRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        }


        public void bindView(ProjectPlanListModel.Data.AimGoals.Aim item) {
            System.out.println("userList"+ userList1.size());
            isRemembered = userSessionManager.isRemembered();


            fragmentManager.setFragmentResultListener(REQUEST_DELETE_GOAL, lifecycleOwner,
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

             GoalAdapter goalAdapter = new GoalAdapter(context, new GoalAdapter.OnItemClickListener() {

                 @Override
                 public void onDelete(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {

                     System.out.println("Click On Delete Button");
                     System.out.println("userlist"+userList);
                     System.out.println("userlist1"+userList1);
                     DeleteDialog dialogD = new DeleteDialog();
                     Bundle bundle = new Bundle();
                     bundle.putString("request_key", REQUEST_DELETE_GOAL);
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
                 public void onEdit(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {
                     System.out.println("Click On Edit Button");
                     long goalId = item.getId();
                     String goalNAme = item.getGoal();

                     System.out.println("user_id");
                     Intent i = new Intent(context, EditGoalActivity.class);
                     i.putExtra("heading", LanguageExtension.setText("add_goal", context.getString(R.string.add_goal)));
                     i.putExtra("is_new", false);
                     i.putExtra("goal_id", goalId);
                     i.putExtra("goal_name", goalNAme);
                     System.out.println("Goal ID"+ goalId);

//                i.putExtra("online_id", item.getUserOnlineId());
                     context.startActivity(i);

                 }



                 @Override
                 public void onView(ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {
                     System.out.println("Click On View Button");
                     ActionPlanTaskListFragment dialogD = new ActionPlanTaskListFragment();
                     Bundle bundle = new Bundle();
                     bundle.putLong("goal_id", item.getId());
                     System.out.println("goal_id"+item.getId());
//                bundle.putLong("online_id", item.getUserOnlineId());
                     dialogD.setArguments(bundle);
                     dialogD.show(fragmentManager, ActionPlanTaskListFragment.TAG);

                 }

                 @Override
                 public void changeStatus(View itemView, ProjectPlanListModel.Data.AimGoals.Aim.Goal item, int position) {

                 }
             });


            binding.tvAim.setText(item.getAim());
//            ViewExtension.makeVisible(binding.ivDeleteAim, item.isDeletable());
//            ViewExtension.makeVisible(binding.ivEditAim, item.isEditable());
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
//            goalAdapter.clearLists();
            goalAdapter.addUserList(item.getGoals());
//            goalAdapter.getItemCount();
//            System.out.println("List Of Goals- "+ goalAdapter.getItemCount());

            binding.innerRecyclerView.setAdapter(goalAdapter);
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
        }

        public void deleteProject(long projectId, int position) {
            String abc = String.valueOf(goalAdapter.getItemCount());
            isRemembered = userSessionManager.isRemembered();
            LoadingDialogExtension.showLoading(context, LanguageExtension.setText("deleting_progress", context.getString(R.string.deleting_progress)));
            AndroidNetworking
                    .post(BASE_URL + PLANNER_GOAL_DELETE)
                    .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                    .addBodyParameter("planner_goals_id", String.valueOf(projectId))
//                    .setTag("delete-user-api")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LoadingDialogExtension.hideLoading();
                            try {
                            int success = response.getInt("success");
                                String message = response.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {

//
//                                goalAdapter.removeUser(position);
//                                // Remove the item from this adapter's data list
//                                userList1.remove(position);
//                                // Notify this adapter that the item is removed
//                                notifyItemRemoved(position);
//                                // Notify this adapter that the data set has changed
//                                notifyItemRangeChanged(position, getItemCount());

                            }
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

//package com.safra.adapters;
//
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.TextView;
//
//        import androidx.recyclerview.widget.RecyclerView;
//
//        import com.safra.R;
//        import com.safra.models.ActionPlanListModel;
//
//        import java.util.List;
//
//
//public class ActionPlanListRecyclerAdapter extends RecyclerView.Adapter<ActionPlanListRecyclerAdapter.ViewHolder> {
//
//    private List<ProjectPlanListModel.Data.AimGoals.Aim> mAimList;
//
//    public ActionPlanListRecyclerAdapter(List<ProjectPlanListModel.Data.AimGoals.Aim> aimList) {
//        mAimList = aimList;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.recycler_actionplan_list, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        ProjectPlanListModel.Data.AimGoals.Aim aim = mAimList.get(position);
//        holder.mAimTextView.setText(aim.getAim());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mAimList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView mAimTextView;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            mAimTextView = itemView.findViewById(R.id.tvAim);
//        }
//    }
//}
