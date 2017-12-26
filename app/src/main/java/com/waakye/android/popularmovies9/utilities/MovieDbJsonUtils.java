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

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

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
    // Can be used for the popularity type search
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

    public static String[] getMovieTitleStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {
        Log.i(LOG_TAG, "getMovieTitleStringsFromJson() method called...");

                /* String array to hold each movie's data */
        String[] parsedMovieTitleData = null;

        JSONObject baseMovieTitleJsonResponse = new JSONObject(movieJsonStr);

        JSONArray movieTitlesArray = baseMovieTitleJsonResponse.getJSONArray("results");

        parsedMovieTitleData = new String[movieTitlesArray.length()];

        for (int i = 0; i < movieTitlesArray.length(); i++){
            // Get movie JSONObject at position i
            JSONObject currentMovieTitle = movieTitlesArray.getJSONObject(i);

            String movieTitle = currentMovieTitle.getString("title");
            String movieSynopsis = currentMovieTitle.getString("overview");
            String moviePosterPath = currentMovieTitle.getString("poster_path");
            String movieReleaseDate = currentMovieTitle.getString("release_date");
            String movieId = currentMovieTitle.getString("id");

            parsedMovieTitleData[i] = MOVIE_POSTER_PREFIX + moviePosterPath;
        }

        return parsedMovieTitleData;
    }


    // For this query: http://api.themoviedb.org/3/movie/157336/videos?api_key=b2433ced24ee89f33371c184240eca2a
    public static String[] getTrailerStringsFromJson(Context context, String trailerJsonStr)
            throws JSONException{

        Log.i(LOG_TAG, "getTrailerStringsFromJson() method called...");

        /* String array to hold each trailers's data */
        String[] parseTrailerData = null;

        JSONObject baseTrailerJsonResponse = new JSONObject(trailerJsonStr);

        JSONArray trailersArray = baseTrailerJsonResponse.getJSONArray("results");

        // Define parsedTrailerData as a String array with a length determined by the number of
        // trailers in the trailersArray
        parseTrailerData = new String[trailersArray.length()];

        for(int i = 0; i < trailersArray.length(); i++){
            // Get trailer JSONObject at position i
            JSONObject currentTrailer = trailersArray.getJSONObject(i);

            String trailerName = currentTrailer.getString("name");
            String trailerType = currentTrailer.getString("type");
            String trailerSite = currentTrailer.getString("site");
            String trailerKey = currentTrailer.getString("key");

            parseTrailerData[i] = YOUTUBE_BASE_URL + trailerKey;
        }

        return parseTrailerData;
    }

    // For this query: http://api.themoviedb.org/3/movie/83542/reviews?api_key=b2433ced24ee89f33371c184240eca2a
    public static String[] getUserReviewStringsFromJson(Context context, String reviewsJsonStr)
            throws JSONException{

        Log.i(LOG_TAG, "getUserReviewStringsFromJson() method called...");

        /* String array to hold each reviews's data */
        String[] parseReviewData = null;

        JSONObject baseReviewJsonResponse = new JSONObject(reviewsJsonStr);

        JSONArray reviewsArray = baseReviewJsonResponse.getJSONArray("results");

        // Define parsedReviewDat as a String array with a length determined by the number of
        // reviews in the reviewsArray
        parseReviewData = new String[reviewsArray.length()];

        for(int i = 0; i < reviewsArray.length(); i++){
            // Get a review JSONObject at position i
            JSONObject currentReview = reviewsArray.getJSONObject(i);

            String reviewAuthor = currentReview.getString("author");
            String reviewContent = currentReview.getString("content");

            parseReviewData[i] = reviewContent + "\n\n" + reviewAuthor;
        }

        return parseReviewData;

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