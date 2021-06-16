package com.example.softwarecocinero.views.cashier.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.UserCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.MainActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CashierProfileActivity extends AppCompatActivity {

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

    private RelativeLayout btnBack;
    private EditText txtUpdateUserName;
    private EditText txtUpdateUserPassword;
    private Button btnUpdateUser;
    private Dialog progress;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.cashier_profile_activity );

        txtUpdateUserName = ( EditText )findViewById( R.id.txtUpdateUserName );
        txtUpdateUserPassword = ( EditText )findViewById( R.id.txtUpdateUserPassword );
        btnBack = ( RelativeLayout )findViewById( R.id.btnBack );
        btnBack.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                onBackPressed();
                overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
            }

        } );
        btnUpdateUser = ( Button )findViewById( R.id.btnUpdateUser );
        btnUpdateUser.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                updateUser();
            }

        } );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        fillUser();
    }

    private void fillUser(){
        txtUpdateUserName.setText( user.getName() );
    }

    private void closeSession(){
        userMdl.delete();
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    public void showProgress(){
        progress = new Dialog( this );
        progress.setContentView( R.layout.progress );
        progress.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ));
        progress.show();
    }

    private boolean validateFieldsUpdate(){
        /* Variables */
        boolean response;
        /* Code start */
        response =  true;
        if( txtUpdateUserName.getText().toString().isEmpty() ){
            response = false;
        }
        return response;
    }

    private void updateUser(){
        /* Variables */
        String name;
        String password;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            name = txtUpdateUserName.getText().toString();
            user.setName( name );
            if( !txtUpdateUserPassword.getText().toString().isEmpty() ){
                password = txtUpdateUserPassword.getText().toString();
                password = enconde( password );
                user.setPassword( password );
            }
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            UserCtrl userCtrl = retrofit.create( UserCtrl.class );
            Call<Void> call = userCtrl.update( user, user.getId(), user.getToken(), user.getId() );
            call.enqueue( new Callback<Void>() {

                @Override
                public void onResponse( Call<Void> call, Response<Void> response ){
                    /* Variables */
                    int responseCode;
                    Headers headers;
                    /* Code start */
                    headers = response.headers();
                    responseCode = Integer.parseInt( headers.get( "response" ) );
                    progress.dismiss();
                    switch( responseCode ) {
                        case 1:
                            Toast.makeText( getApplicationContext(), "El usuario se actualizó correctamente", Toast.LENGTH_LONG ).show();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El usuario ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "El área a la cual pretende actualizar el usuario ya no existe", Toast.LENGTH_LONG ).show();
                            break;
                        case 5:
                            Toast.makeText( getApplicationContext(), "El usuario no puede tener un rol como administrador", Toast.LENGTH_LONG ).show();
                            break;
                        case 6:
                            closeSession();
                            break;
                        case 8:
                            Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesaarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
                            closeSession();
                            break;
                    }
                }

                @Override
                public void onFailure( Call<Void> call, Throwable t ){

                }

            } );
        }else{
            Toast.makeText(getApplicationContext(), "Por favor ingresar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private static String enconde( String password ){
        String response = "";
        try{
            MessageDigest encryption = MessageDigest.getInstance( "SHA-256" );
            byte[] encryptedMessage = encryption.digest( password.getBytes() );
            StringBuilder builder = new StringBuilder();
            for( int i = 0; i < encryptedMessage.length; i++ ){
                String tmp = Integer.toHexString( encryptedMessage[i] & 0xff );
                if( tmp.length() == 1 ){
                    builder.append( '0' ).append( tmp );
                }else{
                    builder.append( tmp );
                }
            }
            response = builder.toString();
        }catch( NoSuchAlgorithmException ex ){
            System.out.println( "Error, the system could not do the encryption" );
        }
        return response;
    }
}