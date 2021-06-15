package com.example.softwarecocinero.views.administrator.product;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.AreaCtrl;
import com.example.softwarecocinero.controllers.CategoryCtrl;
import com.example.softwarecocinero.controllers.ProductCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.AreaSrv;
import com.example.softwarecocinero.services.CategorySrv;
import com.example.softwarecocinero.services.ProductSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.AdministratorMenuActivity;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.administrator.area.AdministratorAreaAdapter;
import com.example.softwarecocinero.views.administrator.category.AdministratorCategoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdministratorProductActivity extends AppCompatActivity {


    /*
     *
     * Global variables
     * area
    */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private AdministratorProductAdapter adapterProduct;
    private PopupWindow popupWindow;
    private View view;
    private ProductSrv product;
    private List<ProductSrv> products;
    private List<CategorySrv> categories;
    private int categoryId;

    /*
     *
     * Layout's components
     *
    */

    private RelativeLayout btnShowAdministratorMenu;
    private RecyclerView tblAdministratorProduct;
    private LinearLayout btnDeleteProduct;
    private FloatingActionButton btnShowPanelInsertProduct;
    private Dialog progress;
    private Dialog dialog;
    private Button btnInsertProduct;
    private Button btnUpdateProduct;
    private EditText txtInsertProductName;
    private EditText txtInsertProductPrice;
    private EditText txtInsertProductTimePreparation;
    private EditText txtUpdateProductName;
    private EditText txtUpdateProductPrice;
    private EditText txtUpdateProductTimePreparation;
    private ArrayAdapter adapterCategory;
    private Spinner cmbProductCategory;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.administrator_product_activity );

        btnShowAdministratorMenu = findViewById( R.id.btnShowAdministratorMenu );
        btnShowAdministratorMenu.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View v ){
                showMenu();
            }
        });
        tblAdministratorProduct = ( RecyclerView )findViewById( R.id.tblAdministratorProduct );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteProduct = ( LinearLayout )view.findViewById( R.id.btnDelete );
        cmbProductCategory = ( Spinner )findViewById( R.id.cmbProductCategory );
        cmbProductCategory.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ){
                /* Variables */
                int categoryIndex;
                int category;
                /* Code start */
                categoryIndex = cmbProductCategory.getSelectedItemPosition();
                categoryId = categories.get( categoryIndex ).getId();
                selectProduct();
            }

            @Override
            public void onNothingSelected( AdapterView<?> parent ){

            }

        } );
        btnDeleteProduct.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteProduct();
            }

        } );
        btnShowPanelInsertProduct = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertProduct );
        btnShowPanelInsertProduct.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertProduct();
            }

        } );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        selectCategory();
        selectProduct();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectCategory();
        selectProduct();
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
        progress.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ));
        progress.show();
    }

    private void closeSession(){
        userMdl.delete();
        Intent intent = new Intent( this, MainActivity.class );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

    private void fillFields(){
        txtUpdateProductName.setText( product.getName() );
        txtUpdateProductPrice.setText( "" + product.getPrice() );
        txtUpdateProductTimePreparation.setText( "" + product.getPreparationTime() );
    }

    private void showPanelInsertProduct(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_product_insert_activity );
        btnInsertProduct = ( Button )dialog.findViewById( R.id.btnInsertProduct );
        txtInsertProductName = ( EditText )dialog.findViewById( R.id.txtInsertProductName );
        txtInsertProductPrice = ( EditText )dialog.findViewById( R.id.txtInsertProductPrice );
        txtInsertProductTimePreparation = ( EditText )dialog.findViewById( R.id.txtInsertProductTimePreparation );
        btnInsertProduct.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertProduct();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void fillCategory(){
        /* Variables */
        ArrayList<String> categoryList;
        /* Code start */
        categoryList = new ArrayList<>();
        for( CategorySrv categorySrv : categories ){
            categoryList.add( categorySrv.getName() );
        }
        adapterCategory = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, categoryList );
        adapterCategory.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbProductCategory.setAdapter( adapterCategory );
    }

    private void selectCategory(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        CategoryCtrl categoryCtrl = retrofit.create( CategoryCtrl.class );
        Call<List<CategorySrv>> call = categoryCtrl.select( user.getToken(), user.getId() );
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

    private void selectProduct(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ProductCtrl product = retrofit.create( ProductCtrl.class );
        Call<List<ProductSrv>> call = product.select( categoryId, user.getToken(), user.getId() );
        call.enqueue( new Callback<List<ProductSrv>>() {

            @Override
            public void onResponse( Call<List<ProductSrv>> call, Response<List<ProductSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start*/
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        products = response.body();
                        fillProduct();
                        break;
                    case 2:
                        closeSession();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesaarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
                        closeSession();
                        break;
                }
            }

            @Override
            public void onFailure( Call<List<ProductSrv>> call, Throwable t ){

            }

        } );
    }

    private boolean validateFieldsInsert(){
        /* Variables */
        boolean response;
        int preparationTime;
        /* Code start */
        response = true;
        if( txtInsertProductName.getText().toString().isEmpty() ){
            response = false;
        }
        if( txtInsertProductPrice.getText().toString().isEmpty() ){
            response = false;
        }
        if( txtInsertProductTimePreparation.getText().toString().isEmpty() ){
            response = false;
        }else{
            preparationTime = Integer.parseInt( txtInsertProductTimePreparation.getText().toString() );
            if( preparationTime <= 0 || preparationTime > 255 ){
                response = false;
            }
        }
        return response;
    }

    private void insertProduct(){
        /* Variables */
        String name;
        long price;
        int timePreparation;
        ProductSrv productSrv;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            name = txtInsertProductName.getText().toString();
            price =  Long.parseLong( txtInsertProductPrice.getText().toString() );
            timePreparation = Integer.parseInt( txtInsertProductTimePreparation.getText().toString() );
            productSrv = new ProductSrv();
            productSrv.setName( name );
            productSrv.setPrice( price );
            productSrv.setPreparationTime( timePreparation );
            productSrv.setCategory( categoryId );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            ProductCtrl productCtrl = retrofit.create( ProductCtrl.class );
            Call<Void> call = productCtrl.insert( productSrv, user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "El producto se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectProduct();
                            dialog.dismiss();
                            break;
                        case 3:
                            closeSession();
                            break;
                        case 5:
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

    private void deleteProduct(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ProductCtrl productCtrl = retrofit.create( ProductCtrl.class );
        Call<Void> call = productCtrl.delete( product.getId(),  user.getToken(), user.getId() );
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
                        Toast.makeText( getApplicationContext(), "El producto se eliminó correctamente", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
                        selectProduct();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        selectProduct();
                        break;
                    case 4:
                        closeSession();
                        break;
                    case 6:
                        Toast.makeText( getApplicationContext(), "No cuenta con los permisos necesaarios para realizar esta consulta", Toast.LENGTH_LONG ).show();
                        closeSession();
                        break;
                }
            }

            @Override
            public void onFailure( Call<Void> call, Throwable t ){

            }

        } );
    }

    private void fillProduct(){
        tblAdministratorProduct.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterProduct = new AdministratorProductAdapter( products );
        adapterProduct.setOnItemClickListener( new AdministratorProductAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                product = products.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                product = products.get( position );
                showPanelUpdateProduct();
                fillFields();
            }

        } );
        tblAdministratorProduct.setAdapter( adapterProduct );
    }

    private void updateProduct(){
        /* Variables */
        String name;
        long price;
        int timePreparation;
        ProductSrv productSrv;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            name = txtUpdateProductName.getText().toString();
            price = Long.parseLong( txtUpdateProductPrice.getText().toString() );
            timePreparation = Integer.parseInt( txtUpdateProductTimePreparation.getText().toString() );
            productSrv = new ProductSrv();
            productSrv.setName( name );
            productSrv.setPrice( price );
            productSrv.setPreparationTime( timePreparation );
            productSrv.setImage( "image" );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            ProductCtrl productCtrl = retrofit.create( ProductCtrl.class );
            Call<Void> call = productCtrl.update( productSrv, product.getId(), user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "El producto se actualizó correctamente", Toast.LENGTH_LONG ).show();
                            selectProduct();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El producto ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectProduct();
                            dialog.dismiss();
                            break;
                        case 4:
                            closeSession();
                            break;
                        case 6:
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
        int preparationTime;
        /* Code start */
        response = true;
        if( txtUpdateProductName.getText().toString().isEmpty() ){
            response = false;
        }
        if( txtUpdateProductPrice.getText().toString().isEmpty() ){
            response = false;
        }
        if( txtUpdateProductTimePreparation.getText().toString().isEmpty() ){
            response = false;
        }else{
            preparationTime = Integer.parseInt( txtUpdateProductTimePreparation.getText().toString() );
            if( preparationTime <= 0 || preparationTime > 255 ){
                response = false;
            }
        }
        return response;
    }

    private void showPanelUpdateProduct(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.administrator_product_update_activity );
        btnUpdateProduct = ( Button )dialog.findViewById( R.id.btnUpdateProduct );
        txtUpdateProductName = ( EditText )dialog.findViewById( R.id.txtUpdateProductName );
        txtUpdateProductPrice = ( EditText )dialog.findViewById( R.id.txtUpdateProductPrice );
        txtUpdateProductTimePreparation = ( EditText )dialog.findViewById( R.id.txtUpdateProductTimePreparation );
        btnUpdateProduct.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateProduct();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

}