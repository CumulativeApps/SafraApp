package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_WEEK;

public class WeekFormElement extends BaseFormElement {

    private String weekFormat;

    public WeekFormElement() {
    }

    public static WeekFormElement createInstance(){
        WeekFormElement weekFormElement = new WeekFormElement();
        weekFormElement
                .setType(TYPE_WEEK)
                .setFieldType("text")
                .setFieldSubType("week")
                .setClassName("form-control");
        return weekFormElement;
    }

    public WeekFormElement setFieldName(String fieldName){
        return (WeekFormElement) super.setFieldName(fieldName);
    }

    public WeekFormElement setFieldLabel(String fieldLabel){
        return (WeekFormElement) super.setFieldLabel(fieldLabel);
    }

    public WeekFormElement setFieldValue(String fieldValue){
        return (WeekFormElement) super.setFieldValue(fieldValue);
    }

    public WeekFormElement setHaveAccess(boolean haveAccess){
        return (WeekFormElement) super.setHaveAccess(haveAccess);
    }

    public WeekFormElement setRole(Long[] role){
        return (WeekFormElement) super.setRole(role);
    }

    public WeekFormElement setRequired(boolean required){
        return (WeekFormElement) super.setRequired(required);
    }

//    public WeekFormElement setWeekFormat(String format){
//        checkValidDateFormat(format);
//        this.weekFormat = format;
//        return this;
//    }
//
//    public String getWeekFormat() {
//        return weekFormat;
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
