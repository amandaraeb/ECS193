package com.lids.barscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchHistoryScreen extends AppCompatActivity{

    @Override
    public void onBackPressed(){}

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        ListView lv;

        SharedPreferences sharedpreferences = getSharedPreferences("ScanHistory", 0);   //open ScanHistory.xml
        int counter = sharedpreferences.getInt("NumberOfBooks", 0);                     //get number of ISBNs. Default = 0
        String[] bookISBN = new String[counter];
        for(int i = 0; i < counter; i++)
        {
            bookISBN[i] = sharedpreferences.getString(Integer.toString(i), "empty");    //get the ISBNs
        }

        //Handle book history
        if(counter != 0) {
            lv = (ListView) findViewById(R.id.BookList);    //find the list layout
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookISBN);
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {       // checks for clicks on ISBN's in the list
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    Resend(position);           //resend function
                }

            });
        }

    }
    public void Resend(int position) {          //resend ISBN
        SharedPreferences sharedpreferences = getSharedPreferences("ScanHistory", 0);
        final String pos = Integer.toString(position);                      // get position in list on screen
        final String ISBN = sharedpreferences.getString(pos, "empty");      // get the ISBN in that position
        final String[] ISBN2 = ISBN.split(" ");                             // take only ISBN, remove spaces/time
        //pop up screen
        TextView title = new TextView(this);
        // Custom title
        title.setText("Resend ISBN?");
        title.setBackgroundColor(Color.parseColor("#474242"));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#eacda3"));
        title.setTextSize(20);
        new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(SearchHistoryScreen.this, BarMain.class);
                        intent.putExtra("resend", ISBN2[0]);                // Pass the ISBN back to Main
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    public void OnButtonClick(View v) {                     // back
        if (v.getId() == R.id.HistBckButton) {
            Intent intent = new Intent(SearchHistoryScreen.this, BarMain.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.HistClearButton) {                // clear the history
            //pop up screen
            TextView title = new TextView(this);
            // Custom title
            title.setText("Clear the Scanning History?");
            title.setBackgroundColor(Color.parseColor("#474242"));
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.parseColor("#eacda3"));
            title.setTextSize(20);
            new AlertDialog.Builder(this)
                    .setCustomTitle(title)
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            getSharedPreferences("ScanHistory", 0).edit().clear().commit(); // clear ScanHistory.xml
                            recreate();                             // refresh the activity
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();

        }

    }
}