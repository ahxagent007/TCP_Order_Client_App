package com.dexian.tcporderclient;

public class OrderList {

    private String ItemName;
    private int ItemQuantity;
    private String TableNo;

    public OrderList() {
    }

    public OrderList(String itemName, int itemQuantity, String tableNo) {
        ItemName = itemName;
        ItemQuantity = itemQuantity;
        TableNo = tableNo;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getItemQuantity() {
        return ItemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        ItemQuantity = itemQuantity;
    }

    public String getTableNo() {
        return TableNo;
    }

    public void setTableNo(String tableNo) {
        TableNo = tableNo;
    }
}
