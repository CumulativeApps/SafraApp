package com.safra;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.FORM_SAVE_API;
import static com.safra.utilities.Common.FORM_VIEW_API;
import static com.safra.utilities.UserPermissions.TEMPLATE_USE;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationBarView;
import com.safra.adapters.FormViewPagerAdapter;
import com.safra.databinding.ActivityCreateFormBinding;
import com.safra.databinding.PopupFormEditBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.events.FieldListChangedEvent;
import com.safra.events.FormAddedEvent;
import com.safra.events.TemplateSelectedEvent;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.fragments.FormEditFragment;
import com.safra.fragments.FormPreviewFragment;
import com.safra.fragments.FormSettingsFragment;
import com.safra.fragments.TemplatesFragment;
import com.safra.models.FormItem;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CreateForm extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener {

    public static final String TAG = "create_form_activity";

    private ActivityCreateFormBinding binding;

    private FormEditFragment formEditFragment;
    private FormSettingsFragment formSettingsFragment;
//    private FormShareFragment formShareFragment;

    private PopupWindow popupWindow;

    private boolean isRemembered;

    private boolean isNew, useTemplate;
    private long formId, onlineId;
    public long formLanguageId;
    public static FormItem formItem = null;

    public static int totalMarks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.ivBack.setOnClickListener(v -> finish());

        isRemembered = userSessionManager.isRemembered();

        binding.bottomNavView.setOnItemSelectedListener(this);

        binding.formViewPager.setUserInputEnabled(false);
        binding.bottomNavView.getMenu().findItem(R.id.menu_settings).setTitle(LanguageExtension.setText("settings", getString(R.string.settings)));
        binding.bottomNavView.getMenu().findItem(R.id.menu_edit).setTitle(LanguageExtension.setText("fields", getString(R.string.fields)));

        if (getIntent() != null) {
            binding.tvCreateFormHeading.setText(getIntent().getStringExtra("heading"));
            isNew = getIntent().getBooleanExtra("is_new", false);
            if (!isNew) {
                formId = getIntent().getLongExtra("form_id", -1);
                onlineId = getIntent().getLongExtra("online_id", -1);
            } else {
                useTemplate = getIntent().getBooleanExtra("use_template", false);
            }
        }
        setAdapter();

        binding.formViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                switch (position) {
                    case 0:
                        binding.bottomNavView.getMenu().findItem(R.id.menu_settings).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNavView.getMenu().findItem(R.id.menu_edit).setChecked(true);
                        break;
//                    case 2:
//                        bottomNavigationView.getMenu().findItem(R.id.menu_share).setChecked(true);
//                        break;
                }
            }
        });

        binding.ivMoreOption.setOnClickListener(this::setPopUpWindow);
    }

    private void setAdapter() {
        Bundle bundleS = new Bundle();
        bundleS.putBoolean("is_new", isNew);

        Bundle bundleE = new Bundle();
        bundleE.putBoolean("is_new", isNew);
//        if(isNew)
//            bundleE.putBoolean("use_template", useTemplate);

        formEditFragment = new FormEditFragment();
        formEditFragment.setArguments(bundleE);
        formSettingsFragment = new FormSettingsFragment();
        formSettingsFragment.setArguments(bundleS);
//        formShareFragment = new FormShareFragment();

        FormViewPagerAdapter adapter = new FormViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.addFragment(formSettingsFragment, FormSettingsFragment.TAG);
        adapter.addFragment(formEditFragment, FormEditFragment.TAG);
//        adapter.addFragment(formShareFragment, FormShareFragment.TAG);
        binding.formViewPager.setAdapter(adapter);
        binding.formViewPager.setOffscreenPageLimit(1);

        if (!isNew) {
            Log.e(TAG, "setAdapter: " + ConnectivityReceiver.isConnected());
            if (ConnectivityReceiver.isConnected()) {
                getEditData();
            } else {
                formItem = dbHandler.getFormDetails(formId);
            }
        } else {
            if (useTemplate && PermissionExtension.checkForPermission(TEMPLATE_USE))
                showTemplatesDialog();
        }
    }

    private void showTemplatesDialog() {
        TemplatesFragment dialogT = new TemplatesFragment();
        dialogT.setCancelable(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialogT.show(ft, TemplatesFragment.TAG);
    }

    private void setPopUpWindow(View parentView) {
        PopupFormEditBinding popupBinding = PopupFormEditBinding.inflate(getLayoutInflater());

        popupBinding.tvSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
        popupBinding.tvPublish.setText(LanguageExtension.setText("publish", getString(R.string.publish)));
        popupBinding.tvPreview.setText(LanguageExtension.setText("preview", getString(R.string.preview)));
//        TextView result = view.findViewById(R.id.tvResult);

//        if (!ConnectivityReceiver.isConnected())
//            publish.setVisibility(View.GONE);

        popupBinding.tvSave.setOnClickListener(v -> {
            validateFormData(0);
            popupWindow.dismiss();
        });
        popupBinding.tvPublish.setOnClickListener(v -> {
            validateFormData(1);
            popupWindow.dismiss();
        });
        popupBinding.tvPreview.setOnClickListener(v -> {
            showPreviewOfForm();
            popupWindow.dismiss();
        });
//        result.setOnClickListener(v -> {
//            popupWindow.dismiss();
//        });

        popupWindow = new PopupWindow(popupBinding.getRoot(), 400, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        // Removes default background.
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setElevation(10f);

        popupWindow.showAsDropDown(parentView, getResources().getDimensionPixelOffset(R.dimen._0dp), getResources().getDimensionPixelOffset(R.dimen._0dp), Gravity.TOP | Gravity.END);
    }

    private void validateFormData(int status) {
        if (!formEditFragment.validateInputs() || !formSettingsFragment.validateInputs()) {
            if (!formEditFragment.validateInputs()) {
                binding.formViewPager.setCurrentItem(1, false);
                binding.bottomNavView.getMenu().findItem(R.id.menu_edit).setChecked(true);
            }
            if (!formSettingsFragment.validateInputs()) {
                binding.formViewPager.setCurrentItem(0, false);
                binding.bottomNavView.getMenu().findItem(R.id.menu_settings).setChecked(true);
            }
        } else {
            HashMap<String, String> formEditHashMap = formEditFragment.getFieldValues();
            HashMap<String, String> formSettingsHashMap = formSettingsFragment.getFieldValues();

            if (ConnectivityReceiver.isConnected()) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken);
                hashMap.put("form_name", formSettingsHashMap.get("form_title"));
                if (!formSettingsHashMap.get("form_description").isEmpty())
                    hashMap.put("form_description", formSettingsHashMap.get("form_description"));
                hashMap.put("form_json", formEditHashMap.get("form_fields"));
                hashMap.put("form_access", formSettingsHashMap.get("form_visibility_id"));
                hashMap.put("form_type", formSettingsHashMap.get("form_type_id"));
                hashMap.put("form_language_id", formSettingsHashMap.get("language_id"));
                hashMap.put("form_status", String.valueOf(status));

                hashMap.put("form_reference", formSettingsHashMap.get("form_reference"));
                hashMap.put("form_map", formSettingsHashMap.get("form_map"));
                hashMap.put("form_email_response",formSettingsHashMap.get("form_response"));

                if (formSettingsHashMap.containsKey("expiry_date"))
                    hashMap.put("form_expiry_date", formSettingsHashMap.get("expiry_date"));
                if (!formSettingsHashMap.get("form_user_ids").isEmpty())
                    hashMap.put("form_user_ids", formSettingsHashMap.get("form_user_ids"));
                if (!formSettingsHashMap.get("form_group_ids").isEmpty())
                    hashMap.put("form_group_ids", formSettingsHashMap.get("form_group_ids"));
                if (formSettingsHashMap.containsKey("mcq_marks"))
                    hashMap.put("form_mcq_marks", formSettingsHashMap.get("mcq_marks"));

                if (isNew)
                    saveForm(hashMap);
                else {
                    hashMap.put("form_id", String.valueOf(formItem.getFormOnlineId()));
                    editForm(hashMap);
                }
            } else {
                Log.e(TAG, "validateFormData: " + formEditHashMap.get("form_fields"));
                if (formItem == null)
                    formItem = new FormItem();

                formItem.setFormJson(formEditHashMap.get("form_fields"));
                formItem.setFormName(formSettingsHashMap.get("form_title"));
                formItem.setFormType(Long.parseLong(formSettingsHashMap.get("form_type_id")));
                formItem.setFormAccess(Long.parseLong(formSettingsHashMap.get("form_visibility_id")));
                formItem.setFormStatus(status);
                formItem.setFormLanguageId(Long.parseLong(formSettingsHashMap.get("language_id")));
                formItem.setFormExpiryDate(formSettingsHashMap.get("expiry_date"));
                formItem.setFormDescription(formSettingsHashMap.get("form_description"));
                formItem.setFormUserId(isRemembered ? userSessionManager.getUserId() : Safra.userId);
                formItem.setTotalMarks(formSettingsHashMap.containsKey("mcq_marks") ? Integer.parseInt(formSettingsHashMap.get("mcq_marks")) : 0);
                if (!formSettingsHashMap.get("form_user_ids").isEmpty())
                    formItem.setUserIds(GeneralExtension.toLongArray(formSettingsHashMap.get("form_user_ids"), ","));
                if (!formSettingsHashMap.get("form_group_ids").isEmpty())
                    formItem.setGroupIds(GeneralExtension.toLongArray(formSettingsHashMap.get("form_group_ids"), ","));

                if (isNew) {
                    long i = dbHandler.addFormOffline(formItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new FormAddedEvent());
//                        setResult(RESULT_SUCCESS_ADD_FORM);
                        finish();
                    }
                } else {
                    formItem.setFormId(formId);
                    formItem.setFormOnlineId(onlineId);

                    long i = dbHandler.updateFormOffline(formItem);
                    if (i > 0) {
                        EventBus.getDefault().post(new FormAddedEvent());
//                        setResult(RESULT_SUCCESS_ADD_FORM);
                        finish();
                    }
                }
            }
        }
    }

    private void getEditData() {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + FORM_VIEW_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("form_id", String.valueOf(onlineId))
                .setTag("form-detail-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response);
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject formData = response.getJSONObject("data").getJSONObject("form_data");
                                formItem = new FormItem();
                                formItem.setFormOnlineId(formData.getLong("form_id"));
                                formItem.setFormUniqueId(formData.getString("form_unique_id"));
                                formItem.setFormName(formData.getString("form_name"));
                                formItem.setFormLanguageId(formData.getLong("form_language_id"));

                                if (!formData.isNull("form_description"))
                                    formItem.setFormDescription(formData.getString("form_description"));

                                formItem.setFormJson(formData.getString("form_json")); // Removed unnecessary conversion to JSONArray


                                formItem.setFormExpiryDate(formData.getString("form_expiry_date"));

                                formItem.setFormType(formData.getLong("form_type"));
                                formItem.setFormAccess(formData.getLong("form_access"));

                                if (formData.has("form_mcq_marks") && !formData.isNull("form_mcq_marks"))
                                    formItem.setTotalMarks(formData.getInt("form_mcq_marks"));

                                formItem.setFormUserId(formData.getLong("form_master_id"));

                                formItem.setFormStatus(formData.getInt("form_status"));

                                if (formData.has("form_user_ids") && !formData.isNull("form_user_ids"))
                                    formItem.setUserIds(GeneralExtension.toLongArray(formData.getString("form_user_ids"), ","));

                                if (formData.has("form_group_ids") && !formData.isNull("form_group_ids"))
                                    formItem.setGroupIds(GeneralExtension.toLongArray(formData.getString("form_group_ids"), ","));

                                if (formData.has("form_group_user_ids") && !formData.isNull("form_group_user_ids"))
                                    formItem.setGroupUserIds(GeneralExtension.toLongArray(formData.getString("form_group_user_ids"), ","));

                                formItem.setDelete(formData.getInt("is_delete") == 1);

                                setFormDetails();

                            } else {
                                Toast.makeText(CreateForm.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                        }

//                        dialogL.dismiss();
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

    private void setFormDetails() {
        Log.e(TAG, "setFormDetails: ");
        formEditFragment.setFormFields(formItem.getFormJson());

        formSettingsFragment.setFormSettings(formItem);
    }

    private void showPreviewOfForm() {
        HashMap<String, String> formEditHashMap = formEditFragment.getFieldValues();
        HashMap<String, String> formSettingsHashMap = formSettingsFragment.getFieldValues();

        FormPreviewFragment previewDialog = new FormPreviewFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        b.putString("form_fields", formEditHashMap.get("form_fields"));
        b.putString("form_title", formSettingsHashMap.get("form_title"));
//                dialogS.setTargetFragment(FormEditFragment.this, REQUEST_SELECT_FORM_ELEMENT);
        previewDialog.setArguments(b);
        previewDialog.show(ft, FormPreviewFragment.TAG);
    }

    private void saveForm(HashMap<String, String> parameters) {
        System.out.println("parameters:-" +parameters);
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("saving_form_progress", getString(R.string.saving_form_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("saving_form_progress", getString(R.string.saving_form_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + FORM_SAVE_API)
                .addBodyParameter(parameters)
                .setTag("save-form-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(CreateForm.this, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                EventBus.getDefault().post(new FormAddedEvent());
//                                setResult(RESULT_SUCCESS_ADD_FORM);
                                finish();
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

    private void editForm(HashMap<String, String> parameters) {
        LoadingDialogExtension.showLoading(this, LanguageExtension.setText("updating_form_progress", getString(R.string.updating_form_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("updating_form_progress", getString(R.string.updating_form_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getSupportFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + FORM_SAVE_API)
                .addBodyParameter(parameters)
                .setTag("save-form-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            Toast.makeText(CreateForm.this, message, Toast.LENGTH_SHORT).show();
//                            dialogL.dismiss();
                            if (success == 1) {
                                EventBus.getDefault().post(new FormAddedEvent());
//                                setResult(RESULT_SUCCESS_ADD_FORM);
                                finish();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTemplateSelected(TemplateSelectedEvent event) {
        if (formItem == null)
            formItem = new FormItem();
        formItem.setFormJson(event.getFormFields());
        formItem.setFormLanguageId(event.getLanguageId());
        setFormDetails();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Log.e(TAG, "onActivityResult: receiving results");
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            binding.formViewPager.setCurrentItem(0, false);
            return true;
        } else if (item.getItemId() == R.id.menu_edit) {
            binding.formViewPager.setCurrentItem(1, false);
            return true;
        }
//        } else if(item.getItemId() == R.id.menu_share){
//            bottomNavigationViewPager.setCurrentItem(2, false);
//            return true;
        return false;
    }

    @Override
    public void onBackPressed() {
//        if (popupWindow.isShowing()) {
//            popupWindow.dismiss();
//            return;
//        }
        super.onBackPressed();
    }

    private boolean loadFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, fragment, tag)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        formItem = null;
    }
}