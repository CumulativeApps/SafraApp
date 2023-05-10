package com.safra.models.formElements;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;

public class QuizTextFormElement extends BaseFormElement {

    public QuizTextFormElement() {
    }

    public static QuizTextFormElement createInstance(){
        QuizTextFormElement quizTextFormElement = new QuizTextFormElement();
        quizTextFormElement
                .setType(TYPE_QUIZ_TEXT)
                .setFieldType("text-field-marks");
        return quizTextFormElement;
    }

    public QuizTextFormElement setFieldName(String fieldName){
        return (QuizTextFormElement) super.setFieldName(fieldName);
    }

    public QuizTextFormElement setFieldLabel(String fieldLabel){
        return (QuizTextFormElement) super.setFieldLabel(fieldLabel);
    }

    public QuizTextFormElement setHaveAccess(boolean haveAccess){
        return (QuizTextFormElement) super.setHaveAccess(haveAccess);
    }

    public QuizTextFormElement setRole(Long[] role){
        return (QuizTextFormElement) super.setRole(role);
    }

    public QuizTextFormElement setRequired(boolean required){
        return (QuizTextFormElement) super.setRequired(required);
    }

    public QuizTextFormElement setListOfFormElement(List<BaseFormElement> elementList){
        return (QuizTextFormElement) super.setElementList(elementList);
    }

    public QuizTextFormElement setJsonArrayOfElements(String jsonArrayOfElements){
        return (QuizTextFormElement) super.setElementJsonArray(jsonArrayOfElements);
    }
}
