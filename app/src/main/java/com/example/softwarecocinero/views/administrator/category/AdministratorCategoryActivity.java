package com.example.softwarecocinero.views.administrator.category;

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
import com.example.softwarecocinero.controllers.CategoryCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.CategorySrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.administrator.area.AdministratorAreaActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdministratorCategoryActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
     */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private AdministratorCategoryAdapter adapterCategory;
    private PopupWindow popupWindow;
    private View view;
    private CategorySrv category;
    private List<CategorySrv> categories;
    private int areaId;

    /*
     *
     * Layout's components
     *
     */

    private RelativeLayout btnShowAdministratorArea;
    private RecyclerView tblAdministratorCategory;
    private LinearLayout btnDeleteCategory;
    private FloatingActionButton btnShowPanelInsertCategory;
    private Dialog progress;
    private Dialog dialog;
    private Button btnInsertCategory;
    private Button btnUpdateCategory;
    private EditText txtInsertCategoryName;
    private EditText txtUpdateCategoryName;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_category_activity );

        btnShowAdministratorArea = findViewById( R.id.btnShowAdministratorArea );
        btnShowAdministratorArea.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelArea();
            }

        });
        tblAdministratorCategory = ( RecyclerView )findViewById( R.id.tblAdministratorCategory );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteCategory = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeleteCategory.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteCategory();
            }

        } );
        btnShowPanelInsertCategory = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertCategory );
        btnShowPanelInsertCategory.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertCategory();
            }

        } );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        areaId = getIntent().getExtras().getInt( "area" );
        selectCategory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectCategory();
    }

    private void showPanelArea(){
        Intent intent = new Intent( this, AdministratorAreaActivity.class );
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
        txtUpdateCategoryName.setText( category.getName() );
    }

    private void fillCategory(){
        tblAdministratorCategory.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterCategory = new AdministratorCategoryAdapter( categories );
        adapterCategory.setOnItemClickListener( new AdministratorCategoryAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                category = categories.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                category = categories.get( position );
                showPanelUpdateCategory();
                fillFields();
            }

        } );
        tblAdministratorCategory.setAdapter( adapterCategory );
    }

    private void selectCategory(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        CategoryCtrl categoryCtrl = retrofit.create( CategoryCtrl.class );
        Call<List<CategorySrv>> call = categoryCtrl.selectByCategory( areaId, user.getToken(), user.getId() );
        call.enqueue( new Callback<List<CategorySrv>>() {

            @Override
            public void onResponse( Call<List<CategorySrv>> call, Response<List<CategorySrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        categories = response.body();
                        fillCategory();
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
            public void onFailure( Call<List<CategorySrv>> call, Throwable t ){

            }

        });
    }

    private void deleteCategory(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        CategoryCtrl categoryCtrl = retrofit.create( CategoryCtrl.class );
        Call<Void> call = categoryCtrl.delete( category.getId(),  user.getToken(), user.getId() );
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
                        Toast.makeText( getApplicationContext(), "La categoría se eliminó correctamente", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
                        selectCategory();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        selectCategory();
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
        if( txtInsertCategoryName.getText().toString().isEmpty() ){
            response = false;
        }
        return response;
    }

    private void insertCategory(){
        /* Variables */
        String name;
        CategorySrv categorySrv;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            name = txtInsertCategoryName.getText().toString();
            categorySrv = new CategorySrv();
            categorySrv.setName( name );
            categorySrv.setArea( areaId );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            CategoryCtrl categoryCtrl = retrofit.create( CategoryCtrl.class );
            Call<Void> call = categoryCtrl.insert( categorySrv, user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "La categoría se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectCategory();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El aŕea a la cual prentende registrar la categoría no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectCategory();
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

    private void updateCategory(){
        /* Variables */
        String name;
        CategorySrv categorySrv;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            name = txtUpdateCategoryName.getText().toString();
            categorySrv = new CategorySrv();
            categorySrv.setName( name );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            CategoryCtrl categoryCtrl = retrofit.create( CategoryCtrl.class );
            Call<Void> call = categoryCtrl.update( categorySrv, category.getId(), user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "La categoría se actualizó correctamente", Toast.LENGTH_LONG ).show();
                            selectCategory();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "La categoría ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectCategory();
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
        if( txtUpdateCategoryName.getText().toString().isEmpty() ){
            response = false;
        }
        return response;
    }

    private void showPanelInsertCategory(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_category_insert_activity );
        btnInsertCategory = ( Button )dialog.findViewById( R.id.btnInsertCategory );
        txtInsertCategoryName = ( EditText )dialog.findViewById( R.id.txtInsertCategoryName );
        btnInsertCategory.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertCategory();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelUpdateCategory(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_category_update_activity );
        btnUpdateCategory = ( Button )dialog.findViewById( R.id.btnUpdateCategory );
        txtUpdateCategoryName = ( EditText )dialog.findViewById( R.id.txtUpdateCategoryName );
        btnUpdateCategory.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateCategory();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }
}