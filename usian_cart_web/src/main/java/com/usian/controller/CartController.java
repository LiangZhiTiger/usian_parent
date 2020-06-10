package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/frontend/cart")
public class CartController {

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    private Integer CART_COOKIE_EXPIRE;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /**
     * 添加商品到购物车
     * @param itemId
     * @param userId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/addItem")
    public Result addItem(Long itemId, String userId, @RequestParam(defaultValue = "1") Integer num, HttpServletRequest request, HttpServletResponse response){
        System.out.println(itemId);
        try {
            //判断是否登录
            if (StringUtils.isBlank(userId)){
                /***********在用户未登录的状态下**********/
                //从cookie查询商品列表
                Map<String, TbItem> cart = getCartFromCookie(request);
                //添加商品到购物车
                addItemToCart(cart,itemId,num);
                //把商品类别重新写入cookie
                addClientCookie(request,response,cart);
            }else{
                /***********在用户登录的状态下**********/
                Map<String,TbItem> cart = getCartFromRodis(userId);
                addItemToCart(cart,itemId,num);
                Boolean bool = cartServiceFeign.addClientRedis(userId,cart);
                if (bool){
                    return Result.ok();
                }
                return Result.error("添加失败");
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("添加失败");
        }

    }

    /**
     * 查看购物车
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/showCart")
    public Result showCart(String userId,HttpServletRequest request,HttpServletResponse response){
        try {
            //判断用户是否登录
            ArrayList<TbItem> tbItems = new ArrayList<>();
            if (StringUtils.isBlank(userId)){
                /**用户没有登录**/
                Map<String, TbItem> cart = getCartFromCookie(request);
                Set<String> keys =  cart.keySet();
                for (String key:keys) {
                    tbItems.add(cart.get(key));
                }
            }else{
                /**登录**/
            }
            return Result.ok(tbItems);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("error");
        }
    }

    /**
     * 修改购物车的商品
     * @param userId
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateItemNum")
    public Result updateItemNum(String userId,Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response){
        try {
            if (StringUtils.isBlank(userId)){
                /**未登录修改购物车商品**/
                Map<String, TbItem> map = getCartFromCookie(request);
                TbItem tbItem = map.get(itemId.toString());
                if(tbItem != null){
                    tbItem.setNum(num);
                }
                map.put(itemId.toString(),tbItem);
                addClientCookie(request,response,map);
            }else{
                /**登录修改购物车**/
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("修改失败");
        }
    }

    /**
     * 删除购物车的商品
     * @param itemId
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteItemFromCart")
    public Result deleteItemFromCart(Long itemId,String userId,HttpServletRequest request,HttpServletResponse response){
        try {
            if (StringUtils.isBlank(userId)){
                /**未登录删除购物车商品**/
                Map<String, TbItem> map = getCartFromCookie(request);
                map.remove(itemId.toString());
                addClientCookie(request,response,map);
            }else{
                /**登录**/
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("删除失败");
        }

    }

    /**
     * 用户登录后得到商品列表
     * @param userId
     * @return
     */
    private Map<String, TbItem> getCartFromRodis(String userId) {
        //从Redis缓存中查询
        Map<String,TbItem> map = cartServiceFeign.getCartFromRodis(userId);
        if (map!=null && map.size()>0){
            return map;
        }
        //缓存没有，new一个空的返回
        return new HashMap<String, TbItem>();
    }

    /**
     * 把商品列表重新写入cookie
     * @param request
     * @param response
     * @param cart
     */
    private void addClientCookie(HttpServletRequest request, HttpServletResponse response, Map<String, TbItem> cart) {
        String cartJson = JsonUtils.objectToJson(cart);
        CookieUtils.setCookie(request,response,CART_COOKIE_KEY,cartJson,CART_COOKIE_EXPIRE,true);
    }

    /**
     * 把商品添加购物车
     * @param cart
     * @param itemId
     * @param num
     */
    private void addItemToCart(Map<String, TbItem> cart, Long itemId, Integer num) {
        TbItem tbItem = cart.get(itemId.toString());
        //判断购物车内是否存在该商品
        if (tbItem!=null){
            //购物车存在该商品
            tbItem.setNum(tbItem.getNum()+num);
        }else {
            //购物车不存在商品
            tbItem = itemServiceFeign.selectItemInfo(itemId);
            tbItem.setNum(num);
        }
        //添加后再存入集合中
        cart.put(itemId.toString(),tbItem);
    }

    /**
     * 获取购物车
     * @param request
     * @return
     */
    private Map<String, TbItem> getCartFromCookie(HttpServletRequest request) {
        //获去购物车
        String cartJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);
        //判断是否存在购物车
        if (StringUtils.isNotBlank(cartJson)){
            //存在购物车
            Map<String,TbItem> map = JsonUtils.jsonToMap(cartJson, TbItem.class);
            return map;
        }
        //不存在创建一个空Map集合
        return new HashMap<String, TbItem>();
    }

    
}
