package com.example.softwarecocinero.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.cashier.payment.CashierPaymentActivity;
import com.example.softwarecocinero.views.cashier.settings.CashierSettingsActivity;

public class CashierMenuActivity extends AppCompatActivity {

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

    private LinearLayout btnShowCashierSettings;
    private LinearLayout btnShowCashierPayment;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.cashier_menu_activity );

        btnShowCashierSettings = ( LinearLayout )findViewById( R.id.btnShowCashierSettings );
        btnShowCashierSettings.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showCashierSettings();
            }

        } );
        btnShowCashierPayment = ( LinearLayout )findViewById( R.id.btnShowCashierPayment );
        btnShowCashierPayment.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showCashierPayment();
            }

        } );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
    }

    private void showCashierSettings(){
        Intent intent = new Intent( this, CashierSettingsActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showCashierPayment(){
        Intent intent = new Intent( this, CashierPaymentActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }
}