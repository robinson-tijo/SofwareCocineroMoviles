package com.example.softwarecocinero.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.softwarecocinero.services.UserSrv;

public class UserMdl {

    Sqlite_Helper database_connection;

    public UserMdl( Context context ) {
        // Here the database is created
        database_connection = new Sqlite_Helper( context, "restaurant_db", null, 2 );
    }

    public void insert( UserSrv user ){
        // Open database
        SQLiteDatabase database = database_connection.getWritableDatabase();
        // I get user's data
        ContentValues values =  new ContentValues();
        values.put( "user_id", user.getId() );
        values.put( "user_name", user.getName() );
        values.put( "user_state", user.getState() );
        values.put( "user_token", user.getToken() );
        values.put( "user_rol", user.getRol() );
        int user_rol = user.getRol();
        if( user_rol == 3 ){
            values.put( "user_area", user.getArea() );
        }
        // We insert data
        long result = database.insert( "User", "user_id", values );
    }

    public UserSrv select(){
        UserSrv user = null;
        SQLiteDatabase database = database_connection.getReadableDatabase();
        Cursor cursor = database.rawQuery( "select * from User", null );
        if( cursor != null ) {
            boolean result = cursor.moveToFirst();
            if( result ){
                user =  new UserSrv();
                user.setId( cursor.getString( cursor.getColumnIndex( "user_id") ) );
                user.setName( cursor.getString( cursor.getColumnIndex( "user_name" ) ) );
                user.setState( cursor.getInt( cursor.getColumnIndex( "user_state") ) );
                user.setToken( cursor.getString( cursor.getColumnIndex( "user_token" ) ) );
                user.setRol( cursor.getInt( cursor.getColumnIndex( "user_rol" ) ) );
                if( user.getRol() == 3 ){
                    user.setArea( cursor.getInt( cursor.getColumnIndex( "user_area" ) ) );
                }
            }
        }
        return user;
    }

    public void delete(){
        // Open database
        SQLiteDatabase database = database_connection.getWritableDatabase();
        database.delete( "User", null, null );
    }

}
