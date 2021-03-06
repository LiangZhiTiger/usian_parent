package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @RequestMapping("/selectCartByUserId")
    public Map<String, TbItem> selectCartByUserId(String userId){
        return cartService.selectCartByUserId(userId);
    }

    @RequestMapping("/addClientRedis")
    public Boolean addClientRedis(String userId, @RequestBody Map<String,TbItem> cart){
        return cartService.addClientRedis(userId,cart);
    }
}
