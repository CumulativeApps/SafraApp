package com.safra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.adapters.FieldRecyclerAdapter;
import com.safra.databinding.FragmentSelectFieldBinding;
import com.safra.extensions.LanguageExtension;
import com.safra.models.FieldItem;
import com.safra.utilities.LineItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.safra.utilities.FormElements.TYPE_ACHIEVED_UNIT;
import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CHECKBOX_GROUP;
import static com.safra.utilities.FormElements.TYPE_DATE;
import static com.safra.utilities.FormElements.TYPE_EMAIL;
import static com.safra.utilities.FormElements.TYPE_FILE;
import static com.safra.utilities.FormElements.TYPE_HEADER;
import static com.safra.utilities.FormElements.TYPE_LOCATION;
import static com.safra.utilities.FormElements.TYPE_QUIZ_MCQ;
import static com.safra.utilities.FormElements.TYPE_MONTH;
import static com.safra.utilities.FormElements.TYPE_NUMBER;
import static com.safra.utilities.FormElements.TYPE_PASSWORD;
import static com.safra.utilities.FormElements.TYPE_PLACE_TARGET;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;
import static com.safra.utilities.FormElements.TYPE_RADIO_GROUP;
import static com.safra.utilities.FormElements.TYPE_SELECT;
import static com.safra.utilities.FormElements.TYPE_SEPARATOR;
import static com.safra.utilities.FormElements.TYPE_TEL;
import static com.safra.utilities.FormElements.TYPE_TEXT;
import static com.safra.utilities.FormElements.TYPE_TEXT_AREA;
import static com.safra.utilities.FormElements.TYPE_TIME;
import static com.safra.utilities.FormElements.TYPE_UNIT_PRICE;
import static com.safra.utilities.FormElements.TYPE_WEEK;

public class SelectFieldFragment extends DialogFragment {

    public static final String TAG = "select_field_fragment";

    private FragmentActivity mActivity = null;

    private final List<FieldItem> fieldList = new ArrayList<>();
    private FieldRecyclerAdapter adapter;

    private String requestKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSelectFieldBinding binding = FragmentSelectFieldBinding.inflate(inflater, container, false);

        binding.ivClose.setOnClickListener(v -> dismiss());
        binding.tvSelectFieldHeading.setText(LanguageExtension.setText("select_the_field", getString(R.string.select_the_field)));

        if(getArguments() != null)
            requestKey = getArguments().getString("request_key");

        binding.rvFields.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        binding.rvFields.addItemDecoration(new LineItemDecoration(mActivity, R.dimen._0dp, true));
        adapter = new FieldRecyclerAdapter(fieldList, (item, position) -> {
            sendResult(item.getFieldType(), item.getFieldName());
            dismiss();
        });
        binding.rvFields.setAdapter(adapter);

        getFields();

        return binding.getRoot();
    }

    private void getFields() {
        fieldList.clear();
        fieldList.add(new FieldItem(TYPE_TEXT, LanguageExtension.setText("text", getString(R.string.text)), R.drawable.ic_short_text));
        fieldList.add(new FieldItem(TYPE_TEXT_AREA, LanguageExtension.setText("text_area", getString(R.string.text_area)), R.drawable.ic_paragraph));
        fieldList.add(new FieldItem(TYPE_EMAIL, LanguageExtension.setText("email", getString(R.string.email)), R.drawable.ic_email));
        fieldList.add(new FieldItem(TYPE_PASSWORD, LanguageExtension.setText("password", getString(R.string.password)), R.drawable.ic_password));
        fieldList.add(new FieldItem(TYPE_TEL, LanguageExtension.setText("telephone", getString(R.string.telephone)), R.drawable.ic_phone));
        fieldList.add(new FieldItem(TYPE_NUMBER, LanguageExtension.setText("number", getString(R.string.number)), R.drawable.ic_number));
        fieldList.add(new FieldItem(TYPE_DATE, LanguageExtension.setText("date", getString(R.string.date)), R.drawable.ic_calendar_field));
        fieldList.add(new FieldItem(TYPE_TIME, LanguageExtension.setText("time", getString(R.string.time)), R.drawable.ic_time_field));
        fieldList.add(new FieldItem(TYPE_WEEK, LanguageExtension.setText("week", getString(R.string.week)), R.drawable.ic_week));
        fieldList.add(new FieldItem(TYPE_MONTH, LanguageExtension.setText("month", getString(R.string.month)), R.drawable.ic_month));
        fieldList.add(new FieldItem(TYPE_RADIO_GROUP, LanguageExtension.setText("radio", getString(R.string.radio)), R.drawable.ic_radio_button));
        fieldList.add(new FieldItem(TYPE_CHECKBOX_GROUP, LanguageExtension.setText("checkbox", getString(R.string.checkbox)), R.drawable.ic_check_box));
        fieldList.add(new FieldItem(TYPE_SELECT, LanguageExtension.setText("dropdown", getString(R.string.dropdown)), R.drawable.ic_multiple_choice));
        fieldList.add(new FieldItem(TYPE_FILE, LanguageExtension.setText("file", getString(R.string.file)), R.drawable.ic_file_upload));
        fieldList.add(new FieldItem(TYPE_PLACE_TARGET, LanguageExtension.setText("place_target", getString(R.string.place_target)), R.drawable.ic_place_target));
        fieldList.add(new FieldItem(TYPE_TEXT, LanguageExtension.setText("url_or_link", getString(R.string.url_or_link)), R.drawable.ic_url));
        fieldList.add(new FieldItem(TYPE_LOCATION, LanguageExtension.setText("location", getString(R.string.location)), R.drawable.ic_address));
        fieldList.add(new FieldItem(TYPE_ACHIEVED_UNIT, LanguageExtension.setText("achieved_unit", getString(R.string.achieved_unit)), R.drawable.ic_achieved_unit));
        fieldList.add(new FieldItem(TYPE_UNIT_PRICE, LanguageExtension.setText("unit_price", getString(R.string.unit_price)), R.drawable.ic_unit_price));
        fieldList.add(new FieldItem(TYPE_HEADER, LanguageExtension.setText("header", getString(R.string.header)), R.drawable.ic_header));
        fieldList.add(new FieldItem(TYPE_SEPARATOR, LanguageExtension.setText("separator", getString(R.string.separator)), R.drawable.ic_separator));
        fieldList.add(new FieldItem(TYPE_CASCADING, LanguageExtension.setText("cascading_dropdown", getString(R.string.cascading_dropdown)), R.drawable.ic_cascading));
        fieldList.add(new FieldItem(TYPE_QUIZ_MCQ, LanguageExtension.setText("mcq", getString(R.string.mcq)), R.drawable.ic_mcq));
        fieldList.add(new FieldItem(TYPE_QUIZ_TEXT, LanguageExtension.setText("question_with_marks", getString(R.string.question_with_marks)), R.drawable.ic_question_with_marks));

        adapter.notifyDataSetChanged();
    }

    public void sendResult(int fieldType, String label) {
        Log.e(TAG, "sendResult: fieldType -> " + fieldType);
        Bundle bundle = new Bundle();
        bundle.putInt("type", fieldType);
        bundle.putString("label", label);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
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
