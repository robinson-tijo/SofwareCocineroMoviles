package com.example.softwarecocinero.views.administrator.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarecocinero.R;
import com.example.softwarecocinero.services.CategorySrv;

import java.util.List;

public class AdministratorCategoryAdapter extends RecyclerView.Adapter<AdministratorCategoryAdapter.ViewHolder> {

    private List<CategorySrv> categories;
    private AdministratorCategoryAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick( int position, View view );
        void onUpdateClick (int position );
    }

    public AdministratorCategoryAdapter( List<CategorySrv> categories ){
        this.categories = categories;
    }

    public void setOnItemClickListener( AdministratorCategoryAdapter.OnItemClickListener listener ){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblCategoryName;
        public ImageButton btnShowPanelDeleteCategory;
        public ImageButton btnShowPanelUpdateCategory;

        public ViewHolder( View itemView, final AdministratorCategoryAdapter.OnItemClickListener listener ){
            super( itemView );

            lblCategoryName = (TextView) itemView.findViewById( R.id.lblCategoryName );
            btnShowPanelDeleteCategory = (ImageButton) itemView.findViewById(R.id.btnShowPanelDeleteCategory );
            btnShowPanelDeleteCategory.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    /* Variables */
                    int position;
                    /* Code start */
                    if (listener != null) {
                        position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position, btnShowPanelDeleteCategory);
                        }
                    }
                }

            } );
            btnShowPanelUpdateCategory = (ImageButton) itemView.findViewById(R.id.btnShowPanelUpdateCategory);
            btnShowPanelUpdateCategory.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick( View v ){
                    /* Variables */
                    int position;
                    /* Code start */
                    if( listener != null ){
                        position = getAdapterPosition();
                        if( position != RecyclerView.NO_POSITION ){
                            listener.onUpdateClick( position );
                        }
                    }
                }

            } );
        }

    }

    @Override
    public AdministratorCategoryAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ){
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.administrator_category_row, parent, false );
        AdministratorCategoryAdapter.ViewHolder evh = new AdministratorCategoryAdapter.ViewHolder( v, listener );
        return evh;
    }

    @Override
    public void onBindViewHolder( AdministratorCategoryAdapter.ViewHolder holder, int position ){
        CategorySrv category = categories.get( position );
        holder.lblCategoryName.setText( category.getName() );
    }

    @Override
    public int getItemCount(){
        return categories.size();
    }
}
