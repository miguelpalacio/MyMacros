package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miguelpalacio.mymacros.database.DatabaseAdapter;

/**
 * Add Meal for Planner page.
 * This page displays the meals saved (using the Meals Fragment layout), so
 * that the user can choose the meal she wants to add to her daily plan.
 */
public class AddMealFragment extends Fragment implements SubheadersListAdapter.ViewHolder.ClickListener {

    RecyclerView mealListView;
    RecyclerView.LayoutManager mealListLayoutManager;
    SubheadersListAdapter mealListAdapter;

    DatabaseAdapter databaseAdapter;
    String[][] mealInfo;

    TextView emptyPageMessage;

    OnMealSet onMealSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_add_meal, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnMealSet interface.
        try {
            onMealSet = (OnMealSet) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealSet interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get the names and summaries of foods inserted by the user.
        mealInfo = databaseAdapter.getMealsList();

        // Food List RecyclerView.
        mealListView = (RecyclerView) getActivity().findViewById(R.id.add_meal_list);

        // Set the adapter.
        mealListAdapter = new SubheadersListAdapter(mealInfo[1], mealInfo[2], mealInfo[3], this);
        mealListView.setAdapter(mealListAdapter);

        // Set the layout manager for the RecyclerView.
        mealListLayoutManager = new LinearLayoutManager(getActivity());
        mealListView.setLayoutManager(mealListLayoutManager);

        // If there are no meals, show the default message for empty page.
        if (mealInfo[0].length == 0) {
            emptyPageMessage = (TextView) getActivity().findViewById(R.id.add_meal_empty_page);
            emptyPageMessage.setVisibility(View.VISIBLE);
            mealListView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onListItemClick(final int position) {
        onMealSet.setMealOnPlanner(Long.parseLong(mealInfo[0][position]));
    }

    public interface OnMealSet {
        void setMealOnPlanner(long mealId);
    }
}
