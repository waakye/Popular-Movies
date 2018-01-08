package com.waakye.android.popularmovies9.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lesterlie on 12/31/17.
 */

public final class MovieListingContract {

    // Prevents someone from accidentally instantiating the contract class
    // Make the constructor private
    private MovieListingContract(){}

    public static String MOVIE_POSTER_PREFIX = "https://image.tmdb.org/t/p/w500";

    public static final String AUTHORITY = "com.waakye.android.popularmovies9";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favorites" directory
    public static final String PATH_FAVORITES = "favorites";

    /* Inner class that defines the table contents */
    public static class MovieListingEntry implements BaseColumns {

        // MovieListingEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // Favorites table and column names
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
