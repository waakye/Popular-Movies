package com.waakye.android.popularmovies9.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.waakye.android.popularmovies9.MovieListing;
import com.waakye.android.popularmovies9.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lesterlie on 12/26/17.
 */

public class MovieDbJsonUtils {

    public static String LOG_TAG = MovieDbJsonUtils.class.getSimpleName();

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

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

    /**
     * This method parses JSON from a web response and returns a List of Trailers describing the
     * trailers' charactertistics
     *
     * @param trailerJsonStr    JSON response from the server
     *
     * @return                  List of Trailers describing the trailer data
     * @throws JSONException    If JSON data cannot be properly parsed
     */
    public static List<Trailer> extractTrailerFromJson(String trailerJsonStr) throws JSONException {

        Log.i(LOG_TAG, "extractTrailerFromJson() method is called...");

        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(trailerJsonStr)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding movieListings to
        ArrayList<Trailer> listOfTrailers = new ArrayList<>();

        JSONObject baseTrailerJsonResponse = new JSONObject(trailerJsonStr);

        JSONArray trailersArray = baseTrailerJsonResponse.getJSONArray("results");

        for (int i = 0; i < trailersArray.length(); i++) {
            // Get trailer JSONObject at position i
            JSONObject currentTrailer = trailersArray.getJSONObject(i);

            String trailerName = currentTrailer.getString("name");
            String trailerType = currentTrailer.getString("type");
            String trailerSite = currentTrailer.getString("site");
            String trailerKey = currentTrailer.getString("key");

            Trailer trailer = new Trailer(trailerName, trailerType, trailerSite, trailerKey);

            listOfTrailers.add(trailer);

        }
        return listOfTrailers;
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

    /**
     * This method parses JSON from a web response and returns a List of MovieListing objects
     * describing the movie possibilities.
     *
     * @param movieJsonStr  JSON response from the server
     *
     * @return A List of MovieListing objects describing the movie data
     *
     * @throws org.json.JSONException  If JSON data cannot be properly parsed.
     */
    public static List<MovieListing> extractItemFromJson(String movieJsonStr)
            throws JSONException {
        Log.i(LOG_TAG, "extractItemFromJson() method called...");

        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(movieJsonStr)){
            return null;
        }

        // Create an empty ArrayList that we can start adding movieListings to
        ArrayList<MovieListing> listOfMovieListings = new ArrayList<>();

        JSONObject baseMovieJsonResponse = new JSONObject(movieJsonStr);

        JSONArray moviesArray = baseMovieJsonResponse.getJSONArray("results");

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

            MovieListing movieListing = new MovieListing(movieTitle, movieSynopsis,
                    moviePosterPath, movieVoteAverageString, movieReleaseDate, movieId);

            listOfMovieListings.add(movieListing);

        }
        return listOfMovieListings;
    }
}
