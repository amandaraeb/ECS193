package com.lids.barscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryScreen extends AppCompatActivity{

    private ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        //Handle book history
        lv = (ListView) findViewById(R.id.BookList);    //find the list layout
        List<String> books = new ArrayList<String>();   //initialize array "books"
        books.add("0471190470");
        books.add("8711075597");
        books.add("3295038132");
        books.add("5004991383");

        //fill in the list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, books);
        lv.setAdapter(arrayAdapter);
    }
    public void OnButtonClick(View v) {
        if(v.getId() == R.id.HistBckButton) {
            Intent intent = new Intent(SearchHistoryScreen.this, BarMain.class);
            startActivity(intent);
        }

    }
}