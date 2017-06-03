package com.loozb.service;

import com.loozb.core.base.BaseService;
import com.loozb.model.TbArticle;
import com.loozb.model.TbClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客列表 服务类
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-22
 */
@Service
@CacheConfig(cacheNames = "TbArticle")
public class TbArticleService extends BaseService<TbArticle> {
    @Autowired
    private TbClassificationService tbClassificationService;

    @Override
    public TbArticle queryById(Long id) {
        TbArticle article = super.queryById(id);
        if(article != null) {
            TbClassification classification = tbClassificationService.queryById(Long.parseLong(article.getClassification()));
            if(classification != null) {
                article.setClassificationName(classification.getName());
            }
        }
        return article;
    }
}
