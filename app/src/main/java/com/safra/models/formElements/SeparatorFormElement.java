package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_SEPARATOR;

public class SeparatorFormElement extends BaseFormElement {

    public SeparatorFormElement() {
    }

    public static SeparatorFormElement createInstance(){
        SeparatorFormElement textFormElement = new SeparatorFormElement();
        textFormElement
                .setType(TYPE_SEPARATOR)
                .setFieldType("break");
        return textFormElement;
    }

    public SeparatorFormElement setFieldLabel(String fieldLabel){
        return (SeparatorFormElement) super.setFieldLabel(fieldLabel);
    }

    public SeparatorFormElement setHaveAccess(boolean haveAccess){
        return (SeparatorFormElement) super.setHaveAccess(haveAccess);
    }

    public SeparatorFormElement setRole(Long[] role){
        return (SeparatorFormElement) super.setRole(role);
    }

    public SeparatorFormElement setFieldName(String fieldName){
        return (SeparatorFormElement) super.setFieldName(fieldName);
    }
}
