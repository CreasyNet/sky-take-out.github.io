package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 开始到结束时间段内每天的营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistic(LocalDate begin, LocalDate end) {
        //将日期放入集合，再转换成字符串
        ArrayList<LocalDate> list = new ArrayList<>();

        while (!(begin.equals(end))){
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
            map.put("status",5);
            map.put("begin",beginLocal);
            map.put("end",endLocal);
            Double turnOver = orderMapper.sumByMap(map);
            turnOver = turnOver == null?0.0:turnOver;
            totals.add(turnOver);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(list,","))
                .turnoverList(StringUtils.join(totals,","))
                .build();
    }
}
