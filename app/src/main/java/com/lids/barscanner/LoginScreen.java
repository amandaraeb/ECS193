package com.lids.barscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
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
    }

    public void onButtonClick(View v) {
        //Android device manager -> data/data/com.lids.barscanner/shared_prefs/Accounts.xml
        //To view xml file, navigate to the file then press the floppy disk icon at the top right
        //saying "pull a file from the device" and save to the project.
        SharedPreferences sharedpreferences = getSharedPreferences("Accounts", Context.MODE_PRIVATE);
        if(v.getId() == R.id.LoginButton) {
            //Hardcode User:Admin  Password:default
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("admin", "admin");                           //Save username
            editor.putString("admin" + "PW", "default");                  //Save password
            editor.apply();

            // Get string info from user and password fields
            EditText TFid = (EditText)findViewById(R.id.TFusername);
            EditText TFpass = (EditText)findViewById(R.id.TFpassword);
            String TFid_str = TFid.getText().toString();
            String TFpass_str= TFpass.getText().toString();

            //authenticate
            //HttpPOSTRequest(TFid_str, TFpass_str);

            String username = sharedpreferences.getString(TFid_str, "failed");      // get username from file
            String password = sharedpreferences.getString(TFid_str + "PW", "failed"); // get password from file
            if(TFid_str.equals(username) && TFpass_str.equals(password)) {          //if user + pass combo exists
                Intent intent = new Intent(LoginScreen.this, BarMain.class);
                startActivity(intent);
            }

            // If user and password don't match -> error
            else {
                Toast loginError = Toast.makeText(LoginScreen.this, "Incorrect ID or password", Toast.LENGTH_SHORT);
                loginError.show();
            }
        }

        else if (v.getId() == R.id.creationButton) {
            Intent intent = new Intent(LoginScreen.this, AccountCreation.class);
            startActivity(intent);
        }
    }

    private void HttpPOSTRequest(final String username, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://amandaraeb.koding.io:8000";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    // This code is executed if the server responds.
                    // The String 'response' contains the server's response.
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
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
            //TODO: What is getParams for? How do we change it from needing a hardcoded string?
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user", username);
                params.put("pass", password);
                return params;
            }
        };
        queue.add(postRequest);
    }
}