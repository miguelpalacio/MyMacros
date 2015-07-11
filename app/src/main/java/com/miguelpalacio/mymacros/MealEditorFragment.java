package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.miguelpalacio.mymacros.database.DatabaseAdapter;
import com.miguelpalacio.mymacros.datatypes.Meal;
import com.miguelpalacio.mymacros.datatypes.MealFood;
import com.miguelpalacio.mymacros.helpers.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Meals Editor Page.
 * Allows the creation and edition of meals.
 */
public class MealEditorFragment extends Fragment implements ItemListAdapter.ViewHolder.ClickListener {

    private static final String MEAL_NAME = "mealName";
    private static final String FOOD_ID_LIST = "foodIdList";
    private static final String FOOD_NAME_LIST = "foodNameList";
    private static final String FOOD_SUMMARY_LIST = "foodSummaryList";
    private static final String FOOD_QUANTITY_LIST = "foodQuantityList";
    private static final String FOOD_PROTEIN_LIST = "foodProteinList";
    private static final String FOOD_CARBS_LIST = "foodCarbsList";
    private static final String FOOD_FAT_LIST = "foodFatList";
    private static final String FOOD_FIBER_LIST = "foodFiberList";
    private static final String OLD_MEAL_NAME = "oldMealName";
    private static final String OLD_FOOD_ID_LIST = "oldFoodIdList";
    private static final String OLD_FOOD_QUANTITY_LIST = "oldFoodQuantityList";

    RecyclerView itemListView;
    RecyclerView.LayoutManager itemListLayoutManager;
    ItemListAdapter itemListAdapter;

    LinearLayout listHeader;
    EditText mealNameEditText;
    LinearLayout nutritionFactsLayout;
    TextView proteinTextView;
    TextView carbsTextView;
    TextView fatTextView;
    TextView fiberTextView;
    TextView energyTextView;

    ArrayList<String> foodIdList;
    ArrayList<String> foodNameList;
    ArrayList<String> foodSummaryList;
    ArrayList<String> foodQuantityList;
    ArrayList<String> foodProteinList;
    ArrayList<String> foodCarbsList;
    ArrayList<String> foodFatList;
    ArrayList<String> foodFiberList;

    DatabaseAdapter databaseAdapter;

    OnMealAddFood onMealAddFoodFragment;
    OnMealSaved onMealSaved;

    final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    long mealId;
    String mealName;
    double totalProtein;
    double totalEnergy;
    double totalCarbs;
    double totalFat;
    double totalFiber;
    String energyUnits;

    String oldMealName;
    ArrayList<String> oldFoodIdList;
    ArrayList<String> oldFoodQuantityList;

    EditText foodQuantityEditText;
    TextView foodQuantityUnits;

    PieChart pieChart;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Ensure that the host activity implements the OnMealAddFood interface.
        try {
            onMealAddFoodFragment = (OnMealAddFood) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealAddFood interface");
        }

        // Ensure that the host activity implements the OnMealSaved interface.
        try {
            onMealSaved = (OnMealSaved) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealSaved interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable menu entries to receive calls.
        setHasOptionsMenu(true);

        // Check if New Meal page was requested.
        if (getArguments().getBoolean(MealsFragment.isNewMealArg)) {
            mealId = -1;
            return;
        }

        // If it is Edit Meal page, retrieve the ID of the food to be shown.
        mealId = getArguments().getLong(MealsFragment.mealIdArg);
    }

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

        // Look for instance data.
        final Bundle mySavedInstanceState = getArguments();

