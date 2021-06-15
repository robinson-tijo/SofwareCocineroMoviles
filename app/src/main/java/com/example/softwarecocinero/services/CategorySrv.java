package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategorySrv {

    @SerializedName( "cat_id" )
    @Expose
    private int id;

    @SerializedName( "cat_name" )
    @Expose
    private String name;

    @SerializedName( "area_id" )
    @Expose
    private int area;

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

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public CategorySrv() {

    }
}
