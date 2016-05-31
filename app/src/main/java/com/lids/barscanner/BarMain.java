package com.lids.barscanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//ZXing imports
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BarMain extends AppCompatActivity implements View.OnClickListener{

    //BarMain widgets
    private Button scanBtn, sendBtn;
    private TextView formatTxt, contentTxt;
    private GoogleApiClient client;

    //Android device manager -> data/data/com.lids.barscanner/shared_prefs/ScanHistory.xml
    //To view xml file, navigate to the file then press the floppy disk icon at the top right
    //saying "pull a file from the device" and save to the project.
    public static final String ScanHistory = "ScanHistory";
    public static final String NumberOfBooks = "NumberOfBooks";

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

        //check for resend from scan history
        Bundle extras = getIntent().getExtras();
        if(extras == null) {                //no resend
            String reSend= null;
        }
        else {
            String reSend = extras.getString("resend");
            HttpPOSTRequest(reSend);        // resend ISBN
        }
    }

    @Override
    public void onClick(View v)  {

        //If scan_button is clicked, begin scanning
        if (v.getId() == R.id.scan_button) {

            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.setCaptureActivity(ZxingCapture.class);
            scanIntegrator.setOrientationLocked(false);
            scanIntegrator.initiateScan();
        }

        else if (v.getId() == R.id.logout_button) {
            Intent intent = new Intent(BarMain.this, LoginScreen.class);
            startActivity(intent);
        }

        //settings button Requires Administrative permission
        else if (v.getId() == R.id.settings_button) {

            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.admin_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Enter",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    String pass= userInput.getText().toString();
                                    if (pass.equals("default")) {
                                        Intent intent = new Intent(BarMain.this, ConfigurationScreen.class);
                                        startActivity(intent);
                                    } else {
                                        Toast loginError = Toast.makeText(BarMain.this, "Incorrect Password", Toast.LENGTH_SHORT);
                                        loginError.show();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }


        //Search History
        else if (v.getId() == R.id.history_button) {
            Intent intent = new Intent(BarMain.this, SearchHistoryScreen.class);
            startActivity(intent);
        }

        //If send_button is clicked, send what's in the scan_content TextView
        else if(v.getId()==R.id.send_button){
            // Temporary parser until server can check flags.


            String isbn = contentTxt.getText().toString().replace("ISBN: ", "");
            // If the TextView is empty, warn the user and do nothing
            if(isbn.equals("")){
                Toast noTextWarning = Toast.makeText(getApplicationContext(), "Nothing to send!", Toast.LENGTH_SHORT);
                noTextWarning.show();
            }
            // Else there is something to send, so send it.
            else {
                GetOCLC(isbn);     //Function for getting the list of WorldCat OCLC's
            }
        }
    }

    public void GetOCLC(String content) {
        final String sendISBN = content;
        //display loading spinner
        final ProgressBar spinner;
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        // JsonArrayRequest
        //create a List of Maps holding <title, info> pairs
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> params;

        RequestQueue queue = Volley.newRequestQueue(this);
        final String URL = "http://linkeddata.sxeau2dwtj.us-east-1.elasticbeanstalk.com/";
        StringRequest req = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String json) {
                try{
                    Log.d("Response", json);
                    // Get JSON Array
                    JSONArray oclcEntries = new JSONArray(json);

                    // Loop through all entries
                    for(int i = 0; i < oclcEntries.length(); i++) {
                        JSONObject entry = oclcEntries.getJSONObject(i);
                        String oclc = entry.getString("oclc");
                        if(oclc.equals("unknown"))
                            oclc = "Unknown OCLC";
                        String title = entry.getString("title");
                        if(title.equals("unknown"))
                            title = "Unknown Title";
                        String author = entry.getString("author");
                        if(author.equals("unknown"))
                            author = "Unknown Author";
                        String publisher = entry.getString("publisher");
                        if(publisher.equals("unknown"))
                            publisher = "Unknown Publisher";
                        String info = title.concat(", ").concat(author).concat(", ").concat(publisher);
                        //Create some temp maps holding OCLC book data
                        final Map<String, String> datum = new HashMap<String, String>(2);

                        datum.put("info", info);
                        Log.d("Response", info);

                        datum.put("oclc", oclc);
                        Log.d("Response", oclc);

                        // add datum to data list
                        data.add(datum);

                    }
                    if(data.size() != 0 )   //if no oclc's returned
                        SelectOCLC(data);   //function to select an oclc from the list generated
                    else {
                        spinner.setVisibility(View.INVISIBLE);
                        Toast sendError = Toast.makeText(getApplicationContext(), "No Records Found For This Book", Toast.LENGTH_LONG);
                        sendError.show();
                    }
                }
                catch (JSONException e){    //error occurred with finding oclc's
                    spinner.setVisibility(View.INVISIBLE);
                    Toast sendError = Toast.makeText(getApplicationContext(), "Unable to Find Valid OCLC's\nWorldCat Book Format Not Supported", Toast.LENGTH_LONG);
                    sendError.show();
                    //throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.INVISIBLE);
                Log.d("ERROR", "error => " + error.toString());
                Toast sendError = Toast.makeText(getApplicationContext(), "OCLC failed to send: ".concat(error.toString()), Toast.LENGTH_LONG);
                sendError.show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type", "oclc");
                params.put("isbn", sendISBN);
                return params;
            }
        };

        queue.add(req);
    }
    public void SelectOCLC(final List<Map<String, String>> data){
        final ProgressBar spinner;
        spinner=(ProgressBar)findViewById(R.id.progressBar);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alert.create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.worldcat_isbn_list, null);  //specify xml file for layout
        alertDialog.setView(convertView);
        TextView title = new TextView(this);
        // Custom title
        title.setText("Select OCLC Number");
        title.setBackgroundColor(Color.parseColor("#474242"));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#eacda3"));
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);      //grab list from xml

        //add Maps to the display list
        spinner.setVisibility(View.INVISIBLE);
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"oclc", "info"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        lv.setAdapter(adapter);
        alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {       // checks for clicks on ISBN's in the list
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Map<String, String> clicked = data.get(position);   //get the <oclc, info> pair for the position clicked
                String oclc = clicked.get("oclc");               //store the <oclc> portion holding OCLC number
                alertDialog.cancel();                      // remove the popup
                HttpPOSTRequest(oclc);
            }
        });

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

            //automatic sending of ISBN after a scan
            String isbn = contentTxt.getText().toString().replace("ISBN: ", "");
            // If the TextView is empty, warn the user and do nothing
            if(isbn.equals("")){
                Toast noTextWarning = Toast.makeText(getApplicationContext(), "Nothing to send!", Toast.LENGTH_SHORT);
                noTextWarning.show();
            }
            // Else there is something to send, so send it.
            else {
                GetOCLC(isbn);     //Function for getting the list of WorldCat OCLC's
            }
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
        final SharedPreferences sharedpreferences = getSharedPreferences(ScanHistory, Context.MODE_PRIVATE);
        final String sendISBN = content;
        //display loading spinner
        final ProgressBar spinner;
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://linkeddata.sxeau2dwtj.us-east-1.elasticbeanstalk.com/";//http://ldsecs193.koding.io:8000
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    // This code is executed if the server responds.
                    // The String 'response' contains the server's response.
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if (response.contains("added")) {
                            Toast sendSuccess = Toast.makeText(getApplicationContext(), "ISBN successfully sent!", Toast.LENGTH_SHORT);

                            //store into history
                            String key;
                            int BookCount = sharedpreferences.getInt("NumberOfBooks", 0);
                            //Save Into History
                            String date = DateFormat.getDateTimeInstance().format(new Date());      // get timestamp
                            String result = sendISBN;
                            //formatting attempt to make all entries as equal in length as possible
                            if(result.length() < 8)
                                result = result + "                    ";
                            else if(result.length() == 8)
                                result = result + "                 ";
                            else if(result.length() == 9)
                                result = result + "               ";
                            else if(result.length() == 10)
                                result = result + "             ";
                            else if(result.length() > 10)
                                result = result + "           ";
                            result = result + date;             // OCLC + timestamp

                            key = Integer.toString(BookCount);
                            BookCount++;
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(key, result);               // store ("key", "ISBN")
                            editor.putInt(NumberOfBooks, BookCount);   // store # of ISBNs
                            editor.apply();

                            Animation fadeIn = new AlphaAnimation(0, 1);
                            final Animation fadeOut = new AlphaAnimation(1, 0);
                            fadeIn.setInterpolator(new DecelerateInterpolator());
                            fadeOut.setInterpolator(new DecelerateInterpolator());
                            fadeIn.setDuration(2000);
                            fadeOut.setDuration(3000);
                            final ImageView imageView = (ImageView) findViewById(R.id.checkmark);
                            imageView.setImageResource(R.drawable.checkmark);
                            spinner.setVisibility(View.INVISIBLE);
                            sendSuccess.show();
                            imageView.startAnimation((fadeIn));
                            // Execute fade out after 2 seconds have passed
                            Handler timeoutHandler = new Handler();

                            timeoutHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.startAnimation((fadeOut));
                                }
                            }, 2000);

                        }
                        else if(response.contains("isbn old")){
                            spinner.setVisibility(View.INVISIBLE);
                            Toast sendFailure = Toast.makeText(getApplicationContext(), "ISBN Already In Database", Toast.LENGTH_LONG);
                            sendFailure.show();
                        }
                        else{
                            spinner.setVisibility(View.INVISIBLE);
                            Toast sendFailure = Toast.makeText(getApplicationContext(), "Server Unable to Send ISBN", Toast.LENGTH_LONG);
                            sendFailure.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    // This code is executed if there is an error.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinner.setVisibility(View.INVISIBLE);
                        Log.d("ERROR", "error => " + error.toString());
                        Toast sendError = Toast.makeText(getApplicationContext(), "BarScanner failed to send: ".concat(error.toString()), Toast.LENGTH_LONG);
                        sendError.show();
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("type", "book");
                params.put("isbn", sendISBN);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
