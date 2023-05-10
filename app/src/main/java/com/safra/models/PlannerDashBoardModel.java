package com.safra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlannerDashBoardModel {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * No args constructor for use in serialization
     */
    public PlannerDashBoardModel() {
    }

    /**
     * @param data
     * @param success
     * @param message
     */
    public PlannerDashBoardModel(Integer success, String message, Data data) {
        super();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public PlannerDashBoardModel withSuccess(Integer success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PlannerDashBoardModel withMessage(String message) {
        this.message = message;
        return this;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public PlannerDashBoardModel withData(Data data) {
        this.data = data;
        return this;
    }

    public class Data {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("active")
        @Expose
        private String active;
        @SerializedName("projects")
        @Expose
        private List<Project> projects;
        @SerializedName("tasks")
        @Expose
        private Tasks tasks;

        /**
         * No args constructor for use in serialization
         */
        public Data() {
        }

        /**
         * @param projects
         * @param active
         * @param title
         * @param tasks
         */
        public Data(String title, String active, List<Project> projects, Tasks tasks) {
            super();
            this.title = title;
            this.active = active;
            this.projects = projects;
            this.tasks = tasks;
        }

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

        public Tasks getTasks() {
            return tasks;
        }

        public void setTasks(Tasks tasks) {
            this.tasks = tasks;
        }

        public Data withTasks(Tasks tasks) {
            this.tasks = tasks;
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
            @SerializedName("aims")
            @Expose
            private List<Aim> aims;

            /**
             * No args constructor for use in serialization
             */
            public Project() {
            }

            /**
             * @param masterId
             * @param createdAt
             * @param aims
             * @param financier
             * @param endDate
             * @param name
             * @param currency
             * @param id
             * @param userId
             * @param startDate
             * @param updatedAt
             * @param status
             */
            public Project(Integer id, Integer userId, String name, Integer masterId, String createdAt, String updatedAt, String startDate, String endDate, String financier, Integer status, String currency, List<Aim> aims) {
                super();
                this.id = id;
                this.userId = userId;
                this.name = name;
                this.masterId = masterId;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
                this.startDate = startDate;
                this.endDate = endDate;
                this.financier = financier;
                this.status = status;
                this.currency = currency;
                this.aims = aims;
            }

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

            public List<Aim> getAims() {
                return aims;
            }

            public void setAims(List<Aim> aims) {
                this.aims = aims;
            }

            public Project withAims(List<Aim> aims) {
                this.aims = aims;
                return this;
            }

            public class Aim {

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

                /**
                 * No args constructor for use in serialization
                 */
                public Aim() {
                }

                /**
                 * @param masterId
                 * @param createdAt
                 * @param aim
                 * @param plannerProjectId
                 * @param id
                 * @param userId
                 * @param updatedAt
                 * @param goals
                 */
                public Aim(Integer id, Integer userId, String aim, Integer masterId, String createdAt, String updatedAt, Integer plannerProjectId, List<Goal> goals) {
                    super();
                    this.id = id;
                    this.userId = userId;
                    this.aim = aim;
                    this.masterId = masterId;
                    this.createdAt = createdAt;
                    this.updatedAt = updatedAt;
                    this.plannerProjectId = plannerProjectId;
                    this.goals = goals;
                }

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
                    @SerializedName("tasks")
                    @Expose
                    private List<Task> tasks;

                    /**
                     * No args constructor for use in serialization
                     */
                    public Goal() {
                    }

                    /**
                     * @param masterId
                     * @param createdAt
                     * @param goal
                     * @param id
                     * @param userId
                     * @param plannerAimId
                     * @param tasks
                     * @param updatedAt
                     */
                    public Goal(Integer id, Integer userId, Integer plannerAimId, String goal, Integer masterId, String createdAt, String updatedAt, List<Task> tasks) {
                        super();
                        this.id = id;
                        this.userId = userId;
                        this.plannerAimId = plannerAimId;
                        this.goal = goal;
                        this.masterId = masterId;
                        this.createdAt = createdAt;
                        this.updatedAt = updatedAt;
                        this.tasks = tasks;
                    }

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

                    public List<Task> getTasks() {
                        return tasks;
                    }

                    public void setTasks(List<Task> tasks) {
                        this.tasks = tasks;
                    }

                    public Goal withTasks(List<Task> tasks) {
                        this.tasks = tasks;
                        return this;
                    }

                    public class Task {

                        @SerializedName("id")
                        @Expose
                        private Integer id;
                        @SerializedName("user_id")
                        @Expose
                        private Integer userId;
                        @SerializedName("planner_goal_id")
                        @Expose
                        private Integer plannerGoalId;
                        @SerializedName("user_ids")
                        @Expose
                        private Object userIds;
                        @SerializedName("title")
                        @Expose
                        private String title;
                        @SerializedName("priority")
                        @Expose
                        private Integer priority;
                        @SerializedName("status")
                        @Expose
                        private Integer status;
                        @SerializedName("observation")
                        @Expose
                        private String observation;
                        @SerializedName("metrics")
                        @Expose
                        private Object metrics;
                        @SerializedName("responsible")
                        @Expose
                        private String responsible;
                        @SerializedName("supervisor")
                        @Expose
                        private String supervisor;
                        @SerializedName("start_date")
                        @Expose
                        private String startDate;
                        @SerializedName("end_date")
                        @Expose
                        private String endDate;
                        @SerializedName("created_at")
                        @Expose
                        private String createdAt;
                        @SerializedName("updated_at")
                        @Expose
                        private String updatedAt;
                        @SerializedName("resources")
                        @Expose
                        private List<Resource> resources;

                        /**
                         * No args constructor for use in serialization
                         */
                        public Task() {
                        }

                        /**
                         * @param endDate
                         * @param observation
                         * @param resources
                         * @param title
                         * @param priority
                         * @param userId
                         * @param plannerGoalId
                         * @param createdAt
                         * @param userIds
                         * @param responsible
                         * @param id
                         * @param metrics
                         * @param supervisor
                         * @param startDate
                         * @param status
                         * @param updatedAt
                         */
                        public Task(Integer id, Integer userId, Integer plannerGoalId, Object userIds, String title, Integer priority, Integer status, String observation, Object metrics, String responsible, String supervisor, String startDate, String endDate, String createdAt, String updatedAt, List<Resource> resources) {
                            super();
                            this.id = id;
                            this.userId = userId;
                            this.plannerGoalId = plannerGoalId;
                            this.userIds = userIds;
                            this.title = title;
                            this.priority = priority;
                            this.status = status;
                            this.observation = observation;
                            this.metrics = metrics;
                            this.responsible = responsible;
                            this.supervisor = supervisor;
                            this.startDate = startDate;
                            this.endDate = endDate;
                            this.createdAt = createdAt;
                            this.updatedAt = updatedAt;
                            this.resources = resources;
                        }

                        public Integer getId() {
                            return id;
                        }

                        public void setId(Integer id) {
                            this.id = id;
                        }

                        public Task withId(Integer id) {
                            this.id = id;
                            return this;
                        }

                        public Integer getUserId() {
                            return userId;
                        }

                        public void setUserId(Integer userId) {
                            this.userId = userId;
                        }

                        public Task withUserId(Integer userId) {
                            this.userId = userId;
                            return this;
                        }

                        public Integer getPlannerGoalId() {
                            return plannerGoalId;
                        }

                        public void setPlannerGoalId(Integer plannerGoalId) {
                            this.plannerGoalId = plannerGoalId;
                        }

                        public Task withPlannerGoalId(Integer plannerGoalId) {
                            this.plannerGoalId = plannerGoalId;
                            return this;
                        }

                        public Object getUserIds() {
                            return userIds;
                        }

                        public void setUserIds(Object userIds) {
                            this.userIds = userIds;
                        }

                        public Task withUserIds(Object userIds) {
                            this.userIds = userIds;
                            return this;
                        }

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
                        }

                        public Task withTitle(String title) {
                            this.title = title;
                            return this;
                        }

                        public Integer getPriority() {
                            return priority;
                        }

                        public void setPriority(Integer priority) {
                            this.priority = priority;
                        }

                        public Task withPriority(Integer priority) {
                            this.priority = priority;
                            return this;
                        }

                        public Integer getStatus() {
                            return status;
                        }

                        public void setStatus(Integer status) {
                            this.status = status;
                        }

                        public Task withStatus(Integer status) {
                            this.status = status;
                            return this;
                        }

                        public String getObservation() {
                            return observation;
                        }

                        public void setObservation(String observation) {
                            this.observation = observation;
                        }

                        public Task withObservation(String observation) {
                            this.observation = observation;
                            return this;
                        }

                        public Object getMetrics() {
                            return metrics;
                        }

                        public void setMetrics(Object metrics) {
                            this.metrics = metrics;
                        }

                        public Task withMetrics(Object metrics) {
                            this.metrics = metrics;
                            return this;
                        }

                        public String getResponsible() {
                            return responsible;
                        }

                        public void setResponsible(String responsible) {
                            this.responsible = responsible;
                        }

                        public Task withResponsible(String responsible) {
                            this.responsible = responsible;
                            return this;
                        }

                        public String getSupervisor() {
                            return supervisor;
                        }

                        public void setSupervisor(String supervisor) {
                            this.supervisor = supervisor;
                        }

                        public Task withSupervisor(String supervisor) {
                            this.supervisor = supervisor;
                            return this;
                        }

                        public String getStartDate() {
                            return startDate;
                        }

                        public void setStartDate(String startDate) {
                            this.startDate = startDate;
                        }

                        public Task withStartDate(String startDate) {
                            this.startDate = startDate;
                            return this;
                        }

                        public String getEndDate() {
                            return endDate;
                        }

                        public void setEndDate(String endDate) {
                            this.endDate = endDate;
                        }

                        public Task withEndDate(String endDate) {
                            this.endDate = endDate;
                            return this;
                        }

                        public String getCreatedAt() {
                            return createdAt;
                        }

                        public void setCreatedAt(String createdAt) {
                            this.createdAt = createdAt;
                        }

                        public Task withCreatedAt(String createdAt) {
                            this.createdAt = createdAt;
                            return this;
                        }

                        public String getUpdatedAt() {
                            return updatedAt;
                        }

                        public void setUpdatedAt(String updatedAt) {
                            this.updatedAt = updatedAt;
                        }

                        public Task withUpdatedAt(String updatedAt) {
                            this.updatedAt = updatedAt;
                            return this;
                        }

                        public List<Resource> getResources() {
                            return resources;
                        }

                        public void setResources(List<Resource> resources) {
                            this.resources = resources;
                        }

                        public Task withResources(List<Resource> resources) {
                            this.resources = resources;
                            return this;
                        }

                        public class Resource {

                            @SerializedName("id")
                            @Expose
                            private Integer id;
                            @SerializedName("planner_task_id")
                            @Expose
                            private Integer plannerTaskId;
                            @SerializedName("name")
                            @Expose
                            private String name;
                            @SerializedName("quantity")
                            @Expose
                            private Integer quantity;
                            @SerializedName("price")
                            @Expose
                            private Integer price;
                            @SerializedName("subtotal")
                            @Expose
                            private Integer subtotal;
                            @SerializedName("created_at")
                            @Expose
                            private String createdAt;
                            @SerializedName("updated_at")
                            @Expose
                            private String updatedAt;

                            /**
                             * No args constructor for use in serialization
                             */
                            public Resource() {
                            }

                            /**
                             * @param plannerTaskId
                             * @param createdAt
                             * @param quantity
                             * @param price
                             * @param subtotal
                             * @param name
                             * @param id
                             * @param updatedAt
                             */
                            public Resource(Integer id, Integer plannerTaskId, String name, Integer quantity, Integer price, Integer subtotal, String createdAt, String updatedAt) {
                                super();
                                this.id = id;
                                this.plannerTaskId = plannerTaskId;
                                this.name = name;
                                this.quantity = quantity;
                                this.price = price;
                                this.subtotal = subtotal;
                                this.createdAt = createdAt;
                                this.updatedAt = updatedAt;
                            }

                            public Integer getId() {
                                return id;
                            }

                            public void setId(Integer id) {
                                this.id = id;
                            }

                            public Resource withId(Integer id) {
                                this.id = id;
                                return this;
                            }

                            public Integer getPlannerTaskId() {
                                return plannerTaskId;
                            }

                            public void setPlannerTaskId(Integer plannerTaskId) {
                                this.plannerTaskId = plannerTaskId;
                            }

                            public Resource withPlannerTaskId(Integer plannerTaskId) {
                                this.plannerTaskId = plannerTaskId;
                                return this;
                            }

                            public String getName() {
                                return name;
                            }

                            public void setName(String name) {
                                this.name = name;
                            }

                            public Resource withName(String name) {
                                this.name = name;
                                return this;
                            }

                            public Integer getQuantity() {
                                return quantity;
                            }

                            public void setQuantity(Integer quantity) {
                                this.quantity = quantity;
                            }

                            public Resource withQuantity(Integer quantity) {
                                this.quantity = quantity;
                                return this;
                            }

                            public Integer getPrice() {
                                return price;
                            }

                            public void setPrice(Integer price) {
                                this.price = price;
                            }

                            public Resource withPrice(Integer price) {
                                this.price = price;
                                return this;
                            }

                            public Integer getSubtotal() {
                                return subtotal;
                            }

                            public void setSubtotal(Integer subtotal) {
                                this.subtotal = subtotal;
                            }

                            public Resource withSubtotal(Integer subtotal) {
                                this.subtotal = subtotal;
                                return this;
                            }

                            public String getCreatedAt() {
                                return createdAt;
                            }

                            public void setCreatedAt(String createdAt) {
                                this.createdAt = createdAt;
                            }

                            public Resource withCreatedAt(String createdAt) {
                                this.createdAt = createdAt;
                                return this;
                            }

                            public String getUpdatedAt() {
                                return updatedAt;
                            }

                            public void setUpdatedAt(String updatedAt) {
                                this.updatedAt = updatedAt;
                            }

                            public Resource withUpdatedAt(String updatedAt) {
                                this.updatedAt = updatedAt;
                                return this;
                            }

                        }

                    }

                }

            }

        }

        public class Tasks {

            @SerializedName("total")
            @Expose
            private Integer total;
            @SerializedName("done")
            @Expose
            private Integer done;
            @SerializedName("pending")
            @Expose
            private Integer pending;
            @SerializedName("expired")
            @Expose
            private Integer expired;

            /**
             * No args constructor for use in serialization
             */
            public Tasks() {
            }

            /**
             * @param total
             * @param expired
             * @param pending
             * @param done
             */
            public Tasks(Integer total, Integer done, Integer pending, Integer expired) {
                super();
                this.total = total;
                this.done = done;
                this.pending = pending;
                this.expired = expired;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public Tasks withTotal(Integer total) {
                this.total = total;
                return this;
            }

            public Integer getDone() {
                return done;
            }

            public void setDone(Integer done) {
                this.done = done;
            }

            public Tasks withDone(Integer done) {
                this.done = done;
                return this;
            }

            public Integer getPending() {
                return pending;
            }

            public void setPending(Integer pending) {
                this.pending = pending;
            }

            public Tasks withPending(Integer pending) {
                this.pending = pending;
                return this;
            }

            public Integer getExpired() {
                return expired;
            }

            public void setExpired(Integer expired) {
                this.expired = expired;
            }

            public Tasks withExpired(Integer expired) {
                this.expired = expired;
                return this;
            }

        }
    }

}








