package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品的id查询关联的套餐的id
     * @param dishIds
     * @return
     */
    List<Long> queryByDishId(List<Long> dishIds);

    /**
     * 批量插入套餐的菜品
     * @param dishes
     */
    void insertBySeMealId(List<SetmealDish> dishes);

    /**
     * 批量删除关联菜品
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据套餐id查询关联菜品并封装集合返回
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> queryBySetmealId(Long id);
}
