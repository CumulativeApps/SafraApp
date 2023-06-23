package com.safra.models.formElements;

//import static com.safra.utilities.FormElements.TYPE_HTML_ELEMENT;


public class HtmlElementFormElement extends BaseFormElement {

    public HtmlElementFormElement() {
    }

    public static HtmlElementFormElement createInstance(){
        HtmlElementFormElement textFormElement = new HtmlElementFormElement();
        textFormElement
//                .setType(TYPE_HTML_ELEMENT)
                .setFieldType("htmlelement");

        return textFormElement;
    }

    public HtmlElementFormElement setFieldName(String fieldName){
        return (HtmlElementFormElement) super.setFieldName(fieldName);
    }

    public HtmlElementFormElement setFieldLabel(String fieldLabel){
        return (HtmlElementFormElement) super.setFieldLabel(fieldLabel);
    }

    public HtmlElementFormElement setFieldValue(String fieldValue){
        return (HtmlElementFormElement) super.setFieldValue(fieldValue);
    }

    public HtmlElementFormElement setHaveAccess(boolean haveAccess){
        return (HtmlElementFormElement) super.setHaveAccess(haveAccess);
    }

    public HtmlElementFormElement setRole(Long[] role){
        return (HtmlElementFormElement) super.setRole(role);
    }

    public HtmlElementFormElement setRequired(boolean required){
        return (HtmlElementFormElement) super.setRequired(required);
    }
}
