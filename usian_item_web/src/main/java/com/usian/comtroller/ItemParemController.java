package com.usian.comtroller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItemParam;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
