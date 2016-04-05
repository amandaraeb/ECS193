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

public class AccountCreation extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_create_screen);
    }
    //back button
    public void OnClick(View v) {
        //Android device manager -> data/data/com.lids.barscanner/shared_prefs/Accounts.xml
        //To view xml file, navigate to the file then press the floppy disk icon at the top right
        //saying "pull a file from the device" and save to the project.
        SharedPreferences sharedpreferences = getSharedPreferences("Accounts", Context.MODE_PRIVATE);
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


            //if password == password confirmation
            if(TFpass_str.equals(TFpassConf_str)) {
                String checker = sharedpreferences.getString(TFid_str, "available");
                if(checker.equals("available")){                                    //if username not taken
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(TFid_str, TFid_str);                           //Save username
                    editor.putString(TFid_str + "PW", TFpass_str);                  //Save password
                    editor.apply();

                    Intent intent = new Intent(AccountCreation.this, BarMain.class);
                    startActivity(intent);
                }
                else {
                    Toast Fail = Toast.makeText(AccountCreation.this, "Username Already Exists", Toast.LENGTH_SHORT);
                    Fail.show();
                }
            }

            else
            {
                Toast Fail = Toast.makeText(AccountCreation.this, "Passwords Do Not Match", Toast.LENGTH_SHORT);
                Fail.show();
            }

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