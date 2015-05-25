package com.miguelpalacio.mymacros;

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

import java.text.DecimalFormat;


public class FoodNewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText foodNameEditText;
    EditText portionEditText;
    EditText proteinEditText;
    EditText carbosEditText;
    EditText fatEditText;
    EditText fiberEditText;
    Spinner portionUnitsSpinner;

    String foodName;
    Double portionQuantity;
    Double proteinQuantity;
    Double carbosQuantity;
    Double fatQuantity;
    Double fiberQuantity;
    int portionUnits;

    DatabaseAdapter databaseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable menu entries to receive calls.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_food_new, container, false);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_food_new, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Insert the new food into the database.
        if (id == R.id.action_save_food) {
            insertFood();
        } else if (id == R.id.action_scan_barcode) {
            Toast.makeText(getActivity(), "Functionality coming soon", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // Spinner methods.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //portionUnits = parent.getItemAtPosition(position).toString();
        /**
         * portionUnits:
         * · 0 = gr
         * · 1 = oz
         * · 2 = ml
         * · 3 = lb
         * · 4 = unit
         */
        portionUnits = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Inserts New Food into the database using the data entered by the user.
     * In case there's missing data, informs the user and shows where the problem is.
     */
    public void insertFood() {

        boolean missingData = false;

        // Verify that all the information required is present.
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
            return;
        }

/*        String inputText;*/

/*        // Decimal format to prepare the data prior to insertion into database.
        DecimalFormat decimalFormat = new DecimalFormat("#.#");*/

        // Get the information entered by the user.

        foodName = foodNameEditText.getText().toString();

        portionQuantity = Double.parseDouble(portionEditText.getText().toString());
        proteinQuantity = Double.parseDouble(proteinEditText.getText().toString());
        carbosQuantity = Double.parseDouble(carbosEditText.getText().toString());
        fatQuantity = Double.parseDouble(fatEditText.getText().toString());
        fiberQuantity = Double.parseDouble(fiberEditText.getText().toString());

/*        inputText = portionEditText.getText().toString();
        portionQuantity = Double.parseDouble(decimalFormat.format(inputText));

        inputText = proteinEditText.getText().toString();
        proteinQuantity = Double.parseDouble(decimalFormat.format(inputText));

        inputText = carbosEditText.getText().toString();
        carbosQuantity = Double.parseDouble(decimalFormat.format(inputText));

        inputText = fatEditText.getText().toString();
        fatQuantity = Double.parseDouble(decimalFormat.format(inputText));

        inputText = fiberEditText.getText().toString();
        fiberQuantity = Double.parseDouble(decimalFormat.format(inputText));*/

        long id = databaseAdapter.insertFood(foodName, proteinQuantity, carbosQuantity,
                fatQuantity, fiberQuantity, portionQuantity, portionUnits);

        if (id < 0) {
            Toast.makeText(getActivity(), "There was an internal problem", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Food saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void update() {
    }

    public void delete() {

    }

}
