package com.example.softwarecocinero.views.cashier.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.PaymentSrv;

import java.util.List;

public class CashierPaymentAdapter extends RecyclerView.Adapter<CashierPaymentAdapter.ViewHolder>  {

    private List<PaymentSrv> payments;
    private CashierPaymentAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
    }

    public CashierPaymentAdapter( List<PaymentSrv> payments ){
        this.payments = payments;
    }

    public void setOnItemClickListener( CashierPaymentAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblPaymentPrice;
        public TextView lblPaymentDate;
        public TextView lblPaymentMethod;
        public ImageButton btnShowPanelDeletePayment;

        public ViewHolder( View itemView, final CashierPaymentAdapter.OnItemClickListener listener ){
            super( itemView );

            lblPaymentPrice = ( TextView )itemView.findViewById( R.id.lblPaymentPrice );
            lblPaymentDate = ( TextView )itemView.findViewById( R.id.lblPaymentDate );
            lblPaymentMethod = ( TextView )itemView.findViewById( R.id.lblPaymentMethod );
            btnShowPanelDeletePayment = ( ImageButton )itemView.findViewById( R.id.btnShowPanelDeletePayment );
            btnShowPanelDeletePayment.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onDeleteClick( position, btnShowPanelDeletePayment );
                        }
                    }
                }

            } );
        }

    }

    @Override
    public CashierPaymentAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.cashier_payment_row, parent, false );
        CashierPaymentAdapter.ViewHolder evh = new CashierPaymentAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( CashierPaymentAdapter.ViewHolder holder, int position ){
        /* Variables */
        String methods[] = { "Efectivo", "Tarjeta" };
        int method;
        /* Code start */
        PaymentSrv payment = payments.get( position );
        holder.lblPaymentPrice.setText( "$ " + payment.getPrice() );
        holder.lblPaymentDate.setText( payment.getDate() );
        method = payment.getMethod() - 1;
        holder.lblPaymentMethod.setText( methods[method] );
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

}
