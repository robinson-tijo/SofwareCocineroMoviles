package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentSrv {

    @SerializedName( "pay_id" )
    @Expose
    private long id;

    @SerializedName( "pay_price" )
    @Expose
    private long price;

    @SerializedName( "pay_method" )
    @Expose
    private int method;

    @SerializedName( "pay_date" )
    @Expose
    private String date;

    @SerializedName( "pay_tip" )
    @Expose
    private long tip;

    @SerializedName( "pay_description" )
    @Expose
    private String description;

    @SerializedName( "ord_id" )
    @Expose
    private long order;

    @SerializedName( "cli_id" )
    @Expose
    private String client;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public long getTip() {
        return tip;
    }

    public void setTip(long tip) {
        this.tip = tip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentSrv() {

    }
}
