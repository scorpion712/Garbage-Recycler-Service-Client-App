package com.example.lauti.finalintromoviles.model;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link: https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

public class UserRecycling {
    private int bottles;
    private int tetrabriks;
    private int paperboard;
    private int glass;
    private int cans;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Date format
    private String date;

    public void setBottles(int bottles) {
        this.bottles = bottles;
    }

    public void setTetrabriks(int tetrabriks) {
        this.tetrabriks = tetrabriks;
    }

    public void setPaperboard(int paperboard) {
        this.paperboard = paperboard;
    }

    public void setGlass(int glass) {
        this.glass = glass;
    }

    public void setCans(int cans) {
        this.cans = cans;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // JSON to be send in a HTTP Request POST method
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("bottles", bottles);
            json.put("tetrabriks", tetrabriks);
            json.put("paperboard", paperboard);
            json.put("glass", glass);
            json.put("cans", cans);
            json.put("date", date.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Create a User Recycling using a json
    public UserRecycling(JSONObject json) {
        try {
            this.bottles = json.getInt("bottles");
            this.tetrabriks = json.getInt("tetrabriks");
            this.paperboard = json.getInt("paperboard");
            this.glass = json.getInt("glass");
            this.cans = json.getInt("cans");
            this.date = json.getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getTetrabriks() {
        return tetrabriks;
    }

    public int getPaperboard() {
        return paperboard;
    }

    public int getGlass() {
        return glass;
    }

    public int getCans() {
        return cans;
    }

    public int getBottles() {
        return bottles;

    }

    public String getDate() {
        return date;
    }
}
