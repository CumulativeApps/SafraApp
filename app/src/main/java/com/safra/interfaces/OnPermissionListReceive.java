package com.safra.interfaces;

import com.safra.models.ModuleItem;

import java.util.List;

public interface OnPermissionListReceive {

    void getPermissions(List<ModuleItem> permissions);

}
