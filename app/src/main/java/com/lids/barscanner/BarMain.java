package com.lids.barscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//ZXing imports
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

// Android Volley imports
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

//Import Google Play Services
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BarMain extends AppCompatActivity implements View.OnClickListener {

    //BarMain widgets
    private Button scanBtn, sendBtn;
    private TextView formatTxt, contentTxt;
    private GoogleApiClient client;

    String[] bookArray = {"", "", "", "", "", "", "", "", "", ""};
    int ISBN_location = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_main);
        //Instantiate BarMain widgets
        scanBtn = (Button) findViewById(R.id.scan_button);
        sendBtn = (Button) findViewById(R.id.send_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);


        //Set listener for buttons
        scanBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    public void onClick(View v) {
        //If scan_button is clicked, begin scanning
        if (v.getId() == R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }

        //settings button Requires Administrative permission
        else if (v.getId() == R.id.settings_button) {

            //pop up screen
            final EditText password = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Admin Password Required")
                    .setMessage("Password")
                    .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String pass = password.getText().toString();
                            if (pass.equals("default")) {
                                Intent intent = new Intent(BarMain.this, ConfigurationScreen.class);
                                startActivity(intent);
                            } else {
                                Toast loginError = Toast.makeText(BarMain.this, "Incorrect password", Toast.LENGTH_SHORT);
                                loginError.show();
                            }
                        }
                    })
                    //temporary skip function
                    .setNeutralButton("Skip", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent(BarMain.this, ConfigurationScreen.class);
                    startActivity(intent);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                            .show();
        }

        //Search History
        else if (v.getId() == R.id.history_button) {
            Intent intent = new Intent(BarMain.this, SearchHistoryScreen.class);
            intent.putExtra("books", bookArray);
            startActivity(intent);
        }

        //If send_button is clicked, send what's in the scan_content TextView
        else if(v.getId()==R.id.send_button){
            String isbn = contentTxt.getText().toString().replace("ISBN: ", "");
            // If the TextView is empty, warn the user and do nothing
            if(isbn.equals("")){
                Toast noTextWarning = Toast.makeText(getApplicationContext(), "Nothing to send!", Toast.LENGTH_SHORT);
                noTextWarning.show();
            }
            // Else there is something to send, so send it.
            else {
                //Save Into History
                String date = DateFormat.getDateTimeInstance().format(new Date());
                String isbn2 = isbn + "               " + date;
                bookArray[ISBN_location] = isbn2;
                ISBN_location++;

                HttpPOSTRequest(isbn);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //If there is a result, get the values and display them
        if (scanningResult != null) {
            //Get values
            String scanFormat = scanningResult.getFormatName();
            String scanContent = scanningResult.getContents();

            //Display values
            formatTxt.setText("FORMAT: ".concat(scanFormat));
            contentTxt.setText("ISBN: ".concat(scanContent));
        }
        //Else, display the warning Toast
        else {
            Toast noDataWarning = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            noDataWarning.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "BarMain Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.lids.barscanner/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "BarMain Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.lids.barscanner/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Custom StringRequest override.
    private void HttpPOSTRequest(String content) {
        final String sendISBN = content;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ldsecs193.koding.io:8000";//"http://amandaraeb.koding.io:8000";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    // This code is executed if the server responds.
                    // The String 'response' contains the server's response.
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if (!response.contains("failure")) {
                            Toast sendSuccess = Toast.makeText(getApplicationContext(), "ISBN successfully sent!", Toast.LENGTH_SHORT);
                            sendSuccess.show();
                        }
                        else{
                            Toast sendFailure = Toast.makeText(getApplicationContext(), "Server unable to send ISBN!", Toast.LENGTH_LONG);
                            sendFailure.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    // This code is executed if there is an error.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        Toast sendError = Toast.makeText(getApplicationContext(), "BarScanner failed to send: ".concat(error.toString()), Toast.LENGTH_LONG);
                        sendError.show();
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("isbn", sendISBN);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
