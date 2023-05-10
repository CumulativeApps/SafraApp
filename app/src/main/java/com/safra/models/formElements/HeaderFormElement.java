package com.safra.models.formElements;

import com.safra.viewholders.HeaderFieldViewHolder;

import static com.safra.utilities.FormElements.TYPE_HEADER;

public class HeaderFormElement extends BaseFormElement {

    public HeaderFormElement() {
    }

    public static HeaderFormElement createInstance(){
        HeaderFormElement textFormElement = new HeaderFormElement();
        textFormElement
                .setType(TYPE_HEADER)
                .setFieldType("header");
        return textFormElement;
    }

    public HeaderFormElement setFieldLabel(String fieldLabel){
        return (HeaderFormElement) super.setFieldLabel(fieldLabel);
    }

    public HeaderFormElement setFieldName(String fieldName){
        return (HeaderFormElement) super.setFieldName(fieldName);
    }

    public HeaderFormElement setHaveAccess(boolean haveAccess){
        return (HeaderFormElement) super.setHaveAccess(haveAccess);
    }

    public HeaderFormElement setRole(Long[] role){
        return (HeaderFormElement) super.setRole(role);
    }

    public HeaderFormElement setFieldSubType(String subType){
        return (HeaderFormElement) super.setFieldSubType(subType);
    }
}
