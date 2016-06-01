package com.matic.lugarenelbar.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.matic.lugarenelbar.DatosBaresActivity;
import com.matic.lugarenelbar.R;
import com.matic.lugarenelbar.models.Bar;
import com.matic.lugarenelbar.volley.VolleySingleton;

import java.util.ArrayList;


public class BarAdapter extends RecyclerView.Adapter<BarAdapter.ViewHolderBar> {

    private ArrayList<Bar> listaBares =new ArrayList<>();
    private LayoutInflater layoutInflater;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private Context context;

    public static class ViewHolderBar extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView img;
        private TextView nombreLegal;
        private TextView calle;
        private TextView nCalle;
        private TextView tel;
        private RatingBar rating;

        ArrayList<Bar> bares = new ArrayList<Bar>();
        Context context;


        public ViewHolderBar(View v, Context context, ArrayList<Bar> bares) {
            super(v);
            this.bares = bares;
            this.context=context;
            img = (ImageView) v.findViewById(R.id.imagen);
            nombreLegal = (TextView) v.findViewById(R.id.nombreLegal);
            calle = (TextView) v.findViewById(R.id.calle);
            nCalle = (TextView) v.findViewById(R.id.nrocalle);
            tel = (TextView) v.findViewById(R.id.telefono);

            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position=getAdapterPosition();
            Bar bar=this.bares.get(position);


            Intent act=new Intent(context, DatosBaresActivity.class);
            act.putExtra("url_logo",bar.getLogo());
            act.putExtra("nombreLegal",bar.getNombreLegal());
            act.putExtra("calle",bar.getCalle());
            act.putExtra("nroCalle",String.valueOf(bar.getNroCalle()));
            act.putExtra("latitud",String.valueOf(bar.getLat()));
            act.putExtra("longitud",String.valueOf(bar.getLon()));
            act.putExtra("telefono",bar.getTelefono());
            act.putExtra("mesasLibres",String.valueOf(bar.getMesasLibres()));
            this.context.startActivity(act);



        }
    }

    public BarAdapter(Context context, ArrayList<Bar> listaBares) {
        this.listaBares = listaBares;
        this.context = context;

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();

    }

    @Override
    public ViewHolderBar onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_bares, viewGroup, false);
        return new ViewHolderBar(v,context,listaBares);
    }


    @Override
    public void onBindViewHolder(final ViewHolderBar holder, int position) {


        Bar currentBar = listaBares.get(position);
        holder.nombreLegal.setText(currentBar.getNombreLegal());
        holder.calle.setText(currentBar.getCalle());
        holder.nCalle.setText(String.valueOf(currentBar.getNroCalle()));
        holder.tel.setText(currentBar.getTelefono());

        String url = currentBar.getLogo();

        if (!url.toString().equals("null")) {

            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    holder.img.setImageBitmap(response.getBitmap());

                }
            });

        } else {
            holder.img.setImageResource(R.drawable.logo_default);
        }

    }

    @Override
    public int getItemCount() {

        return listaBares.size();
    }

}
