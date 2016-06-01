package com.matic.lugarenelbar.taskWebService.promociones;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.matic.lugarenelbar.MainActivity;
import com.matic.lugarenelbar.PromocionesActivity;
import com.matic.lugarenelbar.models.Bar;
import com.matic.lugarenelbar.models.Promocion;
import com.matic.lugarenelbar.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import com.matic.lugarenelbar.MainActivity;

/**
 * Created by Matic on 25/05/2016.
 */
public class TareaWSListarPromociones extends AsyncTask<String, Integer, ArrayList<Promocion>> {

    ArrayList<Promocion> listaPromociones;

    protected ArrayList<Promocion> doInBackground(String... params) {

        boolean resul = true;


        HttpClient httpClient = new DefaultHttpClient();

        HttpGet del = new HttpGet(Constants.URL_API_PROMOCIONES);

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            JSONArray respJSON = new JSONArray(respStr);

            listaPromociones = new ArrayList<>();

            for (int i = 0; i < respJSON.length(); i++) {
                JSONObject obj = respJSON.getJSONObject(i);

                int idPromocion = obj.getInt("idPromocion");
                //SACO EL BAR DEL JSON

                JSONObject objBar = obj.getJSONObject("bar");
                Bar bar = new Bar();
                bar.setId(objBar.getInt("idBar"));
                bar.setNombreLegal(objBar.getString("nombreLegal"));
                bar.setNombreFantasia(objBar.getString("nombreFantasia"));
                bar.setPais(objBar.getString("pais"));
                bar.setCiudad(objBar.getString("ciudad"));
                bar.setCalle(objBar.getString("calle"));
                bar.setNroCalle(objBar.getInt("nroCalle"));
                bar.setLat(objBar.getDouble("latitud"));
                bar.setLon(objBar.getDouble("longitud"));
                bar.setTelefono(objBar.getString("telefono"));
                bar.setLogo(objBar.getString("logo"));
                bar.setMesasLibres(objBar.getInt("mesasLibres"));

                //HASTA ACA PROCESO EL BAR
                String nombrePromocion = obj.getString("nombre");
                String descripcion = obj.getString("descripcion");
                double precio = obj.getDouble("precio");
                String tipoPromo = obj.getString("tipo");

                //Carga en la lista solo las promociones que estan dentro del rango de bares cercanos
                LatLng latlon= MainActivity.getCoordenadasActuales();

                double distancia =  MainActivity.calcularDistancias(latlon.latitude, latlon.longitude, bar.getLat(), bar.getLon()) * 1000;
                Log.d("TAG DISTANCIA", "Distancia: " + distancia);

                if (distancia <= MainActivity.valor_rango) {
                    listaPromociones.add(new Promocion(idPromocion, bar, nombrePromocion, descripcion, precio, tipoPromo));
                }

            }

        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            resul = false;
        }

        return listaPromociones;
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(ArrayList<Promocion> result) {

    }






}
