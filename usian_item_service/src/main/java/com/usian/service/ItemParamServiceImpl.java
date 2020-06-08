package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImpl implements ItemParamService {

    @Autowired
    private TbItemParamMapper tbItemParamMapper;
    
    @Autowired
    private TbItemParamItemMapper  tbItemParamItemMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Integer ITEM_INFO_EXPIRE;

    @Value("${SETNX_BASC_LOCK_KEY}")
    private String SETNX_BASC_LOCK_KEY;

    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {

        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);

        return tbItemParamList.get(0);
    }

    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        TbItemParamExample example = new TbItemParamExample();
        example.setOrderByClause("updated DESC");
        List<TbItemParam> itemParamList = tbItemParamMapper.selectByExampleWithBLOBs(example);
        PageInfo<TbItemParam> pageInfo = new PageInfo<>(itemParamList);
        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(page);
        pageResult.setTotalPage(pageInfo.getPages());
        pageResult.setResult(pageInfo.getList());
        return pageResult;
    }

    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {
        TbItemParamExample itemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = itemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExample(itemParamExample);
        if (tbItemParamList.size()>0){
            return 0;
        }

        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setItemCatId(itemCatId);
        tbItemParam.setParamData(paramData);
        Date date = new Date();
        tbItemParam.setCreated(date);
        tbItemParam.setUpdated(date);
        return tbItemParamMapper.insertSelective(tbItemParam);
    }

    @Override
    public Integer deleteItemParamById(Long id) {
        return tbItemParamMapper.deleteByPrimaryKey(id);
    }

    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        //先从Redis缓存中查询
        TbItemParamItem tbItemParamItem = (TbItemParamItem) redisClient.get(ITEM_INFO + ":" + itemId + ":" + PARAM);
        if (tbItemParamItem!=null){
            return tbItemParamItem;
        }
        //Redis缓存中没有，则查询数据库再存在Redis缓存中
        if (redisClient.setnx(SETNX_BASC_LOCK_KEY+":"+itemId,itemId,30L)){
            TbItemParamItemExample example = new TbItemParamItemExample();
            TbItemParamItemExample.Criteria criteria = example.createCriteria();
            criteria.andItemIdEqualTo(itemId);
            List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
            /*****************解决缓存穿透*****************/
            if (tbItemParamItems!=null&&tbItemParamItems.size()>0){
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + PARAM, tbItemParamItems.get(0));
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + PARAM,ITEM_INFO_EXPIRE);
            }else {
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + PARAM, null);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + PARAM,30L);
            }
            redisClient.del(SETNX_BASC_LOCK_KEY+":"+itemId);
            return tbItemParamItems.get(0);
        }else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectTbItemParamItemByItemId(itemId);
        }
    }
}
