package com.example.coljlee.lds_zxing;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Import communication protocol classes.
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

//Import ZXing classes
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarMain extends AppCompatActivity implements OnClickListener {

    //BarMain widgets
    private Button scanBtn;
    private TextView formatTxt, contentTxt;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_main);

        //Instantiate BarMain widgets
        scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(new OnClickListener()) {
            @Override
            public void onClick (View v){
                // Making String object request.
                HttpPOSTRequest(contentTxt);
            }
        } ;                       //Listen for scanBtn clicks'
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // Custom StringRequest override.
    private void HttpPOSTRequest(contentTxt) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://localhost:80/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    // This code is executed if the server responds.
                    // The String 'response' contains the server's response.
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                };
            new Response.ErrorListener() {
                // This code is executed if there is an error.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR", "error => " + error.toString());
                }
            }
                ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("ISBN", contentTxt );

                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onClick(View v) {
        //If scan_button is clicked, begin scanning
        if (v.getId() == R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan(); //TODO: Scan only for book formats using args
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //If there is a result, get the values and display them
        //Else, display warning Toast
        if (scanningResult != null) {
            //Get values
            String scanFormat = scanningResult.getFormatName();
            String scanContent = scanningResult.getContents();

            //Display values
            //Use concat() to avoid Android warnings
            formatTxt.setText("FORMAT: ".concat(scanFormat));
            contentTxt.setText("ISBN: ".concat(scanContent));
        } else {
            Toast noDataWarning = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            noDataWarning.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BarMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.coljlee.lds_zxing/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BarMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.coljlee.lds_zxing/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Communication Protocol.

}