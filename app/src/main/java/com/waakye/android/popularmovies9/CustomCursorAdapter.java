package com.waakye.android.popularmovies9;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.waakye.android.popularmovies9.data.MovieListingContract;
import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;


/**
 * Created by lesterlie on 1/8/18.
 */

public class CustomCursorAdapter extends
        RecyclerView.Adapter<CustomCursorAdapter.FavoriteViewHolder>{

    public static final String LOG_TAG = CustomCursorAdapter.class.getSimpleName();

    // ImageView variable that refers to the ImageView on activity_main.xml
    public ImageView listItemMovieImageView;


    // Class variables for the Cursor that holds the favorite movie data and the Contexxt
    private Cursor mCursor;
    private Context mContext;

    /*
* An on-click handler that we've defined to make it easy for an Activity to interface with
* our RecyclerView
*/
    final private CustomCursorAdapter.ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    // Inner class for creating ViewHolders
    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Class variables for the favorite movies and TextViews
        ImageView favoriteMovieImageView;

        /**
         * Constructor for the FavoriteViewHolders
         *
         * @param itemView  The view inflated in onCreateViewHolder
         */
        public FavoriteViewHolder(View itemView){
            super(itemView);

            favoriteMovieImageView = (ImageView) itemView.findViewById(R.id.image_view_item_movie);

            // Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            itemView.setOnClickListener(this);

        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param view The View that was clicked
         */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }
    }


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context
     *
     * @param context  the current Context
     */
    public CustomCursorAdapter(Context context, ListItemClickListener listener){
        this.mContext = context;
        mOnClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView
     *
     * @return  A new FavoriteViewHolder that holds the view for each favorite movie
     */
    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        // Inflate the movies_image_view_item.xml to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.movies_image_view_item,
                parent, false);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomCursorAdapter.FavoriteViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(MovieListingEntry._ID);
        int titleIndex = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_TITLE);
        int synopsisIndex = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_SYNOPSIS);
        int posterPathIndex = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_POSTER_PATH);
        int voteAverageIndex = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE);
        int releaseDateIndex = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_VOTE_AVERAGE);
        int movieIdIndex = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_ID);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        String individualMoviePosterUrl = MovieListingContract.MOVIE_POSTER_PREFIX
                + mCursor.getString(posterPathIndex);

        Log.i(LOG_TAG, "moviePosterUrl: " + individualMoviePosterUrl);

        // Uses the Picasso library and the movie Poster url to inflate the listItemMovieImageView
        Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(individualMoviePosterUrl).into(listItemMovieImageView);


    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and re-query occurs, this function swaps the old Cursor with a newly
     * updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor
        if (mCursor == c){
            return null; // because nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        // check if this is a valid cursor, then update the cursor
        if (c != null){
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
