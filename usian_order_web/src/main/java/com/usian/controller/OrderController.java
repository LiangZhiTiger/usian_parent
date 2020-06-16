package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frontend/order")
public class OrderController {

    @Autowired
    private CartServiceFeign cartServiceFeign;

    /**
     * 展示订单页面
     * @param ids
     * @param userId
     * @return
     */
    @RequestMapping("/goSettlement")
    public Result goSettlement(String[] ids,String userId){
        List<TbItem> list = new ArrayList<>();
        Map<String, TbItem> cart = cartServiceFeign.getCartFromRodis(userId);
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            TbItem tbItem = cart.get(id);
            list.add(tbItem);
        }
        if (list.size()>0){
            return Result.ok(list);
        }
        return Result.error("结算失败");
    }
}
