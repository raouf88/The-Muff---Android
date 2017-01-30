package com.raouf.android.muff.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.raouf.android.muff.R;

/**
 *  Splash screen of app.
 *  Note: the content of the splash are shown as a background of this activity using {@link R.style#SplashTheme}.
 *  @author Raouf
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Called once activity is created.
     * close this screen and open {@link MainActivity}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}
