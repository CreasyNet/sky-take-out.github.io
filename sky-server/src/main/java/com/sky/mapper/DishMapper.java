package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 插入新菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品的分页查询
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param id
     */
    void delete(Long id);

    /**
     * 根据菜品id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id=#{id}")
    Dish queryById(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Dish dish);


    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    @Update("update dish set status = #{status} where id = #{id}")
    void updateStatusBid(Integer status, Long id);
}
