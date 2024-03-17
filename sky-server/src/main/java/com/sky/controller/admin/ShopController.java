package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/admin/shop")
@RestController
@Api(tags = "店铺操作接口")
public class ShopController {

    public static final String key = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("管理端设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("管理端设置店铺营业状态，{}", status == 1 ? "营业中" : "打烊");
        redisTemplate.opsForValue().set(key, status);
        return Result.success();
    }

    /**
     * 管理端获取状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus() {
        log.info("获取店铺状态");
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        return Result.success(status);
    }
}
