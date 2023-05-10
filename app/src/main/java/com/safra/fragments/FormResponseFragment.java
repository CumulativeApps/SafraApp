package com.safra.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.safra.FillForm;
import com.safra.R;
import com.safra.databinding.FragmentFormResponseBinding;
import com.safra.extensions.FormExtension;
import com.safra.models.FileItem;
import com.safra.utilities.FormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FormResponseFragment extends DialogFragment {

    public static final String TAG = "form_preview_fragment";

    private FragmentActivity mActivity = null;

    private FormBuilder formBuilder;

    private long formId, responseId, onlineId;
    private String formTitle, formFieldsJson;
    private ArrayList<FileItem> fileList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentFormResponseBinding binding = FragmentFormResponseBinding.inflate(inflater, container, false);

        binding.ivClose.setOnClickListener(v -> dismiss());

        formBuilder = new FormBuilder(mActivity, binding.rvFormFields);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            formId = bundle.getLong("form_id");
            responseId = bundle.getLong("response_id");
            onlineId = bundle.getLong("online_id");
            formTitle = bundle.getString("form_title");
            formFieldsJson = bundle.getString("form_fields");
            binding.tvFormTitle.setText(formTitle);

            if(bundle.containsKey("files")) {
                fileList = bundle.getParcelableArrayList("files");
                convertFieldsToList(formFieldsJson, fileList);
            } else {
                convertFieldsToList(formFieldsJson);
            }
        }

        binding.btnEditResponse.setOnClickListener(v -> {
            Intent i = new Intent(mActivity, FillForm.class);
            Bundle bundle = new Bundle();
            bundle.putString("reason_to_come", "edit_response");
            bundle.putLong("form_id", formId);
            bundle.putString("form_title", formTitle);
            bundle.putString("form_fields", formFieldsJson);
            bundle.putLong("response_id", responseId);
            bundle.putLong("online_id", onlineId);

            if (fileList != null && fileList.size() > 0)
                bundle.putParcelableArrayList("files", fileList);
//            bundle.putLong("form_access", item.getFormAccess());
            i.putExtras(bundle);
            startActivity(i);
        });

        return binding.getRoot();
    }

    private void convertFieldsToList(String formFieldsJson, ArrayList<FileItem> fileList) {
        Log.e(TAG, "convertFieldsToList: " + formFieldsJson);
        try {
            JSONArray jsonArray = new JSONArray(formFieldsJson);
            FormExtension.convertJSONToElementForResponse(formBuilder, jsonArray, fileList);
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                }
//            }
        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsToList: " + je.getLocalizedMessage());
        }
    }

    private void convertFieldsToList(String formFieldsJson) {
        Log.e(TAG, "convertFieldsToList: " + formFieldsJson);
        try {
            JSONArray jsonArray = new JSONArray(formFieldsJson);
            FormExtension.convertJSONToElementForResponse(formBuilder, jsonArray, new ArrayList<>());
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                }
//            }
        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsToList: " + je.getLocalizedMessage());
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
