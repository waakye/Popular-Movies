package com.waakye.android.popularmovies9.data;

import android.content.ContentResolver;
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

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on
     * the device.
     */
    public static final String CONTENT_AUTHORITY = "com.waakye.android.popularmovies9";

    /**
     * Use the CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favorites" directory
    public static final String PATH_FAVORITES = "favorites";


    /* Inner class that defines the table contents */
    public static class MovieListingEntry implements BaseColumns {

        /**
         * This is the {@link Uri} used to get a full list of favorites
         */
        // MovieListingEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVORITES);

        /**
         * THE MIME TYPE OF THE {@link #CONTENT_URI} for a list of favorites
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        /**
         * The MIME type for the {@link #CONTENT_URI} for a single favorite movie
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

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
