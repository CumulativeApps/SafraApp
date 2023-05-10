package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_FILE;

public class FileFormElement extends BaseFormElement {

    public FileFormElement() {
    }

    public static FileFormElement createInstance(){
        FileFormElement fileFormElement = new FileFormElement();
        fileFormElement
                .setType(TYPE_FILE)
                .setFieldType("file")
                .setClassName("form-control");
        return fileFormElement;
    }

    public FileFormElement setFieldName(String fieldName){
        return (FileFormElement) super.setFieldName(fieldName);
    }

    public FileFormElement setFieldLabel(String fieldLabel){
        return (FileFormElement) super.setFieldLabel(fieldLabel);
    }

    public FileFormElement setFieldValue(String fieldValue){
        return (FileFormElement) super.setFieldValue(fieldValue);
    }

    public FileFormElement setHaveAccess(boolean haveAccess){
        return (FileFormElement) super.setHaveAccess(haveAccess);
    }

    public FileFormElement setRole(Long[] role){
        return (FileFormElement) super.setRole(role);
    }

    public FileFormElement setRequired(boolean required){
        return (FileFormElement) super.setRequired(required);
    }
}
