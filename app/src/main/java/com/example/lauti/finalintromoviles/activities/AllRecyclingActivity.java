package com.example.lauti.finalintromoviles.activities;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lauti.finalintromoviles.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class AllRecyclingActivity extends AppCompatActivity {

    private String username;
    private static final String CHART_DESCRIPTION = "Total reciclado hasta hoy.";

    private PieChart pieChart;
    private Button showSent;
    private Button showLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recycling);

        initComponents();
        new GetRecyclingWebService().execute();

    }

    private void initComponents() {
        // Getting the username
        Bundle extras = getIntent().getExtras();
        username = (String) extras.get(LoginActivity.USERNAME);

        // mapping buttons
        showSent = (Button) findViewById(R.id.showSent);
        showSent.setVisibility(View.INVISIBLE); // hide the button
        showSent.setEnabled(false);
        showSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // consume the service;
                showSent.setVisibility(View.INVISIBLE); // hide the button
                showSent.setEnabled(false);
                showLocal.setVisibility(View.VISIBLE);
                showLocal.setEnabled(true);
            }
        });
        showLocal = (Button) findViewById(R.id.showLocally);
        showLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocal.setVisibility(View.INVISIBLE);
                showLocal.setEnabled(false);
                // loadLocalData();
                showSent.setVisibility(View.VISIBLE);
                showSent.setEnabled(true);
            }
        });

        // mapping the chart
        pieChart = (PieChart) findViewById(R.id.graph);

        Description description = new Description();
        description.setText(CHART_DESCRIPTION); // create customized description
        pieChart.setDescription(description); // set the description

        pieChart.setRotationEnabled(false); // true to rotate the pie
        pieChart.setDrawEntryLabels(false);
        //pieChart.setEntryLabelTextSize(20);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Total "/* + total + " t"*/); // total amount in tons
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(17);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);

    }

    private void loadChartData() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Hardcoding like a champ
        int [] entriesValues = {20, 35, 42, 11, 7};
        String [] labelsName = {"bottles", "tetrabriks", "paperboard", "glass", "cans"};

        // Also we have the sum (tons) in the response

        for (int i=0; i < entriesValues.length; i++) {
            //pieEntries.add(new PieEntry(entriesValues[i], RecyclingActivity.RECYCLING_MATERIALS[i].substring(0,1).toUpperCase() + RecyclingActivity.RECYCLING_MATERIALS[i].substring(1).toLowerCase()));
            pieEntries.add(new PieEntry(entriesValues[i], labelsName[i].substring(0,1).toUpperCase() + labelsName[i].substring(1).toLowerCase()));
        }

        // Create the data set
        PieDataSet dataSet = new PieDataSet(pieEntries, "");

        // Create the labels data
        dataSet.setSliceSpace(2); // space between slices in the chart
        dataSet.setValueTextSize(17); // the text size in the slice

        // Add colors to the data set
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.MAGENTA);

        dataSet.setColors(colors);

        // Set data (labels)
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // refresh

    }


    private class GetRecyclingWebService extends AsyncTask<URL, Integer, Long> {

        private final static String ERROR_RESPONSE = "Error response";

        /**
         * webServiceAction is the URL where is the service, we use 10.0.2.2 and not localhost
         * because we try it on the emulator.
         * Note: not working on the device.
         */
        private static final String API_LOCALITATION = "http://10.0.2.2:8080/api/";
        private String webServiceAction = "recycling/";

        public GetRecyclingWebService () {
            webServiceAction = API_LOCALITATION + webServiceAction + username + "/";
        }

        @Override
        protected Long doInBackground(URL... urls) {
            long result = 0;

            ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        /**
                         * We already have the response
                         * We must show it in the chart
                         *
                         * Again we must use "bottles", "tetrabriks"...
                         * respJSON.getInt("bottles");
                         * respJSON.getInt("");
                         * respJSON.getInt("");
                         * respJSON.getInt("");
                         * respJSON.getInt("");
                         */
                        result = 1;
                    } else {
                        //Toast.makeText(context, "Response error", Toast.LENGTH_SHORT).show();
                        Log.e(ERROR_RESPONSE, "RESPONSE ERROR");
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
            loadChartData();
        }

}
}