        if (mySavedInstanceState.containsKey(FOOD_ID_LIST)) {

            // Load previously saved data.
            mealName = mySavedInstanceState.getString(MEAL_NAME);

            foodIdList = mySavedInstanceState.getStringArrayList(FOOD_ID_LIST);
            foodNameList = mySavedInstanceState.getStringArrayList(FOOD_NAME_LIST);
            foodSummaryList = mySavedInstanceState.getStringArrayList(FOOD_SUMMARY_LIST);
            foodQuantityList = mySavedInstanceState.getStringArrayList(FOOD_QUANTITY_LIST);
            foodProteinList = mySavedInstanceState.getStringArrayList(FOOD_PROTEIN_LIST);
            foodCarbsList = mySavedInstanceState.getStringArrayList(FOOD_CARBS_LIST);
            foodFatList = mySavedInstanceState.getStringArrayList(FOOD_FAT_LIST);
            foodFiberList = mySavedInstanceState.getStringArrayList(FOOD_FIBER_LIST);

            oldMealName = mySavedInstanceState.getString(OLD_MEAL_NAME);
            oldFoodIdList = mySavedInstanceState.getStringArrayList(OLD_FOOD_ID_LIST);
            oldFoodQuantityList = mySavedInstanceState.getStringArrayList(OLD_FOOD_QUANTITY_LIST);

            // When coming back from MealAddFoodFragment, check if user selected a food.
            MainActivity activity = (MainActivity) getActivity();
            if (activity.wasFoodAddedToMeal()) {
                setFoodSelected(activity.getMealFoodId(), activity.getMealFoodQuantity());
                activity.setFoodAddedToMeal(false);
            }

        } else {

            // Initialize local variables.
            mealName = "";

            foodIdList = new ArrayList<>();
            foodNameList = new ArrayList<>();
            foodSummaryList = new ArrayList<>();
            foodQuantityList = new ArrayList<>();
            foodProteinList = new ArrayList<>();
            foodCarbsList = new ArrayList<>();
            foodFatList = new ArrayList<>();
            foodFiberList = new ArrayList<>();

            // If Edit Meal page, restore the meal's details.
            if (mealId >= 0) {
                Meal meal = databaseAdapter.getMeal(mealId);

                mealName = meal.getName();
                oldMealName = mealName;

                for (int i = 0; i < meal.getFoods().size(); i++) {
                    setFoodOnLists(meal.getFoods().get(i));
                }

                // Lists to check possible future changes on foods.
                oldFoodIdList = new ArrayList<>();
                oldFoodQuantityList = new ArrayList<>();
                for (int i = 0; i < foodIdList.size(); i++) {
                    oldFoodIdList.add(foodIdList.get(i));
                    oldFoodQuantityList.add(foodQuantityList.get(i));
                }
            }

            // Add new food "button".
            foodNameList.add(getString(R.string.meal_add_food));
            foodSummaryList.add("");
            foodQuantityList.add("");
        }

        // Define the main view (the RecyclerView).
        itemListView = (RecyclerView) getActivity().findViewById(R.id.meal_food_list);

        // Set the adapter for the Foods list (Recycler View).
        itemListAdapter = new ItemListAdapter(foodNameList, foodSummaryList,
                R.layout.fragment_meal_editor_header, R.layout.item_list_one_line_row,
                R.layout.item_list_one_line_row_last, this);
        itemListView.setAdapter(itemListAdapter);

        // Set the layout manager for the RecyclerView.
        itemListLayoutManager = new LinearLayoutManager(getActivity());
        itemListView.setLayoutManager(itemListLayoutManager);

