package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 插入口味值
     * @param flavors
     */
    void insert(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除关联的口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void delete(Long dishId);

    void deleteByIds(List<Long> ids);

    /**
     *根据菜品id获取对应口位置
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id =#{id}")
    List<DishFlavor> getByDishId(Long id);
}
