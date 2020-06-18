package com.usian.quartz;

import com.usian.pojo.TbOrder;
import com.usian.redis.RedisClient;
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
        if (redisClient.setnx("SETNX_LOCK_ORDER_KEY",ip,30L)){
            System.out.println("执行关闭超时订单："+new Date());
            List<TbOrder> tbOrderList = orderService.selectOverTimeTbOrder();
            for (int i = 0; i < tbOrderList.size(); i++) {
                TbOrder tbOrder = tbOrderList.get(i);
                orderService.updateOverTimeTbOrder(tbOrder);
                orderService.updateTbItemByOrderId(tbOrder);
            }
            redisClient.del("SETNX_LOCK_ORDER_KEY");
        }else {
            System.out.println("============机器："+ip+" 占用分布式锁，任务正在执行=======================");
        }

    }
}
