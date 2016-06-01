package com.matic.lugarenelbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.matic.lugarenelbar.taskWebService.bares.TareaWSInsertar;

import android.location.Address;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NuevoBarActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FloatingActionButton fabconfirm;
    private EditText mNombre_legal;
    private EditText mNombre_fantasia;
    private EditText mCalle;
    private EditText mNumeroCalle;
    private EditText mCiudad;
    private EditText mPais;
    private EditText mTelefono;

    private double mlatitud = 0.0;
    private double mlongitud = 0.0;

    private ProgressDialog pd;

    //variables para subir logo
    private String APP_DIRECTORY = "LugarenelBar/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private String TEMPORAL_PICTURE_NAME = "temporal.jpg";
    private final int PHOTO_CODE = 100;
    private final int SELECT_LOGO = 200;

    private ImageView imageView;
    private Button btnUpload_logo;

    //formateo de texto
    private String txtCalle = "";
    private String txtNumero = "";

    private MainActivity cm = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_bar);

        //activar boton back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNombre_legal = (EditText) findViewById(R.id.nombre_legal_bar);
        mNombre_fantasia = (EditText) findViewById(R.id.nombre_fantasia_bar);
        mCalle = (EditText) findViewById(R.id.calle);
        mNumeroCalle = (EditText) findViewById(R.id.numero_calle);
        mCiudad = (EditText) findViewById(R.id.ciudad);
        mPais = (EditText) findViewById(R.id.pais);
        mTelefono = (EditText) findViewById(R.id.telefono);
        imageView = (ImageView) findViewById(R.id.imageView_logo);

        btnUpload_logo = (Button) findViewById(R.id.btn_uploadlogo);
        btnUpload_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(NuevoBarActivity.this);
                builder.setTitle("Elija su opción: ");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if (options[seleccion] == "Tomar Foto") {
                            openCamara();
                        } else if (options[seleccion] == "Elegir de Galeria") {

                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona imagen"), SELECT_LOGO);

                        } else {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    }

                });

                builder.show();
            }
        });

        fabconfirm = (FloatingActionButton) findViewById(R.id.fab_confirm);
        fabconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptConfirm();

            }
        });

    }

    public void guardarDatos() {

        getLatLon();

        int id = 0;
        String nombreLegal = mNombre_legal.getText().toString();
        String nombreFantasia = mNombre_fantasia.getText().toString();
        String calle = mCalle.getText().toString();
        String nro = mNumeroCalle.getText().toString();
        String ciudad = mCiudad.getText().toString();
        String pais = mPais.getText().toString();
        String lat = String.valueOf(mlatitud);
        String lon = String.valueOf(mlongitud);
        String tel = mTelefono.getText().toString();
        String urlLogo = "null"; //logo en "null" por ahora hasta que sepamos como subir img a host y obtener url
        String mesasLibres = "1"; //el PO quiere que las mesas sean > 0 para que los bares se guarden como disponibles

        new TareaWSInsertar() {

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(NuevoBarActivity.this, "", getString(R.string.pd_message_saveSuccessful));
            }

            @Override
            protected void onPostExecute(Boolean result) {

                if (pd.isShowing()) {
                    pd.dismiss();
                }

                if (result) {
                    Toast.makeText(NuevoBarActivity.this, getString(R.string.str_bar_Agregado), Toast.LENGTH_SHORT).show();
                    cm.mainScreen.finish();
                    Intent act = new Intent(NuevoBarActivity.this, MainActivity.class);
                    startActivity(act);
                    finish();
                } else {
                    Toast.makeText(NuevoBarActivity.this, getString(R.string.str_bar_NoAgregado), Toast.LENGTH_SHORT).show();
                }

            }
        }.execute(nombreLegal, nombreFantasia, pais, ciudad, calle, nro, lat, lon, tel, urlLogo, mesasLibres);

    }

    public void openCamara() {

        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();

        String path = Environment.getExternalStorageDirectory() + File.separator +
                MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;

        File newFile = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_CODE:

                String dir = Environment.getExternalStorageDirectory() + File.separator +
                        MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;
                decodeBitMap(dir);
                break;

            case SELECT_LOGO:
                Uri path = data.getData();
                imageView.setImageURI(path);
                break;
        }
    }

    private void decodeBitMap(String dir) {
        Bitmap bm;
        bm = BitmapFactory.decodeFile(dir);
        imageView.setImageBitmap(bm);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Bundle extras = intent.getExtras();
        if (null == extras) return;

    }

    //evento del boton
    public void miLocation(View view) {
        obtenerMilocation();
    }

    public void obtenerMilocation() {

        double lat = 0.0, lon = 0.0;
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mLocationManager.isProviderEnabled("network") == false && mLocationManager.isProviderEnabled("gps") == false) {
            Toast.makeText(this, "GPS está desactivado ", Toast.LENGTH_SHORT).show();

        } else if (mLocationManager.isProviderEnabled("network") == true) {
            Location location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
            lat = location.getLatitude();
            lon = location.getLongitude();

        } else {
            Location location = mLocationManager.getLastKnownLocation(provider);
            lat = location.getLatitude();
            lon = location.getLongitude();

        }

        getAddress(lat, lon);
    }

    public void getLatLon() {

        String calle = mCalle.getText().toString();
        String nro = mNumeroCalle.getText().toString();
        String ciudad = mCiudad.getText().toString();
        String pais = mPais.getText().toString();
        String location = calle + "" + nro + "," + ciudad + "," + pais;
        List<Address> addressList = null;

        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        try {
            addressList = geo.getFromLocationName(location, 1);
        } catch (IOException ex) {
            ex.getMessage();

        }

        Address address = addressList.get(0);

        mlatitud = address.getLatitude();
        mlongitud = address.getLongitude();

    }

    public void getAddress(double latitude, double longitude) {

        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(NuevoBarActivity.this, "esperando ubicacion", Toast.LENGTH_SHORT).show();
            } else {
                if (addresses.size() > 0) {
                    clearTxt();
                    String cadena = addresses.get(0).getAddressLine(0);
                    formatearTexto(cadena);
                    mCiudad.setText(addresses.get(0).getLocality());
                    mPais.setText(addresses.get(0).getCountryName());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean isNumeric(char caracter) {
        try {
            Integer.parseInt(String.valueOf(caracter));
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void formatearTexto(String cadena) {

        char caracter;
        for (int i = 0; i < cadena.length(); i++) {
            caracter = cadena.charAt(i);
            if (isNumeric(caracter)) {
                txtNumero += caracter;
            } else {
                txtCalle += caracter;
            }
        }

        mCalle.setText(txtCalle);
        mNumeroCalle.setText(txtNumero);
        txtNumero = "";
        txtCalle = "";

    }

    private void clearTxt() {
        mCalle.setText("");
        mNumeroCalle.setText("");
        mCiudad.setText("");
        mPais.setText("");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "PERMISO ACEPTADO", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "PERMISO RECHAZADO", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
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

    private void attemptConfirm() {

        // Reset errors.
        mNombre_fantasia.setError(null);
        mNombre_legal.setError(null);
        mCalle.setError(null);
        mNumeroCalle.setError(null);
        mTelefono.setError(null);
        mCiudad.setError(null);
        mPais.setError(null);

        // Store values at the time of the login attempt.
        String nlegal = mNombre_legal.getText().toString();
        String nfantasia = mNombre_fantasia.getText().toString();
        String ncalle = mCalle.getText().toString();
        String nnumcalle = mNumeroCalle.getText().toString();
        String ntel = mTelefono.getText().toString();
        String nciudad = mCiudad.getText().toString();
        String npais = mPais.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nlegal)) {
            mNombre_legal.setError(getString(R.string.error_field_required));
            focusView = mNombre_legal;
            cancel = true;
        }

        if (TextUtils.isEmpty(nfantasia)) {
            mNombre_fantasia.setError(getString(R.string.error_field_required));
            focusView = mNombre_fantasia;
            cancel = true;
        }

        if (TextUtils.isEmpty(ncalle)) {
            mCalle.setError(getString(R.string.error_field_required));
            focusView = mCalle;
            cancel = true;
        }
        if (TextUtils.isEmpty(nnumcalle)) {
            mNumeroCalle.setError(getString(R.string.error_field_required));
            focusView = mNumeroCalle;
            cancel = true;
        }
        if (TextUtils.isEmpty(ntel)) {
            mTelefono.setError(getString(R.string.error_field_required));
            focusView = mTelefono;
            cancel = true;
        }
        if (TextUtils.isEmpty(nciudad)) {
            mCiudad.setError(getString(R.string.error_field_required));
            focusView = mCiudad;
            cancel = true;
        }
        if (TextUtils.isEmpty(npais)) {
            mPais.setError(getString(R.string.error_field_required));
            focusView = mPais;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            getLatLon();
            /*** SI GUARDA CORRECTAMENTE, DEBE MOSTRAR UN MENSAJE (DIALOG) Y CERRAR LA ACTIVIDAD ***/
            guardarDatos();
        }

    }

}
