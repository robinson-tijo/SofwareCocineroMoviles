package com.example.softwarecocinero.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.administrator.product.AdministratorProductActivity;
import com.example.softwarecocinero.views.administrator.settings.AdministratorSettingsActivity;
import com.example.softwarecocinero.views.administrator.area.AdministratorAreaActivity;
import com.example.softwarecocinero.views.administrator.table.AdministratorTableActivity;
import com.example.softwarecocinero.views.administrator.user.AdministratorUserActivity;

public class AdministratorMenuActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
    */

    private UserSrv user;

    /*
     *
     * Layout's components
     *
    */

    private LinearLayout btnShowAdministratorSettings;
    private LinearLayout btnShowAdministratorTable;
    private LinearLayout btnShowAdministratorArea;
    private LinearLayout btnShowAdministratorUser;
    private LinearLayout btnShowAdministratorProduct;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_menu_activity);

        btnShowAdministratorSettings = ( LinearLayout )findViewById( R.id.btnShowAdministratorSettings );
        btnShowAdministratorSettings.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                showAdministratorSettings();
            }

        } );
        btnShowAdministratorTable = ( LinearLayout )findViewById( R.id.btnShowAdministratorTable );
        btnShowAdministratorTable.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                showAdministratorTable();
            }

        } );
        btnShowAdministratorArea = ( LinearLayout )findViewById( R.id.btnShowAdministratorArea );
        btnShowAdministratorArea.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                showAdministratorArea();
            }
            
        } );
        btnShowAdministratorProduct = ( LinearLayout )findViewById( R.id.btnShowAdministratorProduct );
        btnShowAdministratorProduct.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                showAdministratorProduct();
            }

        } );
        btnShowAdministratorUser = ( LinearLayout )findViewById( R.id.btnShowAdministratorUser );
        btnShowAdministratorUser.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                showAdministratorUser();
            }
            
        });
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
    }

    private void showAdministratorSettings(){
        Intent intent = new Intent( this, AdministratorSettingsActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showAdministratorTable(){
        Intent intent = new Intent( this, AdministratorTableActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showAdministratorArea(){
        Intent intent = new Intent( this, AdministratorAreaActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showAdministratorProduct(){
        Intent intent = new Intent( this, AdministratorProductActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showAdministratorUser(){
        Intent intent = new Intent( this, AdministratorUserActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }
}