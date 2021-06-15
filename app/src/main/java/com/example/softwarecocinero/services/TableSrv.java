package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TableSrv {

    @SerializedName( "tab_id" )
    @Expose
    private int id;

    @SerializedName( "tab_description" )
    @Expose
    private String description;

    @SerializedName( "tab_number_seats" )
    @Expose
    private int seatsNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeatsNumber() {
        return seatsNumber;
    }

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public TableSrv() {

    }
}
