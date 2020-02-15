package com.dexian.tcporderclient;

import java.util.List;

public class InfoData {

    private List<OrderList> orderList;
    private List<ItemList> itemList;

    public InfoData(List<OrderList> orderList, List<ItemList> itemList) {
        this.orderList = orderList;
        this.itemList = itemList;
    }

    public List<OrderList> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderList> orderList) {
        this.orderList = orderList;
    }

    public List<ItemList> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemList> itemList) {
        this.itemList = itemList;
    }
}
