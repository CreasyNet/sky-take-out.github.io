package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增套餐
     * @param setmealDT1
     */
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        dishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBySeMealId(dishes);
    }


    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> setmealVOS =  setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(setmealVOS.getTotal(),setmealVOS.getResult());
    }

    /**
     * 删除套餐和关系表中的菜品
     * @param ids
     */
    public void deleteWithDish(List<Long> ids) {

        //套餐只有在起售的状态才能删除
        for (Long id : ids) {
             Setmeal setmeal = setmealMapper.queryById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //关联菜品在售状态下，不可删除套餐
        //思路分析：查询关联菜品表和菜品表，外键为dish_id,返回菜品集合，遍历集合，判断是否又套餐状态是在售
        //获取关联菜品表的的菜品id集合

        //删除套餐的同时要删除套餐菜品关系表内和套餐相关联的菜品
        setmealMapper.delete(ids);

        setmealDishMapper.delete(ids);
    }

    /**
     * 查询id套餐信息显示
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.queryById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);

        //获取关联的菜品集合
        List<SetmealDish> setmealDishes =  setmealDishMapper.queryBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐信息以及关联菜品
     * @param setmealDTO
     */
    @Override
    public void updateWithDishes(SetmealDTO setmealDTO) {
        //准备套餐信息和菜品信息

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //拷贝属性到setmeal对象
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.update(setmeal);

        //关联的菜品修改，先删除原来的关联菜品
        //获取套餐的关联id
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(setmealDTO.getId());
        setmealDishMapper.delete(longs);
        //插入更改后的关联的菜品
        setmealDishMapper.insertBySeMealId(setmealDishes);
    }


    /**
     * 起售的套餐可以展示在用户端，停售的套餐不能展示在用户端
     * 起售套餐时，如果套餐内包含停售的菜品，则不能起售
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = setmealMapper.queryById(id);
        setmeal.setStatus(status);
        //起售套餐时，如果套餐内包含停售的菜品，则不能起售
        List<Dish> dishes =  setmealDishMapper.queryDishesById(id);

        for (Dish dish : dishes) {
            if (dish.getStatus() == StatusConstant.DISABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        setmealMapper.update(setmeal);
    }
}
