package com.safra.models;

import java.util.ArrayList;

public class TemplateItem {
    public int success;
    public String message;
    public Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        public ArrayList<TemplateList> template_list;
        public int total_page;
        public int current_page;
        public int records_in_page;

        public ArrayList<TemplateList> getTemplate_list() {
            return template_list;
        }

        public void setTemplate_list(ArrayList<TemplateList> template_list) {
            this.template_list = template_list;
        }

        public int getTotal_page() {
            return total_page;
        }

        public void setTotal_page(int total_page) {
            this.total_page = total_page;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getRecords_in_page() {
            return records_in_page;
        }

        public void setRecords_in_page(int records_in_page) {
            this.records_in_page = records_in_page;
        }

        public class TemplateList{
            public int template_id;
            public String template_name;
            public String template_json;
            public int template_language_id;
            public String template_unique_id;
            public int template_type;
            public String language_title;
            public String language_url;
            public String language_slug;
            public String template_image_url;

            public int getTemplate_id() {
                return template_id;
            }

            public void setTemplate_id(int template_id) {
                this.template_id = template_id;
            }

            public String getTemplate_name() {
                return template_name;
            }

            public void setTemplate_name(String template_name) {
                this.template_name = template_name;
            }

            public String getTemplate_json() {
                return template_json;
            }

            public void setTemplate_json(String template_json) {
                this.template_json = template_json;
            }

            public int getTemplate_language_id() {
                return template_language_id;
            }

            public void setTemplate_language_id(int template_language_id) {
                this.template_language_id = template_language_id;
            }

            public String getTemplate_unique_id() {
                return template_unique_id;
            }

            public void setTemplate_unique_id(String template_unique_id) {
                this.template_unique_id = template_unique_id;
            }

            public int getTemplate_type() {
                return template_type;
            }

            public void setTemplate_type(int template_type) {
                this.template_type = template_type;
            }

            public String getLanguage_title() {
                return language_title;
            }

            public void setLanguage_title(String language_title) {
                this.language_title = language_title;
            }

            public String getLanguage_url() {
                return language_url;
            }

            public void setLanguage_url(String language_url) {
                this.language_url = language_url;
            }

            public String getLanguage_slug() {
                return language_slug;
            }

            public void setLanguage_slug(String language_slug) {
                this.language_slug = language_slug;
            }

            public String getTemplate_image_url() {
                return template_image_url;
            }

            public void setTemplate_image_url(String template_image_url) {
                this.template_image_url = template_image_url;
            }
        }
    }

}






