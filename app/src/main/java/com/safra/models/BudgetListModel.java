package com.safra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */


public class BudgetListModel{
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

    public static class Datum{
        public int id;
        public int planner_task_id;
        public String name;
        public int quantity;
        public double price;
        public double subtotal;
        public double total;
        public String created_at;
        public String updated_at;
        private boolean isExpanded;
        private boolean isEditable;
        private boolean isResponseViewable;
        private boolean isFillable;
        private boolean isStatusChangeable;
        private boolean isAssignable;
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPlanner_task_id() {
            return planner_task_id;
        }

        public void setPlanner_task_id(int planner_task_id) {
            this.planner_task_id = planner_task_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }

        public boolean isEditable() {
            return isEditable;
        }

        public void setEditable(boolean editable) {
            isEditable = editable;
        }

        public boolean isResponseViewable() {
            return isResponseViewable;
        }

        public void setResponseViewable(boolean responseViewable) {
            isResponseViewable = responseViewable;
        }

        public boolean isFillable() {
            return isFillable;
        }

        public void setFillable(boolean fillable) {
            isFillable = fillable;
        }

        public boolean isStatusChangeable() {
            return isStatusChangeable;
        }

        public void setStatusChangeable(boolean statusChangeable) {
            isStatusChangeable = statusChangeable;
        }

        public boolean isAssignable() {
            return isAssignable;
        }

        public void setAssignable(boolean assignable) {
            isAssignable = assignable;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }
}



