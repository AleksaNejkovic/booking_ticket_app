package com.example.buyyourticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SelectedEvent extends AppCompatActivity {
    EventDetails eventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_event);

        Bundle bundle = getIntent().getExtras();

        loadEventDetails(bundle.getInt("rId"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(2, 2);
        startActivity(getIntent());
        overridePendingTransition(2, 2);
    }

    private void loadEventDetails(int eventId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Routing.getRoute("users/{0}/events/{1}", User.id, eventId);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    eventDetails = new EventDetails(response);
                    displayEventDetails();
                } catch (JSONException | ParseException e) {
                    TextView errorMessage = findViewById(R.id.errorMessage);
                    errorMessage.setText("Error fetching event details.");
                    Log.e("loadEventDetails", e.getMessage(), e);
                }

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayError(error, "getEventDetails", eventId);
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void cancelTickets() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Routing.getRoute("users/{0}/events/{1}/unbook", User.id, eventDetails.getId());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int deletedTickets = eventDetails.getTickets() - eventDetails.getAvailableTickets();
                Toast.makeText(getApplicationContext(), "You are canceled " + deletedTickets + " ticket/s!", Toast.LENGTH_SHORT).show();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayError(error, "deleteBookings", eventDetails.getId());
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void displayEventDetails() {
        TextView sport = findViewById(R.id.sports);
        TextView team1 = findViewById(R.id.team1);
        TextView team2 = findViewById(R.id.team2);
        TextView timeStart = findViewById(R.id.timeStart);
        TextView timeEnd = findViewById(R.id.timeEnd);
        TextView address = findViewById(R.id.address);
        TextView price = findViewById(R.id.price);
        TextView availableSeats = findViewById(R.id.available_seats);

        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sport.setText(eventDetails.getSport());
        team1.setText(eventDetails.getTeams().get(0));
        team2.setText(eventDetails.getTeams().get(1));
        timeStart.setText(fmt.format(eventDetails.getStart()));
        timeEnd.setText(fmt.format(eventDetails.getEnd()));
        address.setText(eventDetails.getAddress());
        price.setText(String.valueOf(eventDetails.getPrice()));
        availableSeats.setText(String.valueOf(eventDetails.getAvailableTickets()));

        Button buttonGetLoc = findViewById(R.id.GetMapLocation);
        buttonGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoLoc = MessageFormat.format("geo:{0},{1}", eventDetails.getLatitude(), eventDetails.getLongitude());
                Uri gmmIntentUri = Uri.parse(geoLoc);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                Toast.makeText(getApplicationContext(), "This is:" + geoLoc, Toast.LENGTH_LONG).show();
                startActivity(mapIntent);
            }
        });

        Button bookTickets = findViewById(R.id.book_tickets);
        bookTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(SelectedEvent.this, BookTicket.class);
                mIntent.putExtra("event_id", eventDetails.getId());
                mIntent.putExtra("available_tickets", eventDetails.getAvailableTickets());
                startActivity(mIntent);
            }
        });

        Button cancelTicket = findViewById(R.id.cancel_tickets);
        cancelTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTickets();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    private void displayError(VolleyError error, String tag, int eventId) {
        // expected errors:
        // - 403: user tried to access event that doesn't belong to him
        // - 404: user id unknown
        String message = error.getMessage();

        if (error.networkResponse != null) {
            switch (error.networkResponse.statusCode) {
                case 403:
                    message = MessageFormat.format("User {0} has no access rights to event #{1}", User.id, eventId);
                    Log.w(tag, message);
                    break;

                case 404:
                    message = MessageFormat.format("Invalid user id provided: {0}", User.id);
                    Log.w(tag, message);
                    break;

                default:
                    // any other status code should be logged as error
                    message = VolleyErrorHandler.getErrorMessage(error);
                    Log.e(tag, message, error);
                    break;
            }
        }

        TextView errorMessage = findViewById(R.id.errorMessage);
        errorMessage.setText(message);
    }
}






