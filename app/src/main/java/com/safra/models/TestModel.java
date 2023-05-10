package com.safra.models;

import com.safra.models.formElements.BaseFormElement;

import org.jsoup.helper.Validate;

import java.util.List;
import java.util.Map;

public class TestModel {
    private List<Component> components;

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public static class Component {
        private String label;
        private String labelPosition;
        private String placeholder;
        private String description;
        private String tooltip;
        private String prefix;
        private String suffix;
        private List<BaseFormElement> elementList;
        public String getElementJsonArray() {
            return elementJsonArray;
        }
        private String elementJsonArray;
        //        private Widget widget;
        private String displayMask;
        private String editor;
        private boolean autoExpand;
        private String customClass;
        private String tabindex;
        private String autocomplete;
        private boolean hidden;
        private boolean hideLabel;
        private boolean showWordCount;
        private boolean showCharCount;
        private boolean autofocus;
        private boolean spellcheck;
        private boolean disabled;
        private boolean tableView;
        private boolean modalEdit;
        private boolean multiple;
        private boolean persistent;
        private String inputFormat;
        private boolean isProtected;
        private boolean dbIndex;
        private String caseType;
        private boolean truncateMultipleSpaces;
        private boolean isEncrypted;
        private String redrawOn;
        private boolean clearOnHide;
        private String customDefaultValue;
        private String calculateValue;
        private boolean calculateServer;
        private boolean allowCalculateOverride;
        private String validateOn;
        private Validate validate;
        private boolean isUnique;
        private String errorLabel;
        private String errors;
        private String key;
        private List<String> tags;
        private Map<String, Object> properties;
        //        private Conditional conditional;
        private String customConditional;
        //        private List<Logic> logic;
        private Map<String, Object> attributes;
        //        private Overlay overlay;
        private String type;
        private int rows;
        private boolean wysiwyg;
        private boolean input;
        private String refreshOn;
        private boolean dataGridLabel;
        private boolean allowMultipleMasks;
        private List<String> addons;
        private boolean mask;
        private String inputType;
        private String inputMask;
        private boolean fixedSize;
        private String id;
        private String defaultValue;

        public Component(String label, String labelPosition, String placeholder, String description, String tooltip, String prefix, String suffix, String displayMask, String editor, boolean autoExpand, String customClass, String tabindex, String autocomplete, boolean hidden, boolean hideLabel, boolean showWordCount, boolean showCharCount, boolean autofocus, boolean spellcheck, boolean disabled, boolean tableView, boolean modalEdit, boolean multiple, boolean persistent, String inputFormat, boolean isProtected, boolean dbIndex, String caseType, boolean truncateMultipleSpaces, boolean isEncrypted, String redrawOn, boolean clearOnHide, String customDefaultValue, String calculateValue, boolean calculateServer, boolean allowCalculateOverride, String validateOn, Validate validate, boolean isUnique, String errorLabel, String errors, String key, List<String> tags, Map<String, Object> properties, String customConditional, Map<String, Object> attributes, String type, int rows, boolean wysiwyg, boolean input, String refreshOn, boolean dataGridLabel, boolean allowMultipleMasks, List<String> addons, boolean mask, String inputType, String inputMask, boolean fixedSize, String id, String defaultValue) {
            this.label = label;
            this.labelPosition = labelPosition;
            this.placeholder = placeholder;
            this.description = description;
            this.tooltip = tooltip;
            this.prefix = prefix;
            this.suffix = suffix;
            this.displayMask = displayMask;
            this.editor = editor;
            this.autoExpand = autoExpand;
            this.customClass = customClass;
            this.tabindex = tabindex;
            this.autocomplete = autocomplete;
            this.hidden = hidden;
            this.hideLabel = hideLabel;
            this.showWordCount = showWordCount;
            this.showCharCount = showCharCount;
            this.autofocus = autofocus;
            this.spellcheck = spellcheck;
            this.disabled = disabled;
            this.tableView = tableView;
            this.modalEdit = modalEdit;
            this.multiple = multiple;
            this.persistent = persistent;
            this.inputFormat = inputFormat;
            this.isProtected = isProtected;
            this.dbIndex = dbIndex;
            this.caseType = caseType;
            this.truncateMultipleSpaces = truncateMultipleSpaces;
            this.isEncrypted = isEncrypted;
            this.redrawOn = redrawOn;
            this.clearOnHide = clearOnHide;
            this.customDefaultValue = customDefaultValue;
            this.calculateValue = calculateValue;
            this.calculateServer = calculateServer;
            this.allowCalculateOverride = allowCalculateOverride;
            this.validateOn = validateOn;
            this.validate = validate;
            this.isUnique = isUnique;
            this.errorLabel = errorLabel;
            this.errors = errors;
            this.key = key;
            this.tags = tags;
            this.properties = properties;
            this.customConditional = customConditional;
            this.attributes = attributes;
            this.type = type;
            this.rows = rows;
            this.wysiwyg = wysiwyg;
            this.input = input;
            this.refreshOn = refreshOn;
            this.dataGridLabel = dataGridLabel;
            this.allowMultipleMasks = allowMultipleMasks;
            this.addons = addons;
            this.mask = mask;
            this.inputType = inputType;
            this.inputMask = inputMask;
            this.fixedSize = fixedSize;
            this.id = id;
            this.defaultValue = defaultValue;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabelPosition() {
            return labelPosition;
        }

        public void setLabelPosition(String labelPosition) {
            this.labelPosition = labelPosition;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<BaseFormElement> getElementList() {
            return elementList;
        }

        public String getTooltip() {
            return tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public String getDisplayMask() {
            return displayMask;
        }

        public void setDisplayMask(String displayMask) {
            this.displayMask = displayMask;
        }

        public String getEditor() {
            return editor;
        }

        public void setEditor(String editor) {
            this.editor = editor;
        }

        public boolean isAutoExpand() {
            return autoExpand;
        }

        public void setAutoExpand(boolean autoExpand) {
            this.autoExpand = autoExpand;
        }

        public String getCustomClass() {
            return customClass;
        }

        public void setCustomClass(String customClass) {
            this.customClass = customClass;
        }

        public String getTabindex() {
            return tabindex;
        }

        public void setTabindex(String tabindex) {
            this.tabindex = tabindex;
        }

        public String getAutocomplete() {
            return autocomplete;
        }

        public void setAutocomplete(String autocomplete) {
            this.autocomplete = autocomplete;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public boolean isHideLabel() {
            return hideLabel;
        }

        public void setHideLabel(boolean hideLabel) {
            this.hideLabel = hideLabel;
        }

        public boolean isShowWordCount() {
            return showWordCount;
        }

        public void setShowWordCount(boolean showWordCount) {
            this.showWordCount = showWordCount;
        }

        public boolean isShowCharCount() {
            return showCharCount;
        }

        public void setShowCharCount(boolean showCharCount) {
            this.showCharCount = showCharCount;
        }

        public boolean isAutofocus() {
            return autofocus;
        }

        public void setAutofocus(boolean autofocus) {
            this.autofocus = autofocus;
        }

        public boolean isSpellcheck() {
            return spellcheck;
        }

        public void setSpellcheck(boolean spellcheck) {
            this.spellcheck = spellcheck;
        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public boolean isTableView() {
            return tableView;
        }

        public void setTableView(boolean tableView) {
            this.tableView = tableView;
        }

        public boolean isModalEdit() {
            return modalEdit;
        }

        public void setModalEdit(boolean modalEdit) {
            this.modalEdit = modalEdit;
        }

        public boolean isMultiple() {
            return multiple;
        }

        public void setMultiple(boolean multiple) {
            this.multiple = multiple;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }

        public String getInputFormat() {
            return inputFormat;
        }

        public void setInputFormat(String inputFormat) {
            this.inputFormat = inputFormat;
        }

        public boolean isProtected() {
            return isProtected;
        }

        public void setProtected(boolean aProtected) {
            isProtected = aProtected;
        }

        public boolean isDbIndex() {
            return dbIndex;
        }

        public void setDbIndex(boolean dbIndex) {
            this.dbIndex = dbIndex;
        }

        public String getCaseType() {
            return caseType;
        }

        public void setCaseType(String caseType) {
            this.caseType = caseType;
        }

        public boolean isTruncateMultipleSpaces() {
            return truncateMultipleSpaces;
        }

        public void setTruncateMultipleSpaces(boolean truncateMultipleSpaces) {
            this.truncateMultipleSpaces = truncateMultipleSpaces;
        }

        public boolean isEncrypted() {
            return isEncrypted;
        }

        public void setEncrypted(boolean encrypted) {
            isEncrypted = encrypted;
        }

        public String getRedrawOn() {
            return redrawOn;
        }

        public void setRedrawOn(String redrawOn) {
            this.redrawOn = redrawOn;
        }

        public boolean isClearOnHide() {
            return clearOnHide;
        }

        public void setClearOnHide(boolean clearOnHide) {
            this.clearOnHide = clearOnHide;
        }

        public String getCustomDefaultValue() {
            return customDefaultValue;
        }

        public void setCustomDefaultValue(String customDefaultValue) {
            this.customDefaultValue = customDefaultValue;
        }

        public String getCalculateValue() {
            return calculateValue;
        }

        public void setCalculateValue(String calculateValue) {
            this.calculateValue = calculateValue;
        }

        public boolean isCalculateServer() {
            return calculateServer;
        }

        public void setCalculateServer(boolean calculateServer) {
            this.calculateServer = calculateServer;
        }

        public boolean isAllowCalculateOverride() {
            return allowCalculateOverride;
        }

        public void setAllowCalculateOverride(boolean allowCalculateOverride) {
            this.allowCalculateOverride = allowCalculateOverride;
        }

        public String getValidateOn() {
            return validateOn;
        }

        public void setValidateOn(String validateOn) {
            this.validateOn = validateOn;
        }

        public Validate getValidate() {
            return validate;
        }

        public void setValidate(Validate validate) {
            this.validate = validate;
        }

        public boolean isUnique() {
            return isUnique;
        }

        public void setUnique(boolean unique) {
            isUnique = unique;
        }

        public String getErrorLabel() {
            return errorLabel;
        }

        public void setErrorLabel(String errorLabel) {
            this.errorLabel = errorLabel;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }

        public String getCustomConditional() {
            return customConditional;
        }

        public void setCustomConditional(String customConditional) {
            this.customConditional = customConditional;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public boolean isWysiwyg() {
            return wysiwyg;
        }

        public void setWysiwyg(boolean wysiwyg) {
            this.wysiwyg = wysiwyg;
        }

        public boolean isInput() {
            return input;
        }

        public void setInput(boolean input) {
            this.input = input;
        }

        public String getRefreshOn() {
            return refreshOn;
        }

        public void setRefreshOn(String refreshOn) {
            this.refreshOn = refreshOn;
        }

        public boolean isDataGridLabel() {
            return dataGridLabel;
        }

        public void setDataGridLabel(boolean dataGridLabel) {
            this.dataGridLabel = dataGridLabel;
        }

        public boolean isAllowMultipleMasks() {
            return allowMultipleMasks;
        }

        public void setAllowMultipleMasks(boolean allowMultipleMasks) {
            this.allowMultipleMasks = allowMultipleMasks;
        }

        public List<String> getAddons() {
            return addons;
        }

        public void setAddons(List<String> addons) {
            this.addons = addons;
        }

        public boolean isMask() {
            return mask;
        }

        public void setMask(boolean mask) {
            this.mask = mask;
        }

        public String getInputType() {
            return inputType;
        }

        public void setInputType(String inputType) {
            this.inputType = inputType;
        }

        public String getInputMask() {
            return inputMask;
        }

        public void setInputMask(String inputMask) {
            this.inputMask = inputMask;
        }

        public boolean isFixedSize() {
            return fixedSize;
        }

        public void setFixedSize(boolean fixedSize) {
            this.fixedSize = fixedSize;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return "Component{" +
                    "label='" + label + '\'' +
                    ", labelPosition='" + labelPosition + '\'' +
                    ", placeholder='" + placeholder + '\'' +
                    ", description='" + description + '\'' +
                    ", tooltip='" + tooltip + '\'' +
                    ", prefix='" + prefix + '\'' +
                    ", suffix='" + suffix + '\'' +
                    ", displayMask='" + displayMask + '\'' +
                    ", editor='" + editor + '\'' +
                    ", autoExpand=" + autoExpand +
                    ", customClass='" + customClass + '\'' +
                    ", tabindex='" + tabindex + '\'' +
                    ", autocomplete='" + autocomplete + '\'' +
                    ", hidden=" + hidden +
                    ", hideLabel=" + hideLabel +
                    ", showWordCount=" + showWordCount +
                    ", showCharCount=" + showCharCount +
                    ", autofocus=" + autofocus +
                    ", spellcheck=" + spellcheck +
                    ", disabled=" + disabled +
                    ", tableView=" + tableView +
                    ", modalEdit=" + modalEdit +
                    ", multiple=" + multiple +
                    ", persistent=" + persistent +
                    ", inputFormat='" + inputFormat + '\'' +
                    ", isProtected=" + isProtected +
                    ", dbIndex=" + dbIndex +
                    ", caseType='" + caseType + '\'' +
                    ", truncateMultipleSpaces=" + truncateMultipleSpaces +
                    ", isEncrypted=" + isEncrypted +
                    ", redrawOn='" + redrawOn + '\'' +
                    ", clearOnHide=" + clearOnHide +
                    ", customDefaultValue='" + customDefaultValue + '\'' +
                    ", calculateValue='" + calculateValue + '\'' +
                    ", calculateServer=" + calculateServer +
                    ", allowCalculateOverride=" + allowCalculateOverride +
                    ", validateOn='" + validateOn + '\'' +
                    ", validate=" + validate +
                    ", isUnique=" + isUnique +
                    ", errorLabel='" + errorLabel + '\'' +
                    ", errors='" + errors + '\'' +
                    ", key='" + key + '\'' +
                    ", tags=" + tags +
                    ", properties=" + properties +
                    ", customConditional='" + customConditional + '\'' +
                    ", attributes=" + attributes +
                    ", type='" + type + '\'' +
                    ", rows=" + rows +
                    ", wysiwyg=" + wysiwyg +
                    ", input=" + input +
                    ", refreshOn='" + refreshOn + '\'' +
                    ", dataGridLabel=" + dataGridLabel +
                    ", allowMultipleMasks=" + allowMultipleMasks +
                    ", addons=" + addons +
                    ", mask=" + mask +
                    ", inputType='" + inputType + '\'' +
                    ", inputMask='" + inputMask + '\'' +
                    ", fixedSize=" + fixedSize +
                    ", id='" + id + '\'' +
                    ", defaultValue='" + defaultValue + '\'' +
                    '}';
        }

        // Getters and setters for the properties

    }
}
