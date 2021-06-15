package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.TableSrv;
import com.example.softwarecocinero.services.UserSrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TableCtrl {

    @GET( "table" )
    Call<List<TableSrv>> select( @Header( "Token" ) String token, @Header( "User" ) String user );

    @GET( "table/available" )
    Call<List<TableSrv>> selectAvailable( @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "table" )
    Call<Void> insert( @Body TableSrv table, @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "table/{id}" )
    Call<Void> update( @Body TableSrv table, @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "table/{id}" )
    Call<Void> delete( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
