package com.usian.listener;

import com.rabbitmq.client.Channel;
import com.usian.pojo.DeDuplication;
import com.usian.pojo.LocalMessage;
import com.usian.service.DeDuplicationService;
import com.usian.service.ItemService;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ItemMQListener {
    @Autowired
    private ItemService itemService;

    @Autowired
    private DeDuplicationService deDuplicationService;

    /**
     * 接到请求扣除库存
     * @param msg
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "item_queue",durable = "true"),
            exchange = @Exchange(value = "order_exchage",type = ExchangeTypes.TOPIC),
            key = {"order.*"}
    ))
    public void listener(String msg, Channel channel, Message message) throws IOException {
        LocalMessage localMessage = JsonUtils.jsonToPojo(msg, LocalMessage.class);
        DeDuplication deDuplication = deDuplicationService.selectDeDuplicationByTxNo(localMessage.getTxNo());
        System.out.println(deDuplication);
        if (deDuplication==null){
            System.out.println("接收到消息"+localMessage.getOrderNo());
            Integer result = itemService.updateTbItemByOrderId(localMessage.getOrderNo());
            if (result<=0){
                throw new RuntimeException("库存扣减失败");
            }
            deDuplicationService.insertDeDuplication(localMessage.getTxNo());
        }else{
            System.out.println("=======幂等生效：事务"+deDuplication.getTxNo()+" 已成功执行===========");
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
