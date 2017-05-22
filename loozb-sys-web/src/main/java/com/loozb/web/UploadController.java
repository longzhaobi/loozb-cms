package com.loozb.web;

import com.loozb.core.Constants;
import com.loozb.core.base.BaseController;
import com.loozb.core.util.FastDFSClient;
import com.loozb.model.PictureResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author： 龙召碧
 * @Date: Created in 2017-5-22 21:28
 */
@RestController
@Api(value = "文件上传接口", description = "文件上传接口")
@RequestMapping(value = "/upload", method = RequestMethod.POST)
public class UploadController extends BaseController {

    @RequestMapping("/images")
    @ApiOperation(value = "上传图片")
    public Object uploadImage(ModelMap modelMap, @RequestParam MultipartFile uploadFile) {
        PictureResult result = new PictureResult();
        //判断图片是否为空
        if (uploadFile.isEmpty()) {
            result.setError(1);
            result.setMessage("请选择图片");
            return result;
        }
        //上传到图片服务器
        try {
            //取图片扩展名
            String originalFilename = uploadFile.getOriginalFilename();
            //取扩展名不要“.”
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            FastDFSClient client = new FastDFSClient("classpath:client.conf");
            String url = client.uploadFile(uploadFile.getBytes(), extName);
            //把url响应给客户端
            result.setError(0);
            result.setUrl(Constants.IMAGE_SERVER_BASE_URL + url);
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(1);
            result.setMessage("图片上传失败");
        }
        return setSuccessModelMap(modelMap, result);
    }
}
