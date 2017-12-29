package com.waakye.android.popularmovies9;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waakye.android.popularmovies9.utilities.MovieDbJsonUtils;
import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lesterlie on 12/29/17.
 */

public class SearchMoviesActivity extends AppCompatActivity
        implements MovieListingAdapter.ListItemClickListener{

    private static final String LOG_TAG = SearchMoviesActivity.class.getSimpleName();

    // TextView to display the error message
    private TextView mErrorMessageDisplay;

    // Loading Indicator
    private ProgressBar mLoadingIndicator;

    private TextView mSearchResultsTextView;

    private MovieListingAdapter mAdapter;

    private RecyclerView mSearchedMoviesList;

    private List<MovieListing> jsonMovieTitleDataList = new ArrayList<>();

    protected List<MovieListing> listJsonMovieData;

    public String search_terms = "";

    public static String theMovieDbQueryUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set Recyclerview variable to the View with id recycler_view_searched_movies
        mSearchedMoviesList = (RecyclerView)findViewById(R.id.recycler_view_searched_movies);

        // Define the RecyclerView with a fixed size
        mSearchedMoviesList.setHasFixedSize(true);

        // Define the layout being used as a GridLayout with 3 columns
        GridLayoutManager layoutManager = new GridLayoutManager(SearchMoviesActivity.this, 2);

        // Set the RecyclerView to be attached to the Gridlayout
        mSearchedMoviesList.setLayoutManager(layoutManager);

        // Attach the RecyclerView to the MovieListingAdapter
        mSearchedMoviesList.setAdapter(mAdapter);

        /**
         * The MovieListingAdapter is responsible for displaying each item in the list.  The first
         * "this" refers to a list of MovieListing objects.  The second "this" refers to the
         * ListItemClickListener of the MovieListingAdapter constructor
         */
        mAdapter = new MovieListingAdapter(this, listJsonMovieData, this);

        mErrorMessageDisplay = (TextView) findViewById(R.id.text_view_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.progress_bar_loading_indicator);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get input from the EditText
                EditText searchTerms = (EditText) findViewById(R.id.edit_search_terms);
                search_terms = searchTerms.getText().toString();

                theMovieDbQueryUrl = urlQueryString(search_terms);
                makeUrlMovieTitleQueryString(theMovieDbQueryUrl);

            }
        });
    }

    private void makeUrlMovieTitleQueryString(String movieTitle){
        Log.i(LOG_TAG, "makeUrlMovieTitleQueryString() method called...");

        new MovieTitleQueryTask().execute(movieTitle);
    }

    /**
     * This method will make the View for the JSON data visible and hide the error message
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is current visible or invisible
     */
    private void showJsonDataView(){
        Log.i(LOG_TAG, "showJsonDataView() method called...");
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON is visible
        mSearchedMoviesList.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON View.
     *
     * Since it is okay to redundantly set  the visiblity of a View, we don't need to check whether
     * each view is currently visible or invisible
     */
    private void showErrorMessage(){
        Log.i(LOG_TAG, "showErrorMessage() method called...");
        // First, hide the currently visible data
        mSearchedMoviesList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // https://api.themoviedb.org/3/search/movie?api_key={api_key}&query=Jack+Reacher
    public String urlQueryString(String search_terms) {

        Log.i(LOG_TAG, "urlQueryString() method called...");
        search_terms = search_terms.trim().replace(" ", "+");

        StringBuilder sb = new StringBuilder(NetworkUtils.MOVIE_DB_TITLE_SEARCH_BASE_URL);
        sb.append("?api_key=");
        sb.append(NetworkUtils.API_KEY);
        sb.append("&query=");
        sb.append(search_terms);
        String builtString = sb.toString();
        Log.i(LOG_TAG, "built string: " + builtString);
        return builtString;

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.i(LOG_TAG, "onListItem() method called...");

        MovieListing individualMovie = jsonMovieTitleDataList.get(clickedItemIndex);
        String individualTitle = individualMovie.getMovieTitle();
        String individualSynopsis = individualMovie.getMovieSynopsis();
        String individualVoteAverage = individualMovie.getMovieVoteAverage();
        String individualReleaseDate = individualMovie.getMovieReleaseDate();
        String individualPosterPath = individualMovie.getMoviePosterPath();
        String individualMovieId = individualMovie.getMovieId();

        MovieListing mlisting = new MovieListing(individualTitle, individualSynopsis,
                individualPosterPath, individualVoteAverage, individualReleaseDate, individualMovieId );

        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("movie", mlisting);
        startActivity(intent);

    }


    public class MovieTitleQueryTask extends AsyncTask<String, Void, List<MovieListing>> {

        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieListing> doInBackground(String... params) {
            Log.i(LOG_TAG, "MovieTitleQueryTask doInBackground() method called...");

            // If there's no search terms, then there's nothing to look up
            if(params.length == 0){
                return null;
            }

            URL movieTitlerSearchUrl = NetworkUtils.createTitleSearchUrl(theMovieDbQueryUrl);

            try {
                String jsonMovieTitleResponse = NetworkUtils.getResponseFromHttpUrl(movieTitlerSearchUrl);

                jsonMovieTitleDataList = MovieDbJsonUtils.extractItemFromJson(jsonMovieTitleResponse);

                // Returns a List of MovieListing objects
                return jsonMovieTitleDataList;
            } catch (IOException e){
                e.printStackTrace();
                return null;
            } catch (JSONException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieListing> movieTitleSearchResults){
            Log.i(LOG_TAG, "MovieTitleQueryTask onPostExecute() method called...");
            // As soon as the loading is complete, hide the loading indicator
            if(movieTitleSearchResults != null && !movieTitleSearchResults.equals("")){
                // Call showJsonDataView if we have valid, non-null results
                showJsonDataView();

                mAdapter.setMovieData(movieTitleSearchResults);

                mSearchedMoviesList.setAdapter(mAdapter);

            }  else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }
}
