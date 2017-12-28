package com.waakye.android.popularmovies9;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
 * Created by lesterlie on 12/29/17.
 */

public class SearchMoviesActivity extends AppCompatActivity {

    private static final String LOG_TAG = SearchMoviesActivity.class.getSimpleName();

    // TextView to display the error message
    private TextView mErrorMessageDisplay;

    // Loading Indicator
    private ProgressBar mLoadingIndicator;

    private TextView mSearchResultsTextView;

    private RecyclerView mSearchedMoviesList;

    private String movieTitle = "Jack Reacher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mErrorMessageDisplay = (TextView)findViewById(R.id.text_view_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar_loading_indicator);


    }

    /**
     * This method will make the View for the JSON data visible and hide the error message
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is current visible or invisible
     */
    private void showJsonDataView(){
        Log.i(LOG_TAG, "showJsonDataView() method called...");
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON is visible
        mSearchedMoviesList.setVisibility(View.VISIBLE);
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
        mSearchedMoviesList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void makeUrlMovieTitleQueryString(String movieTitle){
        Log.i(LOG_TAG, "makeUrlMovieTitleQueryString() method called...");
        new MovieTitleQueryTask().execute(movieTitle);
    }

    // https://api.themoviedb.org/3/search/movie?api_key={api_key}&query=Jack+Reacher
    public String urlQueryString(String search_terms) {

        Log.i(LOG_TAG, "urlQueryString() method called...");
        search_terms = search_terms.trim().replace(" ", "+");

        StringBuilder sb = new StringBuilder(NetworkUtils.MOVIE_DB_TITLE_SEARCH_BASE_URL);
        sb.append("?api_key=");
        sb.append(NetworkUtils.API_KEY);
        sb.append("&query=");
        sb.append(search_terms);
        String builtString = sb.toString();
        return builtString;

    }

    public class MovieTitleQueryTask extends AsyncTask<String, Void, String[]> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.i(LOG_TAG, "MovieTitleQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            String movieTitleUrl = urlQueryString(movieTitle);
            URL movieTitlerSearchUrl = NetworkUtils.createTitleSearchUrl(movieTitleUrl);

            try {
                String jsonMovieTitleResponse = NetworkUtils.getResponseFromHttpUrl(movieTitlerSearchUrl);

                String[] movieTitleJsonMovieData = MovieDbJsonUtils
                        .getMovieTitleStringsFromJson(SearchMoviesActivity.this,jsonMovieTitleResponse);
                return movieTitleJsonMovieData;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieTitleSearchResults){
            Log.i(LOG_TAG, "MovieTitleQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieTitleSearchResults != null && !movieTitleSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                for(String movieTitleString : movieTitleSearchResults){
                    // TODO: need to move this to recycler view
                    mSearchResultsTextView.append((movieTitleString) + "\n\n");
                }
            }  else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }
}
