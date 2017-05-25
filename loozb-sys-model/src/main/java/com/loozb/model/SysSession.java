package com.loozb.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.loozb.core.base.BaseModel;

import java.util.Date;


/**
 * <p>
 * 用户角色信息表
 * </p>
 *
 * @author 龙召碧
 * @since 2017-02-26
 */
@TableName("sys_session")
public class SysSession extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * session
     */
	@TableField("session_id")
	private String sessionId;
    /**
     * 帐号
     */
	private String account;

	/**
	 * 帐号ID
	 */
	@TableField("user_id")
	private Long userId;
    /**
     * 登录IP
     */
	private String ip;
    /**
     * 备注
     */
	private String remark;
    /**
     * 登录时间
     */
	@TableField("start_time")
	private Date startTime;

	@TableField(exist = false)
	private String online;


	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}
}
