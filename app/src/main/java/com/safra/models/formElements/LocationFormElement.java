package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_LOCATION;

public class LocationFormElement extends BaseFormElement {

    public LocationFormElement() {
    }

    public static LocationFormElement createInstance(){
        LocationFormElement locationFormElement = new LocationFormElement();
        locationFormElement
                .setType(TYPE_LOCATION)
                .setFieldType("textfield")
                .setFieldSubType("text")
                .setClassName("form-control location");
        return locationFormElement;
    }

    public LocationFormElement setFieldName(String fieldName){
        return (LocationFormElement) super.setFieldName(fieldName);
    }

    public LocationFormElement setFieldLabel(String fieldLabel){
        return (LocationFormElement) super.setFieldLabel(fieldLabel);
    }

    public LocationFormElement setFieldValue(String fieldValue){
        return (LocationFormElement) super.setFieldValue(fieldValue);
    }

    public LocationFormElement setHaveAccess(boolean haveAccess){
        return (LocationFormElement) super.setHaveAccess(haveAccess);
    }

    public LocationFormElement setRole(Long[] role){
        return (LocationFormElement) super.setRole(role);
    }

    public LocationFormElement setRequired(boolean required){
        return (LocationFormElement) super.setRequired(required);
    }
}
