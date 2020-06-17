package com.usian.pojo;

import java.io.Serializable;

public class OrderInfo implements Serializable {
    private String orderItem;
    private TbOrder tbOrder;
    private TbOrderShipping OrderShipping;

    public String getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public TbOrderShipping getOrderShipping() {
        return OrderShipping;
    }

    public void setOrderShipping(TbOrderShipping orderShipping) {
        OrderShipping = orderShipping;
    }
}
