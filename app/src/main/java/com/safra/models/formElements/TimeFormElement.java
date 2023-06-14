package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_TIME;

public class TimeFormElement extends BaseFormElement {

    private String timeFormat;

    public TimeFormElement() {
    }

    public static TimeFormElement createInstance(){
        TimeFormElement timeFormElement = new TimeFormElement();
        timeFormElement
                .setType(TYPE_TIME)
                .setFieldType("time")
                .setFieldSubType("time")
                .setClassName("form-control");
        return timeFormElement;
    }

    public TimeFormElement setFieldName(String fieldName){
        return (TimeFormElement) super.setFieldName(fieldName);
    }

    public TimeFormElement setFieldLabel(String fieldLabel){
        return (TimeFormElement) super.setFieldLabel(fieldLabel);
    }

    public TimeFormElement setFieldValue(String fieldValue){
        return (TimeFormElement) super.setFieldValue(fieldValue);
    }

    public TimeFormElement setHaveAccess(boolean haveAccess){
        return (TimeFormElement) super.setHaveAccess(haveAccess);
    }

    public TimeFormElement setRole(Long[] role){
        return (TimeFormElement) super.setRole(role);
    }

    public TimeFormElement setRequired(boolean required){
        return (TimeFormElement) super.setRequired(required);
    }

//    public String getTimeFormat() {
//        return timeFormat;
//    }
//
//    public TimeFormElement setTimeFormat(String timeFormat) {
//        checkValidTimeFormat(timeFormat);
//        this.timeFormat = timeFormat;
//        return this;
//    }
//
//    private void checkValidTimeFormat(String format) {
//        try {
//            new SimpleDateFormat(format, Locale.getDefault());
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Time format is not correct: " + e.getMessage());
//        }
//    }
}
