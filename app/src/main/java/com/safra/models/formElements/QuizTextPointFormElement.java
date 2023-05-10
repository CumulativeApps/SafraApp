package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_NUMBER;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_POINT;

public class QuizTextPointFormElement extends BaseFormElement {

    public QuizTextPointFormElement() {
    }

    public static QuizTextPointFormElement createInstance(){
        QuizTextPointFormElement numberFormElement = new QuizTextPointFormElement();
        numberFormElement
                .setType(TYPE_QUIZ_TEXT_POINT)
                .setFieldType("number")
                .setClassName("form-control");
        return numberFormElement;
    }

    public QuizTextPointFormElement setFieldName(String fieldName){
        return (QuizTextPointFormElement) super.setFieldName(fieldName);
    }

    public QuizTextPointFormElement setFieldLabel(String fieldLabel){
        return (QuizTextPointFormElement) super.setFieldLabel(fieldLabel);
    }

    public QuizTextPointFormElement setFieldValue(String fieldValue){
        return (QuizTextPointFormElement) super.setFieldValue(fieldValue);
    }

    public QuizTextPointFormElement setMax(String max){
        return (QuizTextPointFormElement) super.setMax(max);
    }

    public QuizTextPointFormElement setMin(String min){
        return (QuizTextPointFormElement) super.setMin(min);
    }

    public QuizTextPointFormElement setStep(int step){
        return (QuizTextPointFormElement) super.setStep(step);
    }

    public QuizTextPointFormElement setHaveAccess(boolean haveAccess){
        return (QuizTextPointFormElement) super.setHaveAccess(haveAccess);
    }

    public QuizTextPointFormElement setRole(Long[] role){
        return (QuizTextPointFormElement) super.setRole(role);
    }

    public QuizTextPointFormElement setRequired(boolean required){
        return (QuizTextPointFormElement) super.setRequired(required);
    }
}
