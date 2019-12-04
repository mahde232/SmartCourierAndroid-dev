package com.raghdak.wardm.smartcourier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.raghdak.wardm.smartcourier.model.Delivery;
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


public class ViewDeliveriesActivity extends AppCompatActivity {
    private static final String TAG = "ViewDeliveriesActivity";
    private ListView listView;
    private Button btnNavigate;
    private Button btnFinish;
    private Button btnBack;
    private double firstLat;
    private double firstLng;
    static private boolean isUrgentScreen = false;
    List<String> addresses;
    ArrayList<Delivery> deliveries;
    ArrayList<Delivery> deliveriesToOrder;
    protected static final int PERMISSION_ACCESS_COARSE_LOCATION = 111;
    LocationManager mLocationManager;

    static public void setIsUrgentScreen(boolean isUrgentScreen){
        ViewDeliveriesActivity.isUrgentScreen = isUrgentScreen;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deliveries);
        listView = (ListView) findViewById(R.id.list);
        btnNavigate = (Button) findViewById(R.id.btnNavigate);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        Bundle bundle = getIntent().getExtras();
        deliveriesToOrder = (ArrayList<Delivery>) bundle.get("deliveriesToDeliver");
        orderDeliveries(deliveriesToOrder);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if(isUrgentScreen)
                    i = new Intent(getApplicationContext(), MainActivity.class);
                else
                    i = new Intent(getApplicationContext(), RegionActivity.class);
                startActivity(i);
                finish();
            }

        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: ");
                String uri = "waze://?ll=" + deliveries.get(0).getLatitude() + "," + deliveries.get(0).getLongitude() + "&navigate=yes";
                //String uri = "waze://ul?q=66%20Acacia%20Avenue";
                Intent i = new Intent(getApplicationContext(), ReportActivity.class);
                i.putExtra("deliveryID", deliveries.get(0).getId());
                Delivery toType3 = deliveries.remove(0);
                //Change the removed delivery to type 3


                Integer type = 3;
                RequestQueue queue = Volley.newRequestQueue(ViewDeliveriesActivity.this); // this = context
                final String url = "http://" + User.ip + "/delivery/updateType/" + toType3.getId() + '/' + type;
                // prepare the Request
                JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("success","Changed to type 3");
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.toString());
                            }
                        }
                );



                // add it to the RequestQueue  
                queue.add(getRequest);




                //Go to next screen.
                if (deliveries.size() == 0)
                    btnNavigate.setEnabled(false);
                addresses = new ArrayList<String>();
                for (Delivery delivery : deliveries) {
                    addresses.add(delivery.getAddress());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewDeliveriesActivity.this, android.R.layout.simple_list_item_1, addresses);
                listView.setAdapter(adapter);
                startActivity(i);
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

            }

        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "waze://?ll=" + firstLat + "," + firstLng + "&navigate=yes";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                finish();
            }

        });
    }


    private void orderDeliveries(final ArrayList<Delivery> deliveriesToOrder) {
        String URL_FOR_GOOGLE_DIRECTIONS = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
            return;
        }
        //Location location = locationManager.getLastKnownLocation(locationManager
        //        .getBestProvider(criteria, false));
        Location location = getLastKnownLocation();
        firstLat = location.getLatitude();
        firstLng = location.getLongitude();
        URL_FOR_GOOGLE_DIRECTIONS += firstLat + "," + firstLng + "&destination=" + firstLat + "," + firstLng + "&waypoints=optimize:true";
        for (Delivery delivery : deliveriesToOrder) {
            URL_FOR_GOOGLE_DIRECTIONS += "|" + delivery.getLatitude() + "," + delivery.getLongitude();
        }
        URL_FOR_GOOGLE_DIRECTIONS += "&key=" + getText(R.string.google_maps_key).toString();
        //get the sub-region
        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_GOOGLE_DIRECTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("VolleyResponse", "Google Directions: response length is :" + response.length());

                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                    if (!jObj.get("status").equals("OK")) {
                        Log.d("VolleyResponse", "Google Directions: response is :" + jObj.get("status"));
                    } else {
                        //Get JSON Array called "routes" and then get the 1th complete object as JSON
                        JSONArray waypointsOrder = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("waypoint_order");
                        ArrayList<Integer> newOrder = new ArrayList<Integer>();
                        if (waypointsOrder != null) {
                            for (int i = 0; i < waypointsOrder.length(); i++) {
                                newOrder.add(waypointsOrder.getInt(i));
                            }
                        }
                        deliveries = new ArrayList<Delivery>();
                        for (int index : newOrder) {
                            deliveries.add(deliveriesToOrder.get(newOrder.get(index)));
                        }
                        addresses = new ArrayList<String>();
                        for (Delivery delivery : deliveries) {
                            addresses.add(delivery.getAddress());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewDeliveriesActivity.this, android.R.layout.simple_list_item_1, addresses);
                        listView.setAdapter(adapter);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyResponse", "Geocoding Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, "Google Directions");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
            return null;
        }
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}