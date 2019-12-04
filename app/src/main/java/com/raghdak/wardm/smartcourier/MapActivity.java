package com.raghdak.wardm.smartcourier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.raghdak.wardm.smartcourier.SQL.DatabaseHelper;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.Region;
import com.raghdak.wardm.smartcourier.model.User;
import com.raghdak.wardm.smartcourier.tools.AppSingleton;
import com.raghdak.wardm.smartcourier.tools.DataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.raghdak.wardm.smartcourier.ViewDeliveriesActivity.PERMISSION_ACCESS_COARSE_LOCATION;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Button btnGetTrack;
    private DatabaseHelper databaseHelper;
    static private ArrayList<Delivery> allDeliveries;
    private ArrayList<Delivery> urgentDeliveries;
    private ArrayList<Delivery> regularDeliveries;
    private ArrayList<Marker> clickedMarkers;
    private ArrayList<Marker> urgentMarkers;
    private double firstLat;
    private double firstLng;
    private HashMap<Marker, Delivery> markerDeliveryHashMap;
    private ArrayList<LatLng> latLngArray;
    private ArrayList<Delivery> selectedDeliveries;
    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
    LocationManager mLocationManager;

    static public void setAllDeliveries(ArrayList<Delivery> allDeliveries)
    {
        MapActivity.allDeliveries = allDeliveries;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //databaseHelper = DatabaseHelper.getInstance(this);
        urgentDeliveries = new ArrayList<Delivery>();
        //latLngArray = getLatLngArrayFromDeliveries(urgentDeliveries);
        regularDeliveries = new ArrayList<Delivery>();
        clickedMarkers = new ArrayList<Marker>();
        urgentMarkers = new ArrayList<Marker>();
        selectedDeliveries = urgentDeliveries;
        markerDeliveryHashMap = new HashMap<Marker,Delivery>();
        for(Delivery delivery : allDeliveries){
            if(delivery.getIsUrgent() == 1){
                urgentDeliveries.add(delivery);
            }
            else{
                regularDeliveries.add(delivery);
            }
        }
        maxLat = minLat = urgentDeliveries.get(0).getLatitude();
        maxLng = minLng = urgentDeliveries.get(0).getLongitude();
        for(Delivery delivery: urgentDeliveries){
            if(delivery.getLatitude() < minLng){
                minLng = delivery.getLongitude();
            }
            if(delivery.getLongitude() > maxLng){
                maxLng = delivery.getLongitude();
            }
            if(delivery.getLatitude() < minLat){
                minLat = delivery.getLatitude();
            }
            if(delivery.getLatitude() > maxLat){
                maxLat = delivery.getLatitude();
            }
        }
        minLng -= 0.1;
        minLat -= 0.1;
        maxLng += 0.1;
        maxLat += 0.1;
        //TODO: Add 10 km to the border 10km = 0.1 (in Coordinates)
        btnGetTrack = (Button) findViewById(R.id.get_urgent_track);
        btnGetTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch User activity
                ViewDeliveriesActivity.setIsUrgentScreen(true);
                Intent intent = new Intent(getApplicationContext(), ViewDeliveriesActivity.class);
                intent.putExtra("deliveriesToDeliver", selectedDeliveries);
                startActivity(intent);

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        LatLng deliveryLatLng = new LatLng(34,32);
        // Add a marker in Sydney and move the camera
        for(Delivery delivery : urgentDeliveries){
            deliveryLatLng = new LatLng(delivery.getLatitude(),delivery.getLongitude());
            Marker newMarker =mMap.addMarker(new MarkerOptions().position(deliveryLatLng));
            urgentMarkers.add(newMarker);
            markerDeliveryHashMap.put(newMarker,delivery);
        }
        for(Delivery delivery : regularDeliveries){
            if(isInBorder(delivery)) {
                deliveryLatLng = new LatLng(delivery.getLatitude(), delivery.getLongitude());
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(deliveryLatLng).alpha((float) 0.4));
                markerDeliveryHashMap.put(newMarker, delivery);
            }
        }
        latLngArray = new ArrayList<>();
        //Execute Directions API request
        orderDeliveries(urgentDeliveries);

        PolylineOptions lineOptions = new PolylineOptions().addAll(latLngArray).color(Color.BLUE).width(8);
        mMap.addPolyline(lineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(deliveryLatLng));

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //if(!urgentMarkers.contains(marker)){
            if(marker.getAlpha() == 1){
                marker.setAlpha((float)0.4);
                clickedMarkers.remove(marker);
                selectedDeliveries.remove(markerDeliveryHashMap.get(marker));
            }
            else{
                marker.setAlpha((float)1);
                clickedMarkers.add(marker);
                selectedDeliveries.add(markerDeliveryHashMap.get(marker));
            }
        //}
        return true;
    }


    private void orderDeliveries(final ArrayList<Delivery> deliveriesToOrder) {
        String URL_FOR_GOOGLE_DIRECTIONS = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
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

                DataParser parser = new DataParser();
                List<List<HashMap<String, String>>> routes = null;
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                    routes = parser.parse(jObj);
                    ArrayList<LatLng> points;
                    PolylineOptions lineOptions = null;

                    // Traversing through all the routes
                    for (int i = 0; i < routes.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = routes.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(10);
                        lineOptions.color(Color.RED);

                        Log.d("DrawingRoutes","DrawingRoutes lineoptions decoded");

                    }

                    // Drawing polyline in the Google Map for the i-th route
                    if(lineOptions != null) {
                        mMap.addPolyline(lineOptions);
                    }
                    else {
                        Log.d("DrawingRoutes","without Polylines drawn");
                    }

                }catch (JSONException e1) {
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

    boolean isInBorder(Delivery delivery){
        if(delivery.getLongitude() < minLng || delivery.getLongitude() > maxLng || delivery.getLatitude() < minLat || delivery.getLatitude() > maxLat)
            return false;
        else
            return true;
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
