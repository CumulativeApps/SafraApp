package com.safra.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.AddGroup;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.GroupsRecyclerAdapter;
import com.safra.databinding.FragmentUserGroupsBinding;
import com.safra.events.GroupAddedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.RoleItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.UserPermissions.GROUP_ADD;
import static com.safra.utilities.UserPermissions.GROUP_DELETE;
import static com.safra.utilities.UserPermissions.GROUP_UPDATE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class UserGroupsFragment extends Fragment {

    public static final String TAG = "user_groups_fragment";

    public FragmentActivity mActivity = null;

    private FragmentUserGroupsBinding binding;

    private final List<RoleItem> roleList = new ArrayList<>();
    private GroupsRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;
    private boolean isLoadedOnline = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserGroupsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        setText();

        if (PermissionExtension.checkForPermission(GROUP_ADD)) {
            binding.fabAdd.setVisibility(View.VISIBLE);
        } else {
            binding.fabAdd.setVisibility(View.GONE);
        }

        binding.rvGroups.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvGroups.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new GroupsRecyclerAdapter(mActivity, new GroupsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onEdit(RoleItem item, int position) {
                Intent i = new Intent(mActivity, AddGroup.class);
                i.putExtra("heading", LanguageExtension.setText("edit_group", getString(R.string.edit_group)));
                i.putExtra("is_new", false);
                i.putExtra("role_id", item.getRoleId());
                i.putExtra("online_id", item.getRoleOnlineId());
                startActivity(i);
            }

            @Override
            public void viewGroup(RoleItem item, int position) {
                GroupDetailFragment dialogD = new GroupDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("role_id", item.getRoleId());
                bundle.putLong("online_id", item.getRoleOnlineId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), GroupDetailFragment.TAG);
            }
        });
        binding.rvGroups.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            getGroupsFromDB();
