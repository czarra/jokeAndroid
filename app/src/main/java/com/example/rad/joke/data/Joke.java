package com.example.rad.joke.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Joke {


    private String id;
    private String value;

    public Joke (String id, String value) {
        this.id=id;
        this.value=value;
    }

    public Joke() {
        this.id=null;
        this.value=null;
    }

    public static Joke fromJsonObject(JSONObject jsonObject) {
        try {
            return new Joke(jsonObject.getString("id"), jsonObject.getString("value"));

        } catch (JSONException exp) {
            Log.e("Task class", exp.getMessage());
        }
        return new Joke();
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

}
