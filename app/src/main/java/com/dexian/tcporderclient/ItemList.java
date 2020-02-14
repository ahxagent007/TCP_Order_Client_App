package com.dexian.tcporderclient;

public class ItemList {

    private String ItemName;
    private int ItemPrice;

    public ItemList(String itemName, int itemPrice) {
        ItemName = itemName;
        ItemPrice = itemPrice;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(int itemPrice) {
        ItemPrice = itemPrice;
    }
}
