package com.waakye.android.popularmovies9;

/**
 * Created by lesterlie on 12/27/17.
 */

public class Trailer {

    private String mTrailerName;
    private String mTrailerType;
    private String mTrailerSite;
    private String mTrailerKey;

    /**
     * Constructor that initializes Trailer object
     */
    public Trailer(String sTrailerName, String sTrailerType, String sTrailerSite,
                   String sTrailerKey) {
        this.mTrailerName = sTrailerName;
        this.mTrailerType = sTrailerType;
        this.mTrailerSite = sTrailerSite;
        this.mTrailerKey = sTrailerKey;
    }

    public String getTrailerName() {
        return mTrailerName;
    }

    public String getTrailerType() {
        return mTrailerType;
    }

    public String getTrailerSite() {
        return mTrailerSite;
    }

    public String getTrailerKey() {
        return mTrailerKey;
    }
}