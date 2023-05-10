package com.safra.models.formElements;

import org.w3c.dom.Text;

import static com.safra.utilities.FormElements.TYPE_TEXT_AREA;

public class TextAreaFormElement extends BaseFormElement {

    public TextAreaFormElement() {
    }

    public static TextAreaFormElement createInstance(){
        TextAreaFormElement textAreaFormElement = new TextAreaFormElement();
        textAreaFormElement
                .setType(TYPE_TEXT_AREA)
                .setFieldType("textarea")
                .setClassName("form-control");
        return textAreaFormElement;
    }

    public TextAreaFormElement setFieldName(String fieldName){
        return (TextAreaFormElement) super.setFieldName(fieldName);
    }

    public TextAreaFormElement setFieldLabel(String fieldLabel){
        return (TextAreaFormElement) super.setFieldLabel(fieldLabel);
    }

    public TextAreaFormElement setFieldValue(String fieldValue){
        return (TextAreaFormElement) super.setFieldValue(fieldValue);
    }

    public TextAreaFormElement setMaxLength(int maxLength){
        return (TextAreaFormElement) super.setMaxLength(maxLength);
    }

    public TextAreaFormElement setRows(int rows){
        return (TextAreaFormElement) super.setRows(rows);
    }

    public TextAreaFormElement setHaveAccess(boolean haveAccess){
        return (TextAreaFormElement) super.setHaveAccess(haveAccess);
    }

    public TextAreaFormElement setRole(Long[] role){
        return (TextAreaFormElement) super.setRole(role);
    }

    public TextAreaFormElement setRequired(boolean required){
        return (TextAreaFormElement) super.setRequired(required);
    }
}
