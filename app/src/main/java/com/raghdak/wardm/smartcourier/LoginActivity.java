package com.raghdak.wardm.smartcourier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.raghdak.wardm.smartcourier.SQL.DatabaseHelper;
import com.raghdak.wardm.smartcourier.model.Courier;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.model.User;
import com.raghdak.wardm.smartcourier.protocol.response.LoginResponse;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ProgressDialog progressDialog;
    private EditText loginInputEmail, loginInputPassword;
    private Button btnlogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInputEmail = (EditText) findViewById(R.id.login_input_email);
        loginInputPassword = (EditText) findViewById(R.id.login_input_password);
        btnlogin = (Button) findViewById(R.id.btn_login);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        //--------------------------------------------------------------
        databaseHelper = DatabaseHelper.getInstance(this);
        //databaseHelper.addCourier(new Courier("test","1234",
        //       "Ward","Mohanna","Peqiin","+972546697971"));
        //--------------------------------------------------------------
        //--------------------------------------------------------------
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   if(isEmailValid(loginInputEmail.getText().toString())) {
                loginUser(loginInputEmail.getText().toString(),
                        loginInputPassword.getText().toString());
            /*    }
                else{
                    Log.d("Login","input isnt valid Email format");
                    Toast.makeText(getApplicationContext(),
                            "Invalid Email", Toast.LENGTH_LONG).show();
                }*/
            }
        });

    }

    private void loginUser( final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(this); // this = context
        final String url = "http://" + User.ip + "/app/courier/authenticate";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", email);
        params.put("password", password);
        // prepare the Request
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        Gson gson=new Gson();
                        User user = gson.fromJson(response.toString(), User.class);
                        User.currentUser = user;
                        if(user == null || user.getToken() == null){
                            Toast.makeText(getApplicationContext(),
                                    "הכנסת פרטים שגויים", Toast.LENGTH_LONG).show();
                            hideDialog();
                            return;
                        }
                        Intent intent = new Intent(
                                LoginActivity.this,
                                MainActivity.class);
                        intent.putExtra("user", response.toString());
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getApplicationContext(),
                                error.toString(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }
        );
// add it to the RequestQueue  
        queue.add(putRequest);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
