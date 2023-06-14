package com.safra.fragments.fieldPropeties;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.safra.R;
import com.safra.Safra;
import com.safra.adapters.AccessRecyclerAdapter;
import com.safra.adapters.OptionRecyclerAdapter;
import com.safra.databinding.FragmentFieldPropertiesBinding;
import com.safra.dialogs.LoadingDialog;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.LoadingDialogExtension;
import com.safra.models.AccessItem;
import com.safra.models.OptionItem;
import com.safra.utilities.ConnectivityReceiver;
import com.safra.utilities.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.safra.db.DBHandler.dbHandler;
import static com.safra.utilities.Common.BASE_URL;
import static com.safra.utilities.Common.GROUP_LIST_API;
import static com.safra.utilities.Common.PAGE_START;
import static com.safra.utilities.FormElements.TYPE_ACHIEVED_UNIT;
import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CASCADING_SELECT;
import static com.safra.utilities.FormElements.TYPE_SELECT_BOXES_GROUP;
import static com.safra.utilities.FormElements.TYPE_DATE;
import static com.safra.utilities.FormElements.TYPE_EMAIL;
import static com.safra.utilities.FormElements.TYPE_FILE;
import static com.safra.utilities.FormElements.TYPE_HEADER;
import static com.safra.utilities.FormElements.TYPE_LOCATION;
import static com.safra.utilities.FormElements.TYPE_MONTH;
import static com.safra.utilities.FormElements.TYPE_NUMBER;
import static com.safra.utilities.FormElements.TYPE_PASSWORD;
import static com.safra.utilities.FormElements.TYPE_PLACE_TARGET;
import static com.safra.utilities.FormElements.TYPE_QUIZ_MCQ;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_ANSWER;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_POINT;
import static com.safra.utilities.FormElements.TYPE_RADIO_GROUP;
import static com.safra.utilities.FormElements.TYPE_SELECT;
import static com.safra.utilities.FormElements.TYPE_SEPARATOR;
import static com.safra.utilities.FormElements.TYPE_SURVEY;
import static com.safra.utilities.FormElements.TYPE_TEL;
import static com.safra.utilities.FormElements.TYPE_TEXT;
import static com.safra.utilities.FormElements.TYPE_CHECKBOX;
import static com.safra.utilities.FormElements.TYPE_TEXT_AREA;
import static com.safra.utilities.FormElements.TYPE_TIME;
import static com.safra.utilities.FormElements.TYPE_UNIT_PRICE;
import static com.safra.utilities.FormElements.TYPE_URL;
import static com.safra.utilities.FormElements.TYPE_DATETIME;
import static com.safra.utilities.UserSessionManager.userSessionManager;

public class FieldPropertiesFragment extends DialogFragment {

    public static final String TAG = "field_property_fragment";

    private FragmentFieldPropertiesBinding binding;

    private FragmentActivity mActivity = null;

    private final List<AccessItem> accessList = new ArrayList<>();
    private AccessRecyclerAdapter adapterA;

    private final ArrayList<OptionItem> optionList = new ArrayList<>();
    private OptionRecyclerAdapter adapterO;

    private final ArrayList<OptionItem> mcqOptionList = new ArrayList<>();
    private OptionRecyclerAdapter adapterM;

    private long[] role;

    private String name;
    private int position, childPosition = -1, type;

    private String requestKey;

    private boolean isRemembered;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFieldPropertiesBinding.inflate(inflater, container, false);

        isRemembered = userSessionManager.isRemembered();

        binding.ivClose.setOnClickListener(v -> dismiss());

        setText();

        binding.rvLimitAccess.addItemDecoration(new SpaceItemDecoration(mActivity, RecyclerView.VERTICAL, 2, R.dimen._0dp, R.dimen.recycler_horizontal_offset, false));
        adapterA = new AccessRecyclerAdapter(accessList, (item, position) -> {

        });
        binding.rvLimitAccess.setAdapter(adapterA);

