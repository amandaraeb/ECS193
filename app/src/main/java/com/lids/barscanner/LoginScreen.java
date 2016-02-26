package com.lids.barscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.lids.barscanner.BarMain;

public class LoginScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.loginButton) {

            // Get string info from user and password fields
            EditText TFid = (EditText)findViewById(R.id.TFusername);
            EditText TFpass = (EditText)findViewById(R.id.TFpassword);
            String TFid_str = TFid.getText().toString();
            String TFpass_str= TFpass.getText().toString();

            // Hardcoded user and password for time being
            if(TFid_str.equals("admin") && TFpass_str.equals("default")) {
                Intent intent = new Intent(LoginScreen.this, BarMain.class);
                startActivity(intent);
            }

            // If user and password don't match -> error
            else {
                Toast loginError = Toast.makeText(LoginScreen.this, "Incorrect ID or password", Toast.LENGTH_SHORT);
                loginError.show();
            }
        }
    }
}