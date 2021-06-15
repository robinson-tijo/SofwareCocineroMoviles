package com.example.softwarecocinero.views.administrator.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.ProductSrv;


import java.util.List;

public class AdministratorProductAdapter extends RecyclerView.Adapter<AdministratorProductAdapter.ViewHolder>  {

    private List<ProductSrv> products;
    private AdministratorProductAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
    }

    public AdministratorProductAdapter( List<ProductSrv> products ){
        this.products = products;
    }

    public void setOnItemClickListener( AdministratorProductAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblProductName;
        public TextView lblProductPrice;
        public  TextView lblProductTimePreparation;
        public ImageButton btnShowPanelDeleteProduct;
        public ImageButton btnShowPanelUpdateProduct;

        public ViewHolder( View itemView, final AdministratorProductAdapter.OnItemClickListener listener ){
            super( itemView );

            lblProductName = ( TextView )itemView.findViewById( R.id.lblProductName );
            lblProductPrice = ( TextView )itemView.findViewById( R.id.lblProductPrice );
            lblProductTimePreparation = ( TextView )itemView.findViewById( R.id.lblProductTimePreparation );
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

    @Override
    public AdministratorProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.administrator_product_row, parent, false );
        AdministratorProductAdapter.ViewHolder evh = new AdministratorProductAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( AdministratorProductAdapter.ViewHolder holder, int position ){
        ProductSrv product = products.get( position );
        holder.lblProductName.setText( product.getName() );
        holder.lblProductPrice.setText( "$ " + product.getPrice() );
        holder.lblProductTimePreparation.setText( "Tiempo de preparacion: " + product.getPreparationTime() + " min ");
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}