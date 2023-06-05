package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class DiagnosticsListModel {
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
        public String active;
        public String title;
        public Patient patient;

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
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
            public int phone;
            public Object mobile;
            public String address;
            public Date created_at;
            public Date updated_at;
            public int status;
            public int inpatient;
            public int h_discharge;
            public ArrayList<Diagnostic> diagnostics;

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

            public int getPhone() {
                return phone;
            }

            public void setPhone(int phone) {
                this.phone = phone;
            }

            public Object getMobile() {
                return mobile;
            }

            public void setMobile(Object mobile) {
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

            public ArrayList<Diagnostic> getDiagnostics() {
                return diagnostics;
            }

            public void setDiagnostics(ArrayList<Diagnostic> diagnostics) {
                this.diagnostics = diagnostics;
            }

            public static class Diagnostic {
                public int id;
                public int patient_id;
                public String result;
                public String fullName;
                public Date created_at;
                public Date updated_at;

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

                public String getResult() {
                    return result;
                }

                public void setResult(String result) {
                    this.result = result;
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

                public String getFullName() {
                    return fullName;
                }

                public void setFullName(String fullName) {
                    this.fullName = fullName;
                }
            }
        }


    }
}





