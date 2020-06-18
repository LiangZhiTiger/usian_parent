package com.usian.service;

import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbOrderItemMapper;
import com.usian.mapper.TbOrderMapper;
import com.usian.mapper.TbOrderShippingMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServicelmpl implements OrderService {

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Long ORDER_ID_BEGIN;

    @Value("${ORDER_ITEM_ID_KEY}")
    private String ORDER_ITEM_ID_KEY;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public String insertOrder(OrderInfo orderInfo) {
        //解析orderInfo
        TbOrder tbOrder = orderInfo.getTbOrder();
        TbOrderShipping tbOrderShipping = orderInfo.getOrderShipping();
        List<TbOrderItem> tbOrderItemList = JsonUtils.jsonToList(orderInfo.getOrderItem(),TbOrderItem.class);

        //补全并保存订单信息
        if (!redisClient.exists(ORDER_ID_KEY)){
            redisClient.set(ORDER_ID_KEY,ORDER_ID_BEGIN);
        }
        Long orderId = redisClient.incr(ORDER_ID_KEY, 1L);
        tbOrder.setOrderId(orderId.toString());
        Date date = new Date();
        tbOrder.setCreateTime(date);
        tbOrder.setUpdateTime(date);
        tbOrder.setStatus(1);
        tbOrderMapper.insertSelective(tbOrder);

        //补全并保存物流信息
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShipping.setCreated(date);
        tbOrderShipping.setUpdated(date);
        tbOrderShippingMapper.insertSelective(tbOrderShipping);

        //补全并保存明细信息/购买的商品列表信息
        if (!redisClient.exists(ORDER_ITEM_ID_KEY)){
            redisClient.set(ORDER_ITEM_ID_KEY,0);
        }
        for (int i = 0; i < tbOrderItemList.size(); i++) {
            Long orderItemId = redisClient.incr(ORDER_ITEM_ID_KEY, 1L);
            TbOrderItem tbOrderItem = tbOrderItemList.get(i);
            tbOrderItem.setId(orderItemId.toString());
            tbOrderItem.setOrderId(orderId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }
        //订单页面结算成功以后发送请求扣除库存
        amqpTemplate.convertAndSend("order_exchage","order.add",orderId);
        return orderId.toString();
    }

    @Override
    public List<TbOrder> selectOverTimeTbOrder() {
        return tbOrderMapper.selectOverTimeTbOrder();
    }

    /**
     * 关闭超时订单
     * @param tbOrder
     */
    @Override
    public void updateOverTimeTbOrder(TbOrder tbOrder) {
        tbOrder.setStatus(6);
        Date date = new Date();
        tbOrder.setUpdateTime(date);
        tbOrder.setEndTime(date);
        tbOrder.setCloseTime(date);
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }

    /**
     * 订单关闭之后修改库存
     * @param tbOrder
     */
    @Override
    public void updateTbItemByOrderId(TbOrder tbOrder) {
        TbOrderItemExample example = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(tbOrder.getOrderId());
        List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example);
        for (TbOrderItem tbOrderItem:tbOrderItems) {
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum()+tbOrderItem.getNum());
            tbItem.setUpdated(new Date());
            tbItemMapper.updateByPrimaryKey(tbItem);
        }
    }
}
