package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")//分页查询

    public Result<OrderSubmitVO> submit(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索,{}", ordersPageQueryDTO);

//        PageResult orderService.conditionSearch(ordersPageQueryDTO);
//        return Result.success(orderSubmitVO);
        return null;
    }
}