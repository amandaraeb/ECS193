package com.lids.barscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Atelier Omega.ttf");
        TextView titleName = (TextView) findViewById(R.id.TFlogin_title);
        titleName.setTypeface(font);

        Bundle extras = getIntent().getExtras();
        EditText TFid = (EditText)findViewById(R.id.TFusername);
        EditText TFpass = (EditText)findViewById(R.id.TFpassword);

        //Check for username/password from AccountCreation
        if(extras != null) {
            TFid.setText(extras.getString("newUsername"));
            TFpass.setText(extras.getString("newPassword"));
        }


    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.LoginButton) {
            // Get string info from user and password fields
            EditText TFid = (EditText)findViewById(R.id.TFusername);
            EditText TFpass = (EditText)findViewById(R.id.TFpassword);
            String TFid_str = TFid.getText().toString();
            String TFpass_str= TFpass.getText().toString();
            System.out.println(TFid_str + "   " + TFpass_str);

            //Authenticate (Actions in onResponse in HttpPOSTRequest)
            HttpPOSTRequest(TFid_str, TFpass_str);
        }

        else if (v.getId() == R.id.creationButton) {
            Intent intent = new Intent(LoginScreen.this, AccountCreation.class);
            startActivity(intent);
        }
    }

    private void HttpPOSTRequest(final String username, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://linkeddata.sxeau2dwtj.us-east-1.elasticbeanstalk.com/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    // This code is executed if the server responds.
                    // The String 'response' contains the server's response.
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if (response.contains("authentication successful")){
                            Intent intent = new Intent(LoginScreen.this, BarMain.class);
                            startActivity(intent);
                        }
                        else{
                            Toast incorrectPass = Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_LONG);
                            incorrectPass.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    // This code is executed if there is an error.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        Toast sendError = Toast.makeText(getApplicationContext(), "User info failed to send: ".concat(error.toString().concat("\nInternet Connection Required")), Toast.LENGTH_LONG);
                        sendError.show();
                    }
                }
        ){

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("type", "login");
                params.put("user", username);
                params.put("pass", password);
                return params;
            }
        };
        queue.add(postRequest);
    }
}