package com.waakye.android.popularmovies9.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.waakye.android.popularmovies9.R;

/**
 * Created by lesterlie on 12/31/17.
 */

public class MoviePreferences {

    public static String LOG_TAG = MoviePreferences.class.getSimpleName();

    // Empty constructor
    MoviePreferences(){}

    // Popularity Types
    public final static int MOST_POPULAR_MOVIES_POPULARITY_TYPE = 1;
    public final static int HIGHLY_RATED_MOVIES_POPULARITY_TYPE = 2;
    public final static int MY_FAVORITE_MOVIES_POPULARITY_TYPE = 3;


    public static int getPreferredPopularityType(Context context){
        Log.i(LOG_TAG, "getPreferredPopularityType() method called...");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForPopularityType = context.getString(R.string.pref_key);
        String defaultPopularityType = context.getString(R.string.pref_popularity_most_popular_value);
        String preferredPopularityType = prefs.getString(keyForPopularityType, defaultPopularityType);

        // Possible popularity types as Strings
        String mostPopularMovies = context.getString(R.string.pref_popularity_most_popular_value);
        String highlyRatedMovies = context.getString(R.string.pref_popularity_highly_rated_value);
        String myFavoriteMovies = context.getString(R.string.pref_popularity_my_favorites_value);

        int userPrefersPopularityType;
        if(mostPopularMovies.equals(preferredPopularityType)){
            userPrefersPopularityType = MOST_POPULAR_MOVIES_POPULARITY_TYPE;
        } else if (highlyRatedMovies.equals(preferredPopularityType)){
            userPrefersPopularityType = HIGHLY_RATED_MOVIES_POPULARITY_TYPE;
        } else {
            userPrefersPopularityType = MY_FAVORITE_MOVIES_POPULARITY_TYPE;
        }
        return userPrefersPopularityType;
    }
}
