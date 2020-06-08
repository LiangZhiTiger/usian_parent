package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParam;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/detail/")
public class DetailController {
    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /**
     * 根据商品Id查询商品详情
     * @param itemId
     * @return
     */
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if (tbItem!=null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }

    /**
     * 根据商品Id查询商品描述
     * @param itemId
     * @return
     */
    @RequestMapping("/selectItemDescByItemId")
    public Result selectItemDescByItemId(Long itemId){
        TbItemDesc tbItemDesc = itemServiceFeign.selectItemDescByItemId(itemId);
        if (tbItemDesc!=null){
            return Result.ok(tbItemDesc);
        }
        return Result.error("查无结果");
    }

    /**
     * 根据商品Id查询商品规格参数
     * @param itemId
     * @return
     */
    @RequestMapping("/selectTbItemParamItemByItemId")
    public  Result selectTbItemParamItemByItemId(Long itemId){
        TbItemParam tbItemParam = itemServiceFeign.selectTbItemParamItemByItemId(itemId);
        if (tbItemParam!=null){
            return Result.ok(tbItemParam);
        }
        return Result.error("查无结果");
    }
}
