package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.CategorySrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryCtrl {

    @GET( "category/area/{id}" )
    Call<List<CategorySrv>> selectByCategory( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @GET( "category" )
    Call<List<CategorySrv>> select( @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "category/{id}" )
    Call<Void> update( @Body CategorySrv category, @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "category" )
    Call<Void> insert( @Body CategorySrv category, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "category/{id}" )
    Call<Void> delete( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
