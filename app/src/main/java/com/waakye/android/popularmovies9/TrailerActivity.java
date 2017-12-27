package com.waakye.android.popularmovies9;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

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

    // TextView to display the error message
    private TextView mTrailerActivityErrorMessageDisplay;

    private TextView mTrailerResultsTextView;

    // Loading Indicator
    private ProgressBar mTrailerActivityLoadingIndicator;

    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        Intent intent = getIntent();

        movieId = intent.getStringExtra("movieId");
        Log.i(LOG_TAG, "the movie id is " + movieId);

        mTrailerResultsTextView = (TextView)findViewById(R.id.text_view_trailers);

        mTrailerActivityErrorMessageDisplay = (TextView)findViewById(R.id.trailer_activity_text_view_error_message_display);

        mTrailerActivityLoadingIndicator = (ProgressBar) findViewById(R.id.trailer_activity_progress_bar_loading_indicator);

        makeTrailerQuery(movieId);

    }

    private void makeTrailerQuery(String movieId){

        Log.i(LOG_TAG, "makeTrailerQuery() method called...");
        new TrailersQueryTask().execute(movieId);

    }

    public class TrailersQueryTask extends AsyncTask<String, Void, String[]> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mTrailerActivityLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.i(LOG_TAG, "TrailersQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            URL trailerSearchUrl = NetworkUtils.createMovieTrailerUrl(movieId);

            try {
                String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerSearchUrl);

                String[] trailerJsonMovieData = MovieDbJsonUtils
                        .getTrailerStringsFromJson(TrailerActivity.this,jsonTrailerResponse);
                return trailerJsonMovieData;


            } catch (IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] trailersSearchResults){
            Log.i(LOG_TAG, "TrailersQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mTrailerActivityLoadingIndicator.setVisibility(View.INVISIBLE);
            if(trailersSearchResults != null && !trailersSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
//                showJsonDataView();
                for(String trailerString : trailersSearchResults) {
                    mTrailerResultsTextView.append((trailerString) + "\n\n");
                }
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
//                showErrorMessage();
            }
        }
    }


}
