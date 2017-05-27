package com.loozb.service;

import com.loozb.core.base.BaseService;
import com.loozb.mapper.TbClassificationMapper;
import com.loozb.model.TbClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章分类 服务类
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-25
 */
@Service
@CacheConfig(cacheNames = "TbClassification")
public class TbClassificationService extends BaseService<TbClassification> {
    @Autowired(required = false)
    private TbClassificationMapper tbClassificationMapper;

    @Override
    public List<TbClassification> queryList(Map<String, Object> params) {
        List<TbClassification> list = super.queryList(params);
        List<TbClassification> myList = new ArrayList<TbClassification>();
        for (int i = 0; i < list.size(); i++) {
            TbClassification tc = list.get(i);
            int count = tbClassificationMapper.queryArticleCount(tc.getId().toString());
            tc.setArticleNum(count);
            myList.add(tc);
        }

        return myList;
    }
}
