package com.safra.utilities;

public class Common {

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm a";
    public static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String MONTH_FORMAT = "MMMM, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    public static final String WEEK_FORMAT = "'Week' ww, yyyy";

    public static final int PAGE_START = 1;

    //    public static final int REQUEST_SELECT_FORM_ELEMENT = 1001;
    public static final String REQUEST_SELECT_FORM_ELEMENT = "request_select_form_element";
    public static final int RESULT_SUCCESS_SELECT_FORM_ELEMENT = 1002;
    public static final int RESULT_FAILED_SELECT_FORM_ELEMENT = 1003;

    //    public static final int REQUEST_FIELD_PROPERTIES = 1011;
    public static final String REQUEST_FIELD_PROPERTIES = "request_field_properties";
    public static final int RESULT_SUCCESS_FIELD_PROPERTIES = 1012;
    public static final int RESULT_FAILED_FIELD_PROPERTIES = 1013;

    //    public static final int REQUEST_RESET_PASSWORD = 1021;
    public static final String REQUEST_RESET_PASSWORD = "request_reset_password";
    public static final int RESULT_SUCCESS_RESET_PASSWORD = 1022;
    public static final int RESULT_FAILED_RESET_PASSWORD = 1023;

    //    public static final int REQUEST_SELECT_TEMPLATE = 1031;
    public static final String REQUEST_SELECT_TEMPLATE = "request_select_template";
    public static final int RESULT_SUCCESS_SELECT_TEMPLATE = 1032;
    public static final int RESULT_FAILED_SELECT_TEMPLATE = 1033;

    public static final int REQUEST_EDIT_USER = 1041;
    public static final int RESULT_SUCCESS_EDIT_USER = 1042;
    public static final int RESULT_FAILED_EDIT_USER = 1043;

    public static final int REQUEST_EDIT_GROUP = 1051;
    public static final int RESULT_SUCCESS_EDIT_GROUP = 1052;
    public static final int RESULT_FAILED_EDIT_GROUP = 1053;

    public static final int REQUEST_EDIT_TASK = 1061;
    public static final int RESULT_SUCCESS_EDIT_TASK = 1062;
    public static final int RESULT_FAILED_EDIT_TASK = 1063;

    //    public static final int REQUEST_DELETE_TASK = 1071;
    public static final String REQUEST_DELETE_TASK = "request_delete_task";
    public static final int RESULT_SUCCESS_DELETE_TASK = 1072;
    public static final int RESULT_FAILED_DELETE_TASK = 1073;

    public static final int REQUEST_ADD_FORM = 1081;
    public static final int RESULT_SUCCESS_ADD_FORM = 1082;
    public static final int RESULT_FAILED_ADD_FORM = 1083;

    //    public static final int REQUEST_ADD_FORM_OPTION = 1091;
    public static final String REQUEST_ADD_FORM_OPTION = "request_add_form_option";
    public static final int RESULT_ADD_FORM_OPTION = 1092;

    public static final int REQUEST_APP_UPDATE = 1101;

    //    public static final int REQUEST_DELETE_USER = 1111;
    public static final String REQUEST_DELETE_USER = "request_delete_user";
    public static final int RESULT_SUCCESS_DELETE_USER = 1112;
    public static final int RESULT_FAILED_DELETE_USER = 1113;

    public static final String REQUEST_CASCADING_SETTINGS = "request_cascading_settings";

    public static final long UPDATE_INTERVAL = 5 * 1000;  /* 5 secs */
    public static final long FASTEST_INTERVAL = 2 * 1000; /* 2 secs */
    public static final int REQUEST_GPS = 2001;

    public static final int REQUEST_IMAGE_PICKER = 3001;

    public static final int TYPE_USER = 1;
    public static final int TYPE_GROUP = 2;
    public static final int TYPE_TASK = 3;
    public static final int TYPE_FORM = 4;
    public static final int TYPE_RESPONSE = 5;
    public static final int TYPE_TEMPLATE = 6;

    public static final int FORM_TYPE_GENERAL = 0;
    public static final int FORM_TYPE_CASHCHEW = 1;
    public static final int FORM_TYPE_MCQ = 2;

