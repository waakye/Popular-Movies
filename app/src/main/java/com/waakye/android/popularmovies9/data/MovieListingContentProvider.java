package com.waakye.android.popularmovies9.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;

import static com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry.COLUMN_MOVIE_ID;
import static com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry.TABLE_NAME;

/**
 * Created by lesterlie on 1/5/18.
 */

public class MovieListingContentProvider extends ContentProvider {

    public static final String LOG_TAG = MovieListingContentProvider.class.getSimpleName();

    // Define the final integer constants for the directory of favorites and a single favorite movie
    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer.  This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize.  All paths added to the UriMatcher have a corresponding code to
        // return when a match is found.

        // The content URI of the form "content://com.waakye.android.popularmovies9/favorites"
        // will map to the integer code {@link #FAVORITES}. This URI is used to provide access to
        // MULTIPLE rows of the favorites table.
        sUriMatcher.addURI(MovieListingContract.CONTENT_AUTHORITY,
                MovieListingContract.PATH_FAVORITES, FAVORITES);

        // The content URI of the form "content://com.waakye.android.popularmovies9/favorites/#"
        // will map to the integer code {@link #FAVORITE_WITH_ID}.  This is used to provide access to
        // ONE single row of the favorites table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.waakye.android.popularmovies9/favorites/3" matches, but
        // "content://com.waakye.android.popularmovies9/favorites" does not match.
        sUriMatcher.addURI(MovieListingContract.CONTENT_AUTHORITY,
                MovieListingContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);
    }

    // Member variable for a MovieListingDbHelper that's initialized in the onCreate() method
    private MovieListingDbHelper mMovieListingDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieListingDbHelper = new MovieListingDbHelper(context);
        return true;
    }

    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        // Get access to the favorite database (to write new data to)
        final SQLiteDatabase db = mMovieListingDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the favorites directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match){
            case FAVORITES:
                // Inserting values into favorites table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ){
                    returnUri = ContentUris.withAppendedId(MovieListingEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                // Default case throws an UnsupportedOperationException
                throw new UnsupportedOperationException("insert Unknown uri: " + uri);
        }

        // Notify the resolver if uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    // Implement query to handle request for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        SQLiteDatabase db = mMovieListingDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor retCursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        // Query for the favorites directory and write a default case
        switch(match){
            // Query for the favorites directory
            case FAVORITES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_WITH_ID:
                retCursor =db.query(TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Cannot query unknown URI: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;

    }


    /**
     * Deletes data at given URI with optional arguments for more fine tuned deletions
     *
     * @param uri           The full URI to query
     * @param selection     An option restriction to apply to rows when deleting
     * @param selectionArgs Used in conjunction with the selection statement
     * @return              The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mMovieListingDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int favoriteDeleted;

        // Use selections to delete a favorite movie by its row ID
        switch(match){
            case FAVORITE_WITH_ID:

                favoriteDeleted = db.delete(TABLE_NAME, COLUMN_MOVIE_ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("delete unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of favorited movies deleted
        if (favoriteDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return favoriteDeleted;
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
