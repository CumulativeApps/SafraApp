package com.safra.models;

import java.util.List;

public class ModuleItem {

    private long moduleId;
    private String moduleName;
    private String ptModuleName;
    private List<PermissionItem> permissionList;

    public ModuleItem() {
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPtModuleName() {
        return ptModuleName;
    }

    public void setPtModuleName(String ptModuleName) {
        this.ptModuleName = ptModuleName;
    }

    public List<PermissionItem> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<PermissionItem> permissionList) {
        this.permissionList = permissionList;
    }
}
