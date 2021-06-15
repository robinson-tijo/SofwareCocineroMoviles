package com.example.softwarecocinero.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Sqlite_Helper extends SQLiteOpenHelper {

    final String USER_TABLE = "create table User(" +
            "user_id INTEGER PRIMARY KEY NOT NULL," +
            "user_name TEXT NOT NULL," +
            "user_rol INTEGER NOT NULL," +
            "user_token TEXT NOT NULL," +
            "user_state INTEGER NOT NULL," +
            "user_area INTEGER);";

    public Sqlite_Helper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( USER_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.delete( "Session", null, null );
        // onCreate( db );
    }
}

