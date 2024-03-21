package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.result.PageResult;
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
}
