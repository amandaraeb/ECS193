package com.lids.barscanner;
import android.content.Intent;

import com.journeyapps.barcodescanner.CaptureActivity;

// Empty CaptureActivity for BarMain
public class ZxingCapture extends CaptureActivity {
    //Override back button. (Causes crash otherwise)
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ZxingCapture.this, BarMain.class);
        startActivity(intent);
    }
}
