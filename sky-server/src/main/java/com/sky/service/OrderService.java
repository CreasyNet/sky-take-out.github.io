package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.User;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
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

     /**
      * 管理端接单
      * @param
      */
     public void receiveOrder(OrdersConfirmDTO ordersConfirmDTO);

     /**
      * 拒单
      * @param ordersRejectionDTO
      */
     void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

     /**
      * 取消
      * @param ordersCancelDTO
      */
     void cancel(OrdersCancelDTO ordersCancelDTO);

     /**
      * 派送
      * @param id
      */
     void delivery(Long id);

     /**
      * 完成订单
      * @param id
      */
     void complete(Long id);

     /**
      * 订单支付
      * @param ordersPaymentDTO
      * @return
      */
     OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

     /**
      * 支付成功，修改订单状态
      * @param outTradeNo
      */
     void paySuccess(String outTradeNo);
     /**
      * 用户催单
      * @param id
      */
     void reminder(Long id);
}
