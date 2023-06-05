package com.safra.models;

import java.util.ArrayList;
import java.util.Date;

public class GetAvaliableMedicineList {
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
        public ArrayList<Medicine> medicines;

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

        public ArrayList<Medicine> getMedicines() {
            return medicines;
        }

        public void setMedicines(ArrayList<Medicine> medicines) {
            this.medicines = medicines;
        }

        public static class Medicine {
            public int id;
            public int providor_id;
            public int master_id;
            public String name;
            public String chemical;
            public int status;
            public Date created_at;
            public Date updated_at;
            public Provider provider;

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

            public Provider getProvider() {
                return provider;
            }

            public void setProvider(Provider provider) {
                this.provider = provider;
            }

            public class Provider {
                public int id;
                public int master_id;
                public String name;
                public Date created_at;
                public Date updated_at;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
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