    public static final String REQUEST_DELETE_PROJECT = "request_delete_project";
    public static final String REQUEST_DELETE_AIM = "request_delete_aim";
    public static final String REQUEST_DELETE_GOAL = "request_delete_goal";
    public static final String REQUEST_DELETE_ACTION_TASK = "request_delete_task";
    public static final String REQUEST_DELETE_ACTION_RESOURCE = "request_delete_resource";
    public static final String REQUEST_DELETE_HEALTH_PATIENT_LIST = "request_delete_patient";
    public static final String REQUEST_DELETE_HEALTH_PATIENT_CAPTURE_VITAL_LIST = "request_delete_patient_capture_vital_list";
    public static final String REQUEST_DELETE_HEALTH_PATIENT_APPOINTMENT_DELETE = "request_delete_patient_appointment_delete";
    public static final String REQUEST_DELETE_HEALTH_MEDICINE_DELETE = "request_delete_patient_medicine_delete";
    public static final String REQUEST_DELETE_HEALTH_PROVIDER_DELETE = "request_delete_patient_provider_delete";
    public static final String REQUEST_DELETE_HEALTH_DIAGNOSIS_DELETE = "request_delete_patient_diagnosis_delete";
    public static final String REQUEST_DELETE_HEALTH_ALLERGIES_DELETE = "request_delete_patient_allergies_delete";
    public static final String REQUEST_DELETE_HEALTH_MEDICATION_DELETE = "request_delete_patient_medication_delete";
    public static final String REQUEST_DELETE_ACTIVE_VISITS_DELETE = "request_delete_active_visits_delete";



    //            public static final String BASE_URL = "https://safra.co.mz/api/";
    public static final String BASE_URL = "https://staging.safra.co.mz/api/";
//    public static final String BASE_URL = "http://127.0.0.1:8000/api/";

    //    public static final String REPORT_URL = "https://safra.co.mz/app-for-reports/";
    public static final String REPORT_URL = "https://staging.safra.co.mz/app-form-reports/";

    public static final String SIGN_IN_API = "app-user-login";
    public static final String REGISTRATION_API = "app-user-registration";
    public static final String RESET_PASSWORD_API = "app-user-forget-password";
    public static final String VERIFY_OTP_API = "app-user-verify";
    public static final String SEND_OTP_API = "app-user-send-otp";
    public static final String SIGN_OUT_API = "app-user-logout";
    public static final String USER_PROFILE_API = "app-user-profile";
    public static final String UPDATE_USER_PROFILE_API = "app-user-update-profile";
    public static final String CHANGE_PASSWORD_API = "app-change-password";

    public static final String DASHBOARD_API = "app-dashboard";

    public static final String TEMPLATE_LIST_API = "app-template-list";

    public static final String USER_LIST_API = "app-user-list";
    public static final String USER_VIEW_API = "app-view-user";
    public static final String USER_DELETE_API = "app-user-delete";
    public static final String USER_SAVE_API = "app-user-save";
    public static final String USER_STATUS_API = "app-user-status";

    public static final String GROUP_LIST_API = "app-user-role-list";
    public static final String GROUP_VIEW_API = "app-view-user-role";
    public static final String GROUP_SAVE_API = "app-user-save-role";

    public static final String TASK_LIST_API = "app-task-list";
    public static final String TASK_VIEW_API = "app-view-task";
    public static final String TASK_SAVE_API = "app-task-save";
    public static final String TASK_DELETE_API = "app-task-delete";
    public static final String TASK_STATUS_API = "app-task-status";

    public static final String FORM_LIST_API = "app-form-list";
    public static final String FORM_VIEW_API = "app-view-form";
    public static final String FORM_SAVE_API = "app-save-form";
    public static final String FORM_STATUS_API = "app-status-form";
    public static final String FORM_FILL_PUBLIC_API = "app-fill-form-public";
    public static final String FORM_FILL_PRIVATE_API = "app-fill-form-private";
    public static final String FORM_RESPONSE_LIST_API = "app-form-response-list";
    public static final String FORM_RESPONSE_VIEW_API = "app-status-form";

    public static final String FORM_TYPE_LIST = "app-form-type-list";

    public static final String LANGUAGE_LIST_API = "app-language-list";
    public static final String CHANGE_LANGUAGE_API = "app-change-language";

    public static final String ROLE_LIST_API = "app-role-list";
    public static final String PERMISSION_LIST_API = "app-permission-list";

    public static final String NOTIFICATION_LIST_API = "app-notifications";
    public static final String NOTIFICATION_READ_API = "app-read-notification";

    public static final String NOTIFICATION_CHANNEL_ID = "safra_notification_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Safra Notifications";


    public static final String PROJECT = "project";
    public static final String PROJECT_STORE = "project-store";
    public static final String PROJECT_DELETE = "delete-project";
    public static final String PROJECT_STATUS = "project-status/";
    public static final String PROJECT_EDIT = "edit-project/";
    public static final String PROJECT_UPDATE = "project-update/";


