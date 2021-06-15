package com.example.softwarecocinero.views.administrator.table;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.TableCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.TableSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.AdministratorMenuActivity;
import com.example.softwarecocinero.views.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdministratorTableActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
    */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private AdministratorTableAdapter adapterTable;
    private PopupWindow popupWindow;
    private View view;
    private TableSrv table;
    private List<TableSrv> tables;

    /*
     *
     * Layout's components
     *
    */

    private RelativeLayout btnShowAdministratorMenu;
    private RecyclerView tblAdministratorTable;
    private LinearLayout btnDeleteTable;
    private FloatingActionButton btnShowPanelInsertTable;
    private Dialog progress;
    private Dialog dialog;
    private EditText txtInsertTableNumber;
    private EditText txtInsertTableSeats;
    private EditText txtInsertTableDescription;
    private Button btnInsertTable;
    private EditText txtUpdateTableSeats;
    private EditText txtUpdateTableDescription;
    private Button btnUpdateTable;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_table_activity );

        btnShowAdministratorMenu = findViewById( R.id.btnShowAdministratorMenu );
        btnShowAdministratorMenu.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showMenu();
            }

        });
        tblAdministratorTable = ( RecyclerView )findViewById( R.id.tblAdministratorTable );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        userMdl = new UserMdl( getApplicationContext() );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteTable = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeleteTable.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteTable();
            }

        } );
        btnShowPanelInsertTable = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertTable );
        btnShowPanelInsertTable.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertTable();
            }

        } );
        selectTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTable();
    }

    private void showMenu(){
        Intent intent = new Intent( this, AdministratorMenuActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void fillFields(){
        txtUpdateTableSeats.setText( "" + table.getSeatsNumber() );
        txtUpdateTableDescription.setText( table.getDescription() );
    }

    private void fillTable(){
        tblAdministratorTable.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterTable = new AdministratorTableAdapter( tables );
        adapterTable.setOnItemClickListener( new AdministratorTableAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                table = tables.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                table = tables.get( position );
                showPanelUpdateTable();
                fillFields();
            }

        } );
        tblAdministratorTable.setAdapter( adapterTable );
    }

    private void closeSession(){
        userMdl.delete();
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void selectTable(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        TableCtrl table = retrofit.create( TableCtrl.class );
        Call<List<TableSrv>> call = table.select( user.getToken(), user.getId() );
        call.enqueue( new Callback<List<TableSrv>>() {

            @Override
            public void onResponse( Call<List<TableSrv>> call, Response<List<TableSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        tables = response.body();
                        fillTable();
                        break;
                    case 2:
                        closeSession();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
                        closeSession();
                        break;
                }
            }

            @Override
            public void onFailure( Call<List<TableSrv>> call, Throwable t ){

            }

        });
    }

    private void deleteTable(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        TableCtrl tableCtrl = retrofit.create( TableCtrl.class );
        Call<Void> call = tableCtrl.delete( table.getId(),  user.getToken(), user.getId() );
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
                switch( responseCode ){
                    case 1:
                        Toast.makeText( getApplicationContext(), "La mesa se eliminó correctamente", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
                        selectTable();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        selectTable();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "La mesa se encuentra ocupada por lo que no es posible eliminarla", Toast.LENGTH_LONG ).show();
                        break;
                    case 5:
                        Toast.makeText( getApplicationContext(), "La mesa se encuentra reservada por lo que no es posible eliminarla", Toast.LENGTH_LONG ).show();
                        break;
                    case 6:
                        closeSession();
                        break;
                    case 8:
                        Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
                        closeSession();
                        break;
                }
            }

            @Override
            public void onFailure( Call<Void> call, Throwable t ){

            }

        } );
    }

    private void insertTable(){
        /* Variables */
        int seatsNumber;
        String description;
        int tableNumber;
        TableSrv tableSrv;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            seatsNumber = Integer.parseInt( txtInsertTableSeats.getText().toString() );
            tableNumber = Integer.parseInt( txtInsertTableNumber.getText().toString() );
            tableSrv = new TableSrv();
            if( !txtInsertTableDescription.getText().toString().isEmpty() ){
                description = txtInsertTableDescription.getText().toString();
                tableSrv.setDescription( description );
            }
            tableSrv.setId( tableNumber );
            tableSrv.setSeatsNumber( seatsNumber );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            TableCtrl tableCtrl = retrofit.create( TableCtrl.class );
            Call<Void> call = tableCtrl.insert( tableSrv, user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "La mesa se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectTable();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "La mesa ya se encuentra registrada", Toast.LENGTH_LONG ).show();
                            break;
                        case 4:
                            closeSession();
                            break;
                        case 6:
                            Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
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

    private void updateTable(){
        /* Variables */
        int seatsNumber;
        String description;
        TableSrv tableSrv;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            seatsNumber = Integer.parseInt( txtUpdateTableSeats.getText().toString() );
            tableSrv = new TableSrv();
            if( !txtUpdateTableDescription.getText().toString().isEmpty() ){
                description = txtUpdateTableDescription.getText().toString();
                tableSrv.setDescription( description );
            }
            tableSrv.setSeatsNumber( seatsNumber );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            TableCtrl tableCtrl = retrofit.create( TableCtrl.class );
            Call<Void> call = tableCtrl.update( tableSrv, table.getId(), user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "La mesa se actualizó correctamente", Toast.LENGTH_LONG ).show();
                            selectTable();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "La mesa ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectTable();
                            dialog.dismiss();
                            break;
                        case 4:
                            closeSession();
                            break;
                        case 6:
                            Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
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

    private boolean validateFieldsInsert(){
        /* Variables */
        boolean response;
        int number;
        int chairsNumber;
        /* Code start */
        response = true;
        if( txtInsertTableNumber.getText().toString().isEmpty() ){
            response = false;
        }else{
            number = Integer.parseInt( txtInsertTableNumber.getText().toString() );
            if( number <= 0 || number > 255 ){
                response = false;
            }
        }
        if( txtInsertTableSeats.getText().toString().isEmpty() ){
            response = false;
        }else{
            chairsNumber = Integer.parseInt( txtInsertTableSeats.getText().toString() );
            if( chairsNumber <= 0 || chairsNumber > 255 ){
                response = false;
            }
        }
        return response;
    }

    private boolean validateFieldsUpdate(){
        /* Variables */
        boolean response;
        int chairsNumber;
        /* Code start */
        response = true;
        if( txtUpdateTableSeats.getText().toString().isEmpty() ){
            response = false;
        }else{
            chairsNumber = Integer.parseInt( txtUpdateTableSeats.getText().toString() );
            if( chairsNumber <= 0 || chairsNumber > 255 ){
                response = false;
            }
        }
        return response;
    }

    private void showPanelInsertTable(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_table_insert_activity );
        btnInsertTable = ( Button )dialog.findViewById( R.id.btnInsertTable );
        txtInsertTableNumber = ( EditText )dialog.findViewById( R.id.txtInsertTableNumber );
        txtInsertTableSeats = ( EditText )dialog.findViewById( R.id.txtInsertTableSeats );
        txtInsertTableDescription = ( EditText )dialog.findViewById( R.id.txtInsertTableDescription );
        btnInsertTable.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertTable();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelUpdateTable(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_table_update_activity );
        btnUpdateTable = ( Button )dialog.findViewById( R.id.btnUpdateTable );
        txtUpdateTableSeats = ( EditText )dialog.findViewById( R.id.txtUpdateTableSeats );
        txtUpdateTableDescription = ( EditText )dialog.findViewById( R.id.txtUpdateTableDescription );
        btnUpdateTable.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateTable();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    public void showProgress(){
        progress = new Dialog( this );
        progress.setContentView( R.layout.progress );
        progress.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ));
        progress.show();
    }

}