package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();
        String openid = getOpenid(code);
        if (openid == null){
            throw  new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断用户是否为新用户
        User user = userMapper.queryByOpenId(openid);
        if (user != null) {
            return user;
        }

        User userNew = User.builder()
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(userNew);//后面可以通过用户中心完善用户信息
        return userNew;
    }

    /**
     * 根据code获取用户opid
     * @param code
     * @return
     */
    private  String getOpenid(String code) {
        //获取openid
        Map<String, String> param = new HashMap<>();
        param.put("appid", weChatProperties.getAppid());
        param.put("secret", weChatProperties.getSecret());
        param.put("js_code", code);
        param.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, param);

        JSONObject jsonObj = (JSONObject) JSON.parse(json);
        String openid = (String) jsonObj.get("openid");
        return openid;
    }
}
