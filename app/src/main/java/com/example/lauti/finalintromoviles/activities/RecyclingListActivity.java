package com.example.lauti.finalintromoviles.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.controller.RecyclingAdapter;
import com.example.lauti.finalintromoviles.model.UserRecycling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecyclingListActivity extends AppCompatActivity {

    private List<UserRecycling> recyclingList = new ArrayList<>();
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_list);
        // Get the username
        Bundle extras = getIntent().getExtras();
        username = (String) extras.get(LoginActivity.USERNAME);
        // Consume the Web Service GET username recycling list
        new RecyclingListWS().execute();

        // Create a the view
        ListView recyclingListView = (ListView) findViewById(R.id.listView);
        RecyclingAdapter adapter = new RecyclingAdapter(getApplicationContext(), recyclingList);
        recyclingListView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * This class allows to get the Recycling list from the given username
     */
    private class RecyclingListWS extends AsyncTask<URL, Integer, Long> {
        /**
         * webServiceAction is the URL where is the service, we use 10.0.2.2 and not localhost
         * because we try it on the emulator.
         * Note: not working on the device.
         */
        private static final String API_LOCALITATION = "http://10.0.2.2:8080/api/";
        private String webServiceAction = "users_recycling/";

        private void loadUserRecyclingList(JSONArray jsonArray) {
            for(int i=0; i < jsonArray.length(); i++) {
                try {
                    recyclingList.add(new UserRecycling(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected Long doInBackground(URL... urls) {
            long result = 0;

            ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    // Setting the HTTP Request
                    URL requestURL = new URL(API_LOCALITATION + webServiceAction + username + "/"); // WS URL
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
                        JSONArray respJSON = new JSONArray(sb.toString()); // get the JSON Response

                        loadUserRecyclingList(respJSON);
                        result = 1;
                    } else {
                        Log.e("HTTP Result code", "" + httpResult);
                    }
                } catch (ProtocolException e) {
                    //e.printStackTrace();
                    Log.e("PE", e.getMessage());
                } catch (IOException e) {
                    //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("IO", e.getMessage());
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.e("JSON", e.getMessage());
                }
            } else {
                Log.e("Network", "connection failed");
            }

            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
        }
    }
}

