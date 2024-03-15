package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    /**
     * 根据菜品的id查询关联的套餐的id
     * @param dishIds
     * @return
     */
    List<Long> queryByDishId(List<Long> dishIds);
}
