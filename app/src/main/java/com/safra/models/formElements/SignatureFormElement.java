package com.safra.models.formElements;


//import static com.safra.utilities.FormElements.TYPE_SIGNATURE;

public class SignatureFormElement extends BaseFormElement {

    public SignatureFormElement() {
    }

    public static SignatureFormElement createInstance(){
        SignatureFormElement textFormElement = new SignatureFormElement();
        textFormElement
//                .setType(TYPE_SIGNATURE)
                .setFieldType("signature");
        return textFormElement;
    }

    public SignatureFormElement setFieldName(String fieldName){
        return (SignatureFormElement) super.setFieldName(fieldName);
    }

    public SignatureFormElement setFieldLabel(String fieldLabel){
        return (SignatureFormElement) super.setFieldLabel(fieldLabel);
    }

    public SignatureFormElement setFieldValue(String fieldValue){
        return (SignatureFormElement) super.setFieldValue(fieldValue);
    }

    public SignatureFormElement setHaveAccess(boolean haveAccess){
        return (SignatureFormElement) super.setHaveAccess(haveAccess);
    }

    public SignatureFormElement setRole(Long[] role){
        return (SignatureFormElement) super.setRole(role);
    }

    public SignatureFormElement setRequired(boolean required){
        return (SignatureFormElement) super.setRequired(required);
    }
}

