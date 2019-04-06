package com.example.lauti.finalintromoviles.activities;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 *
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 *
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.example.lauti.finalintromoviles.database.UsersDbHelper;
import com.example.lauti.finalintromoviles.model.UserContract;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private Spinner usernameSpin;
    private Button loginButton;
    private Button registerButton;
    private Switch switchLogin; // Used to remember the user logged
    private SharedPreferences preferences;
    private String username;
    private List<String> registeredUsernames = new ArrayList<>();

    // Variable to control the activity response ¿?¿?
    private static final int REGISTER = 1;

    public static final String USERNAME = "usernames"; // Used to save the shared preferences and to pass the username to another activity
    // This are use as messages in a Toast
    private static final String SELECT_USER = "Seleccione un usuario o registre uno nuevo";
    private static final String CREATE_NEW_USER = "Debe crear un nuevo usuario para continuar.";
    private static final String NOT_RESPONSE_MESSAGE = "No se ha obtenido respuesta.";
    private static final String REGISTERED_MESSAGE = "Usuario Registrado.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        // Restore shared preferences
        preferences = getSharedPreferences(USERNAME, MODE_PRIVATE);
        new LoadPreferencesTask().execute();
    }

    private void initComponents() {

        //PARA VER TODOS LO QUE HAY EN LA BASE
       /* UsersDbHelper dbHelper = new UsersDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = dbHelper.getReadableDatabase().query(UserContract.UserEntry.TABLE_NAME,null,null,null,null,null,null);

        while (c.moveToNext())
            Log.d("todos ",c.getString(c.getColumnIndex(UserContract.UserEntry.USERNAME)));
        c.close();
*/
        usernameSpin = (Spinner) findViewById(R.id.spinner);
        switchLogin = (Switch) findViewById(R.id.switchLogin);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! usernameSpin.getSelectedItem().toString().equals(SELECT_USER)) {
                    Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
                    username = usernameSpin.getSelectedItem().toString();
                    recyclingActivity.putExtra(USERNAME, username);
                    startActivity(recyclingActivity);
                    finish(); // que hacer cuando cierro sesion?? start loginActivity y finish?
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
                startActivityForResult(intent, REGISTER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REGISTER:  // we receive the new username
                if (resultCode == RESULT_OK) {  // if the result code is ok
                    // get the username
                    Bundle extras = intent.getExtras();
                    username = (String) extras.get(USERNAME);
                    saveUsername(username);

                    Intent recyclingActivity = new Intent(getApplicationContext(), RecyclingActivity.class);
                    recyclingActivity.putExtra(USERNAME, username);
                    startActivity(recyclingActivity);
                    finish();
                } else {
                    Toast.makeText(this, NOT_RESPONSE_MESSAGE, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    // save username in SharedPreferences
    private void saveUsername(String usernamep) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME, usernamep);
        //editor.commit();
        editor.apply(); // guarda asincrónicamente y seguro

        loadSpinner(usernamep);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveUsername(username);
    }

    private void loadSpinner(String username) {
        if (registeredUsernames != null) {

            UsersDbHelper dbHelper = new UsersDbHelper(getApplicationContext());
            //get all users from the DB
            Cursor c = dbHelper.getReadableDatabase().query(UserContract.UserEntry.TABLE_NAME,null,null,null,null,null,null);
            //because the size of  the spinner changes, we need to create the size of the string dinamically, so, every time
            //need check the number of  the entries and +3 statically, because set 3 options for default
            //IF don´t do this, the spinner crash when scroll to the end of the view
            String [] tospinner= new String[dbHelper.getEntriesCount()+3];
            //static entries to show in the spinner
            tospinner[0] = SELECT_USER;
            tospinner[1] = username;
            tospinner[2] = "lisams";
            int pos = 3;
            while (c.moveToNext()){
                tospinner[pos] = c.getString(c.getColumnIndex(UserContract.UserEntry.USERNAME));
                pos++;}
            c.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, tospinner);
            //new String[]{SELECT_USER, username, "lisams"});
            usernameSpin.setAdapter(adapter);
        }
    }

    private class LoadPreferencesTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(USERNAME, getApplicationContext().MODE_PRIVATE);
            loadSpinner(sharedPreferences.getString(USERNAME, "")); // if there is no shared preferences
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
