package com.example.lauti.finalintromoviles.controller;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 *
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 *
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class GetRecyclingWebService extends AsyncTask<URL, Integer, Long> {
    private Context context;
    /**
     * webServiceAction is the URL where is the service, we use 10.0.2.2 and not localhost
     * because we try it on the emulator.
     * Note: not working on the device.
     */
    private static final String API_LOCALITATION = "http://10.0.2.2:8080/api/";
    private String webServiceAction = "recycling/";
    private String username;

    public GetRecyclingWebService (Context context, String username) {
        this.context = context;
        this.username = username;
        webServiceAction = API_LOCALITATION + webServiceAction + username + "/";
    }

    @Override
    protected Long doInBackground(URL... urls) {
        long result = 0;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                // Setting the HTTP Request
                URL requestURL = new URL(webServiceAction); // WS URL
                HttpURLConnection myConnection = (HttpURLConnection) requestURL.openConnection();
                myConnection.setReadTimeout(10000);
                myConnection.setConnectTimeout(15000);
                myConnection.setRequestMethod("GET"); // HTTP method used is GET
                myConnection.setDoInput(true);
                myConnection.connect();

                StringBuilder sb = new StringBuilder();
                int httpResult = myConnection.getResponseCode();

                if (httpResult == HttpURLConnection.HTTP_OK) {  // We use GET, so we must have an OK
                    BufferedReader br = new BufferedReader(new InputStreamReader(myConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) { // read the response
                        sb.append(line + "\n");
                    }
                    br.close();
                    JSONObject respJSON = new JSONObject(sb.toString()); // get the JSON Response
                    Log.e("////////////////", sb.toString());
                    /**
                     * We already have the response
                     * We must send this json to another activity
                     */
                    result = 1;
                } else {
                    //Toast.makeText(context, "Response error", Toast.LENGTH_SHORT).show();
                    Log.e("Response error", "RESPONSE ERROR");
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                    e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Long result) {
        super.onPostExecute(result);

        if (result == 1) {
           // tvResponse.setText("Respuesta:\n" + tvResponse.getText().toString());
        } else {
        //    tvResponse.setText("There is an error");
        }
    }
}
