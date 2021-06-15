package com.example.softwarecocinero.views.administrator.area;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.AreaSrv;

import java.util.List;

public class AdministratorAreaAdapter extends RecyclerView.Adapter<AdministratorAreaAdapter.ViewHolder>  {

    private List<AreaSrv> areas;
    private AdministratorAreaAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
        void onCategoryClick( int position );
    }

    public AdministratorAreaAdapter( List<AreaSrv> areas ){
        this.areas = areas;
    }

    public void setOnItemClickListener( AdministratorAreaAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblAreaName;
        public ImageButton btnShowPanelDeleteArea;
        public ImageButton btnShowPanelUpdateArea;
        public ImageButton btnShowPanelCategory;

        public ViewHolder( View itemView, final AdministratorAreaAdapter.OnItemClickListener listener ){
            super( itemView );

            lblAreaName = ( TextView )itemView.findViewById( R.id.lblAreaName );
            btnShowPanelDeleteArea = ( ImageButton )itemView.findViewById( R.id.btnShowPanelDeleteArea );
            btnShowPanelDeleteArea.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onDeleteClick( position, btnShowPanelDeleteArea );
                        }
                    }
                }

            } );
            btnShowPanelUpdateArea = ( ImageButton )itemView.findViewById( R.id.btnShowPanelUpdateArea );
            btnShowPanelUpdateArea.setOnClickListener( new View.OnClickListener() {

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
            btnShowPanelCategory = ( ImageButton )itemView.findViewById( R.id.btnShowPanelCategory );
            btnShowPanelCategory.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ){
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onCategoryClick( position );
                        }
                    }
                }

            } );
        }

    }

    @Override
    public AdministratorAreaAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.administrator_area_row, parent, false );
        AdministratorAreaAdapter.ViewHolder evh = new AdministratorAreaAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( AdministratorAreaAdapter.ViewHolder holder, int position ){
        AreaSrv area = areas.get( position );
        holder.lblAreaName.setText( area.getName() );
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

}
