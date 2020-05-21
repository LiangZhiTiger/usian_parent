package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-content-service")
public interface ContentServiceFeign {

    /**
     * 查询分类内容
     * @param id
     * @return
     */
    @RequestMapping("/service/contentCategory/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long id);

    /**
     * 添加分类内容
     * @param tbContentCategory
     * @return
     */
    @RequestMapping("/service/contentCategory/insertContentCategory")
    Integer insertContentCategory(TbContentCategory tbContentCategory);

    /**
     * 删除分类内容
     * @param categoryId
     * @return
     */
    @RequestMapping("/service/contentCategory/deleteContentCategoryById")
    Integer deleteContentCategoryById(@RequestParam Long categoryId);

    /**
     * 修改分类内容
     * @param tbContentCategory
     * @return
     */
    @RequestMapping("/service/contentCategory/updateContentCategory")
    Integer updateContentCategory(TbContentCategory tbContentCategory);
}
