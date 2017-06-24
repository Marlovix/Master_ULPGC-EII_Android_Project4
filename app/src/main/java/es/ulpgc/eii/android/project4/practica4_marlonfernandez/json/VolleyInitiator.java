package es.ulpgc.eii.android.project4.practica4_marlonfernandez.json;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class VolleyInitiator {
    private static VolleyInitiator volley = null;
    private RequestQueue requestQueue;

    private VolleyInitiator(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    static VolleyInitiator getInstance(Context context) {
        if (volley == null) {
            volley = new VolleyInitiator(context);
        }
        return volley;
    }

    RequestQueue getRequestQueue() {
        return requestQueue;
    }

}
