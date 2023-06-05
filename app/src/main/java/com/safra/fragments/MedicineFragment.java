package com.safra.fragments;

import static com.safra.utilities.Common.PAGE_START;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.safra.databinding.FragmentMedicineBinding;
import com.safra.events.TaskAddedEvent;
import com.safra.utilities.ConnectivityReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MedicineFragment extends Fragment {

    public static final String TAG = "medicine_fragment";
    private FragmentActivity mActivity = null;

    private FragmentMedicineBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMedicineBinding.inflate(inflater, container, false);

        binding.btnAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicineListFragment dialogD = new MedicineListFragment();
                Bundle bundle = new Bundle();

//                bundle.putLong("online_id", item.getUserOnlineId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), MedicineListFragment.TAG);
            }
        });

        binding.btnAddProvides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProvidersListFragment dialogD = new ProvidersListFragment();
                Bundle bundle = new Bundle();

//                bundle.putLong("online_id", item.getUserOnlineId());
                dialogD.setArguments(bundle);
                dialogD.show(mActivity.getSupportFragmentManager(), ProvidersListFragment.TAG);
            }
        });

        return binding.getRoot();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAdded(TaskAddedEvent event) {
        if (ConnectivityReceiver.isConnected()) {
//            isLoadedOnline = true;
//            currentPage = PAGE_START;
//            getPatients(pPosition);
//        } else {
//            isLoadedOnline = false;
//            getUsersFromDB();
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