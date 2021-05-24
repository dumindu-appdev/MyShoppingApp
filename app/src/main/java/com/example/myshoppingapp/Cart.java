package com.example.myshoppingapp;

public class Cart {
    private String cart_id;
    private String prod_id;
    private String cust_id;
    private String prod_name;
    private String prod_image;
    private float prod_price;
    private String prod_size;
    private int prod_qty;
    private String date_added;
    private String time_added;

    public Cart() {
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getTime_added() {
        return time_added;
    }

    public void setTime_added(String time_added) {
        this.time_added = time_added;
    }

    public float getProd_price() {
        return prod_price;
    }

    public void setProd_price(float prod_price) {
        this.prod_price = prod_price;
    }

    public int getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(int prod_qty) {
        this.prod_qty = prod_qty;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getProd_image() {
        return prod_image;
    }

    public void setProd_image(String prod_image) {
        this.prod_image = prod_image;
    }

    public String getProd_size() {
        return prod_size;
    }

    public void setProd_size(String prod_size) {
        this.prod_size = prod_size;
    }

}
