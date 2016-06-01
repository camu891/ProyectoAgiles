package com.matic.lugarenelbar.taskWebService.promociones;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.matic.lugarenelbar.MainActivity;
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

/**
 * Created by Matic on 29/05/2016.
 */
public class TareaWSListarPromocionesXidBar  extends AsyncTask<Integer, Integer, ArrayList<Promocion>> {

    ArrayList<Promocion> listaPromocionesXidBar;

    protected ArrayList<Promocion> doInBackground(Integer... params) {

        boolean resul = true;


        HttpClient httpClient = new DefaultHttpClient();

        HttpGet del = new HttpGet(Constants.URL_API_PROMOCIONES+"Promocion/"+params[0]);

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            JSONArray respJSON = new JSONArray(respStr);

            listaPromocionesXidBar = new ArrayList<>();

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

                int distancia = (int) MainActivity.calcularDistancias(latlon.latitude, latlon.longitude, bar.getLat(), bar.getLon()) * 1000;
                Log.d("TAG DISTANCIA", "Distancia: " + distancia);


                //aca hay  q ver si listamos todas las promociones del var, no haria falta calcular la distancia
                if (distancia <= MainActivity.valor_rango) {
                    listaPromocionesXidBar.add(new Promocion(idPromocion, bar, nombrePromocion, descripcion, precio, tipoPromo));
                }

            }

        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            resul = false;
        }

        return listaPromocionesXidBar;
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(ArrayList<Promocion> result) {

    }



}
