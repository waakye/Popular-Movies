package com.waakye.android.popularmovies9.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by lesterlie on 1/5/18.
 */

public class MovieListingContentProvider extends ContentProvider {

    // Define the final integer constants for the directory of favorites and a single favorite movie
    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Define a static buildUriMatcher method that associates URI's with their int match
     *
     * Initialize a new matcher object without any matches, then use .addURI(String authority,
     * String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher(){

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the favorites directory and a single item by ID.
         */
        uriMatcher.addURI(MovieListingContract.AUTHORITY, MovieListingContract.PATH_FAVORITES,
                FAVORITES);
        uriMatcher.addURI(MovieListingContract.AUTHORITY, MovieListingContract.PATH_FAVORITES
                + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a MovieListingDbHelper that's initialized in the onCreate() method
    private MovieListingDbHelper mMovieListingDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieListingDbHelper = new MovieListingDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
