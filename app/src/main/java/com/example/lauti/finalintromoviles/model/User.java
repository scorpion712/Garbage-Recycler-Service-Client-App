package com.example.lauti.finalintromoviles.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 *
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 *
 */
public class User {

    private String firstname;
    private String lastname;
    private String username;
    private String address;
    private String email;

    public User() {
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
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
        try {
        userJSON.put("firstname", firstname);
        userJSON.put("lastname", lastname);
        userJSON.put("username", username);
        userJSON.put("address", address);
        userJSON.put("email", email);
        } catch (JSONException e) {
            Log.e("Error:", e.getMessage());
        }
        return userJSON;

    }
}
