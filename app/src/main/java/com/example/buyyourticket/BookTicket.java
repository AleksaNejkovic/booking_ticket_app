package com.example.buyyourticket;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class BookTicket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_tickets);
        EditText email = (EditText) findViewById(R.id.email);
        email.setText(User.email);
        email.setEnabled(false);
        Bundle bundle = getIntent().getExtras();
        Button save_reservation = findViewById(R.id.save_reservation);
        save_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookTickets(bundle.getInt("event_id"), bundle.getInt("available_tickets"));
            }
        });


    }

    private void bookTickets(int eventId, int maxAvailableTickets) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        EditText ticket_number = (EditText) findViewById(R.id.ticket_number);
        //TO DO check if these two should be sent in request
        //EditText name=(EditText) findViewById(R.id.name);
        //EditText surname=(EditText) findViewById(R.id.surname);

        String url = Routing.getRoute("users/{0}/events/{1}/book", User.id, eventId);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.getMessage();

                // expected stats codes:
                // - 400: no more tickets to book
                // - 403: User has no access rights to this event
                // - 404: user doesn't exist

                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 400:
                            message = "Not enough available tickets for this event";
                            Log.i("bookTickets", message);
                            break;

                        case 403:
                            message = MessageFormat.format("User {0} has no access rights to event #{1}", User.id, eventId);
                            Log.w("bookTickets", message);
                            break;

                        case 404:
                            message = MessageFormat.format("Invalid user id provided: {0}", User.id);
                            Log.w("bookTickets", message);
                            break;

                        default:
                            // any other status code should be logged as error
                            message = VolleyErrorHandler.getErrorMessage(error);
                            Log.e("bookTickets", message, error);
                            break;
                    }
                }

                TextView errorMessage = findViewById(R.id.errorMessage);
                errorMessage.setText(message);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("tickets", ticket_number.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }
}
