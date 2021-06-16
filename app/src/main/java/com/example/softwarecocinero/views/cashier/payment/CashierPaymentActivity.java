package com.example.softwarecocinero.views.cashier.payment;

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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.controllers.ChargeCtrl;
import com.example.softwarecocinero.controllers.OrderCtrl;
import com.example.softwarecocinero.controllers.PaymentCtrl;
import com.example.softwarecocinero.models.UserMdl;
import com.example.softwarecocinero.services.ChargeSrv;
import com.example.softwarecocinero.services.OrderSrv;
import com.example.softwarecocinero.services.PaymentSrv;
import com.example.softwarecocinero.services.UserSrv;
import com.example.softwarecocinero.views.CashierMenuActivity;
import com.example.softwarecocinero.views.MainActivity;
import com.example.softwarecocinero.views.cashier.charge.CashierChargeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CashierPaymentActivity extends AppCompatActivity {

    /*
     *
     * Global variables
     *
     */

    private final String url = "http://192.168.1.254:5000/api/";
    private UserMdl userMdl;
    private UserSrv user;
    private CashierPaymentAdapter adapterPayment;
    private CashierChargeAdapter adapterCharge;
    private PopupWindow popupWindow;
    private View view;
    // private View viewPaymentMenu;
    // private PopupWindow popupWindowPaymentMenu;
    private PaymentSrv payment;
    private List<PaymentSrv> payments;
    private List<OrderSrv> orders;
    private List<ChargeSrv> charges;
    private OrderSrv order;
    private long paymentPrice;
    private long orderPrice;
    private long orderTip;

    /*
     *
     * Layout's components
     *
     */

    private RelativeLayout btnShowCashierMenu;
    private RelativeLayout btnSearchOrder;
    private RecyclerView tblCashierPayment;
    private LinearLayout btnDeletePayment;
    private FloatingActionButton btnShowPanelInsertPayment;
    private Dialog progress;
    private Dialog dialog;
    private Spinner cmbPaymentOrder;
    private Button btnInsertPayment;
    // private Button btnUpdatePayment;
    private TextView lblOrderPrice;
    private RecyclerView tblCashierProduct;
    private ArrayAdapter adapterOrder;
    private TextView lblOrderTotalPrice;
    // private Switch swOrderTip;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.cashier_payment_activity );

        lblOrderTotalPrice = ( TextView )findViewById( R.id.lblOrderTotalPrice );
        btnShowCashierMenu = ( RelativeLayout )findViewById( R.id.btnShowCashierMenu );
        btnShowCashierMenu.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                showMenu();
            }

        });
        btnSearchOrder = ( RelativeLayout )findViewById( R.id.btnSearchOrder );
        btnSearchOrder.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                selectOrder();
            }

        });
        tblCashierPayment = ( RecyclerView )findViewById( R.id.tblCashierPayment );
        LayoutInflater inflater =  ( LayoutInflater )getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate( R.layout.popup_delete_menu, null );
        popupWindow = new PopupWindow( view, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnDeletePayment = ( LinearLayout )view.findViewById( R.id.btnDelete );
        btnDeletePayment.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                showProgress();
                deletePayment();
            }

        } );
        // viewPaymentMenu = inflater.inflate( R.layout.popup_payment_menu, null );
        // popupWindowPaymentMenu = new PopupWindow( viewPaymentMenu, 300, RelativeLayout.LayoutParams.WRAP_CONTENT, true );
        btnShowPanelInsertPayment = ( FloatingActionButton )findViewById( R.id.btnShowPanelInsertPayment );
        btnShowPanelInsertPayment.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick( View v ){
                if( orders.size() > 0 ){
                    // popupWindowPaymentMenu.showAsDropDown( btnShowPanelInsertPayment, -153, 0 );
                    showPanelInsertPayment();
                }else{
                    Toast.makeText( getApplicationContext(), "No existe una orden a la cual realizar el pago", Toast.LENGTH_LONG ).show();
                }

            }

        } );
        cmbPaymentOrder = ( Spinner ) findViewById( R.id.cmbPaymentOrder );
        cmbPaymentOrder.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id ){
                order = orders.get( position );
                lblOrderTotalPrice.setText( "Valor total : " + order.getPrice() );
                selectPayment();
            }

            @Override
            public void onNothingSelected( AdapterView<?> parent ){

            }

        } );
        userMdl = new UserMdl( getApplicationContext() );
        user = ( UserSrv )getIntent().getSerializableExtra( "user" );
        selectOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectOrder();
    }

    private void showMenu(){
        Intent intent = new Intent( this, CashierMenuActivity.class );
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

    private void fillPayment(){
        tblCashierPayment.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterPayment = new CashierPaymentAdapter( payments );
        adapterPayment.setOnItemClickListener( new CashierPaymentAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick( int position, View view ) {
                payment = payments.get( position );
                popupWindow.showAsDropDown( view, -153, 0 );
            }

            @Override
            public void onUpdateClick( int position ) {
                payment = payments.get( position );
            }

        } );
        tblCashierPayment.setAdapter( adapterPayment );
    }

    private void fillOrder(){
        /* Variables */
        ArrayList<String> orderList;
        /* Code start */
        orderList = new ArrayList<>();
        for( OrderSrv orderSrv : orders ){
            orderList.add( "Mesa " + orderSrv.getTable().getId() );
        }
        adapterOrder = new ArrayAdapter<String>( getApplicationContext(), R.layout.spinner_item, orderList );
        adapterOrder.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );
        cmbPaymentOrder.setAdapter( adapterOrder );
    }

    private void selectPayment(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        PaymentCtrl payment = retrofit.create( PaymentCtrl.class );
        Call<List<PaymentSrv>> call = payment.selectByOrder( order.getId(), user.getToken(), user.getId() );
        call.enqueue( new Callback<List<PaymentSrv>>() {

            @Override
            public void onResponse( Call<List<PaymentSrv>> call, Response<List<PaymentSrv>> response ){
                /* Variables */
                Headers headers;
                int responseCode;
                /* Code start */
                headers = response.headers();
                responseCode = Integer.parseInt( headers.get( "response" ) );
                switch( responseCode ){
                    case 1:
                        payments = response.body();
                        fillPayment();
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
            public void onFailure( Call<List<PaymentSrv>> call, Throwable t ){

            }

        });
    }

    private void selectOrder(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        final OrderCtrl order = retrofit.create( OrderCtrl.class );
        Call<List<OrderSrv>> call = order.select( user.getToken(), user.getId() );
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
                        fillOrder();
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

    private void selectCharge(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        ChargeCtrl charge = retrofit.create( ChargeCtrl.class );
        Call<List<ChargeSrv>> call = charge.selectByOrder( order.getId(), user.getToken(), user.getId() );
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

    private void deletePayment(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
        PaymentCtrl paymentCtrl = retrofit.create( PaymentCtrl.class );
        Call<Void> call = paymentCtrl.delete( payment.getId(),  user.getToken(), user.getId() );
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
                        selectPayment();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        selectPayment();
                        break;
                    case 4:
                        Toast.makeText( getApplicationContext(), "La orden ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
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
    }

    private boolean validateFieldsInsert(){
        /* Variables */
        boolean response;
        long paymentPrice;
        /* Code start */
        paymentPrice = order.getPrice() - order.getPaidPrice();
        response = true;
        if( orderPrice == 0 ){
            response = false;
            Toast.makeText(getApplicationContext(), "El valor a pagar no debe ser cero", Toast.LENGTH_SHORT).show();
        }else if( orderPrice > paymentPrice ){
            response = false;
            Toast.makeText(getApplicationContext(), "El valor a pagar supera el precio de la orden", Toast.LENGTH_SHORT).show();
        }
        return response;
    }

    private void insertPayment(){
        /* Variables */
        PaymentSrv paymentSrv;
        /* Code start */
        if( validateFieldsInsert() ){
            showProgress();
            paymentSrv = new PaymentSrv();
            paymentSrv.setPrice( orderPrice );
            paymentSrv.setTip( orderTip );
            paymentSrv.setOrder( order.getId() );
            Retrofit retrofit = new Retrofit.Builder().baseUrl( url ).addConverterFactory( GsonConverterFactory.create() ).build();
            PaymentCtrl paymentCtrl = retrofit.create( PaymentCtrl.class );
            Call<Void> call = paymentCtrl.insert( paymentSrv, user.getToken(), user.getId() );
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
                            Toast.makeText( getApplicationContext(), "El pago se registró correctamente", Toast.LENGTH_LONG ).show();
                            selectOrder();
                            dialog.dismiss();
                            break;
                        case 3:
                            Toast.makeText( getApplicationContext(), "La orden ya no se encuentra disponible", Toast.LENGTH_LONG ).show();
                            dialog.dismiss();
                            break;
                        case 4:
                            Toast.makeText( getApplicationContext(), "La orden ha sido pagada completamente", Toast.LENGTH_LONG ).show();
                            lblOrderTotalPrice.setText( "0" );
                            payments = new ArrayList<>();
                            fillPayment();
                            selectOrder();
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
        }
    }

    private void showPanelInsertPayment(){
        paymentPrice = 0;
        orderTip = 0;
        orderPrice = 0;
        dialog =  new Dialog( this );
        dialog.setContentView( R.layout.cashier_payment_insert_activity );
        btnInsertPayment = ( Button )dialog.findViewById( R.id.btnInsertPayment );
        // swOrderTip = ( Switch ) dialog.findViewById( R.id.swOrderTip );
        lblOrderPrice = ( TextView )dialog.findViewById( R.id.lblOrderPrice );
        btnInsertPayment.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ){
                insertPayment();
            }

        } );
        tblCashierProduct = ( RecyclerView )dialog.findViewById( R.id.tblCashierProduct );
        tblCashierProduct.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        adapterCharge = new CashierChargeAdapter();
        adapterCharge.setOnItemClickListener( new CashierChargeAdapter.OnItemClickListener() {

            @Override
            public void onItemClick( int position ){
                /* Variables */
                ChargeSrv charge;
                int state;
                /* Code start */
                charge = charges.get( position );
                state = charge.getState();
                if( state == 9 ){
                    charges.get( position ).setState( 0 );
                    paymentPrice -= charge.getPrice();
                    orderPrice -= charge.getPrice();
                }else{
                    charges.get( position ).setState( 9 );
                    paymentPrice += charge.getPrice();
                    orderPrice += charge.getPrice();
                }
                /*if( swOrderTip.isChecked() ){
                    paymentPrice -= orderTip;
                    orderTip = ( long )( paymentPrice * 0.1 );
                    paymentPrice += orderTip;
                }*/
                lblOrderPrice.setText( "$ " + paymentPrice );
                adapterCharge.update( charges );
            }

        } );
        /*swOrderTip.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ){
                if( isChecked ){
                    orderTip = ( long )( paymentPrice * 0.1 );
                    paymentPrice += orderTip;
                }else{
                    paymentPrice -= orderTip;
                }
                lblOrderPrice.setText( "$ " + paymentPrice );
            }

        } );*/
        tblCashierProduct.setAdapter( adapterCharge );
        selectCharge();
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.show();
    }

}