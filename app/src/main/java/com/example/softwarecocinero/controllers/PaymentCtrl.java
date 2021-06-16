package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.PaymentSrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PaymentCtrl {

    @GET( "payment/order/{id}" )
    Call<List<PaymentSrv>> selectByOrder( @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "payment/{id}" )
    Call<Void> update( @Body PaymentSrv payment, @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "payment" )
    Call<Void> insert( @Body PaymentSrv payment, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "payment/{id}" )
    Call<Void> delete( @Path( "id" ) long id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
