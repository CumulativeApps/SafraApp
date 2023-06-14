package com.safra.models.formElements;


import static com.safra.utilities.FormElements.TYPE_URL;

public class UrlFormElement extends BaseFormElement {

    public UrlFormElement() {
    }

    public static UrlFormElement createInstance(){
        UrlFormElement urlFormElement = new UrlFormElement();
        urlFormElement
                .setType(TYPE_URL)
                .setFieldType("url")
                .setFieldSubType("text")
                .setClassName("form-control");
        return urlFormElement;
    }

    public UrlFormElement setFieldName(String fieldName){
        return (UrlFormElement) super.setFieldName(fieldName);
    }

    public UrlFormElement setFieldLabel(String fieldLabel){
        return (UrlFormElement) super.setFieldLabel(fieldLabel);
    }

    public UrlFormElement setFieldValue(String fieldValue){
        return (UrlFormElement) super.setFieldValue(fieldValue);
    }

    public UrlFormElement setHaveAccess(boolean haveAccess){
        return (UrlFormElement) super.setHaveAccess(haveAccess);
    }

    public UrlFormElement setRole(Long[] role){
        return (UrlFormElement) super.setRole(role);
    }

    public UrlFormElement setRequired(boolean required){
        return (UrlFormElement) super.setRequired(required);
    }
}

