package com.loozb.service;

import com.loozb.core.base.BaseService;
import com.loozb.model.TbComment;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务类
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-27
 */
@Service
@CacheConfig(cacheNames = "TbComment")
public class TbCommentService extends BaseService<TbComment> {

}
