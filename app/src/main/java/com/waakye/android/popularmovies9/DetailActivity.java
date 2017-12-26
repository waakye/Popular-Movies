package com.waakye.android.popularmovies9;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by lesterlie on 12/26/17.
 */

public class DetailActivity extends Activity {

    TextView mTextViewMovieTitle;
    TextView mTextViewMovieSynopsis;
    TextView mTextViewMoviePosterPath;
    TextView mTextViewMovieReleaseDate;
    TextView mTextViewVoteAverage;
    TextView mTextViewMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieListing movieListing = getIntent().getParcelableExtra("movie listing");

        mTextViewMovieTitle = (TextView)findViewById(R.id.text_view_movie_title);
        mTextViewMovieSynopsis = (TextView)findViewById(R.id.text_view_movie_synopsis);
        mTextViewMoviePosterPath = (TextView)findViewById(R.id.text_view_movie_poster_path);
        mTextViewVoteAverage = (TextView)findViewById(R.id.text_view_movie_vote_average);
        mTextViewMovieReleaseDate = (TextView)findViewById(R.id.text_view_movie_release_date);
        mTextViewMovieId = (TextView)findViewById(R.id.text_view_movie_id);

        if (movieListing != null){
            mTextViewMovieTitle.setText("Movie Title: " + movieListing.movieTitle);
            mTextViewMovieSynopsis.setText("Movie Synopsis: " + movieListing.movieSynopsis);
            mTextViewMoviePosterPath.setText("Poster Path: " + movieListing.moviePosterPath);
            mTextViewVoteAverage.setText("Vote Average: " + movieListing.movieVoteRating);
            mTextViewMovieReleaseDate.setText("Release Date: " + movieListing.movieReleaseDate);
            mTextViewMovieId.setText("Movie Id: " + movieListing.movieId);
        }
    }
}
