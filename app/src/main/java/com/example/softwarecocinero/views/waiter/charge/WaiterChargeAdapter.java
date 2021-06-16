package com.example.softwarecocinero.views.waiter.charge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.ChargeSrv;

import java.util.ArrayList;
import java.util.List;

public class WaiterChargeAdapter extends RecyclerView.Adapter<WaiterChargeAdapter.ViewHolder>  {

    private List<ChargeSrv> charges;
    private WaiterChargeAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
    }

    public WaiterChargeAdapter(){
        charges = new ArrayList<>();
    }

    public void setOnItemClickListener( WaiterChargeAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblProductName;
        public TextView lblProductPrice;
        public  TextView lblChargeDescription;
        public  TextView lblChargeState;
        public ImageButton btnShowPanelDeleteProduct;
        public ImageButton btnShowPanelUpdateProduct;

        public ViewHolder( View itemView, final WaiterChargeAdapter.OnItemClickListener listener ){
            super( itemView );

            lblProductName = ( TextView )itemView.findViewById( R.id.lblProductName );
            lblProductPrice = ( TextView )itemView.findViewById( R.id.lblProductPrice );
            lblChargeState = ( TextView )itemView.findViewById( R.id.lblChargeState );
            lblChargeDescription = ( TextView )itemView.findViewById( R.id.lblChargeDescription );
            btnShowPanelDeleteProduct= ( ImageButton )itemView.findViewById( R.id.btnShowPanelDeleteProduct );
            btnShowPanelDeleteProduct.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onDeleteClick( position, btnShowPanelDeleteProduct );
                        }
                    }
                }

            } );
            btnShowPanelUpdateProduct = ( ImageButton )itemView.findViewById( R.id.btnShowPanelUpdateProduct );
            btnShowPanelUpdateProduct.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ){
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onUpdateClick( position );
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
    public WaiterChargeAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.waiter_charge_row, parent, false );
        WaiterChargeAdapter.ViewHolder evh = new WaiterChargeAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( WaiterChargeAdapter.ViewHolder holder, int position ){
        /* Variables */
        String states[] = { "En espera", "Eliminado", "Preparándose", "Terminado" };
        int colors[] = { 0xffd9534f, 0xffe41b23, 0xfff0ad4e, 0xff5cb85c };
        int state;
        int color;
        /* Code start */
        ChargeSrv charge = charges.get( position );
        holder.lblProductName.setText( charge.getProduct().getName() + " x " + charge.getAmount() );
        holder.lblProductPrice.setText( "$ " + charge.getProduct().getPrice() );
        holder.lblChargeDescription.setText( charge.getDescription() );
        state = charge.getState() - 1;
        holder.lblChargeState.setText( states[state] );
        color = colors[state];
        holder.lblChargeState.setTextColor( color );
    }

    @Override
    public int getItemCount() {
        return charges.size();
    }

}
