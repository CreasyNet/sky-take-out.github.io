package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file)  {
        log.info("文件上传：{}",file);
        //AliOssUtil aliOssUtil = new AliOssUtil();
        //使用UUID构建新的文件名称
        //截取文件前缀
        String filePath = null;
        try {
            String src = file.getOriginalFilename();
            String substringSrc = src.substring(src.lastIndexOf("."));
            UUID uuid = UUID.randomUUID();

            filePath = aliOssUtil.upload(file.getBytes(), uuid.toString()+substringSrc);
            return Result.success(filePath);
        } catch (IOException e) {
           log.error("文件上传失败！");
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
