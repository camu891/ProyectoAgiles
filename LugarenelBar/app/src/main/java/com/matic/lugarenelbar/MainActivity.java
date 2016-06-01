package com.matic.lugarenelbar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matic.lugarenelbar.taskWebService.bares.TareaWSEliminar;
import com.matic.lugarenelbar.taskWebService.bares.TareaWSListarDisponibles;
import com.matic.lugarenelbar.taskWebService.bares.TareaWSListar;
import com.matic.lugarenelbar.utils.Constants;
import com.matic.lugarenelbar.com.matic.lugarenelbar.utils.MapWrapperLayout;
import com.matic.lugarenelbar.com.matic.lugarenelbar.utils.OnInfoWindowElemTouchListener;
import com.matic.lugarenelbar.models.Bar;
import com.matic.lugarenelbar.utils.LoggedAccount;
import com.matic.lugarenelbar.volley.VolleySingleton;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private ViewGroup myContentsView;

    private final int DURACION = 3 * 1000; // 3 segundos

    private static final int GPS_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mapa;
    private SupportMapFragment apmap;
    private Marker markerPosicionActual;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;

    private FloatingActionButton fabinfo;
    private FloatingActionButton fabmilocation;
    private ProgressDialog pd = null;
    private LinearLayout layoutInfo;
    private LinearLayout layoutradiobutton;
    private RadioButton rb_menora;

    Animation animationfadein;

    List<Bar> listaBares;
    List<Bar> listaBaresDisponibles;

    private static double current_latitud = 0.0;
    private static double current_longitud = 0.0;
    private double distancia;
    private static final Double RadioH = 6371.00;
    private int item_selection = 1;
    public static int valor_rango = 1000;
    private int markerImg = 0;

    //variables para infowindowsadapert(infomarcador)
    private String snippettel = "";
    private String estado = "";

    //para obtener la ubicacion
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    //boolean para saber si hace falta hacer el primer zoom o si no hay que seguir moviendo la camara
    private boolean cameraMoved = false;

    //para el boton Borrar en los infoWindow
    private MapWrapperLayout mapWrapperLayout;

    //array de bitmaps descargados
    private Bitmap[] bitmap;
    private Bitmap bitmapAll;
    private Bitmap[] bitmapDisponibles;

    //carga de imagenes
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    //creado para cerrar la actividad luego de crear un bar
    public static Activity mainScreen;

    //para el loader inicial
    private AnimationDrawable loadingViewAnim = null;
    private TextView loadigText = null;
    private ImageView loadigIcon = null;
    private LinearLayout loadingLayout = null;

    private String _nombreLegal;
    private String _nombreFantasia;
    private String _pais;
    private String _ciudad;
    private String _calle;
    private int _nroCalle;
    private double _lat;
    private double _lon;
    private String _telefono;
    private String _logo;
    private int _mesasLibres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainScreen = this;

        configGPS();

        //en lugar de progress bar vamos a usar un loader customizado
        cargarLoader();

        //trae los datos del webservice
        pedirDatosWS();

        layoutInfo = (LinearLayout) findViewById(R.id.layout_view_info);
        layoutradiobutton = (LinearLayout) findViewById(R.id.linearLayout_radiobutton);
        rb_menora = (RadioButton) findViewById(R.id.rbmenora);
        rb_menora.setChecked(true);

        animationfadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        //GoogleMaps
        cargarGoogleMaps();

        //FLOATING BUTTON INFO
        fabinfo = (FloatingActionButton) findViewById(R.id.fab_info);
        fabinfo.setVisibility(View.INVISIBLE);
        fabinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOcultarInfo();
            }
        });

        fabmilocation = (FloatingActionButton) findViewById(R.id.fab_milocation);
        fabmilocation.setVisibility(View.INVISIBLE);
        fabmilocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myLocation();
            }
        });

        //CUANDO LE HAGO CLICK A UN MARCADOR ROJO O VERDE
        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Cuando toco el marcador seteo las  nuevas coordenadas para calcular las distancia
                ocultarLayouts();
                //return false;

                centerWindowsInfo(marker);
                return true;

            }

        });

