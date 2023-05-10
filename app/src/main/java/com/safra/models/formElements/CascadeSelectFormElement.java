package com.safra.models.formElements;

import com.safra.models.CascadeOptionItem;
import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CASCADING_SELECT;
import static com.safra.utilities.FormElements.TYPE_SELECT;

public class CascadeSelectFormElement extends BaseFormElement {

    public CascadeSelectFormElement() {
    }

    public static CascadeSelectFormElement createInstance(){
        CascadeSelectFormElement textFormElement = new CascadeSelectFormElement();
        textFormElement
                .setType(TYPE_CASCADING_SELECT)
                .setFieldType("select")
                .setClassName("form-control");
        return textFormElement;
    }

    public CascadeSelectFormElement setFieldName(String fieldName){
        return (CascadeSelectFormElement) super.setFieldName(fieldName);
    }

    public CascadeSelectFormElement setFieldLabel(String fieldLabel){
        return (CascadeSelectFormElement) super.setFieldLabel(fieldLabel);
    }

    public CascadeSelectFormElement setFieldValue(String fieldValue){
        return (CascadeSelectFormElement) super.setFieldValue(fieldValue);
    }

    public CascadeSelectFormElement setHaveAccess(boolean haveAccess){
        return (CascadeSelectFormElement) super.setHaveAccess(haveAccess);
    }

    public CascadeSelectFormElement setRole(Long[] role){
        return (CascadeSelectFormElement) super.setRole(role);
    }

    public CascadeSelectFormElement setRequired(boolean required){
        return (CascadeSelectFormElement) super.setRequired(required);
    }

    public CascadeSelectFormElement setCascadeOptions(List<CascadeOptionItem> mOptions) {
        return (CascadeSelectFormElement) super.setCascadeOptions(mOptions);
    }

    public CascadeSelectFormElement setParentId(int parentId){
        return (CascadeSelectFormElement) super.setParentId(parentId);
    }
}
