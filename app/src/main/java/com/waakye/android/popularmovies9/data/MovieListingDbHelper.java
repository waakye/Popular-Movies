package com.waakye.android.popularmovies9.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;

/**
 * Created by lesterlie on 12/31/17.
 */


public class MovieListingDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

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
}
