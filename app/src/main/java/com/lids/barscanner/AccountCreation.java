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

public class AccountCreation extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_create_screen);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Atelier Omega.ttf");
        TextView titleName = (TextView) findViewById(R.id.TFlogin_title);
        titleName.setTypeface(font);
    }
    //back button
    public void OnClick(View v) {
        if (v.getId() == R.id.CreateBckButton) {
            Intent intent = new Intent(AccountCreation.this, LoginScreen.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.CreateAccount) {

            EditText TFid = (EditText)findViewById(R.id.Username);
            EditText TFpass = (EditText)findViewById(R.id.Password1);
            EditText TFpassConf = (EditText)findViewById(R.id.Password2);
            String TFid_str = TFid.getText().toString();                    //Username String
            String TFpass_str= TFpass.getText().toString();                 //Password String
            String TFpassConf_str = TFpassConf.getText().toString();        //Password Confirm String

            // User must confirm password
            if(TFid_str.length() == 0) {
                Toast Fail = Toast.makeText(AccountCreation.this, "Please Enter a Non-Empty Username", Toast.LENGTH_SHORT);
                Fail.show();
            }
            else if(TFpass_str.length() == 0){
                Toast Fail = Toast.makeText(AccountCreation.this, "Please Enter a Non-Empty Password", Toast.LENGTH_SHORT);
                Fail.show();
            }
            else if (!TFpass_str.equals(TFpassConf_str)) {
                Toast Fail = Toast.makeText(AccountCreation.this, "Passwords Do Not Match", Toast.LENGTH_SHORT);
                Fail.show();
            }
            else
                HttpPOSTRequest(TFid_str, TFpass_str);

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
                        if (response.contains("account added successfully")){
                            Intent intent = new Intent(AccountCreation.this, LoginScreen.class);
                            intent.putExtra("newUsername", username);
                            intent.putExtra("newPassword", password);
                            startActivity(intent);
                        }
                        else if (response.contains("username already exists")){
                            Toast alreadyExists = Toast.makeText(getApplicationContext(), "That username already exists.", Toast.LENGTH_LONG);
                            alreadyExists.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    // This code is executed if there is an error.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        Toast sendError = Toast.makeText(getApplicationContext(), "User info failed to send: ".concat(error.toString()), Toast.LENGTH_LONG);
                        sendError.show();
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("type", "createAcct");
                params.put("newUser", username);
                params.put("newPass", password);
                return params;
            }
        };
        queue.add(postRequest);
    }

}