package com.waakye.android.popularmovies9;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.waakye.android.popularmovies9.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;

    private String moviePopularityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.edit_text_movies);

        mUrlDisplayTextView = (TextView) findViewById(R.id.text_view_display);

        mSearchResultsTextView = (TextView) findViewById(R.id.text_view_moviedb_search_results_json);

    }

    private void makeMovieDbPopularityQuery(String popularityType){

        URL movieDbDiscoverUrl = NetworkUtils.buildByPopularityTypeUrl(popularityType);
        mUrlDisplayTextView.setText(movieDbDiscoverUrl.toString());
        new MovieDbQueryTask().execute(movieDbDiscoverUrl);
    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieDbSearchResults = null;
            try {
                movieDbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e){
                e.printStackTrace();
            }
            return movieDbSearchResults;
        }

        @Override
        protected void onPostExecute(String movieDbSearchResults){
            if(movieDbSearchResults != null && !movieDbSearchResults.equals("")){
                mSearchResultsTextView.setText(movieDbSearchResults);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemThatWasClicked = item.getItemId();
        if(itemThatWasClicked == R.id.action_popular_movies) {
            Context context = MainActivity.this;
            moviePopularityType = "popularity.desc"; // Most popular movies
            String textToShow = "Popular Movies clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            makeMovieDbPopularityQuery(moviePopularityType);
            return true;
        }

        if(itemThatWasClicked == R.id.action_highly_rated_movies){
            Context context = MainActivity.this;
            moviePopularityType = "vote_average.desc"; // Most highly rated movies
            String textToShow = "Highly Rated Movies clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            makeMovieDbPopularityQuery(moviePopularityType);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
