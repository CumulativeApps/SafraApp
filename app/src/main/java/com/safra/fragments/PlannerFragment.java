package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.Common.PROJECT;
import static com.safra.utilities.UserPermissions.USER_DELETE;
import static com.safra.utilities.UserPermissions.USER_STATUS;
import static com.safra.utilities.UserPermissions.USER_UPDATE;
import static com.safra.utilities.UserPermissions.USER_VIEW;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.Dashboard;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.ProjectRecyclerAdapter;
import com.safra.databinding.FragmentPlannerBinding;
import com.safra.events.ConnectivityChangedEvent;
import com.safra.events.LanguageChangedEvent;
import com.safra.events.LanguagesReceivedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.models.LanguageItem;
import com.safra.models.ProjectListResponseModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PlannerFragment extends Fragment {

    public static final String TAG = "planner_fragment";
    private FragmentPlannerBinding binding;
    private boolean isRemembered;
    private final List<ProjectListResponseModel> userList = new ArrayList<>();
    private ProjectRecyclerAdapter adapter;

    private String searchText = "";
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private final int pPosition = -1;
    private boolean isNextPageCalled = false;


    private boolean isLoadedOnline = false;

    private PopupWindow popupWindow;
    private FragmentActivity mActivity = null;
    private final List<LanguageItem> languageList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_planner, container, false);
        binding = FragmentPlannerBinding.inflate(inflater, container, false);





        isRemembered = userSessionManager.isRemembered();


        setText();


        languageList.clear();
        languageList.addAll(dbHandler.getLanguages());






        binding.mcvTotalProject.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new ProjectsFragment(), ProjectsFragment.TAG));

        binding.mcvTotalActionPlan.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new ActionPlanFragment(), ActionPlanFragment.TAG));

        binding.mcvTotalSchedules.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new SchedulesFragment(), SchedulesFragment.TAG));

        binding.mcvTotalBudget.setOnClickListener(v ->
                ((Dashboard) mActivity).changeFragment(new BudgetFragment(), BudgetFragment.TAG));


        getProjects(pPosition);
        return binding.getRoot();

    }

    private void setText() {


        binding.tvTotalProjects.setText(LanguageExtension.setText("total_project", getString(R.string.total_project)));
        binding.tvTotalActionPlan.setText(LanguageExtension.setText("total_action_plan", getString(R.string.total_action_plan)));
        binding.tvTotalSchedules.setText(LanguageExtension.setText("total_schedules", getString(R.string.total_schedules)));
        binding.tvTotalBudgets.setText(LanguageExtension.setText("total_budget", getString(R.string.total_budget)));


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChanged(LanguageChangedEvent event) {
        setText();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectivityReceived(ConnectivityChangedEvent event) {
//        if (event.isConnected()) {
//            getDashboardData();
//        }
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
    private void getProjects(int pPosition) {
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + PROJECT)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("API CALL123" +response);
                        try {
                            int success = response.getInt("success");

                            String message = response.getString("message");

                            if (success == 1) {
//
                                JSONArray users = response.getJSONArray("data");
                                System.out.println("users"+ users.length());

//                                binding.tvTotalProject.setText(String.valueOf(users.length()));

                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse Error: " + e.getLocalizedMessage());
                        }

                        isNextPageCalled = false;


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorCode());
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                        Log.e(TAG, "onError: " + anError.getErrorBody());

                        isNextPageCalled = false;


                    }
                });

    }

}



