package com.loozb.web;

import com.loozb.core.base.AbstractController;
import com.loozb.core.support.Assert;
import com.loozb.core.util.ParamUtil;
import com.loozb.model.TbComment;
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
 * 评论 前端控制器
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-27
 */
@RestController
@Api(value = "评论管理", description = "评论管理")
public class TbCommentController extends AbstractController<IBlogProvider> {
    @Override
    public String getService() {
        return "tbCommentService";
    }
    // 查询评论列表，前端查询
    @ApiOperation(value = "查询评论列表，默认查询20条")
    @GetMapping("/anon/comments")
    public Object query(ModelMap modelMap,
                        @ApiParam(required = false, value = "起始页") @RequestParam(defaultValue = "1", value = "current") String current,
                        @ApiParam(required = false, value = "查询页数") @RequestParam(defaultValue = "5", value = "size") String size,
                        @ApiParam(required = false, value = "需要排序字段") @RequestParam(defaultValue = "id", value = "orderBy") String orderBy,
                        @ApiParam(required = false, value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword,
                        @ApiParam(required = false, value = "查询分类") @RequestParam(value = "articleId", required = false) String articleId) {
        Map<String, Object> params =  ParamUtil.getPageParams(current, size, keyword, orderBy);
        params.put("articleId", articleId);
        return super.query(modelMap, params);
    }

    @ApiOperation(value = "查询评论详情")
    @GetMapping("/anon/comments/{id}")
    public Object queryById(ModelMap modelMap, @PathVariable Long id) {
        return super.get(modelMap, id);
    }

    /**
     * 创建创建评论
     * @param modelMap
     * @param param
     * @return
     */
    @PostMapping("/anon/comments")
    @ApiOperation(value = "创建评论信息")
    public Object create(ModelMap modelMap, TbComment param) {
        System.out.println(param);
        return super.update(modelMap, param);
    }

    /**
     * 更新评论
     * @param modelMap
     * @param param
     * @return
     */
    @PutMapping("/anon/comments")
    @ApiOperation(value = "更新评论信息")
    public Object update(ModelMap modelMap, TbComment param) {
        Assert.notNull(param, "ARTICLE");
        Assert.notNull(param.getId(), "ID");
        return super.update(modelMap, param);
    }

    /**
     * 删除评论
     * @param modelMap
     * @param id
     * @return
     */
    @DeleteMapping("/anon/comments/{id}")
    @ApiOperation(value = "删除评论信息")
    @RequiresPermissions("article:remove")
    public Object remove(ModelMap modelMap, @PathVariable Long id) {
        return super.del(modelMap, id);
    }
}
