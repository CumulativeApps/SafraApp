package com.safra.models.formElements;

import static com.safra.utilities.FormElements.TYPE_MONTH;

public class MonthFormElement extends BaseFormElement {

    private String monthFormat;

    public MonthFormElement() {
    }

    public static MonthFormElement createInstance(){
        MonthFormElement monthFormElement = new MonthFormElement();
        monthFormElement
                .setType(TYPE_MONTH)
                .setFieldType("text")
                .setFieldSubType("month")
                .setClassName("form-control");
        return monthFormElement;
    }

    public MonthFormElement setFieldName(String fieldName){
        return (MonthFormElement) super.setFieldName(fieldName);
    }

    public MonthFormElement setFieldLabel(String fieldLabel){
        return (MonthFormElement) super.setFieldLabel(fieldLabel);
    }

    public MonthFormElement setFieldValue(String fieldValue){
        return (MonthFormElement) super.setFieldValue(fieldValue);
    }

    public MonthFormElement setHaveAccess(boolean haveAccess){
        return (MonthFormElement) super.setHaveAccess(haveAccess);
    }

    public MonthFormElement setRole(Long[] role){
        return (MonthFormElement) super.setRole(role);
    }

    public MonthFormElement setRequired(boolean required){
        return (MonthFormElement) super.setRequired(required);
    }

//    public MonthFormElement setMonthFormat(String format){
//        checkValidDateFormat(format);
//        this.monthFormat = format;
//        return this;
//    }
//
//    public String getMonthFormat() {
//        return monthFormat;
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
