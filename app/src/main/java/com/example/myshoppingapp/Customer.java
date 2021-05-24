package com.example.myshoppingapp;

public class Customer {
    private String cust_id;
    private String cust_name;
    private  String cust_address;
    private String cust_email;
    private String cust_phone;

    public Customer() {
    }

    public Customer(String id, String name, String address, String email, String phone) {
        this.cust_id = id;
        this.cust_name = name;
        this.cust_address = address;
        this.cust_email = email;
        this.cust_phone = phone;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getCust_address() {
        return cust_address;
    }

    public void setCust_address(String cust_address) {
        this.cust_address = cust_address;
    }

    public String getCust_email() {
        return cust_email;
    }

    public void setCust_email(String cust_email) {
        this.cust_email = cust_email;
    }

    public String getCust_phone() {
        return cust_phone;
    }

    public void setCust_phone(String cust_phone) {
        this.cust_phone = cust_phone;
    }
}
