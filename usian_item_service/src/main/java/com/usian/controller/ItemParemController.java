package com.usian.controller;

import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamItem;
import com.usian.service.ItemParamService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/itemParam")
public class ItemParemController {

    @Autowired
    private ItemParamService itemParamService;

    /**
     * 根据类目Id查询商品参数
     * @param itemCatId
     * @return
     */
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemParamByItemCatId(@PathVariable Long itemCatId){
        TbItemParam tbItemParam = itemParamService.selectItemParamByItemCatId(itemCatId);
        return tbItemParam;
    }

    /**
     * 规格参数查询
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/selectItemParamAll")
    public PageResult selectItemParamAll(Integer page,Integer rows){
        return itemParamService.selectItemParamAll(page,rows);
    }

    /**
     * 添加类目规格模板
     * @param itemCatId
     * @param paramData
     * @return
     */
    @RequestMapping("/insertItemParam")
    public Integer insertItemParam(Long itemCatId,String paramData){
        return itemParamService.insertItemParam(itemCatId,paramData);
    }

    /**
     * 删除类目规格模板
     * @param id
     * @return
     */
    @RequestMapping("/deleteItemParamById")
    public Integer deleteItemParamById(Long id){
        return itemParamService.deleteItemParamById(id);
    }

    /**
     * 根据商品Id查询商品规格参数
     * @param itemId
     * @return
     */
    @RequestMapping("/selectTbItemParamItemByItemId")
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId){
        return itemParamService.selectTbItemParamItemByItemId(itemId);
    }
}
