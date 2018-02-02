package com.waakye.android.popularmovies9;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.waakye.android.popularmovies9.adapters.MovieListingAdapter;
import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;
import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lesterlie on 12/26/17.
 */

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<String[]> {

    public static String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int USER_REVIEW_LOADER_ID = 11;
    private static final int TRAILER_LOADER_ID = 12;

    // TextView to display the error message
    @BindView(R.id.detail_activity_text_view_error_message_display)TextView mDetailActivityErrorMessageDisplay;

    // Loading Indicator
    @BindView(R.id.detail_activity_progress_bar_loading_indicator)ProgressBar mDetailActivityLoadingIndicator;

    // TextView to display user reviews
    @BindView(R.id.text_view_user_reviews)TextView mUserReviewsTextView;

    private static String mIndividualMovieId;

    @BindView(R.id.detail_activity_scroll_view)ScrollView mScrollView;

    // TextView related to each movieListing
    @BindView(R.id.text_view_movie_title) TextView mTextViewMovieTitle;
    @BindView(R.id.text_view_movie_synopsis) TextView mTextViewMovieSynopsis;
    @BindView(R.id.text_view_movie_release_date) TextView mTextViewMovieReleaseDate;
    @BindView(R.id.text_view_movie_vote_average) TextView mTextViewMovieVoteAverage;
    @BindView(R.id.image_view_detail_activity_movie_poster) ImageView mMoviePoster;

    // Need context for Picasso
    private Context context;

    // Buttons
    @BindView(R.id.trailer_button)Button trailerButton;
    @BindView(R.id.watch_trailer_button)Button mWatchTrailerButton;
    @BindView(R.id.favorites_button)Button addToFavoritesButton;
    @BindView(R.id.remove_from_favorites_button)Button removeFavoriteButton;

    private String mTitle;
    private String mSynopsis;
    private String mPosterPath;
    private String mVoteAverage;
    private String mReleaseDate;
    private String mMovieId;

    private String firstTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this); // bind butterknife after
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MovieListing movieListing = getIntent().getParcelableExtra("movie");

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

        makeUserReviewsQuery(mIndividualMovieId);

        makeSingleTrailerQuery(mIndividualMovieId);

        mWatchTrailerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "watch trailer button onClick() method called...");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(firstTrailer));
                startActivity(i);
            }
        });

        trailerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(DetailActivity.this, TrailerActivity.class);
                intent.putExtra("movieId", mIndividualMovieId);
                startActivity(intent);
            }
        });

        addToFavoritesButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick() method called...");
                onClickAddFavorite(view);
            }
        });

        removeFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick() method called...");
                onClickRemoveFavorite(view);
            }
        });
    }

    // From https://eliasbland.wordpress.com/2011/07/28/how-to-save-the-position-of-a-scrollview-when-the-orientation-changes-in-android/
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[] {mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if(position != null){
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
        }
    }

    /**
     * onClickAddFavorite is called when "ADD" button is clicked.
     * It retrieves user input and inserts that new favorite data into the underlying database
     * @param view
     */
    public void onClickAddFavorite(View view){
        Log.i(LOG_TAG, "onClickAddFavorite() method called...");

        // Insert favorite movie via a ContentResolver
        // Create a new empty ContentValues object
        ContentValues cv = new ContentValues();

        cv.put(MovieListingEntry.COLUMN_MOVIE_TITLE, mTitle);
        cv.put(MovieListingEntry.COLUMN_MOVIE_SYNOPSIS, mSynopsis);
        cv.put(MovieListingEntry.COLUMN_MOVIE_POSTER_PATH, mPosterPath);
        cv.put(MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE, mVoteAverage);
        cv.put(MovieListingEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
        cv.put(MovieListingEntry.COLUMN_MOVIE_ID, mMovieId);

        Uri newUri = getContentResolver().insert(MovieListingEntry.CONTENT_URI, cv);
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

    private void makeSingleTrailerQuery(String movieId){

        Log.i(LOG_TAG, "makeSingleTrailerQuery() method called...");

        /**
         * This ID will uniquely identify the Loader.  We can use it to get a handle on our Loader
         * at a later point in time through the support LoaderManager
         */
        int loaderId = TRAILER_LOADER_ID;

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

    private void showWatchTrailerButton(){
        // First make sure the button is invisible
        mWatchTrailerButton.setVisibility(View.VISIBLE);
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
    public Loader<String[]> onCreateLoader(final int id, Bundle loaderArgs) {


        return new AsyncTaskLoader<String[]>(this) {

            /* This String[] will hold and help cache our User Review data */

            String[] jsonRetrievedData = null;

            String[] trailerData = null;

            String singleTrailer = null;

            @Override
            protected void onStartLoading(){
                Log.i(LOG_TAG, "onStarLoading() method called...");
                if(jsonRetrievedData != null){
                    deliverResult(jsonRetrievedData);
                } else {
                    mDetailActivityLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                Log.i(LOG_TAG, "loadInBackground() method called...");

                switch(id){
                    case USER_REVIEW_LOADER_ID:
                        // Use NetworkUtils method to create a URL for user reviews URL
                        URL userReviewsSearchUrl = NetworkUtils.createUserReviewsUrl(mIndividualMovieId);

                        try {
                            Log.i(LOG_TAG, "try-catch block query for json user reviews response");
                            String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(userReviewsSearchUrl);

                            jsonRetrievedData = MovieDbJsonUtils
                                    .getUserReviewStringsFromJson(DetailActivity.this,jsonReviewsResponse);
                            return jsonRetrievedData;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        } catch (JSONException e){
                            e.printStackTrace();
                            return null;
                        }

                    case TRAILER_LOADER_ID:
                        // Use NetworkUtils method to create a URL for trailers URL
                        URL trailerSearchUrl = NetworkUtils.createMovieTrailerUrl(mIndividualMovieId);

                        try{
                            Log.i(LOG_TAG, "try-catch block query for json trailers response");
                            String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerSearchUrl);

                            trailerData = MovieDbJsonUtils
                                    .getTrailerStringsFromJson(DetailActivity.this, jsonTrailerResponse);

                            singleTrailer = trailerData[0];

                            jsonRetrievedData = new String[]{singleTrailer};

                            return jsonRetrievedData;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        } catch (JSONException e){
                            e.printStackTrace();
                            return null;
                        }
                }
                if(jsonRetrievedData != null){
                    return jsonRetrievedData;
                } else {
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener
             *
             * @param data  The result of the load
             */
            public void deliverResult(String[] data){
                jsonRetrievedData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        switch(loader.getId()){
            case USER_REVIEW_LOADER_ID:
                if(data != null && !data.equals("")){
                    showJsonDataView();
                    for(String userReviewString : data) {
                        mUserReviewsTextView.append((userReviewString) + "\n\n");
                    }
                } else {
                    // Call showErrorMessage if the result is null in onPostExecute
                    showErrorMessage();
                }
                break;
            case TRAILER_LOADER_ID:
                if(data != null && !data.equals("")){
                    firstTrailer = data[0];
                    showWatchTrailerButton();
                } else {
                    // Call showErrorMessage if the result is null in onPostExecute
                    showErrorMessage();
                }
                break;
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
