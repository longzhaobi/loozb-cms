package com.loozb.web;

import com.loozb.core.Constants;
import com.loozb.core.base.AbstractController;
import com.loozb.core.base.Parameter;
import com.loozb.core.bind.annotation.CurrentUser;
import com.loozb.core.bind.annotation.Token;
import com.loozb.core.config.Resources;
import com.loozb.core.exception.LoginException;
import com.loozb.core.support.Assert;
import com.loozb.core.support.HttpCode;
import com.loozb.core.support.login.LoginHelper;
import com.loozb.core.util.*;
import com.loozb.core.utils.PasswordUtil;
import com.loozb.model.SysResource;
import com.loozb.model.SysUser;
import com.loozb.model.ext.Authority;
import com.loozb.provider.ISysProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    public Object login(ModelMap modelMap,
                        @ApiParam(required = true, value = "登录帐号") @RequestParam(value = "account") String account,
                        @ApiParam(required = true, value = "登录密码") @RequestParam(value = "password") String password) {
        Assert.notNull(account, "ACCOUNT");
        Assert.notNull(password, "PASSWORD");

        Map<String, Object> params = ParamUtil.getMap();
        params.put("account", account);

        Parameter parameter = new Parameter("sysUserService", "queryList").setMap(params);
        logger.info("{} execute sysUserService.queryList start...", parameter.getNo());
        List<?> list = provider.execute(parameter).getList();
        logger.info("{} execute sysUserService.queryList end.", parameter.getNo());
        if (list.size() == 1) {
            SysUser user = (SysUser) list.get(0);
            if (user == null) {
                throw new LoginException(Resources.getMessage("LOGIN_FAIL", account));
            }

            if ("1".equals(user.getLocked())) {
                throw new LoginException(Resources.getMessage("ACCOUNT_LOCKED", account));
            }
            Long userId = user.getId();
            //判断该用户是否已经登录，如果已经登录，则强制对方下线
            String token = WebUtil.getTokenByUserId(userId);
            if (StringUtils.isNotBlank(token)) {
                WebUtil.clear(token, userId);
            }

            if (user.getPassword().equals(PasswordUtil.decryptPassword(password, user.getSalt()))) {
                //获取角色信息
                Parameter rolesParameter = new Parameter("sysAuthService", "findRoles").setId(userId);
                Set<String> roles = (Set<String>) provider.execute(rolesParameter).getSet();

                //获取权限信息
                Parameter permissionsParameter = new Parameter("sysAuthService", "findPermissions").setId(userId);
                Set<String> permissions = (Set<String>) provider.execute(permissionsParameter).getSet();

                //获取资源信息
                List<SysResource> menus = null;
                String menuCacheKey = "REDIS:MENU:" + userId;
                String menuCache = (String) CacheUtil.getCache().get(menuCacheKey);
                if (StringUtils.isNotBlank(menuCache)) {
                    menus = JsonUtils.jsonToList(menuCache, SysResource.class);
                } else {
                    Parameter resourceParameter = new Parameter("sysResourceService", "getMenus").setId(userId);
                    menus = (List<SysResource>) provider.execute(resourceParameter).getList();
                    if (menus != null) {
                        CacheUtil.getCache().set(menuCacheKey, JsonUtils.objectToJson(menus));
                    }
                }

                // 生成token
                String accessToken = UUID.randomUUID().toString();
                user.setPassword(null);
                user.setSalt(null);

                CacheUtil.getCache().set(Constants.REDIS_SESSION + "TOKEN:" + accessToken, user);
                CacheUtil.getCache().set(Constants.REDIS_SESSION + "ID:" + user.getId(), accessToken);

                return setSuccessModelMap(modelMap, new Authority(roles, permissions, menus, user, accessToken));
            }
        }
        return setModelMap(modelMap, HttpCode.LOGIN_FAIL, Resources.getMessage("LOGIN_FAIL", account));
    }

    // 登出
    @ApiOperation(value = "用户登出")
    @PostMapping("/logout")
    public Object logout(ModelMap modelMap, HttpServletRequest request, @CurrentUser SysUser user, @Token String token) {
        System.out.println(user.getId());
        System.out.println(token);
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
