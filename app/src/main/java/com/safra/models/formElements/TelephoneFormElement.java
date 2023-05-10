package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_TEL;

public class TelephoneFormElement extends BaseFormElement {

    public TelephoneFormElement() {
    }

    public static TelephoneFormElement createInstance(){
        TelephoneFormElement telephoneFormElement = new TelephoneFormElement();
        telephoneFormElement
                .setType(TYPE_TEL)
                .setFieldType("text")
                .setFieldSubType("tel")
                .setClassName("form-control");
        return telephoneFormElement;
    }

    public TelephoneFormElement setFieldName(String fieldName){
        return (TelephoneFormElement) super.setFieldName(fieldName);
    }

    public TelephoneFormElement setFieldLabel(String fieldLabel){
        return (TelephoneFormElement) super.setFieldLabel(fieldLabel);
    }

    public TelephoneFormElement setFieldValue(String fieldValue){
        return (TelephoneFormElement) super.setFieldValue(fieldValue);
    }

    public TelephoneFormElement setHaveAccess(boolean haveAccess){
        return (TelephoneFormElement) super.setHaveAccess(haveAccess);
    }

    public TelephoneFormElement setRole(Long[] role){
        return (TelephoneFormElement) super.setRole(role);
    }

    public TelephoneFormElement setRequired(boolean required){
        return (TelephoneFormElement) super.setRequired(required);
    }
}
