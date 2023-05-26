package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class AppointmentListModel {
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
            public ArrayList<Appointment> appointments;
            private boolean isSynced;
            private boolean isDelete;

            public boolean isViewable() {
                return isViewable;
            }

            public void setViewable(boolean viewable) {
                isViewable = viewable;
            }

            private boolean isViewable;
            private boolean isEditable;
            private boolean isDeletable;

            private boolean isExpanded;

            public boolean isChangeable() {
                return isChangeable;
            }

            public void setChangeable(boolean changeable) {
                isChangeable = changeable;
            }

            private boolean isChangeable;

            private boolean isSelected;
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

            public ArrayList<Appointment> getAppointments() {
                return appointments;
            }

            public void setAppointments(ArrayList<Appointment> appointments) {
                this.appointments = appointments;
            }

            public boolean isSynced() {
                return isSynced;
            }

            public void setSynced(boolean synced) {
                isSynced = synced;
            }

            public boolean isDelete() {
                return isDelete;
            }

            public void setDelete(boolean delete) {
                isDelete = delete;
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

            public boolean isExpanded() {
                return isExpanded;
            }

            public void setExpanded(boolean expanded) {
                isExpanded = expanded;
            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }

            public static class Appointment {
                public int id;
                public int patient_id;
                public String start_date;
                public String start_time;
                public int status;
                public String note;

                public String getFullName() {
                    return fullName;
                }

                public void setFullName(String fullName) {
                    this.fullName = fullName;
                }

                public String fullName;
                public Date created_at;
                public Date updated_at;
                public ArrayList<Note> notes;
                private boolean isEditable;
                private boolean isDeletable;
                private boolean isViewable;
                private boolean isChangeable;

                private boolean isExpanded;

                private boolean isSelected;
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

                public ArrayList<Note> getNotes() {
                    return notes;
                }

                public void setNotes(ArrayList<Note> notes) {
                    this.notes = notes;
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

                public boolean isViewable() {
                    return isViewable;
                }

                public void setViewable(boolean viewable) {
                    isViewable = viewable;
                }

                public boolean isChangeable() {
                    return isChangeable;
                }

                public void setChangeable(boolean changeable) {
                    isChangeable = changeable;
                }

                public boolean isExpanded() {
                    return isExpanded;
                }

                public void setExpanded(boolean expanded) {
                    isExpanded = expanded;
                }

                public boolean isSelected() {
                    return isSelected;
                }

                public void setSelected(boolean selected) {
                    isSelected = selected;
                }

                public class Note {
                    public int id;
                    public int appointment_id;
                    public String note;
                    public Date created_at;
                    public Date updated_at;

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public int getAppointment_id() {
                        return appointment_id;
                    }

                    public void setAppointment_id(int appointment_id) {
                        this.appointment_id = appointment_id;
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

}





