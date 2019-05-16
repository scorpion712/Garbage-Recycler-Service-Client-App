package com.example.lauti.finalintromoviles.activities;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.dialogs.RecyclingDialog;
import com.example.lauti.finalintromoviles.model.UserRecycling;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
    private UserRecycling userRecycling = null; // this is used to send a JSON in a HTTP Request Body
    // Use 10.0.0.2 to test connect with the virtual device. Use your local IP to test with a physical device on the same network
    private static final String API_LOCALITATION = "http://10.0.0.2:8080/api/";
    private static final String RECYCLING_LOAD_MESSAGE = "Reciclado cargado.";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String FILENAME = "user_recycling_file";
    private static final String SAVE_FILE_ERROR_MESSAGE = "Error al guardar el archivo";
    private static final String LOAD_FILE_ERROR_MESSAGE = "Error al cargar el archivo";
    private static final String FIELD_ERROR_MESSAGE = "Error en los campos";
    private static final String NOT_SAVE_ERROR_MESSAGE = "Para enviar un reciclado primero debe cargarlo";


    private android.support.v7.widget.Toolbar toolbar;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recycling_activity, menu); // make toolbar items' visible
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                // show a help dialog
                new RecyclingDialog().show(getSupportFragmentManager(), "");
                break;
            case R.id.logout:
                // Do the logout. Return to login activity
                Intent returnLogin = new Intent(getApplicationContext(), LoginActivity.class);
                setResult(Activity.RESULT_OK, returnLogin);
                finish();
                break;
        }
        return true;
    }

    private void initComponents() {
        // Getting the username
        Bundle extras = getIntent().getExtras();
        username = (String) extras.get(LoginActivity.USERNAME);

        // Mapping Edit Texts
        bottles = (EditText) findViewById(R.id.bottlesText);
        bottles.setText("0");
        bottles.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI); // Disable "fullscreen" on edit text landscape
        tetrabriks = (EditText) findViewById(R.id.tetrabriksText);
        tetrabriks.setText("0");
        tetrabriks.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        paperboard = (EditText) findViewById(R.id.paperboardText);
        paperboard.setText("0");
        paperboard.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        glass = (EditText) findViewById(R.id.glassText);
        glass.setText("0");
        glass.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        cans = (EditText) findViewById(R.id.cansText);
        cans.setText("0");
        cans.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        // Mapping buttons and setting listeners
        loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Save locally the UserRecycling data
                loadUserRecycling();
                if (userRecycling != null) {
                    // Internal Storage
                    saveUserRecycling();
                    cleanFields();
                    userRecycling = null; // we set it as null to control the send data
                    Toast.makeText(getApplicationContext(), RECYCLING_LOAD_MESSAGE, Toast.LENGTH_LONG).show();
                }
            }
        });
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserRecyclingData();
                if (userRecycling != null &&    // if fields are not "0" it means that there is a no saved recycling
                        (bottles.getText().toString().equals("0") && tetrabriks.getText().toString().equals("0") && paperboard.getText().toString().equals("0") && glass.getText().toString().equals("0") && cans.getText().toString().equals("0"))) {
                    new SendUserRecyclingWS().execute(); // Start AsyncTask to send the UserRecycling
                    deleteUserRecyclingData(); // Remove local saved data
                } else {
                    Toast.makeText(getApplicationContext(), NOT_SAVE_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                }
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
                //new GetRecyclingWebService(getApplicationContext(), username).execute(); // this class could be inside the activity, like MyAsinkTask
                Intent allRecyclingActivity = new Intent(getApplicationContext(), AllRecyclingActivity.class);
                allRecyclingActivity.putExtra(LoginActivity.USERNAME, username); // send the local recycling?
                startActivity(allRecyclingActivity);

            }
        });
    }

    // Save User Recycling data locally
    private void saveUserRecycling() {
        /**
         * We must load previous file data and add the new data.
         * Suppose we saved bottles:10, tetrabriks: 4, paperboard: 20, glass: 1, cans: 40
         * and now, we want to save bottles:3, tetrabriks:2, paperboard: 5, glass: 0, cans: 0
         * We must add the new data, so we will get (saved) bottles:13, tetrabriks: 6, paperboard:25, glass: 1, cans: 40
         */
        try {
            // Load from internal storage
            loadUserRecyclingData();
            // Saving the file
            OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));

            osw.write(userRecycling.toJSONObject().toString());
            osw.close();
        } catch (Exception e) {
            Log.e("Save Error", e.getMessage());
            Toast.makeText(getApplicationContext(), SAVE_FILE_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
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
                    if (userRecycling != null) {
                        // Do the sum between the saved data (json) and the loaded data (userRecycling)
                        userRecycling.setBottles(userRecycling.getBottles() + json.getInt(UserRecycling.BOTTLES));  // don't like "bottles", "tetrabriks"...
                        userRecycling.setTetrabriks(userRecycling.getTetrabriks() + json.getInt(UserRecycling.TETRABRIKS));
                        userRecycling.setPaperboard(userRecycling.getPaperboard() + json.getInt(UserRecycling.PAPERBOARD));
                        userRecycling.setGlass(userRecycling.getGlass() + json.getInt(UserRecycling.GLASS));
                        userRecycling.setCans(userRecycling.getCans() + json.getInt(UserRecycling.CANS));
                    } else { // It's new (the 1st)
                        userRecycling = new UserRecycling(json);
                    }
                }
                br.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("File not Found", e.getMessage());
        } catch (IOException e) {
            Log.e("IO ", e.getMessage());
            Toast.makeText(getApplicationContext(), LOAD_FILE_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
            Toast.makeText(getApplicationContext(), LOAD_FILE_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    // Delete the user recycling data file from internal storage
    private void deleteUserRecyclingData() {
        File file = new File(this.getFilesDir(), FILENAME);
        file.delete();
    }

    // Get the data from the Edit Text and load the User Recycling object
    private void loadUserRecycling() {
        if (!bottles.getText().toString().equals("0") || !tetrabriks.getText().toString().equals("0") || !paperboard.getText().toString().equals("0") ||
                !glass.getText().toString().equals("0") || !cans.getText().toString().equals("0")) {
            try {
                JSONObject json = new JSONObject();
                json.put(UserRecycling.BOTTLES, Integer.parseInt(bottles.getText().toString()));
                json.put(UserRecycling.TETRABRIKS, Integer.parseInt(tetrabriks.getText().toString()));
                json.put(UserRecycling.PAPERBOARD, Integer.parseInt(paperboard.getText().toString()));
                json.put(UserRecycling.GLASS, Integer.parseInt(glass.getText().toString()));
                json.put(UserRecycling.CANS, Integer.parseInt(cans.getText().toString()));
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                json.put(UserRecycling.DATE, sdf.format(new Date()));
                userRecycling = new UserRecycling(json);
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.e("Error json", e.getMessage());
            }
        } else {
            Toast.makeText(getApplicationContext(), FIELD_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UserRecycling.BOTTLES, bottles.getText().toString());
        outState.putString(UserRecycling.TETRABRIKS, tetrabriks.getText().toString());
        outState.putString(UserRecycling.PAPERBOARD, paperboard.getText().toString());
        outState.putString(UserRecycling.GLASS, glass.getText().toString());
        outState.putString(UserRecycling.CANS, cans.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bottles.setText(savedInstanceState.getString(UserRecycling.BOTTLES));
        tetrabriks.setText(savedInstanceState.getString(UserRecycling.TETRABRIKS));
        paperboard.setText(savedInstanceState.getString(UserRecycling.PAPERBOARD));
        glass.setText(savedInstanceState.getString(UserRecycling.GLASS));
        cans.setText(savedInstanceState.getString(UserRecycling.CANS));
    }

    /**
     * This AsyncTask can be used to send data to the service or save data locally.
     */
    private class SendUserRecyclingWS extends AsyncTask<URL, Integer, Long> {

        private static final String RECYCLING_SENT_MESSAGE = "Se han enviado sus reciclados";
        private static final String RECYCLING_SENT_ERROR_MESSAGE = "Error al conectar al servidor";
        private boolean sent = false;

        @Override
        protected void onPostExecute(Long aLong) {
            if (sent) {
                Toast.makeText(getApplicationContext(), RECYCLING_SENT_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), RECYCLING_SENT_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
            }
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
                        sent = true;
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
