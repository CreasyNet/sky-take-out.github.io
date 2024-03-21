package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

     OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

     /**
      * 历史订单查询
      * @param page
      * @param pageSize
      * @param status
      * @return
      */
     PageResult listOrder(int page, int pageSize, Integer status);

     /**
      * 订单详情
      * @param orderId
      * @return
      */
     OrderVO orderDetail(Long orderId);

     /**
      * 取消订单
      * @param orderId
      */
     void orderCancel(Long orderId);
     /**
      * 再来一单
      *
      * @param id
      */
     void repetition(Long id);

     /**
      * 管理端订单搜索
      * @param ordersPageQueryDTO
      * @return
      */
     PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
     /**
      * 各个状态的订单数量统计
      * @return
      */
     OrderStatisticsVO statistics();
}
