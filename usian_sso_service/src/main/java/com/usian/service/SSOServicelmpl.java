package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SSOServicelmpl implements SSOService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public Boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        if (checkFlag==1){
            criteria.andUsernameEqualTo(checkValue);
        }else if (checkFlag==2){
            criteria.andPhoneEqualTo(checkValue);
        }
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if (tbUserExample==null && tbUsers.size()>0){
            return true;
        }
        return false;
    }
}
