package com.example.lauti.finalintromoviles.activities;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.lauti.finalintromoviles.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LoginActivity extends AppCompatActivity {

    private Spinner usernameSpin;
    private Button loginButton;
    private Button registerButton;
    private Switch switchLogin; // Used to remember the user logged
    private SharedPreferences preferences;
    private String username;
    private Boolean rememberUser;
    private Set<String> usernameSet;

    // Variables used to save the SharedPreferences
    public static final String USERNAME = "usernames"; // This is also used to pass the username to another activity
    private static final String REMEMBER = "recordar usuario";
    private static final String TAG = "Login";
    private static final String USERNAMES_SET = "set de usernames";

    // This are use as messages in a Toast
    private static final String SELECT_USER = "Seleccione un usuario o registre uno nuevo";
    private static final String CREATE_NEW_USER = "Debe crear un nuevo usuario para continuar.";
    private static final String NOT_RESPONSE_MESSAGE = "No se ha obtenido respuesta.";

    private static final int REGISTER_REQUEST_CODE = 1;
    private static final int RECYCLING_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        restoreSharedPreferences();

        if (!username.equals("") && rememberUser) { // if there is a username and user chose to be remember
            Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
            recyclingActivity.putExtra(USERNAME, username);
            startActivityForResult(recyclingActivity, RECYCLING_REQUEST_CODE);
        } else {
            loadSpinner();
        }
    }

    // Restore shared preferences
    private void restoreSharedPreferences() {
        preferences = getSharedPreferences(TAG, getApplicationContext().MODE_PRIVATE);
        username = preferences.getString(USERNAME, "");
        usernameSet = preferences.getStringSet(USERNAMES_SET,  new HashSet<String>());
        rememberUser = preferences.getBoolean(REMEMBER, false);
    }

    private void initComponents() {
        usernameSpin = (Spinner) findViewById(R.id.spinner);
        switchLogin = (Switch) findViewById(R.id.switchLogin);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usernameSpin.getSelectedItem().toString().equals(SELECT_USER)) {
                    saveSharedPreferences(switchLogin.isChecked());
                    Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
                    username = usernameSpin.getSelectedItem().toString(); // set the selected username
                    saveSharedPreferences(switchLogin.isChecked()); // if the user check to remember save the SharedPreferences in case the user decide to logout after closing the app
                    recyclingActivity.putExtra(USERNAME, username);
                    startActivityForResult(recyclingActivity, RECYCLING_REQUEST_CODE);
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
                startActivityForResult(intent, REGISTER_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REGISTER_REQUEST_CODE:  // we receive the new username
                if (resultCode == Activity.RESULT_OK) {  // if the result code is ok
                    // get the username
                    Bundle extras = intent.getExtras();
                    username = (String) extras.get(USERNAME);
                    saveSharedPreferences(false);
                    Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
                    recyclingActivity.putExtra(USERNAME, username);
                    startActivityForResult(recyclingActivity, RECYCLING_REQUEST_CODE);
                } else {
                    Toast.makeText(this, NOT_RESPONSE_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;
            case RECYCLING_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) { // Logout, we must restore the boolean in the SharedPreferences
                    rememberUser = false;
                    switchLogin.setChecked(rememberUser);
                    saveSharedPreferences(rememberUser);
                    loadSpinner(); // load the spinner (for the case in which the user has restarted the app)
                }
            default:
                break;
        }
    }

    /**
     * Save the usernames and the check for remember the username logged
     *
     * @param check
     */
    private void saveSharedPreferences(boolean check) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME, username);
        if (!usernameSet.contains(username)) {
            usernameSet.add(username);
            editor.putStringSet(USERNAMES_SET, usernameSet);
        }
        editor.putBoolean(REMEMBER, check);
        //editor.commit();
        editor.apply(); // save safely

    }

    // Load the spinner with the saved usernames
    private void loadSpinner() {
        List<String> toSpinner = new ArrayList<>();
        // Entries to show in the spinner
        if (usernameSet.isEmpty()) {
            toSpinner.add(SELECT_USER);
        } else {
            // We add the username saved in the SharedPreferences first to take it more "practical" to login
            toSpinner.add(username);
            for (String s : usernameSet) {
                if (!s.equalsIgnoreCase(username)) {
                    toSpinner.add(s);
                }
            }
        }
        // Setting the adapter to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, toSpinner);
        usernameSpin.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSharedPreferences(rememberUser);
    }


}
