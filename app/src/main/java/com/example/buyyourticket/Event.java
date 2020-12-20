package com.example.buyyourticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    protected int id;
    protected String address;
    protected double latitude;
    protected double longitude;
    protected Date start;
    protected Date end;
    protected String sport;
    protected List<String> teams;

    public Event(int id, String address, double latitude, double longitude, Date start, Date end, String sport, List<String> teams) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.start = start;
        this.end = end;
        this.sport = sport;
        this.teams = teams;
    }

    public Event(JSONObject json) throws JSONException, ParseException {
        this(json.getInt("id"),
             json.getString("address"),
             json.getDouble("lat"),
             json.getDouble("long"),
             INPUT_DATE_FORMAT.parse(json.getString("start")),
             INPUT_DATE_FORMAT.parse(json.getString("end")),
             json.getString("sport"),
             new ArrayList<>());

        JSONArray teamsArray = json.getJSONArray("teams");
        for (int i = 0; i < teamsArray.length(); i++) {
            teams.add(teamsArray.getJSONObject(i).getString("name"));
        }
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getSport() {
        return sport;
    }

    public List<String> getTeams() {
        return teams;
    }
}
