package com.miguelpalacio.mymacros;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PlannerFragment extends Fragment implements ItemListAdapter.ViewHolder.ClickListener {

    public static final String KEY_MEAL_ID_LIST = "meal_id_list";
    public static final String KEY_MEAL_NAME_LIST = "meal_name_list";
    public static final String KEY_MEAL_SUMMARY_LIST = "meal_summary_list";
    public static final String KEY_MEAL_PROTEIN_LIST = "meal_protein_list";
    public static final String KEY_MEAL_CARBS_LIST = "meal_carbs_list";
    public static final String KEY_MEAL_FAT_LIST = "meal_fat_list";
    public static final String KEY_MEAL_FIBER_LIST = "meal_fiber_list";

    SharedPreferences sharedPref;

    RecyclerView itemListView;
    RecyclerView.LayoutManager itemListLayoutManager;
    ItemListAdapter itemListAdapter;

    LinearLayout listHeader;

    ProgressBar proteinProgressBar;
    TextView proteinPercentageText;
    TextView proteinConsumedText;

    ProgressBar carbsProgressBar;
    TextView carbsPercentageText;
    TextView carbsConsumedText;

    ProgressBar fatProgressBar;
    TextView fatPercentageText;
    TextView fatConsumedText;

    ProgressBar fiberProgressBar;
    TextView fiberPercentageText;
    TextView fiberConsumedText;

    ProgressBar energyProgressBar;
    TextView energyPercentageText;
    TextView energyConsumedText;

    OnPlannerAddMeal onPlannerAddMeal;

    DatabaseAdapter databaseAdapter;

    ArrayList<Long> mealIdList;
    ArrayList<String> mealNameList;
    ArrayList<String> mealSummaryList;
    ArrayList<Double> mealProteinList;
    ArrayList<Double> mealCarbsList;
    ArrayList<Double> mealFatList;
    ArrayList<Double> mealFiberList;

    Gson gson;

    final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Ensure that the host activity implements the OnPlannerAddMeal interface.
        try {
            onPlannerAddMeal = (OnPlannerAddMeal) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPlannerAddMeal interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment.
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseAdapter = new DatabaseAdapter(getActivity());

        // Create a Gson object. Gson allows to convert any object into a JSON string.
        gson = new Gson();

        // Load the global SharedPreferences file.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the lists with the info for Today's Plan.
        if (sharedPref.contains(KEY_MEAL_ID_LIST)) {
            String json;

            json = sharedPref.getString(KEY_MEAL_ID_LIST, null);
            Type type = new TypeToken<ArrayList<Long>>(){}.getType();
            mealIdList = gson.fromJson(json, type);

            json = sharedPref.getString(KEY_MEAL_NAME_LIST, null);
            type = new TypeToken<ArrayList<String>>(){}.getType();
            mealNameList = gson.fromJson(json, type);

            json = sharedPref.getString(KEY_MEAL_SUMMARY_LIST, null);
            type = new TypeToken<ArrayList<String>>(){}.getType();
            mealSummaryList = gson.fromJson(json, type);

            json = sharedPref.getString(KEY_MEAL_PROTEIN_LIST, null);
            type = new TypeToken<ArrayList<Double>>(){}.getType();
            mealProteinList = gson.fromJson(json, type);

            json = sharedPref.getString(KEY_MEAL_CARBS_LIST, null);
            type = new TypeToken<ArrayList<Double>>(){}.getType();
            mealCarbsList = gson.fromJson(json, type);

            json = sharedPref.getString(KEY_MEAL_FAT_LIST, null);
            type = new TypeToken<ArrayList<Double>>(){}.getType();
            mealFatList = gson.fromJson(json, type);

            json = sharedPref.getString(KEY_MEAL_FIBER_LIST, null);
            type = new TypeToken<ArrayList<Double>>(){}.getType();
            mealFiberList = gson.fromJson(json, type);

        } else {
            mealIdList = new ArrayList<>();
            mealNameList = new ArrayList<>();
            mealSummaryList = new ArrayList<>();
            mealProteinList = new ArrayList<>();
            mealCarbsList = new ArrayList<>();
            mealFatList = new ArrayList<>();
            mealFiberList = new ArrayList<>();

            mealNameList.add(getString(R.string.planner_add_meal));
            mealSummaryList.add("");
        }

        // When coming back from AddMealFragment, check if user selected a meal.
        MainActivity activity = (MainActivity) getActivity();
        if (activity.wasMealAddedToPlanner()) {
            setMealSelected(activity.getPlannerMealId());
            activity.setMealAddedToPlanner(false);
        }

/*        // TEMPORAL!!!:
        mealNameList = new ArrayList<>();
        mealSummaryList = new ArrayList<>();

        mealNameList.add("Breakfast A");
        mealNameList.add("Post-Workout");
        mealNameList.add("Add new meal or food");
        mealSummaryList.add("Protein: 15 g, Carbohydrates: 36.5 g, Fat: 22 g");
        mealSummaryList.add("Protein: 41.2 g, Carbohydrates: 146.3 g, Fat: 5.5 g");
        mealSummaryList.add("");*/

        // Define the main view (the RecyclerView).
        itemListView = (RecyclerView) getActivity().findViewById(R.id.planner_meal_list);

        // Set the adapter for the Foods list (Recycler View).
        itemListAdapter = new ItemListAdapter(mealNameList, mealSummaryList,
                R.layout.fragment_planner_header, R.layout.item_list_row,
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

                            proteinProgressBar = (ProgressBar) listHeader.findViewById(R.id.planner_progress_bar_protein);
                            carbsProgressBar = (ProgressBar) listHeader.findViewById(R.id.planner_progress_bar_carbs);
                            fatProgressBar = (ProgressBar) listHeader.findViewById(R.id.planner_progress_bar_fat);
                            fiberProgressBar = (ProgressBar) listHeader.findViewById(R.id.planner_progress_bar_fiber);
                            energyProgressBar = (ProgressBar) listHeader.findViewById(R.id.planner_progress_bar_energy);

                            proteinPercentageText = (TextView) listHeader.findViewById(R.id.planner_percentage_protein);
                            carbsPercentageText = (TextView) listHeader.findViewById(R.id.planner_percentage_carbs);
                            fatPercentageText = (TextView) listHeader.findViewById(R.id.planner_percentage_fat);
                            fiberPercentageText = (TextView) listHeader.findViewById(R.id.planner_percentage_fiber);
                            energyPercentageText = (TextView) listHeader.findViewById(R.id.planner_percentage_energy);

                            proteinConsumedText = (TextView) listHeader.findViewById(R.id.planner_taken_protein);
                            carbsConsumedText = (TextView) listHeader.findViewById(R.id.planner_taken_carbs);
                            fatConsumedText = (TextView) listHeader.findViewById(R.id.planner_taken_fat);
                            fiberConsumedText = (TextView) listHeader.findViewById(R.id.planner_taken_fiber);
                            energyConsumedText = (TextView) listHeader.findViewById(R.id.planner_taken_energy);

                            setHeaderData();


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

    // Persist changes prior to leave the fragment.
    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPref.edit();

        String json;

        json = gson.toJson(mealIdList);
        editor.putString(KEY_MEAL_ID_LIST, json);

        json = gson.toJson(mealNameList);
        editor.putString(KEY_MEAL_NAME_LIST, json);

        json = gson.toJson(mealSummaryList);
        editor.putString(KEY_MEAL_SUMMARY_LIST, json);

        json = gson.toJson(mealProteinList);
        editor.putString(KEY_MEAL_PROTEIN_LIST, json);

        json = gson.toJson(mealCarbsList);
        editor.putString(KEY_MEAL_CARBS_LIST, json);

        json = gson.toJson(mealFatList);
        editor.putString(KEY_MEAL_FAT_LIST, json);

        json = gson.toJson(mealFiberList);
        editor.putString(KEY_MEAL_FIBER_LIST, json);

        editor.apply();
    }

    // RecyclerView's item click listeners.

    @Override
    public void onListItemClick(int position) {
        // "Add new" row clicked.
        if (position == mealNameList.size()) {
            Fragment fragment = new AddMealFragment();
            onPlannerAddMeal.openAddMealFragment(fragment, R.string.toolbar_planner_add_meal);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    public interface OnPlannerAddMeal {
        void openAddMealFragment(Fragment fragment, int newToolbarTitle);
    }


    private void setMealSelected(long mealId) {

/*        // Check that the selected meal is not already in Today's plan.
        for (int i = 0; i < mealIdList.size(); i++) {
            if (mealIdList.get(i) == mealId) {
                return;
            }
        }*/

        Meal meal = databaseAdapter.getMeal(mealId);

        // Add meal information to the lists.

        int position = mealIdList.size() - 1;
        if (position < 0) {
            position = 0;
        }

        String summary = "Protein: " + decimalFormat.format(meal.getProtein()) + " g, " +
                "Carbohydrates: " + decimalFormat.format(meal.getCarbohydrates()) + " g, " +
                "Fat: " + decimalFormat.format(meal.getFat()) + " g";

        mealIdList.add(position, mealId);
        mealNameList.add(position, meal.getName());
        mealSummaryList.add(position, summary);

        mealProteinList.add(position, meal.getProtein());
        mealCarbsList.add(position, meal.getCarbohydrates());
        mealFatList.add(position, meal.getFat());
        mealFiberList.add(position, meal.getFiber());

        // Notify to the RecyclerView's adapter that an item was inserted to the lists.
        itemListAdapter.notifyItemInserted(position);

        // Update data shown in RecyclerView List header.
        setHeaderData();
    }


    /**
     * Sets data in the list header (which is the first child of the RecyclerView used
     * for the MealEditorFragment).
     */
    private void setHeaderData() {

        String energyUnits = sharedPref.getString(SettingsFragment.KEY_ENERGY, "");
        double targetProtein = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_PROTEIN_GRAMS, "0"));
        double targetCarbs = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_CARBS_GRAMS, "0"));
        double targetFat = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_FAT_GRAMS, "0"));
        double targetFiber = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_FIBER, "0"));
        double targetEnergy = Double.parseDouble(sharedPref.getString(ProfileFragment.KEY_ENERGY_NEED, "0"));

        double consumedProtein = getSummation(mealProteinList);
        double consumedCarbs = getSummation(mealCarbsList);
        double consumedFat = getSummation(mealFatList);
        double consumedFiber = getSummation(mealFiberList);
        double consumedEnergy = 4 * consumedProtein + 4 * consumedCarbs + 9 * consumedFat;

        if (energyUnits.equals("kJ"))
            consumedEnergy = 4.184 * consumedEnergy;

        // Calculate percentages of consumption.
        int proteinPercentage = (int) (consumedProtein / targetProtein * 100);
        int carbsPercentage = (int) (consumedCarbs / targetCarbs * 100);
        int fatPercentage = (int) (consumedFat / targetFat * 100);
        int fiberPercentage = (int) (consumedFiber / targetFiber * 100);
        int energyPercentage = (int) (consumedEnergy / targetEnergy * 100);

        // Set bars' progress.
        proteinProgressBar.setProgress(proteinPercentage);
        carbsProgressBar.setProgress(carbsPercentage);
        fatProgressBar.setProgress(fatPercentage);
        fiberProgressBar.setProgress(fiberPercentage);
        energyProgressBar.setProgress(energyPercentage);

        // Set Header's TextViews.

        proteinPercentageText.setText(proteinPercentage + "%");
        carbsPercentageText.setText(carbsPercentage + "%");
        fatPercentageText.setText(fatPercentage + "%");
        fiberPercentageText.setText(fiberPercentage + "%");
        energyPercentageText.setText(energyPercentage + "%");

        proteinConsumedText.setText(decimalFormat.format(consumedProtein) + "/" +
                decimalFormat.format(targetProtein) + " g");
        carbsConsumedText.setText(decimalFormat.format(consumedCarbs) + "/" +
                decimalFormat.format(targetCarbs) + " g");
        fatConsumedText.setText(decimalFormat.format(consumedFat) + "/" +
                decimalFormat.format(targetFat) + " g");
        fiberConsumedText.setText(decimalFormat.format(consumedFiber) + "/" +
                decimalFormat.format(targetFiber) + " g");
        energyConsumedText.setText(decimalFormat.format(consumedEnergy) + "/" +
                decimalFormat.format(targetEnergy) + " " + energyUnits);

        itemListAdapter.notifyDataSetChanged();
    }

    private double getSummation(ArrayList<Double> list) {
        double accumulated = 0;
        for (int i = 0; i < list.size(); i++) {
            accumulated = accumulated + list.get(i);
        }
        return accumulated;
    }
}
