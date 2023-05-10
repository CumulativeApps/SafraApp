package com.safra.events;

import com.safra.models.RoleItem;

import java.util.List;

public class GroupListSyncEvent {

    List<RoleItem> groupList;

    public GroupListSyncEvent(List<RoleItem> groupList) {
        this.groupList = groupList;
    }

    public List<RoleItem> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<RoleItem> groupList) {
        this.groupList = groupList;
    }
}
