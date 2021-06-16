package com.example.softwarecocinero.views.cashier.charge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.ChargeSrv;

import java.util.ArrayList;
import java.util.List;

public class CashierChargeAdapter extends RecyclerView.Adapter<CashierChargeAdapter.ViewHolder>  {

    private List<ChargeSrv> charges;
    private CashierChargeAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick( int position );
    }

    public CashierChargeAdapter(){
        this.charges = new ArrayList<>();
    }

    public void setOnItemClickListener( CashierChargeAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblProductName;
        public TextView lblProductPrice;
        public  TextView lblChargeDescription;
        public CardView productRow;

        public ViewHolder( View itemView, final CashierChargeAdapter.OnItemClickListener listener ){
            super( itemView );

            lblProductName = ( TextView )itemView.findViewById( R.id.lblProductName );
            lblProductPrice = ( TextView )itemView.findViewById( R.id.lblProductPrice );
            lblChargeDescription = ( TextView )itemView.findViewById( R.id.lblChargeDescription );
            productRow = ( CardView )itemView.findViewById( R.id.productRow );
            itemView.setOnClickListener( new View.OnClickListener(){

                @Override
                public void onClick( View v ){
                    if( listener != null ){
                        int position = getAdapterPosition();
                        if( position != RecyclerView.NO_POSITION ){
                            listener.onItemClick( position );
                        }
                    }
                }

            } );
        }

    }

    public void update( List<ChargeSrv> charges ){
        this.charges = charges;
        notifyDataSetChanged();
    }

    @Override
    public CashierChargeAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.cashier_charge_row, parent, false );
        CashierChargeAdapter.ViewHolder evh = new CashierChargeAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( CashierChargeAdapter.ViewHolder holder, int position ){
        /* Variables */
        ChargeSrv charge;
        int state;
        /* Code start */
        charge = charges.get( position );
        state = charge.getState();
        holder.lblProductName.setText( charge.getProduct().getName() + " x " + charge.getAmount() );
        holder.lblProductPrice.setText( "$ " + charge.getProduct().getPrice() );
        holder.lblChargeDescription.setText( charge.getDescription() );
        if( state == 9 ){
            holder.productRow.setCardBackgroundColor( 0xffe0fbfc );
        }else{
            holder.productRow.setCardBackgroundColor( 0xffffffff );
        }
    }

    @Override
    public int getItemCount() {
        return charges.size();
    }

}
