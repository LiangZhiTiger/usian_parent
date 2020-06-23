package com.usian.mq;

import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQSender implements ReturnCallback, ConfirmCallback {

    @Autowired
    private LocalMessageMapper localMessageMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息并得到响应
     * @param localMessage
     */
    public void sendMsg(LocalMessage localMessage) {
        RabbitTemplate rabbitTemplate = (RabbitTemplate) this.amqpTemplate;
        //允许回调
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);//成功后回调
        rabbitTemplate.setReturnCallback(this);//失败后回调

        //用于确认之后更改本地消息状态或删除本地消息--本地消息id
        CorrelationData correlationData = new CorrelationData(localMessage.getTxNo());//本地消息Id
        rabbitTemplate.convertAndSend("order_exchage","order.add", JsonUtils.objectToJson(localMessage),correlationData);
    }

    /**
     * 成功发消息之后确认回调
     */
     public void confirm(CorrelationData correlationData, boolean ack, String cause) {
         if (ack){
             //消息发送成功以后更新本地消息为一或者删除本地消息
             String txNo = correlationData.getId();
             LocalMessage localMessage = new LocalMessage();
             localMessage.setTxNo(txNo);
             localMessage.setState(1);
             localMessageMapper.updateByPrimaryKeySelective(localMessage);
         }
     }

    /**
     * 失败之后回调
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("return--message:" + new String(message.getBody()) + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }
}
