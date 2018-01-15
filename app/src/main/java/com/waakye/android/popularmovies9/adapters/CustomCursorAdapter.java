package com.waakye.android.popularmovies9.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.waakye.android.popularmovies9.R;
import com.waakye.android.popularmovies9.data.MovieListingContract;
import com.waakye.android.popularmovies9.data.MovieListingContract.MovieListingEntry;

/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the poster path URL and
 * the inflated image of the poster in a RecyclerView to efficiently display data
 *
 * Created by lesterlie on 1/8/18.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.FavoriteViewHolder>{

    public static final String LOG_TAG = CustomCursorAdapter.class.getSimpleName();

    // ImageView variable that refers to the ImageView on movies_image_view_item.xml
    public ImageView listItemMovieImageView;

    // Class variables for the Cursor that holds movie data and the Context
    private Cursor mCursor;
    private Context mContext;

    /*
    * An on-click handler that we've defined to make it easy for an Activity to interface with
    * our RecyclerView
    */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    // Inner class for creating ViewHolders
    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        /**
         * Constructor for our ViewHolder.  Within this constructor, we get a reference to the
         * ImageView and set an onClickListener to listen for clicks.  Those will be handled by the
         * onClick method below
         * @param itemView  The View that you inflated in
         *                  {@link CustomCursorAdapter#onCreateViewHolder(ViewGroup, int)}
         */

        public FavoriteViewHolder(View itemView){
            super(itemView);

            listItemMovieImageView = (ImageView)itemView.findViewById(R.id.image_view_item_movie);

            // Call setOnClickListener on View passed into the constructor (use 'this' as the OnClickListener)
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
     * @param context the current Context
     */
    public CustomCursorAdapter(Context context, Cursor dataSet, ListItemClickListener listener){

        this.mContext = context;
        mCursor = dataSet;
        mOnClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView
     *
     * @return A new FavoriteViewHolder that holds teh view for each favorite movie
     */
    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup viewGroup,  int viewType){
        Log.i(LOG_TAG, "onCreateViewHolder() method called...");

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // FavoriteViewHolder created and connected to the movies_image_view_item.xml layout
        int layoutForListItem = R.layout.movies_image_view_item;

        boolean shouldAttachToParentImmediately = false;

        // Create a new view
        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, final int position) {
        Log.i(LOG_TAG, "onBindViewHolder() method called with #" + position);

        // Indices for the _id, poster path columns
        int idIndex = mCursor.getColumnIndex(MovieListingEntry._ID);
        int posterPath = mCursor.getColumnIndex(MovieListingEntry.COLUMN_MOVIE_POSTER_PATH);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the desired data
        final int id = mCursor.getInt(idIndex);
        String poster_path = mCursor.getString(posterPath);
        String individual_poster_url = MovieListingContract.MOVIE_POSTER_PREFIX
                + poster_path;

        // Set values
        Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(individual_poster_url).into(listItemMovieImageView);

    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this functions swaps the old Cursor with a newly
     * updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c){
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c){
            return null; // because nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        // check if this is a valid cursor, then update the cursor
        if (c != null){
            this.notifyDataSetChanged();
        }
        return temp;
    }

}