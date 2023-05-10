package com.safra.models.formElements;

import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_CHECKBOX_GROUP;

public class CheckboxGroupFormElement extends BaseFormElement {

    public CheckboxGroupFormElement() {
    }

    public static CheckboxGroupFormElement createInstance(){
        CheckboxGroupFormElement checkboxGroupFormElement = new CheckboxGroupFormElement();
        checkboxGroupFormElement
                .setType(TYPE_CHECKBOX_GROUP)
                .setFieldType("checkbox-group");
        return checkboxGroupFormElement;
    }

    public CheckboxGroupFormElement setFieldName(String fieldName){
        return (CheckboxGroupFormElement) super.setFieldName(fieldName);
    }

    public CheckboxGroupFormElement setFieldLabel(String fieldLabel){
        return (CheckboxGroupFormElement) super.setFieldLabel(fieldLabel);
    }

    public CheckboxGroupFormElement setFieldValue(String fieldValue){
        return (CheckboxGroupFormElement) super.setFieldValue(fieldValue);
    }

    public CheckboxGroupFormElement setHaveAccess(boolean haveAccess){
        return (CheckboxGroupFormElement) super.setHaveAccess(haveAccess);
    }

    public CheckboxGroupFormElement setRole(Long[] role){
        return (CheckboxGroupFormElement) super.setRole(role);
    }

    public CheckboxGroupFormElement setRequired(boolean required){
        return (CheckboxGroupFormElement) super.setRequired(required);
    }

    public CheckboxGroupFormElement setOptions(List<OptionItem> mOptions) {
        return (CheckboxGroupFormElement) super.setOptions(mOptions);
    }
}
