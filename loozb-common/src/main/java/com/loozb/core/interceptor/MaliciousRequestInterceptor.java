package com.loozb.core.interceptor;

import com.loozb.core.Constants;
import com.loozb.core.support.HttpCode;
import com.loozb.core.util.CacheUtil;
import com.loozb.core.util.WebUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 恶意请求拦截器
 * 
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:16:57
 */
public class MaliciousRequestInterceptor extends BaseInterceptor {
	private Boolean allRequest = false; // 拦截所有请求,否则拦截相同请求
	private Long minRequestIntervalTime; // 允许的最小请求间隔
	private Integer maxMaliciousTimes; // 允许的最大恶意请求次数

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,OPTIONS,DELETE");
		response.setHeader("Access-Control-Allow-Headers",
				"x-requested-with,Access-Control-Allow-Origin,EX-SysAuthToken,EX-JSESSIONID");

		String url = request.getMethod() + request.getRequestURI();
		String ip = WebUtil.getHost(request);
		String urlKey = "FILTER:" + ip + ":URL"; //请求URL
		String timeKey = "FILTER:" + ip + ":TIME"; //请求时间
		String requestTimesKey = "FILTER:" + ip + ":MALICIOUS_REQUEST_TIMES"; //请求次数

		//获取上次请求地址
		String preRequest = (String)CacheUtil.getCache().get(urlKey);
		//获取上次请求时间
		Long preRequestTime = (Long)CacheUtil.getCache().get(timeKey);

		if(StringUtils.isNotBlank(preRequest) && preRequestTime != null) {
			//过滤频繁操作
			if((url.equals(preRequest) || allRequest) && System.currentTimeMillis() - preRequestTime < minRequestIntervalTime) {
				Integer maliciousRequestTimes = (Integer) CacheUtil.getCache().get(requestTimesKey);
				if(maliciousRequestTimes == null) {
					maliciousRequestTimes = 1;
				} else {
					maliciousRequestTimes ++ ;
				}
				CacheUtil.getCache().set(requestTimesKey, maliciousRequestTimes);
				if(maliciousRequestTimes > maxMaliciousTimes) {
					response.setStatus(HttpCode.MULTI_STATUS.value());
					logger.warn("To intercept a malicious request : {}", url);
					return false;
				}
			} else {
				CacheUtil.getCache().set(requestTimesKey, 0);
			}
		}
		CacheUtil.getCache().set(urlKey, url);
		CacheUtil.getCache().set(timeKey, System.currentTimeMillis());
		return super.preHandle(request, response, handler);
	}

	public void setAllRequest(Boolean allRequest) {
		this.allRequest = allRequest;
	}

	public void setMinRequestIntervalTime(Long minRequestIntervalTime) {
		this.minRequestIntervalTime = minRequestIntervalTime;
	}

	public void setMaxMaliciousTimes(Integer maxMaliciousTimes) {
		this.maxMaliciousTimes = maxMaliciousTimes;
	}
}
