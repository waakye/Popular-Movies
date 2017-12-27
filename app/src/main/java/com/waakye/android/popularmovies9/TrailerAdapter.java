package com.waakye.android.popularmovies9;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lesterlie on 12/27/17.
 */

public class TrailerAdapter {

    /**
     * Cache of the children views for a list item
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        // Will display the position in the list
        TextView listItemMovieView;

        /**
         * Constructor for our ViewHolder.  Within this constructor, we get a reference to the
         * TextViews and set an onClickListener to listen for clicks.  Those will be handled by the
         * onClick method below
         * @param itemView  The View that you inflated in
         *                  {@link TrailerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public TrailerViewHolder(View itemView){
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
