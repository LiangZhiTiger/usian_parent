package com.usian.comtroller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeignClient.selectItemInfo(itemId);
        if(tbItem != null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }

    /**
     * 分页展示商品
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "3") Integer rows){

        PageResult pageResult = itemServiceFeignClient.selectTbItemAllByPage(page,rows);

        if(pageResult.getResult() != null && pageResult.getResult().size() > 0){
            return Result.ok(pageResult);
        }

        return Result.error("查无结果");

    }

    /**
     * 添加商品
     * @param tbItem
     * @param desc
     * @param itemParams
     * @return
     */
    @RequestMapping("/insertTbItem")
    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer insertTbitemNum = itemServiceFeignClient.insertTbItem(tbItem,desc,itemParams);
        if(insertTbitemNum==3){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    //预更新数据
    @RequestMapping("/preUpdateItem")
    public Result preUpdateItem(Long itemId){
        Map<String,Object> map = itemServiceFeignClient.preUpdateItem(itemId);
        if(map!=null){
            return Result.ok(map);
        }
        return Result.error("没有查询到数据");
    }

    //删除
    @RequestMapping("deleteItemById")
    public Result deleteItemById(Long itemId){
        Integer num = itemServiceFeignClient.deleteItemById(itemId);

        if(num==1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
