package com.safra.events;

import com.safra.models.UserItem;

import java.util.List;

public class UserListSyncEvent {

    List<UserItem> userList;

    public UserListSyncEvent(List<UserItem> userList) {
        this.userList = userList;
    }

    public List<UserItem> getUserList() {
        return userList;
    }

    public void setUserList(List<UserItem> userList) {
        this.userList = userList;
    }
}
