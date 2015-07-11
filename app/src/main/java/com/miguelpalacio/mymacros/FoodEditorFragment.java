package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.miguelpalacio.mymacros.database.DatabaseAdapter;
import com.miguelpalacio.mymacros.datatypes.Food;
import com.miguelpalacio.mymacros.helpers.JSONParser;
import com.miguelpalacio.mymacros.helpers.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Foods Editor Page.
 * Allows the creation and edition of foods.
 */
public class FoodEditorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String URL_GET_FOOD = "http://miguelpalacio.co/mymacros-server/get-food.php";
    private static final String URL_INSERT_FOOD = "http://miguelpalacio.co/mymacros-server/insert-food.php";
    private static final String TAG_CODE_RESPONSE = "codeResponse";
    private static final String TAG_MESSAGE = "message";

    private static final String BARCODE_SCANNED = "barcodeScanned";
    private static final String SAVE_ON_SERVER = "saveOnServer";

    EditText foodNameEditText;
    EditText portionEditText;
    EditText proteinEditText;
    EditText carbsEditText;
    EditText fatEditText;
    EditText fiberEditText;
    Spinner portionUnitsSpinner;

    long foodId;
    String foodName;
    Double portionQuantity;
    Double proteinQuantity;
    Double carbsQuantity;
    Double fatQuantity;
    Double fiberQuantity;
    String portionUnits;

    String oldFoodName;

    DatabaseAdapter databaseAdapter;

    OnFoodSaved onFoodSaved;

    boolean saveOnServer;
    String barcodeScanned;
    JSONParser jsonParser;
    ProgressDialog progressDialog;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure that the host activity implements the OnFoodEditorFragment interface.
        try {
            onFoodSaved = (OnFoodSaved) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFoodSaved interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());
        jsonParser = new JSONParser();
        saveOnServer = false;

        // Look for instance data.
        final Bundle mySavedInstanceState = getArguments();
        if (mySavedInstanceState.containsKey(BARCODE_SCANNED)) {
            barcodeScanned = mySavedInstanceState.getString(BARCODE_SCANNED);
            saveOnServer = mySavedInstanceState.getBoolean(SAVE_ON_SERVER);
        }

        // Get references to the views.

        foodNameEditText = (EditText) getActivity().findViewById(R.id.food_name);
        portionEditText = (EditText) getActivity().findViewById(R.id.food_portion);
        proteinEditText = (EditText) getActivity().findViewById(R.id.food_protein);
        carbsEditText = (EditText) getActivity().findViewById(R.id.food_carbos);
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
        Food food = databaseAdapter.getFood(foodId);

        // Don't show more than 1 decimal position.
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        foodNameEditText.setText(food.getName());
        portionEditText.setText(decimalFormat.format(food.getPortionQuantity()));
        setSpinnerSelection(food.getPortionUnits());
        proteinEditText.setText(decimalFormat.format(food.getProtein()));
        carbsEditText.setText(decimalFormat.format(food.getCarbs()));
        fatEditText.setText(decimalFormat.format(food.getFat()));
        fiberEditText.setText(decimalFormat.format(food.getFiber()));

        oldFoodName = food.getName();
    }

    // Override onResume to check the results of the barcode scanner activity.
    @Override
    public void onResume() {
        super.onResume();

        // Check if a food barcode was scanned.
        MainActivity activity = (MainActivity) getActivity();
        if (activity.wasProductScanned()) {
/*            Toast.makeText(activity, "FORMAT: " + activity.getBarcodeScanFormat(), Toast.LENGTH_SHORT).show();
            Toast.makeText(activity, "CONTENT: " + activity.getBarcodeScanResult(), Toast.LENGTH_SHORT).show();*/
            barcodeScanned = activity.getBarcodeScanResult();
            new GetFoodInfoOnExternalDatabase().execute();
            activity.setProductScanned(false);
        }
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
            //Toast.makeText(getActivity(), "Functionality coming soon", Toast.LENGTH_SHORT).show();
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        } else if (id == R.id.action_delete_food) {
            onFoodDelete();
        }

        return super.onOptionsItemSelected(item);
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


    /**
     * Save local variables in case of restart of fragment (due to re-orientation,
     * because it went into stopped state, etc).
     *
     * <e>Note: onSaveInstanceState doesn't work because it is called when the activity
     * stops, not when the Fragment does.</e>
     */
    @Override
    public void onPause() {
        super.onPause();

        if (barcodeScanned != null) {
            getArguments().putString(BARCODE_SCANNED, barcodeScanned);
            getArguments().putBoolean(SAVE_ON_SERVER, saveOnServer);
        }
    }

    public void setSpinnerSelection(String selection) {
        int spinnerSelection;
        switch (selection) {
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
    }

    // *********************************************************************************************
    // Methods to perform operations in the database.

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
        if (carbsEditText.getText().toString().length() == 0) {
            carbsEditText.setError("Please enter the carbohydrates quantity");
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
        carbsQuantity = Double.parseDouble(carbsEditText.getText().toString());
        fatQuantity = Double.parseDouble(fatEditText.getText().toString());
        fiberQuantity = Double.parseDouble(fiberEditText.getText().toString());

        // Format the foodName string and check its validity.
        foodName = Utilities.formatNameString(foodName);
        if (foodName.length() == 1 && Character.isSpaceChar(foodName.charAt(0))) {
            foodNameEditText.setText("");
            foodNameEditText.setError("Please enter a valid name");
            return;
        }

        // Insert/Update food.
        if (foodId < 0) {
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
                proteinQuantity, carbsQuantity, fatQuantity, fiberQuantity);

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
            if(saveOnServer) {
                // User scanned food's barcode, but food was not registered. Register it.
                new SaveFoodOnExternalDatabase().execute();
            } else {
                Toast.makeText(getActivity(), "Food created", Toast.LENGTH_SHORT).show();
                onFoodSaved.onFoodSavedSuccessfully();
            }
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
                portionUnits, proteinQuantity, carbsQuantity, fatQuantity, fiberQuantity);

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


    // *********************************************************************************************
    // Inner classes to deal with external database. They extend from AsyncTask since they
    // may take perform for a long time, so it's necessary to separate them from the GUI task.

    /**
     * This task will be executed when the user scans a food's barcode and the food is not
     * registered in the external database, and the user has input all the food's data and saved it.
     */
    class SaveFoodOnExternalDatabase extends AsyncTask<String, String, String> {

        // Before starting the background thread, show a process dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading information...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            int codeResponse;

            try {
                // Set parameters for the HTTP request that will be made.
                List<NameValuePair> parameters = new ArrayList<>();
                parameters.add(new BasicNameValuePair("name", foodName));
                parameters.add(new BasicNameValuePair("portionQuantity", Double.toString(portionQuantity)));
                parameters.add(new BasicNameValuePair("portionUnits", portionUnits));
                parameters.add(new BasicNameValuePair("protein", Double.toString(proteinQuantity)));
                parameters.add(new BasicNameValuePair("carbs", Double.toString(carbsQuantity)));
                parameters.add(new BasicNameValuePair("fat", Double.toString(fatQuantity)));
                parameters.add(new BasicNameValuePair("fiber", Double.toString(fiberQuantity)));
                parameters.add(new BasicNameValuePair("barcode", barcodeScanned));

/*                Log.d("Request!", "Starting");*/

                // Perform HTTP request and store the response into a JSON object.
                JSONObject json = jsonParser.makeHttpRequest(URL_INSERT_FOOD, "POST", parameters);
/*                Log.d("Insertion attempt", json.toString());*/

                // Get code response from the JSON object.
                codeResponse = json.getInt(TAG_CODE_RESPONSE);
                if (codeResponse == 1) {
/*                    Log.d("Insertion Successful!", json.toString());*/
                    // Food inserted into external database successfully.
                    return json.getString(TAG_MESSAGE);
                } else {
/*                    Log.d("Insertion Failure!", json.getString(TAG_MESSAGE));*/
                    // Failure upon food insertion into external database.
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // After completing the background task, dismiss the process dialog.
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(getActivity(), "Food created", Toast.LENGTH_SHORT).show();
                // Show the response message (TAG_MESSAGE) from the server in a toast.
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();
                onFoodSaved.onFoodSavedSuccessfully();
            }
        }
    }

    /**
     * Having the food's barcode, this task attempts to retrieve the food's information
     * from the external database, and assigns all the retrieved data into the input fields.
     */
    class GetFoodInfoOnExternalDatabase extends AsyncTask<String, String, String> {

        private JSONObject foodRetrieved;

        // Before starting the background thread, show a process dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Retrieving information...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            int codeResponse;

            try {
                // Set parameters for the HTTP request that will be made.
                List<NameValuePair> parameters = new ArrayList<>();
                parameters.add(new BasicNameValuePair("barcode", barcodeScanned));
/*                Log.d("Request!", "Starting");*/

                // Perform HTTP request and store the response into a JSON object.
                JSONObject json = jsonParser.makeHttpRequest(URL_GET_FOOD, "POST", parameters);
/*                Log.d("Insertion attempt", json.toString());*/

                // Get code response from the JSON object.
                codeResponse = json.getInt(TAG_CODE_RESPONSE);
                if (codeResponse == 1) {
/*                    Log.d("Retrieval Successful!", json.toString());*/
                    foodRetrieved = json.getJSONObject("food");
                    return json.getString(TAG_MESSAGE);
                } else if (codeResponse == 2) {
                    // Enable insertion in external database.
                    saveOnServer = true;
                    return json.getString(TAG_MESSAGE);
                }
                else {
                    //Log.d("Retrieval Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // After completing the background task, dismiss the process dialog.
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            if (file_url != null) {
                // Show the response message (TAG_MESSAGE) from the server in a toast.
                Toast.makeText(getActivity(), file_url, Toast.LENGTH_LONG).show();

                // Assign retrieved food's info (if any) to the input fields.
                if (foodRetrieved != null) {
                    try {
                        foodNameEditText.setText(foodRetrieved.getString("Name"));
                        portionEditText.setText(foodRetrieved.getString("PortionQuantity"));
                        setSpinnerSelection(foodRetrieved.getString("PortionUnits"));
                        proteinEditText.setText(foodRetrieved.getString("Protein"));
                        carbsEditText.setText(foodRetrieved.getString("Carbs"));
                        fatEditText.setText(foodRetrieved.getString("Fat"));
                        fiberEditText.setText(foodRetrieved.getString("Fiber"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
