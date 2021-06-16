package com.example.softwarecocinero.views.waiter.charge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.example.softwarecocinero.controllers.CategoryCtrl;
import com.example.softwarecocinero.controllers.ChargeCtrl;
import com.example.softwarecocinero.controllers.ProductCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.CategorySrv;
import com.example.softwarecocinero.services.ChargeSrv;
import com.example.softwarecocinero.services.ProductSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.waiter.product.WaiterProductAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaiterChargeActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
     */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private WaiterChargeAdapter adapterCharge;
    private WaiterProductAdapter adapterProduct;
    private PopupWindow popupWindow;
    private View view;
    private ChargeSrv charge;
    private List<ChargeSrv> charges;
    private List<ChargeSrv> chargesInsert;
    private List<CategorySrv> categories;
    private List<ProductSrv> products;
    private long orderId;
    private int categoryId;
    private boolean correct;

    /*
     *
     * Layout's components
     *
     */

    private RelativeLayout btnBack;
    private RecyclerView tblWaiterCharge;
    private RecyclerView tblWaiterProduct;
    private LinearLayout btnDeleteCharge;
    private FloatingActionButton btnShowPanelInsertCharge;
    private Dialog progress;
    private Dialog dialog;
    private Button btnInsertCharge;
    private EditText txtUpdateChargeDescription;
    private EditText txtUpdateChargeAmount;
    private Button btnUpdateCharge;
    private EditText txtInsertChargeProduct;
    private RelativeLayout btnSend;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.waiter_charge_activity);

        btnBack = findViewById( R.id.btnBack );
        btnBack.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                onBackPressed();
                overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
            }

        });
        btnSend = findViewById( R.id.btnSend );
        btnSend.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                sendOrder();
            }

        });
        tblWaiterCharge = ( RecyclerView )findViewById( R.id.tblWaiterCharge );
        tblWaiterCharge.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteCharge = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeleteCharge.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteCharge();
            }

        } );
        btnShowPanelInsertCharge = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertCharge );
        btnShowPanelInsertCharge.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertCharge();
            }

        } );
        adapterCharge = new WaiterChargeAdapter();
        adapterCharge.setOnItemClickListener( new WaiterChargeAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                charge = charges.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                charge = charges.get( position );
                showPanelUpdateCharge();
                fillFields();
            }

        } );
        tblWaiterCharge.setAdapter( adapterCharge );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        orderId = getIntent().getExtras().getLong( "order" );
    }

    @Override
    public void onStart() {
        super.onStart();
        selectCharge();
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
        txtUpdateChargeAmount.setText( "" + charge.getAmount() );
        if( !charge.getDescription().equals( "Sin información" ) ){
            txtUpdateChargeDescription.setText( charge.getDescription() );
        }
    }

    private void sendOrder(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ChargeCtrl charge = retrofit.create( ChargeCtrl.class );
        Call<Void> call = charge.send( orderId, user.getToken(), user.getId() );
        call.enqueue( new Callback<Void>() {

            @Override
            public void onResponse( Call<Void> call, Response<Void> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        selectCharge();
                        Toast.makeText( getApplicationContext(), "Se envío a imprimir correctamente", Toast.LENGTH_LONG ).show();
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

        });
    }

    private void addProduct( int position ){
        /* Variables */
        int i;
        boolean exist;
        int index;
        int chargeAmount;
        int productId;
        int size;
        /* Code start */
        i = 0;
        exist = false;
        index = 0;
        size = chargesInsert.size();
        productId = products.get( position ).getId();
        while( !exist && i < size ){
            if( chargesInsert.get( i ).getProduct().getId() ==  productId ){
                index = i;
                exist =  true;
            }
            i++;
        }
        chargeAmount = chargesInsert.get( index ).getAmount();
        chargeAmount++;
        chargesInsert.get( index ).setAmount( chargeAmount );
        adapterProduct.update( products, chargesInsert );
    }

    private void removeProduct( int position ){
        /* Variables */
        int i;
        boolean exist;
        int index;
        int chargeAmount;
        int productId;
        int size;
        /* Code start */
        i = 0;
        exist = false;
        index = 0;
        size = chargesInsert.size();
        productId = products.get( position ).getId();
        while( !exist && i < size ){
            if( chargesInsert.get( i ).getProduct().getId() ==  productId ){
                index = i;
                exist =  true;
            }
            i++;
        }
        chargeAmount = chargesInsert.get( index ).getAmount();
        chargeAmount--;
        if( chargeAmount == 0 ){
            chargesInsert.remove( index );
        }else{
            chargesInsert.get( index ).setAmount( chargeAmount );
        }
        adapterProduct.update( products, chargesInsert );
    }

    private void fillProduct(){
        tblWaiterProduct.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterProduct = new WaiterProductAdapter();
        adapterProduct.setOnItemClickListener( new WaiterProductAdapter.OnItemClickListener() {

            @Override
            public void onItemClick( int position ){
                /* Variables */
                int size;
                int i;
                int proId;
                boolean exits;
                ChargeSrv charge;
                /* Code start */
                proId = products.get( position ).getId();
                size =  chargesInsert.size();
                exits = false;
                i = 0;
                while( !exits && i < size ){
                    if( chargesInsert.get( i ).getProduct().getId() == proId ){
                        exits = true;
                    }
                    i++;
                }
                if( !exits ){
                    charge = new ChargeSrv();
                    charge.setProduct( products.get( position ) );
                    charge.setAmount( 1 );
                    charge.setOrder( orderId );
                    chargesInsert.add( charge );
                    adapterProduct.update( products, chargesInsert );
                }
            }

            @Override
            public void onAddClick( int position ){
                addProduct( position );
            }

            @Override
            public void onRemoveClick( int position ){
                removeProduct( position );
            }

        } );
        tblWaiterProduct.setAdapter( adapterProduct );
    }

    private void selectCharge(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ChargeCtrl charge = retrofit.create( ChargeCtrl.class );
        Call<List<ChargeSrv>> call = charge.selectByOrder( orderId, user.getToken(), user.getId() );
        call.enqueue( new Callback<List<ChargeSrv>>() {

            @Override
            public void onResponse( Call<List<ChargeSrv>> call, Response<List<ChargeSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        charges = response.body();
                        adapterCharge.update( charges );
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
            public void onFailure( Call<List<ChargeSrv>> call, Throwable t ){

            }

        });
    }

    private void selectProduct( String productName ){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ProductCtrl product = retrofit.create( ProductCtrl.class );
        Call<List<ProductSrv>> call = product.selectByName( productName, user.getToken(), user.getId() );
        call.enqueue( new Callback<List<ProductSrv>>() {

            @Override
            public void onResponse( Call<List<ProductSrv>> call, Response<List<ProductSrv>> response ){
                // Variables
                Headers headers;
                int responseCode;
                // Code start
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        products = response.body();
                        adapterProduct.update( products, chargesInsert );
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

    private void deleteCharge(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ChargeCtrl chargeCtrl = retrofit.create( ChargeCtrl.class );
        Call<Void> call = chargeCtrl.delete( charge.getId(),  user.getToken(), user.getId() );
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
                        selectCharge();
                        popupWindow.dismiss();
                        break;
                    case 3:
                        selectCharge();
                        popupWindow.dismiss();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "No es posible eliminar el producto puesto que la orden no se encuentra disponible", Toast.LENGTH_LONG ).show();
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

    private void insertCharge( final List<ChargeSrv> chargesSrv, final int position, final int size ){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ChargeCtrl chargeCtrl = retrofit.create( ChargeCtrl.class );
        Call<Void> call = chargeCtrl.insert( chargesSrv.get( position ), user.getToken(), user.getId() );
        call.enqueue( new Callback<Void>() {

            @Override
            public void onResponse( Call<Void> call, Response<Void> response ){
                /* Variables */
                int responseCode;
                Headers headers;
                int i;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ) {
                    case 1:
                        Toast.makeText( getApplicationContext(), "El producto se registró correctamente", Toast.LENGTH_LONG ).show();
                        i = position;
                        i++;
                        if( i < size ){
                            insertCharge( chargesSrv, i, size );
                        }else{
                            progress.dismiss();
                            if( correct ){
                                dialog.dismiss();
                            }
                            selectCharge();
                        }
                        break;
                    case 3:
                        correct = false;
                        Toast.makeText( getApplicationContext(), "No es posible registrar el producto puesto que la orden no se encuentra disponible", Toast.LENGTH_LONG ).show();
                        break;
                    case 4:
                        correct = false;
                        Toast.makeText( getApplicationContext(), "El producto ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
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

    private void updateCharge(){
        /* Variables */
        String description;
        ChargeSrv chargeSrv;
        int amount;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            amount = Integer.parseInt( txtUpdateChargeAmount.getText().toString() );
            chargeSrv = new ChargeSrv();
            if( !txtUpdateChargeDescription.getText().toString().isEmpty() ){
                description = txtUpdateChargeDescription.getText().toString();
                chargeSrv.setDescription( description );
            }
            chargeSrv.setAmount( amount );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            ChargeCtrl chargeCtrl = retrofit.create( ChargeCtrl.class );
            Call<Void> call = chargeCtrl.update( chargeSrv, charge.getId(), user.getToken(), user.getId() );
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
                            selectCharge();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "El producto ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            selectCharge();
                            dialog.dismiss();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "No es posible actualizar el producto puesto que la orden no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            dialog.dismiss();
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
        }else{
            Toast.makeText(getApplicationContext(), "Por favor ingresar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertProducts(){
        /* Variables */
        int size;
        /* Code start */
        correct = true;
        size = chargesInsert.size();
        if( size > 0 ){
            showProgress();
            insertCharge( chargesInsert, 0, size );
        }else{
            Toast.makeText(getApplicationContext(), "Por favor seleccione uno o más productos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFieldsUpdate(){
        /* Variables */
        boolean response;
        int amount;
        /* Code start */
        response = true;
        if( txtUpdateChargeAmount.getText().toString().isEmpty() ){
            response = false;
        }else{
            amount = Integer.parseInt( txtUpdateChargeAmount.getText().toString() );
            if( amount == 0 ){
                response = false;
            }
        }
        return response;
    }

    private void showPanelInsertCharge(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.waiter_charge_insert_activity );
        btnInsertCharge = ( Button )dialog.findViewById( R.id.btnInsertCharge );
        txtInsertChargeProduct = ( EditText )dialog.findViewById( R.id.txtInsertChargeProduct );
        tblWaiterProduct = ( RecyclerView )dialog.findViewById( R.id.tblWaiterProduct );
        btnInsertCharge.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertProducts();
            }

        } );
        txtInsertChargeProduct.addTextChangedListener( new TextWatcher() {

            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ){

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ){
                // Variables
                String productName;
                // Code
                productName = txtInsertChargeProduct.getText().toString();
                if( productName.isEmpty() ){
                    productName = "all";
                }
                selectProduct( productName );
            }

            @Override
            public void afterTextChanged( Editable s ){

            }

        } );
        selectProduct( "all" );
        chargesInsert = new ArrayList<>();
        // selectCategory();
        fillProduct();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelUpdateCharge(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.waiter_charge_update_activity );
        btnUpdateCharge = ( Button )dialog.findViewById( R.id.btnUpdateCharge );
        txtUpdateChargeDescription = ( EditText )dialog.findViewById( R.id.txtUpdateChargeDescription );
        txtUpdateChargeAmount = ( EditText )dialog.findViewById( R.id.txtUpdateChargeAmount );
        btnUpdateCharge.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateCharge();
            }

        } );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

}