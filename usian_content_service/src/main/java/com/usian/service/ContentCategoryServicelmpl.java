package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.NewThreadAction;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentCategoryServicelmpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(id);
        return tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
    }

    @Override
    public Integer insertContentCategory(TbContentCategory tbContentCategory) {
        Date date = new Date();
        tbContentCategory.setStatus(1);
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(date);
        tbContentCategory.setUpdated(date);
        Integer insertSelectiveNum = tbContentCategoryMapper.insertSelective(tbContentCategory);

        TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
        if (!contentCategory.getIsParent()){
            contentCategory.setIsParent(true);
            contentCategory.setUpdated(date);
            tbContentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        }
        return insertSelectiveNum;
    }

    @Override
    public Integer deleteContentCategoryById(Long categoryId) {
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        if (tbContentCategory.getIsParent()){
            return 0;
        }
        tbContentCategoryMapper.deleteByPrimaryKey(categoryId);

        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(tbContentCategory.getParentId());

        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        if (tbContentCategoryList.size()==0 || tbContentCategoryList==null){
            TbContentCategory contentCategory = new TbContentCategory();
            contentCategory.setId(tbContentCategory.getParentId());
            contentCategory.setIsParent(false);
            contentCategory.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        }
        return 200;
    }

    @Override
    public Integer updateContentCategory(TbContentCategory tbContentCategory) {
        tbContentCategory.setUpdated(new Date());
        return tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
    }


}
