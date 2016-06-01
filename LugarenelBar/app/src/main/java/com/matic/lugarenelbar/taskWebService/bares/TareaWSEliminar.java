package com.matic.lugarenelbar.taskWebService.bares;

import android.os.AsyncTask;
import android.util.Log;

import com.matic.lugarenelbar.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by Matic on 24/05/2016.
 */

public class TareaWSEliminar extends AsyncTask<String, Integer, Boolean> {

    protected Boolean doInBackground(String... params) {

        boolean resul = true;

        HttpClient httpClient = new DefaultHttpClient();

        String name = params[0];

        HttpDelete del =
                new HttpDelete(Constants.URL_API_BARES + "/Bar/" + name);

        del.setHeader("content-type", "application/json");

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            if (!respStr.equals("true"))
                resul = false;
        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            resul = false;
        }

        return resul;
    }

    protected void onPostExecute(Boolean result) {

    }
}
