package com.usian.quartz;

import com.usian.pojo.TbOrder;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    /**
     * 结束超时订单
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("00000000000000000000000000000000000000000000000000000000");
        List<TbOrder> tbOrderList = orderService.selectOverTimeTbOrder();
        for (int i = 0; i < tbOrderList.size(); i++) {
            TbOrder tbOrder = tbOrderList.get(i);
            orderService.updateOverTimeTbOrder(tbOrder);
            orderService.updateTbItemByOrderId(tbOrder);
        }
    }
}
