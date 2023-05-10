package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_ANSWER;

public class QuizTextAnswerFormElement extends BaseFormElement {

    public QuizTextAnswerFormElement() {
    }

    public static QuizTextAnswerFormElement createInstance(){
        QuizTextAnswerFormElement textAreaFormElement = new QuizTextAnswerFormElement();
        textAreaFormElement
                .setType(TYPE_QUIZ_TEXT_ANSWER)
                .setFieldType("textarea")
                .setClassName("form-control");
        return textAreaFormElement;
    }

    public QuizTextAnswerFormElement setFieldName(String fieldName){
        return (QuizTextAnswerFormElement) super.setFieldName(fieldName);
    }

    public QuizTextAnswerFormElement setFieldLabel(String fieldLabel){
        return (QuizTextAnswerFormElement) super.setFieldLabel(fieldLabel);
    }

    public QuizTextAnswerFormElement setFieldValue(String fieldValue){
        return (QuizTextAnswerFormElement) super.setFieldValue(fieldValue);
    }

    public QuizTextAnswerFormElement setMaxLength(int maxLength){
        return (QuizTextAnswerFormElement) super.setMaxLength(maxLength);
    }

    public QuizTextAnswerFormElement setRows(int rows){
        return (QuizTextAnswerFormElement) super.setRows(rows);
    }

    public QuizTextAnswerFormElement setHaveAccess(boolean haveAccess){
        return (QuizTextAnswerFormElement) super.setHaveAccess(haveAccess);
    }

    public QuizTextAnswerFormElement setRole(Long[] role){
        return (QuizTextAnswerFormElement) super.setRole(role);
    }

    public QuizTextAnswerFormElement setRequired(boolean required){
        return (QuizTextAnswerFormElement) super.setRequired(required);
    }
}
