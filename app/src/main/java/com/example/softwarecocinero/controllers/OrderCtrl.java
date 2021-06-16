package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.OrderSrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderCtrl {

    @GET( "order" )
    Call<List<OrderSrv>> select( @Header( "Token" ) String token, @Header( "User" ) String user );

    @GET( "order/user/{id}" )
    Call<List<OrderSrv>> selectByUser( @Path( "id" ) String id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @GET( "order/area/{id}" )
    Call<List<OrderSrv>> selectByArea( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "order/{id}" )
    Call<Void> update( @Body OrderSrv order, @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "order/{id}/area/{area_id}" )
    Call<Void> updateStateByArea( @Body OrderSrv order, @Path( "id" ) long id, @Path( "area_id" ) int areaId, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "order" )
    Call<Void> insert( @Body OrderSrv order, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "order/{id}" )
    Call<Void> delete( @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
