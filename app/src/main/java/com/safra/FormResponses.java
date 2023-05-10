package com.safra;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.adapters.FormResponsesRecyclerAdapter;
import com.safra.databinding.ActivityFormResponsesBinding;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.fragments.FormResponseFragment;
import com.safra.models.FileItem;
import com.safra.models.ResponseItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_RESPONSE_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class FormResponses extends AppCompatActivity {

    public static final String TAG = "form_responses_activity";

    private ActivityFormResponsesBinding binding;

    private final List<ResponseItem> responseList = new ArrayList<>();
    private FormResponsesRecyclerAdapter adapter;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int initialProgressPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;

    private boolean isOnlineLoaded = false;

    private long formId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormResponsesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        binding.tvEmptyState.setText(LanguageExtension.setText("currently_there_are_no_responses", getString(R.string.currently_there_are_no_responses)));

        if (getIntent() != null) {
            binding.tvFormResponseHeading.setText(getIntent().getStringExtra("form_title"));
            formId = getIntent().getLongExtra("form_id", -1);
            Log.e(TAG, "onCreate: " + formId);
        }

//        ((SimpleItemAnimator) formsRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvResponses.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvResponses.addItemDecoration(new SpaceItemDecoration(this, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new FormResponsesRecyclerAdapter(this, responseList, (item, position) -> {
            FormResponseFragment dialogR = new FormResponseFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            b.putLong("form_id", formId);
            b.putLong("response_id", item.getResponseId());
            b.putLong("online_id", item.getOnlineId());
            b.putString("form_title", binding.tvFormResponseHeading.getText().toString());
            b.putString("form_fields", item.getResponseData());
//                Log.e(TAG, "onView: " + item.getResponseFiles().size());
            if (item.getResponseFiles() != null && item.getResponseFiles().size() > 0)
                b.putParcelableArrayList("files", item.getResponseFiles());
//                dialogS.setTargetFragment(FormEditFragment.this, REQUEST_SELECT_FORM_ELEMENT);
            dialogR.setArguments(b);
            dialogR.show(ft, FormResponseFragment.TAG);
        });
        binding.rvResponses.setAdapter(adapter);

        checkForEmptyState();
        if (ConnectivityReceiver.isConnected()) {
            isOnlineLoaded = true;
            getResponses(initialProgressPosition);
        } else {
            isOnlineLoaded = false;
            getResponsesFromDB();
        }

        binding.srlForms.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isOnlineLoaded = true;
                currentPage = PAGE_START;
                getResponses(initialProgressPosition);
            } else {
                isOnlineLoaded = false;
                getResponsesFromDB();
            }
        });

        binding.rvResponses.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    private void getResponsesFromDB() {
        responseList.clear();

        responseList.addAll(dbHandler.getResponses(formId));
        Log.e(TAG, "getResponsesFromDB: " + responseList.size());

//        for (ResponseItem ri : responseList) {
//            if (ri.getFormStatus() == 1 &&
//                    PermissionExtension.checkForPermission(FORM_SUBMIT))
//                ri.setFillable(true);
//
//            if (ri.getFormUserId() ==
//                    (isRemembered ? sessionManager.getUserId() : Safra.userId)) {
//                ri.setEditable(true);
//                ri.setResponseViewable(true);
//            }
//
//            if (PermissionExtension.checkForPermission(FORM_UPDATE))
//                ri.setEditable(true);
//
//            if (PermissionExtension.checkForPermission(FORM_RESPONSES))
//                ri.setResponseViewable(true);
//
//            if (PermissionExtension.checkForPermission(FORM_STATUS))
//                ri.setStatusChangeable(true);
//
//            if (PermissionExtension.checkForPermission(FORM_ASSIGN))
//                ri.setAssignable(true);
//        }

        adapter.notifyDataSetChanged();
        Log.e(TAG, "getResponsesFromDB: " + adapter.getItemCount());

        checkForEmptyState();

        if (binding.srlForms.isRefreshing())
            binding.srlForms.setRefreshing(false);
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(responseList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getResponses(p);
    }

    private void getResponses(int pPosition) {
        binding.srlForms.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + FORM_RESPONSE_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("form_id", String.valueOf(formId))
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .setTag("response-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject serverResponse) {
                        Log.e(TAG, "FormFieldOnResponse: " + serverResponse);
                        try {
                            int success = serverResponse.getInt("success");
                            String message = serverResponse.getString("message");
                            if (success == 1) {
                                JSONObject data = serverResponse.getJSONObject("data");
                                JSONArray responses = data.getJSONArray("response_list");
                                int totalPage = data.getInt("total_page");
                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    responseList.clear();
//                                    pPosition = -1;
                                }

                                if (responses.length() > 0) {

                                    for (int i = 0; i < responses.length(); i++) {
                                        JSONObject response = responses.getJSONObject(i);
                                        ResponseItem responseItem = new ResponseItem();
                                        responseItem.setOnlineId(response.getLong("response_id"));
                                        responseItem.setFormId(response.getLong("response_form_id"));
                                        responseItem.setUserId(response.getLong("response_user_id"));
                                        responseItem.setSubmitDate(response.getString("response_date"));

                                        if (!response.isNull("user_name"))
                                            responseItem.setUserName(response.getString("user_name"));
                                        else
                                            responseItem.setUserName("Anonymous");

                                        responseItem.setResponseData(new JSONArray(response.getString("response_user_data")).toString());

                                        JSONArray files = response.getJSONArray("files");
                                        if (files.length() > 0) {
                                            ArrayList<FileItem> fileList = new ArrayList<>();
                                            for (int j = 0; j < files.length(); j++) {
                                                JSONObject file = files.getJSONObject(j);
                                                FileItem fileItem = new FileItem();
                                                fileItem.setFileId(file.getLong("file_id"));
                                                fileItem.setFileUrl(file.getString("file_url"));

                                                HashMap<String, String> hashMap = GeneralExtension
                                                        .getKeyFromJSONObject(new JSONObject(file.getString("file_other_data")));

                                                for (String key : hashMap.keySet()) {
                                                    if (fileItem.getFileUrl().substring(fileItem.getFileUrl().lastIndexOf("/") + 1)
                                                            .contains(key)) {
                                                        fileItem.setParentFieldName(hashMap.get(key));
                                                        Log.e(TAG, "onResponse: " + hashMap.get(key));
                                                    }
                                                }

                                                fileList.add(fileItem);
                                            }
                                            responseItem.setResponseFiles(fileList);
                                        }

                                        responseList.add(responseItem);

                                        dbHandler.addResponse(responseItem);
                                    }
                                }

                                if (pPosition > 1 && pPosition <= responseList.size() - 1) {
                                    Log.e(TAG, "onResponse: removing progress bar -> " + pPosition);
                                    responseList.remove(pPosition);
                                    adapter.notifyItemRemoved(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());

                                checkForEmptyState();

                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(FormResponses.this, message, Toast.LENGTH_SHORT).show();
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

    private void checkForEmptyState() {
        if (responseList.size() > 0) {
            binding.clData.setVisibility(View.VISIBLE);
            binding.clEmptyState.setVisibility(View.GONE);
        } else {
            binding.clData.setVisibility(View.GONE);
            binding.clEmptyState.setVisibility(View.VISIBLE);
        }
    }
}