package com.usian.controller;

import com.usian.pojo.TbItemCat;
import com.usian.service.ItemCatService;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/itemCat")
public class ItemCatController {

    @Autowired
    private ItemCatService itemCatService;

    /**
     * 根据Id查询商品类目
     * @param id
     * @return
     */
    @RequestMapping("selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(Long id){
       return itemCatService.selectItemCategoryByParentId(id);
    }

    /**
     * 商品类目查询
     * @return
     */
    @RequestMapping("/selectItemCategoryAll")
    public CatResult selectItemCategoryAll(){
        return itemCatService.selectItemCategoryAll();
    }
}
