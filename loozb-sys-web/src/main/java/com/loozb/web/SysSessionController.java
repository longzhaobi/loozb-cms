package com.loozb.web;

import com.loozb.core.base.AbstractController;
import com.loozb.core.util.ParamUtil;
import com.loozb.core.util.WebUtil;
import com.loozb.model.SysUser;
import com.loozb.provider.ISysProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户会话管理
 * </p>
 *
 * @author 龙召碧
 * @since 2017-02-26
 */
@RestController
@Api(value = "会话管理", description = "会话管理")
@RequestMapping(value = "/session")
public class SysSessionController extends AbstractController<ISysProvider> {

    @Override
    public String getService() {
        return "sysSessionService";
    }

    // 查询会话
    @ApiOperation(value = "查询会话")
    @GetMapping
    @RequiresPermissions("session:view")
    public Object query(ModelMap modelMap,
                      @ApiParam(required = false, value = "起始页") @RequestParam(defaultValue = "1", value = "current") String current,
                      @ApiParam(required = false, value = "查询页数") @RequestParam(defaultValue = "20", value = "size") String size,
                      @ApiParam(required = false, value = "需要排序字段") @RequestParam(defaultValue = "id", value = "orderBy") String orderBy,
                      @ApiParam(required = false, value = "查询关键字") @RequestParam(value = "keyword", required = false) String keyword) {
        Integer number = WebUtil.getAllUserNumber();
        Map<String, Object> param = ParamUtil.getPageParams(current, size, keyword, orderBy);
        modelMap.put("userNumber", number); // 用户数大于会话数,有用户没有登录
        return super.query(modelMap, param);
    }

    @DeleteMapping("/{token}")
    @ApiOperation(value = "删除会话")
    @RequiresPermissions("session:remove")
    public Object delete(ModelMap modelMap, @PathVariable String token) {
        SysUser user = WebUtil.getUserByToken(token);
        WebUtil.clear(token, user.getId());
        return setSuccessModelMap(modelMap);
    }
}
