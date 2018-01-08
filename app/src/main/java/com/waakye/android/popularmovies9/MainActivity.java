package com.waakye.android.popularmovies9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.waakye.android.popularmovies9.adapters.MovieListingAdapter;
import com.waakye.android.popularmovies9.data.MoviePreferences;
import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MovieListingAdapter.ListItemClickListener,
        LoaderCallbacks<List<MovieListing>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Popularity types
    public final static int MOST_POPULAR_MOVIES_POPULARITY_TYPE = 1;
    public final static int HIGHLY_RATED_MOVIES_POPULARITY_TYPE = 2;
    public final static int MY_FAVORITE_MOVIES_POPULARITY_TYPE = 3;
    public final static int SEARCH_FAVORITE_MOVIES = 4;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    // TextView to display the error message
    private TextView mErrorMessageDisplay;

    // Loading Indicator
    private ProgressBar mLoadingIndicator;

    private RecyclerView mMoviesList;

    private MovieListingAdapter mAdapter;

    private List<MovieListing> jsonMovieDataList = new ArrayList<>();

    protected List<MovieListing> listJsonMovieData;

    // Option selected in onOptionsItemSelected method
    private int itemThatWasClicked;

    private TextView mSearchResultsTextView;

    private String movieTitle = "Jack Reacher";

    private static final int MOVIE_POSTER_LOADER_ID = 0;

    private int popType = 0;

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

        /**
         * The MovieListingAdapter is responsible for displaying each item in the list.  The first
         * "this" refers to a list of MovieListing objects, the second "this" refers ot the
         * ListItemClickListener of the MovieListingAdapter constructor
         */
        mAdapter = new MovieListingAdapter(this, listJsonMovieData, this);

        mErrorMessageDisplay = (TextView)findViewById(R.id.text_view_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar_loading_indicator);

        int loaderId = MOVIE_POSTER_LOADER_ID;

        LoaderCallbacks<List<MovieListing>> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        Log.d(LOG_TAG, "onCreate: registering preference changed listener");

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void makeMovieDbPopularityQuery(int popularityType){

        Log.i(LOG_TAG, "makeMovieDbPopularityQuery() method called...");
        // Since a loader is used onCreate, we must restart the loader
        getSupportLoaderManager().restartLoader(MOVIE_POSTER_LOADER_ID, null, this);
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

    @Override
    protected void onStart(){
        super.onStart();

        /**
         * If the preferences for popularity type have changed since the user was last in
         * MainActivity, perform another query and set the flag to false.
         */
        if(PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(LOG_TAG, "onStart() method called and preferences were updated...");
            getSupportLoaderManager().restartLoader(MOVIE_POSTER_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    /**
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i(LOG_TAG, "onListItem() method called...");

        MovieListing individualMovie = jsonMovieDataList.get(clickedItemIndex);
        String individualTitle = individualMovie.getMovieTitle();
        String individualSynopsis = individualMovie.getMovieSynopsis();
        String individualVoteAverage = individualMovie.getMovieVoteAverage();
        String individualReleaseDate = individualMovie.getMovieReleaseDate();
        String individualPosterPath = individualMovie.getMoviePosterPath();
        String individualMovieId = individualMovie.getMovieId();

        MovieListing mlisting = new MovieListing(individualTitle, individualSynopsis,
                individualPosterPath, individualVoteAverage, individualReleaseDate, individualMovieId );

        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("movie", mlisting);
        startActivity(intent);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<MovieListing>> onCreateLoader(int id, Bundle loaderArgs) {

        return new AsyncTaskLoader<List<MovieListing>>(this) {

            /* This List<MovieListing> will hold and help cache our MovieListing data */
            List<MovieListing> mMovieListingData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of their loading data
             */
            @Override
            protected void onStartLoading(){
                if(mMovieListingData != null){
                    deliverResult(mMovieListingData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from TheMovieDB in the background.
             *
             * @return Movie data from TheMovieDB as a List of MovieListing objects;
             *         null if an error occurs
             */
            @Override
            public List<MovieListing> loadInBackground() {

                // Used SharedPreferences to determine the popularity type
                popType = MoviePreferences.getPreferredPopularityType(MainActivity.this);

                // Using NetworkUtils method to create a URL for either
                // 1) most popular movies or
                // 2) top rated movies
                URL movieSearchUrl = NetworkUtils.createPopularityTypeUrl(popType);
                Log.i(LOG_TAG, "movieSearchUrl is: " + movieSearchUrl);

                try {
                    Log.i(LOG_TAG, "try-catch block query for json movie response");

                    // Get the HTTP response to determine whether to create an internet connection
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieSearchUrl);

                    jsonMovieDataList =
                            MovieDbJsonUtils.extractItemFromJson(jsonMovieResponse);

                    // Returns a List of MovieListing objects
                    return jsonMovieDataList;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<MovieListing> data) {
                mMovieListingData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<MovieListing>> loader, List<MovieListing> data) {

        // As soon as the loading is complete, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(data != null && !data.equals("")){
            // Call showJsonDataView if we have valid, non-null results
            showJsonDataView();
            // Sets the RecyclerView Adapter, MovieAdapter, to the data source, a String array of movie urls
            mAdapter.setMovieData(data);
            // RecyclerView's adapter is set to the RecyclerView Adapter
            mMoviesList.setAdapter(mAdapter);

        } else {
            // Call showErrorMessage if the result is null in onPostExecute
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MovieListing>> loader) {
         /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mAdapter.setMovieData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        itemThatWasClicked = item.getItemId();
        if(itemThatWasClicked == R.id.action_search_favorite_movies){
            itemThatWasClicked = SEARCH_FAVORITE_MOVIES;
            Intent i = new Intent(this, SearchMoviesActivity.class);
            startActivity(i);
        }

        if (itemThatWasClicked == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        if (itemThatWasClicked == R.id.action_my_favorite_movies){
            Toast toast = Toast.makeText(this, "My Favorites selected", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

         PREFERENCES_HAVE_BEEN_UPDATED = true;
    }
}
