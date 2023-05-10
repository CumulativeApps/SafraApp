package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.DASHBOARD_API;
import static com.safra.utilities.DetailsManager.detailsManager;
import static com.safra.utilities.UserPermissions.FORM_LIST;
import static com.safra.utilities.UserPermissions.GROUP_LIST;
import static com.safra.utilities.UserPermissions.TASK_LIST;
import static com.safra.utilities.UserPermissions.USER_LIST;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.safra.Dashboard;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.LanguageRecyclerAdapter;
import com.safra.databinding.FragmentDashboardBinding;
import com.safra.dialogs.PackageDetailDialog;
import com.safra.events.ConnectivityChangedEvent;
import com.safra.events.LanguageChangedEvent;
import com.safra.events.LanguagesReceivedEvent;
import com.safra.extensions.Extension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.models.LanguageItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.LanguageManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    public static final String TAG = "dashboard_fragment";

    private FragmentDashboardBinding binding;

    private FragmentActivity mActivity = null;

    private final List<LanguageItem> languageList = new ArrayList<>();

    private String pName, uAllowed, pExpiry, desc, ptDesc, t, ptT, contactEmail, contactPhone;

    private boolean isRemembered;

    private LanguageManager languageManager;
    private long selectedLanguage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();
        languageManager = new LanguageManager(mActivity);
        selectedLanguage = languageManager.getLanguage();
        boolean isAgency = isRemembered ? userSessionManager.isAgency() : Safra.isAgency;

        setText();

        if (isAgency) {
            binding.tlStatistics.setVisibility(View.VISIBLE);
            binding.mcvAbout.setVisibility(View.GONE);
            binding.mcvPackage.setVisibility(View.VISIBLE);
        } else {
            binding.tlStatistics.setVisibility(View.GONE);
            binding.mcvAbout.setVisibility(View.VISIBLE);
            binding.mcvPackage.setVisibility(View.INVISIBLE);
        }

        languageList.clear();
        languageList.addAll(dbHandler.getLanguages());

        for (LanguageItem li : languageList) {
            Log.e(TAG, "onCreateView: selectedLanguage -> " + selectedLanguage);
            if (li.getLanguageId() == selectedLanguage) {
                binding.tvLanguage.setText(li.getLanguageName());
                li.setSelected(true);
            }
        }

        showHideStatistics();

        if (ConnectivityReceiver.isConnected())
            getDashboardData();
        else
            setOfflineData();

        binding.tvViewDetails.setOnClickListener(v -> {
            PackageDetailDialog dialogD = new PackageDetailDialog();
            Bundle bundle = new Bundle();
            bundle.putString("plan_name", pName);
            bundle.putString("user_allowed", uAllowed);
            bundle.putString("plan_expiry", pExpiry);
            bundle.putString("description", languageManager.getLanguage() == 2 ? (ptDesc != null ? ptDesc : "") : (desc != null ? desc : ""));
            bundle.putString("terms", languageManager.getLanguage() == 2 ? (ptT != null ? ptT : "") : (t != null ? t : ""));
            bundle.putString("contact_email", contactEmail);
            bundle.putString("contact_phone", contactPhone);
            dialogD.setArguments(bundle);
            dialogD.show(mActivity.getSupportFragmentManager(), PackageDetailDialog.TAG);
        });

        binding.mcvTotalGroup.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new UserGroupsFragment(), UserGroupsFragment.TAG));

        binding.mcvTotalUser.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new UsersFragment(), UsersFragment.TAG));

        binding.mcvTotalTask.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new TasksFragment(), TasksFragment.TAG));

        binding.mcvTotalForm.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new FormsFragment(), FormsFragment.TAG));

        binding.clLanguage.setOnClickListener(v -> openLanguageSelection(mActivity, languageList));

        return binding.getRoot();
    }

    private void setText() {
        binding.tvLanguageTitle.setText(LanguageExtension.setText("language_colon", getString(R.string.language_colon)));

        binding.tvTotalGroupTitle.setText(LanguageExtension.setText("total_groups", getString(R.string.total_groups)));
        binding.tvTotalUserTitle.setText(LanguageExtension.setText("total_users", getString(R.string.total_users)));
        binding.tvTotalTaskTitle.setText(LanguageExtension.setText("total_tasks", getString(R.string.total_tasks)));
        binding.tvTotalFormTitle.setText(LanguageExtension.setText("total_forms", getString(R.string.total_forms)));

        binding.tvAboutTitle.setText(LanguageExtension.setText("about_company", getString(R.string.about_company)));
        binding.tvEmailTitle.setText(LanguageExtension.setText("e_mail", getString(R.string.e_mail)));
        binding.tvContactTitle.setText(LanguageExtension.setText("contact", getString(R.string.contact)));

        binding.tvMembershipTitle.setText(LanguageExtension.setText("membership_plan", getString(R.string.membership_plan)));
        binding.tvLimitTitle.setText(LanguageExtension.setText("users_colon", getString(R.string.users_colon)));
        binding.tvViewDetails.setText(LanguageExtension.setText("view_details", getString(R.string.view_details)));
    }

    private void setOfflineData() {
        Bundle b = dbHandler.getDashboardStatistics(isRemembered ? userSessionManager.getUserId() : Safra.userId);
        binding.tvTotalForm.setText(String.valueOf(b.getInt("form_count", 0)));
        binding.tvTotalTask.setText(String.valueOf(b.getInt("task_count", 0)));
        binding.tvTotalUser.setText(String.valueOf(b.getInt("user_count", 0)));
        binding.tvTotalGroup.setText(String.valueOf(b.getInt("group_count", 0)));

        binding.tvCompanyName.setText(detailsManager.getCompanyName());
        binding.tvEmail.setText(detailsManager.getCompanyEmail());
        binding.tvContact.setText(detailsManager.getCompanyPhone());
        if (mActivity != null)
            Glide.with(mActivity).load(detailsManager.getCompanyImage()).into(binding.ivCompanyIcon);

        pName = detailsManager.getPlanName();
        uAllowed = detailsManager.getUserAllowed();
        pExpiry = detailsManager.getPlanExpiry();
        desc = detailsManager.getDescription();
        ptDesc = detailsManager.getPtDescription();
        t = detailsManager.getTerms();
        ptT = detailsManager.getPtTerms();
        contactEmail = detailsManager.getContactEmail();
        contactPhone = detailsManager.getContactPhone();

        binding.tvPlanName.setText(pName);
        binding.tvLimit.setText(uAllowed);
    }

    private void showHideStatistics() {
        if (PermissionExtension.checkForPermission(GROUP_LIST))
            binding.mcvTotalGroup.setVisibility(View.VISIBLE);
        else
            binding.mcvTotalGroup.setVisibility(View.GONE);

        if (PermissionExtension.checkForPermission(USER_LIST))
            binding.mcvTotalUser.setVisibility(View.VISIBLE);
        else
            binding.mcvTotalUser.setVisibility(View.GONE);

        if (PermissionExtension.checkForPermission(TASK_LIST))
            binding.mcvTotalTask.setVisibility(View.VISIBLE);
        else
            binding.mcvTotalTask.setVisibility(View.GONE);

        if (PermissionExtension.checkForPermission(FORM_LIST))
            binding.mcvTotalForm.setVisibility(View.VISIBLE);
        else
            binding.mcvTotalForm.setVisibility(View.GONE);
    }

    private void getDashboardData() {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        AndroidNetworking
                .post(BASE_URL + DASHBOARD_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .setTag("dashboard-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoadingDialogExtension.hideLoading();
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                String tGroup = data.getString("total_groups");
                                String tUser = data.getString("total_users");
                                String tTask = data.getString("total_tasks");
                                String tForm = data.getString("total_forms");

                                binding.tvTotalGroup.setText(tGroup);
                                binding.tvTotalUser.setText(tUser);
                                binding.tvTotalTask.setText(tTask);
                                binding.tvTotalForm.setText(tForm);

                                JSONObject company = data.getJSONObject("company");
                                String cName = company.getString("company_name");
                                String cEmail = company.getString("company_email");
                                String cPhone = company.getString("company_phone_no");
                                String cImage = company.getString("company_image");
                                detailsManager.updateCompanyDetails(cName, cEmail, cPhone, cImage);

                                binding.tvCompanyName.setText(cName);
                                binding.tvEmail.setText(cEmail);
                                binding.tvContact.setText(cPhone);
                                if (mActivity != null)
                                    Glide.with(mActivity).load(cImage).into(binding.ivCompanyIcon);

                                JSONObject packageData = data.getJSONObject("package");
                                pName = packageData.getString("plan_name");
                                uAllowed = packageData.getString("users_allowed");
                                pExpiry = packageData.getString("plan_expiry");
                                if (packageData.has("description") && !packageData.isNull("description"))
                                    desc = packageData.getString("description");
                                if (packageData.has("pt_description") && !packageData.isNull("pt_description"))
                                    ptDesc = packageData.getString("pt_description");

                                if (packageData.has("terms") && !packageData.isNull("terms"))
                                    t = packageData.getString("terms");
                                if (packageData.has("pt_terms") && !packageData.isNull("pt_terms"))
                                    ptT = packageData.getString("pt_terms");
                                contactEmail = packageData.getString("contact_email");
                                contactPhone = packageData.getString("contact_phone_no");
                                detailsManager.updatePlanDetails(pName, uAllowed, pExpiry, desc, t,
                                        contactEmail, contactPhone, ptDesc, ptT);

                                binding.tvPlanName.setText(pName);
                                binding.tvLimit.setText(uAllowed);

                                long lang = data.getLong("language_id");
                                languageManager.changeLanguage(lang);
                                selectedLanguage = languageManager.getLanguage();
                                for (LanguageItem li : languageList) {
                                    li.setSelected(false);
                                    if (li.getLanguageId() == selectedLanguage) {
                                        binding.tvLanguage.setText(li.getLanguageName());
                                        li.setSelected(true);
                                    }
                                }

                            } else if (success == 2) {
                                Extension.signOutUser(mActivity);
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
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

    private void openLanguageSelection(Context context, List<LanguageItem> options) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null, false);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);

        final RecyclerView optionRecycler = view.findViewById(R.id.rvOptions);
        optionRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        optionRecycler.addItemDecoration(new LineHorizontalItemDecoration(this, R.dimen.recycler_bottom_offset, false));
        final AlertDialog alertDialog = alertDialogBuilder.create();

        ImageView close = view.findViewById(R.id.ivClose);

        alertDialog.show();
        alertDialog.getWindow().setLayout(context.getResources().getDimensionPixelSize(R.dimen.dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT);

        close.setOnClickListener(v -> alertDialog.dismiss());

        LanguageRecyclerAdapter adapter = new LanguageRecyclerAdapter(context, options, (item, position) -> {
            alertDialog.dismiss();
            if (ConnectivityReceiver.isConnected())
                LanguageExtension.changeLanguage(TAG, item.getLanguageId());
            binding.tvLanguage.setText(item.getLanguageName());
            languageManager.changeLanguage(item.getLanguageId());
        });
        optionRecycler.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguagesReceived(LanguagesReceivedEvent event) {
        Log.e(TAG, "onLanguagesReceived: " + event.getLanguages().size());
        languageList.clear();
        languageList.addAll(event.getLanguages());

        if (languageList.size() > 0) {
            for (LanguageItem li : languageList) {
                if (li.getLanguageId() == selectedLanguage)
                    li.setSelected(true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChanged(LanguageChangedEvent event) {
        setText();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectivityReceived(ConnectivityChangedEvent event) {
        if (event.isConnected()) {
            getDashboardData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }
}
