package com.waakye.android.popularmovies9;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lesterlie on 12/26/17.
 */

public class MovieListing implements Parcelable {

    private String mMovieTitle;
    private String mMovieSynopsis;
    private String mMoviePosterPath;
    private String mMovieVoteAverage;
    private String mMovieReleaseDate;
    private String mMovieId;

    /**
     * Constructor that initializes MovieListing object
     */
    public MovieListing(String sMovieTitle, String sMovieSynopsis, String sMoviePosterPath,
                        String sMovieVoteRating, String sMovieReleaseDate, String sMovieId){
        this.mMovieTitle = sMovieTitle;
        this.mMovieSynopsis = sMovieSynopsis;
        this.mMoviePosterPath = sMoviePosterPath;
        this.mMovieVoteAverage = sMovieVoteRating;
        this.mMovieReleaseDate = sMovieReleaseDate;
        this.mMovieId = sMovieId;

    }

    /**
     * Auto-generated method stub
     */
    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * This constructor retrieves data from the Parcel object
     * and is invoked by the method createFromParcel (Parcel source) of the object CREATOR
     * @param in
     */
    private MovieListing(Parcel in) {
        mMovieTitle = in.readString();
        mMovieSynopsis = in.readString();
        mMoviePosterPath = in.readString();
        mMovieVoteAverage = in.readString();
        mMovieReleaseDate = in.readString();
        mMovieId = in.readString();
    }

    /**
     * Stores the MovieListing data to the Parcel object
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieTitle);
        dest.writeString(mMovieSynopsis);
        dest.writeString(mMoviePosterPath);
        dest.writeString(mMovieVoteAverage);
        dest.writeString(mMovieReleaseDate);
        dest.writeString(mMovieId);
    }

    public static final Creator<MovieListing> CREATOR = new Creator<MovieListing>() {
        @Override
        public MovieListing createFromParcel(Parcel in) {
            return new MovieListing(in);
        }

        @Override
        public MovieListing[] newArray(int size) {
            return new MovieListing[size];
        }
    };

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMovieSynopsis() {
        return mMovieSynopsis;
    }

    public String getMoviePosterPath() {
        return mMoviePosterPath;
    }

    public String getMovieVoteAverage() {
        return mMovieVoteAverage;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    public String getMovieId(){
        return mMovieId;
    }
}
