package com.safra.models.formElements;


import static com.safra.utilities.FormElements.TYPE_ADDRESS;

public class AddressFormElement extends BaseFormElement {

    public AddressFormElement() {
    }

    public static AddressFormElement createInstance(){
        AddressFormElement textFormElement = new AddressFormElement();
        textFormElement
                .setType(TYPE_ADDRESS)
                .setFieldType("address");
        return textFormElement;
    }

    public AddressFormElement setFieldName(String fieldName){
        return (AddressFormElement) super.setFieldName(fieldName);
    }

    public AddressFormElement setFieldLabel(String fieldLabel){
        return (AddressFormElement) super.setFieldLabel(fieldLabel);
    }

    public AddressFormElement setFieldValue(String fieldValue){
        return (AddressFormElement) super.setFieldValue(fieldValue);
    }

    public AddressFormElement setHaveAccess(boolean haveAccess){
        return (AddressFormElement) super.setHaveAccess(haveAccess);
    }

    public AddressFormElement setRole(Long[] role){
        return (AddressFormElement) super.setRole(role);
    }

    public AddressFormElement setRequired(boolean required){
        return (AddressFormElement) super.setRequired(required);
    }
}
