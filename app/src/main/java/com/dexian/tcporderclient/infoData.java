package com.dexian.tcporderclient;

public class infoData {

    private OrderList orderList[];
    private ItemList itemList[];

    public infoData(OrderList[] orderList, ItemList[] itemList) {
        this.orderList = orderList;
        this.itemList = itemList;
    }

    public OrderList[] getOrderList() {
        return orderList;
    }

    public void setOrderList(OrderList[] orderList) {
        this.orderList = orderList;
    }

    public ItemList[] getItemList() {
        return itemList;
    }

    public void setItemList(ItemList[] itemList) {
        this.itemList = itemList;
    }
}
