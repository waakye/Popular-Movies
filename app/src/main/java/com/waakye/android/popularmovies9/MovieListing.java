package com.waakye.android.popularmovies9;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lesterlie on 12/26/17.
 */



public class MovieListing implements Parcelable {

    public String movieTitle;
    public String movieSynopsis;
    public String moviePosterPath;
    public String movieVoteRating;
    public String movieReleaseDate;
    public String movieId;

    /**
     * Stores the MovieListing data to the Parcel object
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(movieSynopsis);
        dest.writeString(moviePosterPath);
        dest.writeString(movieVoteRating);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieId);
    }

    /**
     * Auto-generated method stub
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Constructor that initializes MovieListing object
     */
    public MovieListing(String sMovieTitle, String sMovieSynopsis, String sMoviePosterPath,
                        String sMovieVoteRating, String sMovieReleaseDate, String sMovieId){
        this.movieTitle = sMovieTitle;
        this.movieSynopsis = sMovieSynopsis;
        this.moviePosterPath = sMoviePosterPath;
        this.movieVoteRating = sMovieVoteRating;
        this.movieReleaseDate = sMovieVoteRating;
        this.movieReleaseDate = sMovieReleaseDate;
        this.movieId = sMovieId;
    }

    /**
     * This constructor retrieves data from the Parcel object
     * and is invoked by the method createFromParcel (Parcel source) of the object CREATOR
     * @param in
     */
    private MovieListing(Parcel in) {
        movieTitle = in.readString();
        movieSynopsis = in.readString();
        moviePosterPath = in.readString();
        movieVoteRating = in.readString();
        movieReleaseDate = in.readString();
        movieId = in.readString();
    }

    public static final Creator<MovieListing> CREATOR = new Creator<MovieListing>() {
        @Override
        public MovieListing createFromParcel(Parcel source) {
            return new MovieListing(source);
        }

        @Override
        public MovieListing[] newArray(int size) {
            return new MovieListing[size];
        }
    };
}
