package com.example.softwarecocinero.views.administrator.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.TableSrv;

import java.util.List;

public class AdministratorTableAdapter extends RecyclerView.Adapter<AdministratorTableAdapter.ViewHolder> {

    private List<TableSrv> tables;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
    }

    public AdministratorTableAdapter( List<TableSrv> tables ){
        this.tables = tables;
    }

    public void setOnItemClickListener( OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblTableNumber;
        public TextView lblTableDescription;
        public ImageButton btnShowPanelDeleteTable;
        public ImageButton btnShowPanelUpdateTable;

        public ViewHolder( View itemView, final OnItemClickListener listener ){
            super( itemView );

            lblTableDescription = ( TextView )itemView.findViewById( R.id.lblTableDescription );
            lblTableNumber = ( TextView )itemView.findViewById( R.id.lblTableNumber );
            btnShowPanelDeleteTable = ( ImageButton )itemView.findViewById( R.id.btnShowPanelDeleteTable );
            btnShowPanelDeleteTable.setOnClickListener( new View.OnClickListener(){

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onDeleteClick( position, btnShowPanelDeleteTable );
                        }
                    }
                }

            } );
            btnShowPanelUpdateTable = ( ImageButton )itemView.findViewById( R.id.btnShowPanelUpdateTable );
            btnShowPanelUpdateTable.setOnClickListener( new View.OnClickListener() {

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
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.administrator_table_row, parent, false );
        AdministratorTableAdapter.ViewHolder evh = new AdministratorTableAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position ){
        TableSrv table = tables.get( position );
        holder.lblTableNumber.setText( "Mesa " + table.getId() );
        holder.lblTableDescription.setText( table.getDescription() );
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

}
