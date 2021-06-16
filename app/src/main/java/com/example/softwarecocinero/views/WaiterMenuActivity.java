package com.example.softwarecocinero.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.administrator.settings.AdministratorSettingsActivity;
import com.example.softwarecocinero.views.waiter.order.WaiterOrderActivity;
import com.example.softwarecocinero.views.waiter.settings.WaiterSettingsActivity;

public class WaiterMenuActivity extends AppCompatActivity {

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

    private LinearLayout btnShowWaiterSettings;
    private LinearLayout btnShowWaiterOrder;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.waiter_menu_activity );

        btnShowWaiterSettings = ( LinearLayout )findViewById( R.id.btnShowWaiterSettings );
        btnShowWaiterSettings.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showWaiterSettings();
            }

        } );
        btnShowWaiterOrder = ( LinearLayout )findViewById( R.id.btnShowWaiterOrder );
        btnShowWaiterOrder.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showWaiterOrder();
            }

        } );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
    }

    private void showWaiterSettings(){
        Intent intent = new Intent( this, WaiterSettingsActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showWaiterOrder(){
        Intent intent = new Intent( this, WaiterOrderActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }
}