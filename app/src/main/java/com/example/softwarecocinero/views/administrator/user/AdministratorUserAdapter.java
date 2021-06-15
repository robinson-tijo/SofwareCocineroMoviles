package com.example.softwarecocinero.views.administrator.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.UserSrv;

import java.util.List;

public class AdministratorUserAdapter extends RecyclerView.Adapter<AdministratorUserAdapter.ViewHolder>  {

    private List<UserSrv> users;
    private AdministratorUserAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick( int position );
    }

    public AdministratorUserAdapter( List<UserSrv> users ){
        this.users = users;
    }

    public void setOnItemClickListener( AdministratorUserAdapter.OnItemClickListener listener ) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblUserName;
        public TextView lblUserRol;
        public ImageButton btnShowPanelDeleteUser;
        public ImageButton btnShowPanelUpdateUser;

        public ViewHolder( View itemView, final AdministratorUserAdapter.OnItemClickListener listener ){
            super( itemView );

            lblUserName = ( TextView )itemView.findViewById( R.id.lblUserName );
            lblUserRol = ( TextView )itemView.findViewById( R.id.lblUserRol );
            btnShowPanelDeleteUser = ( ImageButton )itemView.findViewById( R.id.btnShowPanelDeleteUser );
            btnShowPanelDeleteUser.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if ( position != RecyclerView.NO_POSITION ){
                            listener.onDeleteClick( position, btnShowPanelDeleteUser);
                        }
                    }
                }

            } );
            btnShowPanelUpdateUser = ( ImageButton )itemView.findViewById( R.id.btnShowPanelUpdateUser );
            btnShowPanelUpdateUser.setOnClickListener(new View.OnClickListener() {

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
    public AdministratorUserAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.administrator_user_row, parent, false );
        AdministratorUserAdapter.ViewHolder evh = new AdministratorUserAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( AdministratorUserAdapter.ViewHolder holder, int position ){
        /* Variables */
        String roles[] = { "Administrador", "Cajero", "Cocinero", "Mesero" };
        String rol;
        /* Code start */
        UserSrv user = users.get( position );
        holder.lblUserName.setText( user.getName() );
        rol = roles[( user.getRol() - 1 )];
        holder.lblUserRol.setText( rol );
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
