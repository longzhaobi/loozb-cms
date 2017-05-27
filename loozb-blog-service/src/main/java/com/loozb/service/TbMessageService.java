package com.loozb.service;

import com.loozb.core.base.BaseService;
import com.loozb.model.TbMessage;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 留言 服务类
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-27
 */
@Service
@CacheConfig(cacheNames = "TbMessage")
public class TbMessageService extends BaseService<TbMessage> {
	
}
