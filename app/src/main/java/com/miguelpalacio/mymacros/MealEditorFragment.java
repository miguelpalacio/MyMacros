package com.miguelpalacio.mymacros;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * Foods Editor Page.
 * Allows the creation and edition of foods.
 */
public class MealEditorFragment extends Fragment implements ItemListAdapter.ViewHolder.ClickListener {

    EditText mealNameEditText;

    RecyclerView foodsListView;
    RecyclerView.LayoutManager foodsListLayoutManager;
    ItemListAdapter foodsListAdapter;

    String[][] mealFoods;

    DatabaseAdapter databaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_meal_editor, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get references to the views.

        mealNameEditText = (EditText) getActivity().findViewById(R.id.meal_name);
        foodsListView = (RecyclerView) getActivity().findViewById(R.id.meal_food_list);

        mealFoods = new String[2][2];

        mealFoods[0] = new String[]{"Oatmeal", "Milk", "Orange Juice", "Olive Oil", "Eggs"};
        mealFoods[1] = new String[]{"25 g", "150 ml", "150 ml", "20 g", "2 units"};

        // Set the adapter for the Foods list (Recycler View).
        foodsListAdapter = new ItemListAdapter(mealFoods[0], mealFoods[1],
                R.layout.fragment_meal_editor_header, true, this);
                //0, this);
        foodsListView.setAdapter(foodsListAdapter);

        // Set the layout manager for the RecyclerView.
        foodsListLayoutManager = new LinearLayoutManager(getActivity());
        foodsListView.setLayoutManager(foodsListLayoutManager);
    }

    @Override
    public void onListItemClick(int position) {

    }
}
