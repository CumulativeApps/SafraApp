package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class OverviewDataModel {

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
        public String user_token;
        public int patient_id;
        public Patient patient;
        public Vital vital;
        public ArrayList<Visit> visits;

        public String getUser_token() {
            return user_token;
        }

        public void setUser_token(String user_token) {
            this.user_token = user_token;
        }

        public int getPatient_id() {
            return patient_id;
        }

        public void setPatient_id(int patient_id) {
            this.patient_id = patient_id;
        }

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
        }

        public Vital getVital() {
            return vital;
        }

        public void setVital(Vital vital) {
            this.vital = vital;
        }

        public ArrayList<Visit> getVisits() {
            return visits;
        }

        public void setVisits(ArrayList<Visit> visits) {
            this.visits = visits;
        }

        public class Patient {
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
            public ArrayList<Allergy> allergies;

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

            public ArrayList<Allergy> getAllergies() {
                return allergies;
            }

            public void setAllergies(ArrayList<Allergy> allergies) {
                this.allergies = allergies;
            }

            public class Diagnostic {
                public int id;
                public int patient_id;
                public String result;
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
            }


            public class Allergy {
                public int id;
                public int patient_id;
                public String allergen;
                public String reaction;
                public String severity;
                public String comment;
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

                public String getAllergen() {
                    return allergen;
                }

                public void setAllergen(String allergen) {
                    this.allergen = allergen;
                }

                public String getReaction() {
                    return reaction;
                }

                public void setReaction(String reaction) {
                    this.reaction = reaction;
                }

                public String getSeverity() {
                    return severity;
                }

                public void setSeverity(String severity) {
                    this.severity = severity;
                }

                public String getComment() {
                    return comment;
                }

                public void setComment(String comment) {
                    this.comment = comment;
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

        public class Vital {
            public int id;
            public int patient_id;
            public int height;
            public int weight;
            public double bMI;
            public int temperature;
            public int pulse;
            public int respiratory_rate;
            public String blood_pressure;
            public int blood_oxygen_saturation;
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
        }

        public static class Visit {
            public int id;
            public int patient_id;
            public String start_date;
            public String start_time;
            public int status;
            public Date created_at;
            public Date updated_at;
            public ArrayList<Note> notes;

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

            public ArrayList<Note> getNotes() {
                return notes;
            }

            public void setNotes(ArrayList<Note> notes) {
                this.notes = notes;
            }

            public class Note {
                public int id;
                public int visit_id;
                public String note;
                public Date created_at;
                public Date updated_at;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getVisit_id() {
                    return visit_id;
                }

                public void setVisit_id(int visit_id) {
                    this.visit_id = visit_id;
                }

                public String getNote() {
                    return note;
                }

                public void setNote(String note) {
                    this.note = note;
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
}













