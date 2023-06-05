package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class ActiveVisitsModel {
    public int success;
    public ArrayList<Datum> data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public static class Datum{
        public int id;
        public int patient_id;
        public String start_date;
        public String start_time;
        public int status;
        public Date created_at;
        public Date updated_at;
        public String patient_name;
        private boolean isEditable;
        private boolean isDeletable;
        private boolean isChangeable;
        private boolean isAssignable;
        private boolean isViewable;
        private boolean isExpanded;
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPatient_id() {
            return patient_id;
        }

        public void setPatient_id(int patient_id) {
            this.patient_id = patient_id;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getPatient_name() {
            return patient_name;
        }

        public void setPatient_name(String patient_name) {
            this.patient_name = patient_name;
        }

        public boolean isEditable() {
            return isEditable;
        }

        public void setEditable(boolean editable) {
            isEditable = editable;
        }

        public boolean isDeletable() {
            return isDeletable;
        }

        public void setDeletable(boolean deletable) {
            isDeletable = deletable;
        }

        public boolean isChangeable() {
            return isChangeable;
        }

        public void setChangeable(boolean changeable) {
            isChangeable = changeable;
        }

        public boolean isAssignable() {
            return isAssignable;
        }

        public void setAssignable(boolean assignable) {
            isAssignable = assignable;
        }

        public boolean isViewable() {
            return isViewable;
        }

        public void setViewable(boolean viewable) {
            isViewable = viewable;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }
    }
}




