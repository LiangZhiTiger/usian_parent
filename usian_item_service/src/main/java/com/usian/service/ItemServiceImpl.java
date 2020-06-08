package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisClient redisClient;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${BASE}")
    private String BASE;

    @Value("${DESC}")
    private String DESC;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Integer ITEM_INFO_EXPIRE;

    @Value("${SETNX_BASC_LOCK_KEY}")
    private String SETNX_BASC_LOCK_KEY;

//    @Override
//    public TbItem selectItemInfo(Long itemId){
//        return tbItemMapper.selectByPrimaryKey(itemId);
//    }

    @Override
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);

        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated desc");

        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo((byte)1);

        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        PageInfo<TbItem> pageInfo = new PageInfo<>(tbItemList);

        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(page);
        pageResult.setTotalPage((int)pageInfo.getTotal());
        pageResult.setResult(tbItemList);

        return pageResult;
    }

    @Override
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
        //补齐tbitem的数据
        long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setStatus((byte)1);
        tbItem.setId(itemId);
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        Integer tbitemNum = tbItemMapper.insertSelective(tbItem);

        //补齐商品描述的数据
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        Integer tbitemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);

        //补全商品规格的数据
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setParamData(itemParams);
        Integer itemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        //添加商品发布消息到mq
        amqpTemplate.convertAndSend("item_exchage","item.add", itemId);

        return tbitemNum+tbitemDescNum+itemParamItemNum;
    }

    @Override
    public Map<String,Object> preUpdateItem(Long itemId) {
        Map<String,Object> map = new HashMap<>();

        //查询商品基本信息
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item",item);

        //查询商品所属类目
        TbItemCat itemCat = this.tbItemCatMapper.selectByPrimaryKey(item.getCid());
        map.put("itemCat", itemCat.getName());

        //查询商品介绍
        TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc",itemDesc.getItemDesc());

        //查询商品规格参数
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria tbItemParamItemExampleCriteria = tbItemParamItemExample.createCriteria();
        tbItemParamItemExampleCriteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        if (tbItemParamItems.size()>0 && tbItemParamItems!=null){
            map.put("itemParamItem",tbItemParamItems.get(0).getParamData());
        }

        return map;
    }

    @Override
    public Integer deleteItemById(Long itemId) {
        Integer num = tbItemMapper.deleteByPrimaryKey(itemId);
        //Redis缓存同步
        redisClient.del(ITEM_INFO + ":" + itemId + ":" + BASE);
        redisClient.del(ITEM_INFO + ":" + itemId + ":" + DESC);
        redisClient.del(ITEM_INFO + ":" + itemId + ":" + PARAM);

        return num;
    }

    @Override
    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        //补齐商品数据
        Long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setStatus((byte)1);
        tbItem.setUpdated(date);
        int updateItem = tbItemMapper.updateByPrimaryKeySelective(tbItem);

        //补齐商品描述
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(tbItem.getId());
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setUpdated(date);
        int updateDesc = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);

        //补齐商品规格参数
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria tbItemParamItemExampleCriteria = tbItemParamItemExample.createCriteria();
        tbItemParamItemExampleCriteria.andItemIdEqualTo(tbItem.getId());
        List<TbItemParamItem> tbpa = tbItemParamItemMapper.selectByExample(tbItemParamItemExample);

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setId(tbpa.get(0).getId());
        tbItemParamItem.setItemId(tbItem.getId());
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(date);
        int updateParam = tbItemParamItemMapper.updateByPrimaryKeySelective(tbItemParamItem);

        //Redis缓存同步
        redisClient.del(ITEM_INFO + ":" + itemId + ":" + BASE);
        redisClient.del(ITEM_INFO + ":" + itemId + ":" + DESC);
        redisClient.del(ITEM_INFO + ":" + itemId + ":" + PARAM);


        return updateItem+updateDesc+updateParam;
    }

    @Override
    public TbItem selectItemInfo(Long itemId) {
        //先从Redis缓存中查询
        TbItem tbItem = (TbItem) redisClient.get(ITEM_INFO + ":" + itemId + ":" + BASE);
        if (tbItem!=null){
            return tbItem;
        }
        //Redis缓存中没有，则查询数据库再存在Redis缓存
        /*****************解决缓存击穿***************/
        if (redisClient.setnx(SETNX_BASC_LOCK_KEY+":"+itemId,itemId,30L)){
            tbItem = tbItemMapper.selectByPrimaryKey(itemId);
            /*****************解决缓存穿透*****************/
            if (tbItem!=null){
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + BASE, tbItem);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + BASE,ITEM_INFO_EXPIRE);
            }else{
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + BASE,null);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + BASE,30L);
            }
            redisClient.del(SETNX_BASC_LOCK_KEY+":"+itemId);
            return tbItem;
        }else{
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return selectItemInfo(itemId);
        }
    }

    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {
        //先从Redis缓存中查询
        TbItemDesc tbItemDesc = (TbItemDesc) redisClient.get(ITEM_INFO + ":" + itemId + ":" + DESC);
        if (tbItemDesc!=null){
            return tbItemDesc;
        }
        //Redis缓存中没有，则查询数据库再存在Redis缓存中
        /*****************解决缓存击穿***************/
        if (redisClient.setnx(SETNX_BASC_LOCK_KEY+":"+itemId,itemId,30L)){
            TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
            /*****************解决缓存穿透*****************/
            if (itemDesc!=null){
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + DESC, itemDesc);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + DESC,ITEM_INFO_EXPIRE);
            }else {
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + DESC, null);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + DESC,30L);
            }
            redisClient.del(SETNX_BASC_LOCK_KEY+":"+itemId);
            return itemDesc;
        }else {
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return selectItemDescByItemId(itemId);
        }
    }


}