//            getGroups(pPosition);
//            isLoadedOnline = true;
        } else {
        getGroupsFromDB();
        isLoadedOnline = false;
        }

        binding.srlManageGroup.setOnRefreshListener(() -> {
            if(ConnectivityReceiver.isConnected()) {
                currentPage = PAGE_START;
                getGroups(pPosition);
                isLoadedOnline = true;
            getGroupsFromDB();
//            } else {
            getGroupsFromDB();
            isLoadedOnline = false;
            }
        });

        binding.rvGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (isLoadedOnline && !isLastPage && !isNextPageCalled) {
                        if (ConnectivityReceiver.isConnected())
                            loadMoreItems();
//                        else
//                            Toast.makeText(ProductList.this, "Looks like you're not connected with internet!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString();
                if(isLoadedOnline) {
                    currentPage = PAGE_START;
//                    getGroups(pPosition);
                } else{
                    adapter.searchGroup(searchText);
                    checkForEmptyState();
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, AddGroup.class);
            i.putExtra("heading", LanguageExtension.setText("add_group", getString(R.string.add_group)));
            i.putExtra("is_new", true);
            startActivity(i);
        });

        return binding.getRoot();
    }

    private void setText() {
        binding.etSearch.setHint(LanguageExtension.setText("search_the_group", getString(R.string.search_the_group)));
        binding.tvEmptyState.setText(LanguageExtension.setText("no_group_found", getString(R.string.no_group_found)));
    }

    private void getGroupsFromDB() {
        roleList.clear();

        roleList.addAll(dbHandler.getGroups(isRemembered ? userSessionManager.getUserId() : Safra.userId));

        for(RoleItem roleItem : roleList) {
            if (roleItem.getAddedBy() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                roleItem.setEditable(true);
                roleItem.setDeletable(true);
            }

            if (PermissionExtension.checkForPermission(GROUP_UPDATE))
                roleItem.setEditable(true);

            if (PermissionExtension.checkForPermission(GROUP_DELETE))
                roleItem.setDeletable(true);
        }

        adapter.clearLists();
        adapter.addGroupList(roleList);
        Log.e(TAG, "getGroupFromDB: " + adapter.getItemCount());

        checkForEmptyState();

        if (binding.srlManageGroup.isRefreshing())
            binding.srlManageGroup.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(roleList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getGroups(p);
    }

//    private void addLoadingAnimation() {
//        roleList.add(null);
//        pPosition = roleList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//    }

    private void getGroups(int pPosition) {
        binding.srlManageGroup.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;

        AndroidNetworking
                .post(BASE_URL + GROUP_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("search_text", searchText)
                .setTag("group-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray roles = data.getJSONArray("role_list");
                                int totalPage = data.getInt("total_page");
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    roleList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (roles.length() > 0) {
                                    List<RoleItem> gList = new ArrayList<>();
                                    for (int i = 0; i < roles.length(); i++) {
                                        JSONObject role = roles.getJSONObject(i);
                                        RoleItem roleItem = new RoleItem();
                                        roleItem.setRoleOnlineId(role.getInt("role_id"));
                                        roleItem.setRoleName(role.getString("role_name"));

                                        if (role.has("role_module_ids") && !role.isNull("role_module_ids"))
                                            roleItem.setModuleIds(GeneralExtension
                                                    .toLongArray(role.getString("role_module_ids"), ","));

                                        if (role.has("role_permission_ids") && !role.isNull("role_permission_ids"))
                                            roleItem.setPermissionIds(GeneralExtension
                                                    .toLongArray(role.getString("role_permission_ids"), ","));

                                        if (role.has("added_by") && !role.isNull("added_by"))
                                            roleItem.setAddedBy(role.getLong("added_by"));

                                        if (roleItem.getAddedBy() == (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                                            roleItem.setEditable(true);
                                            roleItem.setDeletable(true);
                                        }

                                        if (PermissionExtension.checkForPermission(GROUP_UPDATE))
                                            roleItem.setEditable(true);

                                        if (PermissionExtension.checkForPermission(GROUP_DELETE))
                                            roleItem.setDeletable(true);

                                        gList.add(roleItem);
                                        dbHandler.addGroup(roleItem);
                                    }

                                    roleList.addAll(gList);
                                    adapter.addGroupList(gList);
                                }

                                if (pPosition > 1 && pPosition <= roleList.size() - 1) {
                                    roleList.remove(pPosition);
                                    adapter.removeGroup(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

                                Log.e(TAG, "onResponse: " + adapter.getItemCount());

                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

                        if (binding.srlManageGroup.isRefreshing())
                            binding.srlManageGroup.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                        if (binding.srlManageGroup.isRefreshing())
                            binding.srlManageGroup.setRefreshing(false);
                    }
                });

//        groupList.clear();
//        groupList.add(new GroupItem(1, "E-Commerce Group", 10, 200, false));
//        groupList.add(new GroupItem(2, "Survey Group", 5, 100, false));

//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

//    public void deleteGroup(long userId, int position) {
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("deleting_progress", getString(R.string.deleting_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);
//
//        AndroidNetworking
//                .post(BASE_URL + USER_DELETE_API)
//                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
//                .addBodyParameter("user_id", String.valueOf(userId))
//                .setTag("delete-user-api")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            int success = response.getInt("success");
//                            String message = response.getString("message");
//                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
//                            if (success == 1) {
//                                roleList.remove(position);
//                                adapter.notifyItemRemoved(position);
//                                checkForEmptyState();
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
//                            dialogL.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.e(TAG, "onError: " + anError.getErrorCode());
//                        Log.e(TAG, "onError: " + anError.getErrorDetail());
//                        Log.e(TAG, "onError: " + anError.getErrorBody());
//
//                        dialogL.dismiss();
//                    }
//                });
//    }

    private void checkForEmptyState() {
        if(adapter != null) {
            if (adapter.getItemCount() > 0) {
                binding.clData.setVisibility(View.VISIBLE);
                binding.clEmptyState.setVisibility(View.GONE);
            } else {
                binding.clData.setVisibility(View.GONE);
                binding.clEmptyState.setVisibility(View.VISIBLE);
            }
        } else {
            binding.clData.setVisibility(View.GONE);
            binding.clEmptyState.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_EDIT_GROUP && resultCode == RESULT_SUCCESS_EDIT_GROUP) {
//            if(ConnectivityReceiver.isConnected()) {
//                isLoadedOnline = true;
//                currentPage = PAGE_START;
//                getGroups(pPosition);
//            } else {
//                isLoadedOnline = false;
//                getGroupsFromDB();
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupAdded(GroupAddedEvent event){
        if(ConnectivityReceiver.isConnected()) {
            isLoadedOnline = true;
            currentPage = PAGE_START;
//            getGroups(pPosition);
        } else {
            isLoadedOnline = false;
            getGroupsFromDB();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        EventBus.getDefault().unregister(this);
    }

}
