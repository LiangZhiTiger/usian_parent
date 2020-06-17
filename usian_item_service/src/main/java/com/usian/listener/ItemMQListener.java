package com.usian.listener;

import com.usian.service.ItemService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemMQListener {
    @Autowired
    private ItemService itemService;

    /**
     * 接到请求扣除库存
     * @param orderId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "item_queue",durable = "true"),
            exchange = @Exchange(value = "order_exchage",type = ExchangeTypes.TOPIC),
            key = {"order.*"}
    ))
    public void listener(String orderId){
        System.out.println("接收到消息"+orderId);
        Integer result = itemService.updateTbItemByOrderId(orderId);
        if (result<=0){
            throw new RuntimeException("库存扣减失败");
        }
    }
}
