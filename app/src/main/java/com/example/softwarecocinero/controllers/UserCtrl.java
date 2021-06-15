package com.example.softwarecocinero.controllers;

import com.example.softwarecocinero.services.AreaSrv;
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

public interface UserCtrl {

    @GET( "user" )
    Call<List<UserSrv>> select( @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "user/login" )
    Call<UserSrv> login( @Body UserSrv user );

    @PUT( "user/logout/{id}" )
    Call<Void> logout( @Path( "id" ) String id );

    @PUT( "user/{id}" )
    Call<Void> update( @Body UserSrv userSrv, @Path( "id" ) String id, @Header( "Token" ) String token, @Header( "User" ) String user );

    @POST( "user" )
    Call<Void> insert( @Body UserSrv userSrv, @Header( "Token" ) String token, @Header( "User" ) String user );

    @DELETE( "user/{id}" )
    Call<Void> delete( @Path( "id" ) String id, @Header( "Token" ) String token, @Header( "User" ) String user );
}





