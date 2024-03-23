package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 指定区间内的营业数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistic(LocalDate begin,LocalDate end);
}
