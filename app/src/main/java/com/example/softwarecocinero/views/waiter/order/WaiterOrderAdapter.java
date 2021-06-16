package com.example.softwarecocinero.views.waiter.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.OrderSrv;

import java.util.ArrayList;
import java.util.List;

public class WaiterOrderAdapter extends RecyclerView.Adapter<WaiterOrderAdapter.ViewHolder>  {

    private List<OrderSrv> orders;
    private WaiterOrderAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick( int position );
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
    }

    public WaiterOrderAdapter(){
        this.orders = new ArrayList<>();
    }

    public void setOnItemClickListener( WaiterOrderAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblOrderTable;
        public ImageButton btnShowPanelDeleteOrder;
        public ImageButton btnShowPanelUpdateOrder;

        public ViewHolder( View itemView, final WaiterOrderAdapter.OnItemClickListener listener ){
            super( itemView );

            lblOrderTable = ( TextView )itemView.findViewById( R.id.lblOrderTable );
            btnShowPanelDeleteOrder = ( ImageButton )itemView.findViewById( R.id.btnShowPanelDeleteOrder );
            btnShowPanelDeleteOrder.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onDeleteClick( position, btnShowPanelDeleteOrder );
                        }
                    }
                }

            } );
            btnShowPanelUpdateOrder = ( ImageButton )itemView.findViewById( R.id.btnShowPanelUpdateOrder );
            btnShowPanelUpdateOrder.setOnClickListener( new View.OnClickListener() {

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
            itemView.setOnClickListener( new View.OnClickListener(){

                @Override
                public void onClick( View v ){
                    if( listener != null ){
                        int position = getAdapterPosition();
                        if( position != RecyclerView.NO_POSITION ){
                            listener.onItemClick(position);
                        }
                    }
                }

            } );
        }

    }

    public void update( List<OrderSrv> orders ){
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public WaiterOrderAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.waiter_order_row, parent, false );
        WaiterOrderAdapter.ViewHolder evh = new WaiterOrderAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( WaiterOrderAdapter.ViewHolder holder, int position ){
        OrderSrv order = orders.get( position );
        holder.lblOrderTable.setText( "Mesa " + order.getTable().getId() );
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

}
