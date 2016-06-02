package com.lids.barscanner;
import android.content.Intent;

import com.journeyapps.barcodescanner.CaptureActivity;

// Empty CaptureActivity for BarMain
public class ZxingCapture extends CaptureActivity {
    //Disable back button during scanning (causes crash)
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ZxingCapture.this, BarMain.class);
        startActivity(intent);
    }
}
