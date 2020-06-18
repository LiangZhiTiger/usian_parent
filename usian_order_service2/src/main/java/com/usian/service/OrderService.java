package com.usian.service;

import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;

import java.util.List;

public interface OrderService {
    public String insertOrder(OrderInfo orderInfo);

    List<TbOrder> selectOverTimeTbOrder();

    void updateOverTimeTbOrder(TbOrder tbOrder);

    void updateTbItemByOrderId(TbOrder tbOrder);
}
