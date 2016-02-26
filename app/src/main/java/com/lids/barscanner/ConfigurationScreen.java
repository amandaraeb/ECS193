package com.lids.barscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lids.barscanner.BarMain;


public class ConfigurationScreen extends AppCompatActivity {

    //private Button configBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_screen);
        //configBtn = (Button)findViewById(R.id.configButton);
        //configBtn.setOnClickListener((View.OnClickListener) ConfigurationScreen.this);
    }

    public void OnButtonClick(View v) {
        if(v.getId() == R.id.configButton) {
            // TODO: implement configurations
            Intent intent = new Intent(ConfigurationScreen.this, BarMain.class);
            startActivity(intent);
        }

        else if(v.getId() == R.id.defaultButton) {
            // Not sure.. will there be a default setting?
        }
        else if(v.getId() == R.id.ConfigBackBtn) {
            Intent intent = new Intent(ConfigurationScreen.this, BarMain.class);
            startActivity(intent);
        }
    }
}