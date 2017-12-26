package com.waakye.android.popularmovies9;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by lesterlie on 12/26/17.
 */

public class DetailActivity extends AppCompatActivity {

    public static String LOG_DATA = DetailActivity.class.getSimpleName();

    TextView mTextViewMovieTitle;
    TextView mTextViewMovieSynopsis;
    TextView mTextViewMoviePosterPath;
    TextView mTextViewMovieReleaseDate;
    TextView mTextViewMovieVoteAverage;
    TextView mTextViewMovieId;
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
        mTextViewMoviePosterPath = (TextView)findViewById(R.id.text_view_movie_poster_path);
        mTextViewMovieVoteAverage = (TextView)findViewById(R.id.text_view_movie_vote_average);
        mTextViewMovieReleaseDate = (TextView)findViewById(R.id.text_view_movie_release_date);
        mTextViewMovieId = (TextView)findViewById(R.id.text_view_movie_id);

        mMoviePoster = (ImageView) findViewById(R.id.image_view_detail_activity_movie_poster);

        if (movieListing != null){
            mTextViewMovieTitle.setText(movieListing.getMovieTitle());
            mTextViewMovieSynopsis.setText(movieListing.getMovieSynopsis());
            mTextViewMoviePosterPath.setText(" " + MovieAdapter.MOVIE_POSTER_PREFIX
                    + movieListing.getMoviePosterPath());
            mTextViewMovieVoteAverage.setText(" " + movieListing.getMovieVoteAverage());
            mTextViewMovieReleaseDate.setText(" " + movieListing.getMovieReleaseDate());
            mTextViewMovieId.setText(" " + movieListing.getMovieId());
        }

        String moviePosterUrl =
                MovieAdapter.MOVIE_POSTER_PREFIX + movieListing.getMoviePosterPath();
        Log.i(LOG_DATA, "moviePosterUrl: " + moviePosterUrl);

        Picasso.with(context).load(moviePosterUrl).into(mMoviePoster);

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
