package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询购物里的菜品或者套餐
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> query(ShoppingCart shoppingCart);


    /**
     * 插入菜品或者套餐
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 修改购物车数量
     * @param shoppingCartIn
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart shoppingCartIn);

    /**
     * 清空购物车
     */
    @Delete("delete from shopping_cart")
    void deleteAll();
}
