package com.example.lauti.finalintromoviles.activities;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;

import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private Spinner usernameSpin;
    private Button loginButton;
    private Button registerButton;
    private Switch switchLogin; // Used to remember the user logged
    private SharedPreferences preferences;
    private String username;

    public static final String USERNAME = "username"; // Used to save the shared preferences and to pass the username to another activity

    // This are use as messages in a Toast
    private static final String SELECT_USER = "Seleccione un usuario o registre uno nuevo";
    private static final String CREATE_NEW_USER = "Debe crear un nuevo usuario para continuar.";
    private static final String NOT_RESPONSE_MESSAGE = "No se ha obtenido respuesta.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        // Restore shared preferences
        preferences = getSharedPreferences(USERNAME, MODE_PRIVATE); // without this line the SharedPreferences aren't loaded
        new LoadPreferencesTask().execute();
    }

    private void initComponents() {
        usernameSpin = (Spinner) findViewById(R.id.spinner);
        switchLogin = (Switch) findViewById(R.id.switchLogin);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usernameSpin.getSelectedItem().toString().equals(SELECT_USER)) {
                    Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
                    username = usernameSpin.getSelectedItem().toString();
                    recyclingActivity.putExtra(USERNAME, username);
                    startActivity(recyclingActivity);
                    finish();
                } else {
                    if (usernameSpin.getAdapter().isEmpty()) {
                        Toast.makeText(getApplicationContext(), CREATE_NEW_USER, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), SELECT_USER, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        registerButton = (Button) findViewById(R.id.newButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (resultCode) {
            case RESULT_OK:  // we receive the new username
                // get the username
                Bundle extras = intent.getExtras();
                username = (String) extras.get(USERNAME);
                saveUsername();
                Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
                recyclingActivity.putExtra(USERNAME, username);
                startActivity(recyclingActivity);
                finish();
                break;
            default:
                Toast.makeText(this, NOT_RESPONSE_MESSAGE, Toast.LENGTH_LONG).show();
                break;
        }
    }

    // save username in SharedPreferences
    private void saveUsername() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME, username);
        //editor.commit();
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveUsername();
    }

    private void loadSpinner(String username) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, new String[]{SELECT_USER, username});
        usernameSpin.setAdapter(adapter);
    }

    private class LoadPreferencesTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences
            preferences = getSharedPreferences(USERNAME, getApplicationContext().MODE_PRIVATE);
            loadSpinner(preferences.getString(USERNAME, "")); // if there is no shared preferences
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(USERNAME, username);
            //editor.commit();
            editor.apply();
        }

        @Override
        protected Long doInBackground(URL... urls) {
            return null;
        }
    }
}
