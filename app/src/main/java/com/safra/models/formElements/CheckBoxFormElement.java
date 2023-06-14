package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_CHECKBOX;


public class CheckBoxFormElement extends BaseFormElement {

    public CheckBoxFormElement() {
    }

    public static CheckBoxFormElement createInstance(){
        CheckBoxFormElement textFormElement = new CheckBoxFormElement();
        textFormElement
                .setType(TYPE_CHECKBOX)
                .setFieldType("checkbox");
        return textFormElement;
    }

    public CheckBoxFormElement setFieldName(String fieldName){
        return (CheckBoxFormElement) super.setFieldName(fieldName);
    }

    public CheckBoxFormElement setFieldLabel(String fieldLabel){
        return (CheckBoxFormElement) super.setFieldLabel(fieldLabel);
    }

    public CheckBoxFormElement setFieldValue(String fieldValue){
        return (CheckBoxFormElement) super.setFieldValue(fieldValue);
    }

    public CheckBoxFormElement setHaveAccess(boolean haveAccess){
        return (CheckBoxFormElement) super.setHaveAccess(haveAccess);
    }

    public CheckBoxFormElement setRole(Long[] role){
        return (CheckBoxFormElement) super.setRole(role);
    }

    public CheckBoxFormElement setRequired(boolean required){
        return (CheckBoxFormElement) super.setRequired(required);
    }


}
