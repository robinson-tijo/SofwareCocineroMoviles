package com.example.softwarecocinero.views.administrator.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.AreaCtrl;
import com.example.softwarecocinero.controllers.UserCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.AreaSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.AdministratorMenuActivity;
import com.example.softwarecocinero.views.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdministratorUserActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
    */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv _user;
    private AdministratorUserAdapter adapterUser;
    private PopupWindow popupWindow;
    private View view;
    private UserSrv user;
    private List<UserSrv> users;
    private List<AreaSrv> areas;
    private final int INSERT = 1;
    private final int UPDATE = 2;

    /*
     *
     * Layout's components
     *
    */

    private RelativeLayout btnShowAdministratorMenu;
    private RecyclerView tblAdministratorUser;
    private LinearLayout btnDeleteUser;
    private FloatingActionButton btnShowPanelInsertUser;
    private Dialog progress;
    private Dialog dialog;
    private Button btnInsertUser;
    private Button btnUpdateUser;
    private EditText txtInsertUserName;
    private EditText txtInsertUserId;
    private Spinner cmbInsertUserRol;
    private Spinner cmbInsertUserArea;
    private EditText txtUpdateUserName;
    private Spinner cmbUpdateUserRol;
    private Spinner cmbUpdateUserArea;
    private ArrayAdapter adapterRol;
    private ArrayAdapter adapterArea;
    private RelativeLayout pnlInsertUserRol;
    private RelativeLayout pnlUpdateUserRol;
    private Switch swUserState;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_user_activity );

        btnShowAdministratorMenu = ( RelativeLayout )findViewById( R.id.btnShowAdministratorMenu );
        btnShowAdministratorMenu.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showMenu();
            }

        });
        tblAdministratorUser = ( RecyclerView )findViewById( R.id.tblAdministratorUser );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteUser = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteUser();
            }

        } );
        btnShowPanelInsertUser = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertUser );
        btnShowPanelInsertUser.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertUser();
            }

        } );
        userMdl = new UserMdl( getApplicationContext() );
        _user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        selectUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectUser();
    }

    private void showMenu(){
        Intent intent = new Intent( this, AdministratorMenuActivity.class );
        intent.putExtra( "user", _user );
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
        txtUpdateUserName.setText( user.getName() );
        cmbUpdateUserRol.setSelection( user.getRol() );
        if( user.getState() == 3 ){
            swUserState.setChecked( true );
        }
    }

    private void fillRolInsert(){
        String roles[] = { "Seleccione un rol", "Administrador", "Cajero", "Cocinero", "Mesero" };
        adapterRol = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, roles );
        adapterRol.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbInsertUserRol.setAdapter( adapterRol );
    }

    private void fillRolUpdate(){
        String roles[] = { "Seleccione un rol", "Administrador", "Cajero", "Cocinero", "Mesero" };
        adapterRol = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, roles );
        adapterRol.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbUpdateUserRol.setAdapter( adapterRol );
    }

    private void fillUser(){
        tblAdministratorUser.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterUser = new AdministratorUserAdapter( users );
        adapterUser.setOnItemClickListener( new AdministratorUserAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                user = users.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                user = users.get( position );
                showPanelUpdateUser();
                fillFields();
            }

        } );
        tblAdministratorUser.setAdapter( adapterUser );
    }

    private void fillAreaInsert(){
        /* Variables */
        ArrayList<String> areaList;
        /* Code start */
        areaList = new ArrayList<>();
        areaList.add( "Seleccione un área" );
        for( AreaSrv areaSrv : areas ){
            areaList.add( areaSrv.getName() );
        }
        adapterArea = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, areaList );
        adapterArea.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbInsertUserArea.setAdapter( adapterArea );
    }

    private void fillAreaUpdate(){
        /* Variables */
        int index;
        ArrayList<String> areaList;
        int i;
        /* Code start */
        index = 0;
        areaList = new ArrayList<>();
        i = 0;
        areaList.add( "Seleccione un área" );
        for( AreaSrv areaSrv : areas ){
            areaList.add( areaSrv.getName() );
            if( areaSrv.getId() == user.getArea() ){
                index = i;
            }
            i++;
        }
        adapterArea = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, areaList );
        adapterArea.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbUpdateUserArea.setAdapter( adapterArea );
        /* Selected area */
        index++;
        cmbUpdateUserArea.setSelection( index );
    }

    private void selectUser(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        UserCtrl user = retrofit.create( UserCtrl.class );
        Call<List<UserSrv>> call = user.select( _user.getToken(), _user.getId() );
        call.enqueue( new Callback<List<UserSrv>>() {

            @Override
            public void onResponse( Call<List<UserSrv>> call, Response<List<UserSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        users = response.body();
                        fillUser();
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
            public void onFailure( Call<List<UserSrv>> call, Throwable t ){

            }

        });
    }

    private void selectArea( final int action ){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        AreaCtrl area = retrofit.create( AreaCtrl.class );
        Call<List<AreaSrv>> call = area.select( _user.getToken(), _user.getId() );
        call.enqueue( new Callback<List<AreaSrv>>() {

            @Override
            public void onResponse( Call<List<AreaSrv>> call, Response<List<AreaSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                progress.dismiss();
                switch( responseCode ){
                    case 1:
                        areas = response.body();
                        if( action == INSERT ){
                            fillAreaInsert();
                        }else{
                            fillAreaUpdate();
                        }
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

    private void deleteUser(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        UserCtrl userCtrl = retrofit.create( UserCtrl.class );
        Call<Void> call = userCtrl.delete( user.getId(), _user.getToken(), _user.getId() );
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
                        Toast.makeText( getApplicationContext(), "El usuario se eliminó correctamente", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
                        selectUser();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        selectUser();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "El usuario no se puede eliminar debido a que tiene una sesión abierta", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
                        break;
                    case 5:
                        closeSession();
                        break;
                    case 7:
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
        int rolIndex;
        int areaIndex;
        /* Code start */
        response = true;
        rolIndex = cmbInsertUserRol.getSelectedItemPosition();
        if( txtInsertUserName.getText().toString().isEmpty() ){
            response = false;
        }
        if( txtInsertUserId.getText().toString().isEmpty() ){
            response = false;
        }
        if( rolIndex == 0 ){
            response = false;
        }else if( rolIndex == 3 ){
            areaIndex = cmbInsertUserArea.getSelectedItemPosition();
            if( areaIndex == 0 ){
                response = false;
            }
        }
        return response;
    }

    private void insertUser(){
        /* Variables */
        String name;
        String id;
        int rol;
        int area;
        int areaIndex;
        UserSrv userSrv;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            name = txtInsertUserName.getText().toString();
            id = txtInsertUserId.getText().toString();
            rol = cmbInsertUserRol.getSelectedItemPosition();
            userSrv = new UserSrv();
            userSrv.setName( name );
            userSrv.setId( id );
            userSrv.setRol( rol );
            if( rol == 3 ){
                areaIndex = cmbInsertUserArea.getSelectedItemPosition() - 1;
                area = areas.get( areaIndex ).getId();
                userSrv.setArea( area );
            }
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            UserCtrl userCtrl = retrofit.create( UserCtrl.class );
            Call<Void> call = userCtrl.insert( userSrv, _user.getToken(), _user.getId() );
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
                            Toast.makeText( getApplicationContext(), "El usuario se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectUser();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El usuario ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectUser();
                            dialog.dismiss();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "El área a la cual pretende registrar el usuario ya no existe", Toast.LENGTH_LONG ).show();
                            selectUser();
                            dialog.dismiss();
                            break;
                        case 5:
                            closeSession();
                            break;
                        case 7:
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

    private void updateUser(){
        /* Variables */
        String name;
        UserSrv userSrv;
        int area;
        int areaIndex;
        int rol;
        int state;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            name = txtUpdateUserName.getText().toString();
            rol = cmbUpdateUserRol.getSelectedItemPosition();
            userSrv = new UserSrv();
            userSrv.setName( name );
            userSrv.setRol( rol );
            if( rol == 3 ){
                areaIndex = cmbUpdateUserArea.getSelectedItemPosition() - 1;
                area = areas.get( areaIndex ).getId();
                userSrv.setArea( area );
            }
            if( swUserState.isChecked() ){
                state = 3;
            }else{
                state = 1;
            }
            userSrv.setState( state );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            UserCtrl userCtrl = retrofit.create( UserCtrl.class );
            Call<Void> call = userCtrl.update( userSrv, user.getId(), _user.getToken(), _user.getId() );
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
                            selectUser();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El usuario ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectUser();
                            dialog.dismiss();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "El área a la cual pretende actualizar el usuario ya no existe", Toast.LENGTH_LONG ).show();
                            selectUser();
                            dialog.dismiss();
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

    private boolean validateFieldsUpdate(){
        /* Variables */
        boolean response;
        int rolIndex;
        int areaIndex;
        /* Code start */
        response = true;
        rolIndex = cmbUpdateUserRol.getSelectedItemPosition();
        if( txtUpdateUserName.getText().toString().isEmpty() ){
            response = false;
        }
        if( rolIndex == 0 ){
            response = false;
        }else if( rolIndex == 3 ){
            areaIndex = cmbUpdateUserArea.getSelectedItemPosition();
            if( areaIndex == 0 ){
                response = false;
            }
        }
        return response;
    }

    private void showPanelInsertUser(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_user_insert_activity );
        btnInsertUser = ( Button )dialog.findViewById( R.id.btnInsertUser );
        txtInsertUserName = ( EditText )dialog.findViewById( R.id.txtInsertUserName );
        txtInsertUserId = ( EditText )dialog.findViewById( R.id.txtInsertUserId );
        cmbInsertUserRol = ( Spinner )dialog.findViewById( R.id.cmbInsertUserRol );
        cmbInsertUserArea = ( Spinner )dialog.findViewById( R.id.cmbInsertUserArea );
        pnlInsertUserRol = ( RelativeLayout )dialog.findViewById( R.id.pnlInsertUserRol );
        btnInsertUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertUser();
            }

        } );
        cmbInsertUserRol.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ){
                if( position != 3 ){
                    pnlInsertUserRol.setVisibility( view.INVISIBLE );

                }else{
                    showProgress();
                    selectArea( INSERT );
                    pnlInsertUserRol.setVisibility( view.VISIBLE );
                }
            }

            @Override
            public void onNothingSelected( AdapterView<?> parent ){

            }

        });
        fillRolInsert();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelUpdateUser(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_user_update_activity );
        btnUpdateUser = ( Button )dialog.findViewById( R.id.btnUpdateUser );
        txtUpdateUserName = ( EditText )dialog.findViewById( R.id.txtUpdateUserName );
        cmbUpdateUserRol = ( Spinner )dialog.findViewById( R.id.cmbUpdateUserRol );
        cmbUpdateUserArea = ( Spinner )dialog.findViewById( R.id.cmbUpdateUserArea );
        pnlUpdateUserRol = ( RelativeLayout )dialog.findViewById( R.id.pnlUpdateUserRol );
        btnUpdateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateUser();
            }

        } );
        cmbUpdateUserRol.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ){
                if( position != 3 ){
                    pnlUpdateUserRol.setVisibility( view.INVISIBLE );

                }else{
                    showProgress();
                    selectArea( UPDATE );
                    pnlUpdateUserRol.setVisibility( view.VISIBLE );
                }
            }

            @Override
            public void onNothingSelected( AdapterView<?> parent ){

            }

        });
        swUserState = ( Switch )dialog.findViewById( R.id.swUserState );
        swUserState.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ){
                if( isChecked ){
                    swUserState.setText( "Bloqueado" );
                }else{
                    swUserState.setText( "Desbloqueado" );
                }
            }

        } );
        fillRolUpdate();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

}