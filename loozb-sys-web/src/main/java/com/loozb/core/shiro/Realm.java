package com.loozb.core.shiro;

import com.loozb.core.base.BaseProvider;
import com.loozb.core.base.Parameter;
import com.loozb.core.util.ParamUtil;
import com.loozb.core.util.WebUtil;
import com.loozb.core.utils.PasswordUtil;
import com.loozb.model.SysSession;
import com.loozb.model.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限检查类
 *
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:44:45
 */
public class Realm extends AuthorizingRealm {
	private final Logger logger = LogManager.getLogger();
	@Autowired
	@Qualifier("sysProvider")
	protected BaseProvider provider;
	@Autowired
	private RedisOperationsSessionRepository sessionRepository;

	// 权限，初次加载需要权限的资源时会调用此处，之后全部走缓存
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		Long userId = (Long) WebUtil.getCurrentUser();

		Parameter rolesParameter = new Parameter("sysAuthService", "findRoles").setId(userId);
		Set<String> roles = (Set<String>) provider.execute(rolesParameter).getSet();

		//获取权限信息
		Parameter permissionsParameter = new Parameter("sysAuthService", "findPermissions").setId(Long.valueOf(userId));
		Set<String> permissions = (Set<String>) provider.execute(permissionsParameter).getSet();

		// 添加用户权限
		info.setRoles(roles);
		info.setStringPermissions(permissions);

		return info;
	}

	// 登录验证
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		Map<String, Object> params = ParamUtil.getMap();
		params.put("account", token.getUsername());
		Parameter parameter = new Parameter("sysUserService", "queryList").setMap(params);
		logger.info("{} execute sysUserService.queryList start...", parameter.getNo());
		List<?> list = provider.execute(parameter).getList();
		logger.info("{} execute sysUserService.queryList end.", parameter.getNo());
		if (list.size() == 1) {
			SysUser user = (SysUser) list.get(0);
			if(user.getLocked().equals("1")) {
				throw new LockedAccountException("该账户已被锁定");
			}
			//获取前端传过来的用户密码
			StringBuilder sb = new StringBuilder(100);
			for (int i = 0; i < token.getPassword().length; i++) {
				sb.append(token.getPassword()[i]);
			}
			if (user.getPassword().equals(PasswordUtil.decryptPassword(sb.toString(), user.getSalt()))) {
				WebUtil.saveCurrentUser(user.getId());
				saveSession(user.getUsername());
				AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getUsername(), sb.toString(),user.getUsername());
				return authcInfo;
			}
			logger.warn("USER [{}] PASSWORD IS WRONG: {}", token.getUsername(), sb.toString());
			return null;
		} else {
			logger.warn("No user: {}", token.getUsername());
			return null;
		}
	}

	/** 保存session */
	private void saveSession(String account) {
		// 踢出用户
		SysSession record = new SysSession();
		record.setAccount(account);
		Parameter parameter = new Parameter("sysSessionService", "querySessionIdByAccount").setModel(record);
		logger.info("{} execute querySessionIdByAccount start...", parameter.getNo());
		List<?> sessionIds = provider.execute(parameter).getList();
		logger.info("{} execute querySessionIdByAccount end.", parameter.getNo());
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		String currentSessionId= session.getId().toString();
		if (sessionIds != null) {
			for (Object sessionId : sessionIds) {
				record.setSessionId((String) sessionId);
				parameter = new Parameter("sysSessionService", "deleteBySessionId").setModel(record);
				logger.info("{} execute deleteBySessionId start...", parameter.getNo());
				provider.execute(parameter);
				logger.info("{} execute deleteBySessionId end.", parameter.getNo());
				if (!currentSessionId.equals(sessionId)) {
					sessionRepository.delete((String) sessionId);
					sessionRepository.cleanupExpiredSessions();
				}
			}
		}
		// 保存用户
		record.setSessionId(currentSessionId);
		String host = (String) session.getAttribute("HOST");
		record.setIp(StringUtils.isBlank(host) ? session.getHost() : host);
		record.setStartTime(session.getStartTimestamp());
		parameter = new Parameter("sysSessionService", "update").setModel(record);
		logger.info("{} execute sysSessionService.update start...", parameter.getNo());
		provider.execute(parameter);
		logger.info("{} execute sysSessionService.update end.", parameter.getNo());
	}
}
