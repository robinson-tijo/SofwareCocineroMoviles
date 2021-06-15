package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.ProductSrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductCtrl {

    @GET( "product/category/{id}" )
    Call<List<ProductSrv>> select( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @GET( "product/name/{name}" )
    Call<List<ProductSrv>> selectByName( @Path( "name" ) String name, @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "product/{id}" )
    Call<Void> update( @Body ProductSrv product, @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "product" )
    Call<Void> insert( @Body ProductSrv product, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "product/{id}" )
    Call<Void> delete( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
