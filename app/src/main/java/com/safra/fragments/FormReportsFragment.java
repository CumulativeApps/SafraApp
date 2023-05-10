package com.safra.fragments;

import android.content.Context;
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
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.FormReportsRecyclerAdapter;
import com.safra.databinding.FragmentFormReportsBinding;
import com.safra.events.UnsyncedFormsUploaded;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.FormItem;
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
import static com.safra.utilities.Common.FORM_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.UserPermissions.FORM_ASSIGN;
import static com.safra.utilities.UserPermissions.FORM_RESPONSES;
import static com.safra.utilities.UserPermissions.FORM_STATUS;
import static com.safra.utilities.UserPermissions.FORM_SUBMIT;
import static com.safra.utilities.UserPermissions.FORM_UPDATE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class FormReportsFragment extends Fragment {

    public static final String TAG = "form_reports_fragment";

    private FragmentActivity mActivity = null;

    private FragmentFormReportsBinding binding;

    private final List<FormItem> formList = new ArrayList<>();
    private FormReportsRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int initialProgressPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;

    private boolean isOnlineLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormReportsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        binding.etSearch.setHint(LanguageExtension.setText("search_the_form", getString(R.string.search_the_form)));
        binding.tvEmptyState.setText(LanguageExtension.setText("currently_there_is_no_form_available", getString(R.string.currently_there_is_no_form_available)));

        binding.rvForms.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvForms.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new FormReportsRecyclerAdapter(mActivity, (item, position) -> {
            if(item.getFormUniqueId() != null){
                openFormReport(item.getFormOnlineId(), item.getFormUniqueId());
            } else {
                Toast.makeText(mActivity,
                        LanguageExtension.setText("please_upload_this_form_to_view_report_for_his_form",
                                getString(R.string.please_upload_this_form_to_view_report_for_his_form)),
                        Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvForms.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            isOnlineLoaded = true;
            getForms(initialProgressPosition);
        } else {
            isOnlineLoaded = false;
            getFormsFromDB();
        }

        binding.srlForms.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isOnlineLoaded = true;
                currentPage = PAGE_START;
                getForms(initialProgressPosition);
            } else {
                isOnlineLoaded = false;
                getFormsFromDB();
            }
        });

        binding.rvForms.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (isOnlineLoaded && !isLastPage && !isNextPageCalled) {
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
                if(isOnlineLoaded) {
                    currentPage = PAGE_START;
                    getForms(initialProgressPosition);
                } else {
                    adapter.searchForm(searchText);
                    checkForEmptyState();
                }
            }
        });

        return binding.getRoot();
    }

    private void getFormsFromDB() {
        formList.clear();

        formList.addAll(dbHandler.getForms(isRemembered ? userSessionManager.getUserId() : Safra.userId));
        Log.e(TAG, "getFormsFromDB: " + formList.size());

        adapter.clearLists();
        adapter.addFormList(formList);
        Log.e(TAG, "getFormsFromDB: " + adapter.getItemCount());

        checkForEmptyState();

        if (binding.srlForms.isRefreshing())
            binding.srlForms.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(formList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getForms(p);
    }

//    private int addLoadingAnimation() {
//        formList.add(null);
//        int pPosition = formList.size() - 1;
//        Log.e(TAG, "onLoadMore: " + pPosition);
//        adapter.notifyItemInserted(pPosition);
//        adapter.notifyItemChanged(pPosition-1);
//        return pPosition;
//    }

    private void getForms(int pPosition) {
        binding.srlForms.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + FORM_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("search_text", searchText)
                .addBodyParameter("user_id", String.valueOf(userSessionManager.getUserId()))
                .addBodyParameter("user_role", String.valueOf(userSessionManager.getUserRoleId()))
                .setTag("form-list-api")
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
                                JSONArray forms = data.getJSONArray("form_list");
                                int totalPage = data.getInt("total_page");
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    formList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (forms.length() > 0) {
                                    List<FormItem> fList = new ArrayList<>();
                                    for (int i = 0; i < forms.length(); i++) {
                                        JSONObject form = forms.getJSONObject(i);
                                        FormItem formItem = new FormItem();
                                        formItem.setFormOnlineId(form.getLong("form_id"));
                                        formItem.setFormUniqueId(form.getString("form_unique_id"));
                                        formItem.setFormName(form.getString("form_name"));
                                        formItem.setFormLanguageId(form.getInt("form_language_id"));
                                        formItem.setFormLanguageName(form.getString("language_title"));

                                        if (form.has("form_description") && !form.isNull("form_description"))
                                            formItem.setFormDescription(form.getString("form_description"));

                                        if (form.has("form_json") && !form.isNull("form_json"))
                                            formItem.setFormJson(new JSONArray(form.getString("form_json")).toString());

                                        if (form.has("form_expiry_date") && !form.isNull("form_expiry_date"))
                                            formItem.setFormExpiryDate(form.getString("form_expiry_date"));

                                        if (form.has("form_access") && !form.isNull("form_access"))
                                            formItem.setFormAccess(form.getInt("form_access"));

                                        if (form.has("form_status") && !form.isNull("form_status"))
                                            formItem.setFormStatus(form.getInt("form_status"));

                                        if (form.has("form_link") && !form.isNull("form_link"))
                                            formItem.setFormLink(form.getString("form_link"));

                                        if (form.has("form_user_ids") && !form.isNull("form_user_ids"))
                                            formItem.setUserIds(GeneralExtension.toLongArray(form.getString("form_user_ids"), ","));

                                        if (form.has("form_group_ids") && !form.isNull("form_group_ids"))
                                            formItem.setGroupIds(GeneralExtension.toLongArray(form.getString("form_group_ids"), ","));

                                        if (form.has("form_group_user_ids") && !form.isNull("form_group_user_ids"))
                                            formItem.setGroupUserIds(GeneralExtension.toLongArray(form.getString("form_group_user_ids"), ","));

                                        if (formItem.getFormStatus() == 1 &&
                                                PermissionExtension.checkForPermission(FORM_SUBMIT))
                                            formItem.setFillable(true);

                                        formItem.setDelete(form.getInt("is_delete") == 1);

                                        formItem.setFormUserId(form.getLong("form_master_id"));
                                        if (formItem.getFormUserId() ==
                                                (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                                            formItem.setEditable(true);
                                            formItem.setResponseViewable(true);
                                        }


                                        if (PermissionExtension.checkForPermission(FORM_UPDATE))
                                            formItem.setEditable(true);

                                        if (PermissionExtension.checkForPermission(FORM_RESPONSES))
                                            formItem.setResponseViewable(true);

                                        if (PermissionExtension.checkForPermission(FORM_STATUS))
                                            formItem.setStatusChangeable(true);

                                        if (PermissionExtension.checkForPermission(FORM_ASSIGN))
                                            formItem.setAssignable(true);

                                        fList.add(formItem);
                                        dbHandler.addForm(formItem);
                                    }

                                    formList.addAll(fList);
                                    adapter.addFormList(fList);
                                }

                                if (pPosition > 1 && pPosition <= formList.size() - 1) {
                                    Log.e(TAG, "onResponse: removing progress bar -> " + pPosition);
                                    formList.remove(pPosition);
                                    adapter.removeForm(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

                        if (binding.srlForms.isRefreshing())
                            binding.srlForms.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                        if (binding.srlForms.isRefreshing())
                            binding.srlForms.setRefreshing(false);
                    }
                });

//        formList.clear();
//        formList.add(new FormItem());
//        formList.add(new FormItem());
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    private void openFormReport(long onlineId, String formUniqueId){
//        String s = "https://safra.co.mz/app-form-reports/" + (isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
//        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//        CustomTabColorSchemeParams.Builder customTabColorSchemeParamsBuilder =
//                new CustomTabColorSchemeParams.Builder();
//        customTabColorSchemeParamsBuilder.setToolbarColor(ContextCompat.getColor(mActivity, R.color.color_primary));
//        customTabColorSchemeParamsBuilder.setSecondaryToolbarColor(ContextCompat.getColor(mActivity, R.color.color_primary));
//        builder.setDefaultColorSchemeParams(customTabColorSchemeParamsBuilder.build());
//        builder.setShowTitle(false);
//        builder.setUrlBarHidingEnabled(false);
//        CustomTabsIntent intent = builder.build();
//        intent.launchUrl(mActivity, Uri.parse(s));
    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadedUnsyncedForms(UnsyncedFormsUploaded event){
        if (ConnectivityReceiver.isConnected()) {
            isOnlineLoaded = true;
            currentPage = PAGE_START;
            getForms(initialProgressPosition);
        } else {
            isOnlineLoaded = false;
            getFormsFromDB();
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
        mActivity = null;
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
