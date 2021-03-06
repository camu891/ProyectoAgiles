package com.matic.lugarenelbar.taskWebService.bares;

import android.os.AsyncTask;
import android.util.Log;

import com.matic.lugarenelbar.models.Bar;
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
 * Created by Matic on 24/05/2016.
 */
public class TareaWSArrayList extends AsyncTask<String, Integer, ArrayList<Bar>> {

    ArrayList<Bar> listaBares;

    protected ArrayList<Bar> doInBackground(String... params) {

        boolean resul = true;

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet del = new HttpGet(Constants.URL_API_BARES);

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            JSONArray respJSON = new JSONArray(respStr);

            listaBares = new ArrayList<>();

            for (int i = 0; i < respJSON.length(); i++) {
                JSONObject obj = respJSON.getJSONObject(i);

                int idBar = obj.getInt("idBar");
                String nombreLegal = obj.getString("nombreLegal");
                String nombreFantasia = obj.getString("nombreFantasia");
                String pais = obj.getString("pais");
                String ciudad = obj.getString("ciudad");
                String calle = obj.getString("calle");
                int nroCalle = obj.getInt("nroCalle");
                double latitud = obj.getDouble("latitud");
                double longitud = obj.getDouble("longitud");
                String telefono = obj.getString("telefono");
                String logo = obj.getString("logo");
                int mesasLibres = obj.getInt("mesasLibres");

                listaBares.add(new Bar(idBar, nombreLegal, nombreFantasia, pais, ciudad, calle, nroCalle, latitud, longitud, telefono, logo, mesasLibres));

            }

        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            resul = false;
        }

        return listaBares;
    }

    protected void onPostExecute(ArrayList<Bar> result) {

    }
}