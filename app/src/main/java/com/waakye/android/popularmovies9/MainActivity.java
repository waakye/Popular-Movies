package com.waakye.android.popularmovies9;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView mSearchResultsTextView;

    private String moviePopularityType;

    // MovieId of "Jack Reacher" movie
    private static int JACK_REACHER_MOVIE_ID = 343611;

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
        URL movieDbDiscoverUrl = NetworkUtils.buildByPopularityTypeUrl(popularityType);
        new MovieDbQueryTask().execute(movieDbDiscoverUrl);
    }

    private void makeUserReviewsQuery(int movieId){

        Log.i(LOG_TAG, "makeUserReviewsQuery() method called...");
        URL movieUserReviewsUrl = NetworkUtils.createUserReviewsUrl(movieId);
        new UserReviewsQueryTask().execute(movieUserReviewsUrl);

    }

    private void makeTrailerQuery(int movieId){

        Log.i(LOG_TAG, "makeTrailerQuery() method called...");
        URL movieTrailerUrl = NetworkUtils.createMovieTrailerUrl(movieId);
        new TrailersQueryTask().execute(movieTrailerUrl);

    }

    private void makeUrlMovieTitleQueryString(String string){
        Log.i(LOG_TAG, "makeUrlMovieTitleQueryString() method called...");
        URL movieTitleSearchQueryUrl = NetworkUtils.createTitleSearchUrl(string);
        new MovieTitleQueryTask().execute(movieTitleSearchQueryUrl);
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

    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            Log.i(LOG_TAG, "MovieDbQueryTask doInBackground() method called...");
            URL searchUrl = params[0];
            String movieDbSearchResults = null;
            try {
                movieDbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return movieDbSearchResults;
        }

        @Override
        protected void onPostExecute(String movieDbSearchResults){
            Log.i(LOG_TAG, "MovieDbQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieDbSearchResults != null && !movieDbSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(movieDbSearchResults);
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public class TrailersQueryTask extends AsyncTask<URL, Void, String>{

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            Log.i(LOG_TAG, "TrailersQueryTask doInBackground() method called...");
            URL searchUrl = params[0];
            String trailersSearchResults = null;
            try {
                trailersSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return trailersSearchResults;
        }

        @Override
        protected void onPostExecute(String trailersSearchResults){
            Log.i(LOG_TAG, "TrailersQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(trailersSearchResults != null && !trailersSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(trailersSearchResults);
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public class MovieTitleQueryTask extends AsyncTask<URL, Void, String>{

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            Log.i(LOG_TAG, "MovieTitleQueryTask doInBackground() method called...");
            URL searchUrl = params[0];
            String movieTitleSearchResults = null;
            try {
                movieTitleSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return movieTitleSearchResults;
        }

        @Override
        protected void onPostExecute(String movieTitleSearchResults){
            Log.i(LOG_TAG, "MovieTitleQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieTitleSearchResults != null && !movieTitleSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(movieTitleSearchResults);
            }  else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public class UserReviewsQueryTask extends AsyncTask<URL, Void, String>{

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            Log.i(LOG_TAG, "UserReviewsQueryTask doInBackground() method called...");
            URL searchUrl = params[0];
            String userReviewsSearchResults = null;
            try {
                userReviewsSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return userReviewsSearchResults;
        }

        @Override
        protected void onPostExecute(String userReviewsSearchResults){
            Log.i(LOG_TAG, "UserReviewsQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(userReviewsSearchResults != null && !userReviewsSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(userReviewsSearchResults);
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
            Context context = MainActivity.this;
            moviePopularityType = "popularity.desc"; // Most popular movies
            String textToShow = "Popular Movies clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            makeMovieDbPopularityQuery(moviePopularityType);
            return true;
        }

        if(itemThatWasClicked == R.id.action_highly_rated_movies){
            Log.i(LOG_TAG, "onOptionsItemSelected() method -- highly rated movies called...");
            Context context = MainActivity.this;
            moviePopularityType = "vote_average.desc"; // Most highly rated movies
            String textToShow = "Highly Rated Movies clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            makeMovieDbPopularityQuery(moviePopularityType);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
