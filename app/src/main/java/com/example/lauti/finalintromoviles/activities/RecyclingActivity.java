package com.example.lauti.finalintromoviles.activities;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.controller.GetRecyclingWebService;
import com.example.lauti.finalintromoviles.model.UserRecycling;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecyclingActivity extends AppCompatActivity {

    private String username;
    private UserRecycling userRecycling; // this is used to send a JSON in a HTTP Request Body
    private static final String API_LOCALITATION = "http://10.0.2.2:8080/api/";
    private static final String RECYCLING_LOAD_MESSAGE = "Reciclado cargado.";
    private static final String RECYCLING_SENT_MESSAGE = "Reciclado enviado.";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String FILENAME = "User_Recycling_Saved.txt";

    // Buttons
    private Button loadButton;
    private Button sendButton;
    private Button showRecyclng;
    private Button viewAllRecycling;
    // Texts
    private EditText bottles;
    private EditText tetrabriks;
    private EditText paperboard;
    private EditText glass;
    private EditText cans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling);

        initComponents();
    }

    private void initComponents() {
        // Getting the username
        Bundle extras = getIntent().getExtras();
        username = (String) extras.get(LoginActivity.USERNAME);

        // Mapping Edit Texts
        bottles = (EditText) findViewById(R.id.bottlesText);
        bottles.setText("0");
        tetrabriks = (EditText) findViewById(R.id.tetrabriksText);
        tetrabriks.setText("0");
        paperboard = (EditText) findViewById(R.id.paperboardText);
        paperboard.setText("0");
        glass = (EditText) findViewById(R.id.glassText);
        glass.setText("0");
        cans = (EditText) findViewById(R.id.cansText);
        cans.setText("0");

        // Mapping buttons and setting listeners
        loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Save locally the UserRecycling data
                loadUserRecycling();
                // Internal Storage
                saveUserRecycling();
                cleanFields();
                Toast.makeText(getApplicationContext(), RECYCLING_LOAD_MESSAGE, Toast.LENGTH_LONG).show();
            }
        });
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * We must send all the recycling, including the local storage
                 */
                loadUserRecycling();
                // get the local data and plus the new data
                loadUserRecyclingData();
                // Start AsyncTask to send the UserRecycling
                new SendUserRecyclingWS().execute();
                cleanFields();
                // Remove local saved data
                deleteUserRecyclingData();
                Toast.makeText(getApplicationContext(), RECYCLING_SENT_MESSAGE, Toast.LENGTH_LONG).show();
            }
        });
        showRecyclng = (Button) findViewById(R.id.recyclingListButton);
        showRecyclng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new Activity
                Intent recyclingListActivity = new Intent(getApplicationContext(), RecyclingListActivity.class);
                recyclingListActivity.putExtra(LoginActivity.USERNAME, username); // send the username to the new activity
                startActivity(recyclingListActivity);
            }
        });
        viewAllRecycling = (Button) findViewById(R.id.totalButton);
        viewAllRecycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // What if the user has no recycling? Show a message
                // and if only has locally ? Show saying its locally
                // Start a new Activity that contains a circular graph showing all recycling, put which is local and which ones no.
                new GetRecyclingWebService(getApplicationContext(), username).execute(); // this class could be inside the activity, like MyAsinkTask
            }
        });
    }

    /**
     *
     * The next three methods could be done in another AsynkTask
     *
     *
     * */

    // Save User Recycling data locally
    private void saveUserRecycling() {
        /**
         * We must load previous file data and add the new data.
         * Suppose we saved bottles:10, tetrabriks: 4, paperboard: 20, glass: 1, cans: 40
         * and now, we want to save bottles:3, tetrabriks:2, paperboard: 5, glass: 0, cans: 0
         * We must add the new data, so we will get (saved) bottles:13, tetrabriks: 6, paperboard:25, glass: 1, cans: 40
         */
        FileOutputStream fos = null;
        try {
            // Load from internal storage
            loadUserRecyclingData();
            // Save the file
            OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));

            Log.e("To write in file", userRecycling.toJSONObject().toString());


            osw.write(userRecycling.toJSONObject().toString());
            osw.close();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            Log.e("Error", e.getMessage());
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
            //e.printStackTrace();
        }
    }

    // Load User Recycling data from locally storage
    private void loadUserRecyclingData() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
            if (br != null) {
                String line = br.readLine();
                if (line != null) {
                    JSONObject json = new JSONObject(line);
                    // Do the sum
                    userRecycling.setBottles(userRecycling.getBottles() + json.getInt("bottles"));  // don't like "bottles", "tetrabriks"...
                    userRecycling.setTetrabriks(userRecycling.getTetrabriks() + json.getInt("tetrabriks"));
                    userRecycling.setPaperboard(userRecycling.getPaperboard() + json.getInt("paperboard"));
                    userRecycling.setGlass(userRecycling.getGlass() + json.getInt("glass"));
                    userRecycling.setCans(userRecycling.getCans() + json.getInt("cans"));
                }
                br.close();
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            Log.e("File not Found", e.getMessage());
        } catch (IOException e) {
            Log.e("IO ", e.getMessage());
            //e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
            // e.printStackTrace();
        }
    }

    // Delete the user recycling data file from internal storage
    private void deleteUserRecyclingData() {
        File file = new File(this.getFilesDir(), FILENAME);
        file.delete();
    }

    // Get the data from the Edit Text and load the User Recycling

    /**
     * What to do if all fields are 0 ??
     */
    private void loadUserRecycling() {
        if (!bottles.getText().toString().equals("0") || !tetrabriks.getText().toString().equals("0") || !paperboard.getText().toString().equals("0") ||
                !glass.getText().toString().equals("0") || !cans.getText().toString().equals("0")) {
            try {
                JSONObject json = new JSONObject();
                json.put("bottles", Integer.parseInt(bottles.getText().toString()));
                json.put("tetrabriks", Integer.parseInt(tetrabriks.getText().toString()));
                json.put("paperboard", Integer.parseInt(paperboard.getText().toString()));
                json.put("glass", Integer.parseInt(glass.getText().toString()));
                json.put("cans", Integer.parseInt(cans.getText().toString()));
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                json.put("date", sdf.format(new Date()));
                userRecycling = new UserRecycling(json);
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.e("Error json", e.getMessage());
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.EMPTY_FIELDS, Toast.LENGTH_LONG).show();
        }
    }

    // Clean Edit Text fields
    private void cleanFields() {
        bottles.setText("0");
        tetrabriks.setText("0");
        paperboard.setText("0");
        glass.setText("0");
        cans.setText("0");
    }


    /**
     * This AsyncTask can be used to send data to the service or save data locally.
     */
    private class SendUserRecyclingWS extends AsyncTask<URL, Integer, Long> {

        private int codeOperation;
        private static final String RECYCLING_SENT_MESSAGE = "Se han enviado sus reciclados";

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Toast.makeText(getApplicationContext(), RECYCLING_SENT_MESSAGE, Toast.LENGTH_LONG).show();
        }

        @Override
        protected Long doInBackground(URL... urls) {

            ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Consume the WS
                URL url = null;
                try {
                    // Create connection to the API
                    url = new URL(API_LOCALITATION + username + "/recycling");

                    HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();

                    // Connection Parameters
                    myConnection.setReadTimeout(15000 /* milliseconds */);
                    myConnection.setConnectTimeout(15000 /* milliseconds */);
                    myConnection.setRequestMethod("POST"); // It can be any HTTP Request Method like delete, put...
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setDoOutput(true);

                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                    userRecycling.setDate(sdf.format(new Date())); // set the user recycling date (the send date)
                    JSONObject postJSON = userRecycling.toJSONObject(); // JSON to be POST
                    OutputStream os = myConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postJSON.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = myConnection.getResponseCode(); // connection response code
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) { // if it is OK or if */
                        BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));

                        StringBuffer myStringBuffer = new StringBuffer("");
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            myStringBuffer.append(line);
                            //break;
                        }
                        in.close();
                    } else {
                        Log.e("HTTP", " " + responseCode);
                    }
                    return Long.parseLong(String.valueOf(responseCode));
                } catch (ProtocolException e) {
                    //e.printStackTrace();
                    Log.e("Protocol", e.getMessage());
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.e("IOExcep", e.getMessage());
                }
            }
            return null;
        }
    }
}