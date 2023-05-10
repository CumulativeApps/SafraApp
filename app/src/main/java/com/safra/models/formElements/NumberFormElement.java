package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_NUMBER;

public class NumberFormElement extends BaseFormElement {

    public NumberFormElement() {
    }

    public static NumberFormElement createInstance(){
        NumberFormElement numberFormElement = new NumberFormElement();
        numberFormElement
                .setType(TYPE_NUMBER)
                .setFieldType("number")
                .setClassName("form-control");
        return numberFormElement;
    }

    public NumberFormElement setFieldName(String fieldName){
        return (NumberFormElement) super.setFieldName(fieldName);
    }

    public NumberFormElement setFieldLabel(String fieldLabel){
        return (NumberFormElement) super.setFieldLabel(fieldLabel);
    }

    public NumberFormElement setFieldValue(String fieldValue){
        return (NumberFormElement) super.setFieldValue(fieldValue);
    }

    public NumberFormElement setMax(String max){
        return (NumberFormElement) super.setMax(max);
    }

    public NumberFormElement setMin(String min){
        return (NumberFormElement) super.setMin(min);
    }

    public NumberFormElement setStep(int step){
        return (NumberFormElement) super.setStep(step);
    }

    public NumberFormElement setHaveAccess(boolean haveAccess){
        return (NumberFormElement) super.setHaveAccess(haveAccess);
    }

    public NumberFormElement setRole(Long[] role){
        return (NumberFormElement) super.setRole(role);
    }

    public NumberFormElement setRequired(boolean required){
        return (NumberFormElement) super.setRequired(required);
    }
}
