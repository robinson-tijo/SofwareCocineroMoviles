package com.example.softwarecocinero.views.waiter.product;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.ChargeSrv;
import com.example.softwarecocinero.services.ProductSrv;

import java.util.ArrayList;
import java.util.List;

public class WaiterProductAdapter extends RecyclerView.Adapter<WaiterProductAdapter.ViewHolder>  {

    private List<ProductSrv> products;
    private List<ChargeSrv> charges;
    private WaiterProductAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick( int position );
        void onAddClick( int position );
        void onRemoveClick( int position );
    }

    public WaiterProductAdapter(){
        this.products = new ArrayList<>();
        this.charges = new ArrayList<>();
    }

    public void setOnItemClickListener( WaiterProductAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblProductName;
        public TextView lblProductPrice;
        // public TextView lblProductTimePreparation;
        public LinearLayout pnlChargeAmount;
        public TextView lblChargeAmount;
        public ImageButton btnAddProductAmount;
        public ImageButton btnRemoveProductAmount;
        public CardView productRow;

        public ViewHolder( View itemView, final WaiterProductAdapter.OnItemClickListener listener ){
            super( itemView );

            lblProductName = ( TextView )itemView.findViewById( R.id.lblProductName );
            lblProductPrice = ( TextView )itemView.findViewById( R.id.lblProductPrice );
            // lblProductTimePreparation = ( TextView )itemView.findViewById( R.id.lblProductTimePreparation );
            lblChargeAmount = ( TextView )itemView.findViewById( R.id.lblChargeAmount );
            pnlChargeAmount = ( LinearLayout )itemView.findViewById( R.id.pnlChargeAmount );
            productRow = ( CardView )itemView.findViewById( R.id.productRow );
            btnAddProductAmount = ( ImageButton )itemView.findViewById( R.id.btnAddProductAmount );
            btnAddProductAmount.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onAddClick( position );
                        }
                    }
                }

            } );
            btnRemoveProductAmount = ( ImageButton )itemView.findViewById( R.id.btnRemoveProductAmount );
            btnRemoveProductAmount.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ){
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onRemoveClick( position );
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

    public void update( List<ProductSrv> products, List<ChargeSrv> charges ){
        this.products = products;
        this.charges = charges;
        notifyDataSetChanged();
    }

    @Override
    public WaiterProductAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.waiter_product_row, parent, false );
        WaiterProductAdapter.ViewHolder evh = new WaiterProductAdapter.ViewHolder( v, listener );
        return evh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder( WaiterProductAdapter.ViewHolder holder, int position ){
        /* Variables */
        int size;
        boolean exits;
        int i;
        int index;
        int amount;
        /* Code start */
        size = charges.size();
        exits = false;
        i = 0;
        index = 0;
        ProductSrv product = products.get( position );
        holder.lblProductName.setText( product.getName() );
        // holder.lblProductTimePreparation.setText( "Tiempo de preparación : " + product.getPreparationTime() + " min" );
        holder.lblProductPrice.setText( "$ " + product.getPrice() );
        while( !exits && i < size ){
            if( charges.get( i ).getProduct().getId() == product.getId() ){
                exits = true;
                index = i;
            }
            i++;
        }
        if( exits ){
            amount = charges.get( index ).getAmount();
            holder.productRow.setCardBackgroundColor( 0xffe0fbfc );
            holder.pnlChargeAmount.setVisibility( View.VISIBLE );
            holder.lblChargeAmount.setText( "x " + amount );
        }else{
            holder.productRow.setCardBackgroundColor( 0xffffffff );
            holder.pnlChargeAmount.setVisibility( View.INVISIBLE );
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
