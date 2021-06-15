package com.example.softwarecocinero.views.administrator.area;

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
import com.example.softwarecocinero.controllers.AreaCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.AreaSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.AdministratorMenuActivity;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.administrator.category.AdministratorCategoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdministratorAreaActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
     */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private AdministratorAreaAdapter adapterArea;
    private PopupWindow popupWindow;
    private View view;
    private AreaSrv area;
    private List<AreaSrv> areas;

    /*
     *
     * Layout's components
     *
     */

    private RelativeLayout btnShowAdministratorMenu;
    private RecyclerView tblAdministratorArea;
    private LinearLayout btnDeleteArea;
    private FloatingActionButton btnShowPanelInsertArea;
    private Dialog progress;
    private Dialog dialog;
    private Button btnInsertArea;
    private Button btnUpdateArea;
    private EditText txtInsertAreaName;
    private EditText txtUpdateAreaName;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_area_activity );

        btnShowAdministratorMenu = findViewById( R.id.btnShowAdministratorMenu );
        btnShowAdministratorMenu.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showMenu();
            }

        });
        tblAdministratorArea = ( RecyclerView )findViewById( R.id.tblAdministratorArea );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteArea = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeleteArea.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteArea();
            }

        } );
        btnShowPanelInsertArea = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertArea );
        btnShowPanelInsertArea.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertArea();
            }

        } );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        selectArea();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectArea();
    }

    private void showMenu(){
        Intent intent = new Intent( this, AdministratorMenuActivity.class );
        intent.putExtra( "user", user );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    public void showProgress(){
        progress = new Dialog( this );
        progress.setContentView( R.layout.progress );
        progress.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progress.show();
    }

    private void closeSession(){
        userMdl.delete();
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void fillFields(){
        txtUpdateAreaName.setText( area.getName() );
    }

    private void fillArea(){
        tblAdministratorArea.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterArea = new AdministratorAreaAdapter( areas );
        adapterArea.setOnItemClickListener( new AdministratorAreaAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                area = areas.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                area = areas.get( position );
                showPanelUpdateArea();
                fillFields();
            }

            @Override
            public void onCategoryClick( int position ) {
                /* Variables */
                int areaId;
                /* Code start */
                area = areas.get( position );
                areaId = area.getId();
                showPanelCategory( areaId );
            }

        } );
        tblAdministratorArea.setAdapter( adapterArea );
    }

    private void selectArea(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        AreaCtrl area = retrofit.create( AreaCtrl.class );
        Call<List<AreaSrv>> call = area.select( user.getToken(), user.getId() );
        call.enqueue( new Callback<List<AreaSrv>>() {

            @Override
            public void onResponse( Call<List<AreaSrv>> call, Response<List<AreaSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        areas = response.body();
                        fillArea();
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
            public void onFailure( Call<List<AreaSrv>> call, Throwable t ){

            }

        });
    }

    private void deleteArea(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        AreaCtrl areaCtrl = retrofit.create( AreaCtrl.class );
        Call<Void> call = areaCtrl.delete( area.getId(),  user.getToken(), user.getId() );
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
                        Toast.makeText( getApplicationContext(), "El area se eliminó correctamente", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
                        selectArea();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        selectArea();
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
    }

    private boolean validateFieldsInsert(){
        /* Variables */
        boolean response;
        /* Code start */
        response = true;
        if( txtInsertAreaName.getText().toString().isEmpty() ){
            response = false;
        }
        return response;
    }

    private void insertArea(){
        /* Variables */
        String name;
        AreaSrv areaSrv;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            name = txtInsertAreaName.getText().toString();
            areaSrv = new AreaSrv();
            areaSrv.setName( name );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            AreaCtrl areaCtrl = retrofit.create( AreaCtrl.class );
            Call<Void> call = areaCtrl.insert( areaSrv, user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "El area se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectArea();
                            dialog.dismiss();
                            break;
                        case 3:
                            closeSession();
                            break;
                        case 5:
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

    private void updateArea(){
        /* Variables */
        String name;
        AreaSrv areaSrv;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            name = txtUpdateAreaName.getText().toString();
            areaSrv = new AreaSrv();
            areaSrv.setName( name );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            AreaCtrl areaCtrl = retrofit.create( AreaCtrl.class );
            Call<Void> call = areaCtrl.update( areaSrv, area.getId(), user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "El area se actualizó correctamente", Toast.LENGTH_LONG ).show();
                            selectArea();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El área ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectArea();
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

    private boolean validateFieldsUpdate(){
        /* Variables */
        boolean response;
        /* Code start */
        response = true;
        if( txtUpdateAreaName.getText().toString().isEmpty() ){
            response = false;
        }
        return response;
    }

    private void showPanelInsertArea(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_area_insert_activity );
        btnInsertArea = ( Button )dialog.findViewById( R.id.btnInsertArea );
        txtInsertAreaName = ( EditText )dialog.findViewById( R.id.txtInsertAreaName );
        btnInsertArea.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertArea();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelUpdateArea(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_area_update_activity );
        btnUpdateArea = ( Button )dialog.findViewById( R.id.btnUpdateArea );
        txtUpdateAreaName = ( EditText )dialog.findViewById( R.id.txtUpdateAreaName );
        btnUpdateArea.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateArea();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelCategory( int areaId ){
        Intent intent = new Intent( this, AdministratorCategoryActivity.class );
        intent.putExtra( "user", user );
        intent.putExtra( "area", areaId );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

}