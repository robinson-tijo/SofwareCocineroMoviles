package com.example.softwarecocinero.views.waiter.order;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.OrderCtrl;
import com.example.softwarecocinero.controllers.TableCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.OrderSrv;
import com.example.softwarecocinero.services.TableSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.WaiterMenuActivity;
import com.example.softwarecocinero.views.waiter.charge.WaiterChargeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaiterOrderActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
     */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private WaiterOrderAdapter adapterOrder;
    private PopupWindow popupWindow;
    private View view;
    private OrderSrv order;
    private List<OrderSrv> orders;
    private List<TableSrv> tables;
    private final int INSERT = 1;
    private final int UPDATE = 2;

    /*
     *
     * Layout's components
     *
     */

    private RelativeLayout btnShowWaiterMenu;
    private RecyclerView tblWaiterOrder;
    private LinearLayout btnDeleteOrder;
    private FloatingActionButton btnShowPanelInsertOrder;
    private Dialog progress;
    private Dialog dialog;
    private Button btnInsertOrder;
    private Button btnUpdateOrder;
    private Spinner cmbInsertOrderTable;
    private Spinner cmbUpdateOrderTable;
    private ArrayAdapter adapterTable;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.waiter_order_activity );

        btnShowWaiterMenu = findViewById( R.id.btnShowWaiterMenu );
        btnShowWaiterMenu.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showMenu();
            }

        } );
        tblWaiterOrder = ( RecyclerView )findViewById( R.id.tblWaiterOrder );
        tblWaiterOrder.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeleteOrder = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeleteOrder.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deleteOrder();
            }

        } );
        btnShowPanelInsertOrder = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertOrder );
        btnShowPanelInsertOrder.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showPanelInsertOrder();
            }

        } );
        adapterOrder = new WaiterOrderAdapter();
        adapterOrder.setOnItemClickListener( new WaiterOrderAdapter.OnItemClickListener() {

            @Override
            public void onItemClick( int position ){
                /* Variables */
                long orderId;
                /* Code start */
                order = orders.get( position );
                orderId = order.getId();
                showPanelCharge( orderId );
            }

            @Override
            public void onDeleteClick( int position, View view ) {
                order = orders.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                order = orders.get( position );
                showPanelUpdateArea();
            }

        } );
        tblWaiterOrder.setAdapter( adapterOrder );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );

    }

    @Override
    public void onStart() {
        super.onStart();
        selectOrder();
    }

    private void showMenu(){
        Intent intent = new Intent( this, WaiterMenuActivity.class );
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

    private void fillTableInsert(){
        /* Variables */
        ArrayList<String> tableList;
        String row;
        String description;
        /* Code start */
        tableList = new ArrayList<>();
        tableList.add( "Seleccione una mesa" );
        for( TableSrv tableSrv : tables ){
            row = "Mesa " + tableSrv.getId();
            description = tableSrv.getDescription();
            if( !description.equals( "Sin información" ) ){
                row += " -> " + description;
            }
            tableList.add( row );
        }
        adapterTable = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, tableList );
        adapterTable.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbInsertOrderTable.setAdapter( adapterTable );
    }

    private void fillTableUpdate(){
        /* Variables */
        ArrayList<String> tableList;
        String row;
        String description;
        /* Code start */
        tableList = new ArrayList<>();
        tableList.add( "Seleccione una mesa" );
        for( TableSrv tableSrv : tables ){
            row = "Mesa " + tableSrv.getId();
            description = tableSrv.getDescription();
            if( !description.equals( "Sin información" ) ){
                row += " -> " + description;
            }
            tableList.add( row );
        }
        adapterTable = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, tableList );
        adapterTable.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbUpdateOrderTable.setAdapter( adapterTable );
    }

    private void selectOrder(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        final OrderCtrl order = retrofit.create( OrderCtrl.class );
        Call<List<OrderSrv>> call = order.selectByUser( user.getId(), user.getToken(), user.getId() );
        call.enqueue( new Callback<List<OrderSrv>>() {

            @Override
            public void onResponse( Call<List<OrderSrv>> call, Response<List<OrderSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        orders = response.body();
                        adapterOrder.update( orders );
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
            public void onFailure( Call<List<OrderSrv>> call, Throwable t ){

            }

        });
    }

    private void selectTable( final int action ){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        TableCtrl table = retrofit.create( TableCtrl.class );
        Call<List<TableSrv>> call = table.selectAvailable( user.getToken(), user.getId() );
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

                        if( action == INSERT ){
                            fillTableInsert();
                        }else{
                            fillTableUpdate();
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
            public void onFailure( Call<List<TableSrv>> call, Throwable t ){

            }

        });
    }

    private void deleteOrder(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        OrderCtrl orderCtrl = retrofit.create( OrderCtrl.class );
        Call<Void> call = orderCtrl.delete( order.getId(),  user.getToken(), user.getId() );
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
                        Toast.makeText( getApplicationContext(), "La orden se eliminó correctamente", Toast.LENGTH_LONG ).show();
                        selectOrder();
                        popupWindow.dismiss();
                        break;
                    case 3:
                        selectOrder();
                        popupWindow.dismiss();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "No es posible eliminar la orden debido a que ya se han realizado pagos", Toast.LENGTH_LONG ).show();
                        popupWindow.dismiss();
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
        int tableIndex;
        /* Code start */
        response = true;
        tableIndex = cmbInsertOrderTable.getSelectedItemPosition();
        if( tableIndex == 0 ){
            response = false;
        }
        return response;
    }

    private void insertOrder(){
        /* Variables */
        int tableId;
        String userId;
        int tableIndex;
        OrderSrv orderSrv;
        TableSrv table;
        UserSrv user;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            tableIndex =  cmbInsertOrderTable.getSelectedItemPosition() - 1;
            tableId = tables.get( tableIndex ).getId();
            userId = this.user.getId();
            orderSrv = new OrderSrv();
            user = new UserSrv();
            table = new TableSrv();
            user.setId( userId );
            table.setId( tableId );
            orderSrv.setTable( table );
            orderSrv.setUser( user );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            OrderCtrl orderCtrl = retrofit.create( OrderCtrl.class );
            Call<Void> call = orderCtrl.insert( orderSrv, this.user.getToken(), this.user.getId() );
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
                            Toast.makeText( getApplicationContext(), "La orden se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectOrder();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "La mesa a la que se pretende registrar la orden no se encuentra disponible", Toast.LENGTH_LONG ).show();
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

    private void updateOrder(){
        /* Variables */
        int tableId;
        TableSrv table;
        int tableIndex;
        OrderSrv orderSrv;
        /* Code start */
        if( validateFieldsUpdate() ){
            showProgress();
            tableIndex =  cmbUpdateOrderTable.getSelectedItemPosition() - 1;
            tableId = tables.get( tableIndex ).getId();
            orderSrv = new OrderSrv();
            table = new TableSrv();
            table.setId( tableId );
            orderSrv.setTable( table );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            OrderCtrl orderCtrl = retrofit.create( OrderCtrl.class );
            Call<Void> call = orderCtrl.update( orderSrv, order.getId(), user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "La orden se actualizó correctamente", Toast.LENGTH_LONG ).show();
                            selectOrder();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "La orden ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            dialog.dismiss();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "La mesa a la que se pretende actualizar la orden no se encuentra disponible", Toast.LENGTH_LONG ).show();
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

    private boolean validateFieldsUpdate(){
        /* Variables */
        boolean response;
        int tableIndex;
        /* Code start */
        response = true;
        tableIndex = cmbUpdateOrderTable.getSelectedItemPosition();
        if( tableIndex == 0 ){
            response = false;
        }
        return response;
    }

    private void showPanelInsertOrder(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.waiter_order_insert_activity );
        btnInsertOrder = ( Button )dialog.findViewById( R.id.btnInsertOrder );
        cmbInsertOrderTable = ( Spinner ) dialog.findViewById( R.id.cmbInsertOrderTable );
        btnInsertOrder.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertOrder();
            }

        } );
        selectTable( INSERT );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelUpdateArea(){
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.waiter_order_update_activity );
        btnUpdateOrder = ( Button )dialog.findViewById( R.id.btnUpdateOrder );
        cmbUpdateOrderTable = ( Spinner ) dialog.findViewById( R.id.cmbUpdateOrderTable );
        btnUpdateOrder.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                updateOrder();
            }

        } );
        selectTable( UPDATE );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

    private void showPanelCharge( long orderId ){
        Intent intent = new Intent( this, WaiterChargeActivity.class );
        intent.putExtra( "user", user );
        intent.putExtra( "order", orderId );
        startActivity( intent );
        overridePendingTransition( R.anim.animation_left_in, R.anim.animation_left_out );
    }

}