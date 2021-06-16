package com.example.softwarecocinero.controllers;



import com.example.softwarecocinero.services.ChargeSrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChargeCtrl {

    @GET( "charge/order/{id}" )
    Call<List<ChargeSrv>> selectByOrder(@Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "charge/{id}" )
    Call<Void> update( @Body ChargeSrv charge, @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "charge" )
    Call<Void> insert( @Body ChargeSrv charge, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "charge/{id}" )
    Call<Void> delete( @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "charge/send/order/{id}" )
    Call<Void> send( @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
