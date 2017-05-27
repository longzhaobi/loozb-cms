package com.loozb.web;

import com.loozb.core.base.AbstractController;
import com.loozb.core.support.Assert;
import com.loozb.core.util.ParamUtil;
import com.loozb.model.TbMessage;
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
 * 留言 前端控制器
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-27
 */
@RestController
@Api(value = "留言管理", description = "留言管理")
public class TbMessageController extends AbstractController<IBlogProvider> {
    @Override
    public String getService() {
        return "tbMessageService";
    }
    // 查询留言列表，前端查询
    @ApiOperation(value = "查询留言列表，默认查询20条")
    @GetMapping("/anon/messages")
    public Object query(ModelMap modelMap,
                        @ApiParam(required = false, value = "起始页") @RequestParam(defaultValue = "1", value = "current") String current,
                        @ApiParam(required = false, value = "查询页数") @RequestParam(defaultValue = "5", value = "size") String size,
                        @ApiParam(required = false, value = "需要排序字段") @RequestParam(defaultValue = "id", value = "orderBy") String orderBy,
                        @ApiParam(required = false, value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword) {
        Map<String, Object> params =  ParamUtil.getPageParams(current, size, keyword, orderBy);
        return super.query(modelMap, params);
    }

    @ApiOperation(value = "查询留言详情")
    @GetMapping("/anon/messages/{id}")
    public Object queryById(ModelMap modelMap, @PathVariable Long id) {
        return super.get(modelMap, id);
    }

    /**
     * 创建创建留言
     * @param param
     * @return
     */
    @PostMapping("/anon/messages")
    @ApiOperation(value = "创建留言信息")
    public Object create(ModelMap modelMap, TbMessage param) {
        return super.update(modelMap, param);
    }

    /**
     * 更新留言
     * @param modelMap
     * @param param
     * @return
     */
    @PutMapping("/anon/messages")
    @ApiOperation(value = "更新留言信息")
    public Object update(ModelMap modelMap, TbMessage param) {
        Assert.notNull(param, "MESSAGE");
        Assert.notNull(param.getId(), "ID");
        return super.update(modelMap, param);
    }

    /**
     * 删除留言
     * @param modelMap
     * @param id
     * @return
     */
    @DeleteMapping("/anon/messages/{id}")
    @ApiOperation(value = "删除留言信息")
    @RequiresPermissions("article:remove")
    public Object remove(ModelMap modelMap, @PathVariable Long id) {
        return super.del(modelMap, id);
    }
}
