package com.waakye.android.popularmovies9;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waakye.android.popularmovies9.data.MovieListingContract;

/**
 * Created by lesterlie on 1/9/18.
 */

// TODO: Figure out how to show the query results of the user's favorite movies

public class FavoritesActivity extends AppCompatActivity {

    public static String LOG_TAG = FavoritesActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;


    // An instance variable storing a Cursor called mData
    private Cursor mData;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.i(LOG_TAG, "onCreate() method called...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mErrorMessageDisplay = (TextView)findViewById(R.id.text_view_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar_loading_indicator);


        // Execute AsyncTask onCreate()
        new FetchUserFavoriteMoviesTask().execute();

    }

    /**
     * This method will make the View for the JSON data visible and hide the error message
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is current visible or invisible
     */
    private void showQueryDataView(){
        Log.i(LOG_TAG, "showQueryDataView() method called...");
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON View.
     *
     * Since it is okay to redundantly set  the visiblity of a View, we don't need to check whether
     * each view is currently visible or invisible
     */
    private void showErrorMessage(){
        Log.i(LOG_TAG, "showErrorMessage() method called...");
        // First, hide the currently visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // A method to retrieve the user's favorite movies stored in the SQLite database
    public class FetchUserFavoriteMoviesTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {

            Log.i(LOG_TAG, "doInBackground() method called...");

            // Make the query to get the data

            // Get the content resolver
            ContentResolver resolver = getContentResolver();

            // Call the query method on the resolver with the correct URI from the contract class
            Cursor cursor = resolver.query(MovieListingContract.MovieListingEntry.CONTENT_URI,
                    null, null, null, null);

            Log.i(LOG_TAG, "query called()...");
            return cursor;
        }

        // Store the Cursor object in mData
        @Override
        protected void onPostExecute(Cursor cursor){
            super.onPostExecute(cursor);

            // Set the data for MainActivity
            mData = cursor;
        }
    }


}