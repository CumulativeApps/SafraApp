package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_EMAIL;

public class EmailFormElement extends BaseFormElement {

    public EmailFormElement() {
    }

    public static EmailFormElement createInstance(){
        EmailFormElement emailFormElement = new EmailFormElement();
        emailFormElement
                .setType(TYPE_EMAIL)
                .setFieldType("text")
                .setFieldSubType("email")
                .setClassName("form-control");
        return emailFormElement;
    }

    public EmailFormElement setFieldName(String fieldName){
        return (EmailFormElement) super.setFieldName(fieldName);
    }

    public EmailFormElement setFieldLabel(String fieldLabel){
        return (EmailFormElement) super.setFieldLabel(fieldLabel);
    }

    public EmailFormElement setFieldValue(String fieldValue){
        return (EmailFormElement) super.setFieldValue(fieldValue);
    }

    public EmailFormElement setHaveAccess(boolean haveAccess){
        return (EmailFormElement) super.setHaveAccess(haveAccess);
    }

    public EmailFormElement setRole(Long[] role){
        return (EmailFormElement) super.setRole(role);
    }

    public EmailFormElement setRequired(boolean required){
        return (EmailFormElement) super.setRequired(required);
    }
}
