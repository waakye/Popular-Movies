package com.waakye.android.popularmovies9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waakye.android.popularmovies9.adapters.TrailerListingAdapter;
import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lesterlie on 12/27/17.
 */

public class TrailerActivity extends AppCompatActivity
    implements TrailerListingAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Trailer>>{

    public static String LOG_TAG = TrailerActivity.class.getSimpleName();

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static final String SAVED_LAYOUT_MANAGER = "TrailerActivity.recycler.layout";

    private String movieId;

    // TextView to display the error message
    @BindView(R.id.trailer_activity_text_view_error_message_display)TextView mTrailerActivityErrorMessageDisplay;

    // Loading Indicator
    @BindView(R.id.trailer_activity_progress_bar_loading_indicator)ProgressBar mTrailerActivityLoadingIndicator;

    private TrailerListingAdapter mAdapter;

    private RecyclerView mTrailersList;

    protected List<Trailer> jsonTrailerDataList;

    private static final int TRAILER_LOADER_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        ButterKnife.bind(this); // bind butterknife after
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        movieId = intent.getStringExtra("movieId");
        Log.i(LOG_TAG, "the movie id is " + movieId);

        mTrailersList = (RecyclerView)findViewById(R.id.recycler_view_trailers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTrailersList.setLayoutManager(layoutManager);

        mTrailersList.setHasFixedSize(true);

        mTrailersList.setAdapter(mAdapter);

        mAdapter = new TrailerListingAdapter(this, jsonTrailerDataList, this);

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(TRAILER_LOADER_ID, null, this);

    }

    @Override
    public void onSavedInstanceState(Bundle outState){
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mTrailersList.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null){
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
            mTrailersList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void makeTrailerQuery(String movieId){

        Log.i(LOG_TAG, "makeTrailerQuery() method called...");
        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = TRAILER_LOADER_ID;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * List of MovieListing objects. (implements LoaderCallbacks<List<MovieListing>>)
         * The variable callback is passed to the call to initLoader below. This means that
         * whenever the loaderManager has something to notify us of, it will do so through
         * this callback.
         */
        LoaderManager.LoaderCallbacks<List<Trailer>> callback = TrailerActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = null;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
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
        mTrailerActivityErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON is visible
        mTrailersList.setVisibility(View.VISIBLE);
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
        mTrailersList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mTrailerActivityErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i(LOG_TAG, "onListItem() method called...");

        Trailer individualTrailer = jsonTrailerDataList.get(clickedItemIndex);
        String individualTrailerKey = individualTrailer.getTrailerKey();

        String trailerUrl = YOUTUBE_BASE_URL + individualTrailerKey;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(trailerUrl));
        startActivity(i);
    }

    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Trailer>>(this) {

            /* This List<MovieListing> will hold and help cache our MovieListing data */
            List<Trailer> mTrailerListingData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of their loading data
             */
            @Override
            protected void onStartLoading(){
                if(mTrailerListingData != null){
                    deliverResult(mTrailerListingData);
                } else {
                    mTrailerActivityLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * in the background.
             *
             * @return Trailer data from TheMovieDB as a List of Trailer objects;
             *         null if an error occurs
             */
            @Override
            public List<Trailer> loadInBackground(){

                URL trailerSearchUrl = NetworkUtils.createMovieTrailerUrl(movieId);

                try {
                    String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerSearchUrl);

                    jsonTrailerDataList = MovieDbJsonUtils.extractTrailerFromJson(jsonTrailerResponse);

                    return jsonTrailerDataList;

                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                } catch (JSONException e){
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Trailer> data) {
                mTrailerListingData = data;
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
    public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
        // As soon as the loading is complete, hide the loading indicator
        mTrailerActivityLoadingIndicator.setVisibility(View.INVISIBLE);
        if(data != null && !data.equals("")){
            // Call showJsonDataView if we have valid, non-null results
            showJsonDataView();
            // Sets the RecyclerView Adapter, MovieAdapter, to the data source, a String array of movie urls
            mAdapter.setTrailerData(data);
            // RecyclerView's adapter is set to the RecyclerView Adapter
            mTrailersList.setAdapter(mAdapter);

        } else {
            // Call showErrorMessage if the result is null in onPostExecute
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Trailer>> loader) {
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
        mAdapter.setTrailerData(null);
    }
}
