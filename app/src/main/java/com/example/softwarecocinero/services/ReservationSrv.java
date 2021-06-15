package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReservationSrv {

    @SerializedName( "res_id" )
    @Expose
    private long id;

    @SerializedName( "res_date" )
    @Expose
    private String date;

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

    public ReservationSrv() {
    }
}
