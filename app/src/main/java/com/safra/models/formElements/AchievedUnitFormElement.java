package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_NUMBER;

public class AchievedUnitFormElement extends BaseFormElement {

    public AchievedUnitFormElement() {
    }

    public static AchievedUnitFormElement createInstance(){
        AchievedUnitFormElement numberFormElement = new AchievedUnitFormElement();
        numberFormElement
                .setType(TYPE_NUMBER)
                .setFieldType("number")
                .setClassName("form-control achieved_unit");
        return numberFormElement;
    }

    public AchievedUnitFormElement setFieldName(String fieldName){
        return (AchievedUnitFormElement) super.setFieldName(fieldName);
    }

    public AchievedUnitFormElement setFieldLabel(String fieldLabel){
        return (AchievedUnitFormElement) super.setFieldLabel(fieldLabel);
    }

    public AchievedUnitFormElement setFieldValue(String fieldValue){
        return (AchievedUnitFormElement) super.setFieldValue(fieldValue);
    }

    public AchievedUnitFormElement setMax(String max){
        return (AchievedUnitFormElement) super.setMax(max);
    }

    public AchievedUnitFormElement setMin(String min){
        return (AchievedUnitFormElement) super.setMin(min);
    }

    public AchievedUnitFormElement setStep(int step){
        return (AchievedUnitFormElement) super.setStep(step);
    }

    public AchievedUnitFormElement setHaveAccess(boolean haveAccess){
        return (AchievedUnitFormElement) super.setHaveAccess(haveAccess);
    }

    public AchievedUnitFormElement setRole(Long[] role){
        return (AchievedUnitFormElement) super.setRole(role);
    }

    public AchievedUnitFormElement setRequired(boolean required){
        return (AchievedUnitFormElement) super.setRequired(required);
    }
}
