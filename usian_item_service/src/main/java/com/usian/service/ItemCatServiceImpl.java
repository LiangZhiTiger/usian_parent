package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.redis.RedisClient;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Value("${PROTAL_CATRESULT_KEY}")
    private String PROTAL_CATRESULT_KEY;

    /**
     * 根据id查询商品的类目
     * @param id
     * @return
     */
    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {

        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andStatusEqualTo(1);
        criteria.andParentIdEqualTo(id);

        return tbItemCatMapper.selectByExample(tbItemCatExample);
    }

    @Override
    public CatResult selectItemCategoryAll() {
        CatResult catResultRedis = (CatResult) redisClient.get(PROTAL_CATRESULT_KEY);
        if (catResultRedis!=null){
            return catResultRedis;
        }
        CatResult catResult = new CatResult();
        catResult.setData(getCatList(0L));
        redisClient.set(PROTAL_CATRESULT_KEY,catResult);
        return catResult;
    }

    private List<?> getCatList(Long parentId) {
        TbItemCatExample itemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = itemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> itemCatList = tbItemCatMapper.selectByExample(itemCatExample);
        List list = new ArrayList();
        int count = 0;
        for (int i=0;i<itemCatList.size();i++){
            TbItemCat tbItemCat = itemCatList.get(i);
            if (tbItemCat.getIsParent()){
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatList(tbItemCat.getId()));
                list.add(catNode);
                count=count+1;
                if (count==18){
                    break;
                }
            }else {
                list.add(tbItemCat.getName());
            }
        }
        return list;
    }
}
