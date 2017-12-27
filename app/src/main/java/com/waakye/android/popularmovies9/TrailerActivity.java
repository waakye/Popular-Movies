package com.waakye.android.popularmovies9;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lesterlie on 12/27/17.
 */

public class TrailerActivity extends AppCompatActivity {

    public static String LOG_TAG = TrailerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
    }
}