//        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                ocultarLayouts();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void cargarLoader() {

        loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
        loadigText = (TextView) findViewById(R.id.txt_loader);
        loadigIcon = (ImageView) findViewById(R.id.img_loader);
        loadigIcon.setBackgroundResource(R.drawable.loading_animation);
        loadingViewAnim = (AnimationDrawable) loadigIcon.getBackground();
    }

    public void pedirDatosWS() {
        new TareaWSListar() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingLayout.setVisibility(View.VISIBLE);
                loadingViewAnim.start();
            }

            @Override
            protected void onPostExecute(LinkedList<Bar> result) {
                if (result != null) {
                    listaBares = result;

                    createAllMarkers();

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            // Cuando pasen los 3 segundos, refrescamos para que se carguen las imagenes

                            loadingLayout.setVisibility(View.GONE);
                            loadingViewAnim.stop();
                            apmap.getView().setVisibility(View.VISIBLE);
                            fabmilocation.setVisibility(View.VISIBLE);
                            createAllMarkers();

                            }
                    }, DURACION);



                    //new loadImg().execute();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.error_ws), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

        new TareaWSListarDisponibles() {
            @Override
            protected void onPostExecute(LinkedList<Bar> result) {
                if (result != null) {
                    listaBaresDisponibles = result;

                    //new loadImgDisponibles().execute();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.error_ws), Toast.LENGTH_LONG).show();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            }
        }.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Bundle extras = intent.getExtras();
        if (null == extras) return;
    }

    public void cargarGoogleMaps() {
        //Google maps Settings
        apmap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_container);

        apmap.getView().setVisibility(View.INVISIBLE);
        mapa = apmap.getMap();
        //Establecer tipo de mapa y activo mi ubicacion
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        myContentsView = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        this.infoButton = (Button) myContentsView.findViewById(R.id.button);

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(mapa, getPixelsFromDp(this, 39 + 20));

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.abc_list_pressed_holo_dark),
                getResources().getDrawable(R.drawable.abc_list_pressed_holo_light)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // variable local final para poder acceder al valor de marker desde dentro de la
                // clase anonima. Si no es final no se puede acceder desde una clase anonima.
                final Marker finalMarker = marker;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Está seguro que desea eliminar el bar?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                new TareaWSEliminar() {
                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        if (result) {
                                            Toast.makeText(MainActivity.this, "Bar eliminado correctamente!", Toast.LENGTH_SHORT).show();
                                            finalMarker.remove();

                                        }
                                    }
                                }.execute(finalMarker.getTitle());

                                // BackendComunication.getInstance().borrarBar(finalMarker.getPosition());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        mapa.setInfoWindowAdapter(new MyInfoWindowAdapter());
    }

    //NOTA: pedir los permisos en oncreate y luego ejecutar el config, VER COMO HACER ESO
    public void configGPS() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        //boolean para saber si hace falta hacer el primer zoom o si no hay que seguir moviendo la camara
        cameraMoved = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            handleNewLocation(location);
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_latitud, current_longitud), 14.0f));
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void handleNewLocation(Location location) {

        current_latitud = location.getLatitude();
        current_longitud = location.getLongitude();

        createMyLocationMarker();
    }

    public static LatLng getCoordenadasActuales() {
        LatLng latlon = new LatLng(current_latitud, current_longitud);
        return latlon;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(Constants.TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    //evento para crear un alertdialog que te lleva a activar el gps si esta desactivado
    public void dialogActivarGPS() {
        final AlertDialog.Builder abuilder = new AlertDialog.Builder(MainActivity.this);
        abuilder.setMessage(getString(R.string.dialog_gps_message)).setCancelable(false)
                .setPositiveButton(getString(R.string.pd_si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //POR EL LADO DEL SI
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_CODE);
                    }
                })
                .setNegativeButton(getString(R.string.pd_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });

        AlertDialog alert = abuilder.create();
        alert.setTitle(getString(R.string.dialog_gps_titulo));
        alert.show();
    }

    public static double calcularDistancias(double lat1, double long1, double lat2, double long2) {
        double radio = RadioH;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLong = Math.toRadians(long2 - long1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return radio * c;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (layoutInfo.getVisibility() == View.VISIBLE) {

            layoutInfo.setVisibility(View.INVISIBLE);
            layoutInfo.clearAnimation();
        } else if (layoutradiobutton.getVisibility() == View.VISIBLE) {

            layoutradiobutton.setVisibility(View.INVISIBLE);
            layoutradiobutton.clearAnimation();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_referencia) {
            ocultarLayouts();
            mostrarOcultarInfo();
            return true;
        }

        //trae de nuevo los datos
        if (id == R.id.action_refresh) {
            pedirDatosWS();
        }

        if (id == R.id.action_rango) {

            layoutInfo.setVisibility(View.INVISIBLE);
            layoutInfo.clearAnimation();

            if (layoutradiobutton.getVisibility() == View.INVISIBLE) {
                layoutradiobutton.setVisibility(View.VISIBLE);
                layoutradiobutton.startAnimation(animationfadein);

            } else {
                layoutradiobutton.setVisibility(View.INVISIBLE);
                layoutradiobutton.clearAnimation();
            }
        }

        if (id == R.id.action_filtro) {
            layoutInfo.setVisibility(View.INVISIBLE);
            layoutInfo.clearAnimation();
            layoutradiobutton.setVisibility(View.INVISIBLE);
            layoutradiobutton.clearAnimation();

            if (item_selection == 1) {
                createAvailableMarkers();

                Toast.makeText(MainActivity.this, getString(R.string.str_availableBares), Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.ic_action_filter_selected);
                item_selection = 2;

            } else {

                createAllMarkers();
                Toast.makeText(MainActivity.this, getString(R.string.str_allBares), Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.ic_action_filter_outline);
                item_selection = 1;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.bares_cercanos) {
            Intent act = new Intent(this, MainActivity.class);
            startActivity(act);

        } else if (id == R.id.buscar_bares) {
            Intent act = new Intent(this, ListadoBaresActivity.class);
            startActivity(act);
        } else if (id == R.id.suscribirse) {

        } else if (id == R.id.nav_Promociones) {
            Intent act = new Intent(this, PromocionesActivity.class);
            startActivity(act);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, "lugarenelbar@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Modificación en datos de bar");
            startActivity(Intent.createChooser(intent, "Enviar Email"));

        } else if (id == R.id.admin) {
            //llama a loguear usuario si hace falta. si se loguea con exito, ir a NuevoBarActivity
            loginUser(OwnerBarActivity.class);

        } else if (id == R.id.nav_logout) {
            String user = LoggedAccount.getLoggedAccount(this);

            if (!user.equals("")) {
                Toast.makeText(this, user + " " + getString(R.string.logout_correcto), Toast.LENGTH_SHORT).show();
                LoggedAccount.clearLoggedAccount(this);

            } else {
                Toast.makeText(this, getString(R.string.user_noLogueado), Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loginUser(Class nextActivity) {
        if (LoggedAccount.getLoggedAccount(getApplicationContext()).equals("")) {
            // si no esta logueado
            Intent act = new Intent(this, LoginActivity.class);
            act.putExtra("nextActivity", nextActivity.getName());
            startActivity(act);
        } else {
            // si esta logueado
            Intent act = new Intent(this, nextActivity);
            startActivity(act);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configGPS();
                } else {
                    Toast.makeText(MainActivity.this, "PERMISO RECHAZADO, NO VA A PODER OBTENER SU UBICACIÓN.\n" +
                            "Si desea activar nuevamente los permisos diríjase a administración de aplicaciones.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //CLASE QUE MODIFICA LA INFORMACION DEL MARKER
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoContents(Marker marker) {
            Typeface myfont = Typeface.createFromAsset(getAssets(), "Anjasmoro.ttf");
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            tvTitle.setTypeface(myfont);
            TextView tvSnippet_dire = ((TextView) myContentsView.findViewById(R.id.snippet_dire));
            tvSnippet_dire.setText(marker.getSnippet());
            TextView tv_snippet_tel = (TextView) myContentsView.findViewById(R.id.snippet_tel);
            tv_snippet_tel.setText(snippettel);
            TextView tv_snippet_distancia = (TextView) myContentsView.findViewById(R.id.snippet_distancia);
            tv_snippet_distancia.setText(distancia + " mts.");
            TextView tv_snippet_estado = (TextView) myContentsView.findViewById(R.id.snippet_estado);
            tv_snippet_estado.setText(estado);

            infoButtonListener.setMarker(marker);

            // We must call this to set the current marker and infoWindow references
            // to the MapWrapperLayout
            mapWrapperLayout.setMarkerWithInfoWindow(marker, myContentsView);

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }

    //CUSTOM MARKER VIEW - ESTOS DOS METODOS SON PARA LOS MARCADORES DISPONIBLE Y NO DISP
    // EL PRIMERO USA UN RECURSO INT Y EL OTRO UN BITMAP
    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(markerImg, null);

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

    private Bitmap getMarkerBitmap(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(markerImg, null);

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

    public void mostrarOcultarInfo() {
        layoutradiobutton.setVisibility(View.INVISIBLE);
        layoutradiobutton.clearAnimation();

        if (layoutInfo.getVisibility() == View.VISIBLE) {
            layoutInfo.setVisibility(View.INVISIBLE);
            layoutInfo.clearAnimation();

        } else {

            layoutInfo.startAnimation(animationfadein);
            layoutInfo.setVisibility(View.VISIBLE);
        }
    }

    public void eventoRadiobuttons(View view) {
        switch (view.getId()) {

            case R.id.rbmenora:
                //seteo el rango para que se vean bares hasta 1000mts a la redonda
                valor_rango = 1000;
                if (item_selection == 1) {
                    createAllMarkers();
                } else {
                    createAvailableMarkers();
                }

                break;

            case R.id.rbmayora:
                //seteo el rango para que se vean bares hasta 5000mts a la redonda
                valor_rango = 5000;

                if (item_selection == 1) {
                    createAllMarkers();
                } else {
                    createAvailableMarkers();
                }

                break;
        }

        createMyLocationMarker();

        layoutradiobutton.setVisibility(View.INVISIBLE);
        layoutradiobutton.clearAnimation();

    }

    //este metodo es para ocultar las ventanas emergentes de info y radiobutton
    public void ocultarLayouts() {
        if (layoutInfo.getVisibility() == View.VISIBLE) {
            layoutInfo.setVisibility(View.INVISIBLE);
            layoutInfo.clearAnimation();
        } else if (layoutradiobutton.getVisibility() == View.VISIBLE) {
            layoutradiobutton.setVisibility(View.INVISIBLE);
            layoutradiobutton.clearAnimation();
        }
    }

    public void createAllMarkers() {
        mapa.clear();
        createMyLocationMarker();

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();


        if (listaBares != null) {
            for (int i = 0; i < listaBares.size(); i++) {

                Bar bar = listaBares.get(i);

                _nombreLegal = bar.getNombreLegal();
                _nombreFantasia = bar.getNombreFantasia();
                _pais = bar.getPais();
                _ciudad = bar.getCiudad();
                _calle = bar.getCalle();
                _nroCalle = bar.getNroCalle();
                _lat = bar.getLat();
                _lon = bar.getLon();
                _telefono = bar.getTelefono();
                _logo = bar.getLogo();
                _mesasLibres = bar.getMesasLibres();


                if (bar.getMesasLibres() != 0) {
                    markerImg = R.layout.custom_view_marker_disp;
                } else {
                    markerImg = R.layout.custom_view_marker_nodisp;
                }


                distancia = calcularDistancias(current_latitud, current_longitud, bar.getLat(), bar.getLon())*1000;

                distancia=Math.floor(distancia*100) / 100;

                Log.i("TAG Distancia", "Current lat:"+current_latitud+" - Bar Lat: "+bar.getLat()+ " - distancia:" +distancia);
                if (distancia <= valor_rango) {


                    if (!_logo.toString().equals("null")) {

                        imageLoader.get(_logo, new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                                mapa.addMarker(new MarkerOptions()
                                        .position(new LatLng(_lat, _lon))
                                        .title(_nombreFantasia)
                                        .snippet("Dirección: " + _calle + ", " + _nroCalle + ", " + _ciudad + ", "
                                                + _pais + "\n\nTeléfono: " + _telefono +
                                                "\n\nCantidad de mesas libres: " + _mesasLibres +
                                                "\n\nDistancia aprox.: " + distancia + " mts.")
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(response.getBitmap()))));
                            }
                        });
                    } else {


                        mapa.addMarker(new MarkerOptions()
                                .position(new LatLng(bar.getLat(), bar.getLon()))
                                .title(bar.getNombreFantasia())
                                .snippet("Dirección: " + bar.getCalle() + ", " + bar.getNroCalle() + ", " + bar.getCiudad() + ", "
                                        + bar.getPais() + "\n\nTeléfono: " + bar.getTelefono() +
                                        "\n\nCantidad de mesas libres: " + bar.getMesasLibres() +
                                        "\n\nDistancia aprox.: " + distancia + " mts.")
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.logo_default))));

                    }

                }//fin distancia

            }


    }else

    {
        Toast.makeText(MainActivity.this, "Lista vacía", Toast.LENGTH_SHORT).show();
    }

}

    public void createAvailableMarkers() {
        mapa.clear();
        createMyLocationMarker();
        markerImg = R.layout.custom_view_marker_disp;
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();

        if (listaBaresDisponibles != null) {


            for (int i = 0; i < listaBaresDisponibles.size(); i++) {

                Bar bar = listaBaresDisponibles.get(i);

                _nombreLegal = bar.getNombreLegal();
                _nombreFantasia = bar.getNombreFantasia();
                _pais = bar.getPais();
                _ciudad = bar.getCiudad();
                _calle = bar.getCalle();
                _nroCalle = bar.getNroCalle();
                _lat = bar.getLat();
                _lon = bar.getLon();
                _telefono = bar.getTelefono();
                _logo = bar.getLogo();
                _mesasLibres = bar.getMesasLibres();

                distancia = (calcularDistancias(current_latitud, current_longitud, bar.getLat(), bar.getLon()) * 1000);

                distancia=Math.floor(distancia*100) / 100;

                if (distancia <= valor_rango) {


                    if (!_logo.toString().equals("null")) {

                        imageLoader.get(_logo, new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {


                                mapa.addMarker(new MarkerOptions()
                                        .position(new LatLng(_lat, _lon))
                                        .title(_nombreFantasia)
                                        .snippet("Dirección: " + _calle + ", " + _nroCalle + ", " + _ciudad + ", "
                                                + _pais + "\n\nTeléfono: " + _telefono +
                                                "\n\nCantidad de mesas libres: " + _mesasLibres+
                                                "\n\nDistancia aprox.: " + distancia + " mts.")
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(response.getBitmap()))));
                            }
                        });
                    } else {

                        mapa.addMarker(new MarkerOptions()
                                .position(new LatLng(bar.getLat(), bar.getLon()))
                                .title(bar.getNombreFantasia())
                                .snippet("Dirección: " + bar.getCalle() + ", " + bar.getNroCalle() + ", " + bar.getCiudad() + ", "
                                        + bar.getPais() + "\n\nTeléfono: " + bar.getTelefono() +
                                        "\n\nCantidad de mesas libres: " + bar.getMesasLibres()+
                                        "\n\nDistancia aprox.: " + distancia + " mts.")
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.logo_default))));
                    }


                }//fin if disancia


            }


        } else {
            Toast.makeText(MainActivity.this, "Lista vacía", Toast.LENGTH_SHORT).show();
        }
    }

    public void createMyLocationMarker() {
        LatLng latLng = new LatLng(current_latitud, current_longitud);

        markerImg = R.layout.custom_view_marker_milocation;

        //primero remuevo el marker viejo
        if (null != markerPosicionActual) {
            markerPosicionActual.remove();
        }

        //crear el marker de posicion actual y agregarlo
        markerPosicionActual = mapa.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.str_marker_currentLocation))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.logo_milocation))));

        //para que no se muestre infoWindow en el marker de posicion actual
        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mapa.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                if (marker.equals(markerPosicionActual)) {
                    return true;
                }
                return false;
            }
        });

        //mover la camara
        if (!cameraMoved) {
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
            cameraMoved = true;
        }
    }

    public void centerWindowsInfo(Marker marker) {
        int yMatrix = 600, xMatrix = 5;

        DisplayMetrics metrics1 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics1);
        switch (metrics1.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                yMatrix = 80;
                xMatrix = 20;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                yMatrix = 100;
                xMatrix = 25;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                yMatrix = 150;
                xMatrix = 30;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                yMatrix = 200;
                xMatrix = 20;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                yMatrix = 500;
                xMatrix = 1;
                break;
        }

        Projection projection = mapa.getProjection();
        LatLng latLng = marker.getPosition();
        Point point = projection.toScreenLocation(latLng);
        Point point2 = new Point(point.x + xMatrix, point.y - yMatrix - 2000);

        LatLng point3 = projection.fromScreenLocation(point2);
        CameraUpdate zoom1 = CameraUpdateFactory.newLatLng(point3);
        mapa.animateCamera(zoom1);
        marker.showInfoWindow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_CODE:
                if (requestCode == RESULT_OK) {
                    Toast.makeText(this, "gps activado", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void myLocation() {
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_latitud, current_longitud), 14.0f));
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


