package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class PatientListModel {
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

    public static class Data {
        public String title;
        public String active;
        public ArrayList<Patient> patients;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public ArrayList<Patient> getPatients() {
            return patients;
        }

        public void setPatients(ArrayList<Patient> patients) {
            this.patients = patients;
        }

        public static class Patient {
            public int id;
            public int user_id;
            public int master_id;
            public String first_name;
            public String middle_name;
            public String last_name;
            public String gender;
            public String birthdate;
            public String phone;
            public String mobile;
            public String address;
            public Date created_at;
            public Date updated_at;
            public int status;
            public int inpatient;
            public int h_discharge;
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

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public int getMaster_id() {
                return master_id;
            }

            public void setMaster_id(int master_id) {
                this.master_id = master_id;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getMiddle_name() {
                return middle_name;
            }

            public void setMiddle_name(String middle_name) {
                this.middle_name = middle_name;
            }

            public String getLast_name() {
                return last_name;
            }

            public void setLast_name(String last_name) {
                this.last_name = last_name;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getBirthdate() {
                return birthdate;
            }

            public void setBirthdate(String birthdate) {
                this.birthdate = birthdate;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getInpatient() {
                return inpatient;
            }

            public void setInpatient(int inpatient) {
                this.inpatient = inpatient;
            }

            public int getH_discharge() {
                return h_discharge;
            }

            public void setH_discharge(int h_discharge) {
                this.h_discharge = h_discharge;
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

            public boolean isExpanded() {
                return isExpanded;
            }

            public void setExpanded(boolean expanded) {
                isExpanded = expanded;
            }

            public boolean isViewable() {
                return isViewable;
            }

            public void setViewable(boolean viewable) {
                isViewable = viewable;
            }
        }

    }
}






