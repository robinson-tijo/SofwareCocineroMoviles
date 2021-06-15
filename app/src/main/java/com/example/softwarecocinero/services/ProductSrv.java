package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductSrv {

    @SerializedName( "pro_id" )
    @Expose
    private int id;

    @SerializedName( "pro_name" )
    @Expose
    private String name;

    @SerializedName( "pro_price" )
    @Expose
    private long price;

    @SerializedName( "pro_image" )
    @Expose
    private String image;

    @SerializedName( "pro_cost" )
    @Expose
    private long cost;

    @SerializedName( "pro_preparation_time" )
    @Expose
    private int preparationTime;

    @SerializedName( "cat_id" )
    @Expose
    private int category;

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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public ProductSrv() {

    }
}
