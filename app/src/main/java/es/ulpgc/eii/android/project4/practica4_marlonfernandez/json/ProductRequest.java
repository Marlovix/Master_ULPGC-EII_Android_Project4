package es.ulpgc.eii.android.project4.practica4_marlonfernandez.json;

import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment.ProductFormFragment;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Product;

public class ProductRequest {
    private ProductFormFragment productFormFragment;
    private VolleyInitiator volley;
    private RequestQueue requestQueue;
    private boolean operationStatus = false;

    public ProductRequest(ProductFormFragment productFormFragment) {
        this.productFormFragment = productFormFragment;
        volley = VolleyInitiator.getInstance(productFormFragment.getContext());
        requestQueue = volley.getRequestQueue();
    }

    private void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (requestQueue == null)
                requestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            requestQueue.add(request);
        }
    }

    public boolean insertProduct(Product product) {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?InsertProduct";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", product.getName());
            jsonObject.put("description", product.getDescription());
            jsonObject.put("price", product.getPrice());
        } catch (Exception e) {
            e.getStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        operationStatus = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                operationStatus = false;
                String message = volleyError.toString();
                Toast.makeText(productFormFragment.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
        return operationStatus;
    }

    public boolean updateProduct(Product product) {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?UpdateProduct";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDProduct", product.getIdProduct());
            jsonObject.put("name", product.getName());
            jsonObject.put("description", product.getDescription());
            jsonObject.put("price", product.getPrice());
        } catch (Exception e) {
            e.getStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        operationStatus = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                operationStatus = false;
                String message = volleyError.toString();
                Toast.makeText(productFormFragment.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
        return operationStatus;
    }

}