//ESTAS DOS TAREAS ASYNCRONAS SE VAN, SE USO OTRO METODO PARA DESCARGA DE MAGENES.
private class loadImg extends AsyncTask<Void, Integer, Void> {

    @Override
    protected Void doInBackground(Void... params) {


        bitmap = new Bitmap[listaBares.size()];
        try {
            for (int i = 0; i < listaBares.size(); i++) {
                Bar bar = listaBares.get(i);
                String link = bar.getLogo();
                bitmap[i] = Glide.with(MainActivity.this)
                        .load(link)
                        .asBitmap()
                        .into(-1, -1)
                        .get();
                Log.i("URL_API_BARES LOGO", "i All:" + bar.getId() + " - BITMAP " + i + ":" + bitmap[i] + " - url: " + link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        //start laoding
        loadingLayout.setVisibility(View.VISIBLE);
        loadingViewAnim.start();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (bitmap != null) {
            createAllMarkers();
            //Stop Loading Animation
            loadingLayout.setVisibility(View.GONE);
            loadingViewAnim.stop();
            apmap.getView().setVisibility(View.VISIBLE);
            fabmilocation.setVisibility(View.VISIBLE);
        }
    }
}

private class loadImgDisponibles extends AsyncTask<Void, Integer, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        bitmapDisponibles = new Bitmap[listaBaresDisponibles.size()];

        try {
            for (int i = 0; i < listaBaresDisponibles.size(); i++) {
                Bar bar = listaBaresDisponibles.get(i);
                String link = bar.getLogo();
                bitmapDisponibles[i] = Glide.with(MainActivity.this)
                        .load(link)
                        .asBitmap()
                        .into(-1, -1)
                        .get();
                Log.i("URL_API_BARES LOGO", "i DISP: " + i + " idBar: " + bar.getId() + " - BITMAP: " + bitmap[i] + " - url: " + link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}


}//FIN CLASE MAIN
