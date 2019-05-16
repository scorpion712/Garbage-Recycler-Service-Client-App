package com.example.lauti.finalintromoviles.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link:https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */
public class User {

    private String firstname;
    private String lastname;
    private String username;
    private String address="";
    private String email="";

    public User() {
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    // Build User object into JSON
    public JSONObject toJSONObject() {
        JSONObject userJSON = new JSONObject();
/*
        UsersDbHelper dbHelper = new UsersDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT FIRSTNAME,LASTNAME,USERNAME,ADDRESS,EMAIL FROM users",null);
        while(cursor.moveToNext()) {
            String firstnamedb = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserContract.UserEntry.FIRSTNAME));
            String lastnamedb = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserContract.UserEntry.LASTNAME));
            String usernamedb = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserContract.UserEntry.USERNAME));
            String addressdb = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserContract.UserEntry.ADDRESS));
            String emaildb = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserContract.UserEntry.EMAIL));
            try {
                userJSON.put("firstname", firstnamedb);
                userJSON.put("lastname", lastnamedb);
                userJSON.put("username", usernamedb);
                if (!address.equals("")) {
                    userJSON.put("address", addressdb);
                }
                if (!email.equals("")) {
                    userJSON.put("email", emaildb);
                }
            } catch (JSONException e) {
                Log.e("Error:", e.getMessage());
            }
        }
*/
        try {
            userJSON.put("firstname", firstname);
            userJSON.put("lastname", lastname);
            userJSON.put("username", username);
            if (!address.equals("")) {
                userJSON.put("address", address);
            }
            if (!email.equals("")) {
                userJSON.put("email", email);
            }
        } catch (JSONException e) {
            Log.e("Error:", e.getMessage());
        }

        return userJSON;

    }
}
