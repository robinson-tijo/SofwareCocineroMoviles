package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderSrv {

    @SerializedName( "ord_id" )
    @Expose
    private long id;

    @SerializedName( "ord_price" )
    @Expose
    private long price;

    @SerializedName( "ord_paid_price" )
    @Expose
    private long paidPrice;

    @SerializedName( "ord_state" )
    @Expose
    private int state;

    @SerializedName( "user" )
    @Expose
    private UserSrv user;

    @SerializedName( "table" )
    @Expose
    private TableSrv table;

    @SerializedName( "reservation" )
    @Expose
    private TableSrv reservation;

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

    public long getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(long paidPrice) {
        this.paidPrice = paidPrice;
    }

    public UserSrv getUser() {
        return user;
    }

    public void setUser(UserSrv user) {
        this.user = user;
    }

    public TableSrv getTable() {
        return table;
    }

    public void setTable(TableSrv table) {
        this.table = table;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public TableSrv getReservation() {
        return reservation;
    }

    public void setReservation(TableSrv reservation) {
        this.reservation = reservation;
    }

    public OrderSrv() {

    }

}
