package com.example.softwarecocinero.views.administrator.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.UserCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.administrator.profile.AdministratorProfileActivity;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdministratorSettingsActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
    */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;

    /*
     *
     * Layout's components
     *
    */

    private LinearLayout btnAdministratorLogout;
    private RelativeLayout btnBack;
    private LinearLayout btnShowAdministratorProfile;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_settings_activity );

        btnAdministratorLogout = ( LinearLayout )findViewById( R.id.btnAdministratorLogout );
        btnAdministratorLogout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ){
                logout();
            }
        });
        userMdl =  new UserMdl( getApplicationContext() );
        btnBack = ( RelativeLayout )findViewById( R.id.btnBack );
        btnBack.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                onBackPressed();
                overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
            }

        } );
        btnShowAdministratorProfile = ( LinearLayout )findViewById( R.id.btnShowAdministratorProfile );
        btnShowAdministratorProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showProfilePanel();
            }

        } );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
    }

    private void logout(){
        /* Variables */
        UserSrv user;
        /* Code start */
        user = userMdl.select();
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        UserCtrl userCtrl = retrofit.create( UserCtrl.class );
        Call<Void> call = userCtrl.logout( user.getId() );
        call.enqueue( new Callback<Void>(){

            @Override
            public void onResponse( Call<Void> call, Response<Void> response ){
                /* Variables */
                int responseCode;
                Headers headers;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                if( responseCode == 1 || responseCode == 3 ){
                    userMdl.delete();
                    showMainPanel();
                }
            }

            @Override
            public void onFailure( Call<Void> call, Throwable t ){

            }

        } );

    }

    private void showMainPanel(){
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showProfilePanel(){
        Intent intent = new Intent( this, AdministratorProfileActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

}