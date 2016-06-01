package com.matic.lugarenelbar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matic.lugarenelbar.adapters.PromocionAdapter;
import com.matic.lugarenelbar.models.Promocion;
import com.matic.lugarenelbar.taskWebService.promociones.TareaWSListarPromociones;
import com.matic.lugarenelbar.taskWebService.promociones.TareaWSListarPromocionesXidBar;
import com.matic.lugarenelbar.utils.PromocionComparatorPrecio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.CALL_PHONE;

public class DatosBaresActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CALL_PHONE = 0;

    private TextView tvnombre;
    private TextView tvcalle;
    private TextView tvnrocalle;
    private TextView tvmesasLibres;
    private TextView tvtelefono;
    private ImageView tvimagen;
    private LatLng coordenadas_entidad;
    private Button btelefono;

    private String logo;

    private ProgressDialog pd;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private ArrayList<Promocion> listaPromocionesXidBar;

    private Bitmap bm;
    private SupportMapFragment mapFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_bares);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recibirDatos();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

//        mapFragment.getMapAsync(this);


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("info");
        tabSpec.setContent(R.id.layout_info);
        tabSpec.setIndicator("Información");
        tabHost.addTab(tabSpec);




        tabSpec = tabHost.newTabSpec("promo");
        tabSpec.setContent(R.id.layout_promociones);
        tabSpec.setIndicator("Promociones");
        tabHost.addTab(tabSpec);



        if(tabSpec.getTag().equals("promo"))
        {
            cargarPromociones();
        }

        tabSpec = tabHost.newTabSpec("comentarios");
        tabSpec.setContent(R.id.layout_comentarios);
        tabSpec.setIndicator("Comentarios");
        tabHost.addTab(tabSpec);



        if(tabSpec.getTag().equals("comentarios"))
        {
            cargarComentarios();
        }


        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(12);
        }

        Log.i("TAG TABHOST","count: "+tabHost.getTabWidget().getChildCount());





    }



    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(!logo.equals("null"))
        {

        map.addMarker(new MarkerOptions().position(coordenadas_entidad).title(tvnombre.getText().toString()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(bm))));
        }
        else{
            map.addMarker(new MarkerOptions().position(coordenadas_entidad).title(tvnombre.getText().toString()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerResource(R.drawable.logo_default))));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas_entidad, 15f));

    }

    public void recibirDatos() {

        String nombre = getIntent().getStringExtra("nombreLegal");
        String calle = getIntent().getStringExtra("calle");
        String nroCalle = getIntent().getStringExtra("nroCalle");
        String telefono = getIntent().getStringExtra("telefono");
        double lat = Double.parseDouble(getIntent().getStringExtra("latitud"));
        double lon = Double.parseDouble(getIntent().getStringExtra("longitud"));
        String mesasLibres=getIntent().getStringExtra("mesasLibres");
        logo = getIntent().getStringExtra("url_logo");

        Log.i("TAG LOGO","logo: "+logo);


        new loadImg(){
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                if(bitmap!=null){
                    bm=bitmap;

                    tvimagen.setImageBitmap(bm);

                }else{


                    tvimagen.setBackgroundResource(R.drawable.logo_default);

                }


                mapFragment.getMapAsync(DatosBaresActivity.this);

            }
        }.execute(logo);



        coordenadas_entidad = new LatLng(lat, lon);

        tvnombre = (TextView) findViewById(R.id.txt_d_nombre);
        tvcalle = (TextView) findViewById(R.id.txt_d_calle);
        tvnrocalle = (TextView) findViewById(R.id.txt_d_nrocalle);
        tvmesasLibres= (TextView) findViewById(R.id.txt_mesas_libres);

        tvimagen = (ImageView) findViewById(R.id.d_imagen);
        btelefono = (Button) findViewById(R.id.btn_call);


        tvnombre.setText(nombre);
        tvcalle.setText(calle);
        tvnrocalle.setText(nroCalle);
        tvmesasLibres.setText(mesasLibres);


        btelefono.setText("Llamar " + telefono);

    }



    private class loadImg extends AsyncTask<String,Integer,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm= null;
            if (!params.equals("null")){

                try {
                    bm = Glide.with(DatosBaresActivity.this).load(params[0]).asBitmap().into(-1,-1).get();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return bm;
            }else{return null;}

        }
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }






    public void cargarPromociones() {

        //aca tiene q llamar a promociones por idBar
        new TareaWSListarPromociones() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // pd = ProgressDialog.show(DatosBaresActivity.this, "", getString(R.string.pd_message_promociones));
            }

            @Override
            protected void onPostExecute(ArrayList<Promocion> result) {
                super.onPostExecute(result);

               // if (pd.isShowing()) {
               //     pd.dismiss();}

                listaPromocionesXidBar = new ArrayList<>(); //cargar lista con el json del ws

                listaPromocionesXidBar = result;

                Collections.sort(listaPromocionesXidBar, new PromocionComparatorPrecio());

                recycler = (RecyclerView) findViewById(R.id.recicladorPromocionesXidBar);
                recycler.setHasFixedSize(true);

                // Usar un administrador para LinearLayout
                lManager = new LinearLayoutManager(DatosBaresActivity.this);
                recycler.setLayoutManager(lManager);

                // Crear un nuevo adaptador
                adapter = new PromocionAdapter(listaPromocionesXidBar);
                recycler.setAdapter(adapter);

            }
        }.execute();

    }


    private void cargarComentarios() {
    }



    private Bitmap getMarkerBitmap(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_view_marker_bar, null);

        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageBitmap(bitmap);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerResource(@DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_view_marker_bar, null);

        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }











//    public void callEntity(View view) {
//        //para pedir los permisos
//        populateAutoComplete();
//
//        String name=tvnombre.getText().toString();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.confirm_call));
//        builder.setMessage(getString(R.string.call_message)+" "+name+"?")
//                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        String tel = tvtelefono.getText().toString().trim();
//
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
//                        if (ActivityCompat.checkSelfPermission(DatosBaresActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//                        startActivity(intent);
//
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//      }
//
//    private boolean myRequestCall() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
//            Snackbar.make(null, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
//        }
//        return false;
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CALL_PHONE) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            }
//        }
//    }
//
//    private void populateAutoComplete() {
//        if (!myRequestCall()) {
//            return;
//        }
//
//    }
//
//    public void sendEmail(View view) {
//
//        if(!email.equals("null")) {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/html");
//            intent.putExtra(Intent.EXTRA_EMAIL, email);
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Enviado desde CallOliva");
//            startActivity(Intent.createChooser(intent, "Enviar Email"));
//        }
//        else{
//            Toast.makeText(this,getString(R.string.message_error_email),Toast.LENGTH_SHORT).show();
//        }
//
//    }

}