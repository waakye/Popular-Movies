package com.waakye.android.popularmovies9.data;

import android.provider.BaseColumns;

/**
 * Created by lesterlie on 12/31/17.
 */

public final class MovieListingContract {

    // Prevents someone from accidentally instantiating the contract class
    // Make the constructor private
    private MovieListingContract(){}

    /* Inner class that defines the table contents */
    public static class MovieListingEntry implements BaseColumns {

        public static final String TABLE_NAME = "user_favorite_movies";

        public static final String COLUMN_MOVIE_TITLE = "movieTitle";

        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";

        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";

        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movieVoteAverage";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_FAVORITE = "favorite";

    }
}
