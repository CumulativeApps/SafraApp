package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_LIST_API;
import static com.safra.utilities.Common.FORM_STATUS_API;
import static com.safra.utilities.Common.FORM_VIEW_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.REQUEST_ADD_FORM_OPTION;
import static com.safra.utilities.UserPermissions.FORM_ADD;
import static com.safra.utilities.UserPermissions.FORM_ASSIGN;
import static com.safra.utilities.UserPermissions.FORM_RESPONSES;
import static com.safra.utilities.UserPermissions.FORM_STATUS;
import static com.safra.utilities.UserPermissions.FORM_SUBMIT;
import static com.safra.utilities.UserPermissions.FORM_UPDATE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.CreateForm;
import com.safra.FillForm;
import com.safra.FormResponses;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.FormsRecyclerAdapter;
import com.safra.databinding.FragmentFormsBinding;
import com.safra.databinding.PopupMoreOptionFormBinding;
import com.safra.dialogs.ChooserForNewFormDialog;
import com.safra.events.FormAddedEvent;
import com.safra.events.UnsyncedFormsUploaded;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
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

public class FormsFragment extends Fragment {

    public static final String TAG = "forms_fragment";

    private FragmentFormsBinding binding;

    private FragmentActivity mActivity = null;

    private final List<FormItem> formList = new ArrayList<>();
    private FormsRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int initialProgressPosition = -1;
    private boolean isNextPageCalled = false;

    private PopupWindow popupWindow;

    private boolean isRemembered;

    private boolean isOnlineLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormsBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        binding.etSearch.setHint(LanguageExtension.setText("search_the_form", getString(R.string.search_the_form)));
        binding.tvEmptyState.setText(LanguageExtension.setText("currently_there_are_no_forms_press_below_button_to_generate_new_form", getString(R.string.currently_there_are_no_forms_press_below_button_to_generate_new_form)));

        if (PermissionExtension.checkForPermission(FORM_ADD))
            binding.fabAdd.setVisibility(View.VISIBLE);
        else
            binding.fabAdd.setVisibility(View.GONE);

        if (getArguments() != null) {
            if (getArguments().containsKey("reference_id") && getArguments().getLong("reference_id") > -1) {
                openAssignedForm(getArguments().getLong("reference_id"));
            }
        }

        binding.rvForms.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvForms.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new FormsRecyclerAdapter(mActivity, new FormsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void showMoreOption(View view, FormItem item, int position) {
                setPopUpWindowForChangeStatus(view, item);
            }

            @Override
            public void onEdit(FormItem item, int position) {
                Intent i = new Intent(mActivity, CreateForm.class);
                i.putExtra("heading", LanguageExtension.setText("edit_form", getString(R.string.edit_form)));
                i.putExtra("is_new", false);
                i.putExtra("form_id", item.getFormId());
                i.putExtra("online_id", item.getFormOnlineId());
                startActivity(i);
            }

            @Override
            public void onView(FormItem item, int position) {
                FormPreviewFragment previewDialog = new FormPreviewFragment();
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                b.putString("form_title", item.getFormName());
                b.putString("form_fields", item.getFormJson());
//                dialogS.setTargetFragment(FormEditFragment.this, REQUEST_SELECT_FORM_ELEMENT);
                previewDialog.setArguments(b);
                previewDialog.show(ft, FormPreviewFragment.TAG);
            }

            @Override
            public void onFill(FormItem item, int position) {
                Intent i = new Intent(mActivity, FillForm.class);
                Bundle bundle = new Bundle();
                bundle.putString("reason_to_come", "add_response");
                bundle.putLong("form_id", item.getFormOnlineId());
                bundle.putString("form_title", item.getFormName());
                bundle.putString("form_fields", item.getFormJson());
                bundle.putLong("form_access", item.getFormAccess());
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        binding.rvForms.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            isOnlineLoaded = true;
            getForms(initialProgressPosition);
//            getFormsFromDB();
        } else {

            isOnlineLoaded = false;
            getFormsFromDB();
        }

        binding.srlForms.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isOnlineLoaded = true;
                currentPage = PAGE_START;
//                getFormsFromDB();
                getForms(initialProgressPosition);
            } else {
                isOnlineLoaded = false;
                getFormsFromDB();
            }
        });

