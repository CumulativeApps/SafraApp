package com.safra.models.formElements;

import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_SELECT_BOXES_GROUP;

public class SelectBoxesGroupFormElement extends BaseFormElement {

    public SelectBoxesGroupFormElement() {
    }

    public static SelectBoxesGroupFormElement createInstance(){
        SelectBoxesGroupFormElement selectBoxesGroupFormElement = new SelectBoxesGroupFormElement();
        selectBoxesGroupFormElement
                .setType(TYPE_SELECT_BOXES_GROUP)
                .setFieldType("selectboxes");
        return selectBoxesGroupFormElement;
    }

    public SelectBoxesGroupFormElement setFieldName(String fieldName){
        return (SelectBoxesGroupFormElement) super.setFieldName(fieldName);
    }

    public SelectBoxesGroupFormElement setFieldLabel(String fieldLabel){
        return (SelectBoxesGroupFormElement) super.setFieldLabel(fieldLabel);
    }

    public SelectBoxesGroupFormElement setFieldValue(String fieldValue){
        return (SelectBoxesGroupFormElement) super.setFieldValue(fieldValue);
    }

    public SelectBoxesGroupFormElement setHaveAccess(boolean haveAccess){
        return (SelectBoxesGroupFormElement) super.setHaveAccess(haveAccess);
    }

    public SelectBoxesGroupFormElement setRole(Long[] role){
        return (SelectBoxesGroupFormElement) super.setRole(role);
    }

    public SelectBoxesGroupFormElement setRequired(boolean required){
        return (SelectBoxesGroupFormElement) super.setRequired(required);
    }

    public SelectBoxesGroupFormElement setOptions(List<OptionItem> mOptions) {
        return (SelectBoxesGroupFormElement) super.setOptions(mOptions);
    }
}
