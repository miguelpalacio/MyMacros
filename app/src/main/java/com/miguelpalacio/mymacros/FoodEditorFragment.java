package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Foods Editor Page.
 * Allows the creation and edition of foods.
 */
public class FoodEditorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText foodNameEditText;
    EditText portionEditText;
    EditText proteinEditText;
    EditText carbosEditText;
    EditText fatEditText;
    EditText fiberEditText;
    Spinner portionUnitsSpinner;

    long foodId;
    String foodName;
    Double portionQuantity;
    Double proteinQuantity;
    Double carbosQuantity;
    Double fatQuantity;
    Double fiberQuantity;
    String portionUnits;

    String oldFoodName;

    DatabaseAdapter databaseAdapter;

    OnFoodSaved onFoodSaved;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable menu entries to receive calls.
        setHasOptionsMenu(true);

        // Check if New Food page was requested.
        if (getArguments().getBoolean(FoodsFragment.isNewFoodArg)) {
            foodId = -1;
            return;
        }

        // If it is Edit Food page, retrieve the ID of the food to be shown.
        foodId = getArguments().getLong(FoodsFragment.foodIdArg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_food_editor, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Get references to the views.

        foodNameEditText = (EditText) getActivity().findViewById(R.id.food_name);
        portionEditText = (EditText) getActivity().findViewById(R.id.food_portion);
        proteinEditText = (EditText) getActivity().findViewById(R.id.food_protein);
        carbosEditText = (EditText) getActivity().findViewById(R.id.food_carbos);
        fatEditText = (EditText) getActivity().findViewById(R.id.food_fat);
        fiberEditText = (EditText) getActivity().findViewById(R.id.food_fiber);

        // Portion Units Spinner.
        portionUnitsSpinner = (Spinner) getActivity().findViewById(R.id.portion_units_spinner);
        portionUnitsSpinner.setOnItemSelectedListener(this);

        // Populate the Spinner with the user choices.
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_units_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        portionUnitsSpinner.setAdapter(arrayAdapter);

        // On New Food page, return.
        if (foodId < 0) {
            return;
        }

        // Restore the food's details in the views.
        String[] foodInfo = databaseAdapter.getFoodInfo(foodId);

        foodNameEditText.setText(foodInfo[0]);
        portionEditText.setText(foodInfo[1]);

        // Set spinner selection.
        int spinnerSelection;
        switch (foodInfo[2]) {
            case "g":
                spinnerSelection = 0;
                break;
            case "oz":
                spinnerSelection = 1;
                break;
            case "ml":
                spinnerSelection = 2;
                break;
            case "lb":
                spinnerSelection = 3;
                break;
            default:
                spinnerSelection = 4;
                break;
        }
        portionUnitsSpinner.setSelection(spinnerSelection);

        proteinEditText.setText(foodInfo[3]);
        carbosEditText.setText(foodInfo[4]);
        fatEditText.setText(foodInfo[5]);
        fiberEditText.setText(foodInfo[6]);

        oldFoodName = foodInfo[0];
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_food_editor, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // If New Food page, enable Scan Barcode option, otherwise enable Delete option.
        if (foodId < 0) {
            MenuItem scanBarcodeOption = menu.findItem(R.id.action_scan_barcode);
            scanBarcodeOption.setVisible(true);
        } else {
            MenuItem deleteFoodOption = menu.findItem(R.id.action_delete_food);
            deleteFoodOption.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Insert/Update Food.
        if (id == R.id.action_save_food) {
            saveFood();
        } else if (id == R.id.action_scan_barcode) {
            Toast.makeText(getActivity(), "Functionality coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_delete_food) {
            onFoodDelete();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnFoodEditorFragment interface.
        try {
            onFoodSaved = (OnFoodSaved) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFoodSaved interface");
        }
    }

    // Spinner methods.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        portionUnits = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Public Interface to be implemented in MainActivity.
    public interface OnFoodSaved {
        void onFoodSavedSuccessfully();
    }

    private void saveFood() {
        boolean missingData = false;

        // Verify that all the information required is present. If not, inform the user.
        if (foodNameEditText.getText().toString().length() == 0) {
            foodNameEditText.setError("Please enter a name");
            missingData = true;
        }
        if (portionEditText.getText().toString().length() == 0) {
            portionEditText.setError("Please enter the portion quantity");
            missingData = true;
        }
        if (proteinEditText.getText().toString().length() == 0) {
            proteinEditText.setError("Please enter the protein quantity");
            missingData = true;
        }
        if (carbosEditText.getText().toString().length() == 0) {
            carbosEditText.setError("Please enter the carbohydrates quantity");
            missingData = true;
        }
        if (fatEditText.getText().toString().length() == 0) {
            fatEditText.setError("Please enter the fat quantity");
            missingData = true;
        }
        if (fiberEditText.getText().toString().length() == 0) {
            fiberEditText.setError("Please enter the fiber quantity\n(0 gr if not known)");
            missingData = true;
        }
        if (missingData) {
            // If there's missing data, abort.
            Toast.makeText(getActivity(), R.string.toast_food_edit, Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the information entered by the user.
        foodName = foodNameEditText.getText().toString();
        portionQuantity = Double.parseDouble(portionEditText.getText().toString());
        proteinQuantity = Double.parseDouble(proteinEditText.getText().toString());
        carbosQuantity = Double.parseDouble(carbosEditText.getText().toString());
        fatQuantity = Double.parseDouble(fatEditText.getText().toString());
        fiberQuantity = Double.parseDouble(fiberEditText.getText().toString());

        // Get rid of space chars at the beginning and end of the string.
        while (Character.isSpaceChar(foodName.charAt(0)) && foodName.length() > 1) {
            foodName = foodName.substring(1);
        }
        while (Character.isSpaceChar(foodName.charAt(foodName.length() - 1)) && foodName.length() > 1) {
            foodName = foodName.substring(0, foodName.length() - 1);
        }
        if (foodName.length() == 1 && Character.isSpaceChar(foodName.charAt(0))) {
            foodNameEditText.setText("");
            foodNameEditText.setError("Please enter a valid name");
            return;
        }

        // Capitalize the first letter of the foodName string.
        if (foodName.length() > 1) {
            foodName = foodName.substring(0, 1).toUpperCase() + foodName.substring(1);
        } else {
            foodName = foodName.toUpperCase();
        }

        // Insert/Update food.
        if (foodId < 1) {
            insertFood();
        } else {
            updateFood();
        }
    }

    /**
     * Inserts New Food into the database using the data entered by the user.
     */
    private void insertFood() {
        long id = databaseAdapter.insertFood(foodName, portionQuantity, portionUnits,
                proteinQuantity, carbosQuantity, fatQuantity, fiberQuantity);

        // Inform the user about the outcome of the transaction.
        if (id < 0) {
            if (databaseAdapter.isNameInFoods(foodName)) {
                Toast.makeText(getActivity(), "There is a food registered with that name",
                        Toast.LENGTH_SHORT).show();
                foodNameEditText.setError("Please enter a different name");
            } else {
                Toast.makeText(getActivity(), "There was an internal problem",
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getActivity(), "Food not created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Food created", Toast.LENGTH_SHORT).show();
            onFoodSaved.onFoodSavedSuccessfully();
        }
    }

    /**
     * Updates Food in the database using the data entered by the user.
     */
    private void updateFood() {

        // Check that the (possibly) new food name, is not registered by other food in the DB.
        if (!foodName.equals(oldFoodName)) {
            if (databaseAdapter.isNameInFoods(foodName)) {
                Toast.makeText(getActivity(), "There is a food registered with that name",
                        Toast.LENGTH_SHORT).show();
                foodNameEditText.setError("Please enter a different name");
                return;
            }
        }

        // Update the Foods table.
        int updateResult = databaseAdapter.updateFood(foodId, foodName, portionQuantity,
                portionUnits, proteinQuantity, carbosQuantity, fatQuantity, fiberQuantity);

        // Inform the user about the outcome of the transaction.
        if (updateResult == 1) {
            Toast.makeText(getActivity(), "Food updated", Toast.LENGTH_SHORT).show();
            onFoodSaved.onFoodSavedSuccessfully();
        } else {
            Toast.makeText(getActivity(), "There was an internal problem", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the tuple in the Foods table corresponding to the current foodId.
     */
    private void deleteFood() {

        int rowsDeleted = databaseAdapter.deleteFood(foodId);

        if (rowsDeleted == 1) {
            Toast.makeText(getActivity(), "Food deleted", Toast.LENGTH_SHORT).show();
            onFoodSaved.onFoodSavedSuccessfully();
        }
    }

    /**
     * Launch a dialog so that the user confirms the food deletion.
     */
    private void onFoodDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog properties.
        builder.setMessage(R.string.dialog_food_delete_message);
        builder.setPositiveButton(R.string.dialog_food_delete_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFood();
            }
        });
        builder.setNegativeButton(R.string.dialog_food_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User cancelled the dialog.
            }
        });

        // Launch the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
