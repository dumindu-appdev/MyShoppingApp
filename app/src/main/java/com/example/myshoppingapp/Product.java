package com.example.myshoppingapp;

public class Product {
    private String prod_id;
    private String prod_name;
    private String prod_cate;
    private String prod_scat;
    private String prod_desc;
    private String prod_image;
    private float prod_price;

    public Product() {
    }


    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_cate() {
        return prod_cate;
    }

    public void setProd_cate(String prod_cate) {
        this.prod_cate = prod_cate;
    }

    public String getProd_scat() {
        return prod_scat;
    }

    public void setProd_scat(String prod_scat) {
        this.prod_scat = prod_scat;
    }

    public String getProd_desc() {
        return prod_desc;
    }

    public void setProd_desc(String prod_desc) {
        this.prod_desc = prod_desc;
    }

    public String getProd_image() {
        return prod_image;
    }

    public void setProd_image(String prod_image) {
        this.prod_image = prod_image;
    }

    public float getProd_price() {
        return prod_price;
    }

    public void setProd_price(float prod_price) {
        this.prod_price = prod_price;
    }
}
