package com.dogfood.aa20240808.processV2.system.domain.structure;
import java.util.List;
public class FormDataPropertyPermission {
    public String propertyName;
    public String permission;
    public List<FormDataPropertyPermission> subFieldPermissions;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<FormDataPropertyPermission> getSubFieldPermissions() {
        return subFieldPermissions;
    }

    public void setSubFieldPermissions(List<FormDataPropertyPermission> subFieldPermissions) {
        this.subFieldPermissions = subFieldPermissions;
    }
}
