package com.waakye.android.popularmovies9;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // TextView to display the error message
    private TextView mErrorMessageDisplay;

    // Loading Indicator
    private ProgressBar mLoadingIndicator;

    private RecyclerView mMoviesList;

    private MovieAdapter mAdapter;

    private RecyclerView.LayoutManager layoutManager;

    private List<MovieListing> jsonMovieDataList = new ArrayList<>();

    protected String[] simpleJsonMovieData;

    // Option selected in onOptionsItemSelected method
    private int itemThatWasClicked;

    private Toast mToast;

    private TextView mSearchResultsTextView;

    private URL movieSearchUrl;

    // MovieId of "Jack Reacher" movie is "343611"
    private String movieId = "343611";

    private String movieTitle = "Jack Reacher";

    private String edit_text_search_terms = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate() method called...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set RecyclerView variable to the View with id recycler_view_movies
        mMoviesList = (RecyclerView)findViewById(R.id.recycler_view_movies);

        // Define the RecyclerView with a fixed size
        mMoviesList.setHasFixedSize(true);

        // Define the layout being used as a GridLayout with 3 columns
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);

        // Set the RecyclerView to be attached to the GridLayout
        mMoviesList.setLayoutManager(layoutManager);

        // Attach the RecyclerView to the MovieAdapter
        mMoviesList.setAdapter(mAdapter);

        // Currently, the Adapter is loaded with String array data of the poster image urls
        // Pass in second "this" as the ListItemClickListener to the MovieAdapter constructor
        /*
         * The MovieAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new MovieAdapter(this, simpleJsonMovieData, this);

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

    private void makeMovieDbPopularityQuery(int popularityType){

        Log.i(LOG_TAG, "makeMovieDbPopularityQuery() method called...");
        new MovieDbQueryTask().execute(itemThatWasClicked);
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
        mMoviesList.setVisibility(View.VISIBLE);
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
        mMoviesList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i(LOG_TAG, "onListItem() method called...");
        if (mToast != null) {
            mToast.cancel();
        }

        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();
    }

    public class MovieDbQueryTask extends AsyncTask<Integer, Void, List<MovieListing>> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieListing> doInBackground(Integer... params) {
            Log.i(LOG_TAG, "MovieDbQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            // Assign to an int called popType the menu item selected
            int popType = itemThatWasClicked;
            Log.i(LOG_TAG, "popType: " + itemThatWasClicked);

            // Using NetworkUtils method to create a URL for either
            // 1) most popular movies or
            // 2) top rated movies
            URL movieSearchUrl = NetworkUtils.createPopularityTypeUrl(popType);
            Log.i(LOG_TAG, "movieSearchUrl is: " + movieSearchUrl);

            try {
                Log.i(LOG_TAG, "try-catch block query for json movie response");

                // Get the HTTP response to determine whether to create an internet connection
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieSearchUrl);

                jsonMovieDataList = MovieDbJsonUtils.extractItemFromJson(jsonMovieResponse);

                // Returns a List of MovieListing objects
                return jsonMovieDataList;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieListing> movieDbSearchResults){
            Log.i(LOG_TAG, "MovieDbQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movieDbSearchResults != null && !movieDbSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                // A String array of movie Urls based on the List of MovieListing objects
                String[] movieUrls = extractMovieUrlsInMain(movieDbSearchResults);

                // Sets the RecyclerView Adapter, MovieAdapter, to the data source, a String array of
                // movie urls
                mAdapter.setMovieData(movieUrls);

                // RecyclerView's adapter is set to the RecyclerView Adapter
                mMoviesList.setAdapter(mAdapter);
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    public String[] extractMovieUrlsInMain (List<MovieListing> movieDbSearchResults){
        Log.i(LOG_TAG, "extractMovieUrlsInMain() method callled...");

        // Create a String[] array with a length equal to the size of the List of Movies
        String[] moviePosterUrls = new String[movieDbSearchResults.size()];
        int index = 0;
        for(MovieListing movie : movieDbSearchResults){
            moviePosterUrls[index] = (String) movie.getMoviePosterPath();
            index++;
        }
        return moviePosterUrls;
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
        itemThatWasClicked = item.getItemId();
        if(itemThatWasClicked == R.id.action_popular_movies) {
            // Menu item that was clicked
            itemThatWasClicked = 1;
            makeMovieDbPopularityQuery(itemThatWasClicked);
            Log.i(LOG_TAG, "itemThatWasClicked: " + itemThatWasClicked);
//            makeTrailerQuery(movieId);
//            makeUserReviewsQuery(movieId);
//            makeUrlMovieTitleQueryString(movieTitle);
            return true;
        }

        if(itemThatWasClicked == R.id.action_highly_rated_movies){
            itemThatWasClicked = 2;
            makeMovieDbPopularityQuery(itemThatWasClicked);
            Log.i(LOG_TAG, "itemThatWasClicked: " + itemThatWasClicked);
//            makeTrailerQuery(movieId);
//            makeUserReviewsQuery(movieId);
//            makeUrlMovieTitleQueryString(movieTitle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
