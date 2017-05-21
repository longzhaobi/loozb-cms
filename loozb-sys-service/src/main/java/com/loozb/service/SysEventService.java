package com.loozb.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.loozb.core.base.BaseService;
import com.loozb.model.SysEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 操作日志表  服务实现类
 * </p>
 *
 * @author LongZhaoBi
 * @since 2017-05-20
 */
@Service
@CacheConfig(cacheNames = "SysLog")
public class SysEventService extends BaseService<SysEvent> {
    @Autowired
    private SysUserService sysUserService;

    public Page<SysEvent> query(Map<String, Object> params) {
        Page<SysEvent> page = super.query(params);
//        for (SysEvent sysEvent : page.getRecords()) {
//            Long createId = sysEvent.getCreateId();
//            if (createId != null) {
//                SysUser sysUser = sysUserService.queryById(createId);
//                if (sysUser != null) {
//                    sysEvent.setUsername(sysUser.getUsername());
//                } else {
//                    sysEvent.setUsername(createId.toString());
//                }
//            }
//        }
        return page;
    }
}