    public static final String PLANNER_PROJECT_LIST = "planner-list";
    public static final String PLANNER_PROJECT_AIM_LIST = "planner-project";
    public static final String CREATE_ACTION_PLANNER = "create-action-planner";
    public static final String ADD_PLANNER_GOAL = "add-planner-goal";
    public static final String PLANNER_AIM_DELETE = "delete-planner-aim";
    public static final String PLANNER_GOAL_DELETE = "delete-planner-goal";
    public static final String PLANNER_EDIT_AIM = "edit-planner-aim";
    public static final String PLANNER_EDIT_GOAL = "edit-planner-goal";
    public static final String PLANNER_TASK_LIST = "goal-task-list";
    public static final String PLANNER_SAVE_TASK = "goal-task-add";
    public static final String PLANNER_SAVE_TASK_EDIT = "goal-task-update";
    public static final String PLANNER_TASK_DELETE = "goal-task-delete";
    public static final String PLANNER_TASK_RESOURCE = "goal-task-resource-add";
    public static final String PLANNER_TASK_RESOURCE_UPDATE = "goal-task-resource-update";
    public static final String PLANNER_TASK_DELETE_RESOURCE = "goal-task-resource-delete";
    public static final String PLANNER_GET_TASK_DATA = "goal-task-edit";
    public static final String PLANNER_PROJECT_SPINNER_LIST = "project";
    public static final String PLANNER_AIM_SPINNER_LIST = "planner-aims";
    public static final String PLANNER_GOAL_SPINNER_LIST = "planner-goals";
    public static final String PLANNER_TASK_SPINNER_LIST = "planner-tasks";
    public static final String PLANNER_BUDGET_LIST = "planner-resources";
    public static final String PLANNER_SCHEDULE_CALENDAR_LIST = "planner-calendars-tasks";



    public static final String HEALTH_RECORD_PATIENT_LIST = "get-patients";
    public static final String HEALTH_RECORD_PATIENT_REGISTER = "add-patients";
    public static final String HEALTH_RECORD_PATIENT_LIST_UPDATE = "update-patients";
    public static final String HEALTH_RECORD_DELETE_PATIENT = "delete-patients";

    public static final String HEALTH_RECORD_APPOINTMENT_LIST = "get-appointments";
    public static final String HEALTH_RECORD_APPOINTMENT_SAVE = "save-appointment";
    public static final String HEALTH_RECORD_APPOINTMENT_ADD_NOTE = "add-note-appointment";
    public static final String HEALTH_RECORD_APPOINTMENT_DELETE = "delete-appointment";
    public static final String HEALTH_RECORD_APPOINTMENT_UPDATE_STATUS = "update-appointment-status";


    public static final String HEALTH_RECORD_CAPTURE_VITAL_LIST = "get-vitals";
    public static final String HEALTH_RECORD_CAPTURE_VITAL_SAVE = "add-vitals";
    public static final String HEALTH_RECORD_CAPTURE_VITAL_DELETE = "delete-vitals";


    public static final String HEALTH_RECORD_MEDICINE_LIST = "get-medicines";
    public static final String HEALTH_RECORD_PROVIDER_LIST = "get-providers";
    public static final String HEALTH_RECORD_ADD_MEDICINE = "add-medicines";
    public static final String HEALTH_RECORD_ADD_PROVIDER = "add-providers";
    public static final String HEALTH_RECORD_UPDATE_MEDICINE = "update-medicines";
    public static final String HEALTH_RECORD_DELETE_MEDICINE = "delete-medicines";
    public static final String HEALTH_RECORD_DELETE_PROVIDER = "delete-providers";
    public static final String HEALTH_RECORD_CHANGE_STATUS = "update-medicines-status";


    public static final String HEALTH_RECORD_DIAGNOSTICS_LIST = "get-diagnostics";
    public static final String HEALTH_RECORD_DIAGNOSTICS_ADD = "add-diagnostics";
    public static final String HEALTH_RECORD_DIAGNOSTICS_DELETE = "delete-diagnostics";


    public static final String HEALTH_RECORD_ALLERGIES_LIST = "get-allergies";
    public static final String HEALTH_RECORD_ALLERGIES_ADD = "add-allergies";
    public static final String HEALTH_RECORD_ALLERGIES_DELETE = "delete-allergies";
    public static final String HEALTH_RECORD_ALLERGIES_UPDATE = "update-allergies";



    public static final String HEALTH_RECORD_ACTIVE_VITAL_LIST = "active-visits";


    public static final String HEALTH_RECORD_MEDICATION_LIST = "get-medication";
    public static final String HEALTH_RECORD_MEDICATION_ADD = "add-medication";
    public static final String HEALTH_RECORD_AVALIABLE_MEDICINES_LIST = "get-available-medicines";
    public static final String HEALTH_RECORD_MEDICATION_DELETE = "delete-medication";

    public static final String HEALTH_RECORD_MEDICATION_UPDATE = "update-medication";
    public static final String HEALTH_RECORD_MEDICATION_UPDATE_STATUS = "update-medication-status";


    public static final String HEALTH_RECORD_OVERVIEW_LIST = "get-overview";

    public static final String HEALTH_RECORD_ACTIVE_VISIT_ADD = "add-visit";
    public static final String HEALTH_RECORD_ACTIVE_VISIT_DELETE = "delete-visit";

}
