package com.safra.adapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ProjectPlanListModel {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public ProjectPlanListModel withSuccess(Integer success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProjectPlanListModel withMessage(String message) {
        this.message = message;
        return this;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ProjectPlanListModel withData(Data data) {
        this.data = data;
        return this;
    }

    public static class Data {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("active")
        @Expose
        private String active;
        @SerializedName("projects")
        @Expose
        private List<Project> projects;
        @SerializedName("aimGoals")
        @Expose
        private AimGoals aimGoals;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Data withTitle(String title) {
            this.title = title;
            return this;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public Data withActive(String active) {
            this.active = active;
            return this;
        }

        public List<Project> getProjects() {
            return projects;
        }

        public void setProjects(List<Project> projects) {
            this.projects = projects;
        }

        public Data withProjects(List<Project> projects) {
            this.projects = projects;
            return this;
        }

        public AimGoals getAimGoals() {
            return aimGoals;
        }

        public void setAimGoals(AimGoals aimGoals) {
            this.aimGoals = aimGoals;
        }

        public Data withAimGoals(AimGoals aimGoals) {
            this.aimGoals = aimGoals;
            return this;
        }

        public class Project {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("user_id")
            @Expose
            private Integer userId;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("master_id")
            @Expose
            private Integer masterId;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("start_date")
            @Expose
            private String startDate;
            @SerializedName("end_date")
            @Expose
            private String endDate;
            @SerializedName("financier")
            @Expose
            private String financier;
            @SerializedName("status")
            @Expose
            private Integer status;
            @SerializedName("currency")
            @Expose
            private String currency;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Project withId(Integer id) {
                this.id = id;
                return this;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public Project withUserId(Integer userId) {
                this.userId = userId;
                return this;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Project withName(String name) {
                this.name = name;
                return this;
            }

            public Integer getMasterId() {
                return masterId;
            }

            public void setMasterId(Integer masterId) {
                this.masterId = masterId;
            }

            public Project withMasterId(Integer masterId) {
                this.masterId = masterId;
                return this;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public Project withCreatedAt(String createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public Project withUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
                return this;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public Project withStartDate(String startDate) {
                this.startDate = startDate;
                return this;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public Project withEndDate(String endDate) {
                this.endDate = endDate;
                return this;
            }

            public String getFinancier() {
                return financier;
            }

            public void setFinancier(String financier) {
                this.financier = financier;
            }

            public Project withFinancier(String financier) {
                this.financier = financier;
                return this;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public Project withStatus(Integer status) {
                this.status = status;
                return this;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public Project withCurrency(String currency) {
                this.currency = currency;
                return this;
            }

        }

        public static class AimGoals {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("user_id")
            @Expose
            private Integer userId;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("master_id")
            @Expose
            private Integer masterId;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("start_date")
            @Expose
            private String startDate;
            @SerializedName("end_date")
            @Expose
            private String endDate;
            @SerializedName("financier")
            @Expose
            private String financier;
            @SerializedName("status")
            @Expose
            private Integer status;
            @SerializedName("currency")
            @Expose
            private String currency;
            @SerializedName("aims")
            @Expose
            private List<Aim> aims;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public AimGoals withId(Integer id) {
                this.id = id;
                return this;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public AimGoals withUserId(Integer userId) {
                this.userId = userId;
                return this;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public AimGoals withName(String name) {
                this.name = name;
                return this;
            }

            public Integer getMasterId() {
                return masterId;
            }

            public void setMasterId(Integer masterId) {
                this.masterId = masterId;
            }

            public AimGoals withMasterId(Integer masterId) {
                this.masterId = masterId;
                return this;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public AimGoals withCreatedAt(String createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public AimGoals withUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
                return this;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public AimGoals withStartDate(String startDate) {
                this.startDate = startDate;
                return this;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public AimGoals withEndDate(String endDate) {
                this.endDate = endDate;
                return this;
            }

            public String getFinancier() {
                return financier;
            }

            public void setFinancier(String financier) {
                this.financier = financier;
            }

            public AimGoals withFinancier(String financier) {
                this.financier = financier;
                return this;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public AimGoals withStatus(Integer status) {
                this.status = status;
                return this;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public AimGoals withCurrency(String currency) {
                this.currency = currency;
                return this;
            }

            public List<Aim> getAims() {
                return aims;
            }

            public void setAims(List<Aim> aims) {
                this.aims = aims;
            }

            public AimGoals withAims(List<Aim> aims) {
                this.aims = aims;
                return this;
            }

            public static class Aim {

                @SerializedName("id")
                @Expose
                private Integer id;
                @SerializedName("user_id")
                @Expose
                private Integer userId;
                @SerializedName("aim")
                @Expose
                private String aim;
                @SerializedName("master_id")
                @Expose
                private Integer masterId;
                @SerializedName("created_at")
                @Expose
                private String createdAt;
                @SerializedName("updated_at")
                @Expose
                private String updatedAt;
                @SerializedName("planner_project_id")
                @Expose
                private Integer plannerProjectId;
                @SerializedName("goals")
                @Expose
                private List<Goal> goals;

                private boolean isEditable;
                private boolean isDeletable;
                private boolean isViewable;
                private boolean isChangeable;

                private boolean isExpanded;

                private boolean isSelected;

                public Integer getId() {
                    return id;
                }

                public void setId(Integer id) {
                    this.id = id;
                }

                public Aim withId(Integer id) {
                    this.id = id;
                    return this;
                }

                public Integer getUserId() {
                    return userId;
                }

                public void setUserId(Integer userId) {
                    this.userId = userId;
                }

                public Aim withUserId(Integer userId) {
                    this.userId = userId;
                    return this;
                }

                public String getAim() {
                    return aim;
                }

                public void setAim(String aim) {
                    this.aim = aim;
                }

                public Aim withAim(String aim) {
                    this.aim = aim;
                    return this;
                }

                public Integer getMasterId() {
                    return masterId;
                }

                public void setMasterId(Integer masterId) {
                    this.masterId = masterId;
                }

                public Aim withMasterId(Integer masterId) {
                    this.masterId = masterId;
                    return this;
                }

                public String getCreatedAt() {
                    return createdAt;
                }

                public void setCreatedAt(String createdAt) {
                    this.createdAt = createdAt;
                }

                public Aim withCreatedAt(String createdAt) {
                    this.createdAt = createdAt;
                    return this;
                }

                public String getUpdatedAt() {
                    return updatedAt;
                }

                public void setUpdatedAt(String updatedAt) {
                    this.updatedAt = updatedAt;
                }

                public Aim withUpdatedAt(String updatedAt) {
                    this.updatedAt = updatedAt;
                    return this;
                }

                public Integer getPlannerProjectId() {
                    return plannerProjectId;
                }

                public void setPlannerProjectId(Integer plannerProjectId) {
                    this.plannerProjectId = plannerProjectId;
                }

                public Aim withPlannerProjectId(Integer plannerProjectId) {
                    this.plannerProjectId = plannerProjectId;
                    return this;
                }

                public List<Goal> getGoals() {
                    return goals;
                }

                public void setGoals(List<Goal> goals) {
                    this.goals = goals;
                }

                public Aim withGoals(List<Goal> goals) {
                    this.goals = goals;
                    return this;
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

                public class Goal {

                    @SerializedName("id")
                    @Expose
                    private Integer id;
                    @SerializedName("user_id")
                    @Expose
                    private Integer userId;
                    @SerializedName("planner_aim_id")
                    @Expose
                    private Integer plannerAimId;
                    @SerializedName("goal")
                    @Expose
                    private String goal;
                    @SerializedName("master_id")
                    @Expose
                    private Integer masterId;
                    @SerializedName("created_at")
                    @Expose
                    private String createdAt;
                    @SerializedName("updated_at")
                    @Expose
                    private String updatedAt;

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

                    private boolean isEditable;
                    private boolean isDeletable;
                    private boolean isViewable;
                    private boolean isChangeable;

                    private boolean isExpanded;

                    private boolean isSelected;

                    public Integer getId() {
                        return id;
                    }

                    public void setId(Integer id) {
                        this.id = id;
                    }

                    public Goal withId(Integer id) {
                        this.id = id;
                        return this;
                    }

                    public Integer getUserId() {
                        return userId;
                    }

                    public void setUserId(Integer userId) {
                        this.userId = userId;
                    }

                    public Goal withUserId(Integer userId) {
                        this.userId = userId;
                        return this;
                    }

                    public Integer getPlannerAimId() {
                        return plannerAimId;
                    }

                    public void setPlannerAimId(Integer plannerAimId) {
                        this.plannerAimId = plannerAimId;
                    }

                    public Goal withPlannerAimId(Integer plannerAimId) {
                        this.plannerAimId = plannerAimId;
                        return this;
                    }

                    public String getGoal() {
                        return goal;
                    }

                    public void setGoal(String goal) {
                        this.goal = goal;
                    }

                    public Goal withGoal(String goal) {
                        this.goal = goal;
                        return this;
                    }

                    public Integer getMasterId() {
                        return masterId;
                    }

                    public void setMasterId(Integer masterId) {
                        this.masterId = masterId;
                    }

                    public Goal withMasterId(Integer masterId) {
                        this.masterId = masterId;
                        return this;
                    }

                    public String getCreatedAt() {
                        return createdAt;
                    }

                    public void setCreatedAt(String createdAt) {
                        this.createdAt = createdAt;
                    }

                    public Goal withCreatedAt(String createdAt) {
                        this.createdAt = createdAt;
                        return this;
                    }

                    public String getUpdatedAt() {
                        return updatedAt;
                    }

                    public void setUpdatedAt(String updatedAt) {
                        this.updatedAt = updatedAt;
                    }

                    public Goal withUpdatedAt(String updatedAt) {
                        this.updatedAt = updatedAt;
                        return this;
                    }

                }

            }

        }


    }

}