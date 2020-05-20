package com.usian.comtroller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/itemParam")
public class ItemParemController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    /**
     *
     * 根据类目Id查找商品模板参数
     * @param itemCatId
     * @return
     */
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable Long itemCatId){
        TbItemParam tbItemParam = itemServiceFeignClient.selectItemParamByItemCatId(itemCatId);
        if(tbItemParam!=null){
            return Result.ok(tbItemParam);
        }
        return Result.error("查无结果");
    }

    /**
     * 规格参数查询
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/selectItemParamAll")
    public Result selectItemParamAll(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "20") Integer rows){
        PageResult pageResult = itemServiceFeignClient.selectItemParamAll(page,rows);
        if (pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }
}
