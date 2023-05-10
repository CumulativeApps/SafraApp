package com.safra.models.formElements;

import java.util.Date;

import static com.safra.utilities.FormElements.TYPE_DATE;

public class DateFormElement extends BaseFormElement {

    private String dateFormat;

    public DateFormElement() {
    }

    public static DateFormElement createInstance(){
        DateFormElement dateFormElement = new DateFormElement();
        dateFormElement
                .setType(TYPE_DATE)
                .setFieldType("date")
                .setClassName("form-control");
        return dateFormElement;
    }

    public DateFormElement setFieldName(String fieldName){
        return (DateFormElement) super.setFieldName(fieldName);
    }

    public DateFormElement setFieldLabel(String fieldLabel){
        return (DateFormElement) super.setFieldLabel(fieldLabel);
    }

    public DateFormElement setFieldValue(String fieldValue){
        return (DateFormElement) super.setFieldValue(fieldValue);
    }

    public DateFormElement setHaveAccess(boolean haveAccess){
        return (DateFormElement) super.setHaveAccess(haveAccess);
    }

    public DateFormElement setRole(Long[] role){
        return (DateFormElement) super.setRole(role);
    }

    public DateFormElement setRequired(boolean required){
        return (DateFormElement) super.setRequired(required);
    }

//    public DateFormElement setDateFormat(String format){
//        checkValidDateFormat(format);
//        this.dateFormat = format;
//        return this;
//    }
//
//    public String getDateFormat() {
//        return dateFormat;
//    }
//
//    private void checkValidDateFormat(String format) {
//        try {
//            new SimpleDateFormat(format, Locale.getDefault());
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Date format is not correct: " + e.getMessage());
//        }
//    }
}
