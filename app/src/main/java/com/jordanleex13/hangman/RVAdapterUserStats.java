package com.jordanleex13.hangman;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jordanleex13.hangman.Models.CategoryData;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Adapter for the RecyclerView in FragmentUserStats
 */
public class RVAdapterUserStats extends RecyclerView.Adapter<RVAdapterUserStats.ViewHolder> {

    public static final String TAG = RVAdapterUserStats.class.getSimpleName();

    // Arraylist of a user's category data
    private ArrayList<CategoryData> mCurrUserData;

    // Constructor
    public RVAdapterUserStats(ArrayList<CategoryData> list) {
        mCurrUserData = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate view for a single row (viewholder)
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_viewholder_userstats, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder, int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        CategoryData curr = mCurrUserData.get(position);

        // Fill the UI with user data
        String category = curr.getCategoryName();
        category = category.substring(0, 1).toUpperCase() + category.substring(1);  // Simply makes first letter capital
        int wins = curr.getWins();
        int losses = curr.getLosses();

        DecimalFormat df = new DecimalFormat("##.#");   // max 2 decimal places
        String winRate = df.format(curr.getWinRate()*100) + "%";


        viewholder.mCategory.setText(category);
        viewholder.mWins.setText("Wins : " + wins);
        viewholder.mLosses.setText("Losses : " + losses);
        viewholder.mWinRate.setText("Win Rate : " + winRate);
    }

    @Override
    public int getItemCount() {
        return mCurrUserData.size();
    }

    /**
     * Method to update the arraylist. Note that notifyDataSetChanged() must be called to actually update
     * the recycler view
     * @param newList       the new arraylist for a different user
     */
    public void updateArrayList(ArrayList<CategoryData> newList) {
        mCurrUserData = newList;
    }


    /**
     * This class is a viewholder for a CategoryData
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // UI elements
        public final View mView;
        public final TextView mCategory;
        public final TextView mWins;
        public final TextView mLosses;
        public final TextView mWinRate;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            // set up UI elements
            mCategory = (TextView) view.findViewById(R.id.viewholder_category);
            mWins = (TextView) view.findViewById(R.id.viewholder_wins);
            mLosses = (TextView) view.findViewById(R.id.viewholder_losses);
            mWinRate = (TextView) view.findViewById(R.id.viewholder_winrate);

        }

    }
}
