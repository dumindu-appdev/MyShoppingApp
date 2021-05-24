package com.example.myshoppingapp;

public class OrderDetails {
    private String details_id;
    private String prod_id;
    private String prod_size;
    private float prod_price;
    private int prod_qty;

    public OrderDetails() {
    }

    public String getDetails_id() {
        return details_id;
    }

    public void setDetails_id(String details_id) {
        this.details_id = details_id;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
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

    public String getProd_size() {
        return prod_size;
    }

    public void setProd_size(String prod_size) {
        this.prod_size = prod_size;
    }
}
