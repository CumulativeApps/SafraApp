package com.safra.models.formElements;

import org.w3c.dom.Text;

import static com.safra.utilities.FormElements.TYPE_TEXT;

public class TextFormElement extends BaseFormElement {

    public TextFormElement() {
    }

    public static TextFormElement createInstance(){
        TextFormElement textFormElement = new TextFormElement();
        textFormElement
                .setType(TYPE_TEXT)
                .setFieldType("textfield")
                .setFieldSubType("text")
                .setClassName("form-control");
        return textFormElement;
    }

    public TextFormElement setFieldName(String fieldName){
        return (TextFormElement) super.setFieldName(fieldName);
    }

    public TextFormElement setFieldLabel(String fieldLabel){
        return (TextFormElement) super.setFieldLabel(fieldLabel);
    }

    public TextFormElement setFieldValue(String fieldValue){
        return (TextFormElement) super.setFieldValue(fieldValue);
    }

    public TextFormElement setHaveAccess(boolean haveAccess){
        return (TextFormElement) super.setHaveAccess(haveAccess);
    }

    public TextFormElement setRole(Long[] role){
        return (TextFormElement) super.setRole(role);
    }

    public TextFormElement setRequired(boolean required){
        return (TextFormElement) super.setRequired(required);
    }
}
