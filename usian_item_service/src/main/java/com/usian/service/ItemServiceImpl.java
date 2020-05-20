package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public TbItem selectItemInfo(Long itemId){
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

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

        return tbitemNum+tbitemDescNum+itemParamItemNum;
    }

    @Override
    public Map<String,Object> preUpdateItem(Long id) {

        Map<String,Object> map = new HashMap<>();

        TbItem tbItems = tbItemMapper.selectByPrimaryKey(id);

        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(id);

        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbItems.getCid());

        map.put("tbItems",tbItems);
        map.put("tbItemDesc",tbItemDesc);
        map.put("tbItemCat",tbItemCat);

        return map;



    }

    @Override
    public Integer deleteItemById(Long itemId) {

        Integer num = tbItemMapper.deleteByPrimaryKey(itemId);
        return num;
    }
}
