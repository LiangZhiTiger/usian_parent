package com.usian.quartz;

import com.usian.mq.MQSender;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.TbOrder;
import com.usian.redis.RedisClient;
import com.usian.service.LocalMessageService;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private LocalMessageService localMessageService;

    @Autowired
    private MQSender mqSender;

    /**
     * 结束超时订单
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String ip =null;
        try {
             ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //解决quartz集群任务重复执行
        if (redisClient.setnx("SETNX_LOCK_ORDER_KEY"+ip,ip,30L)){
            System.out.println("执行关闭超时订单："+new Date());
            List<TbOrder> tbOrderList = orderService.selectOverTimeTbOrder();
            for (int i = 0; i < tbOrderList.size(); i++) {
                TbOrder tbOrder = tbOrderList.get(i);
                orderService.updateOverTimeTbOrder(tbOrder);
                orderService.updateTbItemByOrderId(tbOrder);
            }

            System.out.println("执行扫描本地消息表的任务...." + new Date());
            List<LocalMessage> localMessages = localMessageService.selectlocalMessageByStatus(0);
            for (int i = 0; i < localMessages.size(); i++) {
                LocalMessage localMessage = localMessages.get(i);
                mqSender.sendMsg(localMessage);
            }
            redisClient.del("SETNX_LOCK_ORDER_KEY"+ip);
        }else {
            System.out.println("============机器："+ip+" 占用分布式锁，任务正在执行=======================");
        }

    }
}
