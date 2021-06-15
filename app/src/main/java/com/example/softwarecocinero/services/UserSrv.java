package com.example.softwarecocinero.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserSrv implements Serializable {

    @SerializedName( "user_id" )
    @Expose
    private String id;

    @SerializedName( "area_id" )
    @Expose
    private int area;

    @SerializedName( "user_name" )
    @Expose
    private String name;

    @SerializedName( "user_rol" )
    @Expose
    private int rol;

    @SerializedName( "user_token" )
    @Expose
    private String token;

    @SerializedName( "user_password" )
    @Expose
    private String password;

    @SerializedName( "user_state" )
    @Expose
    private int state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public UserSrv() {

    }
}