//        binding.rvForms.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) {
//                    if (isOnlineLoaded && !isLastPage && !isNextPageCalled) {
//                        if (ConnectivityReceiver.isConnected())
//                            loadMoreItems();
////                        else
////                            Toast.makeText(ProductList.this, "Looks like you're not connected with internet!", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });

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
                if (isOnlineLoaded) {
                    currentPage = PAGE_START;
                    getForms(initialProgressPosition);
                } else {
                    adapter.searchForm(searchText);
                    checkForEmptyState();
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
//            Intent i = new Intent(mActivity, CreateForm.class);
//            i.putExtra("heading", "Create Form");
//            i.putExtra("is_new", true);
//            startActivityForResult(i, REQUEST_ADD_FORM);
            ChooserForNewFormDialog dialogN = new ChooserForNewFormDialog();
            Bundle bundle = new Bundle();
            bundle.putString("request_key", REQUEST_ADD_FORM_OPTION);
            dialogN.setArguments(bundle);
//            dialogN.setTargetFragment(FormsFragment.this, REQUEST_ADD_FORM_OPTION);
            dialogN.show(getChildFragmentManager(), ChooserForNewFormDialog.TAG);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_ADD_FORM_OPTION, this,
                (requestKey, result) -> {
                    Log.e(TAG, "onCreateView: return from dialog");
                    if (requestKey.equalsIgnoreCase(REQUEST_ADD_FORM_OPTION)) {
                        Intent i = new Intent(mActivity, CreateForm.class);
                        i.putExtra("heading", LanguageExtension.setText("create_form", getString(R.string.create_form)));
                        i.putExtra("is_new", true);
                        i.putExtra("use_template", result.getBoolean("use_template"));
                        startActivity(i);
                    }
                });

        return binding.getRoot();
    }

    private void getFormsFromDB() {
        formList.clear();

        formList.addAll(dbHandler.getForms(isRemembered ? userSessionManager.getUserId() : Safra.userId));
        Log.e(TAG, "getFormsFromDB: " + formList.size());

        for (FormItem fi : formList) {
            if (fi.getFormStatus() == 1 &&
                    PermissionExtension.checkForPermission(FORM_SUBMIT))
                fi.setFillable(true);

            if (fi.getFormUserId() ==
                    (isRemembered ? userSessionManager.getUserId() : Safra.userId)) {
                fi.setEditable(true);
                fi.setResponseViewable(true);
            }

            if (PermissionExtension.checkForPermission(FORM_UPDATE))
                fi.setEditable(true);

            if (PermissionExtension.checkForPermission(FORM_RESPONSES))
                fi.setResponseViewable(true);

            if (PermissionExtension.checkForPermission(FORM_STATUS))
                fi.setStatusChangeable(true);

            if (PermissionExtension.checkForPermission(FORM_ASSIGN))
                fi.setAssignable(true);
        }

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
                .setTag("form-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "getFormsOnResponse: " + response);
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
                                        Log.e(TAG, "onResponse: " + formItem.getFormName());
                                        formItem.setFormLanguageId(form.getInt("form_language_id"));
                                        formItem.setFormLanguageName(form.getString("language_title"));


                                        if (form.has("form_description") && !form.isNull("form_description"))
                                            formItem.setFormDescription(form.getString("form_description"));

//
//                                        String jsonString = form.getString("form_json");
//                                        JSONArray jsonArray = new JSONArray(); // create empty JSONArray
//
//                                        try {
//                                            JSONObject jsonObject = new JSONObject(jsonString); // create JSONObject from the given string
//                                            jsonArray.put(jsonObject); // add the JSONObject to the JSONArray
//                                            // Use the jsonArray object as needed
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//
//
//                                        if (form.has("form_json") && !form.isNull("form_json"))
//
//                                            formItem.setFormJson(String.valueOf(jsonArray));
//                                        System.out.println(jsonArray);

                                        if (form.has("form_json") && !form.isNull("form_json"))
                                            formItem.setFormJson(new JSONArray(form.getString("form_json")).toString());



                                        if (form.has("form_expiry_date") && !form.isNull("form_expiry_date"))
                                            formItem.setFormExpiryDate(form.getString("form_expiry_date"));

                                        if (form.has("form_access") && !form.isNull("form_access"))
                                            formItem.setFormAccess(form.getInt("form_access"));

                                        if (form.has("form_type") && !form.isNull("form_type"))
                                            formItem.setFormType(form.getLong("form_type"));

                                        if (form.has("form_mcq_marks") && !form.isNull("form_mcq_marks"))
                                            formItem.setTotalMarks(form.getInt("form_mcq_marks"));

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
//                                    adapter.removeForm(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;

                                if (!isLastPage)
                                    if (ConnectivityReceiver.isConnected())
                                        loadMoreItems();
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

    private void openAssignedForm(long onlineId) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + FORM_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("form_id", String.valueOf(onlineId))
                .setTag("form-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject formData = response.getJSONObject("data").getJSONObject("form_data");
                                FormItem formItem = new FormItem();
                                formItem.setFormOnlineId(formData.getLong("form_id"));
                                formItem.setFormUniqueId(formData.getString("form_unique_id"));
                                formItem.setFormName(formData.getString("form_name"));
                                formItem.setFormLanguageId(formData.getLong("form_language_id"));

                                if (!formData.isNull("form_description"))
                                    formItem.setFormDescription(formData.getString("form_description"));

//                                String jsonString = formData.getString("form_json");
//                                JSONArray jsonArray = new JSONArray(); // create empty JSONArray
//
//                                try {
//                                    JSONObject jsonObject = new JSONObject(jsonString); // create JSONObject from the given string
//                                    jsonArray.put(jsonObject); // add the JSONObject to the JSONArray
//                                    // Use the jsonArray object as needed
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                formItem.setFormJson(jsonArray.toString());
                                formItem.setFormJson(new JSONArray(formData.getString("form_json")).toString());

                                formItem.setFormExpiryDate(formData.getString("form_expiry_date"));

                                formItem.setFormAccess(formData.getLong("form_access"));

                                formItem.setFormUserId(formData.getLong("form_master_id"));

                                formItem.setFormStatus(formData.getInt("form_status"));

                                if (formData.has("form_user_ids") && !formData.isNull("form_user_ids"))
                                    formItem.setUserIds(GeneralExtension.toLongArray(formData.getString("form_user_ids"), ","));

                                if (formData.has("form_group_ids") && !formData.isNull("form_group_ids"))
                                    formItem.setGroupIds(GeneralExtension.toLongArray(formData.getString("form_group_ids"), ","));

                                if (formData.has("form_group_user_ids") && !formData.isNull("form_group_user_ids"))
                                    formItem.setGroupUserIds(GeneralExtension.toLongArray(formData.getString("form_group_user_ids"), ","));

                                formItem.setDelete(formData.getInt("is_delete") == 1);

                                Intent i = new Intent(mActivity, FillForm.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("reason_to_come", "add_response");
                                bundle.putLong("form_id", formItem.getFormOnlineId());
                                bundle.putString("form_title", formItem.getFormName());
                                bundle.putString("form_fields", formItem.getFormJson());

                                bundle.putLong("form_access", formItem.getFormAccess());
                                i.putExtras(bundle);
                                startActivity(i);

                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
//                            dialogL.dismiss();
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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

    public void changeFormStatus(long formId, String formStatus) {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_progress", getString(R.string.updating_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + FORM_STATUS_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("form_id", String.valueOf(formId))
                .addBodyParameter("form_status", formStatus)
                .setTag("change-form-status-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                dbHandler.updateFormStatus(formId, formStatus, true);

                                if (ConnectivityReceiver.isConnected()) {
                                    isOnlineLoaded = true;
                                    currentPage = PAGE_START;
                                    getForms(initialProgressPosition);
                                } else {
                                    isOnlineLoaded = false;
                                    getFormsFromDB();
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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

    private void setPopUpWindowForChangeStatus(View parentView, FormItem item) {
        PopupMoreOptionFormBinding popupBinding = PopupMoreOptionFormBinding.inflate(getLayoutInflater());
        popupBinding.tvActivate.setText(LanguageExtension.setText("activate", getString(R.string.activate)));
        popupBinding.tvDeactivate.setText(LanguageExtension.setText("deactivate", getString(R.string.deactivate)));
        popupBinding.tvCopyLink.setText(LanguageExtension.setText("copy_link", getString(R.string.copy_link)));
        popupBinding.tvViewResponses.setText(LanguageExtension.setText("view_responses", getString(R.string.view_responses)));

        if (item.isStatusChangeable()) {
            if (item.getFormStatus() == 1)
                popupBinding.tvActivate.setVisibility(View.GONE);
            else
                popupBinding.tvDeactivate.setVisibility(View.GONE);
        } else {
            popupBinding.tvActivate.setVisibility(View.GONE);
            popupBinding.tvDeactivate.setVisibility(View.GONE);
        }

        if (item.getFormLink() == null)
            popupBinding.tvCopyLink.setVisibility(View.GONE);

        if (!item.isResponseViewable()) {
            popupBinding.tvViewResponses.setVisibility(View.GONE);
        }

        popupBinding.tvActivate.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
                changeFormStatus(item.getFormOnlineId(), "active");
            else
                dbHandler.updateFormStatus(item.getFormOnlineId(), "active", false);
            popupWindow.dismiss();
        });
        popupBinding.tvDeactivate.setOnClickListener(v -> {
            if (ConnectivityReceiver.isConnected())
                changeFormStatus(item.getFormOnlineId(), "deactive");
            else
                dbHandler.updateFormStatus(item.getFormOnlineId(), "deactive", false);
            popupWindow.dismiss();
        });
        popupBinding.tvCopyLink.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Form link", item.getFormLink());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(mActivity, "Copied", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
        popupBinding.tvViewResponses.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, FormResponses.class);
            i.putExtra("form_title", item.getFormName());
            i.putExtra("form_id", item.getFormOnlineId());
            startActivity(i);
            popupWindow.dismiss();
        });

        popupWindow = new PopupWindow(popupBinding.getRoot(), getResources().getDimensionPixelSize(R.dimen.group_edit_popup_width),
                ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        // Removes default background.
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setElevation(10f);

        popupWindow.showAsDropDown(parentView, getResources().getDimensionPixelOffset(R.dimen._0dp), getResources().getDimensionPixelOffset(R.dimen._0dp), Gravity.END | Gravity.TOP);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "onActivityResult: " + requestCode);
//        Log.e(TAG, "onActivityResult: " + resultCode);
////        if (requestCode == REQUEST_ADD_FORM && resultCode == RESULT_SUCCESS_ADD_FORM) {
////            if (ConnectivityReceiver.isConnected()) {
////                isOnlineLoaded = true;
////                currentPage = PAGE_START;
////                getForms(initialProgressPosition);
////            } else {
////                isOnlineLoaded = false;
////                getFormsFromDB();
////            }
////        }
////        else if (requestCode == REQUEST_ADD_FORM_OPTION && resultCode == RESULT_ADD_FORM_OPTION) {
////            Intent i = new Intent(mActivity, CreateForm.class);
////            i.putExtra("heading", LanguageExtension.setText("create_form", getString(R.string.create_form)));
////            i.putExtra("is_new", true);
////            i.putExtra("use_template", data != null && data.getBooleanExtra("use_template", false));
////            startActivityForResult(i, REQUEST_ADD_FORM);
////        }
//    }

    private void checkForEmptyState() {
        if (adapter != null) {
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
    public void uploadedUnsyncedForms(UnsyncedFormsUploaded event) {
        if (ConnectivityReceiver.isConnected()) {
            isOnlineLoaded = true;
            currentPage = PAGE_START;
            getForms(initialProgressPosition);
        } else {
            isOnlineLoaded = false;
            getFormsFromDB();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFormAdded(FormAddedEvent event) {
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

    public class FormField {
        private String className;
        private boolean required;
        private String label;
        private String name;
        private boolean access;
        private String type;
        private String subtype;

        public FormField(String className, boolean required, String label, String name, boolean access, String type, String subtype) {
            this.className = className;
            this.required = required;
            this.label = label;
            this.name = name;
            this.access = access;
            this.type = type;
            this.subtype = subtype;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isAccess() {
            return access;
        }

        public void setAccess(boolean access) {
            this.access = access;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubtype() {
            return subtype;
        }

        public void setSubtype(String subtype) {
            this.subtype = subtype;
        }

        // Constructors, getters, and setters
    }

}
