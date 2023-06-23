package com.safra.fragments;

import static com.safra.utilities.Common.REQUEST_CASCADING_SETTINGS;
import static com.safra.utilities.Common.REQUEST_FIELD_PROPERTIES;
import static com.safra.utilities.Common.REQUEST_SELECT_FORM_ELEMENT;
import static com.safra.utilities.Common.REQUEST_SELECT_TEMPLATE;
import static com.safra.utilities.FormElements.TYPE_ACHIEVED_UNIT;
import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CASCADING_SELECT;
import static com.safra.utilities.FormElements.TYPE_SELECT_BOXES_GROUP;
import static com.safra.utilities.FormElements.TYPE_NUMBER;
import static com.safra.utilities.FormElements.TYPE_PLACE_TARGET;
import static com.safra.utilities.FormElements.TYPE_QUIZ_MCQ;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_ANSWER;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_POINT;
import static com.safra.utilities.FormElements.TYPE_RADIO_GROUP;
import static com.safra.utilities.FormElements.TYPE_SELECT;
//import static com.safra.utilities.FormElements.TYPE_SURVEY;
import static com.safra.utilities.FormElements.TYPE_TEXT_AREA;
import static com.safra.utilities.FormElements.TYPE_UNIT_PRICE;
import static com.safra.utilities.UserPermissions.TEMPLATE_USE;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.safra.CreateForm;
import com.safra.R;
import com.safra.databinding.FragmentFormEditBinding;
import com.safra.events.FieldListChangedEvent;
import com.safra.events.MarksChangedEvent;
import com.safra.extensions.FormExtension;
import com.safra.extensions.GeneralExtension;
import com.safra.extensions.LanguageExtension;
import com.safra.extensions.PermissionExtension;
import com.safra.fragments.fieldPropeties.FieldPropertiesFragment;
import com.safra.interfaces.OpenPropertiesInterface;
import com.safra.models.formElements.BaseFormElement;
import com.safra.utilities.FormBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormEditFragment extends Fragment
        implements OpenPropertiesInterface {

    public static final String TAG = "form_edit_fragment";

    private FragmentActivity mActivity = null;

    private FormBuilder formBuilder;

    private boolean useTemplate = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentFormEditBinding binding = FragmentFormEditBinding.inflate(inflater, container, false);

        formBuilder = new FormBuilder(getActivity(), binding.rvFormFields, this);

        if (getArguments() != null) {
            boolean isNew = getArguments().getBoolean("is_new");
            if (isNew)
                useTemplate = getArguments().getBoolean("use_template");
            else {
                if (CreateForm.formItem != null)
                    setFormFields(CreateForm.formItem.getFormJson());
            }
        }

        if (useTemplate && PermissionExtension.checkForPermission(TEMPLATE_USE))
            showTemplatesDialog();

        binding.fabAdd.setOnClickListener(v -> {
            SelectFieldFragment dialogS = new SelectFieldFragment();
            Bundle bundle = new Bundle();
            bundle.putString("request_key", REQUEST_SELECT_FORM_ELEMENT);
            dialogS.setArguments(bundle);
            dialogS.show(getChildFragmentManager(), SelectFieldFragment.TAG);
        });

        getChildFragmentManager().setFragmentResultListener(REQUEST_SELECT_FORM_ELEMENT, this,
                (requestKey, result) -> {
                    if (requestKey.equalsIgnoreCase(REQUEST_SELECT_FORM_ELEMENT)) {
                        if (result.getInt("type") != TYPE_CASCADING) {
                            FormExtension.addFormElement(mActivity, TAG, formBuilder, result, ((CreateForm) mActivity).formLanguageId);
                            EventBus.getDefault().post(new FieldListChangedEvent());
                        } else {
                            CascadingDropdownSettingFragment dialogC = new CascadingDropdownSettingFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("request_key", REQUEST_CASCADING_SETTINGS);
                            bundle.putString("cascade_json", getCascadeJson());
                            bundle.putInt("position", -1);
                            dialogC.setArguments(bundle);
                            dialogC.show(getChildFragmentManager(), CascadingDropdownSettingFragment.TAG);
                        }
                    }
                });

        getChildFragmentManager().setFragmentResultListener(REQUEST_SELECT_TEMPLATE, this,
                (requestKey, result) -> {
                    if (requestKey.equalsIgnoreCase(REQUEST_SELECT_TEMPLATE)) {
                        convertFieldsToList(result.getString("form_fields"));
                        EventBus.getDefault().post(new FieldListChangedEvent());
                    }
                });

        getChildFragmentManager().setFragmentResultListener(REQUEST_FIELD_PROPERTIES, this,
                (requestKey, result) -> {
                    if (requestKey.equalsIgnoreCase(REQUEST_FIELD_PROPERTIES)) {
                        FormExtension.updateFormElement(TAG, formBuilder, result);
                        EventBus.getDefault().post(new FieldListChangedEvent());
                    }
                });

        getChildFragmentManager().setFragmentResultListener(REQUEST_CASCADING_SETTINGS, this,
                (requestKey, result) -> {
                    if (requestKey.equalsIgnoreCase(REQUEST_CASCADING_SETTINGS)) {
                        try {
                            FormExtension.addCascadingSelectElement(TAG, formBuilder, result.getString("cascade_json"),
                                    result.getInt("position"));
                        } catch (JSONException e) {
                            Log.e(TAG, "onCreateView: " + REQUEST_CASCADING_SETTINGS + " -> " + e.getLocalizedMessage());
                        }
                    }
                });

        return binding.getRoot();
    }

    private void showTemplatesDialog() {
        TemplatesFragment dialogT = new TemplatesFragment();
        dialogT.setCancelable(false);
        Bundle bundle = new Bundle();
        bundle.putString("request_key", REQUEST_SELECT_TEMPLATE);
        dialogT.setArguments(bundle);
        dialogT.show(getChildFragmentManager(), TemplatesFragment.TAG);
    }

    public void setFormFields(String s) {
        if (formBuilder != null) {
            Log.e(TAG, "setFormFields: ");
            convertFieldsToList(s);
        }
    }

    @Override
    public void openPropertiesDialog(BaseFormElement baseFormElement, int position, int childPosition) {
        int type = baseFormElement.getType();

        if (type == TYPE_CASCADING) {
            CascadingDropdownSettingFragment dialogC = new CascadingDropdownSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("request_key", REQUEST_CASCADING_SETTINGS);
            bundle.putString("cascade_json", baseFormElement.getElementJsonArray());
            bundle.putInt("position", position);
            dialogC.setArguments(bundle);
            dialogC.show(getChildFragmentManager(), CascadingDropdownSettingFragment.TAG);
        } else {
            FieldPropertiesFragment dialogT = new FieldPropertiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("request_key", REQUEST_FIELD_PROPERTIES);
            bundle.putInt("position", position);
            bundle.putString("label", baseFormElement.getFieldLabel());
            bundle.putString("value", baseFormElement.getFieldValue());
            bundle.putString("name", baseFormElement.getFieldName());
            bundle.putBoolean("is_required", baseFormElement.isRequired());
            bundle.putInt("type", baseFormElement.getType());
            bundle.putString("field_type", baseFormElement.getFieldType());

            bundle.putBoolean("limit_access", baseFormElement.isHaveAccess());

            if (baseFormElement.getRole() != null && baseFormElement.getRole().length > 0)
                bundle.putLongArray("role", GeneralExtension.toPrimitiveLong(baseFormElement.getRole()));
//        bundle.putInt("sub_type", baseFormElement.getFieldSubType());

            switch (type) {
                case TYPE_QUIZ_TEXT_ANSWER:
                    bundle.putInt("child_position", childPosition);
                    bundle.putInt("rows", baseFormElement.getRows());
                    bundle.putInt("max_length", baseFormElement.getMaxLength());
                    break;
                case TYPE_TEXT_AREA:
                    bundle.putInt("rows", baseFormElement.getRows());
                    bundle.putInt("max_length", baseFormElement.getMaxLength());
                    break;
                case TYPE_QUIZ_TEXT_POINT:
                    bundle.putInt("child_position", childPosition);
                    bundle.putString("min", baseFormElement.getMin());
                    bundle.putString("max", baseFormElement.getMax());
                    bundle.putInt("step", baseFormElement.getStep());
                    break;
                case TYPE_NUMBER:
                case TYPE_ACHIEVED_UNIT:
                case TYPE_UNIT_PRICE:
                    bundle.putString("min", baseFormElement.getMin());
                    bundle.putString("max", baseFormElement.getMax());
                    bundle.putInt("step", baseFormElement.getStep());
                    break;
                case TYPE_SELECT:
                case TYPE_PLACE_TARGET:
                case TYPE_SELECT_BOXES_GROUP:
                case TYPE_RADIO_GROUP:
//                case TYPE_SURVEY:
                case TYPE_QUIZ_MCQ:
                case TYPE_CASCADING_SELECT:
                    Log.e(TAG, "openPropertiesDialog: " + baseFormElement.getOptions().size());
                    bundle.putParcelableArrayList("option_list", new ArrayList<>(baseFormElement.getOptions()));
                    break;
                case TYPE_QUIZ_TEXT:
                    bundle.putInt("child_position", childPosition);
                    break;

            }
            dialogT.setArguments(bundle);
//        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//        dialogT.setTargetFragment(FormEditFragment.this, REQUEST_FIELD_PROPERTIES);
            dialogT.show(getChildFragmentManager(), FieldPropertiesFragment.TAG);
        }
    }

    public boolean validateInputs() {
        if (formBuilder.getFormElements().size() == 0) {
            Toast.makeText(mActivity, LanguageExtension.setText("add_at_least_one_field_to_continue",
                    getString(R.string.add_at_least_one_field_to_continue)), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public HashMap<String, String> getFieldValues() {
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            hashMap.put("form_fields", convertFieldsToJson());
        } catch (JSONException e) {
            Log.e(TAG, "getFieldValues: " + e.getLocalizedMessage());
        }

        return hashMap;
    }

    private void convertFieldsToList(String formFieldsJson) {
        int maxLogSize = 1000;
        for (int i = 0; i <= formFieldsJson.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = Math.min(end, formFieldsJson.length());
            Log.e(TAG,"error" +formFieldsJson.substring(start, end));
        }
        formBuilder.clearFormElements();
        try {
            JSONObject jsonObject = new JSONObject(formFieldsJson);
            // Process the jsonObject as needed
            // ...

            // Example: Accessing the "components" array from the jsonObject
            JSONArray components = jsonObject.getJSONArray("components");
            FormExtension.convertJSONToElement(formBuilder, components);

            // Example: Iterating over the "components" array
            for (int i = 0; i < components.length(); i++) {
                JSONObject component = components.getJSONObject(i);
                // Process each component as needed
                // ...
            }

        } catch (JSONException je) {
            Log.e(TAG, "convertFieldsToList: " + je.getLocalizedMessage());
        }
    }

    private String convertFieldsToJson() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        List<BaseFormElement> elementList = formBuilder.getFormElements();
        System.out.println(" formBuilder.getFormElements()"+ elementList);

        jsonArray = FormExtension.convertElementToJSON(elementList);
        System.out.println(" jsonArray"+ elementList);
//        for (BaseFormElement be : elementList) {
//        }
        Log.e(TAG, "convertFieldsToJson: " + jsonArray);

        return jsonArray.toString();
    }

    private String getCascadeJson() {
        return "[{\"id\":1,\"val\":\"Car\",\"parent_id\":0,\"level_id\":1}," +
                "{\"id\":2,\"val\":\"Ford\",\"parent_id\":1,\"level_id\":2}," +
                "{\"id\":3,\"val\":\"GT\",\"parent_id\":2,\"level_id\":3}," +
                "{\"id\":4,\"val\":\"Petrol\",\"parent_id\":3,\"level_id\":4}," +
                "{\"id\":5,\"val\":\"BMW\",\"parent_id\":1,\"level_id\":2}," +
                "{\"id\":6,\"val\":\"i3\",\"parent_id\":5,\"level_id\":3}," +
                "{\"id\":7,\"val\":\"Audi\",\"parent_id\":1,\"level_id\":2}," +
                "{\"id\":8,\"val\":\"Truck\",\"parent_id\":0,\"level_id\":1}]";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFieldListChanged(FieldListChangedEvent event) {
        Log.e(TAG, "onFieldListChanged: ");
        CreateForm.totalMarks = formBuilder.getTotalMarks();
        EventBus.getDefault().post(new MarksChangedEvent());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }


}
