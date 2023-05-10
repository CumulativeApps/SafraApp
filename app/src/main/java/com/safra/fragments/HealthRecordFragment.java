package com.safra.fragments;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.UserSessionManager.userSessionManager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safra.Dashboard;
import com.safra.R;
import com.safra.databinding.FragmentHealthRecordBinding;
import com.safra.databinding.FragmentPlannerBinding;
import com.safra.events.ConnectivityChangedEvent;
import com.safra.events.LanguageChangedEvent;
import com.safra.extensions.LanguageExtension;
import com.safra.models.LanguageItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class HealthRecordFragment extends Fragment {

    public static final String TAG = "health_record_fragment";

    private FragmentHealthRecordBinding binding;
    private boolean isRemembered;

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
//        return inflater.inflate(R.layout.fragment_health_record, container, false);
        binding = FragmentHealthRecordBinding.inflate(inflater, container, false);
        isRemembered = userSessionManager.isRemembered();


        setText();


        languageList.clear();
        languageList.addAll(dbHandler.getLanguages());






        binding.mcvTotalPatient.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new PatientFragment(), PatientFragment.TAG));

        binding.mcvTotalActiveVisits.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new ActiveVisitsFragment(), ActiveVisitsFragment.TAG));

        binding.mcvTotalAppointmentSchedules.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new AppointmentScheduleFragment(), AppointmentScheduleFragment.TAG));

        binding.mcvTotalRegPatient.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new AllergiesFragment(), AllergiesFragment.TAG));

        binding.mcvTotalCaptureVitals.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new CaptureVitalsFragment(), CaptureVitalsFragment.TAG));

        binding.mcvTotalReports.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new MedicalReportsFragment(), MedicalReportsFragment.TAG));

        binding.mcvTotalMedicine.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new MedicineFragment(), MedicineFragment.TAG));

        binding.mcvTotalDiagnostics.setOnClickListener(v ->
                ((Dashboard) mActivity).changeHealthFragment(new DiagnosticsFragment(), DiagnosticsFragment.TAG));
        return binding.getRoot();

    }

    private void setText() {


        binding.tvTotalPatients.setText(LanguageExtension.setText("total_patient", getString(R.string.total_patient)));
        binding.tvTotalActiveVisits.setText(LanguageExtension.setText("active_visits", getString(R.string.active_visits)));
        binding.tvTotalAppointmentSchedules.setText(LanguageExtension.setText("appointment", getString(R.string.appointment)));
        binding.tvTotalRegPatients.setText(LanguageExtension.setText("allergies", getString(R.string.allergies)));
        binding.tvTotalCaptureVitals.setText(LanguageExtension.setText("capture_vitals", getString(R.string.capture_vitals)));
        binding.tvTotalReports.setText(LanguageExtension.setText("reports", getString(R.string.reports)));
        binding.tvTotalMedicines.setText(LanguageExtension.setText("medicine", getString(R.string.medicine)));
        binding.tvTotalDiagnostics.setText(LanguageExtension.setText("diagnostics", getString(R.string.diagnostics)));


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
}