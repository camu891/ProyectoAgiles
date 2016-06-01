package com.matic.lugarenelbar.taskWebService.bares;

import android.os.AsyncTask;
import android.util.Log;

import com.matic.lugarenelbar.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by Matic on 23/05/2016.
 */
public class TareaWSInsertar extends AsyncTask<String, Integer, Boolean> {

    protected Boolean doInBackground(String... params) {

        boolean resul = true;

        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post = new HttpPost(Constants.URL_API_BARES + "/Bar");
        post.setHeader("content-type", "application/json; charset=utf-8");

        try {
            //Construimos el objeto cliente en formato JSON
            JSONObject dato = new JSONObject();

            dato.put("nombreLegal", params[0]);
            dato.put("nombreFantasia", params[1]);
            dato.put("pais", params[2]);
            dato.put("ciudad", params[3]);
            dato.put("calle", params[4]);
            dato.put("nroCalle", Integer.parseInt(params[5]));
            dato.put("latitud", Float.parseFloat(params[6]));
            dato.put("longitud", Float.parseFloat(params[7]));
            dato.put("telefono", params[8]);
            dato.put("logo", params[9]);
            dato.put("mesasLibres", Integer.parseInt(params[10]));

            StringEntity entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            String respStr = EntityUtils.toString(resp.getEntity());

            if (!respStr.equals("true"))
                resul = false;
        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            resul = false;
        }

        return resul;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(Boolean result) {

        if (result) {

        }
    }
}
