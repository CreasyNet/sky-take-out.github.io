package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 保存菜品
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**、
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids
     */
    void deleteWithDishflavor(List<Long> ids);

    /**
     * 根据id查菜品
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 根据id修改菜品
     * @param dishDTO
     */
    void updateByIdWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    List<Dish> getByCategoryId(Long id);

    /**
     * 起售或者禁售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
