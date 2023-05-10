package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_NUMBER;

public class UnitPriceFormElement extends BaseFormElement {

    public UnitPriceFormElement() {
    }

    public static UnitPriceFormElement createInstance(){
        UnitPriceFormElement numberFormElement = new UnitPriceFormElement();
        numberFormElement
                .setType(TYPE_NUMBER)
                .setFieldType("number")
                .setClassName("form-control unit_price");
        return numberFormElement;
    }

    public UnitPriceFormElement setFieldName(String fieldName){
        return (UnitPriceFormElement) super.setFieldName(fieldName);
    }

    public UnitPriceFormElement setFieldLabel(String fieldLabel){
        return (UnitPriceFormElement) super.setFieldLabel(fieldLabel);
    }

    public UnitPriceFormElement setFieldValue(String fieldValue){
        return (UnitPriceFormElement) super.setFieldValue(fieldValue);
    }

    public UnitPriceFormElement setMax(String max){
        return (UnitPriceFormElement) super.setMax(max);
    }

    public UnitPriceFormElement setMin(String min){
        return (UnitPriceFormElement) super.setMin(min);
    }

    public UnitPriceFormElement setStep(int step){
        return (UnitPriceFormElement) super.setStep(step);
    }

    public UnitPriceFormElement setHaveAccess(boolean haveAccess){
        return (UnitPriceFormElement) super.setHaveAccess(haveAccess);
    }

    public UnitPriceFormElement setRole(Long[] role){
        return (UnitPriceFormElement) super.setRole(role);
    }

    public UnitPriceFormElement setRequired(boolean required){
        return (UnitPriceFormElement) super.setRequired(required);
    }
}
