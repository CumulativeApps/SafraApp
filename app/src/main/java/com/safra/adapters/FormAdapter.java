package com.safra.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safra.databinding.FormElementCascadingBinding;
import com.safra.databinding.FormElementCheckboxBinding;
import com.safra.databinding.FormElementCheckboxGroupBinding;
import com.safra.databinding.FormElementDateBinding;
import com.safra.databinding.FormElementEmailBinding;
import com.safra.databinding.FormElementFileBinding;
import com.safra.databinding.FormElementHeaderBinding;
import com.safra.databinding.FormElementLocationBinding;
import com.safra.databinding.FormElementMonthBinding;
import com.safra.databinding.FormElementNumberBinding;
import com.safra.databinding.FormElementPasswordBinding;
import com.safra.databinding.FormElementQuizTextBinding;
import com.safra.databinding.FormElementRadioGroupBinding;
import com.safra.databinding.FormElementSelectBinding;
import com.safra.databinding.FormElementSeparatorBinding;
import com.safra.databinding.FormElementTelephoneBinding;
import com.safra.databinding.FormElementTextBinding;
import com.safra.databinding.FormElementTextareaBinding;
import com.safra.databinding.FormElementTimeBinding;
import com.safra.databinding.FormElementWeekBinding;
import com.safra.events.FieldListChangedEvent;
import com.safra.interfaces.FileSelectionInterface;
import com.safra.interfaces.HandlerClickListener;
import com.safra.interfaces.OnFormElementValueChangedListener;
import com.safra.interfaces.OpenPropertiesInterface;
import com.safra.interfaces.ReloadListener;
import com.safra.interfaces.RequestLocationInterface;
import com.safra.models.OptionItem;
import com.safra.models.formElements.BaseFormElement;
import com.safra.viewholders.BaseFieldViewHolder;
import com.safra.viewholders.CascadingFieldViewHolder;
import com.safra.viewholders.CheckBoxFieldViewHolder;
import com.safra.viewholders.SelectBoxesFieldViewHolder;
import com.safra.viewholders.DateFieldViewHolder;
import com.safra.viewholders.EmailFieldViewHolder;
import com.safra.viewholders.FileFieldViewHolder;
import com.safra.viewholders.HeaderFieldViewHolder;
import com.safra.viewholders.LocationFieldViewHolder;
import com.safra.viewholders.MonthFieldViewHolder;
import com.safra.viewholders.NumberFieldViewHolder;
import com.safra.viewholders.PasswordFieldViewHolder;
import com.safra.viewholders.QuizTextViewHolder;
import com.safra.viewholders.RadioFieldViewHolder;
import com.safra.viewholders.SelectFieldViewHolder;
import com.safra.viewholders.SeparatorFieldViewHolder;
import com.safra.viewholders.TelephoneFieldViewHolder;
import com.safra.viewholders.TextAreaFieldViewHolder;
import com.safra.viewholders.TextFieldViewHolder;
import com.safra.viewholders.TimeFieldViewHolder;
import com.safra.viewholders.DateTimeFieldViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.safra.utilities.FormElements.TYPE_ACHIEVED_UNIT;
import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CASCADING_SELECT;
import static com.safra.utilities.FormElements.TYPE_SELECT_BOXES_GROUP;
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

import org.greenrobot.eventbus.EventBus;