        // Set an observer in the RecyclerView's layout (needed to know when it's accessible).
        itemListView.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (itemListView.getViewTreeObserver().isAlive()) {

                            // Get the references to the views in the header.

                            listHeader = (LinearLayout) itemListView.getChildAt(0);
                            mealNameEditText = (EditText) listHeader.findViewById(R.id.meal_name);
                            nutritionFactsLayout = (LinearLayout) listHeader.findViewById(R.id.meal_nutrition_facts);
                            proteinTextView = (TextView) listHeader.findViewById(R.id.meal_protein);
                            carbsTextView = (TextView) listHeader.findViewById(R.id.meal_carbs);
                            fatTextView = (TextView) listHeader.findViewById(R.id.meal_fat);
                            fiberTextView = (TextView) listHeader.findViewById(R.id.meal_fiber);
                            //energyTextView = (TextView) listHeader.findViewById(R.id.meal_energy);
                            pieChart = (PieChart) getActivity().findViewById(R.id.meal_pie_chart);

                            // Since the references are ready, set data in the list header.
                            setHeaderData();

                            // Configure Pie Chart.

                            // Chart interaction.
                            pieChart.setHighlightEnabled(true);
                            pieChart.highlightValues(null);

                            pieChart.setDrawHoleEnabled(true);
                            pieChart.setHoleColorTransparent(true);

                            pieChart.setTransparentCircleColor(getResources().getColor(R.color.text_background));

                            pieChart.setHoleRadius(35f);
                            pieChart.setTransparentCircleRadius(42f);

                            pieChart.setDrawCenterText(true);
                            pieChart.setCenterTextSize(14f);

                            pieChart.setRotationAngle(0);
                            pieChart.setRotationEnabled(false);

                            //pieChart.setDescription(getString(R.string.pie_chart_meal_description));
                            pieChart.setDescription("");
                            pieChart.setDescriptionTextSize(12f);

                            setPieChartData();

                            // Remove listener to avoid further callings to this method.
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                itemListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                itemListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        }
                    }
                });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_meal_editor, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // If New Food page, enable Scan Barcode option, otherwise enable Delete option.
        if (mealId >= 0) {
            MenuItem deleteFoodOption = menu.findItem(R.id.action_delete_meal);
            deleteFoodOption.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Insert/Update Food.
        if (id == R.id.action_save_meal) {
            saveMeal();
        } else if (id == R.id.action_delete_meal) {
            onMealDelete();
        }

        return super.onOptionsItemSelected(item);
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

        getArguments().putString(MEAL_NAME, mealNameEditText.getText().toString());

        getArguments().putStringArrayList(FOOD_ID_LIST, foodIdList);
        getArguments().putStringArrayList(FOOD_NAME_LIST, foodNameList);
        getArguments().putStringArrayList(FOOD_SUMMARY_LIST, foodSummaryList);
        getArguments().putStringArrayList(FOOD_QUANTITY_LIST, foodQuantityList);
        getArguments().putStringArrayList(FOOD_PROTEIN_LIST, foodProteinList);
        getArguments().putStringArrayList(FOOD_CARBS_LIST, foodCarbsList);
        getArguments().putStringArrayList(FOOD_FAT_LIST, foodFatList);
        getArguments().putStringArrayList(FOOD_FIBER_LIST, foodFiberList);

        getArguments().putString(OLD_MEAL_NAME, oldMealName);
        getArguments().putStringArrayList(OLD_FOOD_ID_LIST, oldFoodIdList);
        getArguments().putStringArrayList(OLD_FOOD_QUANTITY_LIST, oldFoodQuantityList);
    }

    @Override
    public void onListItemClick(int position) {

        // "Add new" row clicked.
        if (position == foodNameList.size()) {
            Fragment fragment = new MealAddFoodFragment();
            onMealAddFoodFragment.openMealAddFoodFragment(fragment, R.string.toolbar_meal_add_food);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (position == foodNameList.size()) {
            return false;
        }

        // Fix position value (don't count header).
        final int pos = position - 1;

        // Open a dialog that gives two options: change food quantity, or delete food from list.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dialog_meal_choose_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Change food quantity.
                    showSetFoodQuantityDialog(pos);
                } else {
                    // Remove food from list.
                    removeFromFoodLists(pos);
                }
            }
        });

        // Launch the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }


