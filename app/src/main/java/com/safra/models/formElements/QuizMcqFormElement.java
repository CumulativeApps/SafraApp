package com.safra.models.formElements;

import com.safra.models.OptionItem;

import java.util.List;

import static com.safra.utilities.FormElements.TYPE_QUIZ_MCQ;

public class QuizMcqFormElement extends BaseFormElement {

    public QuizMcqFormElement() {
    }

    public static QuizMcqFormElement createInstance(){
        QuizMcqFormElement radioGroupFormElement = new QuizMcqFormElement();
        radioGroupFormElement
                .setType(TYPE_QUIZ_MCQ)
                .setFieldType("radio-group")
                .setClassName("mcq");
        return radioGroupFormElement;
    }

    public QuizMcqFormElement setFieldName(String fieldName){
        return (QuizMcqFormElement) super.setFieldName(fieldName);
    }

    public QuizMcqFormElement setFieldLabel(String fieldLabel){
        return (QuizMcqFormElement) super.setFieldLabel(fieldLabel);
    }

    public QuizMcqFormElement setFieldValue(String fieldValue){
        return (QuizMcqFormElement) super.setFieldValue(fieldValue);
    }

    public QuizMcqFormElement setHaveAccess(boolean haveAccess){
        return (QuizMcqFormElement) super.setHaveAccess(haveAccess);
    }

    public QuizMcqFormElement setRole(Long[] role){
        return (QuizMcqFormElement) super.setRole(role);
    }

    public QuizMcqFormElement setRequired(boolean required){
        return (QuizMcqFormElement) super.setRequired(required);
    }

    public QuizMcqFormElement setOptions(List<OptionItem> mOptions) {
        return (QuizMcqFormElement) super.setOptions(mOptions);
    }

}
