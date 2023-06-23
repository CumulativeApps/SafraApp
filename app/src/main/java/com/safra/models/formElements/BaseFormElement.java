package com.safra.models.formElements;

import androidx.annotation.NonNull;

import com.safra.models.CascadeOptionItem;
import com.safra.models.OptionItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseFormElement implements Cloneable {

    private String fieldName;
    private int type;
    private String fieldType, fieldSubType;
    private String fieldLabel;
    private String fieldValue;
    private String fieldContent;

    private boolean haveError;


    private String max, min;
    private int maxLength, rows, step;

    private String className;
    private boolean isHaveAccess;
    private Long[] role;

    private boolean isRequired;

    private List<OptionItem> options;


    private List<String> userData;

    private List<BaseFormElement> elementList;
    private String elementJsonArray;
    private List<CascadeOptionItem> cascadeOptions;
    private int parentId;

    public String getFieldName() {
        return this.fieldName;
    }

    public int getType() {
        return this.type;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public String getFieldSubType() {
        return this.fieldSubType;
    }

    public String getFieldLabel() {
        return this.fieldLabel;
    }

    public String getFieldValue() {
        return fieldValue;
    }
    public String getFieldContent() {
        return fieldContent;
    }

    public boolean isHaveError() {
        return haveError;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public int getRows() {
        return this.rows;
    }

    public String getMin() {
        return this.min;
    }

    public String getMax() {
        return this.max;
    }

    public int getStep() {
        return this.step;
    }

    public String getClassName() {
        return this.className;
    }

    public boolean isHaveAccess() {
        return this.isHaveAccess;
    }

    public Long[] getRole() {
        return this.role;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public List<OptionItem> getOptions() {
        return (this.options == null) ? new ArrayList<>() : this.options;
    }


    public List<String> getUserData() {
        return userData;
    }

    public List<BaseFormElement> getElementList() {
        return elementList;
    }

    public String getElementJsonArray() {
        return elementJsonArray;
    }

    public List<CascadeOptionItem> getCascadeOptions() {
        return cascadeOptions;
    }

    public int getParentId() {
        return parentId;
    }

    public BaseFormElement setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public BaseFormElement setType(int type) {
        this.type = type;
        return this;
    }

    public BaseFormElement setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public BaseFormElement setFieldSubType(String fieldSubType) {
        this.fieldSubType = fieldSubType;
        return this;
    }

    public BaseFormElement setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
        return this;
    }

    public BaseFormElement setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
        return this;
    }
    public BaseFormElement setFieldContent(String fieldContent) {
        this.fieldContent = fieldContent;
        return this;
    }

    public BaseFormElement setHaveError(boolean haveError) {
        this.haveError = haveError;
        return this;
    }

    public BaseFormElement setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public BaseFormElement setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public BaseFormElement setMin(String min) {
        this.min = min;
        return this;
    }

    public BaseFormElement setMax(String max) {
        this.max = max;
        return this;
    }

    public BaseFormElement setStep(int step) {
        this.step = step;
        return this;
    }

    public BaseFormElement setClassName(String className) {
        this.className = className;
        return this;
    }

    public BaseFormElement setHaveAccess(boolean haveAccess) {
        isHaveAccess = haveAccess;
        return this;
    }

    public BaseFormElement setRole(Long[] role) {
        this.role = role;
        return this;
    }

    public BaseFormElement setRequired(boolean required) {
        isRequired = required;
        return this;
    }

    public BaseFormElement setOptions(List<OptionItem> options) {
        this.options = options;
        return this;
    }


    public BaseFormElement setUserData(List<String> userData) {
        this.userData = userData;
        return this;
    }

    public BaseFormElement setElementList(List<BaseFormElement> elementList) {
        this.elementList = elementList;
        return this;
    }

    public BaseFormElement setElementJsonArray(String elementJsonArray) {
        this.elementJsonArray = elementJsonArray;
        return this;
    }

    public BaseFormElement setCascadeOptions(List<CascadeOptionItem> cascadeOptions) {
        this.cascadeOptions = cascadeOptions;
        return this;
    }

    public BaseFormElement setParentId(int parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public String toString() {
        return "BaseFormElement{" +
                "fieldName='" + fieldName + '\'' +
                ", type=" + type +
                ", fieldType='" + fieldType + '\'' +
                ", fieldSubType='" + fieldSubType + '\'' +
                ", fieldLabel='" + fieldLabel + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                ", fieldContent='" + fieldContent + '\'' +
                ", haveError=" + haveError +
                ", max='" + max + '\'' +
                ", min='" + min + '\'' +
                ", maxLength=" + maxLength +
                ", rows=" + rows +
                ", step=" + step +
                ", className='" + className + '\'' +
                ", isHaveAccess=" + isHaveAccess +
                ", role=" + Arrays.toString(role) +
                ", isRequired=" + isRequired +
                ", options=" + (options != null ? options.get(0).isSelected() : "null") +
                '}';
    }

    @NonNull
    @Override
    public BaseFormElement clone() throws CloneNotSupportedException {
        return (BaseFormElement) super.clone();
    }
}


//package com.safra.models.formElements;
//
//import androidx.annotation.NonNull;
//
//import com.safra.models.CascadeOptionItem;
//import com.safra.models.OptionItem;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class BaseFormElement implements Cloneable {
//
//    private String label;
//    private String labelPosition;
//    private String placeholder;
//    private String description;
//    private String tooltip;
//    private String prefix;
//    private String suffix;
//    private String widget;
//    private String inputMask;
//    private String displayMask;
//    private String allowMultipleMasks;
//    private String customClass;
//    private String tabindex;
//    private String autocomplete;
//    private String hidden;
//    private String hideLabel;
//    private String showWordCount;
//    private String showCharCount;
//    private String mask;
//    private String autofocus;
//    private String spellcheck;
//    private String disabled;
//    private String tableView;
//    private String modalEdit;
//    private String multiple;
//    private String persistent;
//    private String inputFormat;
//    private String dbIndex;
//    private String truncateMultipleSpaces;
//    private String encrypted;
//    private String redrawOn;
//    private String clearOnHide;
//    private String customDefaultValue;
//    private String calculateValue;
//    private String calculateServer;
//    private String allowCalculateOverride;
//    private String validateOn;
//    private String validate;
//    private String required;
//    private String pattern;
//    private String customMessage;
//    private String custom;
//    private String customPrivate;
//    private String json;
//    private String minLength;
//    private int maxLength;
//    private String strictDateValidation;
//
//    private String unique;
//
//    private String errorLabel;
//    private String errors;
//    private String key;
//    private String tags;
//
//    private String properties;
//
//    private String conditional;
//    private String show;
//    private String when;
//    private String eq;
//
//
//    private String customConditional;
//    private String logic;
//
//    private String attributes;
//
//    private String overlay;
//    private String style;
//    private String page;
//    private String left;
//    private String top;
//    private String width;
//    private String height;
//
//    private String input;
//    private String refreshOn;
//    private String dataGridLabel;
//    private String addons;
//
//    private String inputType;
//    private String id;
//    private String defaultValue;
//
//
//
//
//
//    public String getLabel() {
//        return label;
//    }
//
//    public void setLabel(String label) {
//        this.label = label;
//    }
//
//    public String getLabelPosition() {
//        return labelPosition;
//    }
//
//    public void setLabelPosition(String labelPosition) {
//        this.labelPosition = labelPosition;
//    }
//
//    public String getPlaceholder() {
//        return placeholder;
//    }
//
//    public void setPlaceholder(String placeholder) {
//        this.placeholder = placeholder;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getTooltip() {
//        return tooltip;
//    }
//
//    public void setTooltip(String tooltip) {
//        this.tooltip = tooltip;
//    }
//
//    public String getPrefix() {
//        return prefix;
//    }
//
//    public void setPrefix(String prefix) {
//        this.prefix = prefix;
//    }
//
//    public String getSuffix() {
//        return suffix;
//    }
//
//    public void setSuffix(String suffix) {
//        this.suffix = suffix;
//    }
//
//    public String getWidget() {
//        return widget;
//    }
//
//    public void setWidget(String widget) {
//        this.widget = widget;
//    }
//
////    public String getType() {
////        return type;
////    }
////
////    public void setType(String type) {
////        this.type = type;
////    }
//
//    public String getInputMask() {
//        return inputMask;
//    }
//
//    public void setInputMask(String inputMask) {
//        this.inputMask = inputMask;
//    }
//
//    public String getDisplayMask() {
//        return displayMask;
//    }
//
//    public void setDisplayMask(String displayMask) {
//        this.displayMask = displayMask;
//    }
//
//    public String getAllowMultipleMasks() {
//        return allowMultipleMasks;
//    }
//
//    public void setAllowMultipleMasks(String allowMultipleMasks) {
//        this.allowMultipleMasks = allowMultipleMasks;
//    }
//
//    public String getCustomClass() {
//        return customClass;
//    }
//
//    public void setCustomClass(String customClass) {
//        this.customClass = customClass;
//    }
//
//    public String getTabindex() {
//        return tabindex;
//    }
//
//    public void setTabindex(String tabindex) {
//        this.tabindex = tabindex;
//    }
//
//    public String getAutocomplete() {
//        return autocomplete;
//    }
//
//    public void setAutocomplete(String autocomplete) {
//        this.autocomplete = autocomplete;
//    }
//
//    public String getHidden() {
//        return hidden;
//    }
//
//    public void setHidden(String hidden) {
//        this.hidden = hidden;
//    }
//
//    public String getHideLabel() {
//        return hideLabel;
//    }
//
//    public void setHideLabel(String hideLabel) {
//        this.hideLabel = hideLabel;
//    }
//
//    public String getShowWordCount() {
//        return showWordCount;
//    }
//
//    public void setShowWordCount(String showWordCount) {
//        this.showWordCount = showWordCount;
//    }
//
//    public String getShowCharCount() {
//        return showCharCount;
//    }
//
//    public void setShowCharCount(String showCharCount) {
//        this.showCharCount = showCharCount;
//    }
//
//    public String getMask() {
//        return mask;
//    }
//
//    public void setMask(String mask) {
//        this.mask = mask;
//    }
//
//    public String getAutofocus() {
//        return autofocus;
//    }
//
//    public void setAutofocus(String autofocus) {
//        this.autofocus = autofocus;
//    }
//
//    public String getSpellcheck() {
//        return spellcheck;
//    }
//
//    public void setSpellcheck(String spellcheck) {
//        this.spellcheck = spellcheck;
//    }
//
//    public String getDisabled() {
//        return disabled;
//    }
//
//    public void setDisabled(String disabled) {
//        this.disabled = disabled;
//    }
//
//    public String getTableView() {
//        return tableView;
//    }
//
//    public void setTableView(String tableView) {
//        this.tableView = tableView;
//    }
//
//    public String getModalEdit() {
//        return modalEdit;
//    }
//
//    public void setModalEdit(String modalEdit) {
//        this.modalEdit = modalEdit;
//    }
//
//    public String getMultiple() {
//        return multiple;
//    }
//
//    public void setMultiple(String multiple) {
//        this.multiple = multiple;
//    }
//
//    public String getPersistent() {
//        return persistent;
//    }
//
//    public void setPersistent(String persistent) {
//        this.persistent = persistent;
//    }
//
//    public String getInputFormat() {
//        return inputFormat;
//    }
//
//    public void setInputFormat(String inputFormat) {
//        this.inputFormat = inputFormat;
//    }
//
//    public String getDbIndex() {
//        return dbIndex;
//    }
//
//    public void setDbIndex(String dbIndex) {
//        this.dbIndex = dbIndex;
//    }
//
//    public String getTruncateMultipleSpaces() {
//        return truncateMultipleSpaces;
//    }
//
//    public void setTruncateMultipleSpaces(String truncateMultipleSpaces) {
//        this.truncateMultipleSpaces = truncateMultipleSpaces;
//    }
//
//    public String getEncrypted() {
//        return encrypted;
//    }
//
//    public void setEncrypted(String encrypted) {
//        this.encrypted = encrypted;
//    }
//
//    public String getRedrawOn() {
//        return redrawOn;
//    }
//
//    public void setRedrawOn(String redrawOn) {
//        this.redrawOn = redrawOn;
//    }
//
//    public String getClearOnHide() {
//        return clearOnHide;
//    }
//
//    public void setClearOnHide(String clearOnHide) {
//        this.clearOnHide = clearOnHide;
//    }
//
//    public String getCustomDefaultValue() {
//        return customDefaultValue;
//    }
//
//    public void setCustomDefaultValue(String customDefaultValue) {
//        this.customDefaultValue = customDefaultValue;
//    }
//
//    public String getCalculateValue() {
//        return calculateValue;
//    }
//
//    public void setCalculateValue(String calculateValue) {
//        this.calculateValue = calculateValue;
//    }
//
//    public String getCalculateServer() {
//        return calculateServer;
//    }
//
//    public void setCalculateServer(String calculateServer) {
//        this.calculateServer = calculateServer;
//    }
//
//    public String getAllowCalculateOverride() {
//        return allowCalculateOverride;
//    }
//
//    public void setAllowCalculateOverride(String allowCalculateOverride) {
//        this.allowCalculateOverride = allowCalculateOverride;
//    }
//
//    public String getValidateOn() {
//        return validateOn;
//    }
//
//    public void setValidateOn(String validateOn) {
//        this.validateOn = validateOn;
//    }
//
//    public String getValidate() {
//        return validate;
//    }
//
//    public void setValidate(String validate) {
//        this.validate = validate;
//    }
//
//    public String getRequired() {
//        return required;
//    }
//
//    public void setRequired(String required) {
//        this.required = required;
//    }
//
//    public String getPattern() {
//        return pattern;
//    }
//
//    public void setPattern(String pattern) {
//        this.pattern = pattern;
//    }
//
//    public String getCustomMessage() {
//        return customMessage;
//    }
//
//    public void setCustomMessage(String customMessage) {
//        this.customMessage = customMessage;
//    }
//
//    public String getCustom() {
//        return custom;
//    }
//
//    public void setCustom(String custom) {
//        this.custom = custom;
//    }
//
//    public String getCustomPrivate() {
//        return customPrivate;
//    }
//
//    public void setCustomPrivate(String customPrivate) {
//        this.customPrivate = customPrivate;
//    }
//
//    public String getJson() {
//        return json;
//    }
//
//    public void setJson(String json) {
//        this.json = json;
//    }
//
//    public String getMinLength() {
//        return minLength;
//    }
//
//    public void setMinLength(String minLength) {
//        this.minLength = minLength;
//    }
//
////    public int getMaxLength() {
////        return maxLength;
////    }
////
////    public void setMaxLength(int maxLength) {
////        this.maxLength = maxLength;
////    }
//
//    public String getStrictDateValidation() {
//        return strictDateValidation;
//    }
//
//    public void setStrictDateValidation(String strictDateValidation) {
//        this.strictDateValidation = strictDateValidation;
//    }
//
//    public String getUnique() {
//        return unique;
//    }
//
//    public void setUnique(String unique) {
//        this.unique = unique;
//    }
//
//    public String getErrorLabel() {
//        return errorLabel;
//    }
//
//    public void setErrorLabel(String errorLabel) {
//        this.errorLabel = errorLabel;
//    }
//
//    public String getErrors() {
//        return errors;
//    }
//
//    public void setErrors(String errors) {
//        this.errors = errors;
//    }
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    public String getTags() {
//        return tags;
//    }
//
//    public void setTags(String tags) {
//        this.tags = tags;
//    }
//
//    public String getProperties() {
//        return properties;
//    }
//
//    public void setProperties(String properties) {
//        this.properties = properties;
//    }
//
//    public String getConditional() {
//        return conditional;
//    }
//
//    public void setConditional(String conditional) {
//        this.conditional = conditional;
//    }
//
//    public String getShow() {
//        return show;
//    }
//
//    public void setShow(String show) {
//        this.show = show;
//    }
//
//    public String getWhen() {
//        return when;
//    }
//
//    public void setWhen(String when) {
//        this.when = when;
//    }
//
//    public String getEq() {
//        return eq;
//    }
//
//    public void setEq(String eq) {
//        this.eq = eq;
//    }
//
//    public String getCustomConditional() {
//        return customConditional;
//    }
//
//    public void setCustomConditional(String customConditional) {
//        this.customConditional = customConditional;
//    }
//
//    public String getLogic() {
//        return logic;
//    }
//
//    public void setLogic(String logic) {
//        this.logic = logic;
//    }
//
//    public String getAttributes() {
//        return attributes;
//    }
//
//    public void setAttributes(String attributes) {
//        this.attributes = attributes;
//    }
//
//    public String getOverlay() {
//        return overlay;
//    }
//
//    public void setOverlay(String overlay) {
//        this.overlay = overlay;
//    }
//
//    public String getStyle() {
//        return style;
//    }
//
//    public void setStyle(String style) {
//        this.style = style;
//    }
//
//    public String getPage() {
//        return page;
//    }
//
//    public void setPage(String page) {
//        this.page = page;
//    }
//
//    public String getLeft() {
//        return left;
//    }
//
//    public void setLeft(String left) {
//        this.left = left;
//    }
//
//    public String getTop() {
//        return top;
//    }
//
//    public void setTop(String top) {
//        this.top = top;
//    }
//
//    public String getWidth() {
//        return width;
//    }
//
//    public void setWidth(String width) {
//        this.width = width;
//    }
//
//    public String getHeight() {
//        return height;
//    }
//
//    public void setHeight(String height) {
//        this.height = height;
//    }
//
//    public String getInput() {
//        return input;
//    }
//
//    public void setInput(String input) {
//        this.input = input;
//    }
//
//    public String getRefreshOn() {
//        return refreshOn;
//    }
//
//    public void setRefreshOn(String refreshOn) {
//        this.refreshOn = refreshOn;
//    }
//
//    public String getDataGridLabel() {
//        return dataGridLabel;
//    }
//
//    public void setDataGridLabel(String dataGridLabel) {
//        this.dataGridLabel = dataGridLabel;
//    }
//
//    public String getAddons() {
//        return addons;
//    }
//
//    public void setAddons(String addons) {
//        this.addons = addons;
//    }
//
//    public String getInputType() {
//        return inputType;
//    }
//
//    public void setInputType(String inputType) {
//        this.inputType = inputType;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getDefaultValue() {
//        return defaultValue;
//    }
//
//    public void setDefaultValue(String defaultValue) {
//        this.defaultValue = defaultValue;
//    }
//    private String fieldName;
//    private int type;
//    private String fieldType, fieldSubType;
//    private String fieldLabel;
//    private String fieldValue;
//
//    private boolean haveError;
//
//    private String max, min;
//    private int  rows, step;
//
//    private String className;
//    private boolean isHaveAccess;
//    private Long[] role;
//
//    private boolean isRequired;
//
//    private List<OptionItem> options;
//
//    private List<String> userData;
//
//    private List<BaseFormElement> elementList;
//    private String elementJsonArray;
//    private List<CascadeOptionItem> cascadeOptions;
//    private int parentId;
//
//    public String getFieldName() {
//        return this.fieldName;
//    }
//
//    public int getType() {
//        return this.type;
//    }
//
//    public String getFieldType() {
//        return this.fieldType;
//    }
//
//    public String getFieldSubType() {
//        return this.fieldSubType;
//    }
//
//    public String getFieldLabel() {
//        return this.fieldLabel;
//    }
//
//    public String getFieldValue() {
//        return fieldValue;
//    }
//
//    public boolean isHaveError() {
//        return haveError;
//    }
//
//    public int getMaxLength() {
//        return this.maxLength;
//    }
//
//    public int getRows() {
//        return this.rows;
//    }
//
//    public String getMin() {
//        return this.min;
//    }
//
//    public String getMax() {
//        return this.max;
//    }
//
//    public int getStep() {
//        return this.step;
//    }
//
//    public String getClassName() {
//        return this.className;
//    }
//
//    public boolean isHaveAccess() {
//        return this.isHaveAccess;
//    }
//
//    public Long[] getRole() {
//        return this.role;
//    }
//
//    public boolean isRequired() {
//        return this.isRequired;
//    }
//
//    public List<OptionItem> getOptions() {
//        return (this.options == null) ? new ArrayList<>() : this.options;
//    }
//
//    public List<String> getUserData() {
//        return userData;
//    }
//
//    public List<BaseFormElement> getElementList() {
//        return elementList;
//    }
//
//    public String getElementJsonArray() {
//        return elementJsonArray;
//    }
//
//    public List<CascadeOptionItem> getCascadeOptions() {
//        return cascadeOptions;
//    }
//
//    public int getParentId() {
//        return parentId;
//    }
//
//    public BaseFormElement setFieldName(String fieldName) {
//        this.fieldName = fieldName;
//        return this;
//    }
//
//    public BaseFormElement setType(int type) {
//        this.type = type;
//        return this;
//    }
//
//    public BaseFormElement setFieldType(String fieldType) {
//        this.fieldType = fieldType;
//        return this;
//    }
//
//    public BaseFormElement setFieldSubType(String fieldSubType) {
//        this.fieldSubType = fieldSubType;
//        return this;
//    }
//
//    public BaseFormElement setFieldLabel(String fieldLabel) {
//        this.fieldLabel = fieldLabel;
//        return this;
//    }
//
//    public BaseFormElement setFieldValue(String fieldValue) {
//        this.fieldValue = fieldValue;
//        return this;
//    }
//
//    public BaseFormElement setHaveError(boolean haveError) {
//        this.haveError = haveError;
//        return this;
//    }
//
//    public BaseFormElement setMaxLength(int maxLength) {
//        this.maxLength = maxLength;
//        return this;
//    }
//
//    public BaseFormElement setRows(int rows) {
//        this.rows = rows;
//        return this;
//    }
//
//    public BaseFormElement setMin(String min) {
//        this.min = min;
//        return this;
//    }
//
//    public BaseFormElement setMax(String max) {
//        this.max = max;
//        return this;
//    }
//
//    public BaseFormElement setStep(int step) {
//        this.step = step;
//        return this;
//    }
//
//    public BaseFormElement setClassName(String className) {
//        this.className = className;
//        return this;
//    }
//
//    public BaseFormElement setHaveAccess(boolean haveAccess) {
//        isHaveAccess = haveAccess;
//        return this;
//    }
//
//    public BaseFormElement setRole(Long[] role) {
//        this.role = role;
//        return this;
//    }
//
//    public BaseFormElement setRequired(boolean required) {
//        isRequired = required;
//        return this;
//    }
//
//    public BaseFormElement setOptions(List<OptionItem> options) {
//        this.options = options;
//        return this;
//    }
//
//    public BaseFormElement setUserData(List<String> userData) {
//        this.userData = userData;
//        return this;
//    }
//
//    public BaseFormElement setElementList(List<BaseFormElement> elementList) {
//        this.elementList = elementList;
//        return this;
//    }
//
//    public BaseFormElement setElementJsonArray(String elementJsonArray) {
//        this.elementJsonArray = elementJsonArray;
//        return this;
//    }
//
//    public BaseFormElement setCascadeOptions(List<CascadeOptionItem> cascadeOptions) {
//        this.cascadeOptions = cascadeOptions;
//        return this;
//    }
//
//    public BaseFormElement setParentId(int parentId) {
//        this.parentId = parentId;
//        return this;
//    }
//
//    @Override
//    public String toString() {
//        return "BaseFormElement{" +
//                "label='" + label + '\'' +
//                ", labelPosition='" + labelPosition + '\'' +
//                ", placeholder='" + placeholder + '\'' +
//                ", description='" + description + '\'' +
//                ", tooltip='" + tooltip + '\'' +
//                ", prefix='" + prefix + '\'' +
//                ", suffix='" + suffix + '\'' +
//                ", widget='" + widget + '\'' +
//                ", type='" + type + '\'' +
//                ", inputMask='" + inputMask + '\'' +
//                ", displayMask='" + displayMask + '\'' +
//                ", allowMultipleMasks='" + allowMultipleMasks + '\'' +
//                ", customClass='" + customClass + '\'' +
//                ", tabindex='" + tabindex + '\'' +
//                ", autocomplete='" + autocomplete + '\'' +
//                ", hidden='" + hidden + '\'' +
//                ", hideLabel='" + hideLabel + '\'' +
//                ", showWordCount='" + showWordCount + '\'' +
//                ", showCharCount='" + showCharCount + '\'' +
//                ", mask='" + mask + '\'' +
//                ", autofocus='" + autofocus + '\'' +
//                ", spellcheck='" + spellcheck + '\'' +
//                ", disabled='" + disabled + '\'' +
//                ", tableView='" + tableView + '\'' +
//                ", modalEdit='" + modalEdit + '\'' +
//                ", multiple='" + multiple + '\'' +
//                ", persistent='" + persistent + '\'' +
//                ", inputFormat='" + inputFormat + '\'' +
//                ", dbIndex='" + dbIndex + '\'' +
//                ", truncateMultipleSpaces='" + truncateMultipleSpaces + '\'' +
//                ", encrypted='" + encrypted + '\'' +
//                ", redrawOn='" + redrawOn + '\'' +
//                ", clearOnHide='" + clearOnHide + '\'' +
//                ", customDefaultValue='" + customDefaultValue + '\'' +
//                ", calculateValue='" + calculateValue + '\'' +
//                ", calculateServer='" + calculateServer + '\'' +
//                ", allowCalculateOverride='" + allowCalculateOverride + '\'' +
//                ", validateOn='" + validateOn + '\'' +
//                ", validate='" + validate + '\'' +
//                ", required='" + required + '\'' +
//                ", pattern='" + pattern + '\'' +
//                ", customMessage='" + customMessage + '\'' +
//                ", custom='" + custom + '\'' +
//                ", customPrivate='" + customPrivate + '\'' +
//                ", json='" + json + '\'' +
//                ", minLength='" + minLength + '\'' +
//                ", maxLength='" + maxLength + '\'' +
//                ", strictDateValidation='" + strictDateValidation + '\'' +
//                ", unique='" + unique + '\'' +
//                ", errorLabel='" + errorLabel + '\'' +
//                ", errors='" + errors + '\'' +
//                ", key='" + key + '\'' +
//                ", tags='" + tags + '\'' +
//                ", properties='" + properties + '\'' +
//                ", conditional='" + conditional + '\'' +
//                ", show='" + show + '\'' +
//                ", when='" + when + '\'' +
//                ", eq='" + eq + '\'' +
//                ", customConditional='" + customConditional + '\'' +
//                ", logic='" + logic + '\'' +
//                ", attributes='" + attributes + '\'' +
//                ", overlay='" + overlay + '\'' +
//                ", style='" + style + '\'' +
//                ", page='" + page + '\'' +
//                ", left='" + left + '\'' +
//                ", top='" + top + '\'' +
//                ", width='" + width + '\'' +
//                ", height='" + height + '\'' +
//                ", input='" + input + '\'' +
//                ", refreshOn='" + refreshOn + '\'' +
//                ", dataGridLabel='" + dataGridLabel + '\'' +
//                ", addons='" + addons + '\'' +
//                ", inputType='" + inputType + '\'' +
//                ", id='" + id + '\'' +
//                ", defaultValue='" + defaultValue + '\'' +
//                "fieldName='" + fieldName + '\'' +
//                ", type=" + type +
//                ", fieldType='" + fieldType + '\'' +
//                ", fieldSubType='" + fieldSubType + '\'' +
//                ", fieldLabel='" + fieldLabel + '\'' +
//                ", fieldValue='" + fieldValue + '\'' +
//                ", haveError=" + haveError +
//                ", max='" + max + '\'' +
//                ", min='" + min + '\'' +
//                ", maxLength=" + maxLength +
//                ", rows=" + rows +
//                ", step=" + step +
//                ", className='" + className + '\'' +
//                ", isHaveAccess=" + isHaveAccess +
//                ", role=" + Arrays.toString(role) +
//                ", isRequired=" + isRequired +
//                ", options=" + (options!=null ? options.get(0).isSelected() : "null") +
//                '}';
//    }
//
//    @NonNull
//    @Override
//    public BaseFormElement clone() throws CloneNotSupportedException {
//        return (BaseFormElement) super.clone();
//    }
//}
