package com.waakye.android.popularmovies9;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView mSearchResultsTextView;

    private String moviePopularityType;

    private URL movieSearchUrl;

    private String movieId = "343611";

    private String movieTitle = "Jack Reacher";

    private String edit_text_search_terms = "";

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate() method called...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchResultsTextView = (TextView) findViewById(R.id.text_view_moviedb_search_results_json);

        mErrorMessageDisplay = (TextView)findViewById(R.id.text_view_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar_loading_indicator);
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

    private void makeMovieDbPopularityQuery(String popularityType){

        Log.i(LOG_TAG, "makeMovieDbPopularityQuery() method called...");
        new MovieDbQueryTask().execute(popularityType);
    }

    private void makeUserReviewsQuery(String movieId){

        Log.i(LOG_TAG, "makeUserReviewsQuery() method called...");
        new UserReviewsQueryTask().execute(movieId);

    }

    private void makeTrailerQuery(String movieId){

        Log.i(LOG_TAG, "makeTrailerQuery() method called...");
        new TrailersQueryTask().execute(movieId);

    }

    private void makeUrlMovieTitleQueryString(String movieTitle){
        Log.i(LOG_TAG, "makeUrlMovieTitleQueryString() method called...");
        new MovieTitleQueryTask().execute(movieTitle);
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
        mSearchResultsTextView.setVisibility(View.VISIBLE);
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
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class MovieDbQueryTask extends AsyncTask<String, Void, String[]> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.i(LOG_TAG, "MovieDbQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            URL movieSearchUrl = NetworkUtils.buildByPopularityTypeUrl(moviePopularityType);
            if(moviePopularityType == "popularity"){
                movieSearchUrl = NetworkUtils.createMostPopularUrl();
                Log.i(LOG_TAG, "movieSearchUrl is " + movieSearchUrl);
            } else if(moviePopularityType == "top_rated"){
                movieSearchUrl = NetworkUtils.createHighlyRatedUrl();
                Log.i(LOG_TAG, "movieSearchUrl is " + movieSearchUrl);
            }


            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieSearchUrl);
                String[] simpleJsonMovieData = MovieDbJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);
                return simpleJsonMovieData;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieDbSearchResults){
            Log.i(LOG_TAG, "MovieDbQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieDbSearchResults != null && !movieDbSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                for(String movieString : movieDbSearchResults){
                    mSearchResultsTextView.append((movieString) + "\n\n");
                }
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public class TrailersQueryTask extends AsyncTask<String, Void, String[]>{

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
                        .getTrailerStringsFromJson(MainActivity.this, jsonTrailerResponse);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(trailersSearchResults != null && !trailersSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                for(String trailerString : trailersSearchResults) {
                    mSearchResultsTextView.append((trailerString) + "\n\n");
                    }
                } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public class MovieTitleQueryTask extends AsyncTask<String, Void, String[]>{

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
                        .getMovieTitleStringsFromJson(MainActivity.this,jsonMovieTitleResponse);
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
                    mSearchResultsTextView.append((movieTitleString) + "\n\n");
                }
            }  else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public class UserReviewsQueryTask extends AsyncTask<String, Void, String[]>{

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.i(LOG_TAG, "UserReviewsQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            URL reviewsSearchUrl = NetworkUtils.createUserReviewsUrl(movieId);

            try {
                String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsSearchUrl);

                String[] reviewsJsonMovieData = MovieDbJsonUtils
                        .getUserReviewStringsFromJson(MainActivity.this,jsonReviewsResponse);
                return reviewsJsonMovieData;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] userReviewsSearchResults){
            Log.i(LOG_TAG, "UserReviewsQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(userReviewsSearchResults != null && !userReviewsSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                for(String userReviewString : userReviewsSearchResults) {
                    mSearchResultsTextView.append((userReviewString) + "\n\n");
                }
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemThatWasClicked = item.getItemId();
        if(itemThatWasClicked == R.id.action_popular_movies) {
            Log.i(LOG_TAG, "onOptionsItemSelected() method -- most popular movies called...");
            mSearchResultsTextView.setText("");
            moviePopularityType = "popularity"; // Most popular movies
//            makeMovieDbPopularityQuery(moviePopularityType);
//            makeTrailerQuery(movieId);
//            makeUserReviewsQuery(movieId);
            makeUrlMovieTitleQueryString(movieTitle);
            return true;
        }

        if(itemThatWasClicked == R.id.action_highly_rated_movies){
            Log.i(LOG_TAG, "onOptionsItemSelected() method -- highly rated movies called...");
            mSearchResultsTextView.setText("");
            moviePopularityType = "top_rated"; // Most highly rated movies
//            makeMovieDbPopularityQuery(moviePopularityType);
//            makeTrailerQuery(movieId);
//            makeUserReviewsQuery(movieId);
            makeUrlMovieTitleQueryString(movieTitle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
