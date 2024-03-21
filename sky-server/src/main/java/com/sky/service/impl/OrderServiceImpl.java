package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //特殊异常的捕获处理，购物车为空和地址普为空时不能下单
        List<ShoppingCart> shoppingCartList = shoppingCartService.showShoppingCart();
        if (shoppingCartList == null){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        AddressBook addressBook = addressBookService.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        //思路分析：
        //1.先创建order表对应的对象
        Orders order = new Orders();
        //2.属性拷贝
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        //3.补充剩余属性
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());
        //4.插入order表
        orderMapper.insert(order);
        //5.订单的详细信息批量插入orderDetail表信息
        //使用集合存放然后批量插入数据库
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetails.add(orderDetail);
        }
        //批量插入订单明细到数据库
        orderDetailMapper.insertBatch(orderDetails);
        //6.封装VO对象并返回
        OrderSubmitVO submitVO = OrderSubmitVO.builder()
                .id(userId)
                .orderTime(order.getOrderTime())
                .orderAmount(order.getAmount())
                .orderNumber(order.getNumber())
                .build();

        return submitVO;
    }

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult listOrder(int page, int pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> pageQuery = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (pageQuery != null && pageQuery.getTotal() > 0) {
            for (Orders orders : pageQuery) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(pageQuery.getTotal(), list);
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    public OrderVO orderDetail(Long orderId){
        Orders order = orderMapper.getById(orderId);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);

        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 取消订单
     * @param orderId
     */
    public void orderCancel(Long orderId) {
      /*  - 待支付和待接单状态下，用户可直接取消订单
                - 商家已接单状态下，用户取消订单需电话沟通商家
                - 派送中状态下，用户取消订单需电话沟通商家
                - 如果在待接单状态下取消订单，需要给用户退款
                - 取消订单后需要将订单状态修改为“已取消”*/
        Orders order = orderMapper.getById(orderId);
        Integer status = order.getStatus();

        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            //支付状态修改为 退款
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(orderId);

    }
}
