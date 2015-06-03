package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static final String FOOD_PORTION_UNITS_LIST = "foodPortionUnitsList";
    private static final String FOOD_PORTION_QUANTITY_LIST = "foodPortionQuantityList";
    private static final String FOOD_PROTEIN_LIST = "foodProteinList";
    private static final String FOOD_CARBS_LIST = "foodCarbsList";
    private static final String FOOD_FAT_LIST = "foodFatList";
    private static final String FOOD_FIBER_LIST = "foodFiberList";

    LinearLayout listHeader;
    EditText mealNameEditText;
    LinearLayout nutritionFactsLayout;
    TextView proteinTextView;
    TextView carbsTextView;
    TextView fatTextView;
    TextView fiberTextView;
    TextView energyTextView;

    RecyclerView itemListView;
    RecyclerView.LayoutManager itemListLayoutManager;
    ItemListAdapter itemListAdapter;

    ArrayList<String> foodIdList;
    ArrayList<String> foodNameList;
    ArrayList<String> foodSummaryList;
    ArrayList<String> foodQuantityList;
    ArrayList<String> foodPortionQuantityList;
    ArrayList<String> foodPortionUnitsList;
    ArrayList<String> foodProteinList;
    ArrayList<String> foodCarbsList;
    ArrayList<String> foodFatList;
    ArrayList<String> foodFiberList;

    DatabaseAdapter databaseAdapter;

    OnMealAddFoodFragment onMealAddFoodFragment;

    final DecimalFormat decimalFormat = new DecimalFormat("#.#");

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

        final Bundle mySavedInstanceState = getArguments();

        final double totalEnergy;
        final double totalProtein;
        final double totalCarbs;
        final double totalFat;
        final double totalFiber;

        // Look for instance data.
        if (mySavedInstanceState.containsKey(FOOD_ID_LIST)) {

            // Load previously saved data.

            foodIdList = mySavedInstanceState.getStringArrayList(FOOD_ID_LIST);
            foodNameList = mySavedInstanceState.getStringArrayList(FOOD_NAME_LIST);
            foodSummaryList = mySavedInstanceState.getStringArrayList(FOOD_SUMMARY_LIST);
            foodQuantityList = mySavedInstanceState.getStringArrayList(FOOD_QUANTITY_LIST);
            foodPortionQuantityList = mySavedInstanceState.getStringArrayList(FOOD_PORTION_QUANTITY_LIST);
            foodPortionUnitsList = mySavedInstanceState.getStringArrayList(FOOD_PORTION_UNITS_LIST);
            foodProteinList = mySavedInstanceState.getStringArrayList(FOOD_PROTEIN_LIST);
            foodCarbsList = mySavedInstanceState.getStringArrayList(FOOD_CARBS_LIST);
            foodFatList = mySavedInstanceState.getStringArrayList(FOOD_FAT_LIST);
            foodFiberList = mySavedInstanceState.getStringArrayList(FOOD_FIBER_LIST);

            // Set nutrition facts values for the meal.
            totalProtein = getTotalValue(foodProteinList);
            totalCarbs = getTotalValue(foodCarbsList);
            totalFat = getTotalValue(foodFatList);
            totalFiber = getTotalValue(foodFiberList);

            totalEnergy = 4*totalProtein + 4*totalCarbs + 9*totalFat;

        } else {

            // Initialize lists.
            foodIdList = new ArrayList<>();
            foodNameList = new ArrayList<>();
            foodSummaryList = new ArrayList<>();
            foodQuantityList = new ArrayList<>();
            foodPortionQuantityList = new ArrayList<>();
            foodPortionUnitsList = new ArrayList<>();
            foodProteinList = new ArrayList<>();
            foodCarbsList = new ArrayList<>();
            foodFatList = new ArrayList<>();
            foodFiberList = new ArrayList<>();

            // Add new food "button".
            foodNameList.add(getString(R.string.meal_add_new));
            foodSummaryList.add("");
            foodQuantityList.add("");
            foodPortionUnitsList.add("");

            // Default values.
            totalProtein = 0;
            totalCarbs = 0;
            totalFat = 0;
            totalFiber = 0;
            totalEnergy = 0;
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

        // Set an observer in the RecyclerView's layout (needed to know when its children are ready).
        final ViewTreeObserver itemListObserver = itemListView.getViewTreeObserver();
        itemListObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (itemListObserver.isAlive()) {

                    // Get the references to the views in the header.
                    listHeader = (LinearLayout) itemListView.getChildAt(0);
                    mealNameEditText = (EditText) listHeader.findViewById(R.id.meal_name);
                    nutritionFactsLayout = (LinearLayout) listHeader.findViewById(R.id.meal_nutrition_facts);
                    proteinTextView = (TextView) listHeader.findViewById(R.id.meal_protein);
                    carbsTextView = (TextView) listHeader.findViewById(R.id.meal_carbs);
                    fatTextView = (TextView) listHeader.findViewById(R.id.meal_fat);
                    fiberTextView = (TextView) listHeader.findViewById(R.id.meal_fiber);
                    energyTextView = (TextView) listHeader.findViewById(R.id.meal_energy);

                    // Restore previous data in the header's view.
                    if (mySavedInstanceState.containsKey(FOOD_ID_LIST)) {
                        mealNameEditText.setText(mySavedInstanceState.getString(MEAL_NAME));
                    }

                    if (totalEnergy != 0) {
                        proteinTextView.setText(decimalFormat.format(totalProtein) + " g");
                        carbsTextView.setText(decimalFormat.format(totalCarbs) + " g");
                        fatTextView.setText(decimalFormat.format(totalFat) + " g");
                        fiberTextView.setText(decimalFormat.format(totalFiber) + " g");
                        energyTextView.setText(decimalFormat.format(totalEnergy) + " Units?");

                        nutritionFactsLayout.setVisibility(View.VISIBLE);
                    }

                    // Remove listener.
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        itemListObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        itemListObserver.removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
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
        getArguments().putStringArrayList(FOOD_PORTION_QUANTITY_LIST, foodPortionQuantityList);
        getArguments().putStringArrayList(FOOD_PORTION_UNITS_LIST, foodPortionUnitsList);
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

    /**
     * This method is called by MainActivity in the MealAddFoodFragment callback.
     * @param foodId ID of the food that was selected by the user.
     * @param foodQuantity the amount of food set by the user.
     */
    public void setFoodSelected(long foodId, double foodQuantity) {
        // Get the food info.
        Food food = databaseAdapter.getFood(foodId);


        // Add food information to the lists.
        int position = foodNameList.size() - 1;

        foodIdList.add(position, Long.toString(foodId));

        foodNameList.add(position, food.getName());
        foodSummaryList.add(position, decimalFormat.format(foodQuantity) + " " + food.getPortionUnits());
        foodQuantityList.add(position, Double.toString(foodQuantity));

        foodPortionQuantityList.add(position, Double.toString(food.getPortionQuantity()));
        foodPortionUnitsList.add(position, food.getPortionUnits());
        foodProteinList.add(position, Double.toString(food.getProtein()));
        foodCarbsList.add(position, Double.toString(food.getCarbohydrates()));
        foodFatList.add(position, Double.toString(food.getFat()));
        foodFiberList.add(position, Double.toString(food.getFiber()));

        // Notify to the RecyclerView's adapter that the data set changed.
        itemListAdapter.notifyItemInserted(position);
    }

    public interface OnMealAddFoodFragment {
        void openMealAddFoodFragment(Fragment fragment, int newToolbarTitle);
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
