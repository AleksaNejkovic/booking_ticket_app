package com.example.buyyourticket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signIn = findViewById(R.id.btnSingIn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        EditText email = findViewById(R.id.inputLogEmail);
        Map<String, String> params = new HashMap<String, String>();
        String useremail = email.getText().toString();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(useremail);
        if (!matcher.matches()) {
            displayError("Invalid email!");
            return;
        }
        params.put("email", useremail);
        JSONObject json = new JSONObject(params);
        String url = Routing.getRoute("auth");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int id = response.getInt("id");
                    User.id = id;
                    User.email = useremail;
                    Intent mIntent = new Intent(MainActivity.this, SportEvents.class);
                    startActivity(mIntent);
                } catch (JSONException e) {

                    displayError("Login failed");
                    Log.e("login", e.getMessage(), e);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = VolleyErrorHandler.getErrorMessage(error);

                // no expected error status codes
                // every invalid response should be treated as an error
                Log.e("login", message, error);

                displayError(message);
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void displayError(String message) {
        TextView errorMessage = findViewById(R.id.errorMessage);
        errorMessage.setText(message);
    }


}