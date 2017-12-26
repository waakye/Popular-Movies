package com.waakye.android.popularmovies9;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lesterlie on 12/26/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private int mNumberOfMovieItems;

    /**
     * Constructor for MovieAdapter that accepts a number of Movie items to display and the
     * specification for the ListItemClickListener
     *
     * @param numberOfMovieItems    Number of movie items to display in list
     */
    public MovieAdapter(int numberOfMovieItems) { mNumberOfMovieItems = numberOfMovieItems;}

    /**
     * This gets called when each new ViewHolder is created.  This happens when the RecyclerView is
     * laid out. Once enough ViewHolders are created to fill the screen, then this method doesn't
     * need to be called anymore.
     * @param viewGroup     The ViewGroup that these ViewHolders are contained within.
     * @param viewType      If your RecyclerView has more than one type of item (which ours doesn't)
     *                      you can use this viewType integer to provide a different layout.  See
     *                      {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                      for more details.
     * @return              A new MovieViewHolder that holds the View for each list item
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
     * In this method, we update the contents of the ViewHolder to display the correct indices in
     * the list for this particular position, using the 'position' argument that is conveniently
     * passed into us.
     *
     * @param holder    The ViewHolder which should be updated to represent the contents of the
     *                  item at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(LOG_TAG, "#" + position);
        holder.bind(position);

    }

    /**
     * This method simply returns the number of items to display.  It is used behind the scenes to
     * help layout our Views and for animations.
     *
     * @return      The number of items available in our movie list
     */
    @Override
    public int getItemCount() {
        return mNumberOfMovieItems;
    }

    /**
     * Cache of the children views for a list item
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {

        // Will display the position in the list
        TextView listItemMovieView;

        /**
         * Constructor for our ViewHolder.  Within this constructor, we get a reference to the
         * TextViews and set an onClickListener to listen for clicks.  Those will be handled by the
         * onClick method below
         * @param itemView  The View that you inflated in
         *                  {@link MovieAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public MovieViewHolder(View itemView){
            super(itemView);

            listItemMovieView = (TextView) itemView.findViewById(R.id.text_view_item_movie);
        }

        /**
         * Create a convenience method that takes an integer as input and use that integer to
         * display the appropriate text within a list item.
         *
         * @param listIndex Position of the item in the list
         */
        public void bind(int listIndex) {
            listItemMovieView.setText(String.valueOf(listIndex));
        }
    }
}
