package com.safra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.adapters.CascadingOptionRecyclerAdapter;
import com.safra.databinding.FragmentCascadingDropdownSettingBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.interfaces.CascadeOptionChanges;
import com.safra.models.CascadeOptionItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CascadingDropdownSettingFragment extends DialogFragment {

    public static final String TAG = "cascading_setting_frg";

    private FragmentActivity mActivity = null;

    private List<CascadeOptionItem> optionList = new ArrayList<>();
    private CascadingOptionRecyclerAdapter adapter;

    private String requestKey;
    private String cascadeJson;
    private int position;

    private boolean isAnyEnable = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCascadingDropdownSettingBinding binding = FragmentCascadingDropdownSettingBinding.inflate(inflater, container, false);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.ivClose.setOnClickListener(v -> dismiss());
        binding.tvSelectFieldHeading.setText(LanguageExtension.setText("cascading_dropdown", getString(R.string.cascading_dropdown)));
        binding.tvAddChild.setText(LanguageExtension.setText("add_child", getString(R.string.add_child)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
//        Log.e(TAG, "onCreateView: " + getTargetRequestCode());
//        if (getTargetFragment() != null)
//            Log.e(TAG, "onCreateView: " + getTargetFragment().getTag());
        if (getArguments() != null) {
            requestKey = getArguments().getString("request_key");
            cascadeJson = getArguments().getString("cascade_json");
            position = getArguments().getInt("position");
        }

        binding.rvCascadingOptions.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
//        binding.rvCascadingOptions.addItemDecoration(new LineItemDecoration(mActivity, R.dimen._0dp, true));
        adapter = new CascadingOptionRecyclerAdapter(mActivity, new CascadeOptionChanges() {
            @Override
            public void onOptionEnableDisable(boolean isEnable) {
                isAnyEnable = adapter.getIsAnyEnabled();
            }
        });
        binding.rvCascadingOptions.setAdapter(adapter);

        binding.tvAddChild.setOnClickListener(v -> {
            adapter.addOption("New Option for level 1", 1);
        });

        binding.btnSave.setOnClickListener(v -> {
            if(!isAnyEnable) {
                optionList = adapter.getOptionList();
                JSONArray jsonArray = new JSONArray();
                int parentId = 0;
                for (CascadeOptionItem coi : optionList) {
                    createJsonForCascading(coi, parentId, jsonArray);
                    Log.e(TAG, "onCreateView: " + coi);
                }

                Log.e(TAG, "onCreateView: " + jsonArray);

                Bundle bundle = new Bundle();
                bundle.putString("cascade_json", jsonArray.toString());
                bundle.putInt("position", position);
                getParentFragmentManager().setFragmentResult(requestKey, bundle);
                dismiss();
            }
        });

        if(cascadeJson != null) {
            try {
                createCascadingForJson(new JSONArray(cascadeJson), optionList);
                adapter.setOptions(optionList);
            } catch (JSONException e) {
                Log.e(TAG, "onCreateView: " + e.getLocalizedMessage());
            }
        }

        return binding.getRoot();
    }

    private void createJsonForCascading(CascadeOptionItem coi, int parentId, JSONArray jsonArray) {
        try {
            int position = jsonArray.length() + 1;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", position);
            jsonObject.put("val", coi.getOption());
            jsonObject.put("parent_id", parentId);
            jsonObject.put("level_id", coi.getLevel());

            jsonArray.put(jsonObject);
            parentId = position;

            if (coi.getChildOptionList().size() > 0) {
                for (CascadeOptionItem coiChild : coi.getChildOptionList()) {
                    createJsonForCascading(coiChild, parentId, jsonArray);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "createJsonForCascading: " + e.getLocalizedMessage());
        }
    }

    private void createCascadingForJson(JSONArray jsonArray, List<CascadeOptionItem> optionList) {
        try {
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CascadeOptionItem optionItem = new CascadeOptionItem();
                    optionItem.setId(jsonObject.getInt("id"));
                    optionItem.setOption(jsonObject.getString("val"));
                    optionItem.setParentId(jsonObject.getInt("parent_id"));
                    optionItem.setChildOptionList(new ArrayList<>());
                    optionItem.setLevel(jsonObject.getInt("level_id"));

                    optionList.add(optionItem);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "createCascadingForJson: " + e.getLocalizedMessage());
        }

        if (optionList.size() > 0) {
            for (int i = (optionList.size() - 1); i >= 0; i--) {
                CascadeOptionItem optionItem = optionList.get(i);
                for (int j = 0; j < optionList.size(); j++) {
                    CascadeOptionItem optionItem1 = optionList.get(j);
                    if (optionItem.getParentId() == optionItem1.getId()) {
                        List<CascadeOptionItem> childList = optionItem1.getChildOptionList();
                        childList.add(0, optionItem);
                        optionItem1.setChildOptionList(childList);
                        optionList.remove(i);
                    }
                }
            }
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