public class FormAdapter
        extends RecyclerView.Adapter<BaseFieldViewHolder>
        implements ReloadListener, HandlerClickListener {

    public static final String TAG = "form_adapter";

    private final Activity context;
    private List<BaseFormElement> elementList;

    private final OnFormElementValueChangedListener listener;
    private final OpenPropertiesInterface propertiesListener;
    private final FileSelectionInterface fileSelectionListener;
    private final RequestLocationInterface requestLocationListener;

    private final boolean isPreview;
    private final boolean isReadOnly;

    // Constructor for editable form
    public FormAdapter(Activity context, OpenPropertiesInterface propertiesListener) {
        this.context = context;
        this.elementList = new ArrayList<>();
        this.listener = null;
        this.isPreview = false;
        this.isReadOnly = false;
        this.propertiesListener = propertiesListener;
        this.fileSelectionListener = null;
        this.requestLocationListener = null;
    }

    // Constructor for viewing form response
    public FormAdapter(Activity context) {
        this.context = context;
        this.elementList = new ArrayList<>();
        this.listener = null;
        this.isPreview = true;
        this.isReadOnly = true;
        this.propertiesListener = null;
        this.fileSelectionListener = null;
        this.requestLocationListener = null;
    }

    // Constructor for fill form response or view form
    public FormAdapter(Activity context, OnFormElementValueChangedListener listener,
                       FileSelectionInterface fileSelectionListener,
                       RequestLocationInterface requestLocationListener) {
        this.context = context;
        this.elementList = new ArrayList<>();
        this.listener = listener;
        this.isPreview = true;
        this.isReadOnly = false;
        this.propertiesListener = null;
        this.fileSelectionListener = fileSelectionListener;
        this.requestLocationListener = requestLocationListener;
    }

    public void addElements(List<BaseFormElement> formElements) {
        this.elementList = formElements;
        notifyDataSetChanged();
        Log.e(TAG, "addElements: replacing list with new list");
    }

    public void addElement(BaseFormElement formElement) {
        this.elementList.add(formElement);
//        if (elementList.size() == 1)
        notifyDataSetChanged();
//        else
//            notifyItemInserted(elementList.size() - 1);
        Log.e(TAG, "addElement: adding form element");
    }

    public void addElement(BaseFormElement formElement, int position) {
        this.elementList.add(position, formElement);
        notifyDataSetChanged();
        Log.e(TAG, "addElement: adding form element at " + position);
    }

    public void clearElements(){
        this.elementList.clear();
        notifyDataSetChanged();
    }

    public void setValueAtIndex(int position, String value) {
        BaseFormElement baseFormElement = elementList.get(position);
        baseFormElement.setFieldValue(value);
        notifyItemChanged(position);
        Log.e(TAG, "setValueAtIndex: set value for element at " + position);
    }

    public void setValueAtName(String fieldName, String value) {
        int position = -1;
        for (int i = 0; i < elementList.size(); i++) {
            if (elementList.get(i).getFieldName().equals(fieldName)) {
                elementList.get(i).setFieldValue(value);
                position = i;
                break;
            }
        }
        notifyItemChanged(position);
        Log.e(TAG, "setValueAtName: set value for element name with " + fieldName);
    }

    public BaseFormElement getValueAtIndex(int index) {
        return elementList.get(index);
    }

    public BaseFormElement getValueAtName(String fieldName) {
        for (BaseFormElement e : elementList) {
            if (e.getFieldName().equals(fieldName)) {
                return e;
            }
        }
        return null;
    }

    public List<BaseFormElement> getElementList() {
        return elementList;
    }

    public OnFormElementValueChangedListener getValueChangedListener() {
        return listener;
    }

    @Override
    public int getItemViewType(int position) {
        return elementList.get(position).getType();
    }

    @NonNull
    @Override
    public BaseFieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.e(TAG, "onCreateViewHolder: " + viewType);
        switch (viewType) {
            case TYPE_TEXT_AREA:
                FormElementTextareaBinding textAreaBinding = FormElementTextareaBinding.inflate(inflater, parent, false);
                return new TextAreaFieldViewHolder(textAreaBinding, this, isPreview, isReadOnly);
            case TYPE_EMAIL:
                FormElementEmailBinding emailBinding = FormElementEmailBinding.inflate(inflater, parent, false);
                return new EmailFieldViewHolder(emailBinding, this, isPreview, isReadOnly);
            case TYPE_PASSWORD:
                FormElementPasswordBinding passwordBinding = FormElementPasswordBinding.inflate(inflater, parent, false);
                return new PasswordFieldViewHolder(passwordBinding, this, isPreview, isReadOnly);
            case TYPE_TEL:
                FormElementTelephoneBinding telephoneBinding = FormElementTelephoneBinding.inflate(inflater, parent, false);
                return new TelephoneFieldViewHolder(telephoneBinding, this, isPreview, isReadOnly);
            case TYPE_NUMBER:
                FormElementTelephoneBinding numberrBinding = FormElementTelephoneBinding.inflate(inflater, parent, false);
                return new TelephoneFieldViewHolder(numberrBinding, this, isPreview, isReadOnly);
            case TYPE_ACHIEVED_UNIT:
            case TYPE_UNIT_PRICE:
                FormElementNumberBinding numberBinding = FormElementNumberBinding.inflate(inflater, parent, false);
                return new NumberFieldViewHolder(numberBinding, this, isPreview, isReadOnly);
            case TYPE_DATE:
                FormElementDateBinding dateBinding = FormElementDateBinding.inflate(inflater, parent, false);
                return new DateFieldViewHolder(dateBinding, this, this, isPreview, isReadOnly);
            case TYPE_TIME:
                FormElementTimeBinding timeBinding = FormElementTimeBinding.inflate(inflater, parent, false);
                return new TimeFieldViewHolder(timeBinding, this, this, isPreview, isReadOnly);
            case TYPE_DATETIME:
                FormElementWeekBinding weekBinding = FormElementWeekBinding.inflate(inflater, parent, false);
                return new DateTimeFieldViewHolder(weekBinding, this, this, isPreview, isReadOnly);
            case TYPE_MONTH:
                FormElementMonthBinding monthBinding = FormElementMonthBinding.inflate(inflater, parent, false);
                return new MonthFieldViewHolder(monthBinding, this, this, isPreview, isReadOnly);
            case TYPE_RADIO_GROUP:
            case TYPE_SURVEY:
            case TYPE_QUIZ_MCQ:
                FormElementRadioGroupBinding radioBinding = FormElementRadioGroupBinding.inflate(inflater, parent, false);
                return new RadioFieldViewHolder(radioBinding, this, this, isPreview, isReadOnly);
            case TYPE_SELECT_BOXES_GROUP:
                FormElementCheckboxGroupBinding selectBoxBinding = FormElementCheckboxGroupBinding.inflate(inflater, parent, false);
                return new SelectBoxesFieldViewHolder(selectBoxBinding, this, this, isPreview, isReadOnly);
            case TYPE_SELECT:
            case TYPE_PLACE_TARGET:
                FormElementSelectBinding selectBinding = FormElementSelectBinding.inflate(inflater, parent, false);
                return new SelectFieldViewHolder(selectBinding, this, this, isPreview, isReadOnly);
            case TYPE_FILE:
                FormElementFileBinding fileBinding = FormElementFileBinding.inflate(inflater, parent, false);
                return new FileFieldViewHolder(fileBinding, this, this, isPreview, isReadOnly, fileSelectionListener);
            case TYPE_LOCATION:
                FormElementLocationBinding locationBinding = FormElementLocationBinding.inflate(inflater, parent, false);
                return new LocationFieldViewHolder(locationBinding, this, this, isPreview, isReadOnly, requestLocationListener);
            case TYPE_HEADER:
                FormElementHeaderBinding headerBinding = FormElementHeaderBinding.inflate(inflater, parent, false);
                return new HeaderFieldViewHolder(headerBinding, this, isPreview);
            case TYPE_SEPARATOR:
                FormElementSeparatorBinding separatorBinding = FormElementSeparatorBinding.inflate(inflater, parent, false);
                return new SeparatorFieldViewHolder(separatorBinding, this, isPreview);
            case TYPE_CASCADING:
                FormElementCascadingBinding cascadingBinding = FormElementCascadingBinding.inflate(inflater, parent, false);
                return new CascadingFieldViewHolder(cascadingBinding, this, isPreview, isReadOnly);
            case TYPE_QUIZ_TEXT:
                FormElementQuizTextBinding quizTextBinding = FormElementQuizTextBinding.inflate(inflater, parent, false);
                return new QuizTextViewHolder(quizTextBinding, this, isPreview, isReadOnly);
            case TYPE_TEXT:
            case TYPE_CHECKBOX:
                FormElementCheckboxBinding checkboxBinding = FormElementCheckboxBinding.inflate(inflater, parent, false);
                return new CheckBoxFieldViewHolder(checkboxBinding, this, isPreview, isReadOnly);
            case TYPE_URL:
            default:
                FormElementTextBinding binding = FormElementTextBinding.inflate(inflater, parent, false);
                return new TextFieldViewHolder(binding, this, isPreview, isReadOnly);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseFieldViewHolder holder, int position) {
        if (holder.getListener() != null) {
            holder.getListener().updatePosition(holder.getAbsoluteAdapterPosition());
        }

        BaseFormElement formElement = elementList.get(position);
        holder.bind(context, formElement);
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    @Override
    public void updateValue(String fieldValue, int position) {
        BaseFormElement baseFormElement = elementList.get(position);
        Log.e("FormAdapter", "updateValue: " + baseFormElement + "->" + fieldValue);
        switch (baseFormElement.getType()) {
            case TYPE_SELECT_BOXES_GROUP:
                boolean isError = true;
                StringBuilder sb = new StringBuilder();
                for (OptionItem o : baseFormElement.getOptions()) {
                    if (o.isSelected()) {
                        isError = false;
                        if (sb.toString().isEmpty()) {
                            sb.append(o.getOptionValue());
                        } else {
                            sb.append(",").append(o.getOptionValue());
                        }
                    }
                }
                baseFormElement.setFieldValue(sb.toString());
                if (baseFormElement.isRequired()) {
                    baseFormElement.setHaveError(isError);
                }
                notifyItemChanged(position);
                break;
            case TYPE_RADIO_GROUP:
            case TYPE_SURVEY:
            case TYPE_QUIZ_MCQ:
            case TYPE_SELECT:
            case TYPE_CASCADING_SELECT:
            case TYPE_PLACE_TARGET:
                baseFormElement.setFieldValue(fieldValue);
                baseFormElement.setHaveError(baseFormElement.isRequired() && TextUtils.isEmpty(fieldValue));
                notifyItemChanged(position);
                break;
            case TYPE_TEXT:
            case TYPE_CHECKBOX:
            case TYPE_URL:
            case TYPE_TEXT_AREA:
            case TYPE_DATE:
            case TYPE_TIME:
            case TYPE_DATETIME:
            case TYPE_MONTH:
            case TYPE_NUMBER:
            case TYPE_FILE:
            case TYPE_ACHIEVED_UNIT:
            case TYPE_UNIT_PRICE:
            default:
                baseFormElement.setFieldValue(fieldValue);
                notifyItemChanged(position);
                if (listener != null)
                    listener.onValueChanged(elementList.get(position));
        }

    }

    public void onItemMove(int initialPosition, int finalPosition) {
        if (initialPosition < elementList.size() && finalPosition < elementList.size()) {
            if (initialPosition < finalPosition) {
                for (int i = initialPosition; i < finalPosition; i++) {
                    Collections.swap(elementList, i, i + 1);
                }
            } else {
                for (int i = initialPosition; i > finalPosition; i--) {
                    Collections.swap(elementList, i, i - 1);
                }
            }
        }
        notifyItemMoved(initialPosition, finalPosition);
    }

    @Override
    public void openProperties(BaseFormElement baseFormElement, int position, int childPosition) {
        if (propertiesListener != null)
            propertiesListener.openPropertiesDialog(baseFormElement, position, childPosition);
    }

    @Override
    public void duplicateItem(BaseFormElement baseFormElement, int position) {
        if (elementList.get(position) == baseFormElement) {
            Log.e(TAG, "duplicateItem: " + baseFormElement);
            try {
                BaseFormElement baseFormElement1 = baseFormElement.clone();
                long currentTime = Calendar.getInstance().getTimeInMillis();
                Log.e(TAG, "duplicateItem: currentTime -> " + currentTime);
                if (baseFormElement1.getType() == TYPE_CASCADING) {
                    String n = baseFormElement1.getFieldName().substring(0, baseFormElement1.getFieldName().lastIndexOf("_") + 1);
                    baseFormElement1.setFieldName(n + currentTime);
                    baseFormElement1.setClassName("form-control cascading_select cs_" + currentTime);

                    List<BaseFormElement> newElementList = new ArrayList<>();
                    for (BaseFormElement bfe : baseFormElement.getElementList()) {
                        BaseFormElement bfe1 = bfe.clone();
                        bfe1.setFieldName("select-" + Calendar.getInstance().getTimeInMillis())
                                .setClassName(bfe.getClassName().substring(0, bfe.getClassName().lastIndexOf("_") + 1) + currentTime);
                        newElementList.add(bfe1);
                    }
                    baseFormElement1.setElementList(newElementList);

                } else if (baseFormElement1.getType() == TYPE_QUIZ_TEXT) {
                    String newClassName = "form-control mcq mcq-text mcq_" + currentTime;
                    List<BaseFormElement> newElementList = new ArrayList<>();
                    for(BaseFormElement bfeOld : baseFormElement.getElementList()){
                        BaseFormElement bfeChild = bfeOld.clone();
                        bfeChild.setClassName(newClassName);
                        bfeChild.setFieldName(bfeOld.getFieldName().substring(0, bfeOld.getFieldName().lastIndexOf("-")+1)
                                + Calendar.getInstance().getTimeInMillis());
                        newElementList.add(bfeChild);
                    }
                    baseFormElement1.setElementList(newElementList);
                } else {
                    String n = baseFormElement1.getFieldName().substring(0, baseFormElement1.getFieldName().indexOf("-") + 1);
                    baseFormElement1.setFieldName(n + currentTime);
                }

                addElement(baseFormElement1, position + 1);
                EventBus.getDefault().post(new FieldListChangedEvent());
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, "duplicateItem: " + e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void deleteItem(BaseFormElement baseFormElement, int position) {
        if (elementList.get(position) == baseFormElement) {
            elementList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position - 1);

            EventBus.getDefault().post(new FieldListChangedEvent());
        }
    }

    public void updateFormElement(BaseFormElement baseFormElement, int position) {
        elementList.set(position, baseFormElement);
        notifyItemChanged(position);
    }

    public void updateFormElement(BaseFormElement baseFormElement, String fieldName) {
        for (int i = 0; i < elementList.size(); i++) {
            if (elementList.get(i).getFieldName().equals(fieldName)) {
                Log.e(TAG, "updateFormElement: " + i);
                elementList.set(i, baseFormElement);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public int getTotalMarks(){
        int totalMarks = 0;
        for(BaseFormElement bfe : elementList){
            if(bfe.getType() == TYPE_QUIZ_MCQ){
                int maxMark = 0;
                for(OptionItem oi : bfe.getOptions()){
                    if(Integer.parseInt(oi.getOptionValue()) > maxMark)
                        maxMark = Integer.parseInt(oi.getOptionValue());
                }

                totalMarks += maxMark;
            } else if(bfe.getType() == TYPE_QUIZ_TEXT){
                List<BaseFormElement> elementList = bfe.getElementList();
                for(BaseFormElement childBfe : elementList){
                    if(childBfe.getType() == TYPE_QUIZ_TEXT_POINT)
                        totalMarks += Integer.parseInt(childBfe.getMax());
                }
            }
        }

        return totalMarks;
    }
}
