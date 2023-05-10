package com.safra.events;

import com.safra.models.TaskItem;

import java.util.List;

public class TaskListSyncEvent {

    List<TaskItem> taskList;

    public TaskListSyncEvent(List<TaskItem> taskList) {
        this.taskList = taskList;
    }

    public List<TaskItem> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskItem> taskList) {
        this.taskList = taskList;
    }
}
