package com.lids.barscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchHistoryScreen extends AppCompatActivity{

    private ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        Bundle extras = getIntent().getExtras();
        String[] bookArray = extras.getStringArray("books");

        //Handle book history
        if(bookArray != null) {
            lv = (ListView) findViewById(R.id.BookList);    //find the list layout
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookArray);
            lv.setAdapter(arrayAdapter);
        }
    }
    public void OnButtonClick(View v) {
        if(v.getId() == R.id.HistBckButton) {
            Intent intent = new Intent(SearchHistoryScreen.this, BarMain.class);
            startActivity(intent);
        }

    }
}