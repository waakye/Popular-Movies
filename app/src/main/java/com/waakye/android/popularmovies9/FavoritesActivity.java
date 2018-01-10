package com.waakye.android.popularmovies9;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.waakye.android.popularmovies9.data.MovieListingContract;

/**
 * Created by lesterlie on 1/9/18.
 */

public class FavoritesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomCursorAdapter.ListItemClickListener{

    public static String LOG_TAG = FavoritesActivity.class.getSimpleName();

    private static int FAVORITE_LOADER_ID = 33;

    // Member variables for adapter and RecyclerView
    private CustomCursorAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private Cursor cursorData;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.i(LOG_TAG, "onCreate() method called...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_favorite_movies);

        // Set the Layout for the RecyclerView to be grid layout
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(this, cursorData, this);
        mRecyclerView.setAdapter(mAdapter);

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);

    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted so this restarts the loader
     * to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume(){
        super.onResume();

        // re-queries for all favorited movies
        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the favorite data
            Cursor mFavoriteData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading(){
                if(mFavoriteData != null){
                    // Delivers any previously loaded data immediately
                    deliverResult(mFavoriteData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data
                try {
                    // Call the query method on the resolver with the correct URI from the contract class
                    return getContentResolver().query(MovieListingContract.MovieListingEntry.CONTENT_URI,
                            null, null, null, null);

                } catch (Exception e){
                    Log.e(LOG_TAG, "Failed to asynchronously load data");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data){
                mFavoriteData = data;
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}