package com.safra.fragments;

import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.HEALTH_RECORD_APPOINTMENT_LIST;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.UserSessionManager.userSessionManager;

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
import com.safra.adapters.NoteAdapter;
import com.safra.databinding.FragmentAppointmentDetailBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.AppointmentListModel;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AppointmentDetailFragment extends DialogFragment {
    public static final String TAG = "appointment_detail";

    private FragmentActivity mActivity = null;

    private FragmentAppointmentDetailBinding binding;
    private final int pPosition = -1;


    private boolean isRemembered;

    private long patientId, onlineId;
    private final List<AppointmentListModel.Data.Patient.Appointment.Note> userList = new ArrayList<>();
    private NoteAdapter adapter;

    private int getStatus;
    private String getDate, getTime, getNote;
    private int currentPage = PAGE_START;
    private boolean isNextPageCalled = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false);

        binding.ivClose.setOnClickListener(v -> dismiss());

        isRemembered = userSessionManager.isRemembered();


        if (getArguments() != null) {
//            userId = getArguments().getLong("user_id");
            onlineId = getArguments().getLong("online_id");
            getStatus = getArguments().getInt("status");
            getDate = getArguments().getString("appointmentDate");
            getTime = getArguments().getString("appointmentTime");
            getNote = getArguments().getString("Note");
            patientId = getArguments().getLong("patientId");
        }


        setText();
        binding.mainNote.setText(getNote);
        binding.noteRecycler.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));

        binding.noteRecycler.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.HORIZONTAL,
                1, R.dimen.recycler_vertical_offset, R.dimen.recycler_horizontal_offset, true));

        adapter = new NoteAdapter(mActivity, new NoteAdapter.OnItemClickListener() {
            @Override
            public void onDelete(AppointmentListModel.Data.Patient.Appointment.Note item, int position) {

            }

            @Override
            public void onEdit(AppointmentListModel.Data.Patient.Appointment.Note item, int position) {

            }

            @Override
            public void onView(AppointmentListModel.Data.Patient.Appointment.Note item, int position) {

            }

            @Override
            public void changeStatus(View itemView, AppointmentListModel.Data.Patient.Appointment.Note item, int position) {

            }
        });
        binding.noteRecycler.setAdapter(adapter);
        binding.noteRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) {
//                    if (isLoadedOnline && !isLastPage && !isNextPageCalled) {
//                        if (ConnectivityReceiver.isConnected())
//                            loadMoreItems();
////                        else
////                            Toast.makeText(ProductList.this, "Looks like you're not connected with internet!", Toast.LENGTH_LONG).show();
//                    }
//                }
            }
        });
        getAppointment(pPosition);
        return binding.getRoot();
    }

    private void setText() {
        binding.tvUserInfoTitle.setText(LanguageExtension.setText("appointment_details", getString(R.string.appointment_details)));

        binding.tvUserNameTitle.setText(LanguageExtension.setText("datetime", getString(R.string.datetime)));
        binding.tvUserPhoneTitle.setText(LanguageExtension.setText("status", getString(R.string.status)));


//        binding.tvUserName.setText(String.valueOf(getStatus));

        binding.tvUserPhone.setText(getDate + " " + getTime);

        if (getStatus == 0) {
            binding.tvUserName.setText("Active");
        } else {
            binding.tvUserName.setText("Completed");
        }

    }

    private void getAppointment(int pPosition) {
        isNextPageCalled = true;
        AndroidNetworking
                .post(BASE_URL + HEALTH_RECORD_APPOINTMENT_LIST)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("patient_id", String.valueOf(patientId))
//                .addBodyParameter("page_no", String.valueOf(currentPage))
//                .addBodyParameter("search_text", searchText)
//                .setTag("user-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.e(TAG, "onResponse: " + response);
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data").getJSONObject("patient");
                                JSONArray appointments = data.getJSONArray("appointments");
//                                int totalPage = data.getInt("total_page");
//                                currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    userList.clear();
                                    adapter.clearLists();
//                                    pPosition = -1;
                                }

                                if (appointments.length() > 0) {
                                    List<AppointmentListModel.Data.Patient.Appointment.Note> uList = new ArrayList<>();
                                    for (int i = 0; i < appointments.length(); i++) {
                                        JSONObject appointment = appointments.getJSONObject(i);
                                        JSONArray notes = appointment.getJSONArray("notes");

                                        if (notes.length() > 0) {
                                            for (int j = 0; j < notes.length(); j++) {
                                                JSONObject note = notes.getJSONObject(j);
                                                AppointmentListModel.Data.Patient.Appointment.Note noteItem = new Gson().fromJson(note.toString(), AppointmentListModel.Data.Patient.Appointment.Note.class);
                                                uList.add(noteItem);
                                            }
                                        }


//                                        uList.add(userItem);
//                                        dbHandler.AddAppointment(userItem);
                                    }

                                    userList.addAll(uList);
                                    adapter.addUserList(uList);
                                }

                                if (pPosition > 1 && pPosition <= userList.size() - 1) {
                                    userList.remove(pPosition);
                                    adapter.removeUser(pPosition);
                                    adapter.notifyItemChanged(pPosition - 1);
                                }

                                if (currentPage == PAGE_START)
                                    adapter.notifyDataSetChanged();
                                else
                                    adapter.notifyItemRangeInserted(pPosition, data.length());


//                                isLastPage = totalPage <= currentPage;
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
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

//        userList.clear();
//        userList.add(new UserItem(1, "John Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//        userList.add(new UserItem(2, "Jane Doe", "02/10/2021, 11:52 AM", "John.doe@safra.cloud", "Moderator", 10, 10));
//
//        adapter.notifyDataSetChanged();
//        checkForEmptyState();
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