package com.waakye.android.popularmovies9.utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by lesterlie on 12/26/17.
 */

public class MovieDbJsonUtils {

    public static String LOG_TAG = MovieDbJsonUtils.class.getSimpleName();

    public static String MOVIE_POSTER_PREFIX = "https://image.tmdb.org/t/p/w500";

    /**
     * This method parses JSON from a web response and returns an array of Strings describing the
     * movie possibilities.
     *
     * @param movieJsonStr  JSON response from the server
     *
     * @return Array of Strings describing the movie data
     *
     * @throws org.json.JSONException  If JSON data cannot be properly parsed.
     */
    public static String[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {
        Log.i(LOG_TAG, "getSimpleMovieStringsFromJson() method called...");

        /* String array to hold each movie's data */
        String[] parsedMovieData = null;

        JSONObject baseMovieJsonResponse = new JSONObject(movieJsonStr);

        JSONArray moviesArray = baseMovieJsonResponse.getJSONArray("results");

        // Define parsedMovieData as a String array with a length determined by the number of movies
        // in the moviesArray.
        parsedMovieData = new String[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get movie JSONObject at position i
            JSONObject currentMovie = moviesArray.getJSONObject(i);

            String movieTitle = currentMovie.getString("title");
            String movieSynopsis = currentMovie.getString("overview");
            String moviePosterPath = currentMovie.getString("poster_path");
            double movieVoteAverage = currentMovie.getDouble("vote_average");
            String movieVoteAverageString = String.valueOf(movieVoteAverage);
            String movieReleaseDate = currentMovie.getString("release_date");
            String movieId = currentMovie.getString("id");

            parsedMovieData[i] = MOVIE_POSTER_PREFIX + moviePosterPath;

        }

        return parsedMovieData;
    }

    // Inflate an image from a String url
    public static Drawable LoadImageFromWebOperations(String stringUrl){
        try {
            InputStream is = (InputStream) new URL(stringUrl).getContent();
            Drawable d = Drawable.createFromStream(is, stringUrl);
            return d;
        } catch (Exception e){
            return null;
        }
    }
}
