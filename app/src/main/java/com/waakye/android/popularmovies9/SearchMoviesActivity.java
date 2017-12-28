package com.waakye.android.popularmovies9;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    public String search_terms = "";

    public static String theMovieDbQueryUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchResultsTextView = (TextView) findViewById(R.id.text_view_moviedb_search_results_json);

        mErrorMessageDisplay = (TextView) findViewById(R.id.text_view_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar_loading_indicator);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get input from the EditText
                EditText searchTerms = (EditText) findViewById(R.id.edit_search_terms);
                search_terms = searchTerms.getText().toString();

                theMovieDbQueryUrl = urlQueryString(search_terms);
                makeUrlMovieTitleQueryString(theMovieDbQueryUrl);

            }
        });
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
        Log.i(LOG_TAG, "built string: " + builtString);
        return builtString;

    }

    public class MovieTitleQueryTask extends AsyncTask<String, Void, String[]> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.i(LOG_TAG, "MovieTitleQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            URL movieTitlerSearchUrl = NetworkUtils.createTitleSearchUrl(theMovieDbQueryUrl);

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
            if(movieTitleSearchResults != null && !movieTitleSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                for(String movieTitleString : movieTitleSearchResults){
                    // TODO: need to move this to recycler view
                    mSearchResultsTextView.append((movieTitleString) + "\n\n");
                }
            }  else {
                // Call showErrorMessage if the result is null in onPostExecute
            }
        }
    }
}
