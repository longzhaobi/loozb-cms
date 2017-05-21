package com.loozb.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.loozb.core.base.BaseModel;


/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author LongZhaoBi
 * @since 2017-05-20
 */
@TableName("sys_event")
@SuppressWarnings("serial")
public class SysEvent extends BaseModel {

    @TableField("title")
    private String title;
    @TableField("request_uri")
    private String requestUri;
    @TableField("parameters")
    private String parameters;
    @TableField("method")
    private String method;
    @TableField("client_host")
    private String clientHost;
    @TableField("user_agent")
    private String userAgent;
    @TableField("status")
    private Integer status;
    @TableField("remark")
    private String remark;

    @TableField(exist = false)
    private String username;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SysEvent sysEvent = (SysEvent) o;

        if (title != null ? !title.equals(sysEvent.title) : sysEvent.title != null) return false;
        if (requestUri != null ? !requestUri.equals(sysEvent.requestUri) : sysEvent.requestUri != null) return false;
        if (parameters != null ? !parameters.equals(sysEvent.parameters) : sysEvent.parameters != null) return false;
        if (method != null ? !method.equals(sysEvent.method) : sysEvent.method != null) return false;
        if (clientHost != null ? !clientHost.equals(sysEvent.clientHost) : sysEvent.clientHost != null) return false;
        if (userAgent != null ? !userAgent.equals(sysEvent.userAgent) : sysEvent.userAgent != null) return false;
        if (status != null ? !status.equals(sysEvent.status) : sysEvent.status != null) return false;
        return remark != null ? remark.equals(sysEvent.remark) : sysEvent.remark == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (requestUri != null ? requestUri.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (clientHost != null ? clientHost.hashCode() : 0);
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        return result;
    }
}