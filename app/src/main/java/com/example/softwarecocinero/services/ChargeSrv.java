package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChargeSrv {

    @SerializedName( "cha_id" )
    @Expose
    private long id;

    @SerializedName( "cha_date" )
    @Expose
    private String date;

    @SerializedName( "cha_price" )
    @Expose
    private long price;

    @SerializedName( "cha_cost" )
    @Expose
    private long cost;

    @SerializedName( "cha_amount" )
    @Expose
    private int amount;

    @SerializedName( "cha_amount_paid" )
    @Expose
    private int amountPaid;

    @SerializedName( "cha_description" )
    @Expose
    private String description;

    @SerializedName( "cha_state" )
    @Expose
    private int state;

    @SerializedName( "ord_id" )
    @Expose
    private long order;

    @SerializedName( "product" )
    @Expose
    private ProductSrv product;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public ProductSrv getProduct() {
        return product;
    }

    public void setProduct(ProductSrv product) {
        this.product = product;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        this.amountPaid = amountPaid;
    }

    public ChargeSrv() {

    }
}
