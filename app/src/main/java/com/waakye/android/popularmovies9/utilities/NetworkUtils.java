package com.waakye.android.popularmovies9.utilities;

import android.net.Uri;
import android.util.Log;

import com.waakye.android.popularmovies9.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by lesterlie on 12/23/17.
 */

public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Base URLs for highly-rated or popular movies list
    private static final String MOVIE_DB_POPULAR_MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/popular";

    private static final String MOVIE_DB_TOP_RATED_MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";

    // Base URL for user reviews or trailers
    // user reviews: http://api.themoviedb.org/3/movie/83542/reviews?api_key=<>
    // trailers: http://api.themoviedb.org/3/movie/157336/videos?api_key=<>
    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie";

    // URL for movie title search
    // https://api.themoviedb.org/3/search/movie?api_key=<>&language=en-US&page=1&include_adult=false
    public static final String MOVIE_DB_TITLE_SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie";

    final static String API_KEY_PARAM = "api_key";
    public final static String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    final static String LANGUAGE_PARAM = "language";
    final static String LANGUAGE_ENGLISH = "en-US";

    final static String CERTIFICATION_COUNTRY_PARAM = "certification_country";
    final static String COUNTRY_US = "US";

    final static String ADULT_PARAM = "include_adult";
    final static String VIDEO_PARAM = "include_video";
    final static String FALSE = "false";

    final static String PAGE_PARAM = "page";
    final static String PAGE_1 = "1";

    final static String VOTE_COUNT_PARAM = "vote_count.gte";
    final static String MINIMUM_VOTE_COUNT_1000 = "1000";

    final static String REVIEWS = "reviews";
    final static String VIDEOS = "videos";

    // https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1

    public static URL createPopularityTypeUrl(int moviePopularityType){

        Uri builtUri = null;

        if(moviePopularityType == 1 ){      // Most popular movies
            builtUri = Uri.parse(MOVIE_DB_POPULAR_MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_ENGLISH)
                    .appendQueryParameter(CERTIFICATION_COUNTRY_PARAM, COUNTRY_US)
                    .appendQueryParameter(ADULT_PARAM, FALSE)
                    .appendQueryParameter(VIDEO_PARAM, FALSE)
                    .appendQueryParameter(VOTE_COUNT_PARAM, MINIMUM_VOTE_COUNT_1000)
                    .appendQueryParameter(PAGE_PARAM, PAGE_1)
                    .build();
        } else if (moviePopularityType == 2){   // Top rated movies
            builtUri = Uri.parse(MOVIE_DB_TOP_RATED_MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_ENGLISH)
                    .appendQueryParameter(CERTIFICATION_COUNTRY_PARAM, COUNTRY_US)
                    .appendQueryParameter(ADULT_PARAM, FALSE)
                    .appendQueryParameter(VIDEO_PARAM, FALSE)
                    .appendQueryParameter(VOTE_COUNT_PARAM, MINIMUM_VOTE_COUNT_1000)
                    .appendQueryParameter(PAGE_PARAM, PAGE_1)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    // Method to build URL for the user reviews of a particular movie using its movie ID
    // http://api.themoviedb.org/3/movie/83542/reviews?api_key=<>

    public static URL createUserReviewsUrl(String movieId){

        Uri builtUri = null;

        builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Built URI " + url);

        return url;

    }

    // Method to build trailer URL for a movie's trailers using its movie ID
    // trailers: http://api.themoviedb.org/3/movie/157336/videos?api_key=<>
    public static URL createMovieTrailerUrl(String movieId){

        Uri builtUri = null;

        builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    // Method to build title search URL of a movie using String information from EditText field
    // https://api.themoviedb.org/3/search/movie?api_key={api_key}&query=Jack+Reacher
    // https://api.themoviedb.org/3/search/movie
    public static URL createTitleSearchUrl(String movieTitleQuery) {

        Uri builtUri = null;

        builtUri = Uri.parse(movieTitleQuery);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response
     *
     * @param url   The URLto fetch the HTTP response from.
     * @return      The contents of the HTTP response
     * @throws IOException  Related to the network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection =  (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
