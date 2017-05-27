package com.loozb.web;

import com.loozb.core.base.AbstractController;
import com.loozb.core.base.Parameter;
import com.loozb.core.support.Assert;
import com.loozb.core.util.ParamUtil;
import com.loozb.model.SysUser;
import com.loozb.model.TbArticle;
import com.loozb.provider.IBlogProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 博客列表 前端控制器
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-22
 */
@RestController
@Api(value = "文章管理", description = "文章管理")
@RequestMapping
public class TbArticleController extends AbstractController<IBlogProvider> {
    @Override
    public String getService() {
        return "tbArticleService";
    }
    // 查询文章列表，前端查询
    @ApiOperation(value = "查询文章列表，默认查询20条")
    @GetMapping("/anon/articles")
    public Object query(ModelMap modelMap,
                        @ApiParam(required = false, value = "起始页") @RequestParam(defaultValue = "1", value = "current") String current,
                        @ApiParam(required = false, value = "查询页数") @RequestParam(defaultValue = "10", value = "size") String size,
                        @ApiParam(required = false, value = "需要排序字段") @RequestParam(defaultValue = "id", value = "orderBy") String orderBy,
                        @ApiParam(required = false, value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword,
                        @ApiParam(required = false, value = "查询分类") @RequestParam(value = "classification", required = false) String classification) {
        Map<String, Object> params =  ParamUtil.getPageParams(current, size, keyword, orderBy);
        params.put("confirm", "1");
        params.put("classification", classification);
        return super.query(modelMap, params);
    }

    // 查询文章列表，后端查询，需要权限
    @ApiOperation(value = "查询文章列表，默认查询20条")
    @GetMapping("/articles")
    @RequiresPermissions("article:view")
    public Object queryCsl(ModelMap modelMap,
                        @ApiParam(required = false, value = "起始页") @RequestParam(defaultValue = "1", value = "current") String current,
                        @ApiParam(required = false, value = "查询页数") @RequestParam(defaultValue = "20", value = "size") String size,
                        @ApiParam(required = false, value = "需要排序字段") @RequestParam(defaultValue = "id", value = "orderBy") String orderBy,
                        @ApiParam(required = false, value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword) {
        return super.query(modelMap,  ParamUtil.getPageParams(current, size, keyword, orderBy));
    }

    /**
     * 查询文章详情，因特殊，需要返回上一篇和下一篇的标题和ID
     * @param modelMap
     * @param id
     * @return
     */
    @ApiOperation(value = "查询文章详情")
    @GetMapping("/anon/articles/{id}")
    public Object queryById(ModelMap modelMap, @PathVariable Long id) {
        return super.get(modelMap, id);
    }

    /**
     * 创建创建文章
     * @param modelMap
     * @param param
     * @return
     */
    @PostMapping("/articles")
    @ApiOperation(value = "创建文章信息")
    @RequiresPermissions("article:create")
    public Object create(ModelMap modelMap, TbArticle param) {
        if(param.getType().equals("1")) {
            param.setAuthor("龙小川");
        }
        param.setContent(param.getContent().replaceAll("\\$", "\\\\\\$"));
        return super.update(modelMap, param);
    }

    /**
     * 更新文章
     * @param modelMap
     * @param param
     * @return
     */
    @PutMapping("/articles")
    @ApiOperation(value = "更新文章信息")
    @RequiresPermissions("article:update")
    public Object update(ModelMap modelMap, TbArticle param) {
        Assert.notNull(param, "ARTICLE");
        Assert.notNull(param.getId(), "ID");
        return super.update(modelMap, param);
    }

    /**
     * 删除文章
     * @param modelMap
     * @param id
     * @return
     */
    @DeleteMapping("/articles/{id}")
    @ApiOperation(value = "删除文章信息")
    @RequiresPermissions("article:remove")
    public Object remove(ModelMap modelMap, @PathVariable Long id) {
        return super.del(modelMap, id);
    }

    /**
     * 确定文章
     * @param modelMap
     * @param id
     * @return
     */
    @PutMapping("/articles/confirm/{id}")
    @ApiOperation(value = "确定文章信息")
    @RequiresPermissions("article:update")
    public Object confirm(ModelMap modelMap, @PathVariable Long id) {
        Parameter parameter = new Parameter(getService(), "queryById").setId(id);
        TbArticle article = (TbArticle)provider.execute(parameter).getModel();
        article.setConfirm(article.getConfirm().equals("1") ? "0" : "1");
        return super.update(modelMap, article);
    }

    /**
     * 置顶文章
     * @param modelMap
     * @param id
     * @return
     */
    @PutMapping("/top/articles/{id}")
    @ApiOperation(value = "置顶文章信息")
    @RequiresPermissions("article:update")
    public Object top(ModelMap modelMap, @PathVariable Long id) {
        Parameter parameter = new Parameter(getService(), "queryById").setId(id);
        TbArticle article = (TbArticle)provider.execute(parameter).getModel();
        article.setSort(article.getSort() == 1 ? 0 : 1);
        return super.update(modelMap, article);
    }

}
