package com.example.buyyourticket;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class VolleyErrorHandler {

    public static String getErrorMessage(VolleyError error) {
        String message = error.getMessage();
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String s=new String(error.networkResponse.data);
                JSONObject data = new JSONObject(s);

                String innerMessage = data.getString("message");
                if (innerMessage != null) {
                    // change the initial error message only if response object contains a different one
                    message = innerMessage;
                }
            } catch (JSONException e) {
                Log.e("errorResponseParsing", e.getMessage(), e);
            }
        }

        return message;
    }
}
