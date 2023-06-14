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
import com.google.gson.Gson;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.TemplatesRecyclerAdapter;
import com.safra.databinding.FragmentTemplatesBinding;
import com.safra.events.TemplateSelectedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.ViewExtension;
import com.safra.models.AllergiesListModel;
import com.safra.models.TemplateItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.TEMPLATE_LIST_API;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class TemplatesFragment extends DialogFragment {

    public static final String TAG = "templates_fragment";

    private FragmentActivity mActivity = null;

    private FragmentTemplatesBinding binding;

    private final List<TemplateItem.Data.TemplateList> templateList = new ArrayList<>();
    private TemplatesRecyclerAdapter adapter;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;

    private boolean isRemembered;

    private boolean isOnlineLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTemplatesBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        binding.tvSelectTemplateHeading.setText(LanguageExtension.setText("select_template", getString(R.string.select_template)));
        binding.tvEmptyState.setText(LanguageExtension.setText("currently_there_are_no_templates", getString(R.string.currently_there_are_no_templates)));

//        ((SimpleItemAnimator) templatesRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvTemplates.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvTemplates.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));
        adapter = new TemplatesRecyclerAdapter(mActivity, templateList, new TemplatesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(TemplateItem.Data.TemplateList item, int position) {
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("form_fields", item.getTemplateJson());
//                bundle.putLong("language_id", item.getTemplateLanguageId());
//                intent.putExtras(bundle);
//                if (getTargetFragment() != null)
//                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_SUCCESS_SELECT_TEMPLATE, intent);
                EventBus.getDefault().post(new TemplateSelectedEvent(item.getTemplate_language_id(), item.getTemplate_json()));
                dismiss();
            }

            @Override
            public void showPreview(TemplateItem.Data.TemplateList item, int position) {
                TemplatePreviewFragment dialogP = new TemplatePreviewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("image_url", item.getTemplate_image_url());
                dialogP.setArguments(bundle);
                dialogP.show(getChildFragmentManager(), TemplatePreviewFragment.TAG);
            }
        });
        binding.rvTemplates.setAdapter(adapter);

        checkForEmptyState();

        if (ConnectivityReceiver.isConnected()) {
            isOnlineLoaded = true;
            getTemplates(pPosition);
        } else {
            isOnlineLoaded = false;
            getTemplatesFromDB();
        }

        binding.srlTemplates.setOnRefreshListener(() -> {
            if (ConnectivityReceiver.isConnected()) {
                isOnlineLoaded = true;
                currentPage = PAGE_START;
                getTemplates(pPosition);
            } else {
                isOnlineLoaded = false;
                getTemplatesFromDB();
            }
        });

        binding.rvTemplates.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (isOnlineLoaded && !isLastPage && !isNextPageCalled) {
//                        if (detector.isConnectingToInternet())
                        loadMoreItems();
//                        else
//                            Toast.makeText(ProductList.this, "Looks like you're not connected with internet!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        binding.ivClose.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    private void getTemplatesFromDB() {
        templateList.clear();

//        templateList.addAll(dbHandler.getTemplates());
        Log.e(TAG, "getTemplatesFromDB: " + templateList.size());

        adapter.notifyDataSetChanged();
        Log.e(TAG, "getTemplatesFromDB: " + adapter.getItemCount());

        checkForEmptyState();
    }

    private void loadMoreItems() {
        int p = ViewExtension.addLoadingAnimation(templateList, adapter);
        currentPage++;
//        progressLoading.setVisibility(View.VISIBLE);
        Log.e(TAG, "loadMoreItems: " + currentPage);
        getTemplates(p);
    }

    private void getTemplates(int pPosition) {
        binding.srlTemplates.setRefreshing(currentPage == PAGE_START);

        isNextPageCalled = true;

        AndroidNetworking
                .post(BASE_URL + TEMPLATE_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(currentPage))
                .addBodyParameter("language_id", "1")
                .setTag("template-list-api")
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
                                JSONArray templates = data.getJSONArray("template_list");
                                int totalPage = data.getInt("total_page");
                                currentPage = data.getInt("current_page");

                                if (templates.length() > 0) {

                                    if (currentPage == PAGE_START)
                                        templateList.clear();

                                    for (int i = 0; i < templates.length(); i++) {
                                        JSONObject template = templates.getJSONObject(i);
                                        TemplateItem.Data.TemplateList templateItem = new Gson().fromJson(template.toString(), TemplateItem.Data.TemplateList.class);

//                                        TemplateItem templateItem = new TemplateItem();
//                                        templateItem.setTemplateId(template.getInt("template_id"));
//                                        templateItem.setTemplateUniqueId(template.getString("template_unique_id"));
//                                        templateItem.setTemplateName(template.getString("template_name"));
//                                        templateItem.setTemplateType(template.getInt("template_type"));
//                                        templateItem.setTemplateLanguageId(template.getLong("template_language_id"));
//
//                                        Log.e(TAG, "onResponse: template_json -> " + new JSONArray(template.getString("template_json")));
//                                        templateItem.setTemplateJson(new JSONArray(template.getString("template_json")).toString());
//
//                                        templateItem.setTemplateImage(template.getString("template_image_url"));

//                                        templateItem.setCreatedAt(template.getString("created_at"));
//                                        templateItem.setDelete(template.getInt("is_delete") == 1);

//                                        if (!template.isNull("updated_at"))
//                                            templateItem.setUpdatedAt(template.getString("updated_at"));


//                                        templateItem.setTemplateStatus(template.getInt("template_status") == 1);

//                                        if (!template.isNull("template_image"))
//                                            templateItem.setTemplateImage(template.getString("template_image"));

                                        templateList.add(templateItem);

//                                        dbHandler.addTemplate(templateItem);
                                    }

                                    if (currentPage == PAGE_START)
                                        adapter.notifyDataSetChanged();
                                    else
                                        adapter.notifyItemRangeInserted(pPosition + 1, data.length());

                                    if (pPosition > 1 && pPosition <= templateList.size() - 1) {
                                        templateList.remove(pPosition);
                                        adapter.notifyItemRemoved(pPosition);
                                        adapter.notifyItemChanged(pPosition - 1);
                                    }

                                    checkForEmptyState();

                                    isLastPage = totalPage == currentPage;
                                }
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;

                        if (binding.srlTemplates.isRefreshing())
                            binding.srlTemplates.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;

                        if (binding.srlTemplates.isRefreshing())
                            binding.srlTemplates.setRefreshing(false);

                        getTemplatesFromDB();
                        isOnlineLoaded = false;
                    }
                });

//        templateList.clear();
//        templateList.add(new FormItem(1, "E-Commerce Form", true, 200, false));
//        templateList.add(new FormItem(2, "Survey Form", false, 100, false));
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
    }

    private void checkForEmptyState() {
        if (templateList.size() > 0) {
            binding.clData.setVisibility(View.VISIBLE);
            binding.clEmptyState.setVisibility(View.GONE);
        } else {
            binding.clData.setVisibility(View.GONE);
            binding.clEmptyState.setVisibility(View.VISIBLE);
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
