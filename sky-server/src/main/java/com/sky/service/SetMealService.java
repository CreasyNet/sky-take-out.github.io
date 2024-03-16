package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {


    /**
     *新增套餐
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐和关联菜品
     * @param ids
     */
    void deleteWithDish(List<Long> ids);


    /**
     * 查询回显
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐和关联菜品
     * @param setmealDTO
     */
    void updateWithDishes(SetmealDTO setmealDTO);
}
