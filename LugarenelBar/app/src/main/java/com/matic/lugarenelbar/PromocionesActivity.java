package com.matic.lugarenelbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.matic.lugarenelbar.adapters.BarAdapter;
import com.matic.lugarenelbar.adapters.PromocionAdapter;
import com.matic.lugarenelbar.models.Bar;
import com.matic.lugarenelbar.models.Promocion;
import com.matic.lugarenelbar.taskWebService.promociones.TareaWSListarPromociones;
import com.matic.lugarenelbar.utils.PromocionComparatorPrecio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PromocionesActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private ArrayList<Promocion> listaPromociones;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cargarPromociones();
    }

    public void cargarPromociones() {

        new TareaWSListarPromociones() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = ProgressDialog.show(PromocionesActivity.this, "", getString(R.string.pd_message_promociones));
            }

            @Override
            protected void onPostExecute(ArrayList<Promocion> result) {
                super.onPostExecute(result);

                if (pd.isShowing()) {
                    pd.dismiss();
                }

                listaPromociones = new ArrayList<>(); //cargar lista con el json del ws

                listaPromociones = result;

                Collections.sort(listaPromociones, new PromocionComparatorPrecio());

                recycler = (RecyclerView) findViewById(R.id.recicladorPromociones);
                recycler.setHasFixedSize(true);

                // Usar un administrador para LinearLayout
                lManager = new LinearLayoutManager(PromocionesActivity.this);
                recycler.setLayoutManager(lManager);

                // Crear un nuevo adaptador
                adapter = new PromocionAdapter(listaPromociones);
                recycler.setAdapter(adapter);

            }
        }.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






}
