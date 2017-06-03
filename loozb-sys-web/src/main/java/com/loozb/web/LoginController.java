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
import com.loozb.model.SysResource;
import com.loozb.model.SysSession;
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
import java.util.*;

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
    public Object login(ModelMap modelMap, HttpServletRequest request, 
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

                CacheUtil.getCache().set(Constants.REDIS_SESSION_TOKEN + accessToken, user, 1800);
                CacheUtil.getCache().set(Constants.REDIS_SESSION_ID + user.getId(), accessToken, 1800);
                saveSession(account, request, accessToken, user);
                return setSuccessModelMap(modelMap, new Authority(roles, permissions, menus, user, accessToken));
            }
        }
        return setModelMap(modelMap, HttpCode.LOGIN_FAIL, Resources.getMessage("LOGIN_FAIL", account));
    }

    private void saveSession(String account, HttpServletRequest request, String accessToken, SysUser user) {
        // 踢出用户
        SysSession record = new SysSession();
        record.setAccount(account);
        Parameter parameter = new Parameter("sysSessionService", "querySessionIdByAccount").setModel(record);
        logger.info("{} execute querySessionIdByAccount start...", parameter.getNo());
        List<?> sessions = provider.execute(parameter).getList();
        logger.info("{} execute querySessionIdByAccount end.", parameter.getNo());

        //到达这一步时，说明用户已经在线了，此时通过account查处SysSessioin后，如果有值进行更新，如果没有新增
        if(sessions != null && sessions.size() > 0) {
            //删除
            record.setUserId(user.getId());
            parameter = new Parameter("sysSessionService", "deleteByUserId").setModel(record);
            logger.info("{} execute deleteByUserId start...", parameter.getNo());
            provider.execute(parameter);
            logger.info("{} execute deleteByUserId end.", parameter.getNo());
        }
        // 保存用户
        record.setSessionId(accessToken);
        String host = WebUtil.getHost(request);
        record.setIp(host);
        record.setUserId(user.getId());
        record.setStartTime(new Date());
        parameter = new Parameter("sysSessionService", "update").setModel(record);
        logger.info("{} execute sysSessionService.update start...", parameter.getNo());
        provider.execute(parameter);
        logger.info("{} execute sysSessionService.update end.", parameter.getNo());
    }

    // 登出
    @ApiOperation(value = "用户登出")
    @PostMapping("/logout")
    public Object logout(ModelMap modelMap, HttpServletRequest request, @CurrentUser SysUser user, @Token String token) {
        WebUtil.clear(token, user.getId());
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
