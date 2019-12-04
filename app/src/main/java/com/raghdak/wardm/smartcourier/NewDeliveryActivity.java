package com.raghdak.wardm.smartcourier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.raghdak.wardm.smartcourier.SQL.DatabaseHelper;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.tools.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewDeliveryActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnSave;
    private EditText claimantTxt, nameTxt, phoneTxt, poboxTxt;
    private DatePicker dueDate;
    private DatabaseHelper databaseHelper;
    private CheckBox urgentCheckBox;
    private static final String TAG = "NewDeliveryActivity";
    private double Lat;
    private double Lng;
    private String mCity;
    private String mAddress;
    private Geocoder mGeocoder;
    private String URL_FOR_LOGIN;
    String Region_string;
    String subRegion_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery);
        databaseHelper = DatabaseHelper.getInstance(this);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        Lat = 0;
        Lng = 0;
        urgentCheckBox = (CheckBox) findViewById(R.id.urgentCheckBox);
        dueDate = (DatePicker) findViewById(R.id.datePicker);
        claimantTxt = (EditText) findViewById(R.id.claimantEditText);
        nameTxt = (EditText) findViewById(R.id.nameEditText);
        phoneTxt = (EditText) findViewById(R.id.phoneEditText);
        poboxTxt = (EditText) findViewById(R.id.poboxEditText);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mGeocoder =  new Geocoder(this.getApplicationContext(), Locale.getDefault());

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Lat = place.getLatLng().latitude;
                Lng = place.getLatLng().longitude;
                URL_FOR_LOGIN = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+Lat + "," + Lng + "&key=" + getText(R.string.google_maps_key).toString() + "&language=he";
                //get the sub-region
                StringRequest strReq = new StringRequest(Request.Method.GET,
                        URL_FOR_LOGIN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VolleyResponse", "Geocoding Response: " + response.toString());

                        JSONObject jObj = null;
                        try {
                            jObj = new JSONObject(response);
                            if(jObj.get("status").equals("ZERO_RESULTS")){
                                //Check if in Golan Heights
                                if(Lat > 32.674641 && Lat < 33.341415 && Lng > 35.578993 && Lng < 35.908003) {
                                    Region_string = "מחוז הצפון";
                                    subRegion_string = "רמת הגולן";
                                }
                            }else {
                                //Get JSON Array called "results" and then get the 0th complete object as JSON
                                JSONObject results = jObj.getJSONArray("results").getJSONObject(0);
                                // Get the value of the attribute whose name is "formatted_string"
                                int index = results.getJSONArray("address_components").length();
                                JSONObject Region = results.getJSONArray("address_components").getJSONObject(index - 2);
                                JSONObject subRegion = results.getJSONArray("address_components").getJSONObject(index - 3);
                                Region_string = Region.getString("long_name");
                                subRegion_string = subRegion.getString("long_name");
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
                AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,"Geocoding");
                mAddress = place.getAddress().toString();
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        //----------------------------------------------------------------
        btnBack.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            //--------------------------------------------------------------------
            @Override
            public void onClick(View view) {
                //TODO: save to Database
                String urgent;
                if (urgentCheckBox.isChecked())
                {
                    urgent = "1";
                }
                else
                {
                    urgent = "0";
                }
                if (Lat == 0 || Lng == 0 || claimantTxt.getText().toString().equals("") || nameTxt.getText().toString().equals("") ||
                        phoneTxt.getText().toString().equals("") || poboxTxt.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            R.string.requiredFieldMissing, Toast.LENGTH_LONG).show();
                    if(claimantTxt.getText().toString().equals(""))
                        claimantTxt.setBackgroundColor(Color.rgb(220,220,220));
                    else
                        claimantTxt.setBackgroundColor(Color.rgb(255,255,255));
                    if(nameTxt.getText().toString().equals(""))
                        nameTxt.setBackgroundColor(Color.rgb(220,220,220));
                    else
                        nameTxt.setBackgroundColor(Color.rgb(255,255,255));
                    if(phoneTxt.getText().toString().equals(""))
                        phoneTxt.setBackgroundColor(Color.rgb(220,220,220));
                    else
                        phoneTxt.setBackgroundColor(Color.rgb(255,255,255));
                    if(poboxTxt.getText().toString().equals(""))
                        poboxTxt.setBackgroundColor(Color.rgb(220,220,220));
                    else
                        poboxTxt.setBackgroundColor(Color.rgb(255,255,255));
                }else {
                    /*Delivery newDelivery = new Delivery(
                            Lat,
                            Lng,
                            Region_string,
                            subRegion_string,
                            mAddress,
                            urgent,
                            claimantTxt.getText().toString(),
                            nameTxt.getText().toString(),
                            phoneTxt.getText().toString(),
                            poboxTxt.getText().toString(),
                            getDateFromDatePicker(dueDate),
                            databaseHelper.getCurrentCourier().getEmail()
                    );*/
                    //databaseHelper.addDelivery(newDelivery);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    /**
     * @param datePicker
     * @return a java.util.Date
     */
    public static java.util.Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }
    private String getCityNameByCoordinates(double lat, double lon) throws IOException {

        List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 1);
        if (addresses != null && addresses.size() > 0) {
            return addresses.get(0).getLocality();
        }
        return null;
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
