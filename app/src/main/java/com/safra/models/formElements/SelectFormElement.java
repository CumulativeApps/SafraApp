package com.safra.models.formElements;

import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_SELECT;

public class SelectFormElement extends BaseFormElement {

    public SelectFormElement() {
    }

    public static SelectFormElement createInstance(){
        SelectFormElement textFormElement = new SelectFormElement();
        textFormElement
                .setType(TYPE_SELECT)
                .setFieldType("select")
                .setClassName("form-control");
        return textFormElement;
    }

    public SelectFormElement setFieldName(String fieldName){
        return (SelectFormElement) super.setFieldName(fieldName);
    }

    public SelectFormElement setFieldLabel(String fieldLabel){
        return (SelectFormElement) super.setFieldLabel(fieldLabel);
    }

    public SelectFormElement setFieldValue(String fieldValue){
        return (SelectFormElement) super.setFieldValue(fieldValue);
    }

    public SelectFormElement setHaveAccess(boolean haveAccess){
        return (SelectFormElement) super.setHaveAccess(haveAccess);
    }

    public SelectFormElement setRole(Long[] role){
        return (SelectFormElement) super.setRole(role);
    }

    public SelectFormElement setRequired(boolean required){
        return (SelectFormElement) super.setRequired(required);
    }

    public SelectFormElement setOptions(List<OptionItem> mOptions) {
        return (SelectFormElement) super.setOptions(mOptions);
    }
}
