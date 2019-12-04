package com.raghdak.wardm.smartcourier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.raghdak.wardm.smartcourier.SQL.DatabaseHelper;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.Region;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.User;
import com.raghdak.wardm.smartcourier.protocol.response.RegionResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class RegionActivity extends AppCompatActivity {

    private static final String TAG = "RegionActivity";


    private Button btnBack;
    private Button btnViewDeliveries;
    private Spinner spinner;
    ArrayList<Region> regions = new ArrayList<Region>();
    ArrayList<String> regionsNames = new ArrayList<String>();
    private Context context = this;
    private DatabaseHelper databaseHelper;
    private ArrayList<Delivery> deliveriesToDeliver = new ArrayList<Delivery>();;
    private ArrayList<String> subRegionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        spinner = (Spinner) findViewById(R.id.spnrRegion);
        //databaseHelper = DatabaseHelper.getInstance(this);
        User user = User.currentUser;
        RequestQueue queue = Volley.newRequestQueue(this); // this = context
        final String url = "http://" + User.ip + "/region/getRegions/" + user.getId();

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        //JSONArray jArray = (JSONArray)response;
                        if(response.length() > 0) {
                            regionsNames.add("בחר אזור");
                            for (int countItem = 0; countItem < response.length(); countItem++) {
                                try {
                                    JSONObject region = response.getJSONObject(countItem);
                                    Gson gson = new Gson();
                                    Region regionItem = gson.fromJson(region.toString(), Region.class);
                                    if (regionItem != null)
                                        regions.add(regionItem);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, regionsNames);
                                    spinner.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (Region regionIt : regions)
                                regionsNames.add(regionIt.getRegionName());
                        }
                        Log.d("Response", response.toString());
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


        /*for(Region region: regions)
            regionsNames.add(region.getRegionName());
        spinner = (Spinner) findViewById(R.id.spnrRegion);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, regionsNames);
        spinner.setAdapter(adapter);*/
        //subRegions = (Spinner) findViewById(R.id.spnrSubRegion);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnViewDeliveries = (Button) findViewById(R.id.btnGetTrack);
        /*(deliveries = getRegionList(regions.getSelectedItem().toString());
        if (deliveries == null) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.no_deliveries_in_region), Toast.LENGTH_LONG).show();
        } else {
            //subRegionsList = new ArrayList<String>();
            //subRegionsList.add(getResources().getString(R.string.all_deliveries));
            //for (Delivery delivery : deliveries) {
            //    subRegionsList.add(delivery.getSubArea());
            //}
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, subRegionsList);
            subRegions.setAdapter(adapter);
        }*/
        //--------------------------------------------------------------------------------------
        btnBack.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               /* String selectedRegion = (String) parentView.getItemAtPosition(position);
                deliveries = getRegionList(regions.getSelectedItem().toString());
                subRegionsList = new ArrayList<String>();
                subRegionsList.add( getResources().getString(R.string.all_deliveries));
                if (deliveries == null) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.no_deliveries_in_region), Toast.LENGTH_LONG).show();
                } else {
                    for (Delivery delivery : deliveries) {
                        if(!subRegionsList.contains(delivery.getSubArea()))
                            subRegionsList.add(delivery.getSubArea());
                    }
                }
                ArrayAdapter<String> updatedAdapter = new ArrayAdapter<String>(RegionActivity.this,
                        android.R.layout.simple_spinner_item, subRegionsList);
                subRegions.setAdapter(updatedAdapter);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //--------------------------------------------------------------------------------------
        btnViewDeliveries.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                String choosedRegion = spinner.getSelectedItem().toString();
                if(choosedRegion == "בחר אזור")
                {
                    Context context = getApplicationContext();
                    CharSequence text = "אנא בחר אזור";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }
                String choosedRegionId = "";
                for(Region regionIt: regions)
                    if(regionIt.getRegionName() == choosedRegion) {
                        choosedRegionId = regionIt.getId();
                        break;
                    }
                if(choosedRegionId == "")
                    return;
                //Get all couriers' deliveries.
                RequestQueue queue = Volley.newRequestQueue(RegionActivity.this); // this = context
                User user = User.currentUser;
                final String url = "http://" + User.ip + "/region/getDeliveries/" + choosedRegionId + '/' + user.getId() + "/toDeliver";
                // prepare the Request
                JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response) {
                                //Get courier deliveries in region
                                if(response.length() > 0) {
                                    for (int index = 0; index < response.length(); index++) {
                                        try {
                                            JSONObject responseJSON = response.getJSONObject(index);
                                            //change delivery's type to 2.
                                            //////////////////////////////
                                            //insert the delivery to the deliveries list.
                                            Gson gson = new Gson();
                                            Delivery deliveryToDeliver = gson.fromJson(responseJSON.toString(), Delivery.class);
                                            if (deliveryToDeliver != null)
                                                deliveriesToDeliver.add(deliveryToDeliver);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                //Go to next screen or show error message.
                                if(deliveriesToDeliver.size() == 0){
                                    Context context = getApplicationContext();
                                    CharSequence text = "אין שליחויות להציג באזור זה";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }else{
                                    //deliveries.clear();
                                    ViewDeliveriesActivity.setIsUrgentScreen(false);
                                    Intent intent = new Intent(getApplicationContext(), ViewDeliveriesActivity.class);
                                    if(deliveriesToDeliver != null) {
                                        intent.putExtra("deliveriesToDeliver", deliveriesToDeliver);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                Log.d("Response", deliveriesToDeliver.toString());
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
                /*deliveries = getRegionList(regions.getSelectedItem().toString());
                if (deliveries == null) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.no_deliveries_in_region), Toast.LENGTH_LONG).show();
                }
                else {
                    if (subRegions.getSelectedItem() != getResources().getString(R.string.all_deliveries)) {
                        for (Delivery delivery : deliveries) {
                            if (!delivery.getSubArea().equals(subRegions.getSelectedItem())) {
                                deliveries.remove(delivery);
                            }
                        }
                    }
                 Launch User activity
                Intent intent = new Intent(getApplicationContext(), ViewDeliveriesActivity.class);
                intent.putExtra("deliveries", deliveries);
                startActivity(intent);
                finish();
                }*/
            }
        });

    }

    private ArrayList<Delivery> getRegionList(String region) {
        //RegionResponse regionResponse = databaseHelper.getRegionDeliveries(region);
        //if (regionResponse.getText().equals("OK")) {
            //deliveries = regionResponse.getDeliveries();
       //     return deliveries;
        //} else {
            //Toast.makeText(getApplicationContext(),
            //        regionResponse.getText(), Toast.LENGTH_LONG).show();
            return null;
        //}
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}


