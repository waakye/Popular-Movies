package com.waakye.android.popularmovies9;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mMovieTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieTextView = (TextView)findViewById(R.id.text_view_movie_data);

        String[] dummyMoviedata = {
                "Jack Reacher - 7.0 - Dec 26, 2012",
                "Contact - 7.4 - September 26, 1997",
                "Limitless - 7.4 - March 23, 2011",
                "The Intern - 7.1 - October 2, 2015",
                "Casablanca - 8.5 - January 23, 1943",
                "Spy - 7.0 - June 5, 2015",
                "Gone Girl - 8.1 - October 2, 2014",
                "How to Lose a Guy in 10 Days - 6.4 - April 18, 2003",
                "Source Code - 7.5 - April 1, 2011",
                "13 Going on 30 - 6.1 - August 4, 2004",
                "Planet of the Apes - 8.0 - April 21, 1968",


        };

        for(String dummmySingleMovie : dummyMoviedata){
            mMovieTextView.append(dummmySingleMovie + "\n\n\n");
        }
    }
}
