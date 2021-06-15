package com.example.softwarecocinero.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.UserCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.UserSrv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    /*
    *
    * Global variables
    *
    */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;

    /*
     *
     * Layout's components
     *
    */

    private Button btnSend;
    private EditText txtUserPassword;
    private EditText txtUserId;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main_activity );

        txtUserPassword = ( EditText )findViewById( R.id.txtUserPassword );
        txtUserId = ( EditText )findViewById( R.id.txtUserId );
        btnSend = ( Button )findViewById( R.id.btnSend );
        btnSend.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ){
                login();
            }
        });
        userMdl =  new UserMdl( getApplicationContext() );
    }

    @Override
    public void onBackPressed(){
        /* Variables */
        UserSrv user;
        /* Code start */
        user = userMdl.select();
        if( user != null ){
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        redirect();
    }

    private void redirect(){
        /* Variables */
        int rol;
        UserSrv user;
        /* Code start */
        user = userMdl.select();
        if( user != null ){
            rol = user.getRol();
            switch( rol ){
                case 1:
                    showAdministrator( user );
                    break;
                case 2:
                    showCashier( user );
                    break;
                case 4:
                    showWaiter( user );
                    break;
            }
        }
    }

    private void login(){
        /* Variables */
        String id;
        String password;
        UserSrv user;
        /* Code start */
        if( txtUserId.getText().toString().isEmpty() || txtUserPassword.getText().toString().isEmpty() ) {
            Toast.makeText(getApplicationContext(), "Por favor ingresar todos los campos", Toast.LENGTH_SHORT).show();
        }else{
            user = new UserSrv();
            id = txtUserId.getText().toString();
            password = enconde( txtUserPassword.getText().toString() );
            user.setId( id );
            user.setPassword( password );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            UserCtrl userCtrl = retrofit.create( UserCtrl.class );
            Call<UserSrv> call = userCtrl.login( user );
            call.enqueue( new Callback<UserSrv>() {

                @Override
                public void onResponse( Call<UserSrv> call, Response<UserSrv> response ){
                    /* Variables */
                    Headers headers;
                    int responseCode;
                    UserSrv user;
                    /* Code start */
                    headers = response.headers();
                    responseCode = Integer.parseInt( headers.get( "response" ) );
                    switch( responseCode ){
                        case 1:
                            user = response.body();
                            userMdl.insert( user );
                            redirect();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El usuario no se encuentra registrado", Toast.LENGTH_LONG ).show();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "Los datos ingresados son incorrectos", Toast.LENGTH_LONG ).show();
                            break;
                        case 5:
                            Toast.makeText( getApplicationContext(), "Este usuario ha sido bloqueado por exceder los 3 intentos permitidos", Toast.LENGTH_LONG ).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<UserSrv> call, Throwable t) {

                }

            } );
        }
    }

    private void showAdministrator( UserSrv user ){
        Intent intent = new Intent( this, AdministratorMenuActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showWaiter( UserSrv user ){
        Intent intent = new Intent( this, WaiterMenuActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void showCashier( UserSrv user ){
        Intent intent = new Intent( this, CashierMenuActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
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