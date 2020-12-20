package com.example.buyyourticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class EventDetails extends Event {
    private double price;
    private int tickets;
    private int availableTickets;

    public EventDetails(int id, String address, double latitude, double longitude, Date start, Date end, String sport, double price, int tickets, int available_tickets, List<String> teams) {
        super(id, address, latitude, longitude, start, end, sport, teams);
        this.price = price;
        this.tickets = tickets;
        this.availableTickets = available_tickets;
    }

    public EventDetails(JSONObject json) throws JSONException, ParseException {
        super(json);
        price = json.getDouble("price");
        availableTickets = json.getInt("available_tickets");
        tickets = json.getInt("tickets");
    }

    public double getPrice() { return price; }

    public int getTickets() {
        return tickets;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }
}
