package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class MedicationListModel {
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

    public class Data {
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
        public ArrayList<Medication> medication;

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

        public ArrayList<Medication> getMedication() {
            return medication;
        }

        public void setMedication(ArrayList<Medication> medication) {
            this.medication = medication;
        }

        public class Medication {
            public int id;
            public int patient_id;
            public int medicine_id;
            public int quantity;
            public String instructions;
            public String note;
            public Date created_at;
            public Date updated_at;
            public int status;
            public Medicine medicine;
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

            public int getMedicine_id() {
                return medicine_id;
            }

            public void setMedicine_id(int medicine_id) {
                this.medicine_id = medicine_id;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public String getInstructions() {
                return instructions;
            }

            public void setInstructions(String instructions) {
                this.instructions = instructions;
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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Medicine getMedicine() {
                return medicine;
            }

            public void setMedicine(Medicine medicine) {
                this.medicine = medicine;
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

            public class Medicine {
                public int id;
                public int providor_id;
                public int master_id;
                public String name;
                public String chemical;
                public int status;
                public Date created_at;
                public Date updated_at;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public int getProvidor_id() {
                    return providor_id;
                }

                public void setProvidor_id(int providor_id) {
                    this.providor_id = providor_id;
                }

                public int getMaster_id() {
                    return master_id;
                }

                public void setMaster_id(int master_id) {
                    this.master_id = master_id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getChemical() {
                    return chemical;
                }

                public void setChemical(String chemical) {
                    this.chemical = chemical;
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
            }
        }
    }
}

