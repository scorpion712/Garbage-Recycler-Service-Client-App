package com.example.lauti.finalintromoviles.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.dialogs.AllRecyclingDialog;
import com.example.lauti.finalintromoviles.model.UserRecycling;
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
import java.util.List;

public class AllRecyclingActivity extends AppCompatActivity {

    private String username;
    private static final String CHART_DESCRIPTION = "Total reciclado hasta hoy.";
    private static final String CHART_DESCRIPTION_EMPTY = "Usted no ha enviado reciclados.";

    private PieChart pieChart;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recycling);

        initComponents();
        new GetRecyclingWebService().execute();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // adding the customized toolbar


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu); // make toolbar items' visible
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                // show a help dialog
                new AllRecyclingDialog().show(getSupportFragmentManager(), "");
                break;
            case R.id.logout:
                // Do the logout. Return to login activity
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
                break;
        }
        return true;
    }

    private void initComponents() {
        // Getting the username
        Bundle extras = getIntent().getExtras();
        username = (String) extras.get(LoginActivity.USERNAME);

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
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(17);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);

    }

    // Load pie chart data using the received parameters from the service response
    private void loadChartData(List<Integer> entriesValues, String [] labelsName, Double total) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (int i =0; i < entriesValues.size(); i++) {
            //pieEntries.add(new PieEntry(entriesValues[i], RecyclingActivity.RECYCLING_MATERIALS[i].substring(0,1).toUpperCase() + RecyclingActivity.RECYCLING_MATERIALS[i].substring(1).toLowerCase()));
            pieEntries.add(new PieEntry(entriesValues.get(i), labelsName[i].substring(0,1).toUpperCase() + labelsName[i].substring(1).toLowerCase()));
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

        pieChart.setCenterText("Total " + total + " t"); // total amount in tons
        if (total <= 0) {
            Description description = new Description();
            description.setText(CHART_DESCRIPTION_EMPTY); // create customized description
            pieChart.setDescription(description); // set the description
        }
        pieChart.invalidate(); // refresh

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private class GetRecyclingWebService extends AsyncTask<URL, Integer, Long> {

        private final static String ERROR_RESPONSE = "Error response";

        /**
         * webServiceAction is the URL where is the service, we use 10.0.2.2 and not localhost
         * because we try it on the emulator.
         * Note: not working on the device.
         */

        // Use 10.0.0.2 to test connect with the virtual device. Use your local IP to test with a physical device on the same network
        private static final String API_LOCALITATION = "http://10.0.0.2:8080/api/";
        private String webServiceAction = "recycling/";


        private List<Integer> entriesValues = new ArrayList<>();
        private String [] labelsName = {"empty"};
        private Double total = 0.0;

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
                        entriesValues.add(respJSON.getInt(UserRecycling.BOTTLES));
                        entriesValues.add(respJSON.getInt(UserRecycling.TETRABRIKS));
                        entriesValues.add(respJSON.getInt(UserRecycling.PAPERBOARD));
                        entriesValues.add(respJSON.getInt(UserRecycling.GLASS));
                        entriesValues.add(respJSON.getInt(UserRecycling.CANS));

                        total = respJSON.getDouble("tons");

                        labelsName = new String[]{UserRecycling.BOTTLES, UserRecycling.TETRABRIKS, UserRecycling.PAPERBOARD, UserRecycling.GLASS, UserRecycling.CANS};
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
            loadChartData(entriesValues, labelsName, total);
        }
    }
}
