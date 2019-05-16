package com.example.lauti.finalintromoviles.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Oneto, Fernando
 * @author: Diez, Lautaro
 * @Note: we use the Web Service did as the final practical work for the subject Service Oriented.
 * To find the project:
 * @link:https://github.com/scorpion712/Rest-Service-Garbage-Recycler
 */

public class UserRecycling {
    private int bottles;
    private int tetrabriks;
    private int paperboard;
    private int glass;
    private int cans;

    // Static variables used to access to json fields
    public static final String BOTTLES = "bottles";
    public static final String TETRABRIKS = "tetrabriks";
    public static final String PAPERBOARD = "paperboard";
    public static final String GLASS = "glass";
    public static final String CANS = "cans";
    public static final String DATE = "date";

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
            json.put(BOTTLES, bottles);
            json.put(TETRABRIKS, tetrabriks);
            json.put(PAPERBOARD, paperboard);
            json.put(GLASS, glass);
            json.put(CANS, cans);
            json.put(DATE, date.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Create a User Recycling using a json
    public UserRecycling(JSONObject json) {
        try {
            this.bottles = json.getInt(BOTTLES);
            this.tetrabriks = json.getInt(TETRABRIKS);
            this.paperboard = json.getInt(PAPERBOARD);
            this.glass = json.getInt(GLASS);
            this.cans = json.getInt(CANS);
            this.date = json.getString(DATE);
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
