package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        //封装用户的id
        Long id = BaseContext.getCurrentId();
        shoppingCart.setUserId(id);

        //查询数据库是否有添加的菜品或者套餐信息--使用动态Sql
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.query(shoppingCart);


        if (shoppingCarts != null && shoppingCarts.size()>0){
            ShoppingCart shoppingCartIn = shoppingCarts.get(0);
            shoppingCartIn.setNumber(shoppingCartIn.getNumber() + 1);
            shoppingCartMapper.update(shoppingCartIn);
            return;
        }

        //如果购物车没有商品，就重新封装购物车商品信息插入表结构中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        //判断菜品是否已经添加到购物车，如果有数据只需要修改菜品的数量
        if (dishId != null){
            //通过菜品表获得菜品信息然后再封装
            Dish dish = dishMapper.queryById(dishId);
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            //菜品的口味值已经传递过来了
        }else {//如果是套餐类型，修改套餐的信息再插入购物车表即可
            Setmeal setmeal = setmealMapper.queryById(setmealId);
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());

        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> showShoppingCart() {
        Long id = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .id(id)
                .build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.query(shoppingCart);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     */
    public void delete() {
        shoppingCartMapper.deleteAll();
    }
}
