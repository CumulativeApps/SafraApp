package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class CaptureVitalListModel {
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

    public static class Data{
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
        public ArrayList<Vital> vitals;

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

        public ArrayList<Vital> getVitals() {
            return vitals;
        }

        public void setVitals(ArrayList<Vital> vitals) {
            this.vitals = vitals;
        }

        public static class Vital{
            public int id;
            public int patient_id;
            public int height;
            public int weight;
//            @JsonProperty("BMI")
            public double bMI;
            public int temperature;
            public int pulse;
            public int respiratory_rate;
            public String blood_pressure;
            public String fullName;
            public int blood_oxygen_saturation;
            public Date created_at;
            public Date updated_at;
            private boolean isEditable;
            private boolean isDeletable;
            private boolean isChangeable;
            private boolean isAssignable;

            private boolean isExpanded;

            public String getFullName() {
                return fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
            }
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

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWeight() {
                return weight;
            }

            public void setWeight(int weight) {
                this.weight = weight;
            }

            public double getbMI() {
                return bMI;
            }

            public void setbMI(double bMI) {
                this.bMI = bMI;
            }

            public int getTemperature() {
                return temperature;
            }

            public void setTemperature(int temperature) {
                this.temperature = temperature;
            }

            public int getPulse() {
                return pulse;
            }

            public void setPulse(int pulse) {
                this.pulse = pulse;
            }

            public int getRespiratory_rate() {
                return respiratory_rate;
            }

            public void setRespiratory_rate(int respiratory_rate) {
                this.respiratory_rate = respiratory_rate;
            }

            public String getBlood_pressure() {
                return blood_pressure;
            }

            public void setBlood_pressure(String blood_pressure) {
                this.blood_pressure = blood_pressure;
            }

            public int getBlood_oxygen_saturation() {
                return blood_oxygen_saturation;
            }

            public void setBlood_oxygen_saturation(int blood_oxygen_saturation) {
                this.blood_oxygen_saturation = blood_oxygen_saturation;
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
        }

    }

}





