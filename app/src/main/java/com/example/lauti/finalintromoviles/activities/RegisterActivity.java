package com.example.lauti.finalintromoviles.activities;
/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented Architecture.
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;
import com.example.lauti.finalintromoviles.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
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
    private boolean requestOk = false; // this boolean is used to check if the user was registered correctly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
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

    // Get the data from Edit Texts and save it into a User
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
        private static final String ERROR_RESPONSE = "Error ";
        private String linkRequestAPI = "users/";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // We need to save shared preferences (if the WS was correctly consumed), so we don't start RecyclingActivity.
            // We go back to Login, save the shared preferences and start the Recycling Activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra(LoginActivity.USERNAME, username.getText().toString());
            if (requestOk) { //  if we registered the user return to login and first show a message saying User is registered successfully
                setResult(Activity.RESULT_OK, returnIntent);
                Toast.makeText(getApplicationContext(), REGISTERED_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                setResult(Activity.RESULT_CANCELED, returnIntent);
            }
            finish();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;

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
                        requestOk = true;
                        result = REGISTERED_MESSAGE;
                    } else {
                        result = new String(ERROR_RESPONSE + responseCode);
                        Log.e("Result", result);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.e("Error", e.getMessage());
                }
            }
            return result;
        }

    }

}
