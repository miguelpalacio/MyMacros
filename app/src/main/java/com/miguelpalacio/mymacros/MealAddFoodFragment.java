package com.miguelpalacio.mymacros;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Add Food for Meal page.
 * This page displays the foods saved (using the Foods Fragment layout), so
 * that the user can choose the food she wants to add to her meal.
 */
public class MealAddFoodFragment extends Fragment implements SubheadersListAdapter.ViewHolder.ClickListener {

    RecyclerView foodListView;
    RecyclerView.LayoutManager foodListLayoutManager;
    SubheadersListAdapter foodListAdapter;

    DatabaseAdapter databaseAdapter;
    String[][] foodInfo;

    EditText foodQuantity;

    TextView emptyPageMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_meal_add_food, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get the names and summaries of foods inserted by the user.
        foodInfo = databaseAdapter.getFoods();

        // Food List RecyclerView.
        foodListView = (RecyclerView) getActivity().findViewById(R.id.meal_add_food_list);

        // Set the adapter.
        foodListAdapter = new SubheadersListAdapter(foodInfo[1], foodInfo[2], foodInfo[3], this);
        foodListView.setAdapter(foodListAdapter);

        // Set the layout manager for the RecyclerView.
        foodListLayoutManager = new LinearLayoutManager(getActivity());
        foodListView.setLayoutManager(foodListLayoutManager);

        // If there are no foods, show the default message for empty page.
        if (foodInfo[0].length == 0) {
            emptyPageMessage = (TextView) getActivity().findViewById(R.id.meal_add_food_empty_page);
            emptyPageMessage.setVisibility(View.VISIBLE);
            foodListView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onListItemClick(int position) {

        // Create a dialog so the user can define the amount of the food selected.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_meal_add_food_title);

        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Pass a custom layout to the dialog.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_meal_add_food, null);
        builder.setView(view);

        // Dialog EditText.
        foodQuantity = (EditText) view.findViewById(R.id.dialog_meal_food_quantity);

        // Show dialog.
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set focus on dialog's EditText, and show soft keyboard.
        foodQuantity.requestFocus();
        dialog.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }
}
