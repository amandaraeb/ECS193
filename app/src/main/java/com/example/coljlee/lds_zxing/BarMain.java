package com.example.coljlee.lds_zxing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Import communication protocol classes.
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//Import ZXing classes
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarMain extends AppCompatActivity implements OnClickListener {

    //BarMain widgets
    private Button scanBtn;
    private TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_main);

        //Instantiate BarMain widgets
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(this);                       //Listen for scanBtn clicks
    }

    @Override
    public void onClick(View v) {
        //If scan_button is clicked, begin scanning
        if(v.getId()==R.id.scan_button){
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
        if (scanningResult!=null) {
            //Get values
            String scanFormat = scanningResult.getFormatName();
            String scanContent = scanningResult.getContents();

            //Display values
            //Use concat() to avoid Android warnings
            formatTxt.setText("FORMAT: ".concat(scanFormat));
            contentTxt.setText("ISBN: ".concat(scanContent));
        }
        else {
            Toast noDataWarning = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            noDataWarning.show();
        }
    }

    // Communication Protocol.

}
