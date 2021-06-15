package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.AreaSrv;
import com.example.softwarecocinero.services.TableSrv;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AreaCtrl {

    @GET( "area" )
    Call<List<AreaSrv>> select( @Header( "Token" ) String token, @Header( "User" ) String user );

    @PUT( "area/{id}" )
    Call<Void> update( @Body AreaSrv area, @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "area" )
    Call<Void> insert( @Body AreaSrv area, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "area/{id}" )
    Call<Void> delete( @Path( "id" ) int id, @Header( "Token" ) String token, @Header( "User" ) String user );

}