        adapterO = new OptionRecyclerAdapter(optionList, new OptionRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onRemove(OptionItem item, int position) {
                optionList.remove(position);
                adapterO.notifyItemRemoved(position);
            }
        });
        binding.rvOptions.setAdapter(adapterO);
        adapterM = new OptionRecyclerAdapter(mcqOptionList, new OptionRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onRemove(OptionItem item, int position) {
                mcqOptionList.remove(position);
                adapterM.notifyItemRemoved(position);
            }
        });
        binding.rvMcqOptions.setAdapter(adapterM);

        if (getArguments() != null) {
            Bundle b = getArguments();
            requestKey = b.getString("request_key");
            position = b.getInt("position");
            if(b.containsKey("child_position"))
                childPosition = b.getInt("child_position");
            type = b.getInt("type");
            setFieldData(type, b);
        }

        binding.etOptionLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.etOptionLabel.setError(null);
            }
        });
        binding.etOptionValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.etOptionValue.setError(null);
            }
        });
        binding.etMcqOptionLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.etMcqOptionLabel.setError(null);
            }
        });
        binding.etMcqOptionValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                    binding.etMcqOptionValue.setError(null);
            }
        });

        binding.cbLimitAccess.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.rvLimitAccess.setVisibility(View.VISIBLE);
            } else {
                binding.rvLimitAccess.setVisibility(View.GONE);
            }
        });

        binding.btnAdd.setOnClickListener(view -> {
            String l = binding.etOptionLabel.getText().toString();
            String v = binding.etOptionValue.getText().toString();
            if ((l.isEmpty() && !v.isEmpty())) {
                binding.etOptionLabel.setError(LanguageExtension.setText("enter_label", getString(R.string.enter_label)));
                binding.etOptionLabel.requestFocus();
            } else if (!l.isEmpty() && v.isEmpty()) {
                v = l.replaceAll("\\s+", "-").toLowerCase();
                optionList.add(new OptionItem(l, v, false));
                if (optionList.size() == 1) {
                    adapterO.notifyDataSetChanged();
                } else {
                    adapterO.notifyItemInserted(optionList.size() - 1);
                }
                binding.etOptionLabel.setText("");
                binding.etOptionValue.setText("");
                binding.etOptionLabel.requestFocus();
                Log.e("Option List", "onCreateView: " + optionList.size());
            } else {
                optionList.add(new OptionItem(l, v, false));
                if (optionList.size() == 1) {
                    adapterO.notifyDataSetChanged();
                } else {
                    adapterO.notifyItemInserted(optionList.size() - 1);
                }
                binding.etOptionLabel.setText("");
                binding.etOptionValue.setText("");
                binding.etOptionLabel.requestFocus();
                Log.e("Option List", "onCreateView: " + optionList.size());
            }
        });

        binding.btnMcqAdd.setOnClickListener(view -> {
            String l = binding.etMcqOptionLabel.getText().toString();
            String v = binding.etMcqOptionValue.getText().toString();
            if (l.isEmpty() || v.isEmpty()) {
                if (v.isEmpty()) {
                    binding.etMcqOptionLabel.setError(LanguageExtension.setText("enter_marks", getString(R.string.enter_marks)));
                    binding.etMcqOptionLabel.requestFocus();
                }
                if (l.isEmpty()) {
                    binding.etMcqOptionLabel.setError(LanguageExtension.setText("enter_answer", getString(R.string.enter_answer)));
                    binding.etMcqOptionLabel.requestFocus();
                }
            } else {
                mcqOptionList.add(new OptionItem(l, v, false));
                if (mcqOptionList.size() == 1) {
                    adapterM.notifyDataSetChanged();
                } else {
                    adapterM.notifyItemInserted(mcqOptionList.size() - 1);
                }
                binding.etMcqOptionLabel.setText("");
                binding.etMcqOptionValue.setText("");
                binding.etMcqOptionLabel.requestFocus();
                Log.e("Option List", "onCreateView: " + mcqOptionList.size());
            }
        });

        binding.btnSave.setOnClickListener(v -> validateInputs());

        return binding.getRoot();
    }

    private void setText() {
        binding.switchRequired.setText(LanguageExtension.setText("required", getString(R.string.required)));
        binding.tvLabel.setText(LanguageExtension.setText("label", getString(R.string.label)));
        binding.cbLimitAccess.setText(LanguageExtension.setText("limit_access_to_one_or_more_roles", getString(R.string.limit_access_to_one_or_more_roles)));
        binding.tvValue.setText(LanguageExtension.setText("value", getString(R.string.value)));
        binding.tvRows.setText(LanguageExtension.setText("rows", getString(R.string.rows)));
        binding.tvMaxLength.setText(LanguageExtension.setText("max_length", getString(R.string.max_length)));
        binding.tvMin.setText(LanguageExtension.setText("min", getString(R.string.min)));
        binding.tvMax.setText(LanguageExtension.setText("max", getString(R.string.max)));
        binding.tvStep.setText(LanguageExtension.setText("step", getString(R.string.step)));
        binding.tvOption.setText(LanguageExtension.setText("options", getString(R.string.options)));
        binding.etOptionLabel.setHint(LanguageExtension.setText("label", getString(R.string.label)));
        binding.etOptionValue.setHint(LanguageExtension.setText("value", getString(R.string.value)));
        binding.btnAdd.setText(LanguageExtension.setText("add", getString(R.string.add)));
        binding.tvMcqOption.setText(LanguageExtension.setText("options", getString(R.string.options)));
        binding.etMcqOptionLabel.setHint(LanguageExtension.setText("answer", getString(R.string.answer)));
        binding.etMcqOptionValue.setHint(LanguageExtension.setText("marks", getString(R.string.marks)));
        binding.btnSave.setText(LanguageExtension.setText("save", getString(R.string.save)));
    }

    private void setFieldData(int type, Bundle b) {
        name = b.getString("name");
        binding.switchRequired.setChecked(b.getBoolean("is_required"));
        binding.etLabel.setText(b.getString("label"));

        boolean isHaveAccess = b.getBoolean("limit_access");
        binding.cbLimitAccess.setChecked(isHaveAccess);

        if (isHaveAccess) {
            binding.rvLimitAccess.setVisibility(View.VISIBLE);
        } else {
            binding.rvLimitAccess.setVisibility(View.GONE);
        }

        if (b.containsKey("role")) {
            role = b.getLongArray("role");
        }
        setAccessList();

        optionList.clear();

        switch (type) {
            case TYPE_TEXT:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("text_field", getString(R.string.text_field)));
                break;
           case TYPE_CHECKBOX:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("checkbox_field", getString(R.string.checkbox_field)));
                break;
            case TYPE_URL:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("url_or_link", getString(R.string.url_or_link)));
                break;
            case TYPE_EMAIL:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("email_field", getString(R.string.email_field)));
                break;
            case TYPE_PASSWORD:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("password_field", getString(R.string.password_field)));
                break;
            case TYPE_TEL:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("telephone_field", getString(R.string.telephone_field)));
                break;
            case TYPE_MONTH:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("month_field", getString(R.string.month_field)));
                break;
            case TYPE_DATETIME:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("date_time", getString(R.string.date_time)));
                break;
            case TYPE_TIME:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("time_field", getString(R.string.time_field)));
                break;
            case TYPE_LOCATION:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("location_field", getString(R.string.location_field)));
                break;
            case TYPE_DATE:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("date_field", getString(R.string.date_field)));
                break;
            case TYPE_FILE:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("file_field", getString(R.string.file_field)));
                break;
            case TYPE_TEXT_AREA:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("text_area_field", getString(R.string.text_area_field)));
                break;
            case TYPE_NUMBER:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("number_field", getString(R.string.number_field)));
                break;
            case TYPE_ACHIEVED_UNIT:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("achieved_unit_field", getString(R.string.achieved_unit_field)));
                break;
            case TYPE_UNIT_PRICE:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("unit_price_field", getString(R.string.unit_price_field)));
                break;
            case TYPE_SELECT_BOXES_GROUP:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("select_boxes_field", getString(R.string.select_boxes_field)));
                break;
            case TYPE_RADIO_GROUP:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("radio_button_field", getString(R.string.radio_button_field)));
                break;
            case TYPE_SURVEY:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("radio_button_field", getString(R.string.radio_button_field)));
                break;
            case TYPE_SELECT:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("dropdown_field", getString(R.string.dropdown_field)));
                break;
            case TYPE_PLACE_TARGET:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("place_target_field", getString(R.string.place_target_field)));
                break;
            case TYPE_HEADER:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("header", getString(R.string.header)));
                break;
            case TYPE_SEPARATOR:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("separator", getString(R.string.separator)));
                break;
            case TYPE_QUIZ_MCQ:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("mcq", getString(R.string.mcq)));
                break;
            case TYPE_CASCADING:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("cascading_dropdown", getString(R.string.cascading_dropdown)));
                break;
            case TYPE_QUIZ_TEXT:
            case TYPE_QUIZ_TEXT_ANSWER:
            case TYPE_QUIZ_TEXT_POINT:
                binding.tvSelectFieldHeading.setText(LanguageExtension.setText("question", getString(R.string.question)));
                break;

        }

        switch (type) {
            case TYPE_SEPARATOR:
            case TYPE_HEADER:
                binding.switchRequired.setVisibility(View.GONE);
                binding.clLimitAccess.setVisibility(View.GONE);
                binding.clValue.setVisibility(View.GONE);
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clOptions.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_TEXT:
            case TYPE_CHECKBOX:
            case TYPE_URL:
            case TYPE_EMAIL:
            case TYPE_PASSWORD:
            case TYPE_TEL:
            case TYPE_MONTH:
            case TYPE_DATETIME:
            case TYPE_TIME:
            case TYPE_LOCATION:
            case TYPE_DATE:
            case TYPE_FILE:
                binding.etValue.setText(b.getString("value"));
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clOptions.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_TEXT_AREA:
            case TYPE_QUIZ_TEXT_ANSWER:
                binding.etValue.setText(b.getString("value"));
                binding.etRows.setText(b.getInt("rows") > 0 ? String.valueOf(b.getInt("rows")) : "");
                binding.etMaxLength.setText(b.getInt("max_length") > 0 ? String.valueOf(b.getInt("max_length")) : "");
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clOptions.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_NUMBER:
            case TYPE_ACHIEVED_UNIT:
            case TYPE_UNIT_PRICE:
            case TYPE_QUIZ_TEXT_POINT:
                binding.etValue.setText(b.getString("value"));
                binding.etMin.setText(b.getString("min"));
                binding.etMax.setText(b.getString("max"));
                binding.etStep.setText(b.getInt("step") > 0 ? String.valueOf(b.getInt("step")) : "");
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clOptions.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_SELECT_BOXES_GROUP:
                adapterO.setMultipleAllowed(true);
                if (b.getParcelableArrayList("option_list").size() > 0) {
                    optionList.addAll(b.getParcelableArrayList("option_list"));
                    adapterO.notifyDataSetChanged();
                }
                binding.clValue.setVisibility(View.GONE);
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_RADIO_GROUP:
            case TYPE_SURVEY:
            case TYPE_SELECT:
            case TYPE_PLACE_TARGET:
                adapterO.setMultipleAllowed(false);
                if (b.getParcelableArrayList("option_list").size() > 0) {
                    optionList.addAll(b.getParcelableArrayList("option_list"));
                    adapterO.notifyDataSetChanged();
                }
                binding.clValue.setVisibility(View.GONE);
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_CASCADING_SELECT:
                adapterO.setMultipleAllowed(false);
                adapterO.setEditable(false);
                if (b.getParcelableArrayList("option_list").size() > 0) {
                    optionList.addAll(b.getParcelableArrayList("option_list"));
                    adapterO.notifyDataSetChanged();
                }
                binding.clOptionEntryFields.setVisibility(View.GONE);
                binding.switchRequired.setVisibility(View.GONE);
                binding.clLimitAccess.setVisibility(View.GONE);
                binding.clValue.setVisibility(View.GONE);
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clMcqOptions.setVisibility(View.GONE);
                break;
            case TYPE_QUIZ_MCQ:
                adapterM.setMultipleAllowed(false);
                if (b.getParcelableArrayList("option_list").size() > 0) {
                    mcqOptionList.addAll(b.getParcelableArrayList("option_list"));
                    adapterM.notifyDataSetChanged();
                }
                binding.clValue.setVisibility(View.GONE);
                binding.clRows.setVisibility(View.GONE);
                binding.clMaxLength.setVisibility(View.GONE);
                binding.clMin.setVisibility(View.GONE);
                binding.clMax.setVisibility(View.GONE);
                binding.clStep.setVisibility(View.GONE);
                binding.clOptions.setVisibility(View.GONE);
                break;
        }
    }

    private void getGroups(int pageNo, List<Long> roleList) {
        AndroidNetworking
                .post(BASE_URL + GROUP_LIST_API)
                .addBodyParameter("user_token", isRemembered ? userSessionManager.getUserToken() : Safra.userToken)
                .addBodyParameter("page_no", String.valueOf(pageNo))
                .setTag("group-list-api")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");
                            String message = response.getString("message");
                            if (success == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray roles = data.getJSONArray("role_list");
                                int totalPage = data.getInt("total_page");
                                int currentPage = data.getInt("current_page");

                                if (currentPage == PAGE_START) {
                                    accessList.clear();
                                }

                                if (roles.length() > 0) {
                                    for (int i = 0; i < roles.length(); i++) {
                                        JSONObject role = roles.getJSONObject(i);
                                        AccessItem roleItem = new AccessItem();
                                        roleItem.setAccessId(role.getInt("role_id"));
                                        roleItem.setAccessName(role.getString("role_name"));

                                        if (roleList.contains(roleItem.getAccessId())) {
                                            roleItem.setSelected(true);
                                        }

                                        accessList.add(roleItem);
                                    }
                                }

                                adapterA.notifyDataSetChanged();

                                if (currentPage < totalPage) {
                                    getGroups(++currentPage, roleList);
                                } else {
                                    LoadingDialogExtension.hideLoading();
//                                    dialogL.dismiss();
                                }
                            } else {
                                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                                LoadingDialogExtension.hideLoading();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                            LoadingDialogExtension.hideLoading();
//                            dialogL.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: code -> " + anError.getErrorCode());
                        Log.e(TAG, "onError: detail -> " + anError.getErrorDetail());
                        Log.e(TAG, "onError: body -> " + anError.getErrorBody());
                        LoadingDialogExtension.hideLoading();
//                        dialogL.dismiss();
                    }
                });
    }

    private void setAccessList() {
        LoadingDialogExtension.showLoading(mActivity, LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        LoadingDialog dialogL = new LoadingDialog();
//        dialogL.setCancelable(false);
//        Bundle bundle = new Bundle();
//        bundle.putString("loading_message", LanguageExtension.setText("getting_data_progress", getString(R.string.getting_data_progress)));
//        dialogL.setArguments(bundle);
//        dialogL.show(getChildFragmentManager(), LoadingDialog.TAG);

        List<Long> roleList = new ArrayList<>();
        if (role != null) {
            Log.e(TAG, "setAccessList: " + role.length);
            for (long i : role) {
                roleList.add(i);
            }
        }

        if (ConnectivityReceiver.isConnected()) {
            getGroups(PAGE_START, roleList);
        } else {
            accessList.clear();
            accessList.addAll(dbHandler.getAccessList(isRemembered ? userSessionManager.getUserId() : Safra.userId));
            for (AccessItem ai : accessList) {
                ai.setSelected(roleList.contains(ai.getAccessId()));
            }
            adapterA.notifyDataSetChanged();
            LoadingDialogExtension.hideLoading();
//            dialogL.dismiss();
        }
//        accessList.add(new AccessItem(1, "Access 1", false));
//        accessList.add(new AccessItem(1, "Access 1", false));
//        accessList.add(new AccessItem(1, "Access 1", false));
//        adapterA.notifyDataSetChanged();
    }

    private void validateInputs() {
        String l = binding.etLabel.getText() != null ? binding.etLabel.getText().toString() : "";
        if (type != TYPE_SEPARATOR && l.isEmpty()) {
            binding.etLabel.setError(LanguageExtension.setText("enter_label", getString(R.string.enter_label)));
            binding.etLabel.requestFocus();
        } else if((type == TYPE_RADIO_GROUP || type == TYPE_SURVEY || type == TYPE_SELECT_BOXES_GROUP || type == TYPE_SELECT
                || type == TYPE_PLACE_TARGET) && optionList.isEmpty()) {
            Toast.makeText(mActivity, LanguageExtension.setText("please_enter_atleast_1_option",
                    getString(R.string.please_enter_atleast_1_option)), Toast.LENGTH_SHORT).show();
        } else if((type == TYPE_QUIZ_MCQ) && mcqOptionList.size() < 2) {
            Toast.makeText(mActivity, LanguageExtension.setText("please_enter_atleast_2_option",
                    getString(R.string.please_enter_atleast_2_option)), Toast.LENGTH_SHORT).show();
        } else {
            int ml, r, sp;
            String mn, mx;
            try {
                ml = Integer.parseInt(binding.etMaxLength.getText() != null ? binding.etMaxLength.getText().toString() : "0");
            } catch (NumberFormatException e) {
                ml = 0;
            }
            try {
                r = Integer.parseInt(binding.etRows.getText() != null ? binding.etRows.getText().toString() : "0");
            } catch (NumberFormatException e) {
                r = 0;
            }
            mn = binding.etMin.getText() != null ? binding.etMin.getText().toString() : "";
            mx = binding.etMax.getText() != null ? binding.etMax.getText().toString() : "";
            try {
                sp = Integer.parseInt(binding.etStep.getText() != null ? binding.etStep.getText().toString() : "0");
            } catch (NumberFormatException e) {
                sp = 0;
            }

            createResponseBundle(l, ml, r, sp, mn, mx);
        }
    }

    private void createResponseBundle(String l, int ml, int r, int sp, String mn, String mx) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        if(childPosition > -1)
            bundle.putInt("child_position", childPosition);
        bundle.putInt("type", type);
        bundle.putString("name", name);
        bundle.putBoolean("is_required", binding.switchRequired.isChecked());
        bundle.putString("label", l);
        bundle.putString("value", binding.etValue.getText() != null ? binding.etValue.getText().toString() : "");
        bundle.putInt("max_length", ml);
        bundle.putInt("rows", r);
        bundle.putString("min", mn);
        bundle.putString("max", mx);
        bundle.putInt("step", sp);
        if (binding.cbLimitAccess.isChecked()) {
            List<AccessItem> list = adapterA.getSelectedList();
            role = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                role[i] = list.get(i).getAccessId();
            }
            bundle.putLongArray("role", role);
        }
        bundle.putBoolean("limit_access", binding.cbLimitAccess.isChecked());
        bundle.putParcelableArrayList("option_list", new ArrayList<>(adapterO.getList()));
        bundle.putParcelableArrayList("mcq_option_list", new ArrayList<>(adapterM.getList()));
        sendResponseBundle(bundle);
    }

    public void sendResponseBundle(Bundle bundle) {
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
        dismiss();
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
