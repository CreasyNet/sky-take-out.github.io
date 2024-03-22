package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User queryByOpenId(String openid);

    void insert(User userNew);

    @Select("select * from user where id = #{id}")
    User getById(Long id);
}
