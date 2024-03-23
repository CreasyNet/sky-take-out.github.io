package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.sky.constant.MessageConstant;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 开始到结束时间段内每天的营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistic(LocalDate begin, LocalDate end) {
        //将日期放入集合，再转换成字符串
        ArrayList<LocalDate> list = new ArrayList<>();

        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            list.add(begin);
        }

        ArrayList<Double> totals = new ArrayList<>();
        //取出每天的营业额，开始的时间和结束的时间
        for (LocalDate localDate : list) {
            //select sum() from orders where orderTime > ？ and orderTime < ? and status = 5;
            LocalDateTime beginLocal = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endLocal = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("status", 5);
            map.put("begin", beginLocal);
            map.put("end", endLocal);
            Double turnOver = orderMapper.sumByMap(map);
            turnOver = turnOver == null ? 0.0 : turnOver;
            totals.add(turnOver);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(list, ","))
                .turnoverList(StringUtils.join(totals, ","))
                .build();
    }

    /**
     * 指定区间内的用户数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //返回日期的列表(字符串)
        ArrayList<LocalDate> localDates = new ArrayList<>();
        localDates.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            localDates.add(begin);
        }
        //求区间内每天总的用户数
        ArrayList<Long> totals = new ArrayList<>();
        ArrayList<Long> newUsers = new ArrayList<>();

        for (LocalDate localDate : localDates) {
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            Map map2 = new HashMap<>();
            map2.put("end", endTime);
            Long total = userMapper.countByDay(map2);
            totals.add(total);
            map2.put("begin", beginTime);
            Long newUser = userMapper.countByDay(map2);
            newUsers.add(newUser);
        }

        //求每天新增用户数
        return UserReportVO.builder()
                .dateList(StringUtils.join(localDates, ","))
                .totalUserList(StringUtils.join(totals, ","))
                .newUserList(StringUtils.join(newUsers, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        /**
         *     //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
         *     private String dateList;
         *
         *     //每日订单数，以逗号分隔，例如：260,210,215
         *     private String orderCountList;
         *
         *     //每日有效订单数，以逗号分隔，例如：20,21,10
         *     private String validOrderCountList;
         *
         *     //订单总数
         *     private Integer totalOrderCount;
         *
         *     //有效订单数
         *     private Integer validOrderCount;
         *
         *     //订单完成率
         *     private Double orderCompletionRate;
         */

//        LocalDateTime beginTime2 = LocalDateTime.of(begin, LocalTime.MIN);
//        LocalDateTime endTime2 = LocalDateTime.of(end, LocalTime.MAX);
//        Map map2 =  new HashMap<>();
//        map2.put("end",endTime2);
//        map2.put("begin",beginTime2);
//        Integer totalOrder = orderMapper.countByDay(map2);
//        map2.put("status",6);
//        Integer validOrders = orderMapper.countByDay(map2);
//        Double percent = validOrders*1.0 / totalOrder;


        ArrayList<LocalDate> localDates = new ArrayList<>();
        localDates.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            localDates.add(begin);
        }

        List<Integer> totals = new ArrayList<>();//记录每日订单数
        List<Integer> valids = new ArrayList<>();//每日有效订单数的集合

        for (LocalDate localDate : localDates) {
            //select count(id) from orders where  create_time > and create_time
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);

            Integer total = orderMapper.countByDay(getMap(beginTime, endTime, null));
            Integer validOrder = orderMapper.countByDay(getMap(beginTime, endTime,Orders.COMPLETED));
            totals.add(total);
            valids.add(validOrder);
        }

        Integer totalOrder = totals.stream().reduce(Integer::sum).get();
        Integer validOrders = valids.stream().reduce(Integer::sum).get();

        Double percent = 0.0;
        if (validOrders != 0) {
            percent = validOrders.doubleValue() / totalOrder;

        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(localDates, ","))
                .orderCountList(StringUtils.join(totals, ","))
                .validOrderCountList(StringUtils.join(valids, ","))
                .totalOrderCount(totalOrder)
                .validOrderCount(validOrders)
                .orderCompletionRate(percent)
                .build();
    }

    /**
     * 销量前10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        /**
         *   //商品名称列表，以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
         *     private String nameList;
         *
         *     //销量列表，以逗号分隔，例如：260,215,200
         *     private String numberList;
         */
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        List<GoodsSalesDTO> goodsSalesDTOS = orderDetailMapper.getSalesTop10(beginTime,endTime);
        List<String> names = goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> nums = goodsSalesDTOS.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
     /*   ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> nums = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOS) {
            names.add(goodsSalesDTO.getName());
            nums.add(goodsSalesDTO.getNumber());
        }*/

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names,","))
                .numberList(StringUtils.join(nums,","))
                .build();
    }

    private Map getMap(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap<>();
        map.put("end", end);
        map.put("begin", begin);
        map.put("status", status);
        return map;
    }
}
