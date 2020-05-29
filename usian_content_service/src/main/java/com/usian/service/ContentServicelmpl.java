package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.redis.RedisClient;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentServicelmpl implements ContentService {

    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Value("${PORTAL_AD_KEY}")
    private String PORTAL_AD_KEY;

    @Autowired
    private TbContentMapper tbContentMapper;

    @Autowired
    private RedisClient redisClient;


    @Override
    public PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId) {
        PageHelper.startPage(page,rows);
        TbContentExample tbContentExample = new TbContentExample();
        tbContentExample.setOrderByClause("updated DESC");

        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> tbContentList = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);

        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContentList);
        PageResult pageResult = new PageResult();
        pageResult.setResult(tbContentList);
        pageResult.setPageIndex(page);
        pageResult.setTotalPage(pageInfo.getPages());
        return pageResult;
    }

    @Override
    public Integer insertTbContent(TbContent tbContent) {
        Date date = new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
        int num = tbContentMapper.insertSelective(tbContent);
        redisClient.hdel(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        return num;
    }

    @Override
    public Integer deleteContentByIds(Long ids) {
        int num = tbContentMapper.deleteByPrimaryKey(ids);
        redisClient.hdel(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        return num;
    }

    @Override
    public List<AdNode> selectFrontendContentByAD() {
        List<AdNode> adNodesRedis = (List<AdNode>)redisClient.hget(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        if (adNodesRedis!=null){
            return adNodesRedis;
        }
        TbContentExample contentExample = new TbContentExample();
        TbContentExample.Criteria criteria = contentExample.createCriteria();
        criteria.andCategoryIdEqualTo(AD_CATEGORY_ID);
        List<TbContent> tbContentList = tbContentMapper.selectByExample(contentExample);
        List<AdNode> adNodeList  = new ArrayList<>();
        for (TbContent content : tbContentList){
            AdNode adNode = new AdNode();
            adNode.setSrc(content.getPic());
            adNode.setSrcB(content.getPic2());
            adNode.setHref(content.getUrl());
            adNode.setHeight(AD_HEIGHT);
            adNode.setWidth(AD_WIDTH);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setWidthB(AD_WIDTHB);
            adNodeList.add(adNode);
        }
        redisClient.hset(PORTAL_AD_KEY,AD_CATEGORY_ID.toString(),adNodeList);
        return adNodeList;
    }
}
