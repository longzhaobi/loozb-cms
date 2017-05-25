package com.loozb.core.shiro.filter;

import com.loozb.core.Constants;
import com.loozb.core.shiro.token.StatelessToken;
import com.loozb.core.util.CookieUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessAuthFilter extends AccessControlFilter {
	private static Logger logger = LogManager.getLogger(StatelessAuthFilter.class);
	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest servletRequest,
			ServletResponse servletResponse) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String token = CookieUtils.getCookieValue(request, Constants.TOKEN);
		StatelessToken statelessToken = new StatelessToken(token);
		try {
			getSubject(servletRequest, servletResponse).login(statelessToken);
		} catch (Exception e) {
			e.printStackTrace();
			onLoginFail(response); // 6、登录失败
			return false;
		}
		return true;
	}

	/**
	 * 登录失败
	 * @param response
	 */
	private void onLoginFail(HttpServletResponse response) {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//402
		httpResponse.setContentType("text/html;charset=UTF-8");
		try {
			httpResponse.getWriter().write("登录时效已过期，请重新登录系统");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
