package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/user/shop")
@RestController("userShopController")
@Api(tags = "店铺操作接口")
public class ShopController {

    public static final String key = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 客户端获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("用户端获取营业状态")
    public Result<Integer> getStatus() {
        log.info("用户端获取店铺状态");
        Integer status = (Integer) redisTemplate.opsForValue().get(key);

        return Result.success(status);
    }
}
