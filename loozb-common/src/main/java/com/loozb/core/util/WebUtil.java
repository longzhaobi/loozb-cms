package com.loozb.core.util;

import com.loozb.core.Constants;
import com.loozb.model.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Web层辅助类
 * 
 * @author ShenHuaJie
 * @version 2016年4月2日 下午4:19:28
 */
public final class WebUtil {
    private WebUtil() {
    }

    private static Logger logger = LogManager.getLogger();

    public volatile static Long currentUser = 0L;

    /** 获取当前用户 */
    public static final Long getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取指定Cookie的值
     * 
     * @param cookieName cookie名字
     * @param defaultValue 缺省值
     * @return
     */
    public static final String getCookieValue(HttpServletRequest request, String cookieName, String defaultValue) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return defaultValue;
        }
        return cookie.getValue();
    }

    /**
     * 获得国际化信息
     * 
     * @param key 键
     * @param request
     * @return
     */
    public static final String getApplicationResource(String key, HttpServletRequest request) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("ApplicationResources", request.getLocale());
        return resourceBundle.getString(key);
    }

    /**
     * 获得参数Map
     * 
     * @param request
     * @return
     */
    public static final Map<String, Object> getParameterMap(HttpServletRequest request) {
        return WebUtils.getParametersStartingWith(request, null);
    }

    /** 获取客户端IP */
    public static final String getHost(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("127.0.0.1".equals(ip)) {
            InetAddress inet = null;
            try { // 根据网卡取本机配置的IP
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ip = inet.getHostAddress();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /** 获取当前用户 */
    public static final SysUser getCurrentUser(HttpServletRequest request) {
        try {
            return WebUtil.getUserByToken(getCurrentToken(request));
        } catch (InvalidSessionException e) {
            logger.error(e);
        }
        return null;
    }

    /** 获取当前TOken */
    public static final String getCurrentToken(HttpServletRequest request) {
        return WebUtil.getCookieValue(request, Constants.TOKEN, null);
    }

    public static final SysUser getUserByToken(String token) {
        if(StringUtils.isNotBlank(token)) {
            return (SysUser)CacheUtil.getCache().get(Constants.REDIS_SESSION_TOKEN + token);
        } else {
            return null;
        }

    }

    public static String getUsernameByToken(String token) {
        SysUser user = getUserByToken(token);
        if (user != null) {
            String username = user.getUsername();
            //刷新，防止正在使用的用户过期
            CacheUtil.getCache().expire(Constants.REDIS_SESSION_TOKEN + token, 1800);
            CacheUtil.getCache().expire(Constants.REDIS_SESSION_ID + user.getId(), 1800);
            return username;
        }
        return null;
    }

    public static String getTokenByUserId(Long userId) {
        String key = Constants.REDIS_SESSION_ID + userId;
        return (String)CacheUtil.getCache().get(key);
    }

    public static void clear(String token) {
        WebUtil.clear(token, null);
    }

    public static void clear(Long userId) {
        WebUtil.clear(null, userId);
    }

    public static void clear(String token, Long userId) {
        if(StringUtils.isNotBlank(token)) {
            CacheUtil.getCache().del(Constants.REDIS_SESSION_TOKEN + token);
        }
        if(userId != null) {
            CacheUtil.getCache().del(Constants.REDIS_SESSION_ID + userId);
        }
    }

    /**
     * 获取在线用户数量
     * @return
     */
    public static Integer getAllUserNumber() {
        String key = Constants.REDIS_SESSION_TOKEN + "*";
        Set<Object> online = CacheUtil.getCache().getAll(key);
        return online.size();
    }

    /**
     * 获取所有在线用户实体
     * @return
     */
    public static Set<Object> getAllUser() {
        String key = Constants.REDIS_SESSION_TOKEN + "*";
        Set<Object> online = CacheUtil.getCache().getAll(key);
        return online;
    }
}
