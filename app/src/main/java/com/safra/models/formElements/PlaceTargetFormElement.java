package com.safra.models.formElements;

import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_SELECT;

public class PlaceTargetFormElement extends BaseFormElement {

    public PlaceTargetFormElement() {
    }

    public static PlaceTargetFormElement createInstance(){
        PlaceTargetFormElement textFormElement = new PlaceTargetFormElement();
        textFormElement
                .setType(TYPE_SELECT)
                .setFieldType("select")
                .setClassName("form-control place_target");
        return textFormElement;
    }

    public PlaceTargetFormElement setFieldName(String fieldName){
        return (PlaceTargetFormElement) super.setFieldName(fieldName);
    }

    public PlaceTargetFormElement setFieldLabel(String fieldLabel){
        return (PlaceTargetFormElement) super.setFieldLabel(fieldLabel);
    }

    public PlaceTargetFormElement setFieldValue(String fieldValue){
        return (PlaceTargetFormElement) super.setFieldValue(fieldValue);
    }

    public PlaceTargetFormElement setHaveAccess(boolean haveAccess){
        return (PlaceTargetFormElement) super.setHaveAccess(haveAccess);
    }

    public PlaceTargetFormElement setRole(Long[] role){
        return (PlaceTargetFormElement) super.setRole(role);
    }

    public PlaceTargetFormElement setRequired(boolean required){
        return (PlaceTargetFormElement) super.setRequired(required);
    }

    public PlaceTargetFormElement setOptions(List<OptionItem> mOptions) {
        return (PlaceTargetFormElement) super.setOptions(mOptions);
    }
}
