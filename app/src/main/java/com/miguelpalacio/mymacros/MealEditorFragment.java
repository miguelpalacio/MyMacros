package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    OnMealAddFoodFragment onMealAddFoodFragment;
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

    boolean editMealInit;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Ensure that the host activity implements the OnMealAddFoodFragment interface.
        try {
            onMealAddFoodFragment = (OnMealAddFoodFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealAddFoodFragment interface");
        }

        // Ensure that the host activity implements the OnMealSaved interface.
        try {
            onMealSaved = (OnMealSaved) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMealSaved interface");
        }
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
                editMealInit = true;
                Meal meal = databaseAdapter.getMeal(mealId);

                mealName = meal.getName();

                for (int i = 0; i < meal.getFoods().size(); i++) {
                    setFoodOnLists(meal.getFoods().get(i));
                }

                editMealInit = false;
            }

            // Add new food "button".
            foodNameList.add(getString(R.string.meal_add_new));
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
                    energyTextView = (TextView) listHeader.findViewById(R.id.meal_energy);

                    // Remove listener.
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        itemListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        itemListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    // Since the references are ready, set data in the list header.
                    setHeaderData();
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
            //onMealDelete();
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
    }

    @Override
    public void onListItemClick(int position) {

        // "Add new" row clicked.
        if (position == foodNameList.size()) {
            Fragment fragment = new MealAddFoodFragment();
            onMealAddFoodFragment.openMealAddFoodFragment(fragment, R.string.toolbar_meal_add_food);
        }
    }

    // Public Interfaces to be implemented in MainActivity.
    public interface OnMealSaved {
        void onMealSavedSuccessfully();
    }

    public interface OnMealAddFoodFragment {
        void openMealAddFoodFragment(Fragment fragment, int newToolbarTitle);
    }

    /**
     * Retrieves the food info and calls setFoodOnLists.
     * @param foodId ID of the food that was selected by the user.
     * @param foodQuantity the amount of food set by the user.
     */
    public void setFoodSelected(long foodId, double foodQuantity) {
        // Retrieve the food info.
        MealFood food = (MealFood) databaseAdapter.getFood(foodId);
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
        double carbs = food.getCarbohydrates() * (foodQuantity / food.getPortionQuantity());
        double fat = food.getFat() * (foodQuantity / food.getPortionQuantity());
        double fiber = food.getFiber() * (foodQuantity / food.getPortionQuantity());

        // Define food summary.
        String foodSummary = decimalFormat.format(foodQuantity) + " " + food.getPortionUnits();

        // Add food information to the lists.
        int position = foodNameList.size() - 1;
        if (editMealInit) {
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
            if (!editMealInit) {
                itemListAdapter.notifyItemInserted(position);
            }
        }
    }


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
                mealNameEditText.setError(getString(R.string.toast_meal_name_registered));
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
     * Updates Meal in the database using the data entered by the user.
     */
    private void updateMeal() {

/*        // Check that the (possibly) new food name, is not registered by other food in the DB.
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
        }*/
    }


    /**
     * Sets data in the list header (which is the first child of the RecyclerView used
     * for the MealEditorFragment).
     */
    private void setHeaderData() {

        if (foodIdList.size() > 0) {
            // Set nutrition facts values for the meal.
            totalProtein = getTotalValue(foodProteinList);
            totalCarbs = getTotalValue(foodCarbsList);
            totalFat = getTotalValue(foodFatList);
            totalFiber = getTotalValue(foodFiberList);

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
            energyTextView.setText(decimalFormat.format(totalEnergy) + " " + energyUnits);
            nutritionFactsLayout.setVisibility(View.VISIBLE);
        }
    }

    // Take a list of numbers (String) and return its summation.
    private double getTotalValue(ArrayList<String> list) {
        double accumulated = 0;
        for (int i = 0; i < list.size(); i++) {
            accumulated = accumulated + Double.parseDouble(list.get(i));
        }
        return accumulated;
    }
}