/*    // Implemented interfaces for dialogs.

    @Override
    public void onDeleteMeal(DialogFragment dialog) {
        deleteMeal();
    }*/

    // Public Interfaces to be implemented in MainActivity.

    public interface OnMealSaved {
        void onMealSavedSuccessfully();
    }

    public interface OnMealAddFood {
        void openMealAddFoodFragment(Fragment fragment, int newToolbarTitle);
    }


    /**
     * Retrieves the food info and calls setFoodOnLists.
     * @param foodId ID of the food that was selected by the user.
     * @param foodQuantity the amount of food set by the user.
     */
    public void setFoodSelected(long foodId, double foodQuantity) {
        // Retrieve the food info.
        MealFood food = new MealFood(databaseAdapter.getFood(foodId));
        food.setFoodQuantity(foodQuantity);

        setFoodOnLists(food);
    }

    /**
     * Set useful information of a given food into lists.
     * @param food the given food.
     */
    public void setFoodOnLists(MealFood food) {

        // Get food quantity in meal.
        double foodQuantity = food.getFoodQuantity();

        // Calculate macros given by the food, and its quantity.
        double protein = food.getProtein() * (foodQuantity / food.getPortionQuantity());
        double carbs = food.getCarbs() * (foodQuantity / food.getPortionQuantity());
        double fat = food.getFat() * (foodQuantity / food.getPortionQuantity());
        double fiber = food.getFiber() * (foodQuantity / food.getPortionQuantity());

        // Define food summary.
        String foodSummary = decimalFormat.format(foodQuantity) + " " + food.getPortionUnits();

        // Add food information to the lists.
        int position = foodIdList.size() - 1;
        if (position < 0) {
            position = 0;
        }
        boolean foodPreviouslyAdded = false;

        // Check that the selected food is not already in the meal's food list.
        for (int i = 0; i < foodIdList.size(); i++) {
            if (foodIdList.get(i).equals(Long.toString(food.getId()))) {
                position = i;
                foodPreviouslyAdded = true;
            }
        }

        if (foodPreviouslyAdded) {
            // Set the new values.
            foodSummaryList.set(position, foodSummary);
            foodQuantityList.set(position, Double.toString(foodQuantity));

            foodProteinList.set(position, Double.toString(protein));
            foodCarbsList.set(position, Double.toString(carbs));
            foodFatList.set(position, Double.toString(fat));
            foodFiberList.set(position, Double.toString(fiber));

            // Notify the RecyclerView's adapter that data changed.
            itemListAdapter.notifyItemChanged(position);

        } else {
            // Insert all the food data into the lists.
            foodIdList.add(position, Long.toString(food.getId()));

            foodNameList.add(position, food.getName());
            foodSummaryList.add(position, decimalFormat.format(foodQuantity) + " " + food.getPortionUnits());
            foodQuantityList.add(position, Double.toString(foodQuantity));

            foodProteinList.add(position, Double.toString(protein));
            foodCarbsList.add(position, Double.toString(carbs));
            foodFatList.add(position, Double.toString(fat));
            foodFiberList.add(position, Double.toString(fiber));

            // Notify to the RecyclerView's adapter that the data set changed.
            if (itemListAdapter != null) {
                itemListAdapter.notifyItemInserted(position);
            }
        }
    }

    /**
     * Sets data in the list header (which is the first child of the RecyclerView used
     * for the MealEditorFragment).
     */
    private void setHeaderData() {

        if (foodIdList.size() > 0) {
            // Set nutrition facts values for the meal.
            totalProtein = getSummation(foodProteinList);
            totalCarbs = getSummation(foodCarbsList);
            totalFat = getSummation(foodFatList);
            totalFiber = getSummation(foodFiberList);

            // Get the energy units setting.
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            energyUnits = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");

            // Calculate the energy.
            if (energyUnits.equals("kJ")) {
                totalEnergy = 4.184 * (4 * totalProtein + 4 * totalCarbs + 9 * totalFat);
            } else {
                totalEnergy = 4 * totalProtein + 4 * totalCarbs + 9 * totalFat;
            }

        } else {
            // Default nutrition facts values.
            totalProtein = 0;
            totalCarbs = 0;
            totalFat = 0;
            totalFiber = 0;
            totalEnergy = 0;
            energyUnits = "";
        }

        // Set meal name in mealNameEditText.
        mealNameEditText.setText(mealName);

        // Show the meal's nutrition facts.
        if (totalEnergy != 0) {
            proteinTextView.setText(decimalFormat.format(totalProtein) + " g");
            carbsTextView.setText(decimalFormat.format(totalCarbs) + " g");
            fatTextView.setText(decimalFormat.format(totalFat) + " g");
            fiberTextView.setText(decimalFormat.format(totalFiber) + " g");
            //energyTextView.setText(decimalFormat.format(totalEnergy) + " " + energyUnits);
            nutritionFactsLayout.setVisibility(View.VISIBLE);
        } else {
            nutritionFactsLayout.setVisibility(View.GONE);
        }
    }

    private void setPieChartData() {

        // Calculate macronutrient percentages.
        double totalCalories = 4 * totalProtein + 4 * totalCarbs + 9 * totalFat;
        float proteinRate = (float) (100 * 4 * totalProtein / totalCalories);
        float carbsRate = (float) (100 * 4 * totalCarbs / totalCalories);
        float fatRate = (float) (100 * 9 * totalFat / totalCalories);

        // Y Axis (Pie Chart entries).
        ArrayList<Entry> macronutrientEntries = new ArrayList<>();

        macronutrientEntries.add(new Entry(proteinRate, 0));
        macronutrientEntries.add(new Entry(carbsRate, 1));
        macronutrientEntries.add(new Entry(fatRate, 2));

        // Pie Chart Data Set.
        PieDataSet macrosDataSet = new PieDataSet(macronutrientEntries, "");
        macrosDataSet.setSliceSpace(3f);
        macrosDataSet.setSelectionShift(5f);
        //macrosDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Add colors to data set.
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.color_protein));
        colors.add(getResources().getColor(R.color.color_carbs));
        colors.add(getResources().getColor(R.color.color_fat));

        macrosDataSet.setColors(colors);

        // X Axis (Legends).
        ArrayList<String> legendMacros = new ArrayList<>();
        legendMacros.add("Protein");
        legendMacros.add("Carbohydrates");
        legendMacros.add("Fat");

        // Data for the chart.
        PieData pieData = new PieData(legendMacros, macrosDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieChart.setData(pieData);

        // Hide Chart legend and labels.
        pieChart.getLegend().setEnabled(false);
/*        pieChart.setDrawSliceText(false);*/

        // Set center text (total energy + units).
        pieChart.setCenterText(decimalFormat.format(totalEnergy) + " " + energyUnits);

        // Refresh chart.
        pieChart.invalidate();

        // Animate the chart.
        //pieChart.animateY(800, Easing.EasingOption.EaseInQuad);
        pieChart.animateXY(1200, 1200);
    }


    // Take a list of numbers (String) and return its summation.
    private double getSummation(ArrayList<String> list) {
        double accumulated = 0;
        for (int i = 0; i < list.size(); i++) {
            accumulated = accumulated + Double.parseDouble(list.get(i));
        }
        return accumulated;
    }


    // Shows a dialog in order to edit the amount of food in a selected food from the list.
    private void showSetFoodQuantityDialog(final int position) {
        // Create a dialog so the user can define the amount of the food selected.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_meal_add_food_title);

        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long foodId = Long.parseLong(foodIdList.get(position));
                double foodQuantity = Double.parseDouble(foodQuantityEditText.getText().toString());

                // Set changes on lists.
                setFoodSelected(foodId, foodQuantity);
                setHeaderData();
                setPieChartData();
                itemListAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Load data to a MealFood object.
        long foodId = Long.parseLong(foodIdList.get(position));
        MealFood food = new MealFood(databaseAdapter.getFood(foodId));

        // Pass a custom layout to the dialog.
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_meal_add_food, null);
        builder.setView(view);

        // Dialog EditText.
        foodQuantityEditText = (EditText) view.findViewById(R.id.dialog_meal_food_quantity);
        foodQuantityEditText.setText(decimalFormat.
                format(Double.parseDouble(foodQuantityList.get(position))));

        // Set units label.
        foodQuantityUnits = (TextView) view.findViewById(R.id.dialog_meal_food_quantity_units);
        foodQuantityUnits.setText(food.getPortionUnits());

        // Show dialog.
        AlertDialog dialog = builder.create();
        dialog.show();

        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Disable dialog's OK button.
        //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        positiveButton.setEnabled(false);

        // Enable dialog's OK button if foodQuantityEditText EditText contains a valid value.
        foodQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("") || s.toString().equals(".")) {
                    //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    positiveButton.setEnabled(false);
                } else {
                    //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    positiveButton.setEnabled(true);
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


    // Deletes a food's info from the lists.
    public void removeFromFoodLists(int position) {
        foodIdList.remove(position);

        foodNameList.remove(position);
        foodSummaryList.remove(position);
        foodQuantityList.remove(position);

        foodProteinList.remove(position);
        foodCarbsList.remove(position);
        foodFatList.remove(position);
        foodFiberList.remove(position);

        //itemListAdapter.notifyItemRemoved(position + 1);

        setHeaderData();
        setPieChartData();
        itemListAdapter.notifyDataSetChanged();
    }


    // *********************************************************************************************
    // Methods to perform operations in the database.

    /**
     * Verifies that there's all the required data to save changes in the Meal,
     * and calls the appropriate method to either create or update meal.
     */
    private void saveMeal() {

        // Verify that all the information required is present. If not, inform the user.
        if (mealNameEditText.getText().toString().length() == 0) {
            mealNameEditText.setError(getString(R.string.error_meal_name_missing));
            return;
        }
        if (foodIdList.size() == 0) {
            Toast.makeText(getActivity(), R.string.toast_meal_foods_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalEnergy == 0) {
            Toast.makeText(getActivity(), R.string.toast_meal_foods_no_macros, Toast.LENGTH_SHORT).show();
            return;
        }

        mealName = mealNameEditText.getText().toString();

        // Format the mealName string and check its validity.
        mealName = Utilities.formatNameString(mealName);
        if (mealName.length() == 1 && Character.isSpaceChar(mealName.charAt(0))) {
            mealNameEditText.setText("");
            mealNameEditText.setError("Please enter a valid name");
            return;
        }

        // Insert/Update meal.
        if (mealId < 0) {
            insertMeal();
        } else {
            updateMeal();
        }
    }

    /**
     * Inserts New Meal into the database using the data entered by the user.
     */
    private void insertMeal() {
        long id = databaseAdapter.insertMeal(mealName, totalProtein, totalCarbs, totalFat,
                totalFiber, foodIdList, foodQuantityList);

        // Inform the user about the outcome of the transaction.
        if (id < 0) {
            if (databaseAdapter.isNameInMeals(mealName)) {
                Toast.makeText(getActivity(), R.string.toast_meal_already_registered,
                        Toast.LENGTH_SHORT).show();
                mealNameEditText.setError(getString(R.string.error_meal_name_registered));
            } else {
                Toast.makeText(getActivity(), R.string.toast_meal_database_problem,
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getActivity(), R.string.toast_meal_not_created, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.toast_meal_created, Toast.LENGTH_SHORT).show();
            onMealSaved.onMealSavedSuccessfully();
        }
    }

    /**
     * Updates Meal (and its foods) in the database using the data entered by the user.
     */
    private void updateMeal() {

        // Check that the (possibly) new meal name, is not registered by other food in the DB.
        if (!mealName.equals(oldMealName)) {
            if (databaseAdapter.isNameInFoods(mealName)) {
                Toast.makeText(getActivity(), R.string.toast_meal_already_registered,
                        Toast.LENGTH_SHORT).show();
                mealNameEditText.setError(getString(R.string.error_meal_name_registered));
                return;
            }
        }

        // Check for changes in meal's foods.

        // Lists to indicate changes.
        ArrayList<Long> deletedFoods = new ArrayList<>();
        ArrayList<Long> newFoods = new ArrayList<>();
        ArrayList<Double> newFoodQuantities = new ArrayList<>();
        ArrayList<Long> updatedFoods = new ArrayList<>();
        ArrayList<Double> updatedFoodQuantities = new ArrayList<>();

        // Look for deleted foods.
        for (int i = 0; i < oldFoodIdList.size(); i++) {
            boolean foodDeleted = true;
            for (int j = 0; j < foodIdList.size(); j++) {
                if (oldFoodIdList.get(i).equals(foodIdList.get(j))) {
                    foodDeleted = false;
                    break;
                }
            }
            if (foodDeleted) {
                deletedFoods.add(Long.parseLong(oldFoodIdList.remove(i)));
                i = i - 1;
            }
        }

        // Look for new foods.
        for (int i = 0; i < foodIdList.size(); i++) {
            boolean newAdded = true;
            for (int j = 0; j < oldFoodIdList.size(); j++) {
                if (foodIdList.get(i).equals(oldFoodIdList.get(j))) {
                    newAdded = false;
                    break;
                }
            }
            if (newAdded) {
                newFoods.add(Long.parseLong(foodIdList.remove(i)));
                newFoodQuantities.add(Double.parseDouble(foodQuantityList.remove(i)));
                i = i - 1;
            }
        }

        // Look for updated foods.
        for (int i = 0; i < foodIdList.size(); i++) {
            boolean updated = true;
            for (int j = 0; j < oldFoodIdList.size(); j++) {
                if (foodIdList.get(i).equals(oldFoodIdList.get(j))) {
                    Log.d("Old value? ", "" + oldFoodQuantityList.get(j));
                    Log.d("New value? ", "" + foodQuantityList.get(i));
                    if (foodQuantityList.get(i).equals(oldFoodQuantityList.get(j))) {
                        updated = false;
                        break;
                    }
                }
            }
            if (updated) {
                Log.d("Food updated? ", "apparently YES");
                updatedFoods.add(Long.parseLong(foodIdList.remove(i)));
                updatedFoodQuantities.add(Double.parseDouble(foodQuantityList.remove(i)));
                i = i - 1;
            }
        }

        // Update both the Meals and MealFoods tables.
        int updateResult = databaseAdapter.updateMeal(mealId, mealName, totalProtein, totalCarbs,
                totalFat, totalFiber, deletedFoods, newFoods, newFoodQuantities, updatedFoods,
                updatedFoodQuantities);

        // Inform the user about the outcome of the transaction.
        if (updateResult == 1) {
            Toast.makeText(getActivity(), R.string.toast_meal_updated, Toast.LENGTH_SHORT).show();
            onMealSaved.onMealSavedSuccessfully();
        } else {
            Toast.makeText(getActivity(), R.string.toast_meal_internal_problem, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the tuple in the Meals table corresponding to the current mealId.
     * Moreover, deletes all the tuples in MealFoods related to meal.
     */
    private void deleteMeal() {

        int rowsDeleted = databaseAdapter.deleteMeal(mealId);

        if (rowsDeleted == 1) {
            Toast.makeText(getActivity(), getString(R.string.toast_meal_deleted), Toast.LENGTH_SHORT).show();
            onMealSaved.onMealSavedSuccessfully();
        }
    }

    /**
     * Launches a dialog so that the user confirms the meal deletion.
     */
    private void onMealDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog properties.
        builder.setMessage(R.string.dialog_meal_delete_message);
        builder.setPositiveButton(R.string.dialog_meal_delete_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMeal();
            }
        });
        builder.setNegativeButton(R.string.dialog_meal_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User cancelled the dialog.
            }
        });

        // Launch the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
/*        DialogFragment deleteMealDialog = new DialogDeleteMeal();
        deleteMealDialog.show(getFragmentManager(), "deleteMealDialog");*/
    }

}
