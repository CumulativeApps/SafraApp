package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_PASSWORD;

public class PasswordFormElement extends BaseFormElement {

    public PasswordFormElement() {
    }

    public static PasswordFormElement createInstance(){
        PasswordFormElement passwordFormElement = new PasswordFormElement();
        passwordFormElement
                .setType(TYPE_PASSWORD)
                .setFieldType("password")
                .setClassName("form-control");
        return passwordFormElement;
    }

    public PasswordFormElement setFieldName(String fieldName){
        return (PasswordFormElement) super.setFieldName(fieldName);
    }

    public PasswordFormElement setFieldLabel(String fieldLabel){
        return (PasswordFormElement) super.setFieldLabel(fieldLabel);
    }

    public PasswordFormElement setFieldValue(String fieldValue){
        return (PasswordFormElement) super.setFieldValue(fieldValue);
    }

    public PasswordFormElement setHaveAccess(boolean haveAccess){
        return (PasswordFormElement) super.setHaveAccess(haveAccess);
    }

    public PasswordFormElement setRole(Long[] role){
        return (PasswordFormElement) super.setRole(role);
    }

    public PasswordFormElement setRequired(boolean required){
        return (PasswordFormElement) super.setRequired(required);
    }
}
