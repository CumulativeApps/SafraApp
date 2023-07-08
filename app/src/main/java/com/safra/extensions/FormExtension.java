package com.safra.extensions;

import static com.safra.utilities.FormElements.TYPE_ACHIEVED_UNIT;
import static com.safra.utilities.FormElements.TYPE_ADDRESS;
import static com.safra.utilities.FormElements.TYPE_CASCADING;
import static com.safra.utilities.FormElements.TYPE_CHECKBOX;
import static com.safra.utilities.FormElements.TYPE_DATE;
import static com.safra.utilities.FormElements.TYPE_DATETIME;
import static com.safra.utilities.FormElements.TYPE_EMAIL;
import static com.safra.utilities.FormElements.TYPE_FILE;
import static com.safra.utilities.FormElements.TYPE_HEADER;
import static com.safra.utilities.FormElements.TYPE_LOCATION;
import static com.safra.utilities.FormElements.TYPE_MONTH;
import static com.safra.utilities.FormElements.TYPE_NUMBER;
import static com.safra.utilities.FormElements.TYPE_PASSWORD;
import static com.safra.utilities.FormElements.TYPE_PLACE_TARGET;
import static com.safra.utilities.FormElements.TYPE_QUIZ_MCQ;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_ANSWER;
import static com.safra.utilities.FormElements.TYPE_QUIZ_TEXT_POINT;
import static com.safra.utilities.FormElements.TYPE_RADIO_GROUP;
import static com.safra.utilities.FormElements.TYPE_SELECT;
import static com.safra.utilities.FormElements.TYPE_SELECT_BOXES_GROUP;
import static com.safra.utilities.FormElements.TYPE_SEPARATOR;
import static com.safra.utilities.FormElements.TYPE_TEL;
import static com.safra.utilities.FormElements.TYPE_TEXT;
import static com.safra.utilities.FormElements.TYPE_TEXT_AREA;
import static com.safra.utilities.FormElements.TYPE_TIME;
import static com.safra.utilities.FormElements.TYPE_UNIT_PRICE;
import static com.safra.utilities.FormElements.TYPE_URL;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.safra.R;
import com.safra.models.CascadeOptionItem;
import com.safra.models.FileItem;
import com.safra.models.OptionItem;
import com.safra.models.formElements.AchievedUnitFormElement;
import com.safra.models.formElements.AddressFormElement;
import com.safra.models.formElements.BaseFormElement;
import com.safra.models.formElements.CascadeFormElement;
import com.safra.models.formElements.CascadeSelectFormElement;
import com.safra.models.formElements.CheckBoxFormElement;
import com.safra.models.formElements.DateFormElement;
import com.safra.models.formElements.EmailFormElement;
import com.safra.models.formElements.FileFormElement;
import com.safra.models.formElements.HeaderFormElement;
import com.safra.models.formElements.HtmlElementFormElement;
import com.safra.models.formElements.LocationFormElement;
import com.safra.models.formElements.MonthFormElement;
import com.safra.models.formElements.NumberFormElement;
import com.safra.models.formElements.PasswordFormElement;
import com.safra.models.formElements.PlaceTargetFormElement;
import com.safra.models.formElements.QuizMcqFormElement;
import com.safra.models.formElements.QuizTextAnswerFormElement;
import com.safra.models.formElements.QuizTextFormElement;
import com.safra.models.formElements.QuizTextPointFormElement;
import com.safra.models.formElements.RadioGroupFormElement;
import com.safra.models.formElements.SelectBoxesGroupFormElement;
import com.safra.models.formElements.SelectFormElement;
import com.safra.models.formElements.SeparatorFormElement;
import com.safra.models.formElements.TelephoneFormElement;
import com.safra.models.formElements.TextAreaFormElement;
import com.safra.models.formElements.TextFormElement;
import com.safra.models.formElements.TimeFormElement;
import com.safra.models.formElements.UnitPriceFormElement;
import com.safra.models.formElements.UrlFormElement;
import com.safra.models.formElements.WeekFormElement;
import com.safra.utilities.FormBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FormExtension {

    /**
     * Add a form element to form builder
     *
     * @param TAG         tag of activity or fragment from which call is made
     * @param formBuilder instance of form builder
     * @param bundle      data of form element as a bundle
     */
    public static void addFormElement(Context context, String TAG, FormBuilder formBuilder,
                                      Bundle bundle, long languageId) {
        switch (bundle.getInt("type")) {
            case TYPE_TEXT:
                formBuilder.addFormElement(TextFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("textfield-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_CHECKBOX:
                formBuilder.addFormElement(CheckBoxFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("checkbox-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_ADDRESS:
                formBuilder.addFormElement(AddressFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("address-" + (Calendar.getInstance().getTimeInMillis())));
                break;
//            case TYPE_SIGNATURE:
//                formBuilder.addFormElement(SignatureFormElement.createInstance()
//                        .setFieldLabel(bundle.getString("label"))
//                        .setFieldName("signature-" + (Calendar.getInstance().getTimeInMillis())));
//                break;
//            case TYPE_HTML_ELEMENT:
//                formBuilder.addFormElement(HtmlElementFormElement.createInstance()
//                        .setFieldLabel(bundle.getString("label"))
//                        .setFieldName("HTMLElement-" + (Calendar.getInstance().getTimeInMillis())));
//                break;
            case TYPE_URL:
                formBuilder.addFormElement(UrlFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("url-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_TEXT_AREA:
                formBuilder.addFormElement(TextAreaFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("textarea-" + Calendar.getInstance().getTimeInMillis())
                        .setRows(0)
                        .setMaxLength(0));
                break;
            case TYPE_EMAIL:
                formBuilder.addFormElement(EmailFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_PASSWORD:
                formBuilder.addFormElement(PasswordFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_TEL:
                formBuilder.addFormElement(TelephoneFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_LOCATION:
                formBuilder.addFormElement(LocationFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_NUMBER:
                formBuilder.addFormElement(NumberFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setMin("")
                        .setMax("")
                        .setStep(0)
                        .setFieldName("number-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_ACHIEVED_UNIT:
                formBuilder.addFormElement(AchievedUnitFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setMin("")
                        .setMax("")
                        .setStep(0)
                        .setFieldName("number-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_UNIT_PRICE:
                formBuilder.addFormElement(UnitPriceFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setMin("")
                        .setMax("")
                        .setStep(0)
                        .setFieldName("number-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_DATE:
                formBuilder.addFormElement(DateFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("date-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_FILE:
                formBuilder.addFormElement(FileFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("file-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_TIME:
                formBuilder.addFormElement(TimeFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_MONTH:
                formBuilder.addFormElement(MonthFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_DATETIME:
                formBuilder.addFormElement(WeekFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("text-" + (Calendar.getInstance().getTimeInMillis())));
                break;
            case TYPE_RADIO_GROUP:
                List<OptionItem> optionsR = new ArrayList<>();
                String oR = LanguageExtension.setText(languageId, "option", context.getString(R.string.option));
                String oRV = oR.toLowerCase();
                optionsR.add(new OptionItem(oR + " 1", oRV + "-1", false));
//                optionsR.add(new OptionItem(oR + " 2", oRV + "-2", false));
//                optionsR.add(new OptionItem(oR + " 3", oRV + "-3", false));
                formBuilder.addFormElement(RadioGroupFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("radio-group-" + (Calendar.getInstance().getTimeInMillis()))
                        .setOptions(optionsR));
                break;
            case TYPE_SELECT_BOXES_GROUP:
                List<OptionItem> optionsC = new ArrayList<>();
                String oC = LanguageExtension.setText(languageId, "option", context.getString(R.string.option));
                String oCV = oC.toLowerCase();
                optionsC.add(new OptionItem(oC + " 1", oCV + "-1", false));
//                optionsC.add(new OptionItem(oC + " 2", oCV + "-2", false));
//                optionsC.add(new OptionItem(oC + " 3", oCV + "-3", false));
                formBuilder.addFormElement(SelectBoxesGroupFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("checkbox-group-" + (Calendar.getInstance().getTimeInMillis()))
                        .setOptions(optionsC));
                break;

            case TYPE_SELECT:
                List<OptionItem> optionS = new ArrayList<>();
                String oS = LanguageExtension.setText(languageId, "option", context.getString(R.string.option));
                String oSV = oS.toLowerCase();
                optionS.add(new OptionItem(oS + " 1", oSV + "-1", false));
                optionS.add(new OptionItem(oS + " 2", oSV + "-2", false));
                optionS.add(new OptionItem(oS + " 3", oSV + "-3", false));
                formBuilder.addFormElement(SelectFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("select-" + (Calendar.getInstance().getTimeInMillis()))
                        .setOptions(optionS));
                break;

            case TYPE_PLACE_TARGET:
                List<OptionItem> optionP = new ArrayList<>();
                String oP = LanguageExtension.setText(languageId, "place", context.getString(R.string.place));
                String oPV = LanguageExtension.setText(languageId, "target", context.getString(R.string.target)).toLowerCase();
                optionP.add(new OptionItem(oP + " 1", oPV + "-1", false));
                optionP.add(new OptionItem(oP + " 2", oPV + "-2", false));
                optionP.add(new OptionItem(oP + " 3", oPV + "-3", true));
                formBuilder.addFormElement(PlaceTargetFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("select-" + (Calendar.getInstance().getTimeInMillis()))
                        .setOptions(optionP));
                break;

            case TYPE_HEADER:
                formBuilder.addFormElement(HeaderFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldSubType("h1"));
                break;

            case TYPE_SEPARATOR:
                formBuilder.addFormElement(SeparatorFormElement.createInstance()
                        .setFieldName("break-" + (Calendar.getInstance().getTimeInMillis())));
                break;

            case TYPE_QUIZ_MCQ:
                List<OptionItem> optionsM = new ArrayList<>();
                String oM = LanguageExtension.setText(languageId, "answer", context.getString(R.string.answer));
                optionsM.add(new OptionItem(oM + " 1", "1", false));
                optionsM.add(new OptionItem(oM + " 2", "2", false));
                optionsM.add(new OptionItem(oM + " 3", "3", false));
                optionsM.add(new OptionItem(oM + " 4", "4", false));
                formBuilder.addFormElement(QuizMcqFormElement.createInstance()
                        .setFieldLabel(bundle.getString("label"))
                        .setFieldName("radio-group-" + (Calendar.getInstance().getTimeInMillis()))
                        .setOptions(optionsM));
                break;
            case TYPE_QUIZ_TEXT:
                addTextFieldWithMarksElement(context, TAG, formBuilder, bundle, languageId);
                break;
        }
    }

    /**
     * Add TextField with points element to form builder
     *
     * @param context     context of activity or fragment from which call is made
     * @param TAG         tag of activity or fragment from which call is made
     * @param formBuilder instance of form builder
     * @param bundle      data for form element as bundle
     * @param languageId  current selected language
     */
    private static void addTextFieldWithMarksElement(Context context, String TAG, FormBuilder formBuilder,
                                                     Bundle bundle, long languageId) {
        QuizTextFormElement tmfe = QuizTextFormElement.createInstance();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        List<BaseFormElement> elementList = new ArrayList<>();
        elementList.add(QuizTextAnswerFormElement.createInstance()
                .setFieldLabel(LanguageExtension.setText(languageId, "question", context.getString(R.string.question)))
                .setClassName("form-control mcq mcq-text mcq_" + currentTime)
                .setFieldName("textarea-" + Calendar.getInstance().getTimeInMillis())
                .setRows(0)
                .setMaxLength(0)
                .setRequired(true));
        elementList.add(QuizTextPointFormElement.createInstance()
                .setFieldLabel(LanguageExtension.setText(languageId, "max_point_1", context.getString(R.string.max_point_1))
                        + " " + 5 + " " + LanguageExtension.setText(languageId, "max_point_2", context.getString(R.string.max_point_2)))
                .setClassName("form-control mcq mcq-text mcq_" + currentTime)
                .setFieldName("number-" + (Calendar.getInstance().getTimeInMillis()))
                .setMin("0")
                .setMax("5")
                .setStep(1)
                .setRequired(true));
        tmfe.setListOfFormElement(elementList);
        formBuilder.addFormElement(tmfe);
    }

    /**
     * Add cascade element to form builder
     *
     * @param TAG         tag of screen of form builder
     * @param formBuilder form builder instance
     * @param cascadeJson JSON string of cascade element
     * @throws JSONException if invalid JSON
     */
    public static void addCascadingSelectElement(String TAG, FormBuilder formBuilder,
                                                 String cascadeJson, int position) throws JSONException {
        JSONArray jsonArray = new JSONArray(cascadeJson);
        if (jsonArray.length() > 0) {
            List<CascadeOptionItem> optionList = getCascadeOptionListFromJSONArray(jsonArray);
            int maxLevel = getMaxLevelForCascadeElement(optionList);

            long currentTime = Calendar.getInstance().getTimeInMillis();

            if (maxLevel > 0) {
                List<BaseFormElement> elementList = new ArrayList<>();
                for (int i = 0; i < maxLevel; i++) {
                    List<CascadeOptionItem> levelList = getCascadeOptionList(i + 1, optionList);

                    elementList.add(CascadeSelectFormElement.createInstance()
                            .setClassName("form-control " + "level_" + (i + 1) + " cascading_select cs_" + currentTime)
                            .setFieldName("select-" + Calendar.getInstance().getTimeInMillis())
                            .setFieldLabel("Select " + (i + 1))
                            .setCascadeOptions(levelList));
                }

                BaseFormElement bfe = CascadeFormElement.createInstance()
                        .setFieldName("cascading_logic_" + currentTime)
                        .setElementList(elementList)
                        .setElementJsonArray(cascadeJson);

                if (position == -1) {
                    formBuilder.addFormElement(bfe);
                } else {
                    formBuilder.updateFormElement(bfe, position);
                }
            }
        }
    }

    /**
     * Find and add cascade element from whole JSONArray of form
     *
     * @param jsonArray      whole JSONArray of form
     * @param fieldTimestamp timestamp to identify related child-parent field from JSONArray
     * @return new CascadeFormElement with its children elements
     * @throws JSONException if invalid JSON
     */
    public static CascadeFormElement addCascadingSelectElementFromJson(JSONArray jsonArray, String fieldTimestamp)
            throws JSONException {
        CascadeFormElement cfe = new CascadeFormElement();
        List<CascadeOptionItem> optionList = new ArrayList<>();
        List<BaseFormElement> elementList = new ArrayList<>();
        // for loop to find hidden type for cascade logic
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has("name")) {
                String name = jsonObject.getString("name");
                if (name.contains("cascading_logic_") && name.contains(fieldTimestamp)) {
                    cfe = CascadeFormElement.createInstance()
                            .setFieldName(jsonObject.getString("name"))
                            .setJsonArrayOfElements(jsonObject.getString("value"));
                    if (jsonObject.has("userData"))
                        cfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                    optionList = getCascadeOptionListFromJSONArray(new JSONArray(cfe.getElementJsonArray()));
                }
            }
        }

        // for loop to find child spinner for cascade field
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has("className")) {
                String className = jsonObject.getString("className");
                if (className.contains("cascading_select") && className.contains(fieldTimestamp)
                        && jsonObject.getString("name").contains("select-")) {
                    int level = GeneralExtension.findLevelFromClassName(className);
                    CascadeSelectFormElement csfe = CascadeSelectFormElement.createInstance()
                            .setFieldName(jsonObject.getString("name"))
                            .setFieldLabel(jsonObject.getString("label"))
                            .setCascadeOptions(getCascadeOptionList(level, optionList));
                    csfe.setClassName(jsonObject.getString("className"));
                    if (jsonObject.has("userData"))
                        csfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                    elementList.add(csfe);
                }
            }
        }

        if (elementList.size() > 0) {
            cfe.setElementList(elementList);
            return cfe;
        }

        return null;
    }

    /**
     * Find and add TextField with points from whole JSONArray of form
     *
     * @param jsonArray      whole JSONArray of form
     * @param fieldTimestamp timestamp to identify related child-parent field from JSONArray
     * @return new TextFieldMarksFormElement with its children elements
     * @throws JSONException if invalid JSON
     */
    public static QuizTextFormElement addTextFieldMarksElementFromJson(JSONArray jsonArray, String fieldTimestamp)
            throws JSONException {
        QuizTextFormElement tfmfe = QuizTextFormElement.createInstance();
        List<BaseFormElement> elementList = new ArrayList<>();
        // for loop to find question field
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has("className") && jsonObject.has("type")) {
                String className = jsonObject.getString("className");
                String type = jsonObject.getString("type");
                if (className.contains("mcq-text") && className.contains(fieldTimestamp)
                        && type.equalsIgnoreCase("textarea")) {
                    String name = "", label = "", value = "", role = "";
                    int maxLength = 0, rows = 0;
                    boolean isRequired = false, haveAccess = false;

                    if (jsonObject.has("name")) name = jsonObject.getString("name");
                    if (jsonObject.has("label")) label = jsonObject.getString("label");
                    if (jsonObject.has("required")) isRequired = jsonObject.getBoolean("required");
                    if (jsonObject.has("value")) value = jsonObject.getString("value");
                    if (jsonObject.has("role")) role = jsonObject.getString("role");
                    if (jsonObject.has("maxlength")) maxLength = jsonObject.getInt("maxlength");
                    if (jsonObject.has("rows")) rows = jsonObject.getInt("rows");
                    if (jsonObject.has("access")) haveAccess = jsonObject.getBoolean("access");

                    QuizTextAnswerFormElement tafe = QuizTextAnswerFormElement.createInstance()
                            .setFieldLabel(label)
                            .setFieldName(name)
                            .setRequired(isRequired)
                            .setMaxLength(maxLength)
                            .setRows(rows)
                            .setFieldValue(value)
                            .setHaveAccess(haveAccess)
                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                    tafe.setClassName(className);
                    if (jsonObject.has("userData"))
                        tafe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));

                    elementList.add(tafe);
                }
            }
        }

        // for loop to find points field
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has("className") && jsonObject.has("type")) {
                String className = jsonObject.getString("className");
                String type = jsonObject.getString("type");
                if (className.contains("mcq-text") && className.contains(fieldTimestamp)
                        && type.equalsIgnoreCase("number")) {
                    String name = "", label = "", value = "", role = "", min = "", max = "";
                    int step = 0;
                    boolean isRequired = false, haveAccess = false;

                    if (jsonObject.has("name")) name = jsonObject.getString("name");
                    if (jsonObject.has("label")) label = jsonObject.getString("label");
                    if (jsonObject.has("required")) isRequired = jsonObject.getBoolean("required");
                    if (jsonObject.has("value")) value = jsonObject.getString("value");
                    if (jsonObject.has("role")) role = jsonObject.getString("role");
                    if (jsonObject.has("min")) min = jsonObject.getString("min");
                    if (jsonObject.has("max")) max = jsonObject.getString("max");
                    if (jsonObject.has("step")) step = jsonObject.getInt("step");
                    if (jsonObject.has("access")) haveAccess = jsonObject.getBoolean("access");

                    QuizTextPointFormElement nfe = QuizTextPointFormElement.createInstance()
                            .setFieldLabel(label)
                            .setFieldName(name)
                            .setRequired(isRequired)
                            .setMin(min)
                            .setMax(max)
                            .setStep(step)
                            .setFieldValue(value)
                            .setHaveAccess(haveAccess)
                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                    nfe.setClassName(className);
                    if (jsonObject.has("userData"))
                        nfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));

                    elementList.add(nfe);
                }
            }
        }

        if (elementList.size() == 2) {
            tfmfe.setElementList(elementList);
            return tfmfe;
        }

        return null;
    }

    /**
     * Update a form element
     *
     * @param TAG         tag of activity or fragment from which call is made
     * @param formBuilder instance of form builder
     * @param bundle      data of form element as a bundle
     */
    public static void updateFormElement(String TAG, FormBuilder formBuilder, Bundle bundle) {
        String name = bundle.getString("name");
        int position = bundle.getInt("position");
        int childPosition = -1;
        if (bundle.containsKey("child_position"))
            childPosition = bundle.getInt("child_position");
        Log.e(TAG, "updateFormElement: " + position);

        String label = bundle.containsKey("label") ? bundle.getString("label") : "";
        String value = bundle.containsKey("value") ? bundle.getString("value") : "";
        boolean isRequired = bundle.containsKey("is_required") && bundle.getBoolean("is_required");
        boolean limitAccess = bundle.containsKey("limit_access") && bundle.getBoolean("limit_access");

        Long[] role;
        if (bundle.containsKey("role")) {
            role = GeneralExtension.fromPrimitiveLong(bundle.getLongArray("role"));
        } else {
            role = new Long[0];
        }

        BaseFormElement baseFormElement = formBuilder.getFormElement(position);
        if (childPosition == -1) {
            switch (bundle.getInt("type")) {
                case TYPE_TEXT:
                case TYPE_CHECKBOX:
                case TYPE_ADDRESS:
//                case TYPE_SIGNATURE:
//                case TYPE_HTML_ELEMENT:
                case TYPE_URL:
                case TYPE_EMAIL:
                case TYPE_PASSWORD:
                case TYPE_TEL:
                case TYPE_FILE:
                case TYPE_DATETIME:
                case TYPE_MONTH:
                case TYPE_TIME:
                case TYPE_DATE:
                case TYPE_LOCATION:
                    baseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        baseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
                case TYPE_TEXT_AREA:
                    int maxLength = bundle.containsKey("max_length") ? bundle.getInt("max_length") : 0;
                    int rows = bundle.containsKey("rows") ? bundle.getInt("rows") : 0;
                    Log.e(TAG, "updateFormElement: rows -> " + rows);
                    baseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setRows(rows)
                            .setMaxLength(maxLength)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        baseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
                case TYPE_NUMBER:
                case TYPE_ACHIEVED_UNIT:
                case TYPE_UNIT_PRICE:
                    String min = bundle.containsKey("min") ? bundle.getString("min") : "";
                    String max = bundle.containsKey("max") ? bundle.getString("max") : "";
                    int step = bundle.containsKey("step") ? bundle.getInt("step") : 0;
                    baseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setMin(min)
                            .setMax(max)
                            .setStep(step)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        baseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
                case TYPE_RADIO_GROUP:
                case TYPE_SELECT_BOXES_GROUP:
                case TYPE_SELECT:
                case TYPE_PLACE_TARGET:
                    List<OptionItem> optionList = bundle.containsKey("option_list") ? bundle.getParcelableArrayList("option_list") : new ArrayList<>();
                    baseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setOptions(optionList)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        baseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
                case TYPE_QUIZ_MCQ:
                    List<OptionItem> mcqOptionList = bundle.containsKey("mcq_option_list") ?
                            bundle.getParcelableArrayList("mcq_option_list") : new ArrayList<>();
                    baseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setOptions(mcqOptionList)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        baseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
                case TYPE_HEADER:
                case TYPE_SEPARATOR:
                    baseFormElement.setFieldLabel(label);
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
            }
        } else {
            BaseFormElement childBaseFormElement = baseFormElement.getElementList().get(childPosition);

            for (int i = 0; i < baseFormElement.getElementList().size(); i++) {
                BaseFormElement bfe = baseFormElement.getElementList().get(i);
                bfe.setRequired(isRequired);
                baseFormElement.getElementList().set(i, bfe);
            }

            switch (bundle.getInt("type")) {
                case TYPE_QUIZ_TEXT_ANSWER:
                    int maxLength = bundle.containsKey("max_length") ? bundle.getInt("max_length") : 0;
                    int rows = bundle.containsKey("rows") ? bundle.getInt("rows") : 0;
                    Log.e(TAG, "updateFormElement: rows -> " + rows);
                    childBaseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setRows(rows)
                            .setMaxLength(maxLength)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        childBaseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    baseFormElement.getElementList().set(childPosition, childBaseFormElement);
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
                case TYPE_QUIZ_TEXT_POINT:
                    String min = bundle.containsKey("min") ? bundle.getString("min") : "";
                    String max = bundle.containsKey("max") ? bundle.getString("max") : "";
                    int step = bundle.containsKey("step") ? bundle.getInt("step") : 0;
                    childBaseFormElement
                            .setFieldLabel(label)
                            .setFieldValue(value)
                            .setRequired(isRequired)
                            .setMin(min)
                            .setMax(max)
                            .setStep(step)
                            .setHaveAccess(limitAccess);
                    if (limitAccess && role.length > 0)
                        childBaseFormElement.setRole(role);
                    Log.e(TAG, "onActivityResult: updating to builder");
                    baseFormElement.getElementList().set(childPosition, childBaseFormElement);
                    formBuilder.updateFormElement(baseFormElement, position);
                    break;
            }
        }
    }

    /**
     * Convert form element to JSONObject
     *
     * @param elementList list of elements as BaseFormElement
     * @throws JSONException if invalid data
     */
    public static JSONArray convertElementToJSON(List<BaseFormElement> elementList) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (BaseFormElement baseFormElement : elementList) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("className", baseFormElement.getClassName());
            jsonObject.put("required", baseFormElement.isRequired());
            jsonObject.put("label", baseFormElement.getFieldLabel());
            jsonObject.put("name", baseFormElement.getFieldName());
            jsonObject.put("access", baseFormElement.isHaveAccess());
            jsonObject.put("content", baseFormElement.getFieldValue());

            System.out.println("baseFormElement.getFieldContent():-" + baseFormElement.getFieldValue());

//            jsonObject.put("label", baseFormElement.getLabel());
//            jsonObject.put("labelPosition", baseFormElement.getLabelPosition());
//            jsonObject.put("placeholder", baseFormElement.getPlaceholder());
//            jsonObject.put("description", baseFormElement.getDescription());
//            jsonObject.put("tooltip", baseFormElement.getTooltip());
//            jsonObject.put("prefix", baseFormElement.getPrefix());
//            jsonObject.put("suffix", baseFormElement.getSuffix());
//            jsonObject.put("widget", baseFormElement.getWidget());
//            jsonObject.put("inputMask", baseFormElement.getInputMask());
//            jsonObject.put("displayMask", baseFormElement.getDisplayMask());
//            jsonObject.put("allowMultipleMasks", baseFormElement.getAllowMultipleMasks());
//            jsonObject.put("customClass", baseFormElement.getCustomClass());
//            jsonObject.put("tabindex", baseFormElement.getTabindex());
//            jsonObject.put("autocomplete", baseFormElement.getLabelPosition());
//            jsonObject.put("hidden", false);
//            jsonObject.put("hideLabel", false);
//            jsonObject.put("showWordCount", true);
//            jsonObject.put("showCharCount", true);
//            jsonObject.put("mask", false);
//            jsonObject.put("autofocus", true);
//            jsonObject.put("spellcheck", true);
//            jsonObject.put("disabled", false);
//            jsonObject.put("tableView", true);
//            jsonObject.put("modalEdit", false);
//            jsonObject.put("multiple", false);
//            jsonObject.put("persistent", true);
//            jsonObject.put("inputFormat", baseFormElement.getLabelPosition());
//            jsonObject.put("dbIndex", false);
//            jsonObject.put("truncateMultipleSpaces", false);
//            jsonObject.put("encrypted", false);
//            jsonObject.put("redrawOn", baseFormElement.getLabelPosition());
//            jsonObject.put("clearOnHide", true);
//            jsonObject.put("customDefaultValue", baseFormElement.getLabelPosition());
//            jsonObject.put("calculateValue", baseFormElement.getLabelPosition());
//            jsonObject.put("calculateServer", false);
//            jsonObject.put("allowCalculateOverride", false);
//            jsonObject.put("validateOn", baseFormElement.getLabelPosition());
//            jsonObject.put("validate", true);
//            jsonObject.put("required", true);
//            jsonObject.put("pattern", baseFormElement.getLabelPosition());
//            jsonObject.put("customMessage", baseFormElement.getLabelPosition());
//            jsonObject.put("custom", baseFormElement.getLabelPosition());
//            jsonObject.put("customPrivate", false);
//            jsonObject.put("json", baseFormElement.getLabelPosition());
//            jsonObject.put("minLength", 0);
//            jsonObject.put("Length", 50);
//            jsonObject.put("strictDateValidation", false);
//            jsonObject.put("unique", false);
//            jsonObject.put("errorLabel", baseFormElement.getLabelPosition());
//            jsonObject.put("errors", baseFormElement.getLabelPosition());
//            jsonObject.put("key", "text-1682684197287");
//            jsonObject.put("tags", baseFormElement.getLabelPosition());
//            jsonObject.put("properties", new JSONObject());
//            jsonObject.put("conditional", new JSONObject());
//            jsonObject.put("show", true);
//            jsonObject.put("when", baseFormElement.getLabelPosition());
//            jsonObject.put("eq", baseFormElement.getLabelPosition());
//            jsonObject.put("customConditional", baseFormElement.getLabelPosition());
//            jsonObject.put("logic", baseFormElement.getLabelPosition());
//            jsonObject.put("attributes", new JSONObject());
//            jsonObject.put("overlay", baseFormElement.getLabelPosition());
//            jsonObject.put("style", baseFormElement.getLabelPosition());
//            jsonObject.put("page", baseFormElement.getLabelPosition());
//            jsonObject.put("left", baseFormElement.getLabelPosition());
//            jsonObject.put("top", baseFormElement.getLabelPosition());
//            jsonObject.put("width", baseFormElement.getLabelPosition());
//            jsonObject.put("height", baseFormElement.getLabelPosition());
//            jsonObject.put("input", true);
//            jsonObject.put("refreshOn", baseFormElement.getLabelPosition());
//            jsonObject.put("dataGridLabel", baseFormElement.getLabelPosition());
//            jsonObject.put("addons", new JSONArray());
//            jsonObject.put("inputType", baseFormElement.getLabelPosition());
//            jsonObject.put("id", baseFormElement.getLabelPosition());

            if (baseFormElement.isHaveAccess()) {
                StringBuilder sb = new StringBuilder();
                for (Long i : baseFormElement.getRole()) {
                    if (sb.toString().isEmpty()) {
                        sb.append(i);
                    } else {
                        sb.append(",").append(i);
                    }
                }
                jsonObject.put("role", sb.toString());
            }

            jsonObject.put("type", baseFormElement.getFieldType());

            switch (baseFormElement.getType()) {
                case TYPE_TEXT:
                case TYPE_CHECKBOX:
                case TYPE_ADDRESS:
//                case TYPE_SIGNATURE:
//                case TYPE_HTML_ELEMENT:
                case TYPE_URL:
                case TYPE_EMAIL:
                case TYPE_PASSWORD:
                case TYPE_TEL:
                case TYPE_MONTH:
                case TYPE_DATETIME:
                case TYPE_TIME:
                case TYPE_LOCATION:
                case TYPE_HEADER:
                    jsonObject.put("subtype", baseFormElement.getFieldSubType());
                    break;
                case TYPE_TEXT_AREA:
                    if (baseFormElement.getRows() > 0)
                        jsonObject.put("rows", baseFormElement.getRows());
                    if (baseFormElement.getMaxLength() > 0)
                        jsonObject.put("maxlength", baseFormElement.getMaxLength());
                    break;
                case TYPE_NUMBER:
                case TYPE_ACHIEVED_UNIT:
                case TYPE_UNIT_PRICE:
                    if (!baseFormElement.getMax().isEmpty())
                        jsonObject.put("max", Integer.parseInt(baseFormElement.getMax()));
                    if (!baseFormElement.getMin().isEmpty())
                        jsonObject.put("min", Integer.parseInt(baseFormElement.getMin()));
                    if (baseFormElement.getStep() > 0)
                        jsonObject.put("step", baseFormElement.getStep());
                    break;
                case TYPE_SELECT_BOXES_GROUP:
                case TYPE_RADIO_GROUP:
                case TYPE_QUIZ_MCQ:
                case TYPE_SELECT:
                case TYPE_PLACE_TARGET:
                    if (baseFormElement.getOptions().size() > 0) {
                        JSONArray valueArray = new JSONArray();
                        for (OptionItem o : baseFormElement.getOptions()) {
                            JSONObject valueObject = new JSONObject();
                            valueObject.put("label", o.getOptionKey());
                            valueObject.put("value", o.getOptionValue());
//                            valueObject.put("selected", o.isSelected());

                            valueArray.put(valueObject);
                        }

                        jsonObject.put("values", valueArray);
                    }
                    break;
                case TYPE_CASCADING:
                    jsonObject.put("value", baseFormElement.getElementJsonArray());
                    if (baseFormElement.getElementList() != null && baseFormElement.getElementList().size() > 0) {
                        for (BaseFormElement bfe : baseFormElement.getElementList()) {
                            JSONObject elementJson = new JSONObject();
                            elementJson.put("className", bfe.getClassName());
                            elementJson.put("required", bfe.isRequired());
                            elementJson.put("label", bfe.getFieldLabel());
                            elementJson.put("name", bfe.getFieldName());
                            elementJson.put("access", bfe.isHaveAccess());
                            elementJson.put("type", bfe.getFieldType());

                            JSONArray valueArray = new JSONArray();

                            if (bfe.getOptions().size() > 0) {
                                for (OptionItem o : bfe.getOptions()) {
                                    JSONObject valueObject = new JSONObject();
                                    valueObject.put("label", o.getOptionKey());
                                    valueObject.put("value", o.getOptionValue());
//                                    valueObject.put("selected", o.isSelected());

                                    valueArray.put(valueObject);
                                }

                                elementJson.put("values", valueArray);
                            }

                            if (bfe.getCascadeOptions() != null && !bfe.getCascadeOptions().isEmpty()) {
                                for (CascadeOptionItem cascadeOptionItem : bfe.getCascadeOptions()) {
                                    JSONObject valueObject = new JSONObject();
                                    valueObject.put("label", cascadeOptionItem.getOption());
                                    valueObject.put("value", cascadeOptionItem.getId());
                                    valueObject.put("selected", cascadeOptionItem.isSelected());
                                    valueArray.put(valueObject);
                                }

                                elementJson.put("values", valueArray);
                            }

                            if (bfe.getUserData() != null && bfe.getUserData().size() > 0) {
                                JSONArray userDataJsonArray = new JSONArray();
                                for (String s : bfe.getUserData())
                                    userDataJsonArray.put(s);

                                elementJson.put("userData", userDataJsonArray);
                            }

                            jsonArray.put(elementJson);
                        }
                    }
                    break;
                case TYPE_QUIZ_TEXT:
                    if (baseFormElement.getElementList() != null && baseFormElement.getElementList().size() > 0) {
                        for (BaseFormElement bfe : baseFormElement.getElementList()) {
                            JSONObject elementJson = new JSONObject();
                            elementJson.put("className", bfe.getClassName());
                            elementJson.put("required", bfe.isRequired());
                            elementJson.put("label", bfe.getFieldLabel());
                            elementJson.put("name", bfe.getFieldName());
                            elementJson.put("access", bfe.isHaveAccess());
                            elementJson.put("type", bfe.getFieldType());

                            switch (bfe.getType()) {
                                case TYPE_TEXT_AREA:
                                    if (bfe.getRows() > 0)
                                        elementJson.put("rows", bfe.getRows());
                                    if (bfe.getMaxLength() > 0)
                                        elementJson.put("maxlength", bfe.getMaxLength());
                                    break;
                                case TYPE_NUMBER:
                                    if (!bfe.getMax().isEmpty())
                                        elementJson.put("max", Integer.parseInt(bfe.getMax()));
                                    if (!bfe.getMin().isEmpty())
                                        elementJson.put("min", Integer.parseInt(bfe.getMin()));
                                    if (bfe.getStep() > 0)
                                        elementJson.put("step", bfe.getStep());
                                    break;
                            }

                            if (bfe.getUserData() != null && bfe.getUserData().size() > 0) {
                                JSONArray userDataJsonArray = new JSONArray();
                                for (String s : bfe.getUserData())
                                    userDataJsonArray.put(s);

                                elementJson.put("userData", userDataJsonArray);
                            }

                            jsonArray.put(elementJson);

                        }
                    }
                    break;
                case TYPE_DATE:
                case TYPE_FILE:
                case TYPE_SEPARATOR:
                    break;

            }

            if (baseFormElement.getType() != TYPE_QUIZ_TEXT) {
                if (baseFormElement.getUserData() != null && baseFormElement.getUserData().size() > 0) {
                    JSONArray userDataJsonArray = new JSONArray();
                    for (String s : baseFormElement.getUserData())
                        userDataJsonArray.put(s);

                    jsonObject.put("userData", userDataJsonArray);
                }
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * Convert JSONObject to form element
     *
     * @param formBuilder - instance of form builder
     * @param jsonArray   - JSONArray of form element that want to convert
     * @throws JSONException - if invalid JSONObject
     */
    public static void convertJSONToElement(FormBuilder formBuilder, JSONArray jsonArray)
            throws JSONException {
        List<String> traverseFieldTimeStamp = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Log.e("FormExtension", "convertJSONToElementForResponse: " + jsonObject);

            String name = "", label = "", className = "", type = "", subType = "", value = "",
                    role = "", min = "", max = "";
            JSONArray values = new JSONArray();
            int maxLength = 0, rows = 0, step = 0;
            boolean isRequired = false, haveAccess = false;
            ;

            if (jsonObject.has("name")) name = jsonObject.getString("name");
            if (jsonObject.has("label")) label = jsonObject.getString("label");
            if (jsonObject.has("required")) isRequired = jsonObject.getBoolean("required");
            if (jsonObject.has("className")) className = jsonObject.getString("className");
            if (jsonObject.has("type")) type = jsonObject.getString("type");
            if (jsonObject.has("subtype")) subType = jsonObject.getString("subtype");
            if (jsonObject.has("value")) value = jsonObject.getString("value");
            if (jsonObject.has("role")) role = jsonObject.getString("role");
            if (jsonObject.has("min")) min = jsonObject.getString("min");
            if (jsonObject.has("max")) max = jsonObject.getString("max");
            if (jsonObject.has("values")) values = jsonObject.getJSONArray("values");
            if (jsonObject.has("maxlength")) maxLength = jsonObject.getInt("maxlength");
            if (jsonObject.has("rows")) rows = jsonObject.getInt("rows");
            if (jsonObject.has("step")) step = jsonObject.getInt("step");
            if (jsonObject.has("access")) haveAccess = jsonObject.getBoolean("access");


            if ((!className.isEmpty() && className.contains("cascading_select"))
                    || type.equalsIgnoreCase("hidden")) {
                String fieldTimeStamp = null;
                if (!className.isEmpty())
                    fieldTimeStamp = className.substring(className.lastIndexOf("_") + 1);
                else if (type.equalsIgnoreCase("hidden"))
                    fieldTimeStamp = name.substring((name.lastIndexOf("_")) + 1);
                if (!traverseFieldTimeStamp.contains(fieldTimeStamp)) {
                    CascadeFormElement cfe = addCascadingSelectElementFromJson(jsonArray, fieldTimeStamp);
                    if (cfe != null)
                        formBuilder.addFormElement(cfe);
                    traverseFieldTimeStamp.add(fieldTimeStamp);
                }
            } else if ((!className.isEmpty() && className.contains("mcq-text"))) {
                String fieldTimeStamp = className.substring(className.lastIndexOf("_") + 1);
                if (!traverseFieldTimeStamp.contains(fieldTimeStamp)) {
                    QuizTextFormElement tfmfe = addTextFieldMarksElementFromJson(jsonArray, fieldTimeStamp);
                    if (tfmfe != null)
                        formBuilder.addFormElement(tfmfe);
                    traverseFieldTimeStamp.add(fieldTimeStamp);
                }
            } else {
                if (!type.isEmpty()) {
                    switch (type) {
                        case "email":
                            EmailFormElement efe = EmailFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(efe);
                            break;
                        case "password":
                            PasswordFormElement pfe = PasswordFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(pfe);
                            break;
                        case "textfield":
                            if (label.contains("Text Field")) {

                                TextFormElement tfe = TextFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                formBuilder.addFormElement(tfe);
                            }else {
                                LocationFormElement lfe = LocationFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                formBuilder.addFormElement(lfe);
                            }
                            break;
                        case "address":
                            AddressFormElement tfea = AddressFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(tfea);
                            break;
                        case "htmlelement":
                            HtmlElementFormElement tfeh = HtmlElementFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(tfeh);
                            break;
//                        case "signature":
//                            SignatureFormElement tfes = SignatureFormElement.createInstance()
//                                    .setFieldLabel(label)
//                                    .setFieldName(name)
//                                    .setRequired(isRequired)
//                                    .setFieldValue(value)
//                                    .setHaveAccess(haveAccess)
//                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                            formBuilder.addFormElement(tfes);
//                            break;
                        case "checkbox":
                            CheckBoxFormElement tfee = CheckBoxFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(tfee);
                            break;
                        case "radio":
                            RadioGroupFormElement rgfe = RadioGroupFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(rgfe);
                            break;

                        case "url":
                            UrlFormElement url = UrlFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(url);
                            break;

//                        case "location":
//
////                            if (className.contains("location")) {
//                            LocationFormElement lfe = LocationFormElement.createInstance()
//                                    .setFieldLabel(label)
//                                    .setFieldName(name)
//                                    .setRequired(isRequired)
//                                    .setFieldValue(value)
//                                    .setHaveAccess(haveAccess)
//                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                            formBuilder.addFormElement(lfe);
//                            break;
////                            }
                        case "phoneNumber":
                            TelephoneFormElement tlfe = TelephoneFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(tlfe);
                            break;
                        case "time":
                            TimeFormElement text = TimeFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(text);
                            break;
                        case "currency":
                            UnitPriceFormElement upfe = UnitPriceFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setMin(min)
                                    .setMax(max)
                                    .setStep(step)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(upfe);
                            break;

                        case "day":
                            MonthFormElement mfe = MonthFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(mfe);
                            break;
                        case "datetime":
                            WeekFormElement wfe = WeekFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(wfe);
                            break;
                        case "text":
                            switch (subType) {
//                                case "text":

                            }
                            break;
                        case "header":
                            HeaderFormElement hfe = HeaderFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setFieldSubType(subType);
                            formBuilder.addFormElement(hfe);
                            break;
                        case "break":
                            SeparatorFormElement spfe = SeparatorFormElement.createInstance()
                                    .setFieldName(name)
                                    .setFieldLabel(label);
                            formBuilder.addFormElement(spfe);
                            break;
                        case "textarea":
                            TextAreaFormElement tafe = TextAreaFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setMaxLength(maxLength)
                                    .setRows(rows)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(tafe);
                            break;
                        case "number":
//                            if (className.contains("achieved_unit")) {
//                                AchievedUnitFormElement aufe = AchievedUnitFormElement.createInstance()
//                                        .setFieldLabel(label)
//                                        .setFieldName(name)
//                                        .setRequired(isRequired)
//                                        .setMin(min)
//                                        .setMax(max)
//                                        .setStep(step)
//                                        .setFieldValue(value)
//                                        .setHaveAccess(haveAccess)
//                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                                formBuilder.addFormElement(aufe);
//                            } else {
                                NumberFormElement nfe = NumberFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                formBuilder.addFormElement(nfe);
//                            }
                            break;
                        case "date":
                            DateFormElement dfe = DateFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(dfe);
                            break;
                        case "file":
                            FileFormElement ffe = FileFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(ffe);
                            break;
                        case "radio-group":
                            if (className.contains("mcq")) {
                                QuizMcqFormElement mfee = QuizMcqFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                formBuilder.addFormElement(mfee);

                            }
                        case "selectboxes":
                            SelectBoxesGroupFormElement cgfe = SelectBoxesGroupFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(cgfe);
                            break;

                        case "select":
//                            if (label.contains("Place-Goal")) {
//
//                                PlaceTargetFormElement ptfep = PlaceTargetFormElement.createInstance()
//                                        .setFieldLabel("Place-Goal")
//                                        .setFieldName(name)
//                                        .setRequired(isRequired)
//                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
//                                        .setFieldValue(value)
//                                        .setHaveAccess(haveAccess)
//                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                                formBuilder.addFormElement(ptfep);
//                            } else {
                                SelectFormElement sfe = SelectFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                formBuilder.addFormElement(sfe);

//                            }
                            break;
//                        case "fieldset":
//                            PlaceTargetFormElement ptfe = PlaceTargetFormElement.createInstance()
//                                    .setFieldLabel("Place-Goal")
//                                    .setFieldName(name)
//                                    .setRequired(isRequired)
//                                    .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
//                                    .setFieldValue(value)
//                                    .setHaveAccess(haveAccess)
//                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                            formBuilder.addFormElement(ptfe);
//
//                            AchievedUnitFormElement aufe = AchievedUnitFormElement.createInstance()
//                                    .setFieldLabel("Unit Achieved")
//                                    .setFieldName(name)
//                                    .setRequired(isRequired)
//                                    .setMin(min)
//                                    .setMax(max)
//                                    .setStep(step)
//                                    .setFieldValue(value)
//                                    .setHaveAccess(haveAccess)
//                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                            formBuilder.addFormElement(aufe);
//
//                            break;

                        case "container":

                            QuizTextAnswerFormElement ptfeq = QuizTextAnswerFormElement.createInstance()
                                    .setFieldLabel("Question")
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setMaxLength(maxLength)
                                    .setRows(rows)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(ptfeq);

                            QuizTextPointFormElement nfeq = QuizTextPointFormElement.createInstance()
                                    .setFieldLabel("Points")
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setMin(min)
                                    .setMax(max)
                                    .setStep(step)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            formBuilder.addFormElement(nfeq);
                            break;


                    }
                }
            }
        }
    }

    /**
     * Convert JSONObject to form element
     *
     * @param jsonArray   - JSONArray of form element that want to convert
     * @param elementList - List to add elements
     * @throws JSONException - if invalid JSONObject
     */
    public static void convertJSONToElement(List<BaseFormElement> elementList, JSONArray jsonArray)
            throws JSONException {
        List<String> traverseFieldTimeStamp = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Log.e("FormExtension", "convertJSONToElementForResponse: " + jsonObject);

            String name = "", label = "", className = "", type = "", subType = "", value = "",
                    role = "", min = "", max = "";
            JSONArray values = new JSONArray();
            int maxLength = 0, rows = 0, step = 0;
            boolean isRequired = false, haveAccess = false;

            if (jsonObject.has("name")) name = jsonObject.getString("name");
            if (jsonObject.has("label")) label = jsonObject.getString("label");
            if (jsonObject.has("required")) isRequired = jsonObject.getBoolean("required");
            if (jsonObject.has("className")) className = jsonObject.getString("className");
            if (jsonObject.has("type")) type = jsonObject.getString("type");
            if (jsonObject.has("subtype")) subType = jsonObject.getString("subtype");
            if (jsonObject.has("value")) value = jsonObject.getString("value");
            if (jsonObject.has("role")) role = jsonObject.getString("role");
            if (jsonObject.has("min")) min = jsonObject.getString("min");
            if (jsonObject.has("max")) max = jsonObject.getString("max");
            if (jsonObject.has("values")) values = jsonObject.getJSONArray("values");
            if (jsonObject.has("maxlength")) maxLength = jsonObject.getInt("maxlength");
            if (jsonObject.has("rows")) rows = jsonObject.getInt("rows");
            if (jsonObject.has("step")) step = jsonObject.getInt("step");
            if (jsonObject.has("access")) haveAccess = jsonObject.getBoolean("access");

            if ((!className.isEmpty() && className.contains("cascading_select"))
                    || type.equalsIgnoreCase("hidden")) {
                String fieldTimeStamp = null;
                if (!className.isEmpty())
                    fieldTimeStamp = className.substring(className.lastIndexOf("_") + 1);
                else if (type.equalsIgnoreCase("hidden"))
                    fieldTimeStamp = name.substring((name.lastIndexOf("_")) + 1);
                if (!traverseFieldTimeStamp.contains(fieldTimeStamp)) {
                    CascadeFormElement cfe = addCascadingSelectElementFromJson(jsonArray, fieldTimeStamp);
                    if (cfe != null)
                        elementList.add(cfe);
                    traverseFieldTimeStamp.add(fieldTimeStamp);
                }
            } else if ((!className.isEmpty() && className.contains("mcq-text"))) {
                String fieldTimeStamp = className.substring(className.lastIndexOf("_") + 1);
                if (!traverseFieldTimeStamp.contains(fieldTimeStamp)) {
                    QuizTextFormElement tfmfe = addTextFieldMarksElementFromJson(jsonArray, fieldTimeStamp);
                    if (tfmfe != null)
                        elementList.add(tfmfe);
                    traverseFieldTimeStamp.add(fieldTimeStamp);
                }
            } else {
                if (!type.isEmpty()) {
                    switch (type) {
                        case "textfield":
                            TextFormElement tfe = TextFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(tfe);
                            break;
                        case "address":
                            AddressFormElement tfea = AddressFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(tfea);
                            break;
                        case "htmlelement":
                            HtmlElementFormElement tfeh = HtmlElementFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(tfeh);
                            break;
//                        case "signature":
//                            SignatureFormElement tfes = SignatureFormElement.createInstance()
//                                    .setFieldLabel(label)
//                                    .setFieldName(name)
//                                    .setRequired(isRequired)
//                                    .setFieldValue(value)
//                                    .setHaveAccess(haveAccess)
//                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                            elementList.add(tfes);
//                            break;
                        case "checkbox":
                            CheckBoxFormElement tfee = CheckBoxFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(tfee);
                            break;
                        case "text":
                            switch (subType) {
                                case "text":
                                    if (className.contains("location")) {
                                        LocationFormElement lfe = LocationFormElement.createInstance()
                                                .setFieldLabel(label)
                                                .setFieldName(name)
                                                .setRequired(isRequired)
                                                .setFieldValue(value)
                                                .setHaveAccess(haveAccess)
                                                .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                        elementList.add(lfe);
                                    }
                                    break;
                                case "email":
                                    EmailFormElement efe = EmailFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    elementList.add(efe);
                                    break;
                                case "password":
                                    PasswordFormElement pfe = PasswordFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    elementList.add(pfe);
                                    break;
                                case "tel":
                                    TelephoneFormElement tlfe = TelephoneFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    elementList.add(tlfe);
                                    break;
                                case "month":
                                    MonthFormElement mfe = MonthFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    elementList.add(mfe);
                                    break;
                                case "week":
                                    WeekFormElement wfe = WeekFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    elementList.add(wfe);
                                    break;
                                case "time":
                                    TimeFormElement time = TimeFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    elementList.add(time);
                                    break;
                            }
                            break;
                        case "header":
                            HeaderFormElement hfe = HeaderFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setFieldSubType(subType);
                            elementList.add(hfe);
                            break;
                        case "break":
                            SeparatorFormElement spfe = SeparatorFormElement.createInstance()
                                    .setFieldName(name)
                                    .setFieldLabel(label);
                            elementList.add(spfe);
                            break;
                        case "textarea":
                            TextAreaFormElement tafe = TextAreaFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setMaxLength(maxLength)
                                    .setRows(rows)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(tafe);
                            break;
                        case "number":
                            if (className.contains("achieved_unit")) {
                                AchievedUnitFormElement aufe = AchievedUnitFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(aufe);
                            } else if (className.contains("unit_price")) {
                                UnitPriceFormElement upfe = UnitPriceFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(upfe);
                            } else {
                                NumberFormElement nfe = NumberFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(nfe);
                            }
                            break;

                        case "date":
                            DateFormElement dfe = DateFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(dfe);
                            break;
                        case "file":
                            FileFormElement ffe = FileFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(ffe);
                            break;
                        case "radio":
                            if (className.contains("mcq")) {
                                QuizMcqFormElement mfe = QuizMcqFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(mfe);
                            } else {
                                RadioGroupFormElement rgfe = RadioGroupFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(rgfe);
                            }
                            break;
                        case "checkbox-group":
                            SelectBoxesGroupFormElement cgfe = SelectBoxesGroupFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            elementList.add(cgfe);
                            break;
                        case "select":
                            if (className.contains("place_target")) {
                                PlaceTargetFormElement ptfe = PlaceTargetFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(ptfe);
                            } else {
                                SelectFormElement sfe = SelectFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                elementList.add(sfe);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * Convert JSONObject to form element
     *
     * @param formBuilder - instance of form builder
     * @param jsonArray   - JSONArray of form element that want to convert
     * @param fileList    - List of files if has else null
     * @throws JSONException - if invalid JSONObject
     */
    public static void convertJSONToElementForResponse(FormBuilder formBuilder, JSONArray jsonArray,
                                                       ArrayList<FileItem> fileList)
            throws JSONException {
        List<String> traverseFieldTimeStamp = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Log.e("FormExtension", "convertJSONToElementForResponse: " + jsonObject);
            String name = "", label = "", className = "", type = "", subType = "", value = "",
                    role = "", min = "", max = "";
            JSONArray values = new JSONArray();
            int maxLength = 0, rows = 0, step = 0;
            boolean isRequired = false, haveAccess = false;

            if (jsonObject.has("name")) name = jsonObject.getString("name");
            if (jsonObject.has("label")) label = jsonObject.getString("label");
            if (jsonObject.has("required")) isRequired = jsonObject.getBoolean("required");
            if (jsonObject.has("className")) className = jsonObject.getString("className");
            if (jsonObject.has("type")) type = jsonObject.getString("type");
            if (jsonObject.has("subtype")) subType = jsonObject.getString("subtype");
            if (jsonObject.has("value")) value = jsonObject.getString("value");
            if (jsonObject.has("role")) role = jsonObject.getString("role");
            if (jsonObject.has("min")) min = jsonObject.getString("min");
            if (jsonObject.has("max")) max = jsonObject.getString("max");
            if (jsonObject.has("values")) values = jsonObject.getJSONArray("values");
            if (jsonObject.has("maxlength")) maxLength = jsonObject.getInt("maxlength");
            if (jsonObject.has("rows")) rows = jsonObject.getInt("rows");
            if (jsonObject.has("step")) step = jsonObject.getInt("step");
            if (jsonObject.has("access")) haveAccess = jsonObject.getBoolean("access");

            if ((!className.isEmpty() && className.contains("cascading_select"))
                    || type.equalsIgnoreCase("hidden")) {
                String fieldTimeStamp = null;
                if (!className.isEmpty())
                    fieldTimeStamp = className.substring(className.lastIndexOf("_") + 1);
                else if (type.equalsIgnoreCase("hidden"))
                    fieldTimeStamp = name.substring((name.lastIndexOf("_")) + 1);
                if (!traverseFieldTimeStamp.contains(fieldTimeStamp)) {
                    CascadeFormElement cfe = addCascadingSelectElementFromJson(jsonArray, fieldTimeStamp);
                    if (cfe != null)
                        formBuilder.addFormElement(cfe);
                    traverseFieldTimeStamp.add(fieldTimeStamp);
                }
            } else if ((!className.isEmpty() && className.contains("mcq-text"))) {
                String fieldTimeStamp = className.substring(className.lastIndexOf("_") + 1);
                if (!traverseFieldTimeStamp.contains(fieldTimeStamp)) {
                    QuizTextFormElement tfmfe = addTextFieldMarksElementFromJson(jsonArray, fieldTimeStamp);
                    if (tfmfe != null)
                        formBuilder.addFormElement(tfmfe);
                    traverseFieldTimeStamp.add(fieldTimeStamp);
                }
            } else {
                if (!type.isEmpty()) {
                    switch (type) {
                        case "textfield":

                            TextFormElement tfe = TextFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            tfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));

                            formBuilder.addFormElement(tfe);
                            break;
                        case "address":

                            AddressFormElement tfea = AddressFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            tfea.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));

                            formBuilder.addFormElement(tfea);
                            break;
                        case "htmlelement":

                            HtmlElementFormElement tfeh = HtmlElementFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            tfeh.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                            formBuilder.addFormElement(tfeh);
                            break;
//                        case "signature":
//                            SignatureFormElement tfes = SignatureFormElement.createInstance()
//                                    .setFieldLabel(label)
//                                    .setFieldName(name)
//                                    .setRequired(isRequired)
//                                    .setFieldValue(value)
//                                    .setHaveAccess(haveAccess)
//                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
//                            tfes.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
//                            formBuilder.addFormElement(tfes);
//                            break;
                        case "checkbox":
                            CheckBoxFormElement tfee = CheckBoxFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            tfee.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                            formBuilder.addFormElement(tfee);
                            break;
                        case "text":
                            switch (subType) {
                                case "text":
                                    if (className.contains("location")) {
                                        LocationFormElement lfe = LocationFormElement.createInstance()
                                                .setFieldLabel(label)
                                                .setFieldName(name)
                                                .setRequired(isRequired)
                                                .setFieldValue(value)
                                                .setHaveAccess(haveAccess)
                                                .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                        lfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                        formBuilder.addFormElement(lfe);
                                        break;
                                    }

                                case "email":
                                    EmailFormElement efe = EmailFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    efe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                    formBuilder.addFormElement(efe);
                                    break;

                                case "password":
                                    PasswordFormElement pfe = PasswordFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    pfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                    formBuilder.addFormElement(pfe);
                                    break;

                                case "tel":
                                    TelephoneFormElement tlfe = TelephoneFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    tlfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                    formBuilder.addFormElement(tlfe);
                                    break;

                                case "month":
                                    MonthFormElement mfe = MonthFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    mfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                    formBuilder.addFormElement(mfe);
                                    break;

                                case "week":
                                    WeekFormElement wfe = WeekFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    wfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                    formBuilder.addFormElement(wfe);
                                    break;

                                case "time":
                                    TimeFormElement time = TimeFormElement.createInstance()
                                            .setFieldLabel(label)
                                            .setFieldName(name)
                                            .setRequired(isRequired)
                                            .setFieldValue(value)
                                            .setHaveAccess(haveAccess)
                                            .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                    time.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                    formBuilder.addFormElement(time);
                                    break;
                            }
                            break;

                        case "header":
                            HeaderFormElement hfe = HeaderFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setFieldSubType(subType);
                            formBuilder.addFormElement(hfe);
                            break;

                        case "break":
                            SeparatorFormElement spfe = SeparatorFormElement.createInstance()
                                    .setFieldName(name)
                                    .setFieldLabel(label);
                            formBuilder.addFormElement(spfe);
                            break;

                        case "textarea":
                            TextAreaFormElement tafe = TextAreaFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setMaxLength(maxLength)
                                    .setRows(rows)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            tafe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                            formBuilder.addFormElement(tafe);
                            break;

                        case "number":
                            if (className.contains("achieved_unit")) {
                                AchievedUnitFormElement aufe = AchievedUnitFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                aufe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(aufe);
                            } else if (className.contains("unit_price")) {
                                UnitPriceFormElement upfe = UnitPriceFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                upfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(upfe);
                            } else {
                                NumberFormElement nfe = NumberFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setMin(min)
                                        .setMax(max)
                                        .setStep(step)
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                nfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(nfe);
                            }
                            break;
                        case "date":
                            DateFormElement dfe = DateFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            dfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                            formBuilder.addFormElement(dfe);
                            break;
                        case "file":
                            FileFormElement ffe = FileFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);

                            if (fileList.size() > 0) {

                                for (FileItem fi : fileList) {
                                    if (fi.getParentFieldName().equals(name)) {
                                        Log.e("FormExtension", "convertJSONToElementForResponse: " + name);
                                        Log.e("FormExtension", "convertJSONToElementForResponse: " + fi.getParentFieldName());
                                        List<String> data = new ArrayList<>();
                                        data.add(fi.getFileUrl());
                                        ffe.setUserData(data);
                                        Log.e("FormExtension", "convertJSONToElementForResponse: " + name + "->" + data.size());
                                        break;
                                    }
                                }
                            } else {
                                ffe.setUserData(new ArrayList<>());
                            }
                            formBuilder.addFormElement(ffe);
                            break;
                        case "radio":
                            if (className.contains("mcq")) {
                                QuizMcqFormElement mfe = QuizMcqFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                mfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(mfe);
                            } else {
                                RadioGroupFormElement rgfe = RadioGroupFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                rgfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(rgfe);
                            }
                            break;
                        case "checkbox-group":
                            SelectBoxesGroupFormElement cgfe = SelectBoxesGroupFormElement.createInstance()
                                    .setFieldLabel(label)
                                    .setFieldName(name)
                                    .setRequired(isRequired)
                                    .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                    .setFieldValue(value)
                                    .setHaveAccess(haveAccess)
                                    .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                            cgfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                            formBuilder.addFormElement(cgfe);
                            break;
                        case "select":
                            if (className.contains("place_target")) {
                                PlaceTargetFormElement ptfe = PlaceTargetFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(jsonObject.has("values") ? getOptionList(jsonObject.getJSONArray("values")) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                ptfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(ptfe);
                            } else {
                                SelectFormElement sfe = SelectFormElement.createInstance()
                                        .setFieldLabel(label)
                                        .setFieldName(name)
                                        .setRequired(isRequired)
                                        .setOptions(values.length() > 0 ? getOptionList(values) : new ArrayList<>())
                                        .setFieldValue(value)
                                        .setHaveAccess(haveAccess)
                                        .setRole(!role.isEmpty() ? GeneralExtension.toLongArray(role, ",") : new Long[0]);
                                sfe.setUserData(GeneralExtension.convertJsonToList(jsonObject.getJSONArray("userData")));
                                formBuilder.addFormElement(sfe);
                            }
                            break;

                    }
                }
            }
        }

    }

    /**
     * Convert JSONArray to List<OptionItem>
     *
     * @param jsonArray Provided JSONArray
     * @return List of OptionItem
     * @throws JSONException if invalid JSON
     */
    public static List<OptionItem> getOptionList(JSONArray jsonArray) throws JSONException {
        List<OptionItem> options = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject valueObject = jsonArray.getJSONObject(j);
            OptionItem optionItem = new OptionItem();
            optionItem.setOptionKey(valueObject.getString("label"));
            optionItem.setOptionValue(valueObject.getString("value"));
//            optionItem.setSelected(valueObject.getBoolean("selected"));
            options.add(optionItem);
        }

        return options;
    }


    /**
     * Convert JSONArray to List<CascadeOptionItem>
     *
     * @param level      - Level id to get options for
     * @param optionList - Provided whole List of CascadeOptionItem
     * @return - List<CascadeOptionItem>
     */
    public static List<CascadeOptionItem> getCascadeOptionList(int level, List<CascadeOptionItem> optionList) {
        List<CascadeOptionItem> options = new ArrayList<>();
        for (int j = 0; j < optionList.size(); j++) {
            if (optionList.get(j).getLevel() == level) {
                options.add(optionList.get(j));
            }
        }

        return options;
    }

    /**
     * Convert JSONArray of cascade option to List
     *
     * @param jsonArray Provided JSONArray
     * @return List of CascadeOptionItem
     * @throws JSONException if invalid JSON
     */
    public static List<CascadeOptionItem> getCascadeOptionListFromJSONArray(JSONArray jsonArray)
            throws JSONException {
        List<CascadeOptionItem> optionList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CascadeOptionItem optionItem = new CascadeOptionItem();
            optionItem.setId(jsonObject.getInt("id"));
            optionItem.setOption(jsonObject.getString("val"));
            if (jsonObject.has("parent_id"))
                optionItem.setParentId(jsonObject.getInt("parent_id"));
            if (jsonObject.has("level_id"))
                optionItem.setLevel(jsonObject.getInt("level_id"));

            optionList.add(optionItem);
        }

        return optionList;
    }

    /**
     * Get max level of cascading select
     *
     * @param optionList whole list of CascadeOptionItem
     * @return max no. of level
     */
    public static int getMaxLevelForCascadeElement(List<CascadeOptionItem> optionList) {
        int maxLevel = 0;
        for (CascadeOptionItem coi : optionList) {
            maxLevel = Math.max(maxLevel, coi.getLevel());
        }

        return maxLevel;
    }

    /**
     * Convert List of FileItem to JSONArray
     *
     * @param fileList Provided list of FileItem
     * @return JSONArray for given List
     * @throws JSONException if invalid JSON
     */
    public static JSONArray convertFileListToArray(ArrayList<FileItem> fileList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        if (fileList != null && fileList.size() > 0) {
            for (FileItem fi : fileList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("file_id", fi.getFileId());
                jsonObject.put("file_url", fi.getFileUrl());
                jsonObject.put("parent_field", fi.getParentFieldName());

                jsonArray.put(jsonObject);
            }
        }

        return jsonArray;
    }

    /**
     * Convert JSONArray to List of FileItem
     *
     * @param jsonArray Provided JSONArray
     * @return List of FileItem
     * @throws JSONException if invalid JSON
     */
    public static ArrayList<FileItem> convertFileDataToList(JSONArray jsonArray) throws JSONException {
        ArrayList<FileItem> fileList = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FileItem fileItem = new FileItem();
                fileItem.setFileId(jsonObject.getLong("file_id"));
                fileItem.setFileUrl(jsonObject.getString("file_url"));
                fileItem.setParentFieldName(jsonObject.getString("parent_field"));

                fileList.add(fileItem);
            }
        }

        return fileList;
    }
}
