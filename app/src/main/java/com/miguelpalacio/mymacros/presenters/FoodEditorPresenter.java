package com.miguelpalacio.mymacros.presenters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.miguelpalacio.mymacros.R;
import com.miguelpalacio.mymacros.helpers.JSONParser;
import com.miguelpalacio.mymacros.helpers.Utilities;
import com.miguelpalacio.mymacros.model.DatabaseAdapter;
import com.miguelpalacio.mymacros.views.FoodEditorFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for the FoodEditorFragment.
 */
public class FoodEditorPresenter {

    static final String URL_GET_FOOD = "http://miguelpalacio.co/mymacros-server/get-food.php";
    static final String URL_INSERT_FOOD = "http://miguelpalacio.co/mymacros-server/insert-food.php";
    static final String TAG_CODE_RESPONSE = "codeResponse";
    static final String TAG_MESSAGE = "message";

    static final String BARCODE_SCANNED = "barcodeScanned";
    static final String SAVE_ON_SERVER = "saveOnServer";

    Context context;
    DatabaseAdapter databaseAdapter;

    long foodId;
    String foodName;
    Double portionQuantity;
    Double proteinQuantity;
    Double carbsQuantity;
    Double fatQuantity;
    Double fiberQuantity;
    String portionUnits;

    String oldFoodName;

    boolean saveOnServer;
    String barcodeScanned;
    JSONParser jsonParser;
    ProgressDialog progressDialog;

    FoodEditorFragment.OnFoodSaved onFoodSaved;

    public FoodEditorPresenter(Context context, FoodEditorFragment.OnFoodSaved onFoodSaved) {
        this.context = context;
        this.onFoodSaved = onFoodSaved;

        databaseAdapter = new DatabaseAdapter(context);
    }

    public int setSpinnerSelection(String selection) {
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
        //portionUnitsSpinner.setSelection(spinnerSelection);
        return spinnerSelection;
    }

    // *********************************************************************************************
    // Methods to perform operations in the database.

    private void saveFood(EditText foodNameEditText, EditText portionEditText, EditText proteinEditText,
                          EditText carbsEditText, EditText fatEditText, EditText fiberEditText) {
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
            Toast.makeText(context, R.string.toast_food_edit, Toast.LENGTH_SHORT).show();
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
            insertFood(foodNameEditText);

        } else {
            updateFood(foodNameEditText);
        }
    }

    /**
     * Inserts New Food into the database using the data entered by the user.
     */
    private void insertFood(EditText foodNameEditText) {
        long id = databaseAdapter.insertFood(foodName, portionQuantity, portionUnits,
                proteinQuantity, carbsQuantity, fatQuantity, fiberQuantity);

        // Inform the user about the outcome of the transaction.
        if (id < 0) {
            if (databaseAdapter.isNameInFoods(foodName)) {
                Toast.makeText(context, "There is a food registered with that name",
                        Toast.LENGTH_SHORT).show();
                foodNameEditText.setError("Please enter a different name");
            } else {
                Toast.makeText(context, "There was an internal problem",
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Food not created", Toast.LENGTH_SHORT).show();

        } else {
            if(saveOnServer) {
                // User scanned food's barcode, but food was not registered. Register it.
                new SaveFoodOnExternalDatabase().execute();
            } else {
                Toast.makeText(context, "Food created", Toast.LENGTH_SHORT).show();
                onFoodSaved.onFoodSavedSuccessfully();
            }
        }
    }

    /**
     * Updates Food in the database using the data entered by the user.
     */
    private void updateFood(EditText foodNameEditText) {

        // Check that the (possibly) new food name, is not registered by other food in the DB.
        if (!foodName.equals(oldFoodName)) {
            if (databaseAdapter.isNameInFoods(foodName)) {
                Toast.makeText(context, "There is a food registered with that name",
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
            Toast.makeText(context, "Food updated", Toast.LENGTH_SHORT).show();
            onFoodSaved.onFoodSavedSuccessfully();
        } else {
            Toast.makeText(context, "There was an internal problem", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the tuple in the Foods table corresponding to the current foodId.
     */
    private void deleteFood() {

        int rowsDeleted = databaseAdapter.deleteFood(foodId);

        if (rowsDeleted == 1) {
            Toast.makeText(context, "Food deleted", Toast.LENGTH_SHORT).show();
            onFoodSaved.onFoodSavedSuccessfully();
        }
    }

    /**
     * Launch a dialog so that the user confirms the food deletion.
     */
    private void onFoodDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

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

    public void retrieveFoodFromServer(EditText foodNameEditText, EditText portionEditText,
                                       EditText proteinEditText, EditText carbsEditText,
                                       EditText fatEditText, EditText fiberEditText) {

/*        foodNameEditText.setText(foodRetrieved.getString("Name"));
        portionEditText.setText(foodRetrieved.getString("PortionQuantity"));
        setSpinnerSelection(foodRetrieved.getString("PortionUnits"));
        proteinEditText.setText(foodRetrieved.getString("Protein"));
        carbsEditText.setText(foodRetrieved.getString("Carbs"));
        fatEditText.setText(foodRetrieved.getString("Fat"));
        fiberEditText.setText(foodRetrieved.getString("Fiber"));*/
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
            progressDialog = new ProgressDialog(context);
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
                Toast.makeText(context, "Food created", Toast.LENGTH_SHORT).show();
                // Show the response message (TAG_MESSAGE) from the server in a toast.
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
                onFoodSaved.onFoodSavedSuccessfully();
            }
        }
    }

    /**
     * Having the food's barcode, this task attempts to retrieve the food's information
     * from the external database, and assigns all the retrieved data into the input fields.
     */
/*    class GetFoodInfoOnExternalDatabase extends AsyncTask<String, String, String> {

        private JSONObject foodRetrieved;

        // Before starting the background thread, show a process dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
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
*//*                Log.d("Request!", "Starting");*//*

                // Perform HTTP request and store the response into a JSON object.
                JSONObject json = jsonParser.makeHttpRequest(URL_GET_FOOD, "POST", parameters);
*//*                Log.d("Insertion attempt", json.toString());*//*

                // Get code response from the JSON object.
                codeResponse = json.getInt(TAG_CODE_RESPONSE);
                if (codeResponse == 1) {
*//*                    Log.d("Retrieval Successful!", json.toString());*//*
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
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();

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
    }*/
}
