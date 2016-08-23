package com.jordanleex13.hangman;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jordanleex13.hangman.Models.CategoryData;

/**
 * Adapter used to display categories
 */
public class RVAdapterCategories extends RecyclerView.Adapter<RVAdapterCategories.ViewHolder> {


    private ViewHolder.OnCategoryClickedListener mListener;

    // Separate method to set the listener
    public void setOnCategoryClickedListener(ViewHolder.OnCategoryClickedListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate view for a single row (viewholder)
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_viewholder_category, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder, int position) {

        // Fill the UI with category names
        String category = CategoryData.CATEGORY_LIST[position];
        category = category.substring(0, 1).toUpperCase() + category.substring(1);  // Simply makes first letter capital

        viewholder.mCategory.setText(category);
    }


    @Override
    public int getItemCount() {
        return CategoryData.NUM_OF_CATEGORIES;
    }


    /**
     * This class is a viewholder for a CategoryData
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // UI elements
        public final View mView;
        public final TextView mCategory;
        private OnCategoryClickedListener mListener;


        public ViewHolder(View view, OnCategoryClickedListener listener) {
            super(view);
            mView = view;

            mListener = listener;

            // set up listener for this specific viewholder (row)
            mView.setOnClickListener(this);

            // set up UI elements
            mCategory = (TextView) view.findViewById(R.id.viewholder_category_categories);

        }

        // interface to communicate when viewholder is clicked
        public interface OnCategoryClickedListener {
            void onCategoryClicked(View itemView);
        }

        @Override
        public void onClick(View view) {
            mListener.onCategoryClicked(mView);
        }

    }
}
