package com.example.buyyourticket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SportEvents extends AppCompatActivity {
    List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_events);

        loadEvents();
    }

    private void loadEvents() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Routing.getRoute("users/{0}/events", User.id);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                events = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        events.add(new Event(response.getJSONObject(i)));
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                displayEventsData();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.getMessage();

                // expected stats codes:
                // - 404: user doesn't exist

                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 404:
                            message = MessageFormat.format("Invalid user id provided: {0}", User.id);
                            Log.w("getEvents", message);
                            break;

                        default:
                            // any other status code should be logged as error
                            message = VolleyErrorHandler.getErrorMessage(error);
                            Log.e("getEvents", message);
                            break;
                    }
                }

                TextView errorMessage = findViewById(R.id.errorMessage);
                errorMessage.setText(message);
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void displayEventsData() {
        MyAdapter adapter = new MyAdapter(getApplicationContext(), events);
        ListView listView = findViewById(R.id.sport_events);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = events.get(position);
                String result = MessageFormat.format("{0} {1} vs {2}", e.getSport(), e.getTeams().get(0), e.getTeams().get(1));
                Toast.makeText(SportEvents.this, result, Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(SportEvents.this, SelectedEvent.class);
                mIntent.putExtra("rId", e.getId());
                startActivity(mIntent);
            }
        });
    }

    class MyAdapter extends ArrayAdapter<Event> {
        List<Event> events;

        MyAdapter(Context context, List<Event> events) {
            super(context, R.layout.row_in_event, R.id.sport, events);
            this.events = events;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_in_event, parent, false);
            TextView sport = row.findViewById(R.id.sport);
            TextView tim1 = row.findViewById(R.id.team1);
            TextView tim2 = row.findViewById(R.id.team2);
            TextView start = row.findViewById(R.id.time1);
            TextView end = row.findViewById(R.id.time2);
            TextView location = row.findViewById(R.id.location);

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Event e = this.events.get(position);
            sport.setText(e.getSport());
            tim1.setText(e.getTeams().get(0));
            tim2.setText(e.getTeams().get(1));
            start.setText(fmt.format(e.getStart()));
            end.setText(fmt.format(e.getEnd()));
            location.setText(e.getAddress());

            return row;
        }
    }
}