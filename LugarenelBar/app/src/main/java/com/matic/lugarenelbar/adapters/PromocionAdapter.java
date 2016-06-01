package com.matic.lugarenelbar.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.matic.lugarenelbar.R;
import com.matic.lugarenelbar.models.Bar;
import com.matic.lugarenelbar.models.Promocion;
import com.matic.lugarenelbar.volley.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by Guillermo on 5/25/2016.
 */
public class PromocionAdapter extends RecyclerView.Adapter<PromocionAdapter.ViewHolderPromocion> {

    private ArrayList<Promocion> listaPromociones;
    private LayoutInflater layoutInflater;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    public static class ViewHolderPromocion extends RecyclerView.ViewHolder {

        private ImageView imgBar;
        private TextView nombrePromo;
        private TextView descripcion;
        private TextView precio;
        private TextView nombreBar;

        public ViewHolderPromocion(View v) {
            super(v);

            imgBar = (ImageView) v.findViewById(R.id.imagenPromo);
            nombrePromo = (TextView) v.findViewById(R.id.nombrePromo);
            descripcion = (TextView) v.findViewById(R.id.descrip_promocion);
            precio = (TextView) v.findViewById(R.id.precio_promocion);
            nombreBar = (TextView) v.findViewById(R.id.nombre_bar_promo);

        }

    }

    public PromocionAdapter(ArrayList<Promocion> listaPromociones) {
        this.listaPromociones = listaPromociones;

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();

    }

    @Override
    public ViewHolderPromocion onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_promociones, viewGroup, false);
        return new ViewHolderPromocion(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolderPromocion holder, int position) {

        Promocion currentPromocion = listaPromociones.get(position);

        holder.nombrePromo.setText(currentPromocion.getNombre());
        holder.descripcion.setText(currentPromocion.getDescripcion());
        holder.precio.setText(String.valueOf(currentPromocion.getPrecio()));
        holder.nombreBar.setText(currentPromocion.getBar().getNombreLegal());

        String url = currentPromocion.getBar().getLogo();

        if (!url.toString().equals("null")) {

            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    holder.imgBar.setImageBitmap(response.getBitmap());

                }
            });

        } else {
            holder.imgBar.setImageResource(R.drawable.logo_default);
        }

    }

    @Override
    public int getItemCount() {
        return listaPromociones.size();
    }

}

