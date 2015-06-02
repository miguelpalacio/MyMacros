package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Meals Editor Page.
 * Allows the creation and edition of meals.
 */
public class MealEditorFragment extends Fragment implements ItemListAdapter.ViewHolder.ClickListener {

    private static final String MEAL_FOODS_NAMES = "mealFoodsNames";
    private static final String MEAL_FOODS_QUANTITY = "mealFoodsQuantity";
    private static final String MEAL_FOODS_UNITS = "mealFoodsUnits";

    EditText mealNameEditText;

    RecyclerView foodsListView;
    RecyclerView.LayoutManager foodsListLayoutManager;
    ItemListAdapter foodsListAdapter;

    ArrayList<String> mealFoodsIds;
    ArrayList<String> mealFoodsNames;
    ArrayList<String> mealFoodsQuantity;
    ArrayList<String> mealFoodsUnits;

    DatabaseAdapter databaseAdapter;

    OnMealAddFoodFragment onMealAddFoodFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_meal_editor, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnMealAddFoodFragment interface.
        try {
            onMealAddFoodFragment = (OnMealAddFoodFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealAddFoodFragment interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Look for instance data.
        Bundle mySavedInstanceState = getArguments();

        if (mySavedInstanceState.containsKey(MEAL_FOODS_NAMES)) {
            mealFoodsNames = mySavedInstanceState.getStringArrayList(MEAL_FOODS_NAMES);
            mealFoodsQuantity = mySavedInstanceState.getStringArrayList(MEAL_FOODS_QUANTITY);
            mealFoodsUnits = mySavedInstanceState.getStringArrayList(MEAL_FOODS_UNITS);
        } else {
            // Query to DB goes here (MORE OR LESS... NEEDS FIXES).
            //List<List<String>> mealFoodsInfo = databaseAdapter.getMealFoodsInfo(1);
            mealFoodsNames = new ArrayList<>();
            mealFoodsQuantity = new ArrayList<>();
            mealFoodsUnits = new ArrayList<>();

            // Add new food "button".
            mealFoodsNames.add(getString(R.string.meal_add_new));
            mealFoodsQuantity.add("");
            mealFoodsUnits.add("");
        }

        // Get references to the views.

        mealNameEditText = (EditText) getActivity().findViewById(R.id.meal_name);
        foodsListView = (RecyclerView) getActivity().findViewById(R.id.meal_food_list);

        // Set the adapter for the Foods list (Recycler View).
        foodsListAdapter = new ItemListAdapter(mealFoodsNames, mealFoodsUnits,
                R.layout.fragment_meal_editor_header, R.layout.item_list_one_line_row,
                R.layout.item_list_one_line_row_last, this);
        foodsListView.setAdapter(foodsListAdapter);

        // Set the layout manager for the RecyclerView.
        foodsListLayoutManager = new LinearLayoutManager(getActivity());
        foodsListView.setLayoutManager(foodsListLayoutManager);
    }

    /**
     * Save local variables in case of restart of fragment (due to re-orientation,
     * because it went into stopped state, etc).
     *
     * <e>Note: onSaveInstanceState doesn't work because it is called when the activity
     * stops, not the Fragment.</e>
     */
    @Override
    public void onPause() {
        super.onPause();

        getArguments().putStringArrayList(MEAL_FOODS_NAMES, mealFoodsNames);
        getArguments().putStringArrayList(MEAL_FOODS_QUANTITY, mealFoodsQuantity);
        getArguments().putStringArrayList(MEAL_FOODS_UNITS, mealFoodsUnits);
    }

    @Override
    public void onListItemClick(int position) {

        // "Add new" row clicked.
        if (position == mealFoodsNames.size()) {
            Fragment fragment = new MealAddFoodFragment();
            onMealAddFoodFragment.openMealAddFoodFragment(fragment, R.string.toolbar_meal_add_food);
        }
    }

    /**
     * This method is called by MainActivity in the MealAddFoodFragment callback.
     * @param foodId ID of the food that was selected by the user.
     * @param foodQuantity the amount of food set by the user.
     */
    public void setFoodSelected(long foodId, double foodQuantity) {
        // Get the food info.
        Food food = databaseAdapter.getFood(foodId);

        // sasdaaasdas
        int index = mealFoodsNames.size() - 1;
        mealFoodsNames.add(index, food.getName());
        mealFoodsQuantity.add(index, Double.toString(foodQuantity));
        mealFoodsUnits.add(index, food.getPortionUnits());

        Toast.makeText(getActivity(), "New list size: " + mealFoodsNames.size(), Toast.LENGTH_SHORT).show();

        foodsListAdapter.notifyItemInserted(index);
    }

    public interface OnMealAddFoodFragment {
        void openMealAddFoodFragment(Fragment fragment, int newToolbarTitle);
    }
}