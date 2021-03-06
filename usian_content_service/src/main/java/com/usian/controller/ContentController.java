package com.usian.controller;

import com.usian.pojo.TbContent;
import com.usian.service.ContentService;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 查询分类内容具体数据
     * @param page
     * @param rows
     * @param categoryId
     * @return
     */
    @RequestMapping("/selectTbContentAllByCategoryId")
    public PageResult selectTbContentAllByCategoryId(Integer page,Integer rows,Long categoryId){
        return contentService.selectTbContentAllByCategoryId(page,rows,categoryId);
    }

    /**
     * 添加分类内容具体数据
     * @param tbContent
     * @return
     */
    @RequestMapping("/insertTbContent")
    public Integer insertTbContent(@RequestBody TbContent tbContent){
        return contentService.insertTbContent(tbContent);
    }

    /**
     * 删除分类内容具体数据
     * @param ids
     * @return
     */
    @RequestMapping("/deleteContentByIds")
    public Integer deleteContentByIds(Long ids){
        return contentService.deleteContentByIds(ids);
    }

    /**
     * 大广告展示查询
     * @return
     */
    @RequestMapping("/selectFrontendContentByAD")
    public List<AdNode> selectFrontendContentByAD(){
        return contentService.selectFrontendContentByAD();
    }
}
