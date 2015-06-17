package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.miguelpalacio.mymacros.database.DatabaseAdapter;

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

    EditText foodQuantityEditText;
    TextView foodQuantityUnits;

    TextView emptyPageMessage;

    OnFoodQuantitySet onFoodQuantitySet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_meal_add_food, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnFoodQuantitySet interface.
        try {
            onFoodQuantitySet = (OnFoodQuantitySet) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFoodQuantitySet interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get the names and summaries of foods inserted by the user.
        foodInfo = databaseAdapter.getFoodsList();

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
    public void onListItemClick(final int position) {

        // Create a dialog so the user can define the amount of the food selected.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_meal_add_food_title);

        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long foodId = Long.parseLong(foodInfo[0][position]);
                double foodQuantity = Double.parseDouble(foodQuantityEditText.getText().toString());

                // Send the food selected and the amount set by the user, back to MainActivity.
                onFoodQuantitySet.setFoodOnMealEditor(foodId, foodQuantity);
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
        foodQuantityEditText = (EditText) view.findViewById(R.id.dialog_meal_food_quantity);

        // Set units label.
        foodQuantityUnits = (TextView) view.findViewById(R.id.dialog_meal_food_quantity_units);
        foodQuantityUnits.setText(foodInfo[4][position]);

        // Show dialog.
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Disable dialog's OK button.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // Enable dialog's OK button if foodQuantityEditText EditText contains a valid value.
        foodQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("") || s.toString().equals(".")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set focus on dialog's EditText, and show soft keyboard.
        foodQuantityEditText.requestFocus();
        dialog.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    public interface OnFoodQuantitySet {
        void setFoodOnMealEditor(long foodId, double foodQuantity);
    }
}
