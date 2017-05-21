package com.loozb.model.ext;

import com.loozb.model.Login;
import com.loozb.model.SysResource;

import java.util.List;
import java.util.Set;

/**
 * 权限
 * @Author： 龙召碧
 * @Date: Created in 2017-2-10 0:35
 */
public class Authority {

    /**
     * 拥有角色
     */
    private Set<String> hasRoles;

    /**
     * 拥有权限
     */
    private Set<String> hasPermissions;

    /**
     * 拥有菜单资源
     */
    private List<SysResource> hasMenus;

    /**
     * 用户信息
     */
    private Login login;

    public Authority() {
    };

    public Authority(Set<String> hasRoles, Set<String> hasPermissions, List<SysResource> hasMenus,Login login) {
        this.hasRoles = hasRoles;
        this.hasPermissions = hasPermissions;
        this.hasMenus = hasMenus;
        this.login = login;
    }

    public Set<String> getHasRoles() {
        return hasRoles;
    }

    public void setHasRoles(Set<String> hasRoles) {
        this.hasRoles = hasRoles;
    }

    public Set<String> getHasPermissions() {
        return hasPermissions;
    }

    public void setHasPermissions(Set<String> hasPermissions) {
        this.hasPermissions = hasPermissions;
    }

    public List<SysResource> getHasMenus() {
        return hasMenus;
    }

    public void setHasMenus(List<SysResource> hasMenus) {
        this.hasMenus = hasMenus;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

}