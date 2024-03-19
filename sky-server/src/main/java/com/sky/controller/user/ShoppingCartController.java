package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;

import com.sky.entity.ShoppingCart;
import com.sky.result.Result;

import com.sky.service.ShoppingCartService;
import com.sky.service.impl.ShoppingCartServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@Slf4j
@RequestMapping("/user/shoppingCart")
@RestController
@Api(tags = "C端-购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param dishDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result save(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加菜品到购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
        log.info("查看购物车");
        List<ShoppingCart> shoppingCarts = shoppingCartService.showShoppingCart();
        return Result.success(shoppingCarts);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result clean(){
        log.info("清空购物车");
        shoppingCartService.delete();
        return Result.success();
    }

}
