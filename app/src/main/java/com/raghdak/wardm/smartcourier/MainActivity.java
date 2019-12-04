package com.raghdak.wardm.smartcourier;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.raghdak.wardm.smartcourier.SQL.DatabaseHelper;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.User;
import com.raghdak.wardm.smartcourier.tools.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button btnLogOut;
    private Button btnNewDelivery;
    private Button btnViewShipemtns;
    private Button btnViewUrgentDeliveries;
    private TextView allDeliveriesTextView;
    private TextView deliveredDeliveriesTextView;
    private DatabaseHelper databaseHelper;
    private ArrayList<Delivery> allDeliveries = new ArrayList<Delivery>();
    List<Address> addressesList;

    private void countAllDeliveries()
    {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this); // this = context
        User user = User.currentUser;
        final String url = "http://" + User.ip + "/courier/getDeliveries/" + user.getId() + "/toDeliver";
        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        allDeliveriesTextView.setText("" + response.length());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        allDeliveriesTextView.setText("" + -1);

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void countDeliveredDeliveries()
    {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this); // this = context
        User user = User.currentUser;
        final String url = "http://" + User.ip + "/courier/getDeliveries/" + user.getId() + "/delivered";
        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        deliveredDeliveriesTextView.setText("" + response.length());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        deliveredDeliveriesTextView.setText("" + -1);

                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countAllDeliveries();
        countDeliveredDeliveries();
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        //btnNewDelivery = (Button) findViewById(R.id.btnNewDelivery);
        btnViewShipemtns = (Button) findViewById(R.id.btnViewDeliveries);
        btnViewUrgentDeliveries = (Button) findViewById(R.id.btnViewUrgentDeliveries);
        allDeliveriesTextView = (TextView) findViewById(R.id.allDeliveriesTextView);
        deliveredDeliveriesTextView = (TextView) findViewById(R.id.deliveredDeliveriesTextView);

        //databaseHelper = DatabaseHelper.getInstance(this);
        //--------------------------------------------------------------------
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        //----------------------------------------------------------------
        /*btnNewDelivery.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NewDeliveryActivity.class);
                startActivity(i);
                finish();
            }
        });*/

        //----------------------------------------------------------------
        btnViewShipemtns.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegionActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnViewUrgentDeliveries.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this); // this = context
                User user = User.currentUser;
                final String url = "http://" + User.ip + "/courier/getDeliveries/" + user.getId() + "/toDeliver";
                // prepare the Request
                JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int countItem = 0; countItem < response.length(); countItem++) {
                                    Gson gson = new Gson();
                                    try {
                                        JSONObject delivery = response.getJSONObject(countItem);
                                        Delivery deliveryItem = gson.fromJson(delivery.toString(), Delivery.class);
                                        if (deliveryItem != null)
                                            allDeliveries.add(deliveryItem);
                                        MapActivity.setAllDeliveries(allDeliveries);
                                        Intent i = new Intent(getApplicationContext(), MapActivity.class);
                                        startActivity(i);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.toString());
                            }
                        }
                );

                // add it to the RequestQueue
                queue.add(getRequest);
            }
        });
    }


}
