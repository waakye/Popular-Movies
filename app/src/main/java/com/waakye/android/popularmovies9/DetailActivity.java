package com.waakye.android.popularmovies9;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.waakye.android.popularmovies9.adapters.MovieListingAdapter;
import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;
import com.waakye.android.popularmovies9.data.MovieListingDbHelper;
import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lesterlie on 12/26/17.
 */

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<String[]> {

    public static String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int USER_REVIEW_LOADER_ID = 11;


    // TextView to display the error message
    private TextView mDetailActivityErrorMessageDisplay;

    // Loading Indicator
    private ProgressBar mDetailActivityLoadingIndicator;

    private TextView mUserReviewsTextView;

    private TextView mTrailersTextView;

    private static String mIndividualMovieId;

    TextView mTextViewMovieTitle;
    TextView mTextViewMovieSynopsis;
    TextView mTextViewMovieReleaseDate;
    TextView mTextViewMovieVoteAverage;
    ImageView mMoviePoster;

    private Context context;

    private String mTitle;
    private String mSynopsis;
    private String mPosterPath;
    private String mVoteAverage;
    private String mReleaseDate;
    private String mMovieId;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MovieListing movieListing = getIntent().getParcelableExtra("movie");

        // Getting reference to TextView text_view_movie_title in activity_detail
        mTextViewMovieTitle = (TextView)findViewById(R.id.text_view_movie_title);

        // Getting reference to TextView text_view_movie_synopsis in activity_detail
        mTextViewMovieSynopsis = (TextView)findViewById(R.id.text_view_movie_synopsis);
        mTextViewMovieVoteAverage = (TextView)findViewById(R.id.text_view_movie_vote_average);
        mTextViewMovieReleaseDate = (TextView)findViewById(R.id.text_view_movie_release_date);

        mMoviePoster = (ImageView) findViewById(R.id.image_view_detail_activity_movie_poster);

        mTitle = movieListing.getMovieTitle();
        mSynopsis = movieListing.getMovieSynopsis();
        mPosterPath = movieListing.getMoviePosterPath();
        mVoteAverage = movieListing.getMovieVoteAverage();
        mReleaseDate = movieListing.getMovieReleaseDate();
        mMovieId = movieListing.getMovieId();

        if (movieListing != null){
            mTextViewMovieTitle.setText(mTitle);
            mTextViewMovieSynopsis.setText(mSynopsis);
            mTextViewMovieVoteAverage.setText(" " + mVoteAverage);
            mTextViewMovieReleaseDate.setText(" " + mReleaseDate);
            mIndividualMovieId = mMovieId;
        }

        String moviePosterUrl = MovieListingAdapter.MOVIE_POSTER_PREFIX + mPosterPath;
        Log.i(LOG_TAG, "moviePosterUrl: " + moviePosterUrl);

        Picasso.with(context).load(moviePosterUrl).into(mMoviePoster);

        mDetailActivityErrorMessageDisplay = (TextView)findViewById(R.id.detail_activity_text_view_error_message_display);

        mDetailActivityLoadingIndicator = (ProgressBar) findViewById(R.id.detail_activity_progress_bar_loading_indicator);

        mUserReviewsTextView = (TextView) findViewById(R.id.text_view_user_reviews);

        makeUserReviewsQuery(mIndividualMovieId);
        Button trailerButton = (Button)findViewById(R.id.trailer_button);
        trailerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(DetailActivity.this, TrailerActivity.class);
                intent.putExtra("movieId", mIndividualMovieId);
                startActivity(intent);
            }
        });

        Button addToFavoriteButton = (Button)findViewById(R.id.favorites_button);
        addToFavoriteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick() method called...");
                onClickAddFavorite(view);
            }
        });

        Button removeFavoriteButton = (Button)findViewById(R.id.remove_from_favorites_button);
        removeFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick() method called...");
                onClickRemoveFavorite(view);
            }
        });
    }

    /**
     * onClickAddFavorite is called when "ADD" button is clicked.
     * It retrieves user input and inserts that new favorite data into the underlying database
     * @param view
     */
    public void onClickAddFavorite(View view){
        Log.i(LOG_TAG, "onClickAddFavorite() method called...");

        // Use getAllMovieIds method to retrieve the storedMovieIds
        String[] storedMovieIds = getAllMovieIds();

        if (compareMovieIdsInFavorites(storedMovieIds, mMovieId) != true) {
            // Create database helper
            MovieListingDbHelper mDbHelper = new MovieListingDbHelper(this);

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Insert favorite movie via a ContentResolver

            // Create a new empty ContentValues object
            ContentValues cv = new ContentValues();

            cv.put(MovieListingEntry.COLUMN_MOVIE_TITLE, mTitle);
            cv.put(MovieListingEntry.COLUMN_MOVIE_SYNOPSIS, mSynopsis);
            cv.put(MovieListingEntry.COLUMN_MOVIE_POSTER_PATH, mPosterPath);
            cv.put(MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE, mVoteAverage);
            cv.put(MovieListingEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
            cv.put(MovieListingEntry.COLUMN_MOVIE_ID, mMovieId);

            // Insert a new row for favorite in the database, returning the ID of that new row
            long newRowId = db.insert(MovieListingEntry.TABLE_NAME, null, cv);

            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion
                Toast.makeText(this, "Error with saving movie", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID
                Toast.makeText(this, "Movie saved with row ID: " + newRowId, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Movie is already stored in Favorites", Toast.LENGTH_SHORT).show();
        }


        // Finish activity (this returns back to MainActivity)
        finish();

    }

    /**
     * Helper method to get the movieIds contained in the database
     */
    private String[] getAllMovieIds() {

        // Create database helper
        MovieListingDbHelper mDbHelper = new MovieListingDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use
        // after the query
        String[] projectionAllColumns = {
                MovieListingEntry._ID,
                MovieListingEntry.COLUMN_MOVIE_TITLE,
                MovieListingEntry.COLUMN_MOVIE_SYNOPSIS,
                MovieListingEntry.COLUMN_MOVIE_POSTER_PATH,
                MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                MovieListingEntry.COLUMN_MOVIE_RELEASE_DATE,
                MovieListingEntry.COLUMN_MOVIE_ID,
        };

        Cursor cursor = db.query(
                MovieListingEntry.TABLE_NAME,
                projectionAllColumns,
                MovieListingEntry.COLUMN_MOVIE_ID,
                null,
                null,
                null,
                null
        );

        // Based on StackOverflow: https://stackoverflow.com/questions/18863816/putting-cursor-data-into-an-array
        cursor.moveToFirst();
        List<String> storedMovieIds = new ArrayList<String>();
        while(!cursor.isAfterLast()){
            storedMovieIds.add(cursor.getString(cursor.getColumnIndex("movieId")));
            cursor.moveToNext();
        }
        cursor.close();
        return storedMovieIds.toArray(new String[storedMovieIds.size()]);

    }

    // Used: https://www.programcreek.com/2014/04/check-if-array-contains-a-value-java/
    public static boolean compareMovieIdsInFavorites(String[] array, String targetValue){
        return Arrays.asList(array).contains(targetValue);
    }


    public void onClickRemoveFavorite(View view){

        Toast.makeText(this, "Remove button pressed", Toast.LENGTH_SHORT).show();

        Uri uri = MovieListingEntry.CONTENT_URI.buildUpon()
                .appendEncodedPath(mIndividualMovieId).build();

        getContentResolver().delete(uri, null, new String[]{mIndividualMovieId});

    }

    private void makeUserReviewsQuery(String movieId){

        Log.i(LOG_TAG, "makeUserReviewsQuery() method called...");
//        new UserReviewsQueryTask().execute(mIndividualMovieId);

        /**
         * This ID will uniquely identify the Loader.  We can use it to get a handle on our Loader
         * at a later point in time through the support LoaderManager
         */
        int loaderId = USER_REVIEW_LOADER_ID;

        /**
         * From DetailActivity, we have implemented the LoaderCallbacks interface with the type of
         * String[].  The variable callback is passed to the call to initLoader below.  This means
         * that whenever the loaderManager has something to notify us of, it will do so through
         * this callback.
         */
        LoaderCallbacks<String[]> callback = DetailActivity.this;

        /**
         * The second parameter of initLoader method below is a Bundle.  Optionally, you can pass
         * a Bundle to the initLoader that you can then access from within the onCreateLoader
         * callback.
         */
        Bundle bundleForLoader = null;

        /**
         * Ensures that a loader is initialized and active.  If the loader doesn't already exist,
         * one is created and (if the activity/fragment is currently started) starts the loader.
         * Otherwise, the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {
        // First, make sure the error is invisible
        mDetailActivityErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mUserReviewsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        mUserReviewsTextView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mDetailActivityErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    /**
     * Instantiate and return a new loader for the given ID.
     *
     * @param id    The ID whose loader is to be created
     * @param loaderArgs  Any arguments supplied by the caller
     *
     * @return  Return a new Loader instance that is ready to start loading
     */
    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            /* This String[] will hold and help cache our User Review data */
            String[] userReviewsData = null;

            @Override
            protected void onStartLoading(){
                Log.i(LOG_TAG, "onStarLoading() method called...");
                if(userReviewsData != null){
                    deliverResult(userReviewsData);
                } else {
                    mDetailActivityLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                Log.i(LOG_TAG, "loadInBackground() method called...");

                // Use NetworkUtils method to create a URL for user reviews URL
                URL userReviewsSearchUrl = NetworkUtils.createUserReviewsUrl(mIndividualMovieId);

                try {
                    Log.i(LOG_TAG, "try-catch block query for json user reviews response");
                    String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(userReviewsSearchUrl);

                    String[] reviewsJsonMovieData = MovieDbJsonUtils
                            .getUserReviewStringsFromJson(DetailActivity.this,jsonReviewsResponse);
                    return reviewsJsonMovieData;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (JSONException e){
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener
             *
             * @param data  The result of the load
             */
            public void deliverResult(String[] data){
                userReviewsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        if(data != null && !data.equals("")){
            showJsonDataView();
            for(String userReviewString : data) {
                mUserReviewsTextView.append((userReviewString) + "\n\n");
            }
        } else {
            // Call showErrorMessage if the result is null in onPostExecute
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
