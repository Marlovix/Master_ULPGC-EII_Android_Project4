package es.ulpgc.eii.android.project4.practica4_marlonfernandez.json;

import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment.CustomerFormFragment;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment.OrderFormFragment;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment.OrderListFragment;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment.ProductFormFragment;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Customer;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Order;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Product;

public class OrderRequest {
    private OrderListFragment listFragment;
    private OrderFormFragment formFragment;
    private CustomerFormFragment customerFormFragment;
    private ProductFormFragment productFormFragment;
    private VolleyInitiator volley;
    private RequestQueue requestQueue;
    private List<Order> orders = new ArrayList<>();
    private boolean operationStatus = false;

    public OrderRequest(OrderListFragment listFragment) {
        this.listFragment = listFragment;
        volley = VolleyInitiator.getInstance(listFragment.getContext());
        requestQueue = volley.getRequestQueue();
    }

    public OrderRequest(OrderFormFragment formFragment) {
        this.formFragment = formFragment;
        volley = VolleyInitiator.getInstance(formFragment.getContext());
        requestQueue = volley.getRequestQueue();
    }

    public OrderRequest(CustomerFormFragment customerFormFragment) {
        this.customerFormFragment = customerFormFragment;
        volley = VolleyInitiator.getInstance(customerFormFragment.getContext());
        requestQueue = volley.getRequestQueue();
    }

    public OrderRequest(ProductFormFragment productFormFragment) {
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

    public void queryOrdersCustomerForm() {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject orderJSON = (JSONObject) data.get(i);
                                int idOrder = orderJSON.getInt("IDOrder");
                                int idCustomer = orderJSON.getInt("IDCustomer");
                                String nameCustomer = orderJSON.getString("customerName");
                                Customer customer = new Customer(idCustomer, nameCustomer);
                                int idProduct = orderJSON.getInt("IDProduct");
                                String nameProduct = orderJSON.getString("productName");
                                float priceProduct = (float) orderJSON.getDouble("price");
                                Product product = new Product(idProduct, nameProduct, priceProduct);
                                String code = orderJSON.getString("code");
                                int quantity = orderJSON.getInt("quantity");
                                String date = orderJSON.getString("date");
                                int year = Integer.valueOf(date.substring(0, 4));
                                int month = Integer.valueOf(date.substring(5, 7));
                                int day = Integer.valueOf(date.substring(8));

                                Order order = new Order(idOrder, code, day, month, year, customer, product, quantity);
                                orders.add(order);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                operationStatus = false;
                Toast.makeText(customerFormFragment.getContext(),
                        volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
    }

    public void queryOrdersProductForm() {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject orderJSON = (JSONObject) data.get(i);
                                int idOrder = orderJSON.getInt("IDOrder");
                                int idCustomer = orderJSON.getInt("IDCustomer");
                                String nameCustomer = orderJSON.getString("customerName");
                                Customer customer = new Customer(idCustomer, nameCustomer);
                                int idProduct = orderJSON.getInt("IDProduct");
                                String nameProduct = orderJSON.getString("productName");
                                float priceProduct = (float) orderJSON.getDouble("price");
                                Product product = new Product(idProduct, nameProduct, priceProduct);
                                String code = orderJSON.getString("code");
                                int quantity = orderJSON.getInt("quantity");
                                String date = orderJSON.getString("date");
                                int year = Integer.valueOf(date.substring(0, 4));
                                int month = Integer.valueOf(date.substring(5, 7));
                                int day = Integer.valueOf(date.substring(8));

                                Order order = new Order(idOrder, code, day, month, year, customer, product, quantity);
                                orders.add(order);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                operationStatus = false;
                Toast.makeText(productFormFragment.getContext(),
                        volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
    }

    public void queryOrders() {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?QueryOrders";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject orderJSON = (JSONObject) data.get(i);
                                int idOrder = orderJSON.getInt("IDOrder");
                                int idCustomer = orderJSON.getInt("IDCustomer");
                                String nameCustomer = orderJSON.getString("customerName");
                                Customer customer = new Customer(idCustomer, nameCustomer);
                                int idProduct = orderJSON.getInt("IDProduct");
                                String nameProduct = orderJSON.getString("productName");
                                float priceProduct = (float) orderJSON.getDouble("price");
                                Product product = new Product(idProduct, nameProduct, priceProduct);
                                String code = orderJSON.getString("code");
                                int quantity = orderJSON.getInt("quantity");
                                String date = orderJSON.getString("date");
                                int year = Integer.valueOf(date.substring(0, 4));
                                int month = Integer.valueOf(date.substring(5, 7));
                                int day = Integer.valueOf(date.substring(8));

                                Order order = new Order(idOrder, code, day, month, year, customer, product, quantity);
                                orders.add(order);
                            }
                            Order[] result = new Order[orders.size()];
                            for (int i = 0; i < orders.size(); i++) {
                                result[i] = orders.get(i);
                            }
                            listFragment.setOrders(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                operationStatus = false;
                Toast.makeText(listFragment.getContext(),
                        volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
    }

    public boolean insertOrder(Order order) {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?InsertOrder";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("code", order.getCode());
            jsonObject.put("date", order.getDateDB());
            jsonObject.put("IDCustomer", order.getCustomer().getIdCustomer());
            jsonObject.put("IDProduct", order.getProduct().getIdProduct());
            jsonObject.put("quantity", order.getQuantity());
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
                Toast.makeText(listFragment.getContext(),
                        volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
        return operationStatus;
    }

    public boolean updateOrder(Order order) {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?UpdateOrder";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", order.getCode());
            jsonObject.put("date", order.getDateDB());
            jsonObject.put("IDOrder", order.getIdOrder());
            jsonObject.put("IDCustomer", order.getCustomer().getIdCustomer());
            jsonObject.put("IDProduct", order.getProduct().getIdProduct());
            jsonObject.put("quantity", order.getQuantity());
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
                Toast.makeText(listFragment.getContext(),
                        volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
        return operationStatus;
    }

    public boolean deleteOrder(int id) {
        String url = "http://tip.dis.ulpgc.es/ventas/server.php?DeleteOrder";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IDOrder", id);
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
                Toast.makeText(listFragment.getContext(),
                        volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        addToQueue(request);
        return this.operationStatus;
    }

    public Order[] getOrders() {
        Order[] result = new Order[0];
        if (orders != null) {
            result = new Order[orders.size()];
            for (int i = 0; i < orders.size(); i++) {
                result[i] = orders.get(i);
            }
        }
        return result;
    }

}
