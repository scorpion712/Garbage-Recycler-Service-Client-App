package com.example.lauti.finalintromoviles.activities;
/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented Architecture.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.dialogs.RegisterDialog;
import com.example.lauti.finalintromoviles.model.User;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText address;
    private EditText email;


    private User user = new User(); // we use this class to save the data to send on the HTTP Request Body in a JSON

    private static final String FIELDS_ERROR = "Error en los campos";

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // adding the customized toolbar


        initComponents();
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
                new RegisterDialog().show(getSupportFragmentManager(), "");
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
        firstName = (EditText) findViewById(R.id.nameText);
        lastName = (EditText) findViewById(R.id.lastNameText);
        username = (EditText) findViewById(R.id.usernameText);
        address = (EditText) findViewById(R.id.addressText);
        email = (EditText) findViewById(R.id.emailText);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("") || username.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), FIELDS_ERROR, Toast.LENGTH_LONG).show();
                } else {
                    saveUserData();
                    // Do POST registering the new user
                    new RegisterUserWS().execute();
                }
            }
        });
    }

    // Get the data from Edit Texts and save it into a User and then save it into de DB
    private void saveUserData() {
        user.setFirstname(firstName.getText().toString());
        user.setLastname(lastName.getText().toString());
        user.setUsername(username.getText().toString());
        user.setAddress(address.getText().toString());
        user.setEmail(email.getText().toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class RegisterUserWS extends AsyncTask<Void, Void, String> {

        private static final String API_LOCALITATION = "http://10.0.2.2:8080/api/";
        private static final String REGISTERED_MESSAGE = "Usuario Registrado.";
        private String linkRequestAPI = "users/";

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // We need to save shared preferences (if the WS was correctly consumed), so we don't start RecyclingActivity.
            // We go back to Login, save the shared preferences and start the Recycling Activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra(LoginActivity.USERNAME, username.getText().toString());
            if (result.equals(REGISTERED_MESSAGE)) { //  if we registered the user return to login and first show a message saying User is registered successfully
                setResult(Activity.RESULT_OK, returnIntent);
                Toast.makeText(getApplicationContext(), REGISTERED_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                setResult(Activity.RESULT_CANCELED, returnIntent);
            }
            finish();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                URL url = null;
                try {
                    // Create connection to the API
                    url = new URL(API_LOCALITATION + linkRequestAPI); // WS URL
                    HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();

                    JSONObject postJSON = new JSONObject(user.toJSONObject().toString()); // JSON Object to send on POST

                    // Connection Parameters
                    myConnection.setReadTimeout(15000 /* milliseconds */);
                    myConnection.setConnectTimeout(15000 /* milliseconds */);
                    myConnection.setRequestMethod("POST"); // It can be any HTTP Request Method like DELETE, PUT...
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setDoInput(true);  // we do a input with POST

                    // Get Request Response
                    OutputStream os = myConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postJSON.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = myConnection.getResponseCode();// connection OK?
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        return result = REGISTERED_MESSAGE;
                    } else {
                        Log.e("Result HTTP",""+ responseCode);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.e("Error", e.getMessage());
                }
            }
            return result ;
        }

    }

}