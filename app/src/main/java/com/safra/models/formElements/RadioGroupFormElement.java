package com.safra.models.formElements;

import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_RADIO_GROUP;

public class RadioGroupFormElement extends BaseFormElement {

    public RadioGroupFormElement() {
    }

    public static RadioGroupFormElement createInstance(){
        RadioGroupFormElement radioGroupFormElement = new RadioGroupFormElement();
        radioGroupFormElement
                .setType(TYPE_RADIO_GROUP)
                .setFieldType("radio-group");
        return radioGroupFormElement;
    }

    public RadioGroupFormElement setFieldName(String fieldName){
        return (RadioGroupFormElement) super.setFieldName(fieldName);
    }

    public RadioGroupFormElement setFieldLabel(String fieldLabel){
        return (RadioGroupFormElement) super.setFieldLabel(fieldLabel);
    }

    public RadioGroupFormElement setFieldValue(String fieldValue){
        return (RadioGroupFormElement) super.setFieldValue(fieldValue);
    }

    public RadioGroupFormElement setHaveAccess(boolean haveAccess){
        return (RadioGroupFormElement) super.setHaveAccess(haveAccess);
    }

    public RadioGroupFormElement setRole(Long[] role){
        return (RadioGroupFormElement) super.setRole(role);
    }

    public RadioGroupFormElement setRequired(boolean required){
        return (RadioGroupFormElement) super.setRequired(required);
    }

    public RadioGroupFormElement setOptions(List<OptionItem> mOptions) {
        return (RadioGroupFormElement) super.setOptions(mOptions);
    }

}
