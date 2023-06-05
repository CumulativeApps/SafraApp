package com.safra.utilities;

import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CHECKBOX_GROUP;
import static com.safra.utilities.FormElements.TYPE_DATE;
import static com.safra.utilities.FormElements.TYPE_EMAIL;
import static com.safra.utilities.FormElements.TYPE_FILE;
import static com.safra.utilities.FormElements.TYPE_HEADER;
import static com.safra.utilities.FormElements.TYPE_LOCATION;
import static com.safra.utilities.FormElements.TYPE_MONTH;
import static com.safra.utilities.FormElements.TYPE_NUMBER;
import static com.safra.utilities.FormElements.TYPE_PASSWORD;
import static com.safra.utilities.FormElements.TYPE_QUIZ_MCQ;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;
import static com.safra.utilities.FormElements.TYPE_RADIO_GROUP;
import static com.safra.utilities.FormElements.TYPE_SELECT;
import static com.safra.utilities.FormElements.TYPE_SEPARATOR;
import static com.safra.utilities.FormElements.TYPE_TEL;
import static com.safra.utilities.FormElements.TYPE_TEXT;
import static com.safra.utilities.FormElements.TYPE_TEXT_AREA;
import static com.safra.utilities.FormElements.TYPE_TIME;
import static com.safra.utilities.FormElements.TYPE_WEEK;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.R;
import com.safra.adapters.FormAdapter;
import com.safra.interfaces.FileSelectionInterface;
import com.safra.interfaces.OnFormElementValueChangedListener;
import com.safra.interfaces.OpenPropertiesInterface;
import com.safra.interfaces.RequestLocationInterface;
import com.safra.models.formElements.BaseFormElement;

import java.util.List;

public class FormBuilder {

    public static final String TAG = "form_builder";

    public FormAdapter formAdapter;

    public FormBuilder(Activity context, RecyclerView recyclerView) {
        initializeFormBuilderHelper(context, recyclerView);
    }

    public FormBuilder(Activity context, RecyclerView recyclerView,
                       OpenPropertiesInterface openPropertiesInterface) {
        initializeFormBuilderHelper(context, recyclerView, openPropertiesInterface);
    }

    public FormBuilder(Activity context, RecyclerView recyclerView,
                       OnFormElementValueChangedListener listener,
                       FileSelectionInterface fileSelectionInterface,
                       RequestLocationInterface requestLocationInterface) {
        initializeFormBuilderHelper(context, recyclerView, listener,
                fileSelectionInterface, requestLocationInterface);
    }

    private void initializeFormBuilderHelper(Activity context, RecyclerView recyclerView,
                                             OpenPropertiesInterface openPropertiesInterface) {

        this.formAdapter = new FormAdapter(context, openPropertiesInterface);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(context, RecyclerView.VERTICAL, 1, R.dimen._1dp, R.dimen._5dp, true));
        recyclerView.setAdapter(formAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                formAdapter.onItemMove(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void initializeFormBuilderHelper(Activity context,
                                             RecyclerView recyclerView) {

        this.formAdapter = new FormAdapter(context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(context, RecyclerView.VERTICAL, 1, R.dimen._1dp, R.dimen._5dp, true));
        recyclerView.setAdapter(formAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TEXT, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TEXT_AREA, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_NUMBER, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_DATE, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_EMAIL, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_FILE, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_LOCATION, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_MONTH, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_PASSWORD, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TEL, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TIME, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_WEEK, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_CHECKBOX_GROUP, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_RADIO_GROUP, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_SELECT, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_HEADER, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_SEPARATOR, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_CASCADING, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_QUIZ_MCQ, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_QUIZ_TEXT, 0);
    }

    private void initializeFormBuilderHelper(Activity context,
                                             RecyclerView recyclerView,
                                             OnFormElementValueChangedListener listener,
                                             FileSelectionInterface fileSelectionInterface,
                                             RequestLocationInterface requestLocationInterface) {

        this.formAdapter = new FormAdapter(context, listener, fileSelectionInterface, requestLocationInterface);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(context, RecyclerView.VERTICAL, 1, R.dimen._1dp, R.dimen._5dp, true));
        recyclerView.setAdapter(formAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TEXT, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TEXT_AREA, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_NUMBER, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_DATE, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_EMAIL, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_FILE, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_LOCATION, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_MONTH, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_PASSWORD, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TEL, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_TIME, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_WEEK, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_CHECKBOX_GROUP, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_RADIO_GROUP, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_SELECT, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_HEADER, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_SEPARATOR, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_CASCADING, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_QUIZ_MCQ, 0);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(TYPE_QUIZ_TEXT, 0);
    }

    public void addFormElements(List<BaseFormElement> baseFormElements) {
        this.formAdapter.addElements(baseFormElements);
    }

    public void addFormElement(BaseFormElement baseFormElement) {
        this.formAdapter.addElement(baseFormElement);
    }

    public BaseFormElement getFormElement(String fieldName) {
        return this.formAdapter.getValueAtName(fieldName);
    }

    public BaseFormElement getFormElement(int index) {
        return this.formAdapter.getValueAtIndex(index);
    }

    public void updateFormElement(BaseFormElement baseFormElement, int position) {
        Log.e(TAG, "updateFormElement: " + position);
        this.formAdapter.updateFormElement(baseFormElement, position);
    }

    public void updateFormElement(BaseFormElement baseFormElement, String fieldName) {
        Log.e(TAG, "updateFormElement: " + fieldName);
        this.formAdapter.updateFormElement(baseFormElement, fieldName);
    }

    public List<BaseFormElement> getFormElements() {
        return this.formAdapter.getElementList();
    }

    public int getTotalMarks() {
        return formAdapter.getTotalMarks();
    }

    public boolean isValidForm() {
        boolean isAllValid = true;
        for (int i = 0; i < this.formAdapter.getItemCount(); i++) {
            BaseFormElement baseFormElement = this.formAdapter.getValueAtIndex(i);
            if ((baseFormElement.isRequired() && TextUtils.isEmpty(baseFormElement.getFieldValue()))
                    || baseFormElement.isHaveError()) {
                baseFormElement.setHaveError(true);
                this.formAdapter.notifyItemChanged(i);
                isAllValid = false;
            }
        }
        return isAllValid;
    }

    public void clearFormElements() {
        this.formAdapter.clearElements();
    }

}
