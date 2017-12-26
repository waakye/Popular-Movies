package com.waakye.android.popularmovies9;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lesterlie on 12/27/17.
 */


public class MovieListingAdapter extends RecyclerView.Adapter<MovieListingAdapter.MovieListingViewHolder>{

    public static final String LOG_TAG = MovieListingAdapter.class.getSimpleName();

    // ImageView variable that refers to the ImageView on activity_main.xml
    public ImageView listItemMovieImageView;

    // Movie Poster Prefix that completes the url
    public static String MOVIE_POSTER_PREFIX = "https://image.tmdb.org/t/p/w500";

    // Define a List<MovieListing> that will contain the movie listing data
    private List<MovieListing> movieListingsList;

    private Context context;

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

    public class MovieListingViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /**
         * Constructor for our ViewHolder.  Within this constructor, we get a reference to the
         * ImageView and set an onClickListener to listen for clicks.  Those will be handled by the
         * onClick method below
         * @param itemView  The View that you inflated in
         *                  {@link MovieListingAdapter#onCreateViewHolder(ViewGroup, int)}
         */

        public MovieListingViewHolder(View itemView){
            super(itemView);

            listItemMovieImageView = (ImageView)itemView.findViewById(R.id.image_view_item_movie);

            // Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            itemView.setOnClickListener(this);

        }

        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    /**
     * Constructor for MovieListingAdapter that accepts a number of MovieListing items to display and the
     * specification for the ListItemClickListener
     *
     * @param myDataSet    String[] containing the data to populate views to be used by RecyclerView
     */
    public MovieListingAdapter(Context context, List<MovieListing> myDataSet, ListItemClickListener listener){
        this.context = context;
        movieListingsList = myDataSet;
        mOnClickListener = listener;

    }

    /**
     * This gets called when each new ViewHolder is created.  This happens when the RecyclerView is
     * laid out. Once enough ViewHolders are created to fill the screen, then this method doesn't
     * need to be called anymore.
     * @param viewGroup     The ViewGroup that these ViewHolders are contained within.
     * @param viewType      If your RecyclerView has more than one type of item (which ours doesn't)
     *                      you can use this viewType integer to provide a different layout.  See
     *                      {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                      for more details.
     * @return              A new MovieListingViewHolder that holds the View for each list item
     */
    @Override
    public MovieListingAdapter.MovieListingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i(LOG_TAG, "onCreateViewHolder() method called...");

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // MovieViewHolder created and connected to the movies_image_view_item.xml layout
        int layoutForListItem = R.layout.movies_image_view_item;

        boolean shouldAttachToParentImmediately = false;

        // Create a new view
        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieListingViewHolder viewHolder = new MovieListingViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
     * In this method, we update the contents of the ViewHolder to display the correct indices in
     * the list for this particular position, using the 'position' argument that is conveniently
     * passed into us.
     *
     * @param viewHolder    The ViewHolder which should be updated to represent the contents of the
     *                      item at the given position in the data set.
     * @param position      The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieListingViewHolder viewHolder, final int position) {
        Log.i(LOG_TAG, "onBindViewHolder() method called with #" + position);

        String individualMoviePosterUrl = MOVIE_POSTER_PREFIX + movieListingsList.indexOf(position);
        Log.i(LOG_TAG, "moviePosterUrl: " + individualMoviePosterUrl);

        // Uses the Picasso library and the movie Poster url to inflate the listItemMovieImageView
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context).load(individualMoviePosterUrl).into(listItemMovieImageView);

    }

    /**
     * This method simply returns the number of items to display.  It is used behind the scenes to
     * help layout our Views and for animations.
     *
     * @return      The number of items available in our movie list
     */
    @Override
    public int getItemCount() {
        return movieListingsList.size();
    }

    public void setMovieData(List<MovieListing> movieData){
        Log.i(LOG_TAG, "setMovieData() method is called...");
        movieListingsList = movieData;
        notifyDataSetChanged();
    }
}
