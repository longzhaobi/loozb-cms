package com.loozb.web;

import com.loozb.core.base.AbstractController;
import com.loozb.core.base.Parameter;
import com.loozb.core.config.Resources;
import com.loozb.core.exception.LoginException;
import com.loozb.core.support.Assert;
import com.loozb.core.support.HttpCode;
import com.loozb.core.support.login.LoginHelper;
import com.loozb.core.util.CacheUtil;
import com.loozb.core.util.JsonUtils;
import com.loozb.core.util.SecurityUtil;
import com.loozb.core.util.WebUtil;
import com.loozb.model.Login;
import com.loozb.model.SysResource;
import com.loozb.model.SysUser;
import com.loozb.model.ext.Authority;
import com.loozb.provider.ISysProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户登录
 *
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:11:21
 */
@RestController
@Api(value = "登录接口", description = "登录接口")
public class LoginController extends AbstractController<ISysProvider> {

    public String getService() {
        return "sysUserService";
    }

    // 登录
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public Object login(@ApiParam(required = true, value = "登录帐号和密码") Login user, ModelMap modelMap,
                        HttpServletRequest request) {
        Assert.notNull(user.getAccount(), "ACCOUNT");
        Assert.notNull(user.getPassword(), "PASSWORD");
        if (LoginHelper.login(user.getAccount(), user.getPassword())) {
            request.setAttribute("msg", "[" + user.getAccount() + "]登录成功.");
            Long userId = (Long) WebUtil.getCurrentUser();

            //获取角色信息
            Set<String> roles = null;
            String roleCacheKey = "REDIS:ROLE:" + userId;
            String roleCache = (String)CacheUtil.getCache().get(roleCacheKey);
            if(StringUtils.isNotBlank(roleCache)) {
                roles = new HashSet<String>();
                String[] arr = roleCache.split(",");
                for (String a : arr) {
                    roles.add(a);
                }
            } else {
                Parameter rolesParameter = new Parameter("sysAuthService", "findRoles").setId(userId);
                roles = (Set<String>)provider.execute(rolesParameter).getSet();
                if(roles != null) {
                    CacheUtil.getCache().set(roleCacheKey, StringUtils.join(roles.toArray(), ","));
                }
            }

            //获取权限信息
            Set<String> permissions = null;
            String permissionCacheKey = "REDIS:PERMISSION:" + userId;
            String permissionCache = (String)CacheUtil.getCache().get(permissionCacheKey);
            if(StringUtils.isNotBlank(permissionCache)) {
                permissions = new HashSet<String>();
                String[] arr = permissionCache.split(",");
                for (String a : arr) {
                    permissions.add(a);
                }
            } else {
                Parameter permissionsParameter = new Parameter("sysAuthService", "findPermissions").setId(userId);
                permissions = (Set<String>)provider.execute(permissionsParameter).getSet();
                if(permissions != null) {
                    CacheUtil.getCache().set(permissionCacheKey, StringUtils.join(permissions.toArray(), ","));
                }
            }

            //获取资源信息
            List<SysResource> menus = null;
            String menuCacheKey = "REDIS:MENU:" + userId;
            String menuCache = (String)CacheUtil.getCache().get(menuCacheKey);
            if(StringUtils.isNotBlank(menuCache)) {
                menus = JsonUtils.jsonToList(menuCache, SysResource.class);
            } else {
                Parameter resourceParameter = new Parameter("sysResourceService", "getMenus").setId(userId);
                menus = (List<SysResource>)provider.execute(resourceParameter).getList();
                if(menus != null) {
                    CacheUtil.getCache().set(menuCacheKey, JsonUtils.objectToJson(menus));
                }
            }
            return setSuccessModelMap(modelMap, new Authority(roles, permissions, menus, user));
        }
        request.setAttribute("msg", "[" + user.getAccount() + "]登录失败.");
        throw new LoginException(Resources.getMessage("LOGIN_FAIL"));
    }

    // 登出
    @ApiOperation(value = "用户登出")
    @PostMapping("/logout")
    public Object logout(ModelMap modelMap) {
        SecurityUtils.getSubject().logout();
        return setSuccessModelMap(modelMap);
    }

    // 注册
    @ApiOperation(value = "用户注册")
    @PostMapping("/regin")
    public Object regin(ModelMap modelMap, SysUser sysUser) {
        Assert.notNull(sysUser.getUsername(), "ACCOUNT");
        Assert.notNull(sysUser.getPassword(), "PASSWORD");
        sysUser.setPassword(SecurityUtil.encryptPassword(sysUser.getPassword()));
        provider.execute(new Parameter("sysUserService", "update").setModel(sysUser));
        if (LoginHelper.login(sysUser.getUsername(), sysUser.getPassword())) {
            return setSuccessModelMap(modelMap);
        }
        throw new IllegalArgumentException(Resources.getMessage("LOGIN_FAIL"));
    }

    // 没有登录
    @ApiOperation(value = "没有登录")
    @RequestMapping(value = "/unauthorized", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Object unauthorized(ModelMap modelMap) throws Exception {
        return setModelMap(modelMap, HttpCode.UNAUTHORIZED);
    }

    // 没有权限
    @ApiOperation(value = "没有权限")
    @RequestMapping(value = "/forbidden", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Object forbidden(ModelMap modelMap) {
        return setModelMap(modelMap, HttpCode.FORBIDDEN);
    }
}
