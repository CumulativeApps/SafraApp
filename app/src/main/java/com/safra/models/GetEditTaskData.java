package com.safra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class GetEditTaskData{
    public class User{
        public int user_id;
        public String user_name;
        public int user_role_id;
        public String user_phone_no;
        public String user_email;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public int getUser_role_id() {
            return user_role_id;
        }

        public void setUser_role_id(int user_role_id) {
            this.user_role_id = user_role_id;
        }

        public String getUser_phone_no() {
            return user_phone_no;
        }

        public void setUser_phone_no(String user_phone_no) {
            this.user_phone_no = user_phone_no;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }
    }
    public class Goal{
        public int id;
        public int user_id;
        public int planner_goal_id;
        public Object user_ids;
        public String title;
        public int priority;
        public int status;
        public String observation;
        public String metrics;
        public String responsible;
        public String supervisor;
        public String start_date;
        public String end_date;
        public Date created_at;
        public Date updated_at;

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

        public int getPlanner_goal_id() {
            return planner_goal_id;
        }

        public void setPlanner_goal_id(int planner_goal_id) {
            this.planner_goal_id = planner_goal_id;
        }

        public Object getUser_ids() {
            return user_ids;
        }

        public void setUser_ids(Object user_ids) {
            this.user_ids = user_ids;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getObservation() {
            return observation;
        }

        public void setObservation(String observation) {
            this.observation = observation;
        }

        public String getMetrics() {
            return metrics;
        }

        public void setMetrics(String metrics) {
            this.metrics = metrics;
        }

        public String getResponsible() {
            return responsible;
        }

        public void setResponsible(String responsible) {
            this.responsible = responsible;
        }

        public String getSupervisor() {
            return supervisor;
        }

        public void setSupervisor(String supervisor) {
            this.supervisor = supervisor;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
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

//public class GetEditTaskData {
//
//    @SerializedName("success")
//    @Expose
//    public Integer success;
//    @SerializedName("message")
//    @Expose
//    public String message;
//    @SerializedName("data")
//    @Expose
//    public Data data;
//
//    public GetEditTaskData withSuccess(Integer success) {
//        this.success = success;
//        return this;
//    }
//
//    public GetEditTaskData withMessage(String message) {
//        this.message = message;
//        return this;
//    }
//
//    public GetEditTaskData withData(Data data) {
//        this.data = data;
//        return this;
//    }
//
//    public Integer getSuccess() {
//        return success;
//    }
//
//    public void setSuccess(Integer success) {
//        this.success = success;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public Data getData() {
//        return data;
//    }
//
//    public void setData(Data data) {
//        this.data = data;
//    }
//
//    public static class Data {
//
//        @SerializedName("goal")
//        @Expose
//        public Goal goal;
//        @SerializedName("users")
//        @Expose
//        public List<User> users;
//
//        public Data withGoal(Goal goal) {
//            this.goal = goal;
//            return this;
//        }
//
//        public Data withUsers(List<User> users) {
//            this.users = users;
//            return this;
//        }
//
//        public Goal getGoal() {
//            return goal;
//        }
//
//        public void setGoal(Goal goal) {
//            this.goal = goal;
//        }
//
//        public List<User> getUsers() {
//            return users;
//        }
//
//        public void setUsers(List<User> users) {
//            this.users = users;
//        }
//
//        public static class Goal {
//
//            @SerializedName("id")
//            @Expose
//            public Integer id;
//            @SerializedName("user_id")
//            @Expose
//            public Integer userId;
//            @SerializedName("planner_goal_id")
//            @Expose
//            public Integer plannerGoalId;
//            @SerializedName("user_ids")
//            @Expose
//            public Object userIds;
//            @SerializedName("title")
//            @Expose
//            public String title;
//            @SerializedName("priority")
//            @Expose
//            public Integer priority;
//            @SerializedName("status")
//            @Expose
//            public Integer status;
//            @SerializedName("observation")
//            @Expose
//            public String observation;
//            @SerializedName("metrics")
//            @Expose
//            public String metrics;
//            @SerializedName("responsible")
//            @Expose
//            public String responsible;
//            @SerializedName("supervisor")
//            @Expose
//            public String supervisor;
//            @SerializedName("start_date")
//            @Expose
//            public String startDate;
//            @SerializedName("end_date")
//            @Expose
//            public String endDate;
//            @SerializedName("created_at")
//            @Expose
//            public String createdAt;
//            @SerializedName("updated_at")
//            @Expose
//            public String updatedAt;
//
//            public Goal withId(Integer id) {
//                this.id = id;
//                return this;
//            }
//
//            public Goal withUserId(Integer userId) {
//                this.userId = userId;
//                return this;
//            }
//
//            public Goal withPlannerGoalId(Integer plannerGoalId) {
//                this.plannerGoalId = plannerGoalId;
//                return this;
//            }
//
//            public Goal withUserIds(Object userIds) {
//                this.userIds = userIds;
//                return this;
//            }
//
//            public Goal withTitle(String title) {
//                this.title = title;
//                return this;
//            }
//
//            public Goal withPriority(Integer priority) {
//                this.priority = priority;
//                return this;
//            }
//
//            public Goal withStatus(Integer status) {
//                this.status = status;
//                return this;
//            }
//
//            public Goal withObservation(String observation) {
//                this.observation = observation;
//                return this;
//            }
//
//            public Goal withMetrics(String metrics) {
//                this.metrics = metrics;
//                return this;
//            }
//
//            public Goal withResponsible(String responsible) {
//                this.responsible = responsible;
//                return this;
//            }
//
//            public Goal withSupervisor(String supervisor) {
//                this.supervisor = supervisor;
//                return this;
//            }
//
//            public Goal withStartDate(String startDate) {
//                this.startDate = startDate;
//                return this;
//            }
//
//            public Goal withEndDate(String endDate) {
//                this.endDate = endDate;
//                return this;
//            }
//
//            public Goal withCreatedAt(String createdAt) {
//                this.createdAt = createdAt;
//                return this;
//            }
//
//            public Goal withUpdatedAt(String updatedAt) {
//                this.updatedAt = updatedAt;
//                return this;
//            }
//
//            public Integer getId() {
//                return id;
//            }
//
//            public void setId(Integer id) {
//                this.id = id;
//            }
//
//            public Integer getUserId() {
//                return userId;
//            }
//
//            public void setUserId(Integer userId) {
//                this.userId = userId;
//            }
//
//            public Integer getPlannerGoalId() {
//                return plannerGoalId;
//            }
//
//            public void setPlannerGoalId(Integer plannerGoalId) {
//                this.plannerGoalId = plannerGoalId;
//            }
//
//            public Object getUserIds() {
//                return userIds;
//            }
//
//            public void setUserIds(Object userIds) {
//                this.userIds = userIds;
//            }
//
//            public String getTitle() {
//                return title;
//            }
//
//            public void setTitle(String title) {
//                this.title = title;
//            }
//
//            public Integer getPriority() {
//                return priority;
//            }
//
//            public void setPriority(Integer priority) {
//                this.priority = priority;
//            }
//
//            public Integer getStatus() {
//                return status;
//            }
//
//            public void setStatus(Integer status) {
//                this.status = status;
//            }
//
//            public String getObservation() {
//                return observation;
//            }
//
//            public void setObservation(String observation) {
//                this.observation = observation;
//            }
//
//            public String getMetrics() {
//                return metrics;
//            }
//
//            public void setMetrics(String metrics) {
//                this.metrics = metrics;
//            }
//
//            public String getResponsible() {
//                return responsible;
//            }
//
//            public void setResponsible(String responsible) {
//                this.responsible = responsible;
//            }
//
//            public String getSupervisor() {
//                return supervisor;
//            }
//
//            public void setSupervisor(String supervisor) {
//                this.supervisor = supervisor;
//            }
//
//            public String getStartDate() {
//                return startDate;
//            }
//
//            public void setStartDate(String startDate) {
//                this.startDate = startDate;
//            }
//
//            public String getEndDate() {
//                return endDate;
//            }
//
//            public void setEndDate(String endDate) {
//                this.endDate = endDate;
//            }
//
//            public String getCreatedAt() {
//                return createdAt;
//            }
//
//            public void setCreatedAt(String createdAt) {
//                this.createdAt = createdAt;
//            }
//
//            public String getUpdatedAt() {
//                return updatedAt;
//            }
//
//            public void setUpdatedAt(String updatedAt) {
//                this.updatedAt = updatedAt;
//            }
//        }
//        public static class User {
//
//            @SerializedName("user_id")
//            @Expose
//            public Integer userId;
//            @SerializedName("user_name")
//            @Expose
//            public String userName;
//            @SerializedName("user_role_id")
//            @Expose
//            public Integer userRoleId;
//            @SerializedName("user_phone_no")
//            @Expose
//            public String userPhoneNo;
//            @SerializedName("user_email")
//            @Expose
//            public String userEmail;
//
//            public User withUserId(Integer userId) {
//                this.userId = userId;
//                return this;
//            }
//
//            public User withUserName(String userName) {
//                this.userName = userName;
//                return this;
//            }
//
//            public User withUserRoleId(Integer userRoleId) {
//                this.userRoleId = userRoleId;
//                return this;
//            }
//
//            public User withUserPhoneNo(String userPhoneNo) {
//                this.userPhoneNo = userPhoneNo;
//                return this;
//            }
//
//            public User withUserEmail(String userEmail) {
//                this.userEmail = userEmail;
//                return this;
//            }
//
//            public Integer getUserId() {
//                return userId;
//            }
//
//            public void setUserId(Integer userId) {
//                this.userId = userId;
//            }
//
//            public String getUserName() {
//                return userName;
//            }
//
//            public void setUserName(String userName) {
//                this.userName = userName;
//            }
//
//            public Integer getUserRoleId() {
//                return userRoleId;
//            }
//
//            public void setUserRoleId(Integer userRoleId) {
//                this.userRoleId = userRoleId;
//            }
//
//            public String getUserPhoneNo() {
//                return userPhoneNo;
//            }
//
//            public void setUserPhoneNo(String userPhoneNo) {
//                this.userPhoneNo = userPhoneNo;
//            }
//
//            public String getUserEmail() {
//                return userEmail;
//            }
//
//            public void setUserEmail(String userEmail) {
//                this.userEmail = userEmail;
//            }
//        }
//    }
//}
//
//
//
//
//
