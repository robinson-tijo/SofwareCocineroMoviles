package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AreaSrv {

    @SerializedName( "area_id" )
    @Expose
    private int id;

    @SerializedName( "area_name" )
    @Expose
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AreaSrv() {

    }
}
