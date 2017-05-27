package com.loozb.web;

import com.loozb.core.base.AbstractController;
import com.loozb.core.support.Assert;
import com.loozb.core.util.ParamUtil;
import com.loozb.model.TbClassification;
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
 * 文章分类 前端控制器
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-25
 */
@RestController
@Api(value = "文章分类管理", description = "文章分类管理")
public class TbClassificationController extends AbstractController<IBlogProvider> {
    @Override
    public String getService() {
        return "tbClassificationService";
    }
    // 查询文章类型
    @ApiOperation(value = "查询文章类型，默认查询20条")
    @GetMapping("/anon/classifications")
    public Object query(ModelMap modelMap,
                        @ApiParam(required = false, value = "起始页") @RequestParam(defaultValue = "1", value = "current") String current,
                        @ApiParam(required = false, value = "查询页数") @RequestParam(defaultValue = "20", value = "size") String size,
                        @ApiParam(required = false, value = "需要排序字段") @RequestParam(defaultValue = "id", value = "orderBy") String orderBy,
                        @ApiParam(required = false, value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword) {
        return super.query(modelMap,  ParamUtil.getPageParams(current, size, keyword, orderBy));
    }

    // 查询所有文章类型，顺便把其有多少文章也查出来
    @ApiOperation(value = "查询文章类型列表")
    @GetMapping("/anon/all/classifications")
    public Object queryList(ModelMap modelMap, String keyword) {
        Map<String, Object> params = ParamUtil.getMap();
        params.put("keyword", keyword);
        return super.queryList(modelMap, params);
    }

    @ApiOperation(value = "查询文章类型")
    @GetMapping("/anon/classifications/{id}")
    public Object queryById(ModelMap modelMap, @PathVariable Long id) {
        return super.get(modelMap, id);
    }

    /**
     * 创建创建文章类型
     * @param modelMap
     * @param param
     * @return
     */
    @PostMapping("/classifications")
    @ApiOperation(value = "创建文章类型")
    @RequiresPermissions("classification:create")
    public Object create(ModelMap modelMap, TbClassification param) {
        return super.update(modelMap, param);
    }

    /**
     * 更新文章
     * @param modelMap
     * @param param
     * @return
     */
    @PutMapping("/classifications")
    @ApiOperation(value = "更新文章类型")
    @RequiresPermissions("classification:update")
    public Object update(ModelMap modelMap, TbClassification param) {
        Assert.notNull(param, "classification");
        Assert.notNull(param.getId(), "ID");
        return super.update(modelMap, param);
    }

    /**
     * 删除文章
     * @param modelMap
     * @param id
     * @return
     */
    @DeleteMapping("/classifications/{id}")
    @ApiOperation(value = "删除文章类型")
    @RequiresPermissions("classification:remove")
    public Object remove(ModelMap modelMap, @PathVariable Long id) {
        return super.del(modelMap, id);
    }
}
