package com.waakye.android.popularmovies9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by lesterlie on 12/27/17.
 */

/**
 * Things to do with the movie id:
 * 1) Get all the trailers related to this movieId
 * 2) Show the trailers as a list in a RecyclerView or ListView
 * 3) When a user clicks an item in the recyclerView or list, then show the trailer using an intent
 */

public class TrailerActivity extends AppCompatActivity {

    public static String LOG_TAG = TrailerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        Intent intent = getIntent();

        String movieId = intent.getStringExtra("movieId");
        Log.i(LOG_TAG, "the movie id is " + movieId);

    }
}
