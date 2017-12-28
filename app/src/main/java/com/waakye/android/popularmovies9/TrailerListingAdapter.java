package com.waakye.android.popularmovies9;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lesterlie on 12/28/17.
 */


public class TrailerListingAdapter
        extends RecyclerView.Adapter<TrailerListingAdapter.TrailerListingViewHolder> {

    public static final String LOG_TAG = TrailerListingAdapter.class.getSimpleName();

    private List<Trailer> trailerListingsList;

    private Context context;

    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;

    /**
     * The interface that receives onClick messages
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    /**
     * Cache of the children views for a list item
     */
    public class TrailerListingViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView listItemTrailerNameView;

        /**
         * Constructor for our ViewHolder.  Within this constructor, we get a reference to the
         * TextViews and set an onClickListener to listen for clicks.  Those will be handled by the
         * onClick method below
         *
         * @param itemView The View that you inflated in
         *                 {@link TrailerListingAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public TrailerListingViewHolder(View itemView) {
            super(itemView);

            listItemTrailerNameView = (TextView) itemView.findViewById(R.id.text_view_item_trailer_name);

            // Call setOnClickListener on the View passed into the constructor (use 'this' as
            // the OnClickListener)
            itemView.setOnClickListener(this);
        }

        public TextView getTextView() {
            return listItemTrailerNameView;
        }


        /**
         * Called whenever a user clicks
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v){
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    /**
     * Constructor for TrailerAdapter that accepts a number of Trailer items to display and the
     * specification for the ListItemClickListener
     *
     * @param myDataSet    String[] containing the data to populate views to be used by RecyclerView
     */
    public TrailerListingAdapter(Context context, List<Trailer> myDataSet, ListItemClickListener listener) {
        this.context = context;
        trailerListingsList = myDataSet;
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
     * @return              A new TrailerViewHolder that holds the View for each list item
     */
    @Override
    public TrailerListingAdapter.TrailerListingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i(LOG_TAG, "onCreateViewHolder() method called...");

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutForListItem = R.layout.trailer_list_item;
        boolean shouldAttachToParentImmediately = false;

        // Create a new view
        View view = inflater.inflate(layoutForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailerListingViewHolder viewHolder = new TrailerListingViewHolder(view);

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
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TrailerListingViewHolder viewHolder, final int position) {
        Log.d(LOG_TAG, "#" + position);

        String[] trailerNamesArray = extractTrailerNames(trailerListingsList);

        String individualTrailerName = trailerNamesArray[position];

        viewHolder.getTextView().setText(individualTrailerName);
    }

    /**
     * This method simply returns the number of items to display.  It is used behind the scenes to
     * help layout our Views and for animations.
     *
     * @return      The number of items available in our movie list
     */
    @Override
    public int getItemCount() {
        return trailerListingsList.size();
    }

    public void setTrailerData(List<Trailer> trailerData){
        Log.i(LOG_TAG, "setMovieData() method is called...");
        trailerListingsList = trailerData;
        notifyDataSetChanged();
    }

    public String[] extractTrailerNames(List<Trailer> trailerSearchResults){
        Log.i(LOG_TAG, "extractTrailerNames() method called...");

        String[] trailerNames = new String[trailerSearchResults.size()];
        int index = 0;
        for(Trailer trailer: trailerSearchResults){
            trailerNames[index] = (String)trailer.getTrailerName();
            index++;
        }
        return trailerNames;
    }
}
