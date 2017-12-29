package com.waakye.android.popularmovies9;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by lesterlie on 12/26/17.
 */

public class DetailActivity extends AppCompatActivity {

    public static String LOG_TAG = DetailActivity.class.getSimpleName();

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

        if (movieListing != null){
            mTextViewMovieTitle.setText(movieListing.getMovieTitle());
            mTextViewMovieSynopsis.setText(movieListing.getMovieSynopsis());
            mTextViewMovieVoteAverage.setText(" " + movieListing.getMovieVoteAverage());
            mTextViewMovieReleaseDate.setText(" " + movieListing.getMovieReleaseDate());
            mIndividualMovieId = movieListing.getMovieId();
        }

        String moviePosterUrl =
                MovieAdapter.MOVIE_POSTER_PREFIX + movieListing.getMoviePosterPath();
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
    }

    private void makeUserReviewsQuery(String movieId){

        Log.i(LOG_TAG, "makeUserReviewsQuery() method called...");
        new UserReviewsQueryTask().execute(mIndividualMovieId);

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

    public class UserReviewsQueryTask extends AsyncTask<String, Void, String[]> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDetailActivityLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.i(LOG_TAG, "UserReviewsQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            URL userReviewsSearchUrl = NetworkUtils.createUserReviewsUrl(mIndividualMovieId);

            try {
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

        @Override
        protected void onPostExecute(String[] userReviewsSearchResults){
            Log.i(LOG_TAG, "UserReviewsQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            mDetailActivityLoadingIndicator.setVisibility(View.INVISIBLE);
            if(userReviewsSearchResults != null && !userReviewsSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                for(String userReviewString : userReviewsSearchResults) {
                    mUserReviewsTextView.append((userReviewString) + "\n\n");
                }
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
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
