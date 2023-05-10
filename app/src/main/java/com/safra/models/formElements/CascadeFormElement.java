package com.safra.models.formElements;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_CASCADING;

public class CascadeFormElement extends BaseFormElement {

    public CascadeFormElement() {
    }

    public static CascadeFormElement createInstance(){
        CascadeFormElement textFormElement = new CascadeFormElement();
        textFormElement
                .setType(TYPE_CASCADING)
                .setFieldType("hidden");
        return textFormElement;
    }

    public CascadeFormElement setFieldName(String fieldName){
        return (CascadeFormElement) super.setFieldName(fieldName);
    }

    public CascadeFormElement setFieldLabel(String fieldLabel){
        return (CascadeFormElement) super.setFieldLabel(fieldLabel);
    }

    public CascadeFormElement setHaveAccess(boolean haveAccess){
        return (CascadeFormElement) super.setHaveAccess(haveAccess);
    }

    public CascadeFormElement setRole(Long[] role){
        return (CascadeFormElement) super.setRole(role);
    }

    public CascadeFormElement setRequired(boolean required){
        return (CascadeFormElement) super.setRequired(required);
    }

    public CascadeFormElement setListOfFormElement(List<BaseFormElement> elementList){
        return (CascadeFormElement) super.setElementList(elementList);
    }

    public CascadeFormElement setJsonArrayOfElements(String jsonArrayOfElements){
        return (CascadeFormElement) super.setElementJsonArray(jsonArrayOfElements);
    }
}
