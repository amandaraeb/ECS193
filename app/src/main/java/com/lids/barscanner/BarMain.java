package com.lids.barscanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import java.util.List;
import java.util.Map;



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
            startActivity(intent);
        }

        //If send_button is clicked, send what's in the scan_content TextView
        else if(v.getId()==R.id.send_button){
            // Temporary parser until server can check flags.


            String isbn = contentTxt.getText().toString().replace("ISBN: ", "");
            //isbn = "123456789"; //dummy isbn for testing on virtual phone
            // If the TextView is empty, warn the user and do nothing
            if(isbn.equals("")){
                Toast noTextWarning = Toast.makeText(getApplicationContext(), "Nothing to send!", Toast.LENGTH_SHORT);
                noTextWarning.show();
            }
            // Else there is something to send, so send it.
            else {
                //function call will have to be moved somewhere else once WorldCat parsing is implemented
                SelectResult(isbn);     //Function for selecting from returned list of WorldCat ISBN's

                //HttpPOSTRequest(isbn);
            }
        }
    }

    public void SelectResult(String content) {
        // JsonArrayRequest
        //create a List of Maps holding <title, info> pairs
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        RequestQueue queue = Volley.newRequestQueue(this);
        final String URL = "http://ldsecs193.koding.io:8000";
        StringRequest req = new StringRequest(Method.GET, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String json) {
                try {
                    // Get JSON Array
                    JSONArray oclcEntries = new JSONArray(json);

                    // Loop through all entries
                    for(int i = 0; i < oclcEntries.length(); i++) {
                        JSONObject entry = oclcEntries.getJSONObject(i);
                        String oclc = entry.getString("oclc");
                        String title = entry.getString("title");
                        String author = entry.getString("author");
                        String publisher = entry.getString("publisher");

                        //Create some temp maps holding OCLC book data
                        final Map<String, String> datum = new HashMap<String, String>(2);

                        datum.put("title", title);
                        datum.put("author", author);
                        datum.put("publisher", publisher);
                        datum.put("oclc", oclc);

                        // add datum to data list
                        data.add(datum);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        queue.add(req);

        //load history file
        final SharedPreferences sharedpreferences = getSharedPreferences(ScanHistory, Context.MODE_PRIVATE);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alert.create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.worldcat_isbn_list, null);  //specify xml file for layout
        alertDialog.setView(convertView);
        alertDialog.setTitle("           Select OCLC ISBN");
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);      //grab list from xml
    /*
        //create a List of Maps holding <title, info> pairs
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        //Create some temp maps holding OCLC book data
        final Map<String, String> datum = new HashMap<String, String>(2);
        datum.put("title", "162596101");
        datum.put("info", "Curves and Surfaces, Gerald E Farin\nSan Francisco CA:Morgan Kaufmann\nLondon:Academic Press,©2002");
        data.add(datum);
        Map<String, String> datum2 = new HashMap<String, String>(2);
        datum2.put("title", "248043606");
        datum2.put("info", "Curves and Surfaces, Gerald Farin\nSan Francisco, Calif:Morgan Kaufmann Publ,2002");
        data.add(datum2);
        Map<String, String> datum3 = new HashMap<String, String>(2);
        datum3.put("title", "254200301");
        datum3.put("info","Curves and Surfaces, Gerald Farin\nSan Francisco, Calif:Morgan Kaufmann Publ,2006");
        data.add(datum3);
        Map<String, String> datum4 = new HashMap<String, String>(2);
        datum4.put("title", "300381010");
        datum4.put("info","Curves and Surfaces, Gerald E Farin\nSan Francisco Calif:M Kaufmann,©2002");
        data.add(datum4);
*/
        //add Maps to the display list
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "info"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        lv.setAdapter(adapter);
        alertDialog.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {       // checks for clicks on ISBN's in the list
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                String key;                                 // key for ScanHistory ISBN's
                Map<String, String> clicked = data.get(position);   //get the <title, info> pair for the position clicked
                String result = clicked.get("title");               //store the <title> portion holding OCLC number
                int BookCount = sharedpreferences.getInt("NumberOfBooks", 0);
                //Save Into History
                String date = DateFormat.getDateTimeInstance().format(new Date());    // get timestamp
                String isbn = result + "               " + date;                    // OCLC + timestamp
                key = Integer.toString(BookCount);
                BookCount++;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(key, isbn);               // store ("key", "ISBN")
                editor.putInt(NumberOfBooks, BookCount);   // store # of ISBNs
                editor.apply();
                alertDialog.cancel();                      // remove the popup
                HttpPOSTRequest(result);
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
        //display loading spinner
        final ProgressBar spinner;
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ldsecs193.koding.io:8000";//"http://amandaraeb.koding.io:8000";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    // This code is executed if the server responds.
                    // The String 'response' contains the server's response.
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        if (response.contains("added")) {
                            Toast sendSuccess = Toast.makeText(getApplicationContext(), "ISBN successfully sent!", Toast.LENGTH_SHORT);

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
                        else{
                            spinner.setVisibility(View.INVISIBLE);
                            Toast sendFailure = Toast.makeText(getApplicationContext(), "Server unable to send ISBN!", Toast.LENGTH_LONG);
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
