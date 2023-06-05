package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class ScheduleCalendarModel {
    public int success;
    public String message;
    public ArrayList<Datum> data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public static class Datum {
        public int id;
        public int user_id;
        public int planner_goal_id;
        public Object user_ids;
        public String title;
        public int priority;
        public int status;
        public String observation;
        public String metrics;
        public String responsible;
        public String supervisor;
        public String start_date;
        public String end_date;
        public Date created_at;
        public Date updated_at;
        public Who who;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getPlanner_goal_id() {
            return planner_goal_id;
        }

        public void setPlanner_goal_id(int planner_goal_id) {
            this.planner_goal_id = planner_goal_id;
        }

        public Object getUser_ids() {
            return user_ids;
        }

        public void setUser_ids(Object user_ids) {
            this.user_ids = user_ids;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getObservation() {
            return observation;
        }

        public void setObservation(String observation) {
            this.observation = observation;
        }

        public String getMetrics() {
            return metrics;
        }

        public void setMetrics(String metrics) {
            this.metrics = metrics;
        }

        public String getResponsible() {
            return responsible;
        }

        public void setResponsible(String responsible) {
            this.responsible = responsible;
        }

        public String getSupervisor() {
            return supervisor;
        }

        public void setSupervisor(String supervisor) {
            this.supervisor = supervisor;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public Date getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public Date getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Date updated_at) {
            this.updated_at = updated_at;
        }

        public Who getWho() {
            return who;
        }

        public void setWho(Who who) {
            this.who = who;
        }

        public class Who {
            public int user_id;
            public Object google_id;
            public Object company_id;
            public int user_language_id;
            public String user_fcm_token;
            public Object user_roles;
            public Object user_template_ids;
            public String user_permission_ids;
            public String user_module_ids;
            public int user_session_id;
            public int user_subscription_id;
            public String user_name;
            public String user_phone_no;
            public String user_email;
            public String user_password;
            public int user_email_is_verified;
            public Object user_otp;
            public String user_profile_image;
            public String user_sweet_word;
            public String user_sweet_words;
            public Object user_temp_token;
            public Object user_token_expiry;
            public int user_role_id;
            public int user_master_id;
            public int added_by;
            public int user_status;
            public int is_delete;
            public Date created_at;
            public Date updated_at;

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public Object getGoogle_id() {
                return google_id;
            }

            public void setGoogle_id(Object google_id) {
                this.google_id = google_id;
            }

            public Object getCompany_id() {
                return company_id;
            }

            public void setCompany_id(Object company_id) {
                this.company_id = company_id;
            }

            public int getUser_language_id() {
                return user_language_id;
            }

            public void setUser_language_id(int user_language_id) {
                this.user_language_id = user_language_id;
            }

            public String getUser_fcm_token() {
                return user_fcm_token;
            }

            public void setUser_fcm_token(String user_fcm_token) {
                this.user_fcm_token = user_fcm_token;
            }

            public Object getUser_roles() {
                return user_roles;
            }

            public void setUser_roles(Object user_roles) {
                this.user_roles = user_roles;
            }

            public Object getUser_template_ids() {
                return user_template_ids;
            }

            public void setUser_template_ids(Object user_template_ids) {
                this.user_template_ids = user_template_ids;
            }

            public String getUser_permission_ids() {
                return user_permission_ids;
            }

            public void setUser_permission_ids(String user_permission_ids) {
                this.user_permission_ids = user_permission_ids;
            }

            public String getUser_module_ids() {
                return user_module_ids;
            }

            public void setUser_module_ids(String user_module_ids) {
                this.user_module_ids = user_module_ids;
            }

            public int getUser_session_id() {
                return user_session_id;
            }

            public void setUser_session_id(int user_session_id) {
                this.user_session_id = user_session_id;
            }

            public int getUser_subscription_id() {
                return user_subscription_id;
            }

            public void setUser_subscription_id(int user_subscription_id) {
                this.user_subscription_id = user_subscription_id;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getUser_phone_no() {
                return user_phone_no;
            }

            public void setUser_phone_no(String user_phone_no) {
                this.user_phone_no = user_phone_no;
            }

            public String getUser_email() {
                return user_email;
            }

            public void setUser_email(String user_email) {
                this.user_email = user_email;
            }

            public String getUser_password() {
                return user_password;
            }

            public void setUser_password(String user_password) {
                this.user_password = user_password;
            }

            public int getUser_email_is_verified() {
                return user_email_is_verified;
            }

            public void setUser_email_is_verified(int user_email_is_verified) {
                this.user_email_is_verified = user_email_is_verified;
            }

            public Object getUser_otp() {
                return user_otp;
            }

            public void setUser_otp(Object user_otp) {
                this.user_otp = user_otp;
            }

            public String getUser_profile_image() {
                return user_profile_image;
            }

            public void setUser_profile_image(String user_profile_image) {
                this.user_profile_image = user_profile_image;
            }

            public String getUser_sweet_word() {
                return user_sweet_word;
            }

            public void setUser_sweet_word(String user_sweet_word) {
                this.user_sweet_word = user_sweet_word;
            }

            public String getUser_sweet_words() {
                return user_sweet_words;
            }

            public void setUser_sweet_words(String user_sweet_words) {
                this.user_sweet_words = user_sweet_words;
            }

            public Object getUser_temp_token() {
                return user_temp_token;
            }

            public void setUser_temp_token(Object user_temp_token) {
                this.user_temp_token = user_temp_token;
            }

            public Object getUser_token_expiry() {
                return user_token_expiry;
            }

            public void setUser_token_expiry(Object user_token_expiry) {
                this.user_token_expiry = user_token_expiry;
            }

            public int getUser_role_id() {
                return user_role_id;
            }

            public void setUser_role_id(int user_role_id) {
                this.user_role_id = user_role_id;
            }

            public int getUser_master_id() {
                return user_master_id;
            }

            public void setUser_master_id(int user_master_id) {
                this.user_master_id = user_master_id;
            }

            public int getAdded_by() {
                return added_by;
            }

            public void setAdded_by(int added_by) {
                this.added_by = added_by;
            }

            public int getUser_status() {
                return user_status;
            }

            public void setUser_status(int user_status) {
                this.user_status = user_status;
            }

            public int getIs_delete() {
                return is_delete;
            }

            public void setIs_delete(int is_delete) {
                this.is_delete = is_delete;
            }

            public Date getCreated_at() {
                return created_at;
            }

            public void setCreated_at(Date created_at) {
                this.created_at = created_at;
            }

            public Date getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(Date updated_at) {
                this.updated_at = updated_at;
            }
        }


    }

}



