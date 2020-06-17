package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SSOServicelmpl implements SSOService {

    @Autowired
    private TbUserMapper tbUserMapper;
    @Autowired
    private RedisClient redisClient;

    @Value("${USER_INFO}")
    private String USER_INFO;

    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;

    @Override
    public Boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        //判断是用户名还是手机号校验
        if (checkFlag==1){
            //用户名校验
            criteria.andUsernameEqualTo(checkValue);
        }else if (checkFlag==2){
            //手机号校验
            criteria.andPhoneEqualTo(checkValue);
        }
        //搜索判断
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if (tbUsers==null || tbUsers.size()==0){
            return true;
        }
        return false;
    }

    @Override
    public Integer userRegister(TbUser tbUser) {
        //加密密码
        String digest = MD5Utils.digest(tbUser.getPassword());
        tbUser.setPassword(digest);
        //给出创建时间
        Date date = new Date();
        tbUser.setCreated(date);
        tbUser.setUpdated(date);
        return tbUserMapper.insertSelective(tbUser);
    }

    @Override
    public Map userLogin(String username, String password) {
        //加密密码
        String digest = MD5Utils.digest(password);
        //搜索判断是否有该账号
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(digest);
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if (tbUsers==null ||tbUsers.size()<=0){
            return null;
        }

        //如果存在存入Redis缓存中
        TbUser tbUser = tbUsers.get(0);
        //获取token
        String token = UUID.randomUUID().toString();
        tbUser.setPassword(null);
        redisClient.set(USER_INFO+":"+token,tbUser);
        redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);

        //返回用户信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("token",token);
        map.put("username",tbUser.getUsername());
        map.put("userid",tbUser.getId());
        return map;
    }

    @Override
    public TbUser getUserByToken(String token) {
        //通过token获取Redis账户信息
        TbUser tbUser = (TbUser) redisClient.get(USER_INFO + ":" + token);
        if (tbUser!=null){
            //每次登录需要重置登录时间
            redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);
            return tbUser;
        }
        return null;
    }

    @Override
    public Boolean logOut(String token) {
        return redisClient.del(USER_INFO + ":" + token);
    }
}
