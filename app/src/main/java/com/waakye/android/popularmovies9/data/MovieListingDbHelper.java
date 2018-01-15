package com.waakye.android.popularmovies9.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;

/**
 * Created by lesterlie on 12/31/17.
 */


public class MovieListingDbHelper extends SQLiteOpenHelper {

    public static String LOG_TAG = MovieListingDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    public static Cursor getFavoriteMovie;

    public MovieListingDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        final String SQL_CREATE_MOVIE_FAVORITES_TABLE = "CREATE TABLE " +
                MovieListingEntry.TABLE_NAME + " (" +
                MovieListingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieListingEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieListingEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT, " +
                MovieListingEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                MovieListingEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT, " +
                MovieListingEntry.COLUMN_MOVIE_ID + " TEXT, " +
                MovieListingEntry.COLUMN_MOVIE_FAVORITE + " INTEGER " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListingEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public static Cursor getFavoriteMovie(String clickedPosition, SQLiteDatabase db){
        Log.i(LOG_TAG, "getFavoriteMovie() method called...");
        String[] projectionAllColumns = {
                MovieListingEntry._ID,
                MovieListingEntry.COLUMN_MOVIE_TITLE,
                MovieListingEntry.COLUMN_MOVIE_SYNOPSIS,
                MovieListingEntry.COLUMN_MOVIE_POSTER_PATH,
                MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                MovieListingEntry.COLUMN_MOVIE_RELEASE_DATE,
                MovieListingEntry.COLUMN_MOVIE_ID
        };

        String selection = MovieListingEntry._ID + " =?";
        String[] selection_args = {String.valueOf(clickedPosition)};
        Cursor cursor = db.query(MovieListingEntry.TABLE_NAME, projectionAllColumns, selection,
                selection_args, null, null, null);
        return cursor;
    }

}
